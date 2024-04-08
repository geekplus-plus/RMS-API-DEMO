package com.geekplus.demo.api.callback.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/10/13 12:47
 * @since athena-sprint-4c200m-4.0
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RobotTaskResponseMsgBody {

    private String taskId;

    private List<TaskInfo> taskInfos;
}
