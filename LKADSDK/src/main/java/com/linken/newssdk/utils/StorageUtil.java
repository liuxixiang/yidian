package com.linken.newssdk.utils;

import android.content.Context;

/**
 * @author zhangzhun
 * @date 2018/8/10
 */

public class StorageUtil {

    public static String getInternalCacheBasePath() {
        Context ctx = ContextUtils.getApplicationContext();
        return ctx.getFilesDir().getAbsolutePath();
    }
}
