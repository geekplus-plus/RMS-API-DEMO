package com.geekplus.demo.api.callback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RobotTaskResponseMsgHeader {

    private String responseId;
    private int code;
    private String msg;

}
