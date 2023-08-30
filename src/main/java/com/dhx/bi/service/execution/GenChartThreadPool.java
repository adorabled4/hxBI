package com.dhx.bi.service.execution;

import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DTO.chart.BiResponse;
import com.dhx.bi.service.GenChartStrategy;

import javax.annotation.Resource;

/**
 * 线程池生成
 *
 * @author adorabled4
 * @className GenChartSync
 * @date 2023/08/30
 */
public class GenChartThreadPool implements GenChartStrategy {


    @Override
    public BiResponse executeGenChart(ChartEntity chartEntity) {
        return null;
    }
}
