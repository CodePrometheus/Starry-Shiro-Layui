package com.star.system.job.controller;

import com.star.common.annotation.OperationLog;
import com.star.system.framework.controller.BaseController;
import com.star.common.entity.AjaxResult;
import com.star.common.entity.QueryRequest;
import com.star.common.entity.Strings;
import com.star.system.job.entity.JobLog;
import com.star.system.job.service.IJobLogService;
import com.wuwenze.poi.ExcelKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

/**
 * @Author: zzStar
 * @Date: 03-08-2021 12:07
 */
@Slf4j
@Validated
@RestController
@RequestMapping("jobLog")
@RequiredArgsConstructor
public class JobLogController extends BaseController {

    private final IJobLogService jobLogService;

    @GetMapping
    @RequiresPermissions("job:log:view")
    public AjaxResult jobLogList(QueryRequest request, JobLog log) {
        return new AjaxResult().success()
                .data(getDataTable(jobLogService.findJobLogs(request, log)));
    }

    @GetMapping("delete/{jobIds}")
    @RequiresPermissions("job:log:delete")
    @OperationLog(exceptionMessage = "删除调度日志失败")
    public AjaxResult deleteJobLog(@NotBlank(message = "{required}") @PathVariable String jobIds) {
        jobLogService.deleteJobLogs(StringUtils.split(jobIds, Strings.COMMA));
        return new AjaxResult().success();
    }

    @GetMapping("excel")
    @RequiresPermissions("job:log:export")
    @OperationLog(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest request, JobLog jobLog, HttpServletResponse response) {
        ExcelKit.$Export(JobLog.class, response)
                .downXlsx(jobLogService.findJobLogs(request, jobLog).getRecords(), false);
    }
}
