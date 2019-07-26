package com.linken.newssdk.utils;

/**
 * @author zhangzhun
 * @data 2019/5/7
 */
public class HMTAgentUtil {

    private static final String CHANNELID = "yidian_sdk_android";

    public static void init() {
        ThreadUtils.exec(new Runnable() {
            @Override
            public void run() {
//                HMTAgent.setChannelId(ContextUtils.getApplicationContext(), CHANNELID);
//                HMTAgent.Initialize(ContextUtils.getApplicationContext());
            }
        });
    }
}
