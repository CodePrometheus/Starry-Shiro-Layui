package com.star.system.framework.rest;

import com.star.common.entity.StarryConstant;
import com.star.common.utils.DateUtil;
import com.star.system.security.authentication.StarryUtil;
import com.star.system.framework.domain.User;
import com.star.system.framework.service.IUserDataPermissionService;
import com.star.system.framework.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.ExpiredSessionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 视图控制器
 *
 * @Author: zzStar
 * @Date: 03-05-2021 20:32
 */
@Controller("systemView")
@RequiredArgsConstructor
public class ViewController extends BaseController {

    private final IUserService userService;
    private final IUserDataPermissionService userDataPermissionService;

    @GetMapping("login")
    @ResponseBody
    public Object login(HttpServletRequest request) {
        if (StarryUtil.isAjaxRequest(request)) {
            throw new ExpiredSessionException();
        } else {
            ModelAndView mav = new ModelAndView();
            mav.setViewName(StarryUtil.view("login"));
            return mav;
        }
    }

    @GetMapping("unauthorized")
    public String unauthorized() {
        return StarryUtil.view("error/403");
    }


    @GetMapping("/")
    public String redirectIndex() {
        return "redirect:/index";
    }

    @GetMapping("index")
    public String index(Model model) {
        User principal = userService.findByName(getCurrentUser().getUsername());
        userService.doGetUserAuthorizationInfo(principal);
        principal.setPassword("It's a secret");
        model.addAttribute("user", principal);
        model.addAttribute("permissions", principal.getStringPermissions());
        model.addAttribute("roles", principal.getRoles());
        return "index";
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "layout")
    public String layout() {
        return StarryUtil.view("layout");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "password/update")
    public String passwordUpdate() {
        return StarryUtil.view("system/user/passwordUpdate");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "user/profile")
    public String userProfile() {
        return StarryUtil.view("system/user/userProfile");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "user/avatar")
    public String userAvatar() {
        return StarryUtil.view("system/user/avatar");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "user/profile/update")
    public String profileUpdate() {
        return StarryUtil.view("system/user/profileUpdate");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "system/user")
    @RequiresPermissions("user:view")
    public String systemUser() {
        return StarryUtil.view("system/user/user");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "system/user/add")
    @RequiresPermissions("user:add")
    public String systemUserAdd() {
        return StarryUtil.view("system/user/userAdd");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "system/user/detail/{username}")
    @RequiresPermissions("user:view")
    public String systemUserDetail(@PathVariable String username, Model model) {
        resolveUserModel(username, model, true);
        return StarryUtil.view("system/user/userDetail");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "system/user/update/{username}")
    @RequiresPermissions("user:update")
    public String systemUserUpdate(@PathVariable String username, Model model) {
        resolveUserModel(username, model, false);
        return StarryUtil.view("system/user/userUpdate");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "system/role")
    @RequiresPermissions("role:view")
    public String systemRole() {
        return StarryUtil.view("system/role/role");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "system/menu")
    @RequiresPermissions("menu:view")
    public String systemMenu() {
        return StarryUtil.view("system/menu/menu");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "system/dept")
    @RequiresPermissions("dept:view")
    public String systemDept() {
        return StarryUtil.view("system/dept/dept");
    }

    @RequestMapping(StarryConstant.VIEW_PREFIX + "index")
    public String pageIndex() {
        return StarryUtil.view("index");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "404")
    public String error404() {
        return StarryUtil.view("error/404");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "403")
    public String error403() {
        return StarryUtil.view("error/403");
    }

    @GetMapping(StarryConstant.VIEW_PREFIX + "500")
    public String error500() {
        return StarryUtil.view("error/500");
    }

    private void resolveUserModel(String username, Model model, Boolean transform) {
        User user = userService.findByName(username);
        String deptIds = userDataPermissionService.findByUserId(String.valueOf(user.getUserId()));
        user.setDeptIds(deptIds);
        model.addAttribute("user", user);
        if (transform) {
            String sex = user.getSex();
            switch (sex) {
                case User.SEX_MALE:
                    user.setSex("男");
                    break;
                case User.SEX_FEMALE:
                    user.setSex("女");
                    break;
                default:
                    user.setSex("保密");
                    break;
            }
        }
        if (user.getLastLoginTime() != null) {
            model.addAttribute("lastLoginTime", DateUtil.getDateFormat(user.getLastLoginTime(), DateUtil.FULL_TIME_SPLIT_PATTERN));
        }
    }
}
