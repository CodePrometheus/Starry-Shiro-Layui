package com.star.system.framework.controller;

import com.star.common.annotation.OperationLog;
import com.star.common.entity.AjaxResult;
import com.star.common.entity.QueryRequest;
import com.star.common.entity.Strings;
import com.star.common.exception.StarryException;
import com.star.common.utils.Md5Util;
import com.star.system.framework.entity.User;
import com.star.system.framework.service.IUserService;
import com.wuwenze.poi.ExcelKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @Author: zzStar
 * @Date: 03-05-2021 20:31
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController extends BaseController {

    private final IUserService userService;

    @GetMapping("{username}")
    public User getUser(@NotBlank(message = "{required}") @PathVariable String username) {
        return userService.findUserDetailList(username);
    }

    @GetMapping("check/{username}")
    public boolean checkUserName(@NotBlank(message = "{required}") @PathVariable String username, String userId) {
        return userService.findByName(username) == null || StringUtils.isNotBlank(userId);
    }

    @GetMapping("list")
    @RequiresPermissions("user:view")
    public AjaxResult userList(User user, QueryRequest request) {
        return new AjaxResult().success()
                .data(getDataTable(userService.findUserDetailList(user, request)));
    }

    @PostMapping
    @RequiresPermissions("user:add")
    @OperationLog(operation = "新增用户", exceptionMessage = "新增用户失败")
    public AjaxResult addUser(@Valid User user) {
        userService.createUser(user);
        return new AjaxResult().success();
    }

    @GetMapping("delete/{userIds}")
    @RequiresPermissions("user:delete")
    @OperationLog(operation = "删除用户", exceptionMessage = "删除用户失败")
    public AjaxResult deleteUsers(@NotBlank(message = "{required}") @PathVariable String userIds) {
        userService.deleteUsers(StringUtils.split(userIds, Strings.COMMA));
        return new AjaxResult().success();
    }

    @PostMapping("update")
    @RequiresPermissions("user:update")
    @OperationLog(operation = "修改用户", exceptionMessage = "修改用户失败")
    public AjaxResult updateUser(@Valid User user) {
        if (user.getUserId() == null) {
            throw new StarryException("用户ID为空");
        }
        userService.updateUser(user);
        return new AjaxResult().success();
    }

    @PostMapping("password/reset/{usernames}")
    @RequiresPermissions("user:password:reset")
    @OperationLog(exceptionMessage = "重置用户密码失败")
    public AjaxResult resetPassword(@NotBlank(message = "{required}") @PathVariable String usernames) {
        userService.resetPassword(StringUtils.split(usernames, Strings.COMMA));
        return new AjaxResult().success();
    }

    @PostMapping("password/update")
    @OperationLog(exceptionMessage = "修改密码失败")
    public AjaxResult updatePassword(
            @NotBlank(message = "{required}") String oldPassword,
            @NotBlank(message = "{required}") String newPassword) {
        User user = getCurrentUser();
        if (!StringUtils.equals(user.getPassword(), Md5Util.encrypt(user.getUsername(), oldPassword))) {
            throw new StarryException("原密码不正确");
        }
        userService.updatePassword(user.getUsername(), newPassword);
        return new AjaxResult().success();
    }

    @GetMapping("avatar/{image}")
    @OperationLog(exceptionMessage = "修改头像失败")
    public AjaxResult updateAvatar(@NotBlank(message = "{required}") @PathVariable String image) {
        userService.updateAvatar(getCurrentUser().getUsername(), image);
        return new AjaxResult().success();
    }

    @PostMapping("theme/update")
    @OperationLog(exceptionMessage = "修改系统配置失败")
    public AjaxResult updateTheme(String theme, String isTab) {
        userService.updateTheme(getCurrentUser().getUsername(), theme, isTab);
        return new AjaxResult().success();
    }

    @PostMapping("profile/update")
    @OperationLog(exceptionMessage = "修改个人信息失败")
    public AjaxResult updateProfile(User user) throws StarryException {
        user.setUserId(getCurrentUser().getUserId());
        userService.updateProfile(user);
        return new AjaxResult().success();
    }

    @GetMapping("excel")
    @RequiresPermissions("user:export")
    @OperationLog(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest queryRequest, User user, HttpServletResponse response) {
        ExcelKit.$Export(User.class, response)
                .downXlsx(userService.findUserDetailList(user, queryRequest).getRecords(), false);
    }
}
