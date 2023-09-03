package com.dhx.bi.mapper;

import com.dhx.bi.model.DO.ChartLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dhx.bi.model.DTO.ChartLogDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author dhx
* @description 针对表【chart_logs】的数据库操作Mapper
* @createDate 2023-09-03 13:24:19
* @Entity com.dhx.bi.model.DO.ChartLogEntity
*/
public interface ChartLogMapper extends BaseMapper<ChartLogEntity> {

    List<ChartLogDTO> getLogs(@Param("dayCount") Integer dayCount, @Param("userId")Long userId);
}




