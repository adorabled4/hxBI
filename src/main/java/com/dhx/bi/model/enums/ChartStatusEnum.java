package com.dhx.bi.model.enums;

/**
 * chart状态枚举
 *
 * @author adorabled4
 * @className ChartStatusEnum
 * @date 2023/08/15
 */
public enum ChartStatusEnum {

    /**
     * 等待
     */
    WAIT("wait"),
    /**
     * 运行
     */
    RUNNING("running"),
    /**
     * 成功
     */
    SUCCEED("succeed"),
    /**
     * 提起
     */
    FAILED("failed");
    /**
     * 状态
     */
    final String status;

    ChartStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
