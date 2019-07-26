package com.linken.newssdk.utils;

import android.util.Log;

/**
 * Created by chenyichang on 2018/5/18.
 */

public class LogUtils {

    public static boolean DEBUG = false;

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public static boolean isDebug() {
        return DEBUG;
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

}
