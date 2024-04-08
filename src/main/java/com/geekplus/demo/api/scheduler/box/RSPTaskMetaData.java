package com.geekplus.demo.api.scheduler.box;

import java.util.Map;


import javax.annotation.Resource;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.geekplus.demo.api.scheduler.quarz.AbstractJobMetadata;
import com.geekplus.demo.api.scheduler.quarz.TaskIdConstant;

/**
 * @author wanglinlin
 * @version 1.0
 * @date 2023/5/16 16:26
 * @since athena-5.6.0
 * @Description 多拣选操作，定时送回货箱任务
 **/
@Component
public class RSPTaskMetaData extends AbstractJobMetadata {

    @Resource
    private Environment environment;
    @Override
    public Integer getId() {
        return TaskIdConstant.RSP_MULTI_STATION_TASK;
    }

    @Override
    public String getJobName() {
        return "RSP多拣选定时送回";
    }

    @Override
    public String implClass() {
        return RSPReturnBoxTask.class.getName();
    }

    @Override
    public String cornExpression() {
        return "0/1 * * * * ?";
    }

    @Override
    public String getDesc() {
        return "RSP多拣选定时送回";
    }

    @Override
    public String getJobGroup() {
        return "RSP";
    }

    @Override
    public Map<String, String> defaultParams() {
        Map<String, String> params = super.defaultParams();
        params.put("stationStayTime", environment.getProperty("station.stay.time"));
        params.put("httpRequestUrl", environment.getProperty("rms.http.host"));
        return params;
    }
}
