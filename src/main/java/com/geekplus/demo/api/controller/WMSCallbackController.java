package com.geekplus.demo.api.controller;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.geekplus.demo.api.callback.CallbackHandler;
import com.geekplus.demo.api.callback.CallbackStrategyFactory;
import com.geekplus.demo.api.constants.MsgType;
import com.geekplus.demo.api.scheduler.quarz.JobContainer;
import com.geekplus.demo.api.scheduler.quarz.JobMetadata;
import com.geekplus.demo.api.scheduler.quarz.SchedulerManager;
import com.geekplus.demo.api.scheduler.quarz.SchedulerService;
import com.geekplus.demo.api.scheduler.quarz.TaskIdConstant;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/12/21 15:19
 * @since athena-sprint-4c200m-4.0
 **/
@Slf4j
@RestController
@RequestMapping("/mock")
public class WMSCallbackController {


    @Resource
    private SchedulerService schedulerService;
    private boolean isInit = false;
    private Cache<String, Integer> requestIdCache = CacheBuilder.newBuilder().
            expireAfterWrite(1, TimeUnit.MINUTES).build();


    /**
     * {"id":"*","msgType":"com.geekplus.athena.engine.api.msg.callback.WarehouseCallbackMsg","request":{"header":{"requestId":"fa28e70005764e98bb30f5185451d4d9","version":"3.3.0"},"body":{"callbackCategory":"BOX","callbackType":"SCAN_BOX","boxCode":"B1640072056487","collectingTowerId":"101","source":"EQUIPMENT"}}}
     * @param data
     * @return
     */
    @RequestMapping(value = "/{strategyName}/callback", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String callback(@RequestBody String data, @PathVariable("strategyName") String strategyName) {
        if ("test".equalsIgnoreCase(data)) {
            // 简单测试这个回调接口是否是通的
            return callBackMsgResponse(System.currentTimeMillis() + "_test", MsgType.WAREHOUSE_CALLBACK_MSG, "testOK");
        }
        if (!isInit) {
            isInit = initScheduler();
        }
        JSONObject jsonObject = JSON.parseObject(data);
        JSONObject request = jsonObject.getJSONObject("request");
        JSONObject header = request.getJSONObject("header");
        String requestId = header.getString("requestId");
        JSONObject body = request.getJSONObject("body");
        String msgType = jsonObject.getString("msgType");

        if (Objects.nonNull(requestIdCache.getIfPresent(requestId))) {
            // 模拟处理幂等
            return callBackMsgResponse(requestId, msgType, "duplicate");
        }
        requestIdCache.put(requestId, 0);
        CallbackHandler callbackHandler = CallbackStrategyFactory.getCallbackHandler(strategyName);
        if (Objects.nonNull(callbackHandler)) {
            // 模拟器异步处理回调， 千万不要在回调流程夹杂业务逻辑
            ThreadUtil.execAsync(() -> callbackHandler.process(header, body));
            return callBackMsgResponse(requestId, msgType, "success");
        }
        return callBackMsgResponse(requestId, msgType, "error");
    }

    private boolean initScheduler() {
        try {
            schedulerService.startJob(TaskIdConstant.RSP_MULTI_STATION_TASK);
            schedulerService.startJob(TaskIdConstant.SHELF_MULTI_STATION_TASK);
        } catch (SchedulerException e) {
            return false;
        }
        return true;
    }

    /**
     * 收到回调立马反馈，不要在回调流程里处理业务逻辑， 注意反馈的格式必须按照如下msg的方式， 不然RMS会认为发送失败
     * @param requestId
     * @param msgType
     * @param msgResult
     * @return
     */
    private String callBackMsgResponse(String requestId, String msgType, String msgResult) {
        if (msgType.contains(MsgType.ROBOT_TASK_CALLBACK_MSG)) {
            msgType = MsgType.ROBOT_TASK_CALLBACK_RESPONSE_MSG;
        } else if (msgType.contains(MsgType.WAREHOUSE_CALLBACK_MSG)) {
            msgType = MsgType.WAREHOUSE_CALLBACK_RESPONSE_MSG;
        }
        String msg = "{\n"
                + "  \"id\": \"*\",\n"
                + "  \"msgType\": \"%s\",\n"
                + "  \"response\": {\n"
                + "    \"header\": {\n"
                + "      \"responseId\": \"%s\",\n"
                + "      \"code\": 0,\n"
                + "      \"msg\": \"" + msgResult +"\"\n"
                + "    }\n"
                + "  }\n"
                + "}";
        msg = String.format(msg, msgType, requestId);
        return msg;
    }


}
