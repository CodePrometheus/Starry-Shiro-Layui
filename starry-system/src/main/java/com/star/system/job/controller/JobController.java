package com.star.system.job.controller;

import com.star.common.annotation.OperationLog;
import com.star.system.framework.controller.BaseController;
import com.star.common.entity.AjaxResult;
import com.star.common.entity.QueryRequest;
import com.star.common.entity.Strings;
import com.star.system.job.entity.Job;
import com.star.system.job.service.IJobService;
import com.wuwenze.poi.ExcelKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.CronExpression;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @Author: zzStar
 * @Date: 03-08-2021 12:07
 */
@Slf4j
@Validated
@RestController
@RequestMapping("job")
@RequiredArgsConstructor
public class JobController extends BaseController {

    private final IJobService jobService;

    @GetMapping
    @RequiresPermissions("job:view")
    public AjaxResult jobList(QueryRequest request, Job job) {
        return new AjaxResult().success()
                .data(getDataTable(jobService.findJobs(request, job)));
    }

    @GetMapping("cron/check")
    public boolean checkCron(String cron) {
        try {
            return CronExpression.isValidExpression(cron);
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping
    @RequiresPermissions("job:add")
    @OperationLog(operation = "新增定时任务", exceptionMessage = "新增定时任务失败")
    public AjaxResult addJob(@Valid Job job) {
        jobService.createJob(job);
        return new AjaxResult().success();
    }

    @GetMapping("delete/{jobIds}")
    @RequiresPermissions("job:delete")
    @OperationLog(operation = "删除定时任务", exceptionMessage = "删除定时任务失败")
    public AjaxResult deleteJob(@NotBlank(message = "{required}") @PathVariable String jobIds) {
        jobService.deleteJobs(StringUtils.split(jobIds, Strings.COMMA));
        return new AjaxResult().success();
    }

    @PostMapping("update")
    @OperationLog(operation = "修改定时任务", exceptionMessage = "修改定时任务失败")
    public AjaxResult updateJob(@Valid Job job) {
        jobService.updateJob(job);
        return new AjaxResult().success();
    }

    @GetMapping("run/{jobIds}")
    @RequiresPermissions("job:run")
    @OperationLog(operation = "执行定时任务", exceptionMessage = "执行定时任务失败")
    public AjaxResult runJob(@NotBlank(message = "{required}") @PathVariable String jobIds) {
        jobService.run(jobIds);
        return new AjaxResult().success();
    }

    @GetMapping("pause/{jobIds}")
    @RequiresPermissions("job:pause")
    @OperationLog(operation = "暂停定时任务", exceptionMessage = "暂停定时任务失败")
    public AjaxResult pauseJob(@NotBlank(message = "{required}") @PathVariable String jobIds) {
        jobService.pause(jobIds);
        return new AjaxResult().success();
    }

    @GetMapping("resume/{jobIds}")
    @RequiresPermissions("job:resume")
    @OperationLog(operation = "恢复定时任务", exceptionMessage = "恢复定时任务失败")
    public AjaxResult resumeJob(@NotBlank(message = "{required}") @PathVariable String jobIds) {
        jobService.resume(jobIds);
        return new AjaxResult().success();
    }

    @GetMapping("excel")
    @RequiresPermissions("job:export")
    @OperationLog(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest request, Job job, HttpServletResponse response) {
        ExcelKit.$Export(Job.class, response)
                .downXlsx(jobService.findJobs(request, job).getRecords(), false);
    }
}
