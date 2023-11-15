package com.dhx.bi.controller;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.service.login3rd.AbstractRegisterLoginComponent;
import com.dhx.bi.service.login3rd.factory.RegisterLoginComponentFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author adorabled4
 * @className LoginController
 * @date : 2023/11/08/ 21:04
 **/
@RestController
@RequestMapping(value = "/login3rd")
@Slf4j
public class LoginController {

    @GetMapping("/gitee/callback")
    public BaseResponse loginByGitee(HttpServletRequest httpServletRequest) {
        AbstractRegisterLoginComponent gitee = RegisterLoginComponentFactory.getComponent("GITEE");
        return gitee.login3rd(httpServletRequest);
    }

    @GetMapping("/github/callback")
    public BaseResponse loginByGithub(HttpServletRequest httpServletRequest) {
        AbstractRegisterLoginComponent github = RegisterLoginComponentFactory.getComponent("GITHUB");
        return github.login3rd(httpServletRequest);
    }
}
