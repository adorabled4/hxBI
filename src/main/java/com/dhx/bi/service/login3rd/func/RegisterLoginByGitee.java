package com.dhx.bi.service.login3rd.func;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.model.DTO.user.VerifyCodeRegisterRequest;
import com.dhx.bi.service.UserService;
import com.dhx.bi.service.login3rd.factory.RegisterLoginComponentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * @author adorabled4
 * @className RegisterLoginByDefault
 * @date : 2023/11/15/ 11:58
 **/
@Component
public class RegisterLoginByGitee extends AbstractRegisterLoginFunc implements RegisterLoginFuncInterface {

    @Autowired
    private UserService userService;
    @Value("${gitee.state}")
    private String giteeState;
    @Value("${gitee.token.url}")
    private String giteeTokenUrl;
    @Value("${gitee.user.url}")
    private String giteeUserUrl;

    @PostConstruct
    private void initFuncMap() {
        RegisterLoginComponentFactory.funcMap.put("GITEE", this);
    }

    @Override
    public BaseResponse login3rd(HttpServletRequest request) {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        if (!giteeState.equals(state)) {
            throw new UnsupportedOperationException("state不匹配");
        }
        String tokenUrl = giteeTokenUrl.concat(code);
        String result = HttpUtil.post(tokenUrl, "");
        String accessToken = (String) JSONUtil.parseObj(result).get("access_token");
        // 请求用户信息
        String userUrl = giteeUserUrl.concat(accessToken);
        JSONObject userInfo = JSONUtil.parseObj(HttpUtil.get(userUrl));
        String email = (String) userInfo.get("email");
        return userService.quickLogin(email);
    }
}
