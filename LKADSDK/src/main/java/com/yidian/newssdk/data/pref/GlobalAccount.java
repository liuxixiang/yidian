package com.yidian.newssdk.data.pref;

import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.utils.SPUtils;

/**
 * Created by chenyichang on 2018/5/19.
 */

public class GlobalAccount {

    private static final String FILE_NAME = "global_account";

    public static void saveYduserId(String userId) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "userId", userId);
    }

    public static String getYduserId() {
        return (String) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "userId", "");
    }

    public static void saveCookie(String cookie) {
        SPUtils.put(FILE_NAME, ContextUtils.getApplicationContext(), "cookie", cookie);
    }

    public static String getCookie() {
        return (String) SPUtils.get(FILE_NAME, ContextUtils.getApplicationContext(), "cookie", "");
    }


}
