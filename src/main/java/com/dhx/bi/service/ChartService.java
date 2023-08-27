package com.dhx.bi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dhx.bi.model.DO.ChartEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dhx.bi.model.DTO.chart.ChartQueryRequest;
import com.dhx.bi.model.document.Chart;
import org.springframework.data.domain.Page;

import java.util.List;

/**
* @author dhx
* @description 针对表【t_chart(图表表)】的数据库操作Service
* @createDate 2023-08-01 14:42:26
*/
public interface ChartService extends IService<ChartEntity> {

    /**
     * 保存chart文档 : 当存在旧版本时自动设置为newVersion
     *
     * @param chart 图表
     * @return boolean
     */
    boolean saveDocument(Chart chart);

    /**
     * 列表文件
     *
     * @param userId 用户id
     * @return {@link List}<{@link Chart}>
     */
    List<Chart> listDocuments(long userId);

    /**
     * 查询图表Document
     *
     * @param chartQueryRequest 图查询请求
     * @return {@link Page}<{@link Chart}>
     */
    Page<Chart> getChartList(ChartQueryRequest chartQueryRequest);


    /**
     * 通过ChartId 获取 Chart(latest version)
     *
     * @param chartId 表id
     * @return {@link Chart}
     */
    Chart getChartByChartId(long chartId);

    /**
     * 插入Chart
     *
     * @param chartEntity 表实体
     * @return boolean
     */
    boolean insertChart(ChartEntity chartEntity);

    /**
     * 从mongo删除Chart
     *
     * @param id id
     * @return boolean
     */
    boolean deleteFromMongo(long id);


    /**
     * 从mongo更新Chart : 创建新的版本
     *
     * @param chart 图表
     * @return boolean
     */
    boolean updateDocument(Chart chart);

    QueryWrapper<ChartEntity> getQueryWrapper(ChartQueryRequest chartQueryRequest);
}
