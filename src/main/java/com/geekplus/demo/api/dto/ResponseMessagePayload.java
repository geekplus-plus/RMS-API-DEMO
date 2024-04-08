package com.geekplus.demo.api.dto;

import java.util.Map;

import lombok.Data;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2024/04/08 13:32
 **/
@Data
public class ResponseMessagePayload {

    private ResponseMessageHeader header;
    private Map<String, Object> body;

}
