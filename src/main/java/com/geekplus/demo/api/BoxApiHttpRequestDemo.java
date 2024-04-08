package com.geekplus.demo.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.hutool.core.util.StrUtil;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;

import com.geekplus.demo.api.constants.Instruction;
import com.geekplus.demo.api.constants.MessageFieldConstant;
import com.geekplus.demo.api.constants.MsgType;
import com.geekplus.demo.api.constants.TaskType;
import com.geekplus.demo.api.dto.RequestMessage;
import com.geekplus.demo.api.dto.RequestMessageHeader;
import com.geekplus.demo.api.dto.RequestMessagePayload;
import com.geekplus.demo.api.dto.ResponseMessage;
import com.geekplus.demo.api.util.HttpClient;
import com.geekplus.demo.api.util.HttpHeader;

public class BoxApiHttpRequestDemo {

    public static void main(String[] args) throws Exception {
        deliverBox();
    }

    private static void deliverBox() {
        // 请求货架进站
        String requestUrl = "http://127.0.0.1:8895";
        String channelId = "yourChannelId";
        String requestId = UUID.randomUUID().toString();
        // 创建header对象
        RequestMessageHeader header = new RequestMessageHeader(channelId, requestId);
        RequestMessagePayload payload = new RequestMessagePayload(header);
        // 创建body对象
        Map<String, Object> body = new HashMap<>();
        body.put(MessageFieldConstant.TASK_TYPE, TaskType.DELIVER_BOX);
        body.put(MessageFieldConstant.INSTRUCTION, Instruction.GO_FETCH);
        body.put(MessageFieldConstant.STATION_ID, 1);
        body.put(MessageFieldConstant.BOX_CODE, "B1711038558");
        payload.setBody(body);
        // 创建整个消息对象
        RequestMessage requestMessage = new RequestMessage(channelId, MsgType.ROBOT_TASK_REQUEST_MSG, payload);
        // 发送请求， 并记录返回的任务号到自己的业务系统
        String result = HttpClient.sendPostRequest(requestUrl, JSON.toJSONString(requestMessage), HttpHeader.getJsonHeader());
        if (StrUtil.isNotEmpty(result)) {
            ResponseMessage responseMessage = JSON.parseObject(result, ResponseMessage.class);
            long taskId = Long.parseLong(String.valueOf(responseMessage.getResponse().getBody().get("taskId")));
            System.out.println(taskId);
        } else {
            // 说明出现了一些未知异常， 需要业务系统处理这种异常
        }
    }

}
