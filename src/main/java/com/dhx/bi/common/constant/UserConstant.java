package com.dhx.bi.common.constant;

/**
 * @author dhx_
 * @className UserConstans
 * @date : 2023/01/07/ 14:53
 **/
public class UserConstant {

    public static final String PASSWORD_SALT = "dhxSalt";

    public static final int USER_PAGE_SIZE = 10;

    /**
     * 默认权限 => 普通用户
     */
    public static final  int DEFAULT_ROLE= 0;

    /**
     * 管理员权限
     */
    public static final  int ADMIN_ROLE= 1;

    /**
     * 普通用户
     */
    public static final String DEFAULT_USER="0";

    /**
     * 管理员
     */
    public static final String ADMIN_USER="1";


    /**
     * 用户名正则
     */
    public static final String USER_NAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{6,12}$";

    /**
     * 密码正则
     */
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[\\s\\S]{8,16}$";
}