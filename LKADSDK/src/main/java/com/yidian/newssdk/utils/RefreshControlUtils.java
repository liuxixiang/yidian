package com.yidian.newssdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yidian.newssdk.data.pref.GlobalConfig;

/**
 * @author zhangzhun
 * @date 2018/8/14
 */

public class RefreshControlUtils {

    public enum OPERATION {
        INDIVIDUAL_CHANNEL("last_channel_news", TimeUtil.HOUR),
        CHECK_GET3RD_INFO("get_3rd_info", TimeUtil.HOUR);


        public String tag;
        public long   threshold;

        OPERATION(String tagName, long threshold) {
            tag = tagName;
            this.threshold = threshold;
        }
    }


    public static void saveLastOperationTime(OPERATION operation) {
        saveToPreference(operation.tag);
    }


    private static void saveToPreference(String name, long timeStamp) {
        // write the last refresh time to shared preference file.
        SharedPreferences.Editor ed = ContextUtils.getApplicationContext()
                .getSharedPreferences("sdk_refresh", Context.MODE_PRIVATE).edit();
        ed.putLong(name, timeStamp);
        ed.apply();
    }

    private static void saveToPreference(String name) {
        // write the last refresh time to shared preference file.
        SharedPreferences.Editor ed = ContextUtils.getApplicationContext()
                .getSharedPreferences("sdk_refresh", Context.MODE_PRIVATE).edit();
        ed.putLong(name, System.currentTimeMillis());
        ed.apply();
    }

    public static boolean checkIndividualChannelNeedUpdate(String channelName, boolean firstUpdate){
        String spKey = channelName + '_' +  OPERATION.INDIVIDUAL_CHANNEL.tag;
        long lastTime = readFromPreference(spKey);
        if (lastTime == 0) {
            saveToPreference(spKey);
            return firstUpdate;
        }
        return System.currentTimeMillis() - lastTime > OPERATION.INDIVIDUAL_CHANNEL.threshold;
    }

    public static void saveIndividualChannelNeedUpdate(String channelName){
        String spKey = channelName + '_' +  OPERATION.INDIVIDUAL_CHANNEL.tag;
        saveToPreference(spKey);
    }

    private static long readFromPreference(String name) {
        SharedPreferences sp = ContextUtils.getApplicationContext()
                .getSharedPreferences("sdk_refresh", Context.MODE_PRIVATE);
        long lastTime = sp.getLong(name, 0);
        return lastTime;
    }

    public static boolean checkNeedUpdate(OPERATION operation, boolean firstUpdate) {
        long lastTime = readFromPreference(operation.tag);
        if (lastTime == 0) {
            saveToPreference(operation.tag);
            return firstUpdate;
        }
        return System.currentTimeMillis() - lastTime > operation.threshold;
    }

}
