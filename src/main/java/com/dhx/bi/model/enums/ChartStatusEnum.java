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
    WAIT("wait","生成等待中"),
    /**
     * 运行
     */
    RUNNING("running","正在执行生成..."),
    /**
     * 成功
     */
    SUCCEED("succeed","生成成功"),
    /**
     * 提起
     */
    FAILED("failed","图表生成失败");
    /**
     * 状态
     */
    final String status;

    /**
     * 执行信息
     */
    String message;

    ChartStatusEnum(String status, String msg) {
        this.status = status;
        this.message = msg;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
