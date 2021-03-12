package com.star.system.monitor.rest;

import com.star.common.annotation.OperationLog;
import com.star.system.framework.rest.BaseController;
import com.star.common.entity.AjaxResult;
import com.star.common.entity.QueryRequest;
import com.star.common.entity.Strings;
import com.star.system.monitor.domain.SystemLog;
import com.star.system.monitor.service.OperationLogService;
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
 * @Date: 03-06-2021 17:37
 */
@Slf4j
@RestController
@RequestMapping("log")
@RequiredArgsConstructor
public class OperationLogController extends BaseController {

    private final OperationLogService logService;

    @GetMapping("list")
    @RequiresPermissions("log:view")
    public AjaxResult logList(SystemLog log, QueryRequest request) {
        return new AjaxResult().success()
                .data(getDataTable(logService.findLogs(log, request)));
    }

    @GetMapping("delete/{ids}")
    @RequiresPermissions("log:delete")
    @OperationLog(exceptionMessage = "删除日志失败")
    public AjaxResult deleteLogs(@NotBlank(message = "{required}") @PathVariable String ids) {
        logService.deleteLogs(StringUtils.split(ids, Strings.COMMA));
        return new AjaxResult().success();
    }

    @GetMapping("excel")
    @RequiresPermissions("log:export")
    @OperationLog(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest request, SystemLog lg, HttpServletResponse response) {
        ExcelKit.$Export(SystemLog.class, response)
                .downXlsx(logService.findLogs(lg, request).getRecords(), false);
    }
}
