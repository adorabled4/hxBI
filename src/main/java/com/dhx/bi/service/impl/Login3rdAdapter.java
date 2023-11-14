package com.dhx.bi.service.impl;

import cn.hutool.json.JSONObject;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.service.Login3rdTarget;
import com.dhx.bi.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author adorabled4
 * @className Login3rdAdapter
 * @date : 2023/11/08/ 21:00
 **/
@Slf4j
@Component
public class Login3rdAdapter extends UserServiceImpl implements Login3rdTarget {

    @Value("${gitee.state}")
    private String giteeState;
    @Value("${gitee.token.url}")
    private String giteeTokenUrl;
    @Value("${gitee.user.url}")
    private String giteeUserUrl;

    @Override
    public BaseResponse loginByGitee(String code) {
//        if (!giteeState.equals(state)) {
//            throw new UnsupportedOperationException("state不匹配");
//        }
        String tokenUrl = giteeTokenUrl.concat(code);
        JSONObject accessToken = HttpUtil.execute(tokenUrl, HttpMethod.POST);
        log.info("gitee 返回值:{}", accessToken);
        // 请求用户信息
        String userUrl = giteeUserUrl.concat((String) accessToken.get("access_token"));
        JSONObject userInfo = HttpUtil.execute(userUrl, HttpMethod.POST);
        log.info("gitee 返回userInfo:{}", userInfo);
        String email = (String) userInfo.get("email");
        return autoRegister3rdAndLogin(email);
    }

    private BaseResponse autoRegister3rdAndLogin(String email) {
        // 邮箱快速登录(方法内已经包含了注册的逻辑)
        return super.quickLogin(email);
    }
}
