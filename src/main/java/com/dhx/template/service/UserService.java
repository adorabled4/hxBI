package com.dhx.template.service;

import com.dhx.template.common.BaseResponse;
import com.dhx.template.model.DO.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dhx.template.model.VO.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author dhx
* @description 针对表【t_user】的数据库操作Service
* @createDate 2023-05-04 16:18:15
*/
public interface UserService extends IService<UserEntity> {

    BaseResponse<Boolean> deleteUserById(Long userId);

    BaseResponse<List<UserVO>> getUserList(int pageSize, int current);

    BaseResponse<UserVO> getUserById(Long userId);

    BaseResponse addUser(UserEntity userVo);

    /**
     * 用户注册
     * @param userAccount 用户账户名
     * @param password
     * @param checkPassword
     * @return
     */
    BaseResponse register(String userAccount, String password, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 账户
     * @param password 密码
     * @return 返回token
     */
    BaseResponse login(String userAccount, String password, HttpServletRequest request);
}