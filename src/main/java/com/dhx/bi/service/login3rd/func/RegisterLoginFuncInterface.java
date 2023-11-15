package com.dhx.bi.service.login3rd.func;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.model.DTO.user.VerifyCodeRegisterRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author adorabled4
 * @className RegisterLoginFuncInterface
 * @date : 2023/11/15/ 11:55
 **/
public interface RegisterLoginFuncInterface {
    BaseResponse login(String email, String password);

    BaseResponse register(VerifyCodeRegisterRequest registerRequest);

    boolean checkUserExists(String email);

    BaseResponse login3rd(HttpServletRequest request);
}
