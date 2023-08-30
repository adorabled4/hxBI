package com.dhx.bi.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.annotation.AuthCheck;
import com.dhx.bi.common.constant.RedisConstant;
import com.dhx.bi.common.constant.UserConstant;
import com.dhx.bi.common.exception.BusinessException;
import com.dhx.bi.manager.StrategySelector;
import com.dhx.bi.model.DTO.ServerLoadInfo;
import com.dhx.bi.model.VO.ChartVO;
import com.dhx.bi.model.document.Chart;
import com.dhx.bi.mq.producer.BiMqMessageProducer;
import com.dhx.bi.manager.AiManager;
import com.dhx.bi.manager.RedisLimiterManager;
import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DO.UserEntity;
import com.dhx.bi.model.DTO.DeleteRequest;
import com.dhx.bi.model.DTO.chart.*;
import com.dhx.bi.model.enums.ChartStatusEnum;
import com.dhx.bi.service.ChartService;
import com.dhx.bi.service.GenChartStrategy;
import com.dhx.bi.service.UserService;
import com.dhx.bi.utils.ExcelUtils;
import com.dhx.bi.utils.ResultUtil;
import com.dhx.bi.utils.ServerMetricsUtil;
import com.dhx.bi.utils.ThrowUtils;
import com.dhx.bi.webSocket.WebSocketServer;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author adorabled4
 * @className ChartEntityController
 * @date : 2023/07/04/ 10:16
 **/
@RequestMapping("/chart")
@RestController
@Slf4j
@Api
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private BiMqMessageProducer biMqMessageProducer;

    @Resource
    private WebSocketServer webSocketServer;

    @Resource
    StrategySelector selector;


    @PostMapping("/list/chart/unsucceed")
    public BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page> getUnsucceedChart(@RequestBody ChartQueryRequest chartQueryRequest) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserEntity loginUser = userService.getLoginUser();
        chartQueryRequest.setUserId(loginUser.getUserId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ChartEntity> wrapper = chartService.getQueryWrapper(chartQueryRequest);
        wrapper.ne("status", ChartStatusEnum.SUCCEED.getStatus());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChartEntity> page = chartService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size),
                wrapper);
        return ResultUtil.success(page);

    }

    @PostMapping("/list/chart/all")
    public BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page> getAllCharts(@RequestBody ChartQueryRequest chartQueryRequest) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserEntity loginUser = userService.getLoginUser();
        chartQueryRequest.setUserId(loginUser.getUserId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ChartEntity> wrapper = chartService.getQueryWrapper(chartQueryRequest);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChartEntity> page =
                chartService.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size), wrapper);
        List<ChartVO> chartVOS = page.getRecords().stream().map(item -> {
            ChartVO chartVO = BeanUtil.copyProperties(item, ChartVO.class);
            return chartVO;
        }).collect(Collectors.toList());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page newPage = BeanUtil.copyProperties(page, com.baomidou.mybatisplus.extension.plugins.pagination.Page.class);
        newPage.setRecords(chartVOS);
//        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChartVO> newPage=chartService.buildPage(page,chartVOS);
        return ResultUtil.success(newPage);
    }

    @GetMapping("/regen/chart")
    public BaseResponse<String> regenerateChart(@RequestParam("chartId") Long chartId) {
        // 取出数据
        ChartEntity chartEntity = chartService.getById(chartId);
        ThrowUtils.throwIf(chartEntity.getChartData().length() > 1000, ErrorCode.SYSTEM_ERROR, "原始信息过长!");
        // 获取用户信息
        UserEntity user = userService.getLoginUser();
        redisLimiterManager.doRateLimit(RedisConstant.GEN_CHART_LIMIT_KEY + user.getUserId());
        chartEntity.setStatus(ChartStatusEnum.WAIT.getStatus());
        // 更新状态信息
        boolean updateById = chartService.updateById(chartEntity);
        ThrowUtils.throwIf(!updateById, ErrorCode.SYSTEM_ERROR, "重新生成图表失败");
        // 2. send to rabbitMQ
        long newChartId = chartEntity.getId();
        biMqMessageProducer.sendGenChartMessage(String.valueOf(newChartId));
        return ResultUtil.success("操作成功");
    }

    /**
     * 智能图表(同步)
     *
     * @param multipartFile 数据文件
     * @param chartRequest  图要求
     * @return {@link BaseResponse}<{@link BiResponse}>
     */
