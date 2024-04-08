package com.geekplus.demo.api.scheduler.shelf;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.geekplus.demo.api.scheduler.quarz.AbstractJobMetadata;
import com.geekplus.demo.api.scheduler.quarz.TaskIdConstant;

/**
 * @author wanglinlin
 * @version 1.0
 * @date 2024/4/08 21:16
 * @Description 多拣选操作，定时送回货架任务
 **/
@Component
public class ShelfTaskMetaData extends AbstractJobMetadata {

    @Resource
    private Environment environment;
    @Override
    public Integer getId() {
        return TaskIdConstant.SHELF_MULTI_STATION_TASK;
    }

    @Override
    public String getJobName() {
        return "货架定时送回";
    }

    @Override
    public String implClass() {
        return ShelfReturnTask.class.getName();
    }

    @Override
    public String cornExpression() {
        return "0/1 * * * * ?";
    }

    @Override
    public String getDesc() {
        return "货架定时送回";
    }

    @Override
    public String getJobGroup() {
        return "SHELF";
    }

    @Override
    public Map<String, String> defaultParams() {
        Map<String, String> params = super.defaultParams();
        params.put("stationStayTime", environment.getProperty("station.stay.time"));
        params.put("httpRequestUrl", environment.getProperty("rms.http.host"));
        return params;
    }
}
