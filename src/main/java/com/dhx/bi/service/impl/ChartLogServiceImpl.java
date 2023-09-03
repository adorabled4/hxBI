package com.dhx.bi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DO.ChartLogEntity;
import com.dhx.bi.model.enums.ChartStatusEnum;
import com.dhx.bi.service.ChartLogService;
import com.dhx.bi.mapper.ChartLogMapper;
import org.springframework.stereotype.Service;

/**
 * @author dhx
 * @description 针对表【chart_logs】的数据库操作Service实现
 * @createDate 2023-09-03 13:24:19
 */
@Service
public class ChartLogServiceImpl extends ServiceImpl<ChartLogMapper, ChartLogEntity>
        implements ChartLogService {


    @Override
    public Long recordLog(ChartEntity chartEntity) {
        ChartLogEntity chartLogEntity = new ChartLogEntity();
        chartLogEntity.setUserId(chartEntity.getUserId());
        chartLogEntity.setChartId(chartEntity.getId());
        if (chartEntity.getStatus().equals(ChartStatusEnum.SUCCEED.getStatus())) {
            chartLogEntity.setResult(ChartStatusEnum.SUCCEED.getStatus());
        } else {
            chartLogEntity.setResult(ChartStatusEnum.FAILED.getStatus());
        }
        boolean save = save(chartLogEntity);
        return chartLogEntity.getLogId();
    }
}