//    @PostMapping("/gen")
//    @Deprecated
//    public BaseResponse<BiResponse> getChartByAiSync(@RequestPart("file") MultipartFile multipartFile,
//                                                     GenChartByAIRequest chartRequest) {
//        // 取出数据
//        String chartType = chartRequest.getChartType();
//        String name = chartRequest.getName();
//        String goal = chartRequest.getGoal();
//        // 获取用户信息
//        UserEntity user = userService.getLoginUser();
//        // 校验
//        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空!");
//        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长!");
//        ExcelUtils.checkExcelFile(multipartFile);
//        // 获取CSV
//        String csvData = ExcelUtils.excel2CSV(multipartFile);
//        // 构造用户输入
//        StringBuilder userInput = new StringBuilder("");
//        // 拼接图表类型;
//        String userGoal = goal;
//        if (StringUtils.isNotBlank(chartType)) {
//            userGoal += ", 请使用 " + chartType;
//        }
//        userInput.append("分析需求: ").append('\n');
//        userInput.append(userGoal).append("\n");
//        userInput.append("原始数据：").append("\n");
//        userInput.append(csvData).append("\n");
//
//        // 系统预设 ( 简单预设 )
//        /* 较好的做法是在系统（模型）层面做预设效果一般来说，会比直接拼接在用户消息里效果更好一些。*/
//
//        /*
//        分析需求：
//        分析网站用户的增长情况
//        原始数据：
//        日期,用户数
//        1号,10
//        2号,20
//        3号,30
//        */
////        String result = aiManager.doChat(userInput.toString(), AIConstant.BI_MODEL_ID);
//        String result = aiManager.chatAndGenChart(goal, chartType, csvData);
//        String[] split = result.split("【【【【【");
//        // 第一个是 空字符串
//        if (split.length < 3) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误!");
//        }
//        // 图表代码
//        String genChart = split[1].trim();
//        // 分析结果
//        String genResult = split[2].trim();
//        // 插入数据到数据库
//        ChartEntity chartEntity = new ChartEntity();
//        chartEntity.setUserId(user.getUserId());
//        chartEntity.setName(name);
//        chartEntity.setGoal(goal);
//        chartEntity.setChartData(csvData);
//        chartEntity.setChartType(chartType);
//        chartEntity.setGenChart(genChart);
//        chartEntity.setGenResult(genResult);
//        boolean save = chartService.save(chartEntity);
//        boolean syncResult = chartService.syncChart(chartEntity);
//        ThrowUtils.throwIf(!save&&syncResult, ErrorCode.SYSTEM_ERROR, "图表保存失败!");
//
//        // 封装返回结果
//        BiResponse biResponse = new BiResponse();
//        biResponse.setGenChart(genChart);
//        biResponse.setGenResult(genResult);
//
//        // todo 图表原始数据压缩 (AI接口普遍有字数限制)
//        return ResultUtil.success(biResponse);
//    }


    /**
     * 智能图表(异步) : 消息队列
     *
     * @param multipartFile 数据文件
     * @param chartRequest  图要求
     * @return {@link BaseResponse}<{@link BiResponse}>
     */
    @PostMapping("/gen/async/mq")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                        GenChartByAIRequest chartRequest) {
        // 1.save chat(Not Generated)
        // 取出数据
        String chartType = chartRequest.getChartType();
        String name = chartRequest.getName();
        String goal = chartRequest.getGoal();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空!");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长!");
        ExcelUtils.checkExcelFile(multipartFile);
        // 获取用户信息
        UserEntity user = userService.getLoginUser();
        redisLimiterManager.doRateLimit(RedisConstant.GEN_CHART_LIMIT_KEY + user.getUserId());
        // 读取文件信息
        String csvData = ExcelUtils.excel2CSV(multipartFile);
        ThrowUtils.throwIf(csvData.length() > 1000, ErrorCode.SYSTEM_ERROR, "原始信息过长!");
        // 插入数据到数据库
        ChartEntity chartEntity = new ChartEntity();
        chartEntity.setUserId(user.getUserId());
        chartEntity.setName(name);
        chartEntity.setGoal(goal);
        chartEntity.setStatus(ChartStatusEnum.WAIT.getStatus());
        chartEntity.setChartType(chartType);
        chartEntity.setChartData(csvData);
        boolean save = chartService.save(chartEntity);
        ThrowUtils.throwIf(!save,ErrorCode.SYSTEM_ERROR,"保存图表失败!");
        // 在这里选择执行的策略
        //1. 获取当前执行状态
        ServerLoadInfo info= ServerMetricsUtil.getLoadInfo();
        //2. 获取执行策略
        GenChartStrategy genChartStrategy = selector.selectStrategy(info);
        //3. 执行生成图表
        BiResponse biResponse = genChartStrategy.executeGenChart(chartEntity);
        if(StringUtils.isNotBlank(biResponse.getGenChart())){
            return ResultUtil.success(biResponse);
        }
        return ResultUtil.success(biResponse);
    }

