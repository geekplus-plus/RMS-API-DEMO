package com.geekplus.demo.api.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2024/04/08 13:32
 **/
@Data
public class ResponseMessageHeader implements Serializable {

    private Integer code;
    private String responseId;
    private String msg;

}
