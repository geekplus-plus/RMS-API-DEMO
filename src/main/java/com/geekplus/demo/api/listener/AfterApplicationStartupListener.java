package com.geekplus.demo.api.listener;

import lombok.extern.slf4j.Slf4j;

import org.quartz.SchedulerException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.geekplus.demo.api.scheduler.quarz.JobContainer;
import com.geekplus.demo.api.scheduler.quarz.JobMetadata;
import com.geekplus.demo.api.scheduler.quarz.SchedulerManager;
import com.geekplus.demo.api.scheduler.quarz.TaskIdConstant;

/**
 * 项目启动成功后会调用的代码
 * @author wanglinlin
 * @version 1.0
 * @date 2024-04-08
 * @since geekplus-api-demo
 **/
@Slf4j
public class AfterApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
       initScheduler();
       log.info("==========项目启动成功==========");
    }
    private void initScheduler() {
        SchedulerManager.initScheduler();
        for (JobMetadata registeredJob : JobContainer.initJobs()) {
            if (JobMetadata.START.equals(registeredJob.getJobStatus())) {
                try {
                    SchedulerManager.registerJob(registeredJob.getJobConfigure());
                } catch (SchedulerException e) {
                }
            }
        }
    }
}
