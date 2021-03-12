package com.star.system.monitor.controller;

import com.star.common.annotation.OperationLog;
import com.star.system.framework.controller.BaseController;
import com.star.common.entity.AjaxResult;
import com.star.common.entity.QueryRequest;
import com.star.common.entity.Strings;
import com.star.system.monitor.entity.LoginLog;
import com.star.system.monitor.service.ILoginLogService;
import com.wuwenze.poi.ExcelKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

/**
 * @Author: zzStar
 * @Date: 03-06-2021 15:56
 */
@Slf4j
@RestController
@RequestMapping("loginLog")
@RequiredArgsConstructor
public class LoginLogController extends BaseController {

    private final ILoginLogService loginLogService;

    @GetMapping("list")
    @RequiresPermissions("loginlog:view")
    public AjaxResult loginLogList(LoginLog loginLog, QueryRequest request) {
        return new AjaxResult().success()
                .data(getDataTable(loginLogService.findLoginLogs(loginLog, request)));
    }

    @GetMapping("delete/{ids}")
    @RequiresPermissions("loginlog:delete")
    @OperationLog(exceptionMessage = "删除日志失败")
    public AjaxResult deleteLogs(@NotBlank(message = "{required}") @PathVariable String ids) {
        loginLogService.deleteLoginLogs(StringUtils.split(ids, Strings.COMMA));
        return new AjaxResult().success();
    }

    @GetMapping("excel")
    @RequiresPermissions("loginlog:export")
    @OperationLog(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest request, LoginLog loginLog, HttpServletResponse response) {
        ExcelKit.$Export(LoginLog.class, response)
                .downXlsx(loginLogService.findLoginLogs(loginLog, request).getRecords(), false);
    }
}
