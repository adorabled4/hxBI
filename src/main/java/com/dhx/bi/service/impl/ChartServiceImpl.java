package com.dhx.bi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dhx.bi.common.constant.CommonConstant;
import com.dhx.bi.manager.StrategySelector;
import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DTO.ServerLoadInfo;
import com.dhx.bi.model.DTO.chart.BiResponse;
import com.dhx.bi.model.DTO.chart.ChartQueryRequest;
import com.dhx.bi.model.VO.ChartVO;
import com.dhx.bi.model.document.Chart;
import com.dhx.bi.repository.ChartRepository;
import com.dhx.bi.service.ChartService;
import com.dhx.bi.mapper.ChartMapper;
import com.dhx.bi.service.GenChartStrategy;
import com.dhx.bi.utils.SqlUtils;
import com.dhx.bi.utils.UserHolder;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author dhx
 * @description 针对表【t_chart(图表表)】的数据库操作Service实现
 * @createDate 2023-08-01 14:42:26
 */
@Service
@Slf4j
public class ChartServiceImpl extends ServiceImpl<ChartMapper, ChartEntity>
        implements ChartService {

    @Resource
    ChartRepository chartRepository;

    @Resource
    MongoTemplate mongoTemplate;

    @Resource
    StrategySelector strategySelector;

    @Override
    public boolean saveDocument(Chart chart) {
        Long chartId = chart.getChartId();
        List<Chart> charts = chartRepository.findAllByChartId(chartId);
        if (charts.size() != 0) {
            return updateDocument(chart);
        } else {
            Chart save = chartRepository.save(chart);
            return true;
        }
    }

    @Override
    public boolean syncChart(ChartEntity chartEntity, String genChart, String genResult) {
        Chart chart = BeanUtil.copyProperties(chartEntity, Chart.class);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setChartId(chartEntity.getId());
        Long chartId = chart.getChartId();
        List<Chart> charts = chartRepository.findAllByChartId(chartId);
        if (charts.size() != 0) {
            return updateDocument(chart);
        } else {
            chart.setVersion(1);
            Chart save = chartRepository.save(chart);
            return true;
        }
    }

    @Override
    public List<Chart> listDocuments(long userId) {
        return chartRepository.findAllByUserId(userId, PageRequest.of(3, 1));
    }

    @Override
    public Page<Chart> getChartList(ChartQueryRequest chartQueryRequest) {
        // page size
        // 页号 每一页的大小
        // 这个API的页号是从0开始的
        // 默认按照时间降序
        PageRequest pageRequest = PageRequest.of(chartQueryRequest.getCurrent() - 1, chartQueryRequest.getPageSize(), Sort.by("creatTime").descending());
        Long userId = chartQueryRequest.getUserId();
        if (userId == null) {
            userId = UserHolder.getUser().getUserId();
        }
        String name = chartQueryRequest.getName();
        // 查找符合搜索名称的chart
        if (StringUtils.isNotBlank(name)) {
            // . 可以重复 0~n次 , 匹配所有满足的name
            String regex = ".*" + name + ".*";
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(userId).and("name").regex(regex));
            query.with(pageRequest);
            List<Chart> charts = mongoTemplate.find(query, Chart.class);
            return excludeOldVersionAndBuildPage(charts, pageRequest);
        } else {
            List<Chart> charts = chartRepository.findAllByUserId(userId, pageRequest);
            return excludeOldVersionAndBuildPage(charts, pageRequest);
        }
    }

    /**
     * 排除旧版本和构建返回Page
     *
     * @param charts   图表
     * @param pageable 可分页
     * @return {@link Page}<{@link Chart}>
     */
    private Page<Chart> excludeOldVersionAndBuildPage(List<Chart> charts, Pageable pageable) {
        long count = chartRepository.count();
        // 排除旧版本号Chart
        Map<Long, Chart> latestChartsMap = new HashMap<>();
        for (Chart chart : charts) {
            Long chartId = chart.getChartId();
            // 当chartId 相同时 , 获取version较大的chart
            if (!latestChartsMap.containsKey(chartId) || chart.getVersion() > latestChartsMap.get(chartId).getVersion()) {
                latestChartsMap.put(chartId, chart);
            }
        }
        return new PageImpl<>(new ArrayList<>(latestChartsMap.values()), pageable, count);
    }

    @Override
    public Chart getChartByChartId(long chartId) {
        List<Chart> charts = chartRepository.findAllByChartId(chartId);
        if (charts.size() == 0) return null;
        if (charts.size() == 1) {
            return charts.get(0);
        }
        // 找到最大的版本
        int maxVersionIdx = 0;
        int maxVersion = Integer.MIN_VALUE;
        for (int i = 0; i < charts.size(); i++) {
            Chart chart = charts.get(i);
            if (chart.getVersion() > maxVersion) {
                maxVersionIdx = i;
                maxVersion = chart.getVersion();
            }
        }
        return charts.get(maxVersionIdx);
    }

    @Override
    public boolean insertChart(ChartEntity chartEntity) {
        try {
            Chart chart = BeanUtil.copyProperties(chartEntity, Chart.class);
            chart.setChartId(chartEntity.getId());
            chart.setVersion(Chart.DEFAULT_VERSION);
            long chartId = chart.getChartId();
            Query query = new Query();
            query.addCriteria(Criteria.where("chartId").is(chartId));
            List<Chart> charts = mongoTemplate.find(query, Chart.class);
            // 是新的图表
            if (charts.size() == 0) {
                chartRepository.save(chart);
            } else {
                // 是需要更新的图表 : 获取新的版本号 => 保存
                int nextVersion = getNextVersion(charts);
                chart.setVersion(nextVersion);
                chartRepository.save(chart);
            }
            return true;
        } catch (RuntimeException e) {
            log.error("保存Chart到MongoDB失败 : {} , 异常信息:{} ", chartEntity, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteAllFromMongo(long id) {
        return chartRepository.deleteAllByChartId(id) != -1;
    }

    @Override
    public boolean updateDocument(Chart chart) {
        try {
            // 不设置ID ,使用MongoDB自动的ObjectId
            chart.setId(null);
            List<Chart> allByChartId = chartRepository.findAllByChartId(chart.getChartId());
            int nextVersion = getNextVersion(allByChartId);
            chart.setVersion(nextVersion);
            Chart save = chartRepository.save(chart);
            return true;
        } catch (RuntimeException e) {
            log.error("更新文档失败: {},{}", e, chart);
            return false;
        }
    }

    /**
     * 获取下一个版本号
     *
     * @param charts 图表
     * @return int
     */
    private int getNextVersion(List<Chart> charts) {
        int maxVersion = Integer.MIN_VALUE;
        for (int i = 0; i < charts.size(); i++) {
            Chart chart = charts.get(i);
            if (chart.getVersion() > maxVersion) {
                maxVersion = chart.getVersion();
            }
        }
        return maxVersion + 1;
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ChartEntity> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<ChartEntity> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = UserHolder.getUser().getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chart_type", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChartVO> buildPage(com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChartEntity> page, List<ChartVO> chartVOS) {
        long total = page.getTotal();
        long current = page.getCurrent();
        long size = page.getSize();
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChartVO> newPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        newPage.setTotal(total);
        newPage.setSize(size);
        newPage.setCurrent(current);
        newPage.setRecords(chartVOS);
        return newPage;
    }

    @Override
    public boolean deleteSingleFromMongo(long id, int version) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chartId").is(id));
        query.addCriteria(Criteria.where("version").is(version));
        DeleteResult remove = mongoTemplate.remove(query, Chart.class);
        // 按照前端的参数, 必定会存在一个对应的document , 如果没有就是删除失败了
        return remove.getDeletedCount() == 1;
    }

    @Override
    public BiResponse genChart(ChartEntity chartEntity, ServerLoadInfo info) {
        GenChartStrategy genChartStrategy = strategySelector.selectStrategy(info);
        return genChartStrategy.executeGenChart(chartEntity);
    }
}




