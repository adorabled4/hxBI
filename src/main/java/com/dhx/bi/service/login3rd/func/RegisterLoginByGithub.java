package com.dhx.bi.service.login3rd.func;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.service.UserService;
import com.dhx.bi.service.login3rd.factory.RegisterLoginComponentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author adorabled4
 * @className RegisterLoginByDefault
 * @date : 2023/11/15/ 11:58
 **/
@Component
public class RegisterLoginByGithub extends AbstractRegisterLoginFunc implements RegisterLoginFuncInterface {
    @Autowired
    private UserService userService;
    @PostConstruct
    private void initFuncMap() {
        RegisterLoginComponentFactory.funcMap.put("GITHUB", this);
    }
    @Value("${github.state}")
    private String githubState;
    @Value("${github.token.url}")
    private String githubTokenUrl;
    @Value("${github.user.url}")
    private String githubUserUrl;
    @Override
    public BaseResponse login3rd(HttpServletRequest request) {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        if (!githubState.equals(state)) {
            throw new UnsupportedOperationException("state不匹配");
        }
        String tokenUrl = githubTokenUrl.concat(code);
        String result = HttpUtil.post(tokenUrl, "");
        Map<String, String> resultMap = splitGithubAccessToken(result);
        String accessToken = resultMap.get("access_token");
        // 请求用户信息
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer ".concat(accessToken));
        String userInfo = HttpUtil.createGet(githubUserUrl).addHeaders(headerMap).execute(false).body();
        String email = (String) JSONUtil.parseObj(userInfo).get("email");
        return userService.quickLogin(email);
    }
    private Map<String, String> splitGithubAccessToken(String data) {
        Map<String, String> result = new HashMap<>();
        Arrays.stream(data.split("&"))
                .forEach(entry -> {
                    String[] keyValue = entry.split("=");
                    if (keyValue.length == 2) {
                        result.put(keyValue[0], keyValue[1]);
                    }
                });
        System.out.println(result);
        return result;
    }
}
