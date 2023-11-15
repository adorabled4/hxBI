package com.dhx.bi.service.login3rd.func;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.model.DO.UserEntity;
import com.dhx.bi.model.DTO.user.VerifyCodeRegisterRequest;
import com.dhx.bi.service.UserService;
import com.dhx.bi.utils.ResultUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author adorabled4
 * @className AbstractRegisterLoginFunc
 * @date : 2023/11/15/ 11:55
 **/
public abstract class AbstractRegisterLoginFunc implements RegisterLoginFuncInterface {
    protected BaseResponse commonLogin(String email, String password, UserService userService) {
        return userService.login(email,password);
    }

    protected BaseResponse commonRegister(VerifyCodeRegisterRequest registerRequest, UserService userService) {
        if(commonCheckUserExists(registerRequest.getEmail(), userService)) {
            throw new RuntimeException("User already registered.");
        }
        if(userService.register(registerRequest)){
            return ResultUtil.success("注册成功");
        }
        return ResultUtil.success("注册失败");
    }

    protected boolean commonCheckUserExists(String email, UserService userService) {
        UserEntity user = userService.getOne(new QueryWrapper<UserEntity>().eq("email", email));
        if(user == null) {
            return false;
        }
        return true;
    }
    @Override
    public BaseResponse login(String email, String password) {
        throw new UnsupportedOperationException();
    }
    @Override
    public BaseResponse register(VerifyCodeRegisterRequest request){
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkUserExists(String userName){
        throw new UnsupportedOperationException();
    }
    @Override
    public BaseResponse login3rd(HttpServletRequest request) {
        throw new UnsupportedOperationException();
    }
}
