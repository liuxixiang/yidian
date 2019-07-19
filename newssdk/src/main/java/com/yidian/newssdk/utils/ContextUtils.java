package com.yidian.newssdk.utils;

import android.content.Context;

/**
 * Created by chenyichang on 2018/5/19.
 */

public class ContextUtils {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getApplicationContext() {
        return mContext;
    }
}
