package com.geekplus.demo.api.callback;

import com.alibaba.fastjson.JSONObject;

/**
 * @author wanglinlin
 * @version 1.0
 * @date 2024-04-08
 * @since geekplus-api-demo
 **/
public interface CallbackHandler {

    default void register(String name, CallbackHandler callbackHandler) {
        CallbackStrategyFactory.register(name, callbackHandler);
    }

    void process(JSONObject header, JSONObject body);

    /**
     * 实现类用注解@PostConstruct修饰
     */
    void init();

}
