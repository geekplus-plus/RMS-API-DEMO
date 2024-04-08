package com.geekplus.demo.api.callback.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import com.geekplus.demo.api.callback.CallbackHandler;
import com.geekplus.demo.api.constants.Instruction;
import com.geekplus.demo.api.constants.TaskPhase;
import com.geekplus.demo.api.constants.TaskType;
import com.geekplus.demo.api.scheduler.box.RSPReturnBoxTask;
import com.geekplus.demo.api.scheduler.shelf.ShelfReturnTask;


/**
 * @company Geek+
 * @auther: linlin.wang
 * @date: 2022/6/9 13:59
 * @description: 处理工作站的回调事件，发送到达工作站自动送回
 * @since
 */
@Slf4j
@Component
public class TaskCallbackHandler implements CallbackHandler {
    @Override
    public void process(JSONObject header, JSONObject body) {
        String requestId = header.getString("requestId");
        long startTime = System.currentTimeMillis();
        String taskType = body.getString("taskType");
        if (StrUtil.isNotEmpty(taskType)) {
            String taskId = body.getString("taskId");
            if (TaskType.DELIVER_SHELF.equals(taskType)) {
                String instruction = body.getString("instruction");
                String taskPhase = body.getString("taskPhase");
                if (TaskPhase.SHELF_ARRIVED.equals(taskPhase) && !Instruction.GO_RETURN.equals(instruction)) {
                    // SHELF_ARRIVED 说明进站了，可以送回
                    ShelfReturnTask.addTask(new ShelfReturnTask(requestId + taskId, startTime,
                            Long.valueOf(taskId)));
                }
            } else {
                List<JSONObject> tasks = (List) body.get("tasks");
                if (CollUtil.isEmpty(tasks)) {
                    return;
                }
                JSONObject object = tasks.get(0);
                if (!object.containsKey("taskId") || !object.containsKey("taskPhase")
                        || !object.containsKey("boxCode")) {
                    return;
                }
                if (!object.getString("instruction").equals("GO_FETCH")) {
                    return;
                }
                if (!object.getString("taskPhase").equals("BOX_ARRIVED")) {
                    return;
                }
                if (object.containsKey("stationId")) {
                    RSPReturnBoxTask.addTask(new RSPReturnBoxTask(requestId + object.getString("taskId"), startTime,
                            Long.valueOf(object.getString("taskId")), object.getString("boxCode"), object.getString("stationId")));
                }

            }
        } else {
            List<JSONObject> tasks = (List) body.get("tasks");
            if (CollUtil.isEmpty(tasks)) {
                return;
            }
        }

    }

    @PostConstruct
    @Override
    public void init() {
        register("task", this);

    }



}
