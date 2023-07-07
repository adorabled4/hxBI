package com.dhx.bi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.constant.UserConstant;
import com.dhx.bi.model.DO.UserEntity;
import com.dhx.bi.model.DTO.JwtToken;
import com.dhx.bi.model.DTO.user.UserDTO;
import com.dhx.bi.model.VO.UserVO;
import com.dhx.bi.service.JwtTokensService;
import com.dhx.bi.service.UserService;
import com.dhx.bi.mapper.UserMapper;
import com.dhx.bi.utils.ResultUtil;
import com.dhx.bi.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author dhx
* @description 针对表【t_user】的数据库操作Service实现
* @createDate 2023-05-04 16:18:15
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity>
    implements UserService{

    @Resource
    UserMapper userMapper;

    @Resource
    JwtTokensService jwtTokensService;

    @Override
    public BaseResponse login(String userAccount, String password, HttpServletRequest request) {
        //1. 获取的加密密码
        UserEntity user = query().eq("user_account",userAccount).one();
        String handlerPassword=user.getUserPassword();

        //2. 查询用户密码是否正确
        boolean checkpw = BCrypt.checkpw(password, handlerPassword);
        if(!checkpw){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR,"账户名或密码错误!");
        }
        //3. 获取jwt的token并将token写入Redis
        String token = jwtTokensService.generateAccessToken(user);
        String refreshToken = jwtTokensService.generateRefreshToken(user);
        JwtToken jwtToken = new JwtToken(token,refreshToken);
        jwtTokensService.save2Redis(jwtToken,user);
//        //4. 保存用户的登录IPV4地址
//        try{
//            String remoteAddr = request.getRemoteAddr();
//            if(StringUtils.isNotBlank(remoteAddr)){
//                user.setLastLoginIp(remoteAddr);
//                save(user);
//            }
//        }catch(RuntimeException e){
//            log.error("保存用户登录IP失败, remoteAddress:{}", request.getRemoteAddr());
//        }
        // 返回jwtToken
        return ResultUtil.success(token);
    }

    @Override
    public BaseResponse register(String userAccount, String password, String checkPassword) {
        if(!password.equals(checkPassword)){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致!");
        }
        Long cnt = query().eq("user_account", userAccount).count();
        if(cnt!=0){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR,"账户名已被注册!");
        }
        UserEntity user = new UserEntity();
        String handlerPassword = BCrypt.hashpw(password);
        user.setUserAccount(userAccount);
        user.setUserName("user-"+ UUID.randomUUID().toString().substring(0,10));
        user.setUserPassword(handlerPassword);
        boolean save = save(user);
        if(!save){
            return ResultUtil.error(ErrorCode.SYSTEM_ERROR,"注册失败");
        }
        return ResultUtil.success(user.getUserId());
    }

    @Override
    public BaseResponse<UserVO> getUserById(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        System.out.println(user);
        if(user==null){
            return ResultUtil.error(ErrorCode.PARAMS_ERROR,"用户不存在!");
        }
        // 转换成vo 对象
        UserVO UserVO = BeanUtil.copyProperties(user, UserVO.class);
        return ResultUtil.success(UserVO);
    }

    @Override
    public BaseResponse<Boolean> deleteUserById(Long userId) {
        boolean result = remove(new QueryWrapper<UserEntity>().eq("user_id", userId));
        return ResultUtil.success(result);
    }

    @Override
    public BaseResponse<List<UserVO>> getUserList(int pageSize, int current) {
        // 分页查询数据
        List<UserEntity> records = query().page(new Page<>(current,pageSize)).getRecords();
        // 转换为UserVO
        List<UserVO> UserVOList = records.stream().map(item -> BeanUtil.copyProperties(item, UserVO.class)).collect(Collectors.toList());
        return ResultUtil.success(UserVOList);
    }


    @Override
    public BaseResponse addUser(UserEntity user) {
        String password=user.getUserPassword();
        String handlerPassword = BCrypt.hashpw(password);
        user.setUserPassword(handlerPassword);
        save(user);
        return ResultUtil.success(user.getUserId());
    }

    @Override
    public UserEntity getLoginUser() {
        UserDTO user = UserHolder.getUser();
        Long userId = user.getUserId();
        return getById(userId);
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        UserDTO user = UserHolder.getUser();
        if(user.getUserRole()==null){
            return false;
        }
        return user.getUserRole().equals(UserConstant.ADMIN_ROLE) ;
    }

    @Override
    public boolean isAdmin(UserEntity user) {
        if(user.getUserRole()==null){
            return false;
        }
        return user.getUserRole().equals(UserConstant.ADMIN_ROLE) ;
    }
}




