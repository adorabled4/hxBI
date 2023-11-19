package com.dhx.bi.model.enums;


/**
 * @author adorabled4
 * @className PointChangeEnum
 * @date : 2023/11/19/ 14:30
 **/
public enum PointChangeEnum {

    DAILY_LOGIN_ADD("SYSTEM", 50, "每日登录奖励", ChangeType.INCREASE),
    CHAT_DEDUCT("SYSTEM", 2, "在线提问扣除", ChangeType.DECREASE),
    GEN_CHART_DEDUCT("SYSTEM", 5, "生成图表扣除", ChangeType.DECREASE),
    GEN_CHART_FAILED_ADD("SYSTEM", 3, "生成图表失败补偿", ChangeType.DECREASE);

    /**
     * 改变来源
     */
    private String source;

    /**
     * 变化量
     */
    private int changeAmount;

    /**
     * 原因说明
     */
    private String reason;

    /**
     * 改变类型(增加or减少)
     */
    private ChangeType changeType;

    PointChangeEnum(String source, int changeAmount, String reason, ChangeType changeType) {
        this.source = source;
        this.changeAmount = changeAmount;
        this.changeType = changeType;
        this.reason = reason;
    }

    public String getSource() {
        return source;
    }

    public int getChangeAmount() {
        return changeAmount;
    }

    public String getReason() {
        return reason;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    /**
     * 变化类型
     *
     * @author dhx
     * @date 2023/11/19
     */
    public enum ChangeType {
        INCREASE,
        DECREASE
    }
}
