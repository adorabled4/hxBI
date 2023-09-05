package com.dhx.bi.model.DTO;

import lombok.Data;

import java.util.Date;

/**
 * @author adorabled4
 * @className ChartLogDTO
 * @date : 2023/09/03/ 14:40
 **/
@Data
public class ChartLogDTO {

    /**
     * 时间
     */
    private Date createTime;

    /**
     * 结果 : succeed or failed
     */
    private String result;

    /**
     * 次数
     */
    private Long count;
}
