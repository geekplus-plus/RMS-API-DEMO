package com.geekplus.demo.api.scheduler.quarz;

import cn.hutool.core.collection.CollUtil;

import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

/**
 * @author wanglinlin
 * @version athena-5.7.0
 * @Description 描述
 * @date 2023/5/18 20:00:09
 * @since athena-5.7.0.0
 **/
@Service
public class SchedulerService {

    public void startJob(Integer id) throws SchedulerException {
        JobMetadata jobMetadata = JobContainer.findOneById(id);
        if (JobMetadata.STOP.equals(jobMetadata.getJobStatus())) {
            JobConfigure jobConfigure = jobMetadata.getJobConfigure();
            if (CollUtil.isEmpty(jobConfigure.getExecuteParams())) {
                jobConfigure.setExecuteParams(jobMetadata.defaultParams());
            }
            SchedulerManager.registerJob(jobConfigure);
            jobMetadata.setJobStatus(JobMetadata.START);
        }
    }

    public void stopJob(Integer id) throws SchedulerException {
        JobMetadata jobMetadata = JobContainer.findOneById(id);
        if (JobMetadata.START.equals(jobMetadata.getJobStatus())) {
            SchedulerManager.deleteJob(jobMetadata.getJobName(), jobMetadata.getJobGroup());
            jobMetadata.setJobStatus(JobMetadata.STOP);
        }
    }
}
