package com.dhx.bi.service.login3rd;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.model.DTO.user.VerifyCodeRegisterRequest;
import com.dhx.bi.service.login3rd.func.RegisterLoginFuncInterface;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author adorabled4
 * @className RegisterLoginComponent
 * @date 2023/11/15
 */
public class RegisterLoginComponent extends AbstractRegisterLoginComponent {

    public RegisterLoginComponent(RegisterLoginFuncInterface funcInterface) {
        super(funcInterface);
    }

    @Override
    public BaseResponse login(String email, String password) {
        return funcInterface.login(email, password);
    }

    @Override
    public BaseResponse register(String email) {
        return funcInterface.register(VerifyCodeRegisterRequest.builder().email(email).build());
    }

    @Override
    public boolean checkUserExists(String email) {
        return funcInterface.checkUserExists(email);
    }

    @Override
    public BaseResponse login3rd(HttpServletRequest request) {
        return funcInterface.login3rd(request);
    }
}
