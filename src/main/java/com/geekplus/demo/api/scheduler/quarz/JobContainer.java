package com.geekplus.demo.api.scheduler.quarz;

import java.util.Collection;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Maps;

import com.geekplus.demo.api.constants.SpringContextUtil;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/10/19 14:11
 * @since athena-sprint-4c200m-4.0
 **/
@Slf4j
public class JobContainer {

    private static Map<Integer, JobMetadata> jobMetadataMap = Maps.newHashMap();

    public static Collection<JobMetadata> getRegisteredJobs() {
        return jobMetadataMap.values();
    }

    public static JobMetadata findOneById(Integer id) {
        return jobMetadataMap.get(id);
    }

    public static Collection<JobMetadata> initJobs() {
        Collection<JobMetadata> jobMetadataList = SpringContextUtil.getBeanList(JobMetadata.class);
        for (JobMetadata jobMetadata : jobMetadataList) {
            jobMetadata.setJobConfigure(jobMetadata.buildJobConfigure());
            jobMetadataMap.put(jobMetadata.getId(), jobMetadata);
        }
        return jobMetadataMap.values();
    }

}
