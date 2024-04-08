package com.geekplus.demo.api.scheduler.quarz;

import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/10/19 13:29
 * @since athena-sprint-4c200m-4.0
 **/
@Slf4j
public abstract class AbstractJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (beforeExecuteTask(context)) {
            executeTask(context);
            removeTriggerIfNeeded(context);
            afterExecuteTask(context);
        }
    }


    protected void afterExecuteTask(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        String taskId = jobKey.getName();
        String group = jobKey.getGroup();
        if (TriggerConfigure.SCHEDULE_ONCE.equals(group)) {
            try {
                SchedulerManager.deleteJob(taskId, group);
            } catch (SchedulerException e) {
                log.error("error when delete schedule job:{}, {}", taskId, e);
            }
        }
        String jobName = context.getJobDetail().getKey().getName();
       // log.info("finish job:{} at:{}", jobName, new Date());
    }


    protected boolean beforeExecuteTask(JobExecutionContext context) {
        JobKey jobKey = context.getJobDetail().getKey();
        String taskId = jobKey.getName();
        String group = jobKey.getGroup();
        if (TriggerConfigure.SCHEDULE_ONCE.equals(group)) {
            boolean needExecute = true;
            // TODO 根据状态啥的判断要不要执行
            return needExecute;
        }
        return true;
    }


    private void removeTriggerIfNeeded(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        Map<String, Object> dataMap = jobDetail.getJobDataMap();
        if (dataMap != null && !dataMap.isEmpty()) {
            Object removeTriggerFlag = dataMap.get("needRemoveTrigger");
            if (removeTriggerFlag != null) {
                boolean isOneOff = (boolean) removeTriggerFlag;
                if (isOneOff) {
                    Trigger trigger = context.getTrigger();
                    try {
                        SchedulerManager.removeTrigger(trigger);
                    } catch (SchedulerException e) {
                        log.error("error when remove trigger", e);
                    }
                }
            }
        }
    }

    public abstract void executeTask(JobExecutionContext context) throws JobExecutionException;

}
