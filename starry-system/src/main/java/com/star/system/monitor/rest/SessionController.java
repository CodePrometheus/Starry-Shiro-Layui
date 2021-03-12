package com.star.system.monitor.rest;

import com.star.system.framework.rest.BaseController;
import com.star.common.entity.AjaxResult;
import com.star.system.monitor.domain.ActiveUser;
import com.star.system.monitor.service.ISessionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @Author: zzStar
 * @Date: 03-07-2021 16:12
 */
@RestController
@RequestMapping("session")
@RequiredArgsConstructor
public class SessionController extends BaseController {

    private final ISessionService sessionService;

    @GetMapping("list")
    @RequiresPermissions("online:view")
    public AjaxResult list(String username) {
        List<ActiveUser> list = sessionService.list(username);
        return new AjaxResult().success()
                .data(getDataTable(list, CollectionUtils.size(list)));
    }

    @GetMapping("delete/{id}")
    @RequiresPermissions("user:kickout")
    public AjaxResult forceLogout(@PathVariable String id) {
        sessionService.forceLogout(id);
        return new AjaxResult().success();
    }
}
