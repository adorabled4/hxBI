package com.dhx.bi.service.login3rd.func;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.model.DTO.user.VerifyCodeRegisterRequest;
import com.dhx.bi.service.UserService;
import com.dhx.bi.service.login3rd.factory.RegisterLoginComponentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author adorabled4
 * @className RegisterLoginByDefault
 * @date : 2023/11/15/ 11:58
 **/
@Component
public class RegisterLoginByDefault extends AbstractRegisterLoginFunc implements RegisterLoginFuncInterface {

    @Autowired
    private UserService userService;

    @PostConstruct
    private void initFuncMap() {
        RegisterLoginComponentFactory.funcMap.put("Default", this);
    }

    @Override
    public BaseResponse login(String email, String password) {
        return super.commonLogin(email, password, userService);
    }

    @Override
    public BaseResponse register(VerifyCodeRegisterRequest registerRequest) {
        return super.commonRegister(registerRequest, userService);
    }
    @Override
    public boolean checkUserExists(String userName) {
        return super.commonCheckUserExists(userName, userService);
    }
}