//    @PostMapping("/gen/async")
//    public BaseResponse<BiResponse> getChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
//                                                      GenChartByAIRequest chartRequest) {
//        // 1.save chat(Not Generated)
//        // 取出数据
//        String chartType = chartRequest.getChartType();
//        String name = chartRequest.getName();
//        String goal = chartRequest.getGoal();
//        // 校验
//        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空!");
//        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长!");
//        ExcelUtils.checkExcelFile(multipartFile);
//        // 获取用户信息
//        UserEntity user = userService.getLoginUser();
//        // 读取文件信息
//        String csvData = ExcelUtils.excel2CSV(multipartFile);
//        // 插入数据到数据库
//        ChartEntity chartEntity = new ChartEntity();
//        chartEntity.setUserId(user.getUserId());
//        chartEntity.setName(name);
//        chartEntity.setGoal(goal);
//        chartEntity.setStatus(ChartStatusEnum.WAIT.getStatus());
//        chartEntity.setChartType(chartType);
//        chartEntity.setChartData(csvData);
//        boolean save = chartService.save(chartEntity);
//        // 2.submit task to thread pool
//        redisLimiterManager.doRateLimit("genChart_" + user.getUserId());
//        try {
//            CompletableFuture.runAsync(() -> {
//                ChartEntity genChartEntity = new ChartEntity();
//                genChartEntity.setId(chartEntity.getId());
//                genChartEntity.setStatus(ChartStatusEnum.RUNNING.getStatus());
//                boolean b = chartService.updateById(genChartEntity);
//                // 处理异常
//                ThrowUtils.throwIf(!b, new BusinessException(ErrorCode.SYSTEM_ERROR, "修改图表状态信息失败 " + chartEntity.getId()));
//                // 获取CSV
//                // 构造用户输入
//                StringBuilder userInput = new StringBuilder("");
//                // 拼接图表类型;
//                String userGoal = goal;
//                if (StringUtils.isNotBlank(chartType)) {
//                    userGoal += ", 请使用 " + chartType;
//                }
//                userInput.append("分析需求: ").append('\n');
//                userInput.append(userGoal).append("\n");
//                userInput.append("原始数据：").append("\n");
//                userInput.append(csvData).append("\n");
//                // 系统预设 ( 简单预设 )
//                /* 较好的做法是在系统（模型）层面做预设效果一般来说，会比直接拼接在用户消息里效果更好一些。*/
//                String result = aiManager.doChat(userInput.toString(), AIConstant.BI_MODEL_ID);
////                String result = aiManager.chatAndGenChart(goal,chartType,csvData);
//                String[] split = result.split("【【【【【");
//                // 第一个是 空字符串
//                if (split.length < 3) {
//                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误!");
//                }
//                // 图表代码
//                String genChart = split[1].trim();
//                // 分析结果
//                String genResult = split[2].trim();
//                String compressJson = compressJson(genChart);
//                // 更新数据
//                ChartEntity updateChartResult = new ChartEntity();
//                updateChartResult.setId(chartEntity.getId());
//                updateChartResult.setGenChart(compressJson);
//                updateChartResult.setGenResult(genResult);
//                updateChartResult.setStatus(ChartStatusEnum.SUCCEED.getStatus());
//                boolean updateGenResult = chartService.updateById(updateChartResult);
//                boolean syncResult = chartService.syncChart(chartEntity);
//                ThrowUtils.throwIf(!updateGenResult && syncResult, ErrorCode.SYSTEM_ERROR, "生成图表保存失败!");
//                try {
//                    webSocketServer.sendMessage("您的[" + chartEntity.getName() + "]生成成功 , 前往 我的图表 进行查看",
//                            new HashSet<>(Arrays.asList(chartEntity.getUserId().toString())));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }, threadPoolExecutor);
//
//        } catch (BusinessException e) {
//            ChartEntity updateChartResult = new ChartEntity();
//            updateChartResult.setId(chartEntity.getId());
//            updateChartResult.setStatus(ChartStatusEnum.FAILED.getStatus());
//            updateChartResult.setExecMessage(e.getDescription());
//            boolean updateResult = chartService.updateById(updateChartResult);
//            if (!updateResult) {
//                log.info("更新图表FAILED状态信息失败 , chatId:{}", updateChartResult.getId());
//            }
//        }
//        // return
//        BiResponse biResponse = new BiResponse();
//        biResponse.setChartId(chartEntity.getId());
//        return ResultUtil.success(biResponse);
//    }

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChartEntity(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ChartEntity chart = new ChartEntity();
        BeanUtils.copyProperties(chartAddRequest, chart);
        UserEntity loginUser = userService.getLoginUser();
        chart.setUserId(loginUser.getUserId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartEntityId = chart.getId();
        return ResultUtil.success(newChartEntityId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChartEntity(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserEntity user = userService.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        ChartEntity oldChartEntity = chartService.getById(id);
        ThrowUtils.throwIf(oldChartEntity == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChartEntity.getUserId().equals(user.getUserId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
//        boolean b = chartService.removeById(id);  不再直接删除原始数据
        boolean result = chartService.deleteFromMongo(id);
        // 如果没有已经生成好的文档, 那么应该显示原本数据
        if(chartService.getChartByChartId(oldChartEntity.getId())==null){
            oldChartEntity.setStatus(ChartStatusEnum.WAIT.getStatus());
            chartService.updateById(oldChartEntity);
        }
        return ResultUtil.success( result);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChartEntity(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ChartEntity chart = new ChartEntity();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        ChartEntity oldChartEntity = chartService.getById(id);
        ThrowUtils.throwIf(oldChartEntity == null, ErrorCode.NOT_FOUND_ERROR);
        // 查看用户是否修改了原始数据 : 重新提交到AI服务进行图表生成
        if (!oldChartEntity.getChartData().equals(chartUpdateRequest.getChartData())) {
            // 发送消息到AI生成模块重新进行生成
            biMqMessageProducer.sendGenChartMessage(String.valueOf(oldChartEntity.getId()));
        }
        boolean result = chartService.updateById(chart);
        return ResultUtil.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartEntityById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getChartByChartId(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtil.success(chart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartEntityByPage(@RequestBody ChartQueryRequest chartQueryRequest) {
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> charts = chartService.getChartList(chartQueryRequest);
        return ResultUtil.success(charts);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartEntityByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                             HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserEntity loginUser = userService.getLoginUser();
        chartQueryRequest.setUserId(loginUser.getUserId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> charts = chartService.getChartList(chartQueryRequest);
        return ResultUtil.success(charts);
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChartEntity(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ChartEntity chart = new ChartEntity();
        BeanUtils.copyProperties(chartEditRequest, chart);
        UserEntity loginUser = userService.getLoginUser();
        long id = chartEditRequest.getId();
        // 判断是否存在
        ChartEntity oldChartEntity = chartService.getById(id);
        ThrowUtils.throwIf(oldChartEntity == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChartEntity.getUserId().equals(loginUser.getUserId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        // 查看用户是否修改了原始数据 : 重新提交到AI服务进行图表生成
        if (!oldChartEntity.getChartData().equals(chartEditRequest.getChartData())) {
            // 发送消息到AI生成模块重新进行生成
            biMqMessageProducer.sendGenChartMessage(String.valueOf(oldChartEntity.getId()));
        }
        return ResultUtil.success(result);
    }

    /**
     * 压缩json
     *
     * @param data 数据
     * @return {@link String}
     */
    public String compressJson(String data) {
        data = data.replaceAll("\t+", "");
        data = data.replaceAll(" +", "");
        data = data.replaceAll("\n+", "");
        return data;
    }
}
