package com.star.system.job.utils;

import com.star.common.entity.StarryConstant;
import com.star.common.utils.SpringContextUtil;
import com.star.system.job.entity.Job;
import com.star.system.job.entity.JobLog;
import com.star.system.job.service.IJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.concurrent.Future;

/**
 * @Description: 定时任务
 * @Author: zzStar
 * @Date: 03-07-2021 17:58
 */
@Slf4j
public class ScheduleJob extends QuartzJobBean {

    /**
     * 线程池
     */
    private final ThreadPoolTaskExecutor scheduleJobExecutorService = SpringContextUtil.getBean(StarryConstant.Starry_SHIRO_THREAD_POOL, ThreadPoolTaskExecutor.class);

    /**
     * 每次启动定时任务 ，需要执行的方法
     * 当Scheduler调用一个Job，就会将JobExecutionContext传递给job的execute方法
     *
     * @param context
     * @throws JobExecutionContext
     */
    @Override
    protected void executeInternal(JobExecutionContext context) {
        // 执行的job
        Job scheduleJob = (Job) context.getMergedJobDataMap().get(Job.JOB_PARAM_KEY);

        // 获取spring bean
        IJobLogService scheduleJobLogService = SpringContextUtil.getBean(IJobLogService.class);

        JobLog jobLog = new JobLog();
        jobLog.setJobId(scheduleJob.getJobId());
        jobLog.setBeanName(scheduleJob.getBeanName());
        jobLog.setMethodName(scheduleJob.getMethodName());
        jobLog.setParams(scheduleJob.getParams());
        jobLog.setCreateTime(new Date());

        long startTime = System.currentTimeMillis();

        try {
            // 开始执行
            log.info("任务准备执行，任务ID：{}", scheduleJob.getJobId());
            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBeanName(), scheduleJob.getMethodName(),
                    scheduleJob.getParams());
            // 提交任务
            Future<?> future = scheduleJobExecutorService.submit(task);
            // 等待计算结果 阻塞
            future.get();
            long times = System.currentTimeMillis() - startTime;
            jobLog.setTimes(times);
            // 任务状态 0：成功 1：失败
            jobLog.setStatus(JobLog.JOB_SUCCESS);

            log.info("任务执行完毕，任务ID：{} 总共耗时：{} 毫秒", scheduleJob.getJobId(), times);
        } catch (Exception e) {
            log.error("任务执行失败，任务ID：" + scheduleJob.getJobId(), e);
            long times = System.currentTimeMillis() - startTime;
            jobLog.setTimes(times);
            // 任务状态 0：成功 1：失败
            jobLog.setStatus(JobLog.JOB_FAIL);
            jobLog.setError(StringUtils.substring(e.toString(), 0, 2000));
        } finally {
            scheduleJobLogService.saveJobLog(jobLog);
        }
    }
}
