package com.dhx.bi.model.document;

import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author adorabled4
 * @className Chart
 * @date : 2023/08/25/ 17:20
 **/
@Document("chart")
@Data
public class Chart {

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 表名称
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
    private String execMessage;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 生成的图表数据
     */
    private String genChart;

    /**
     * 生成的分析结论
     */
    private String genResult;

    /**
     * 创建时间
     */
    private Date createTime;

//
//    /**
//     * 更新时间
//     */
//    private Date updateTime;
//
//    /**
//     * 逻辑删除
//     */
//    private Integer isDelete;

    private static final long serialVersionUID = 1L;


    @Override
    public String toString() {
        return "Chart{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", goal='" + goal + '\'' +
                ", status='" + status + '\'' +
                ", execMessage='" + execMessage + '\'' +
                ", chartData='" + chartData + '\'' +
                ", chartType='" + chartType + '\'' +
                ", genChart='" + genChart + '\'' +
                ", genResult='" + genResult + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Chart chart = (Chart) o;

        return new EqualsBuilder().append(id, chart.id).append(userId, chart.userId).append(name, chart.name).append(goal, chart.goal).append(status, chart.status).append(execMessage, chart.execMessage).append(chartData, chart.chartData).append(chartType, chart.chartType).append(genChart, chart.genChart).append(genResult, chart.genResult).append(createTime, chart.createTime).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(userId).append(name).append(goal).append(status).append(execMessage).append(chartData).append(chartType).append(genChart).append(genResult).append(createTime).toHashCode();
    }
}
