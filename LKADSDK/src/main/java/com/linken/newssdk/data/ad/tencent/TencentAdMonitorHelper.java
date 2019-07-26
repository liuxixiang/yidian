package com.linken.newssdk.data.ad.tencent;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.linken.ad.data.AdvertisementCard;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lishuxiang on 2018/6/8.
 */

public class TencentAdMonitorHelper {

    interface ClickReplaceMacro {
        String WIDTH = "__WIDTH__";
        String HEIGHT = "__HEIGHT__";
        String DOWN_X = "__DOWN_X__";
        String DOWN_Y = "__DOWN_Y__";
        String UP_X = "__UP_X__";
        String UP_Y = "__UP_Y__";
    }

    /**
     * Sample click url
     * http://c.gdt.qq.com/gdt_mclick.fcg?viewid=9lCJ3Nk9g5wLz7k_80KYb5tofvWqN_XfE6I2GRbeFvXz1KXVFGdkx4TH4iY9svVrx5nGbrTWQU7F83EaF!O5MIqEeuIt2a28_9tzhiFUUlAjs1Bg_LUzJaYiHPsogfPn!MwjlLYc!2CUmeGx6oWfp!BidFhL1ZzybbRTGcbUT1L3t3pd7dVd6exJkjBDHquCv7szfLd_ThTwFIUk!_ij5g&jtype=0&i=1&os=2&asi=%7B%22mf%22%3A%22Xiaomi%22%7D&acttype=1&s=%7B%22req_width%22%3A%22__REQ_WIDTH__%22%2C%22req_height%22%3A%22__REQ_HEIGHT__%22%2C%22width%22%3A%22__WIDTH__%22%2C%22height%22%3A%22__HEIGHT__%22%2C%22down_x%22%3A%22__DOWN_X__%22%2C%22down_y%22%3A%22__DOWN_Y__%22%2C%22up_x%22%3A%22__UP_X__%22%2C%22up_y%22%3A%22__UP_Y__%22%7D&xp=3
     *
     * @param url 点击url
     * @return
     */
    public static String updateThirdPartyClickUrl(String url, TencentClickParamData clickParamData) {//负责落地页url的处理，替换相关字段
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        return generalParameterReplacement(url, clickParamData);
    }

    private static boolean hasMacroToReplace(@NonNull String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.contains(ClickReplaceMacro.WIDTH) || url.contains(ClickReplaceMacro.HEIGHT)
                || url.contains(ClickReplaceMacro.DOWN_X) || url.contains(ClickReplaceMacro.DOWN_Y)
                || url.contains(ClickReplaceMacro.UP_X) || url.contains(ClickReplaceMacro.UP_Y);
    }

    private static String generalParameterReplacement(@NonNull String url, TencentClickParamData clickParamData) {
        if (TextUtils.isEmpty(url) || !hasMacroToReplace(url) || clickParamData == null) {
            return url;
        }
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("__\\w+__");
        Matcher matcher = pattern.matcher(url);

        while (matcher.find()) {
            String str = matcher.group();
            if (ClickReplaceMacro.DOWN_X.equals(str)) {
                matcher.appendReplacement(sb, String.valueOf(clickParamData.getDownX()));
            } else if (ClickReplaceMacro.DOWN_Y.equals(str)) {
                matcher.appendReplacement(sb, String.valueOf(clickParamData.getDownY()));
            } else if (ClickReplaceMacro.UP_X.equals(str)) {
                matcher.appendReplacement(sb, String.valueOf(clickParamData.getUpX()));
            } else if (ClickReplaceMacro.UP_Y.equals(str)) {
                matcher.appendReplacement(sb, String.valueOf(clickParamData.getUpY()));
            } else if (ClickReplaceMacro.WIDTH.equals(str)) {
                matcher.appendReplacement(sb, String.valueOf(clickParamData.getWidth()));
            } else if (ClickReplaceMacro.HEIGHT.equals(str)) {
                matcher.appendReplacement(sb, String.valueOf(clickParamData.getHeight()));
            }
        }

        matcher.appendTail(sb);

        return sb.toString();
    }

    public static boolean shouldReportView(@NonNull AdvertisementCard card) {
        if (card.tencentAdHasExpose) {
            return false;
        } else {
            card.tencentAdHasExpose = true;
            return true;
        }
    }

    public static void reportThirdPartyEvents(String[] urls) {
//        if (urls == null) return;
//
//        for (int i = 0; i < urls.length; i++) {
//            final String macroUrl = urls[i];
//            if (AdvertisementUtil.reportLogEnabled) {
//                Log.d(ADConstants.AD_LOG, macroUrl);
//            }
//            AdExposeUrlApi api = new AdExposeUrlApi(macroUrl, new BaseTaskListener() {
//
//                @Override
//                public void onAllFinish(BaseTask task) {
//                    AdExposeUrlApi api = (AdExposeUrlApi) task;
//                    IAdRelatedStatusListener listener = AdvertisementModule.getInstance().getAdRelatedStateListener();
//                    if (listener != null) {
//                        listener.reportThirdPartyEvent(macroUrl, api.getResult().isSuccessful() && api.getAPIResult().isSuccessful());
//                    }
//                }
//
//                @Override
//                public void onCancel() {
//                }
//
//            });
//            api.setIsJsonResult(true);
//            api.dispatch();
//        }
    }
}
