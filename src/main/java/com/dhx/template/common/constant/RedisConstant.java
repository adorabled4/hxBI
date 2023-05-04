package com.dhx.template.common.constant;

import java.util.concurrent.TimeUnit;

/**
 * @author adorabled4
 * @className RedisConstant
 * @date : 2023/05/04/ 16:49
 **/
public class RedisConstant {

    /**
     *  jwt 秘钥
     */
    public static final String SECRET_KEY = "mySecretKey";

    /**
     * token过期时间
     */
    public static final long EXPIRATION_TIME = TimeUnit.DAYS.toSeconds(1);

    /**
     * access_token 前缀
     */
    public static final String ACCESS_TOKEN_PREFIX = "access_token:";

    /**
     * 刷新token 前缀
     */
    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:";


    /**
     * 缓存用户剩余次数
     */
    public static final String USER_INTERFACE_INFO_PREFIX= "user:interface:";

    /**
     *
     */
    public static final long LEFT_NUM_TTL = TimeUnit.DAYS.toHours(12);


    /**
     * 是一个集合
     */
    public static final String INTERFACE_RANK_KEY = "interface:rank";
}
