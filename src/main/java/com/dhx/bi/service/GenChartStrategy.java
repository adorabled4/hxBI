package com.dhx.bi.service;

import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DTO.chart.BiResponse;

/**
 * @author adorabled4
 * @className GenChartStrategy
 * @date : 2023/08/30/ 11:41
 **/
public interface GenChartStrategy {

    BiResponse executeGenChart(ChartEntity chartEntity);
}
