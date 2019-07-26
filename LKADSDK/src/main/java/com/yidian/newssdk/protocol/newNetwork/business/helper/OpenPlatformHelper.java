package com.yidian.newssdk.protocol.newNetwork.business.helper;

import android.text.TextUtils;

import com.yidian.newssdk.NewsFeedsSDK;
import com.yidian.newssdk.data.pref.GlobalConfig;
import com.yidian.newssdk.protocol.newNetwork.RequestManager;
import com.yidian.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;
import com.yidian.newssdk.utils.TimeUtil;

import org.json.JSONObject;

/**
 * @author zhangzhun
 * @date 2018/8/2
 */

public class OpenPlatformHelper {

    private static final String TAG = OpenPlatformHelper.class.getSimpleName();

    private static String sOpenPath;



    public static void getOp(String appid) {
        if (TextUtils.isEmpty(appid)) {
            throw new RuntimeException("SDK appid can not be null or empty");
        }
        RequestManager.requestOpenPlatform(appid, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                String path = response.optString("path");
                if (!TextUtils.isEmpty(path)) {
                    sOpenPath = path;
                }
                saveParams(response);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    public static void saveParams(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        String path = jsonObject.optString("path");
        long systime = jsonObject.optLong("systime", 0);
        if (systime > 0) {
            long diff = TimeUtil.calculateTimeOffset(systime);
            GlobalConfig.saveServerDiffTime(diff);
        }
        if (!TextUtils.isEmpty(path)) {
            GlobalConfig.saveOpParams(path);
        }
    }

    public static String getOpenPath() {
        if (!TextUtils.isEmpty(sOpenPath)) {
            return sOpenPath;
        }
        sOpenPath = GlobalConfig.getOpParams();
        if (TextUtils.isEmpty(sOpenPath)) {
            getOp(NewsFeedsSDK.getInstance().getAppId());
        }
        return sOpenPath;
    }

}
