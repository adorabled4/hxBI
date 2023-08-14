package com.dhx.bi.model.DTO.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * @author adorabled4
 * @className ChartAddRequest
 * @date : 2023/07/04/ 10:21
 **/
@Data
public class ChartAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * wait,running,succeed,failed
     */
    private String status;

    /**
     * 执行信息
     */
    private String execmessage;

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
