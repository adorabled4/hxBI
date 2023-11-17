package com.dhx.bi.model.DTO.user;

import com.dhx.bi.common.constant.UserConstant;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author adorabled4
 * @className VerifyCodeRegisterRequest
 * @date : 2023/08/26/ 12:50
 **/
@Data
@Builder
public class VerifyCodeRegisterRequest {

    /**
     * 4~16位 数字,大小写字母组成
     */
    @Pattern(regexp = UserConstant.EMAIL_REGEX,message = "邮箱不符合规范")
    private String email;


    /**
     * 至少8-16个字符，至少1个大写字母，1个小写字母和1个数字，其他可以是任意字符
     */
    @Pattern(regexp = UserConstant.PASSWORD_REGEX,message = "密码不符合规范")
    private String password;

    /**
     * 代码
     */
    private String code;
}
