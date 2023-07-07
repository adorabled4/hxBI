package com.dhx.bi.model.DTO.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * @author adorabled4
 * @className GenChartByAIRequest
 * @date : 2023/07/05/ 09:27
 **/
@Data
public class GenChartByAIRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 业务
     */
    private String biz;

    /**
     * 表名称
     */
    private String name;

    /**
     * 目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;


}
