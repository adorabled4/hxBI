package com.dhx.bi.model.enums;

/**
 * @author adorabled4
 * @className GenChartStrategyEnum
 * @date : 2023/08/30/ 13:20
 **/
public enum GenChartStrategyEnum {

    GEN_MQ("gen_mq"),
    GEN_SYNC("gen_sync"),
    GEN_THREAD_POOL("gen_thread_pool"),
    GEN_REJECT("gen_reject");

    private String value;

    GenChartStrategyEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
