package com.dhx.bi.service.execution;

import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.constant.AIConstant;
import com.dhx.bi.common.exception.BusinessException;
import com.dhx.bi.common.exception.GenChartException;
import com.dhx.bi.manager.AiManager;
import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DTO.chart.BiResponse;
import com.dhx.bi.model.enums.ChartStatusEnum;
import com.dhx.bi.model.enums.PointChangeEnum;
import com.dhx.bi.service.ChartLogService;
import com.dhx.bi.service.ChartService;
import com.dhx.bi.service.GenChartStrategy;
import com.dhx.bi.service.PointService;
import com.dhx.bi.utils.ChartUtil;
import com.dhx.bi.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 同步生成
 *
 * @author adorabled4
 * @className GenChartSync
 * @date : 2023/08/30/ 11:46
 **/
@Component(value = "gen_sync")
@Slf4j
//@Component(value = GenChartStrategyEnum.GEN_SYNC.getValue())
public class GenChartSync implements GenChartStrategy {

    @Resource
    ChartService chartService;

    @Resource
    AiManager aiManager;

    @Resource
    ChartLogService logService;

    @Resource
    PointService pointService;
    @Override
    public BiResponse executeGenChart(ChartEntity chartEntity) {
        // 系统预设 ( 简单预设 )
        /* 较好的做法是在系统（模型）层面做预设效果一般来说，会比直接拼接在用户消息里效果更好一些。*/
        /*
        分析需求：
        分析网站用户的增长情况
        原始数据：
        日期,用户数
        1号,10
        2号,20
        3号,30
        */
//        String result = aiManager.doChat(userInput.toString(), AIConstant.BI_MODEL_ID);
        try{
            String userInput = ChartUtil.buildUserInput(chartEntity);
            String result = aiManager.doChat(userInput, AIConstant.BI_MODEL_ID);
            String[] split = result.split("【【【【【");
            // 第一个是 空字符串
            if (split.length < 3) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误!");
            }
            // 图表代码
            String genChart = split[1].trim();
            // 分析结果
            String genResult = split[2].trim();
            // 更新数据到数据库
//            chartEntity.setGenChart(genChart);
//            chartEntity.setGenResult(genResult);
            chartEntity.setStatus(ChartStatusEnum.SUCCEED.getStatus());
            chartEntity.setExecMessage(ChartStatusEnum.SUCCEED.getMessage());
            chartEntity.setExecMessage("生成成功");
            genChart = ChartUtil.compressJson(genChart);
            boolean save = chartService.updateById(chartEntity);
            ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "图表保存失败!");
            boolean syncResult = chartService.syncChart(chartEntity,genChart,genResult);
            ThrowUtils.throwIf(!syncResult, ErrorCode.SYSTEM_ERROR, "图表同步失败!");
            // 记录生成日志
            logService.recordLog(chartEntity);
            // 封装返回结果
            BiResponse biResponse = new BiResponse();
            biResponse.setGenChart(genChart);
            biResponse.setChartId(chartEntity.getId());
            biResponse.setGenResult(genResult);
            return biResponse;
        } catch (BusinessException e) {
            // 更新状态信息
            ChartEntity updateChartResult = new ChartEntity();
            updateChartResult.setId(chartEntity.getId());
            updateChartResult.setStatus(ChartStatusEnum.FAILED.getStatus());
            updateChartResult.setExecMessage(e.getDescription());
            boolean updateResult = chartService.updateById(updateChartResult);
            // 记录生成日志
            logService.recordLog(chartEntity);
            if (!updateResult) {
                log.info("更新图表FAILED状态信息失败 , chatId:{}", updateChartResult.getId());
            }
            pointService.sendCompensateMessage(chartEntity.getUserId(), PointChangeEnum.GEN_CHART_FAILED_ADD);
            // 抛出异常进行日志打印
            throw new GenChartException(chartEntity.getId(), e);
        }
    }
}
