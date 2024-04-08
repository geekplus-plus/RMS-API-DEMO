package com.geekplus.demo.api.callback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanglinlin
 * @version 1.0
 * @date 2024-04-08
 * @since geekplus-api-demo
 **/
public class CallbackStrategyFactory {

    private static Map<String, CallbackHandler> maps = new HashMap<>();

    public static void register(String name, CallbackHandler callbackHandler) {
        maps.put(name, callbackHandler);
    }

    public static CallbackHandler getCallbackHandler(String name) {
        return maps.get(name);
    }

}
