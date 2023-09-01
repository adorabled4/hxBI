package com.dhx.bi.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.annotation.AuthCheck;
import com.dhx.bi.common.constant.RedisConstant;
import com.dhx.bi.common.constant.UserConstant;
import com.dhx.bi.common.exception.BusinessException;
import com.dhx.bi.manager.OssManager;
import com.dhx.bi.model.DO.UserEntity;
import com.dhx.bi.model.DTO.FileUploadResult;
import com.dhx.bi.model.DTO.user.*;
import com.dhx.bi.model.VO.UserVO;
import com.dhx.bi.model.enums.MailEnum;
import com.dhx.bi.service.MessageService;
import com.dhx.bi.service.UserService;
import com.dhx.bi.utils.ResultUtil;
import com.dhx.bi.utils.ThrowUtils;
import com.dhx.bi.utils.UserHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    MessageService messageService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    OssManager ossManager;

    @PostMapping("/login/email")
    @ApiOperation("用户登录-email")
    public BaseResponse login(@Valid @RequestBody LoginEmailRequest param, HttpServletRequest request) {
        if (param == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        String email = param.getEmail();
        String password = param.getPassword();
        if (password == null || email == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR, "用户名或密码不能为空!");
        }
        return userService.login(email, password);
    }

    @PostMapping("/login/email/quick")
    @ApiOperation("验证码快速登录-email")
    public BaseResponse quickLogin(@Valid @RequestBody QuickLoginEmailRequest param) {
        if (param == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        String email = param.getEmail();
        String code = param.getCode();
        verifyCode(code, email);
        ;
        return userService.quickLogin(email);
    }

    @PostMapping("/register/email")
    @ApiOperation("用户注册-email")
    public BaseResponse registerByEmail(@Valid @RequestBody VerifyCodeRegisterRequest request) {
        if (request == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        String email = request.getEmail();
        String code = request.getCode();
        String codeKey = RedisConstant.VERIFY_CODE_KEY + email;
        String realCodeValue = stringRedisTemplate.opsForValue().get(codeKey);
        String[] split = realCodeValue.split("-");
        String verifyCode = split[1];
        if (!verifyCode.equals(code)) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR, "验证码错误!");
        }
        userService.register(request);
        return ResultUtil.success("注册成功!");
    }


    /**
     * 发送验证码
     *
     * @param email 电子邮件
     * @return {@link BaseResponse}<{@link String}>
     */
    @GetMapping("/send/code")
    public BaseResponse<String> sendVerifyCode(@RequestParam("email") String email) {
        // 校验
        if (!email.matches(UserConstant.EMAIL_REGEX)) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR, "邮箱格式非法!");
        }
        Long cnt = userService.query().eq("email", email).count();
        if (cnt != 0) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR, "该邮箱已被使用!");
        }
        // 发送验证码
        String code = RandomUtil.randomNumbers(6);
        String codeKey = RedisConstant.VERIFY_CODE_KEY + email;
        // 校验是否允许发送
        verifyCodeSend(email);
        // 发送
        messageService.sendCode(email, code, MailEnum.VERIFY_CODE);
        // 保存验证码的有效期
        long now = new Date().getTime() / 1000;
        String codeVal = now + "-" + code;
        // 存储
        stringRedisTemplate.opsForValue().set(codeKey, codeVal, RedisConstant.VERIFY_CODE_TTL, TimeUnit.SECONDS);
        return ResultUtil.success("发送成功!");
    }

    @GetMapping("/{id}")
    @ApiOperation("通过用户id获取用户信息")
    public BaseResponse<UserVO> getUserById(@PathVariable("id") Long userId) {
        if (userId == null || userId < 0) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.getUserById(userId);
    }


    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    public BaseResponse<List<UserVO>> getUserList(
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "current", defaultValue = "1") int current) {
        return userService.getUserList(pageSize, current);
    }


    @DeleteMapping("/{id}")
    @ApiOperation("通过ID删除用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserById(@PathVariable("id") Long userId) {
        if (userId == null || userId < 0) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.deleteUserById(userId);
    }


    @PostMapping("/add")
    @ApiOperation("添加用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse addUser(@RequestBody UserEntity userVO) {
        if (userVO == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        return userService.addUser(userVO);
    }

    @PostMapping("/update")
    @ApiOperation("更新用户")
    public BaseResponse updateUserInfo(@RequestPart("file") MultipartFile multipartFile, UserUpdateRequest param) {
        if (multipartFile != null) {
            // 执行更新用户图像操作
            FileUploadResult result = ossManager.uploadImage(multipartFile);
            ThrowUtils.throwIf(result.getStatus().equals("error"), ErrorCode.SYSTEM_ERROR, "上传头像失败!");
            String url = result.getName();
            param.setAvatarUrl(url);
        }
        if (param == null) {
            return ResultUtil.error(ErrorCode.PARAMS_ERROR);
        }
        UserEntity userEntity = BeanUtil.copyProperties(param, UserEntity.class);
        UserDTO user = UserHolder.getUser();
        userEntity.setUserId(user.getUserId());
        boolean b = userService.updateById(userEntity);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR, "更新用户信息失败!");
        return ResultUtil.success("更新成功!");
    }

    @GetMapping("/current")
    @ApiOperation("获取当前用户信息")
    public BaseResponse<UserVO> currentUser() {
        UserDTO user = UserHolder.getUser();
        UserEntity userEntity = userService.getById(user.getUserId());
        UserVO UserVO = BeanUtil.copyProperties(userEntity, UserVO.class);
        return ResultUtil.success(UserVO);
    }

    /**
     * 验证是否可以允许发送验证码
     *
     * @param email 电子邮件
     * @return boolean
     */
    boolean verifyCodeSend(String email) {
        String codeKey = RedisConstant.VERIFY_CODE_KEY + email;
        String oldCode = stringRedisTemplate.opsForValue().get(codeKey);
        // 判断是否之前发送过
        if (StringUtils.isNotBlank(oldCode)) {
            String[] split = oldCode.split("-");
            long time = Long.parseLong(split[0]);
            // 如果两次发送的间隔小于 60s => reject
            if (new Date().getTime() / 1000 - time < 60) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请稍后发送验证码!");
            }
        }
        return true;
    }

    private void verifyCode(String code, String email) {
        String codeKey = RedisConstant.VERIFY_CODE_KEY + email;
        String realCodeValue = stringRedisTemplate.opsForValue().get(codeKey);
        if (StringUtils.isBlank(realCodeValue)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请重新发送验证码!");
        }
        String[] split = realCodeValue.split("-");
        String verifyCode = split[1];
        if (!verifyCode.equals(code)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "验证码错误!");
        }
    }



}
