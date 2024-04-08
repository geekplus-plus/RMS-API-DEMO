package com.geekplus.demo.api.demo;

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

public class ShelfApiHttpRequestDemo {

    /**
     * 【回调渠道】确认t_api_callback_msg_channel表中是否有postman_001
     * 【无请执行】INSERT INTO `t_api_callback_msg_channel` (`channel_id`, `channel_type`, `enable`, `max_retry_times`, `max_retry_timeout`, `channel_url`, `subscribe_channel_ids`) VALUES ('postman_001', 'HTTP', '1', '5', '20000', 'http://localhost:8888/mock/task/callback', '[]');
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        deliverShelf();
    }

    private static void deliverShelf() {
        // 请求货架进站
        String requestUrl = "http://127.0.0.1:8895";
        String channelId = "postman_001";
        String requestId = UUID.randomUUID().toString();
        // 创建header对象
        RequestMessageHeader header = new RequestMessageHeader(channelId, requestId);
        RequestMessagePayload payload = new RequestMessagePayload(header);
        // 创建body对象
        Map<String, Object> body = new HashMap<>();
        body.put(MessageFieldConstant.TASK_TYPE, TaskType.DELIVER_SHELF);
        body.put(MessageFieldConstant.INSTRUCTION, Instruction.GO_FETCH);
        body.put(MessageFieldConstant.STATION_ID, 26);
        body.put(MessageFieldConstant.SHELF_CODE, "A006412");
        body.put(MessageFieldConstant.SHELF_SIDES, Sets.newHashSet("F"));
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
