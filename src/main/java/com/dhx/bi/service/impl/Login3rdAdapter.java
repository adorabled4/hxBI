package com.dhx.bi.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.service.Login3rdTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    @Value("${github.state}")
    private String githubState;
    @Value("${github.token.url}")
    private String githubTokenUrl;
    @Value("${github.user.url}")
    private String githubUserUrl;

    @Override
    public BaseResponse loginByGitee(String state, String code) {
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
        return autoRegister3rdAndLogin(email);
    }

    @Override
    public BaseResponse loginByGithub(String state, String code) {
        if (!githubState.equals(state)) {
            throw new UnsupportedOperationException("state不匹配");
        }
        String tokenUrl = githubTokenUrl.concat(code);
        String result = HttpUtil.post(tokenUrl, "");
        Map<String, String> resultMap = splitGithubAccessToken(result);
        String accessToken = resultMap.get("access_token");
        log.info("github 返回值:{}", result);
        // 请求用户信息
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer ".concat(accessToken));
        String userInfo = HttpUtil.createGet(githubUserUrl).addHeaders(headerMap).execute(false).body();
        log.info("github 返回userInfo:{}", userInfo);
        String email = (String) JSONUtil.parseObj(userInfo).get("email");
        return autoRegister3rdAndLogin(email);
    }

    private BaseResponse autoRegister3rdAndLogin(String email) {
        // 邮箱快速登录(方法内已经包含了注册的逻辑)
        return super.quickLogin(email);
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
