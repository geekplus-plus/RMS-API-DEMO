package com.geekplus.demo.api.scheduler.quarz;

import java.util.Map;

import cn.hutool.core.util.StrUtil;

import com.google.common.collect.Maps;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/10/19 16:05
 * @since athena-sprint-4c200m-4.0
 **/
public abstract class AbstractJobMetadata implements JobMetadata {

    protected JobConfigure jobConfigure;
    protected volatile String jobStatus;

    @Override
    public void setJobConfigure(JobConfigure jobConfigure) {
        this.jobConfigure = jobConfigure;
    }

    @Override
    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Override
    public String getJobStatus() {
        if (StrUtil.isBlank(jobStatus)) {
            return JobMetadata.STOP;
        }
        return jobStatus;
    }

    @Override
    public JobConfigure getJobConfigure() {
        return jobConfigure;
    }

    @Override
    public Map<String, String> defaultParams() {
        Map<String, String> params = Maps.newHashMap();
        params.put("internal_job_id", String.valueOf(getId()));
        return params;
    }
}
