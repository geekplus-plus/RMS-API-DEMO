package com.geekplus.demo.api.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2024/04/08 13:32
 **/
@Data
public class RequestMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String msgType;
    private RequestMessagePayload request;

    public RequestMessage(String channelId, String msgType, RequestMessagePayload request) {
        this.id = channelId;
        this.msgType = msgType;
        this.request = request;
    }
 }
