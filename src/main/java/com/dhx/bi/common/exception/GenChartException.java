package com.dhx.bi.common.exception;

import com.dhx.bi.common.ErrorCode;
import lombok.Data;

/**
 * @author adorabled4
 * @className GenChartException
 * @date : 2023/08/27/ 22:53
 **/
public class GenChartException extends BusinessException {

    /**
     * 图表id
     */
    private Long chartId;

    public GenChartException(int code, String description, String message) {
        super(code, description, message);
    }

    public GenChartException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }

    public GenChartException(ErrorCode errorCode) {
        super(errorCode);
    }


    public GenChartException(long chartId, BusinessException e) {
        super(e.getCode(), e.getDescription(), e.getMessage());
        this.chartId = chartId;
    }

    public GenChartException() {
        super(ErrorCode.SYSTEM_ERROR);
    }

    public Long getChartId() {
        return chartId;
    }

    public void setChartId(Long chartId) {
        this.chartId = chartId;
    }
}
