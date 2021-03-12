package com.star.system.job.utils;

import com.star.system.job.entity.Job;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * @Description: 定时任务工具类
 * @Author: zzStar
 * @Date: 03-08-2021 13:09
 */
@Slf4j
public abstract class ScheduleUtil {

    /**
     * cron表达式的格式为：秒 分 时 日 周 年；其中年是可选的，其它为必填。
     * 每个属性允许的值：
     * <p>
     * 秒， 0-59；
     * 分，0-59；
     * 时，0-23；
     * 日，1-31；
     * 周 ，1-7；
     * 年，可选，1970-2099
     * 下面是cron表达式使用的一些符号：
     * <p>
     * “/”：指定增加值；比如上面的”0/5”，意思就是从第0秒开始，然后每隔5秒执行一次。
     * “*”：表示所有值；比如”5 * ？ * *”，意思就是每一分钟的第5秒执行一次。
     * “?”：没有指定具体值，表示某月的某一天或者每周的某一天；只能在”日”和”周”上使用”?”，而且必须使用”?”；比如”5 * * ？ * “或”5 * * * ?”都是正确的且效果一样，而”5 * * * * *”则是错误的。
     * “,”：表示多选；比如”1,2,3,5 * * ? * *”，意思就是每分钟的第1、2、3、5秒执行一次任务。
     * “-“：表示范围；比如”1-5 * * ? * *”，意思就是每分钟的第1到5秒执行一次任务。
     * “L”：只允许在”日”和”周”上使用，分别有不同的作用；如果在”日”上使用，则表示该月的最后一天，如：1月31日；如果在”周”上使用，则意味着7或者SAT；另外，如果数字和L在”周”上组合使用则有其它含义，如：”6L”，则代表当前月的最后一个星期五。L用在”日”上还可以指定偏移，如”L-3”，表示该月的第3天到最后一天。
     * “W”：表示给定日期最近的工作日，只能用在”日”上面；例如：”15W”，表示每月15号最近的一个工作日，如果15号是周六，则在14号周五执行；如果15号是周日，则在16号周一执行。需要注意的是结合W使用时，不能使用范围，只能使用单个值。另外W也能和L联合使用，表示当月的最后一个工作日。
     * “#”：表示当月第几个周X，只能在”周”上使用；如：”1#3”，表示当月的第3个星期一。
     */

    private static final String JOB_NAME_PREFIX = "TASK_";

    /**
     * 获取触发器 key
     */
    private static TriggerKey getTriggerKey(Long jobId) {
        return TriggerKey.triggerKey(JOB_NAME_PREFIX + jobId);
    }

    /**
     * 获取jobKey
     */
    private static JobKey getJobKey(Long jobId) {
        return JobKey.jobKey(JOB_NAME_PREFIX + jobId);
    }

    /**
     * 获取表达式触发器
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
        } catch (SchedulerException e) {
            log.error("获取Cron表达式失败", e);
        }
        return null;
    }

    /**
     * 创建定时任务
     *
     * @param scheduler
     * @param scheduleJob
     */
    public static void createScheduleJob(Scheduler scheduler, Job scheduleJob) {
        try {
            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(getJobKey(scheduleJob.getJobId()))
                    .build();

            /**
             * 表达式调度构建器
             * withMisfireHandlingInstructionDoNothing
             * ——不触发立即执行
             * ——等待下次Cron触发频率到达时刻开始按照Cron频率依次执行
             *
             * withMisfireHandlingInstructionIgnoreMisfires
             * ——以错过的第一个频率时间立刻开始执行
             * ——重做错过的所有频率周期后
             * ——当下一次触发频率发生时间大于当前时间后，再按照正常的Cron频率依次执行
             *
             * withMisfireHandlingInstructionFireAndProceed
             * ——以当前时间为触发频率立刻触发一次执行
             * ——然后按照Cron频率依次执行
             */
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            // 按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleJob.getJobId()))
                    .withSchedule(scheduleBuilder).build();

            // 放入参数，运行时的方法可以获取
            jobDetail.getJobDataMap().put(Job.JOB_PARAM_KEY, scheduleJob);

            scheduler.scheduleJob(jobDetail, trigger);

            // 暂停任务
            if (scheduleJob.getStatus().equals(Job.ScheduleStatus.PAUSE.getValue())) {
                pauseJob(scheduler, scheduleJob.getJobId());
            }
        } catch (SchedulerException e) {
            log.error("创建定时任务失败", e);
        }
    }

    /**
     * 更新定时任务
     *
     * @param scheduler
     * @param scheduleJob
     */
    public static void updateScheduleJob(Scheduler scheduler, Job scheduleJob) {
        try {
            TriggerKey triggerKey = getTriggerKey(scheduleJob.getJobId());

            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
                    .withMisfireHandlingInstructionDoNothing();

            CronTrigger trigger = getCronTrigger(scheduler, scheduleJob.getJobId());

            if (trigger == null) {
                throw new SchedulerException("获取Cron表达式失败");
            } else {
                // 按新的 cronExpression表达式重新构建 trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                // 参数
                trigger.getJobDataMap().put(Job.JOB_PARAM_KEY, scheduleJob);
            }

            scheduler.rescheduleJob(triggerKey, trigger);

            // 暂停任务
            if (scheduleJob.getStatus().equals(Job.ScheduleStatus.PAUSE.getValue())) {
                pauseJob(scheduler, scheduleJob.getJobId());
            }

        } catch (SchedulerException e) {
            log.error("更新定时任务失败", e);
        }
    }

    /**
     * 立即执行任务
     */
    public static void run(Scheduler scheduler, Job scheduleJob) {
        try {
            // 参数
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(Job.JOB_PARAM_KEY, scheduleJob);

            scheduler.triggerJob(getJobKey(scheduleJob.getJobId()), dataMap);
        } catch (SchedulerException e) {
            log.error("执行定时任务失败", e);
        }
    }

    /**
     * 暂停任务
     *
     * @param scheduler
     * @param jobId
     */
    public static void pauseJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.pauseJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            log.error("暂停定时任务失败", e);
        }
    }

    /**
     * 恢复任务
     *
     * @param scheduler
     * @param jobId
     */
    public static void resumeJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.resumeJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            log.error("恢复定时任务失败", e);
        }
    }

    /**
     * 删除定时任务
     *
     * @param scheduler
     * @param jobId
     */
    public static void deleteScheduleJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.deleteJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            log.error("删除定时任务失败", e);
        }
    }
}
