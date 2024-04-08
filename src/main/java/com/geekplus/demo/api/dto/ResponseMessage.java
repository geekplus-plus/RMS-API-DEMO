package com.geekplus.demo.api.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2024/04/08 13:32
 **/
@Data
public class ResponseMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String msgType;
    private ResponseMessagePayload response;

 }
