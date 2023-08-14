package com.dhx.bi.model.DTO.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * @author adorabled4
 * @className ChartEditRequest
 * @date : 2023/07/04/ 10:30
 **/
@Data
public class ChartEditRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * id
     */
    private Long id;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 执行信息
     */
    private String execmessage;

    /**
     * wait,running,succeed,failed
     */
    private String status;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;

    private static final long serialVersionUID = 1L;
}