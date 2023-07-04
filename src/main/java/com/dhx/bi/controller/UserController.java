package com.dhx.bi.controller;

import cn.hutool.core.bean.BeanUtil;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.annotation.AuthCheck;
import com.dhx.bi.common.constant.UserConstant;
import com.dhx.bi.model.DO.UserEntity;
import com.dhx.bi.model.DTO.UserDTO;
import com.dhx.bi.model.VO.UserVO;
import com.dhx.bi.model.param.LoginParam;
import com.dhx.bi.model.param.RegisterParam;
import com.dhx.bi.service.UserService;
import com.dhx.bi.utils.ResultUtil;
import com.dhx.bi.utils.UserHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @author adorabled4
 * @className UserController
 * @date : 2023/05/04/ 16:41
 **/
@RestController
@RequestMapping("/user")
@Api()
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse login(@Valid @RequestBody LoginParam param, HttpServletRequest request){
        if(param==null){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        String userName = param.getUserAccount();
        String password = param.getPassword();
        if(password==null ||userName==null){
            return ResultUtil.error(ErrorCode.NULL_ERROR,"用户未注册!");
        }
        return userService.login(userName,password,request);
    }
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse register(@Valid @RequestBody RegisterParam param){
        if(param==null){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = param.getUserAccount();
        String password = param.getPassword();;
        String checkPassword = param.getCheckPassword();;
        return userService.register(userAccount,password,checkPassword);
    }

    @GetMapping("/{id}")
    @ApiOperation("通过用户id获取用户信息")
    public BaseResponse<UserVO> getUserById(@PathVariable("id") Long userId){
        if(userId==null || userId<0){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.getUserById(userId);
    }


    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    public BaseResponse<List<UserVO>> getUserList(
            @RequestParam(value = "pageSize",defaultValue = "5")int pageSize,
            @RequestParam(value = "current",defaultValue ="1")int current){
        return userService.getUserList(pageSize,current);
    }


    @DeleteMapping("/{id}")
    @ApiOperation("通过ID删除用户")
    @AuthCheck(mustRole =UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserById(@PathVariable("id") Long userId){
        if(userId==null || userId<0){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.deleteUserById(userId);
    }


    @PostMapping("/add")
    @ApiOperation("添加用户")
    @AuthCheck(mustRole =UserConstant.ADMIN_ROLE)
    public BaseResponse addUser(@RequestBody UserEntity userVO){
        if(userVO==null){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.addUser(userVO);
    }

    @PostMapping("/update")
    @ApiOperation("更新用户")
    public BaseResponse updateUserInfo(@RequestBody UserVO userVO){
        if(userVO==null){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        UserEntity userEntity = BeanUtil.copyProperties(userVO, UserEntity.class);
        return ResultUtil.success(userService.updateById(userEntity));
    }

    @GetMapping("/current")
    @ApiOperation("获取当前用户信息")
    public BaseResponse<UserVO> currentUser(){
        UserDTO user = UserHolder.getUser();
        UserEntity userEntity = userService.getById(user.getUserId());
        UserVO UserVO = BeanUtil.copyProperties(userEntity, UserVO.class);
        return ResultUtil.success(UserVO);
    }
}
