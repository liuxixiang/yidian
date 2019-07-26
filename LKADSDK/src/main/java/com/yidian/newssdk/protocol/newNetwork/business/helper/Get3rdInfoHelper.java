package com.yidian.newssdk.protocol.newNetwork.business.helper;

import android.text.TextUtils;

import com.yidian.newssdk.NewsFeedsSDK;
import com.yidian.newssdk.data.pref.GlobalConfig;
import com.yidian.newssdk.protocol.newNetwork.RequestManager;
import com.yidian.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;
import com.yidian.newssdk.utils.RefreshControlUtils;

import org.json.JSONObject;

/**
 * @author zhangzhun
 * @date 2018/8/14
 */

public class Get3rdInfoHelper {

    private static String sAppInfo;

    public static void request3rdInfo(String appid) {
        if (TextUtils.isEmpty(appid)) {
            throw new RuntimeException("SDK appid can not be null or empty");
        }

        RequestManager.request3rdInfo(appid, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                parseJson(response);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
        RefreshControlUtils.saveLastOperationTime(RefreshControlUtils.OPERATION.CHECK_GET3RD_INFO);
    }

    private static void parseJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        JSONObject appInfo = jsonObject.optJSONObject("app_info");
        GlobalConfig.saveAppInfo(appInfo.toString());
    }

    public static String get3rdAppInfo() {
        if (TextUtils.isEmpty(sAppInfo)) {
            sAppInfo = GlobalConfig.getAppInfo();
        }
        return sAppInfo;
    }

    public static void onFragmentOnResume() {
        if (RefreshControlUtils.checkNeedUpdate(RefreshControlUtils.OPERATION.CHECK_GET3RD_INFO, false)) {
            request3rdInfo(NewsFeedsSDK.getInstance().getAppId());
        }
    }

}
