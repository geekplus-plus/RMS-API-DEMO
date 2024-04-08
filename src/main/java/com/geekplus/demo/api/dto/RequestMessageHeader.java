package com.geekplus.demo.api.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2024/04/08 13:32
 **/
@Data
public class RequestMessageHeader implements Serializable {

    private static final long serialVersionUID = 1L;
    private String channelId;
    private String requestId;
    private String clientCode = "geek";
    private String warehouseCode = "geek";
    private String userId = "admin";
    private String userKey = "123456";
    private String version = "3.3.0";
    private String language = "en_us";

    public RequestMessageHeader(String channelId, String requestId) {
        this.requestId = requestId;
        this.channelId = channelId;
    }

}
