package com.dhx.bi.service;

import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DO.ChartLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dhx.bi.model.DTO.ChartLogDTO;

import java.util.List;

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

    /**
     * 获取过去 dayCount  天数的日志信息
     * @param dayCount
     * @return {@link List}<{@link ChartLogEntity}>
     */
    List<ChartLogDTO> getLogs(Integer dayCount, Long userId);
}
