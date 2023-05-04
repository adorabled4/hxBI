package com.dhx.template.aop;

import com.dhx.template.common.annotation.AuthCheck;
import com.dhx.template.common.ErrorCode;
import com.dhx.template.common.constant.UserConstant;
import com.dhx.template.model.DTO.UserDTO;
import com.dhx.template.utils.ResultUtil;
import com.dhx.template.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author adorabled4
 * @className AuthCheckAOP
 * @date : 2023/05/04/ 16:26
 **/
@Aspect
@Component
@Slf4j
public class AuthCheckAOP {

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.dhx.template.common.annotation.AuthCheck)")
    public void logPointcut() {
    }

    @Around(value = "logPointcut()")
    public Object run(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();//方法签名
        Method method = ((MethodSignature) signature).getMethod();
        //这个方法才是目标对象上有注解的方法
        Method realMethod = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
        //获取注解
        AuthCheck authCheck = realMethod.getAnnotation(AuthCheck.class);
        String mustRole = authCheck.mustRole();
        String[] anyRole = authCheck.anyRole();
        UserDTO user = UserHolder.getUser();
        Integer userRole = user.getUserRole();
        if (userRole.equals(1) || anyRole.length == 0 && mustRole.equals("")) {
            return joinPoint.proceed();
        } else {
            // 需要检测用户的身份
            // 1. 需要管理员身份
            if (StringUtils.isNotBlank(mustRole) && mustRole.equals(String.valueOf(UserConstant.ADMIN_ROLE))) {
                return ResultUtil.error(ErrorCode.NO_AUTH);
            } else if (anyRole.length!=0){
                // 2. 需要用户登录
                 for (String role : anyRole) {
                    if(role.equals(String.valueOf(user.getUserRole()))){
                        return joinPoint.proceed();
                    }
                }
            }
            return ResultUtil.error(ErrorCode.NO_AUTH);
        }
    }


}
