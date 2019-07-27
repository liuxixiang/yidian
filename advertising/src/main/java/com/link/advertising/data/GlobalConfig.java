package com.link.advertising.data;


import com.link.advertising.utils.ContextUtils;
import com.link.advertising.utils.SPUtils;


public class GlobalConfig {

    public static final String FILE_NAME = "global_config";

    public static String getDeviceId() {
        return (String) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "sDeviceId", "");
    }

    public static void saveDeviceId(String sDeviceId) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "sDeviceId", sDeviceId);
    }


}
