package com.geekplus.demo.api.scheduler.shelf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.PostConstruct;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSON;

import com.geekplus.demo.api.constants.Instruction;
import com.geekplus.demo.api.constants.MessageFieldConstant;
import com.geekplus.demo.api.constants.MsgType;
import com.geekplus.demo.api.dto.RequestMessage;
import com.geekplus.demo.api.dto.RequestMessageHeader;
import com.geekplus.demo.api.dto.RequestMessagePayload;
import com.geekplus.demo.api.dto.ResponseMessage;
import com.geekplus.demo.api.scheduler.quarz.AbstractJob;
import com.geekplus.demo.api.util.HttpClient;
import com.geekplus.demo.api.util.HttpHeader;

/**
 * @company Geek+
 * @auther: linlin.wang
 * @date: 2023/3/10 15:16
 * @description: 货架送回
 * @since
 */
@Setter
@Getter
@Slf4j
@DisallowConcurrentExecution
public class ShelfReturnTask extends AbstractJob {
    private static int stationStayTime = 5000;
    private static String requestUrl;

    private static volatile Set<ShelfReturnTask> returnShelfTasks = new CopyOnWriteArraySet<>();
    private long taskId;
    private long time;
    private long startTime;
    private String requestId;
    private int checkExceptionTaskTime;

    private long lastReturnTaskEmptyTime;

    public ShelfReturnTask() {

    }

    @PostConstruct
    public void init() {
        lastReturnTaskEmptyTime = System.currentTimeMillis();
    }

    public ShelfReturnTask(String requestId, long startTime, long taskId) {
        this.taskId = taskId;
        this.requestId = requestId;
        this.startTime = startTime;
    }

    // 添加任务
    public static void addTask(ShelfReturnTask task) {
        if (returnShelfTasks.contains(task)) {
            log.info("returnBoxTaskQueue add task is repeat, taskId:{}, requestId:{}", task.getTaskId(), task.getRequestId());
            return;
        }
        returnShelfTasks.add(task);
        log.info("returnBoxTaskQueue add task suc, taskId:{},  requestId:{}", task.getTaskId(), task.getRequestId());
    }

    @Override
    public void executeTask(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        requestUrl = dataMap.getString("httpRequestUrl");

        Set<ShelfReturnTask> canGoReturnTasks = new HashSet<>();
        for (ShelfReturnTask returnBoxTask : returnShelfTasks) {
            if ((System.currentTimeMillis() - returnBoxTask.getStartTime()) > stationStayTime) {
                canGoReturnTasks.add(returnBoxTask);
            }
        }
        for (ShelfReturnTask goReturnTask : canGoReturnTasks) {
            String result = goReturnRobotTask(goReturnTask);
            if (!StrUtil.equals(result, "FAIL")) {
                returnShelfTasks.remove(goReturnTask);
            }
        }
    }


    public String goReturnRobotTask(ShelfReturnTask returnBoxTask) {
        String channelId = "postman_001";
        Long taskId = returnBoxTask.getTaskId();
        String returnRequestId = UUID.randomUUID().toString();
        RequestMessageHeader returnHeader = new RequestMessageHeader("postman_001", returnRequestId);
        RequestMessagePayload payload = new RequestMessagePayload(returnHeader);
        // 创建body对象
        Map<String, Object> body = new HashMap<>();
        body.put(MessageFieldConstant.INSTRUCTION, Instruction.GO_RETURN);
        body.put(MessageFieldConstant.TASK_ID, taskId);
        payload.setBody(body);
        // 创建整个消息对象
        RequestMessage requestMessage = new RequestMessage(channelId, MsgType.ROBOT_TASK_UPDATE_REQUEST_MSG, payload);
        ResponseMessage responseMessage;
        try {
            String result = HttpClient.sendPostRequest(requestUrl, JSON.toJSONString(requestMessage), HttpHeader.getJsonHeader());
            responseMessage = JSON.parseObject(result, ResponseMessage.class);
        } catch (Exception e) {
            return "FAIL";
        }
        int code = responseMessage.getResponse().getHeader().getCode();
        log.info("goReturnRobotTask result --------------------->jobId {}, cost {}, responseHeaderCode {}",
                returnBoxTask.getTaskId(), (System.currentTimeMillis() - startTime), responseMessage);
        if (code == 0 || code == 2021) {
            return "SUC";
        } else {
            return "FAIL";
        }
    }
}
