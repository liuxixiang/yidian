package com.linken.newssdk.data.pref;

import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.SPUtils;

/**
 * Created by chenyichang on 2018/5/19.
 */

public class GlobalConfig {

    public static final String FILE_NAME = "global_config";

    public static String getAppInfo() {
        return (String) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "app_info", "");
    }

    public static void saveAppInfo(String appInfo) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "app_info", appInfo);
    }


    public static String getOpParams() {
        return (String) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "op", "");
    }

    public static void saveOpParams(String op) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "op", op);
    }

    /**
     *
     * @return 与服务器的误差，单位毫秒
     */
    public static long getServerDiffTime() {
        return (long) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "serverDiffTime", 0L);
    }

    public static void saveServerDiffTime(long serverDiffTime) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "serverDiffTime", serverDiffTime);
    }

    public static String getDeviceId() {
        return (String) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "sDeviceId", "");
    }

    public static void saveDeviceId(String sDeviceId) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "sDeviceId", sDeviceId);
    }

    public static void saveThemeId(int themeId) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "themeId", themeId);
    }

    public static int getThemeId() {
        return (int) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "themeId", 0);
    }

    public static void saveFontSize(float fontSize) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "fontSize", fontSize);
    }

    public static float getFontSize() {
        return (float) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "fontSize", 17.0f);
    }

    public static boolean getNightModeConfig() {
        return (boolean) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "nightMode", false);
    }

    public static void saveNightModeConfig(boolean isNightMode) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "nightMode", isNightMode);
    }

}
