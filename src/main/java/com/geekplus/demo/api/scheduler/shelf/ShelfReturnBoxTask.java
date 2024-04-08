package com.geekplus.demo.api.scheduler.shelf;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.PostConstruct;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.geekplus.demo.api.callback.dto.GoReturnMessage;
import com.geekplus.demo.api.callback.dto.RobotTaskResponseMsg;
import com.geekplus.demo.api.scheduler.quarz.AbstractJob;
import com.geekplus.demo.api.util.HttpClient;
import com.geekplus.demo.api.util.HttpHeader;

/**
 * @company Geek+
 * @auther: linlin.wang
 * @date: 2023/3/10 15:16
 * @description: RSP 送回任务
 * @since
 */
@Setter
@Getter
@Slf4j
@DisallowConcurrentExecution
public class ShelfReturnBoxTask extends AbstractJob {
    private static int stationStayTime = 5000;
    private static String requestUrl;

    private static volatile Set<ShelfReturnBoxTask> returnBoxTasks = new CopyOnWriteArraySet<>();
    private long taskId;
    private long time;
    private long startTime;
    private String requestId;
    private int checkExceptionTaskTime;

    private long lastReturnTaskEmptyTime;

    public ShelfReturnBoxTask() {

    }

    @PostConstruct
    public void init() {
        lastReturnTaskEmptyTime = System.currentTimeMillis();
    }

    public ShelfReturnBoxTask(String requestId, long startTime, long taskId) {
        this.taskId = taskId;
        this.requestId = requestId;
        this.startTime = startTime;
    }

    // 添加任务
    public static void addTask(ShelfReturnBoxTask task) {
        if (returnBoxTasks.contains(task)) {
            log.info("returnBoxTaskQueue add task is repeat, taskId:{}, requestId:{}", task.getTaskId(), task.getRequestId());
            return;
        }
        returnBoxTasks.add(task);
        log.info("returnBoxTaskQueue add task suc, taskId:{},  requestId:{}", task.getTaskId(), task.getRequestId());
    }

    @Override
    public void executeTask(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        requestUrl = dataMap.getString("requestUrl");

        Set<ShelfReturnBoxTask> canGoReturnTasks = new HashSet<>();
        for (ShelfReturnBoxTask returnBoxTask : returnBoxTasks) {
            if ((System.currentTimeMillis() - returnBoxTask.getStartTime()) > stationStayTime) {
                canGoReturnTasks.add(returnBoxTask);
            }
        }
        for (ShelfReturnBoxTask goReturnTask : canGoReturnTasks) {
            String result = goReturnRobotTask(goReturnTask);
            if (!StrUtil.equals(result, "FAIL")) {
                returnBoxTasks.remove(goReturnTask);
            }
        }
    }


    public String goReturnRobotTask(ShelfReturnBoxTask returnBoxTask) {
        String requestMessage = GoReturnMessage.generateGoReturnMessage(returnBoxTask.getRequestId(),
                returnBoxTask.getTaskId(), 0);
        String result;
        try {
            result = HttpClient.sendPostRequest(requestUrl, requestMessage, HttpHeader.getJsonHeader());
        } catch (Exception e) {
            return "FAIL";
        }
        if (StrUtil.isEmpty(result)) {
            log.info("goReturnRobotTask result --------------------->jobId {}, cost {}, result is null",
                    returnBoxTask.getTaskId(), (System.currentTimeMillis() - startTime));
            return "FAIL";
        } else {
            RobotTaskResponseMsg robotTaskResponseMsg = JSONUtil.toBean(result, RobotTaskResponseMsg.class);
            int code = robotTaskResponseMsg.getResponse().getHeader().getCode();

            log.info("goReturnRobotTask result --------------------->jobId {}, cost {}, responseHeaderCode {}",
                    returnBoxTask.getTaskId(), (System.currentTimeMillis() - startTime), result);
            if (code == 0 || code == 2021) {
                return "SUC" + result;
            } else {
                return "FAIL";
            }
        }
    }
}
