package com.geekplus.demo.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.hutool.core.collection.CollUtil;

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

public class BoxApiHttpCallbackDemo {

    public static void main(String[] args) throws Exception {
        Long taskId = 5124009L;
        String callbackMessage = packageCallBack(taskId) ;
        returnBox(callbackMessage);
    }

    private static String packageCallBack(Long taskId) {
        String callbackMessage = "{\"id\":\"yourChannelId\",\"msgType\":\"RobotTaskCallbackMsg\",\"request\":{\"header\":{\"warehouseCode\":\"geek\",\"requestId\":\"3cf5a28e119f4332ba052b25d0153668\",\"version\":\"3.3.0\",\"ext\":\"P_AND_RS\"},\"body\":{\"taskType\":\"DELIVER_BOX\",\"wrapTaskId\":5124009,\"robotId\":2220008,\"robotPallets\":[{\"layer\":1,\"boxCode\":\"B1711038558\"}],\"tasks\":[{\"robotTypePhase\":\"P40_BOX_ARRIVED\",\"dest\":{\"x\":0.0,\"y\":0.0,\"z\":1},\"totalLayerCount\":1,\"waitDir\":-1,\"taskType\":\"DELIVER_BOX\",\"hostTaskId\":\"\",\"taskPhase\":\"BOX_ARRIVED\",\"startContainerLocation\":\"LATTICE:1065103501\",\"taskStatus\":\"EXECUTING\",\"stationId\":1,\"boxCode\":\"B1711038558\",\"startLatticeCode\":\"1065103501\",\"logicAreaId\":7,\"belongLogicAreaId\":7,\"isEmpty\":false,\"jobBusinessType\":\"DEFAULT\",\"robotId\":2220008,\"boxStatus\":\"LOADED\",\"destCellCode\":\"10251000\",\"robotLayer\":1,\"instruction\":\"GO_FETCH\",\"shelfSide\":\"F\",\"taskId\":" + taskId+ ",\"availableLayerCount\":0,\"destLocation\":{\"x\":7.382,\"y\":1.832,\"z\":1}}]}}}";
        return callbackMessage;
    }

    private static void returnBox(String callbackMessage) {
        JSONObject messageObject = JSON.parseObject(callbackMessage);
        JSONObject request = messageObject.getJSONObject("request");
        JSONObject header = request.getJSONObject("header");
        // 业务系统应当用requestId来做幂等，防止重复消费
        String requestId = header.getString("requestId");
        String msgType = messageObject.getString("msgType");
        JSONObject respBody = request.getJSONObject("body");
        // 当接收到回调消息之后, 先反馈收到再处理业务逻辑， 不要在回调流程直接处理业务
        callBackMsgResponse(requestId, msgType, "success");
        // 模拟在其他线程收到进站回调后送回货箱
        String requestUrl = "http://127.0.0.1:8895";
        String channelId = "yourChannelId";
        List<JSONObject> tasks = (List) respBody.get("tasks");
        if (CollUtil.isEmpty(tasks)) {
            return;
        }
        JSONObject task = tasks.get(0);
        String taskPhase = task.getString("taskPhase");
        String instruction = task.getString("instruction");
        // GO_FETCH SHELF_ARRIVED 说明进站了，可以送回
        if (TaskPhase.BOX_ARRIVED.equals(taskPhase) && Instruction.GO_FETCH.equals(instruction)) {
            Long taskId = task.getLong("taskId");
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
            String requestMsg = JSON.toJSONString(requestMessage);
            String result = HttpClient.sendPostRequest(requestUrl, requestMsg, HttpHeader.getJsonHeader());
            System.out.println("goReturn example \n:" + result);
            ResponseMessage responseMessage = JSON.parseObject(result, ResponseMessage.class);
            System.out.println("responseMessage example \n:" + responseMessage);
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
