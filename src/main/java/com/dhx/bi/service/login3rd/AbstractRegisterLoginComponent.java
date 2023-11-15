package com.dhx.bi.service.login3rd;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.service.login3rd.func.RegisterLoginFuncInterface;

import javax.servlet.http.HttpServletRequest;

/**
 * 摘要注册登录组件
 *
 * @author adorabled4
 * @className AbstractRegisterLoginComponent
 * @date 2023/11/15
 */
public abstract class AbstractRegisterLoginComponent {
    protected RegisterLoginFuncInterface funcInterface;

    public AbstractRegisterLoginComponent(RegisterLoginFuncInterface funcInterface) {
        validate(funcInterface);
        this.funcInterface = funcInterface;
    }
    protected final void validate(RegisterLoginFuncInterface funcInterface) {
        if(!(funcInterface instanceof RegisterLoginFuncInterface)) {
            throw new UnsupportedOperationException("Unknown register/login function type!");
        }
    }

    public abstract BaseResponse login(String email, String password);
    public abstract BaseResponse register(String email);
    public abstract boolean checkUserExists(String userName);
    public abstract BaseResponse login3rd(HttpServletRequest request);
}
