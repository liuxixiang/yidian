package com.link.advertising.utils;

import android.content.Context;

public class ContextUtils {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getApplicationContext() {
        return mContext;
    }
}
