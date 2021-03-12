package com.star.system.framework.rest;


import com.star.common.annotation.OperationLog;
import com.star.common.entity.AjaxResult;
import com.star.common.entity.QueryRequest;
import com.star.common.entity.Strings;
import com.star.common.exception.StarryException;
import com.star.system.framework.domain.Dept;
import com.star.system.framework.service.IDeptService;
import com.wuwenze.poi.ExcelKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @Author: zzStar
 * @Date: 03-05-2021 20:30
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("dept")
public class DeptController {

    private final IDeptService deptService;

    @GetMapping("select/tree")
    @OperationLog(exceptionMessage = "获取部门树失败")
    public AjaxResult getDeptTree() throws StarryException {
        return new AjaxResult().success().data(deptService.findDept());
    }

    @GetMapping("tree")
    @OperationLog(exceptionMessage = "获取部门树失败")
    public AjaxResult getDeptTree(Dept dept) throws StarryException {
        return new AjaxResult().success().data(deptService.findDept(dept));
    }

    @PostMapping
    @RequiresPermissions("dept:add")
    @OperationLog(operation = "新增部门", exceptionMessage = "新增部门失败")
    public AjaxResult addDept(@Valid Dept dept) {
        deptService.createDept(dept);
        return new AjaxResult().success();
    }

    @GetMapping("delete/{deptIds}")
    @RequiresPermissions("dept:delete")
    @OperationLog(operation = "删除部门", exceptionMessage = "删除部门失败")
    public AjaxResult deleteDept(@NotBlank(message = "{required}") @PathVariable String deptIds) throws StarryException {
        deptService.deleteDept(StringUtils.split(deptIds, Strings.COMMA));
        return new AjaxResult().success();
    }

    @PostMapping("update")
    @RequiresPermissions("dept:update")
    @OperationLog(operation = "修改部门", exceptionMessage = "修改部门失败")
    public AjaxResult updateDept(@Valid Dept dept) throws StarryException {
        deptService.updateDept(dept);
        return new AjaxResult().success();
    }

    @GetMapping("excel")
    @RequiresPermissions("dept:export")
    @OperationLog(exceptionMessage = "导出Excel失败")
    public void export(Dept dept, QueryRequest request, HttpServletResponse response) throws StarryException {
        ExcelKit.$Export(Dept.class, response)
                .downXlsx(deptService.findDept(dept, request), false);
    }
}
