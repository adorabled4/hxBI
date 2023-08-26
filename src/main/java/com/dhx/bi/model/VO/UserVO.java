package com.dhx.bi.model.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author adorabled4
 * @className UserVO
 * @date : 2023/05/04/ 16:19
 **/
@Data
public class UserVO implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 昵称
     */
    private String userName;



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
     * 邮箱
     */
    private String email;


    /**
     * 是否是管理员
     */
    private String userRole;

    /**
     * 出生日期
     */
    private Date birth;

    private static final long serialVersionUID = 1L;

}
