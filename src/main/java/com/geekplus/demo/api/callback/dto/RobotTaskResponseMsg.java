package com.geekplus.demo.api.callback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/10/13 12:45
 * @since athena-sprint-4c200m-4.0
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RobotTaskResponseMsg {

    private String id;
    private String msgType;
    private Response response;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private RobotTaskResponseMsgHeader header;
        private RobotTaskResponseMsgBody body;
    }

}
