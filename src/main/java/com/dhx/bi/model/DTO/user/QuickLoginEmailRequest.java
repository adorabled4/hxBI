package com.dhx.bi.model.DTO.user;

import com.dhx.bi.common.constant.UserConstant;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author adorabled4
 * @className LoginParam
 * @date : 2023/05/04/ 16:41
 **/
@Data
public class QuickLoginEmailRequest {
    /**
     * 4~16位 数字,大小写字母组成
     */
    @Pattern(regexp = UserConstant.EMAIL_REGEX,message = "邮箱不符合规范")
    private String email;


    /**
     * 代码
     */
    @Pattern(regexp = "[0-9]{6}", message = "验证码错误!")
    private String code;

}
