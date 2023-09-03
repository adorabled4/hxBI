package com.dhx.bi.service;

import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DO.ChartLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author dhx
 * @description 针对表【chart_logs】的数据库操作Service
 * @createDate 2023-09-03 13:24:19
 */
public interface ChartLogService extends IService<ChartLogEntity> {

    /**
     * 记录生成日志
     *
     * @param chartEntity
     */
    Long recordLog(ChartEntity chartEntity);
}
