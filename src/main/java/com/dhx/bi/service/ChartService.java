package com.dhx.bi.service;

import com.dhx.bi.model.DO.ChartEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dhx.bi.model.document.Chart;

/**
* @author dhx
* @description 针对表【t_chart(图表表)】的数据库操作Service
* @createDate 2023-08-01 14:42:26
*/
public interface ChartService extends IService<ChartEntity> {

    /**
     * 保存chart文档
     *
     * @param chart 图表
     * @return boolean
     */
    boolean saveDocument(Chart chart);
}
