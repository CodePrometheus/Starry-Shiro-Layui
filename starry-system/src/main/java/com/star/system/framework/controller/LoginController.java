package com.star.system.framework.controller;

import com.star.common.annotation.Limit;
import com.star.common.entity.AjaxResult;
import com.star.common.exception.StarryException;
import com.star.system.utils.StarryProperties;
import com.star.system.utils.ValidateCodeService;
import com.star.common.utils.Md5Util;
import com.star.system.framework.entity.User;
import com.star.system.framework.service.IUserService;
import com.star.system.monitor.service.ILoginLogService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Map;


/**
 * @Author: zzStar
 * @Date: 03-05-2021 20:32
 */
@Validated
@RestController
@RequiredArgsConstructor
public class LoginController extends BaseController {

    private final IUserService userService;
    private final ValidateCodeService validateCodeService;
    private final ILoginLogService loginLogService;
    private final StarryProperties properties;

    @PostMapping("login")
    @Limit(key = "login", period = 60, count = 10, name = "登录接口", prefix = "limit")
    public AjaxResult login(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password,
            @NotBlank(message = "{required}") String verifyCode,
            boolean rememberMe, HttpServletRequest request) {
        validateCodeService.check(request.getSession().getId(), verifyCode);
        UsernamePasswordToken token = new UsernamePasswordToken(username,
                Md5Util.encrypt(username.toLowerCase(), password), rememberMe);
        super.login(token);
        // 保存登录日志
        loginLogService.saveLoginLog(username);
        return new AjaxResult().success().data(properties.getShiro().getSuccessUrl());
    }

    @PostMapping("register")
    public AjaxResult register(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) throws StarryException {
        User user = userService.findByName(username);
        if (user != null) {
            throw new StarryException("该用户名已存在");
        }
        userService.register(username, password);
        return new AjaxResult().success();
    }

    @GetMapping("index/{username}")
    public AjaxResult index(@NotBlank(message = "{required}") @PathVariable String username) {
        // 更新登录时间
        userService.updateLoginTime(username);
        // 获取首页数据
        Map<String, Object> data = loginLogService.retrieveIndexPageData(username);
        return new AjaxResult().success().data(data);
    }

    @GetMapping("images/captcha")
    @Limit(key = "get_captcha", period = 60, count = 10, name = "获取验证码", prefix = "limit")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException, StarryException {
        validateCodeService.create(request, response);
    }
}
