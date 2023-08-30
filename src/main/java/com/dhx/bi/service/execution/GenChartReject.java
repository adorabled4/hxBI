package com.dhx.bi.service.execution;

import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.exception.BusinessException;
import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DTO.chart.BiResponse;
import com.dhx.bi.service.GenChartStrategy;
import org.springframework.stereotype.Service;

/**
 * 拒绝策略
 *
 * @author adorabled4
 * @className GenChartSync
 * @date 2023/08/30
 */
@Service
public class GenChartReject implements GenChartStrategy {

    @Override
    public BiResponse executeGenChart(ChartEntity chartEntity) {
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "服务器繁忙,请稍后重试!");
    }
}
