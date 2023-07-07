package com.dhx.bi.service;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.model.DO.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dhx.bi.model.VO.UserVO;

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

    /**
     * 获取当前登录用户
     * @return
     */
    UserEntity getLoginUser();

    /**
     * 是否是admin
     *
     * @param request 请求
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);


    /**
     * 是管理
     *
     * @param user 用户
     * @return boolean
     */
    boolean isAdmin(UserEntity user);
}