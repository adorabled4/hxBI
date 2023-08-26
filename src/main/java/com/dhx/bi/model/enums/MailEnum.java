package com.dhx.bi.model.enums;

/**
 * @author adorabled4
 * @enum MailEnum
 * @date : 2023/08/26/ 12:23
 **/
public enum MailEnum {

    /**
     * 验证码
     */
    VERIFY_CODE("[hxBI]您的验证码为: ", "请确保信息安全, 不要泄露您的验证码", "[hxBI]验证码");

    /**
     * 前缀
     */
    private String prefix;
    /**
     * 后缀
     */
    private String suffix;
    /**
     * 标题
     */
    private String title;

    MailEnum(String prefix, String suffix, String title) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.title = title;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getTitle() {
        return title;
    }
}
