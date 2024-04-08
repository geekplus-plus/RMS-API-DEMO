package com.geekplus.demo.api.scheduler.quarz;

import lombok.extern.slf4j.Slf4j;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/10/19 13:49
 * @since athena-sprint-4c200m-4.0
 **/
@Slf4j
public class SchedulerManager {

    private static Scheduler scheduler = null;

    public static Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * You must initial the quartz scheduler before you can use the quartz
     */
    public synchronized static void initScheduler() {
        try {
            if (scheduler == null) {
                System.setProperty("org.quartz.jobStore.misfireThreshold", "600");
                scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
            }
        } catch (SchedulerException e) {
            log.error("error when initial quartz Scheduler");
        }
    }

    /**
     * 根据配置类注册任务和触发器
     * @param jobConfigure
     */
    public synchronized static void registerJob(JobConfigure jobConfigure) throws SchedulerException {
        if (scheduler == null) {
            initScheduler();
        }
        JobKey jobKey = SchedulerManager.newJobKey(jobConfigure.getKey(), jobConfigure.getGroup());
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            scheduler.scheduleJob(jobConfigure.buildJobDetail(), jobConfigure.getTriggerConfigure().buildTrigger());
        } else {
            scheduler.pauseJob(jobKey);
            scheduler.resumeJob(jobKey);
        }
    }

    /**
     * 恢复某个任务
     * @param jobKey
     * @param group
     * @throws SchedulerException
     */
    public static void resumeJob(String jobKey, String group) throws SchedulerException {
        if (scheduler == null) {
            initScheduler();
        }
        scheduler.resumeJob(newJobKey(jobKey, group));
        scheduler.resumeTrigger(newTriggerKey(jobKey, group));

    }

    /**
     * 暂停某个任务
     * @param jobKey
     * @param group
     * @throws SchedulerException
     */
    public static void pauseJob(String jobKey, String group) throws SchedulerException {
        if (scheduler == null) {
            initScheduler();
        }
        scheduler.pauseTrigger(newTriggerKey(jobKey, group));
        scheduler.pauseJob(newJobKey(jobKey, group));
    }

    /**
     * 删除一个任务：先暂停触发器再注销任务然后再删除任务
     * @param jobKey
     * @param group
     * @throws SchedulerException
     */
    public static void deleteJob(String jobKey, String group) throws SchedulerException {
        scheduler.pauseTrigger(newTriggerKey(jobKey, group));
        scheduler.unscheduleJob(newTriggerKey(jobKey, group));
        scheduler.deleteJob(newJobKey(jobKey, group));
    }

    /**
     * 触发一个任务并马上执行
     * @param jobKey
     * @param group
     */
    public static void triggerJob(String jobKey, String group) throws SchedulerException {
        scheduler.triggerJob(newJobKey(jobKey, group));
    }

    /**
     * 按照新的设定的事件重新注册触发器
     * @param jobKey
     * @param group
     * @param cron
     * @throws SchedulerException
     */
    public static void rescheduleJob(String jobKey, String group, String cron) throws SchedulerException {
        TriggerKey triggerKey = newTriggerKey(jobKey, group);
        CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
        if (trigger != null && !trigger.getCronExpression().equals(cron)) {
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }

    /**
     * 服务停止的时候可以调用这个方法，以免进程还停留在JVM
     */
    public static void shutdownScheduler() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                log.error("error when shutdown quartz Scheduler", e);
            } finally {
                scheduler = null;
            }
        }
    }

    public static void removeTrigger(Trigger trigger) throws SchedulerException {
        scheduler.unscheduleJob(trigger.getKey());
    }

    public static void removeTrigger(String jobKey, String group) throws SchedulerException {
        scheduler.unscheduleJob(newTriggerKey(jobKey, group));
    }

    public static void pauseAll() throws SchedulerException {
        scheduler.pauseAll();
    }

    public static void resumeAll() throws SchedulerException {
        scheduler.resumeAll();
    }

    public static JobKey newJobKey(String key, String group) {
        if (group == null || "".equals(group)) {
            group = Scheduler.DEFAULT_GROUP;
        }
        return new JobKey(key, group);
    }

    public static TriggerKey newTriggerKey(String key, String group) {
        if (group == null || "".equals(group)) {
            group = Scheduler.DEFAULT_GROUP;
        }
        return new TriggerKey(key, group);
    }

}
