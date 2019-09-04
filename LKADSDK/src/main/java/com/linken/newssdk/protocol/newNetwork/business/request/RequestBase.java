package com.linken.newssdk.protocol.newNetwork.business.request;

import android.text.TextUtils;

import com.linken.newssdk.NewsFeedsSDK;
import com.linken.newssdk.SDKContants;
import com.linken.newssdk.protocol.newNetwork.business.helper.OpenPlatformHelper;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.EncryptUtil;
import com.linken.newssdk.utils.NetUtil;
import com.linken.newssdk.utils.SystemUtil;
import com.linken.newssdk.utils.TimeUtil;

import java.util.Map;


/**
 * Created by chenyichang on 2018/5/19.
 */
public abstract class RequestBase implements IRequest {

    @Override
    public String getHost() {
        if (!TextUtils.isEmpty(SDKContants.URL_HOST) && !TextUtils.equals("/", SDKContants.URL_HOST)) {
            return SDKContants.URL_HOST;
        } else {
            SDKContants.URL_HOST = OpenPlatformHelper.getOpenPath() + "/";
            return OpenPlatformHelper.getOpenPath() + "/";
        }
    }

    @Override
    public abstract String getPath();

    /*appid=mCTffjSnTKhm9ONZn-DL4g2x
    &nonce=qbcJbQbO
    &timestamp=1502073267
    &secretkey=253188f71e97be7888e8f6be579d0405aa9004c9
    &3rd_userid=adsfjakldjfaklfdjklasfdf
    &action=refesh&channel=视频&count=5&history_timestamp=1502073267
    &version=020117
    &net=wifi
    &history_count=0'
*/
    //加一些公共参数
    @Override
    public String getURI() {

        StringBuilder builder = new StringBuilder();
        builder.append(getHost());
        builder.append(getPath());
        long timestamp = TimeUtil.convertToServerTimeMillis(System.currentTimeMillis()) / 1000;
        builder.append("?appid=" + NewsFeedsSDK.getInstance().getAppId());
        builder.append("&timestamp=" + timestamp);
        builder.append("&nonce=nTKhmm9ON");
        builder.append("&secretkey=" + getSecretkey(timestamp, "nTKhmm9ON"));
        builder.append("&3rd_userid=" + SystemUtil.getMd5RealImei());
        builder.append("&version=" + SDKContants.API_VER);
        builder.append("&net=" + NetUtil.getNetTypeString(ContextUtils.getApplicationContext()));
        builder.append("&platform=android");
        builder.append("&cv=" + SDKContants.SDK_VER);
        return builder.toString();
    }

    protected String getSecretkey(long timestamp, String nTKhmm9ON) {
        return EncryptUtil.SHA1(EncryptUtil.getMD5_32(NewsFeedsSDK.getInstance().getAppKey()) + nTKhmm9ON + timestamp);
    }

    /*{
  "userInfo": {
    "mac": "10:10:10:10:10:10",
    "imei": "102c78e5e4764285ac72096127c88eae",
    "ip": "183.61.126.46",
    "appVersion": "3.1.2",
    "region": "北京市,北京市,海淀区",
    "cityCode": "1",
    "3rd_ad_version": "1.0"
  },
  "deviceInfo": {
    "screenHeight": 1280,
    "screenWidth": 720,
    "device": "lcsh92_wet_tdd",
    "androidVersion": "4.2.2",
    "network": "wifi"
  }
}
*/
    @Override
    public String getBody() {
        return null;
    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }
}
