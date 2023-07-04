package com.dhx.bi.model.VO;

import lombok.Data;

import java.io.Serializable;

/**
 * @author adorabled4
 * @className UserBaseInfoVo
 * @date : 2023/05/04/ 16:20
 **/
@Data
public class UserBaseInfoVo implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 昵称
     */
    private String userName;

    /**
     * 账户
     */
    private String userAccount;

    /**
     * 地址
     */
    private String address;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 性别1男0女
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 是否是管理员
     */
    private Integer userRole;


    private static final long serialVersionUID = 1L;
}
