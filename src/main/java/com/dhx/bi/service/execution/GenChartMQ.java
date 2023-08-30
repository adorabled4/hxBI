package com.dhx.bi.service.execution;

import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DTO.chart.BiResponse;
import com.dhx.bi.service.GenChartStrategy;

/**
 * 通过MQ异步消息生成
 *
 * @author adorabled4
 * @className GenChartSync
 * @date 2023/08/30
 */
public class GenChartMQ implements GenChartStrategy {

    @Override
    public BiResponse executeGenChart(ChartEntity chartEntity) {
        return null;
    }
}
