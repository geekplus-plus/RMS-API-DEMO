package com.geekplus.demo.api.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.geekplus.demo.api.constants.Instruction;
import com.geekplus.demo.api.constants.MessageFieldConstant;
import com.geekplus.demo.api.constants.MsgType;
import com.geekplus.demo.api.constants.TaskPhase;
import com.geekplus.demo.api.dto.RequestMessage;
import com.geekplus.demo.api.dto.RequestMessageHeader;
import com.geekplus.demo.api.dto.RequestMessagePayload;
import com.geekplus.demo.api.dto.ResponseMessage;
import com.geekplus.demo.api.util.HttpClient;
import com.geekplus.demo.api.util.HttpHeader;

public class ShelfApiHttpCallbackDemo {

    public static void main(String[] args) throws Exception {
        String callbackMessage = "This is callback message";
        returnShelf(callbackMessage);
    }

    private static void returnShelf(String callbackMessage) {
        JSONObject messageObject = JSON.parseObject(callbackMessage);
        JSONObject request = messageObject.getJSONObject("request");
        JSONObject header = request.getJSONObject("header");
        // 业务系统应当用requestId来做幂等，防止重复消费
        String requestId = header.getString("requestId");
        String msgType = messageObject.getString("msgType");
        JSONObject respBody = request.getJSONObject("body");
        // 当接收到回调消息之后, 先反馈收到再处理业务逻辑， 不要在回调流程直接处理业务
        callBackMsgResponse(requestId, msgType, "success");
        // 模拟在其他线程收到进站回调后送回货架
        String requestUrl = "http://127.0.0.1:8895";
        String channelId = "yourChannelId";
        String instruction = respBody.getString("instruction");
        String taskPhase = respBody.getString("taskPhase");
        // SHELF_ARRIVED 说明进站了，可以送回
        if (TaskPhase.SHELF_ARRIVED.equals(taskPhase) && !Instruction.GO_RETURN.equals(instruction)) {
            Long taskId = respBody.getLong("taskId");
            String returnRequestId = UUID.randomUUID().toString();
            RequestMessageHeader returnHeader = new RequestMessageHeader(channelId, returnRequestId);
            RequestMessagePayload payload = new RequestMessagePayload(returnHeader);
            // 创建body对象
            Map<String, Object> body = new HashMap<>();
            body.put(MessageFieldConstant.INSTRUCTION, Instruction.GO_RETURN);
            body.put(MessageFieldConstant.TASK_ID, taskId);
            payload.setBody(body);
            // 创建整个消息对象
            RequestMessage requestMessage = new RequestMessage(channelId, MsgType.ROBOT_TASK_UPDATE_REQUEST_MSG, payload);
            String result = HttpClient.sendPostRequest(requestUrl, JSON.toJSONString(requestMessage), HttpHeader.getJsonHeader());
            ResponseMessage responseMessage = JSON.parseObject(result, ResponseMessage.class);
            // 业务系统判断result是否成功
        }
    }

    private static String callBackMsgResponse(String requestId, String msgType, String msgResult) {
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
