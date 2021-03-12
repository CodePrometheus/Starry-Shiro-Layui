package com.star.system.framework.rest;


import com.star.common.annotation.OperationLog;
import com.star.common.entity.AjaxResult;
import com.star.common.entity.QueryRequest;
import com.star.common.exception.StarryException;
import com.star.system.framework.domain.Role;
import com.star.system.framework.service.IRoleService;
import com.wuwenze.poi.ExcelKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @Author: zzStar
 * @Date: 03-05-2021 20:31
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("role")
public class RoleController extends BaseController {

    private final IRoleService roleService;

    @GetMapping
    public AjaxResult getAllRoles(Role role) {
        return new AjaxResult().success().data(roleService.findRoles(role));
    }

    @GetMapping("list")
    @RequiresPermissions("role:view")
    public AjaxResult roleList(Role role, QueryRequest request) {
        return new AjaxResult().success()
                .data(getDataTable(roleService.findRoles(role, request)));
    }

    @PostMapping
    @RequiresPermissions("role:add")
    @OperationLog(operation = "新增角色", exceptionMessage = "新增角色失败")
    public AjaxResult addRole(@Valid Role role) {
        roleService.createRole(role);
        return new AjaxResult().success();
    }

    @GetMapping("delete/{roleIds}")
    @RequiresPermissions("role:delete")
    @OperationLog(operation = "删除角色", exceptionMessage = "删除角色失败")
    public AjaxResult deleteRoles(@NotBlank(message = "{required}") @PathVariable String roleIds) {
        roleService.deleteRoles(roleIds);
        return new AjaxResult().success();
    }

    @PostMapping("update")
    @RequiresPermissions("role:update")
    @OperationLog(operation = "修改角色", exceptionMessage = "修改角色失败")
    public AjaxResult updateRole(Role role) {
        roleService.updateRole(role);
        return new AjaxResult().success();
    }

    @GetMapping("excel")
    @RequiresPermissions("role:export")
    @OperationLog(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest queryRequest, Role role, HttpServletResponse response) throws StarryException {
        ExcelKit.$Export(Role.class, response)
                .downXlsx(roleService.findRoles(role, queryRequest).getRecords(), false);
    }

}
