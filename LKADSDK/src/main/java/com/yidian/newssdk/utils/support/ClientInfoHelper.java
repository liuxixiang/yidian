package com.yidian.newssdk.utils.support;

import android.os.Build;

import com.yidian.newssdk.NewsFeedsSDK;
import com.yidian.newssdk.data.pref.GlobalAccount;
import com.yidian.newssdk.SDKContants;
import com.yidian.newssdk.data.pref.GlobalConfig;
import com.yidian.newssdk.protocol.newNetwork.business.helper.Get3rdInfoHelper;
import com.yidian.newssdk.utils.AdUtils;
import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.utils.DensityUtil;
import com.yidian.newssdk.utils.DeviceUtils;
import com.yidian.newssdk.utils.IpUtils;
import com.yidian.newssdk.utils.LocationMgr;
import com.yidian.newssdk.utils.NetUtil;
import com.yidian.newssdk.utils.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhangzhun
 * @date 2018/7/23
 */

public class ClientInfoHelper {

    public static String getClientInfo() {
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("mac", DeviceUtils.getMac(ContextUtils.getApplicationContext()));
            userInfo.put("imei", SystemUtil.getMd5RealImei());
            userInfo.put("ip", IpUtils.getIP());
            userInfo.put("appVersion", SDKContants.SDK_VER);
            userInfo.put("3rd_ad_version", AdUtils.getAdVersion());
            userInfo.put("region", "");
            userInfo.put("cityCode", "");
            userInfo.put("latitude", LocationMgr.getInstance().mLatitude);
            userInfo.put("longitude", LocationMgr.getInstance().mLongitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject deviceInfo = new JSONObject();
        try {
            deviceInfo.put("screenHeight", DensityUtil.getScreenHeight(ContextUtils.getApplicationContext()));
            deviceInfo.put("screenWidth", DensityUtil.getScreenWidth(ContextUtils.getApplicationContext()));
            deviceInfo.put("device", Build.DEVICE);
            deviceInfo.put("androidVersion", DeviceUtils.getOsVer());
            deviceInfo.put("network", NetUtil.getNetTypeString(ContextUtils.getApplicationContext()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Map<String, String> body = new HashMap<>();
//        body.put("userInfo", userInfo.toString());
//        body.put("deviceInfo", deviceInfo.toString());

        JSONObject wrapper = new JSONObject();

        JSONObject clientInfo = new JSONObject();
        try {
            clientInfo.put("userInfo", userInfo);
            clientInfo.put("deviceInfo", deviceInfo);
//            wrapper.put("", c);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringBuilder builder = new StringBuilder();
        builder.append("clientInfo=")
                .append(clientInfo.toString());
        return builder.toString();
    }

    public static String getHybridClientInfo() {
        // ref to EnhancedJsInterface.java
        //  net_type,  font_scale, device_id, distribution_type, distribution, platform, appid,

        JSONObject clientInfo = new JSONObject();

        try {
            // todo: 版本号，发布渠道，etc. AppId等放的位置
            String netType = NetUtil.getNetTypeString(ContextUtils.getApplicationContext());
            clientInfo.put("net_type", netType);
            clientInfo.put("version", SDKContants.SDK_VER);
            clientInfo.put("api_ver", SDKContants.API_VER);
            clientInfo.put("device_name", Build.MODEL);
            clientInfo.put("platform", "1");
            clientInfo.put("appid", NewsFeedsSDK.getInstance().getAppId());
            clientInfo.put("device_id", SystemUtil.getMd5Imei());
            clientInfo.put("imei", SystemUtil.getMd5RealImei());
            clientInfo.put("mac", DeviceUtils.getMac(ContextUtils.getApplicationContext()));
            clientInfo.put("ip", IpUtils.getIP());
            clientInfo.put("region", "");
            clientInfo.put("cityCode", "");
            clientInfo.put("latitude", LocationMgr.getInstance().mLatitude);
            clientInfo.put("longitude", LocationMgr.getInstance().mLongitude);
            clientInfo.put("3rd_ad_version", AdUtils.getAdVersion());
            clientInfo.put("is_night", false);
            clientInfo.put("show_img", "always");
            clientInfo.put("latitude", LocationMgr.getInstance().mLatitude);
            clientInfo.put("longitude", LocationMgr.getInstance().mLongitude);
            clientInfo.put("app_info", Get3rdInfoHelper.get3rdAppInfo());
            clientInfo.put("userid", GlobalAccount.getYduserId());
            clientInfo.put("screenHeight", DensityUtil.getScreenHeight(ContextUtils.getApplicationContext()));
            clientInfo.put("screenWidth", DensityUtil.getScreenWidth(ContextUtils.getApplicationContext()));
            clientInfo.put("device", Build.DEVICE);
            clientInfo.put("androidVersion", Build.VERSION.RELEASE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return clientInfo.toString();
    }
}
