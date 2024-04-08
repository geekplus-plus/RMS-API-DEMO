package com.geekplus.demo.api.scheduler.quarz;

import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/10/19 13:51
 * @since athena-sprint-4c200m-4.0
 **/
@Slf4j
@Getter
@Setter
public class JobConfigure {


    /**
     * job key
     */
    private String key;
    /**
     * job group
     */
    private String group = "DEFAULT";
    /**
     * job description
     */
    private String description;
    /**
     * executable job class
     */
    private String className;
    /**
     * job trigger
     */
    private TriggerConfigure triggerConfigure;

    private Map<String, String> executeParams;

    private Date triggerStartTime;

    public JobDetail buildJobDetail() {
        JobDetail job = null;
        try {
            Class clazz = Class.forName(className);
            job = JobBuilder.newJob(clazz).withIdentity(getJobKey()).build();
            if (executeParams != null && !executeParams.isEmpty()) {
                job.getJobDataMap().putAll(executeParams);
            }
        } catch (ClassNotFoundException e) {
            log.error("error when build quartz job detail");
        }
        return job;
    }

    public JobKey getJobKey() {
        return new JobKey(key, group);
    }

}
