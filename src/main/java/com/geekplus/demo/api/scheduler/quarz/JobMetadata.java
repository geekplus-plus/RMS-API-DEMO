package com.geekplus.demo.api.scheduler.quarz;

import java.util.Map;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/10/19 14:08
 * @since athena-sprint-4c200m-4.0
 **/
public interface JobMetadata {

    String START = "START";
    String STOP = "STOP";
    String DEFAULT_GROUP = "DEFAULT";

    /**
     * 任务主键id， 确保全局唯一
     * @return
     */
    Integer getId();

    /**
     * 任务名称， 请确保全局唯一
     * @return
     */
    String getJobName();

    /**
     * 任务分组，默认DEFAULT
     * @return
     */
    default String getJobGroup() {
        return DEFAULT_GROUP;
    }

    /**
     * 任务状态， 默认STOP， 有START 和 STOP 两种状态
     * @return
     */
    String getJobStatus();

    /**
     * 设置任务状态
     * @return
     */
    void setJobStatus(String jobStatus);

    /**
     * 任务执行的cron表达式
     * @return
     */
    default String cornExpression() {
        return "0/5 * * * * ?";
    }

    /**
     * 具体的任务实现类，包含包路径
     * @return
     */
    String implClass();

    /**
     * 任务描述
     * @return
     */
    String getDesc();

    /**
     * 是否需要展现在查询页面
     * @return
     */
    default boolean needShowInQueryListPage() {
        return true;
    }

    void setJobConfigure(JobConfigure jobConfigure);

    JobConfigure getJobConfigure();

    default JobConfigure buildJobConfigure() {
        JobConfigure jobConfigure = new JobConfigure();
        TriggerConfigure triggerConfigure = new TriggerConfigure();
        triggerConfigure.setKey("triggerKey".concat(getJobName()));
        triggerConfigure.setGroup("DEFAULT");
        triggerConfigure.setCron(cornExpression());
        jobConfigure.setTriggerConfigure(triggerConfigure);
        jobConfigure.setGroup(getJobGroup());
        jobConfigure.setKey(getJobName());
        jobConfigure.setClassName(implClass());
        jobConfigure.setDescription(getDesc());
        return jobConfigure;
    }

    /**
     * 提供的默认参数
     * @return
     */
    Map<String, String> defaultParams();

}
