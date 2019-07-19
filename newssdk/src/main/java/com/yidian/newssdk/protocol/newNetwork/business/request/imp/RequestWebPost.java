package com.yidian.newssdk.protocol.newNetwork.business.request.imp;

import com.yidian.newssdk.NewsFeedsSDK;
import com.yidian.newssdk.SDKContants;
import com.yidian.newssdk.protocol.newNetwork.business.request.RequestBase;
import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.utils.NetUtil;
import com.yidian.newssdk.utils.SystemUtil;
import com.yidian.newssdk.utils.TimeUtil;

/**
 * @author zhangzhun
 * @date 2018/7/23
 */

public class RequestWebPost extends RequestBase {

    private String mOriginUrl;
    private String mOriginPostBody;
    private boolean mNeedSDKAuth;

    public RequestWebPost(String originRequest, String originPostBody) {
        this.mOriginUrl = originRequest;
        this.mOriginPostBody = originPostBody;
    }

    public RequestWebPost(String originRequest, String originPostBody, boolean needSDKAuth) {
        this.mOriginUrl = originRequest;
        this.mOriginPostBody = originPostBody;
        this.mNeedSDKAuth = needSDKAuth;
    }

    @Override
    protected String getPath() {
        return null;
    }

    @Override
    public String getBody() {
        return mOriginPostBody;
    }

    @Override
    public String getURI() {
        StringBuilder builder = new StringBuilder(mOriginUrl);
        if (mNeedSDKAuth) {
            if (mOriginUrl.lastIndexOf("?") != -1) {
                builder.append("&appid=" + NewsFeedsSDK.getInstance().getAppId());
            } else {
                builder.append("?appid=" + NewsFeedsSDK.getInstance().getAppId());
            }
            builder.append("&cv=" + SDKContants.SDK_VER);
            long timestamp = TimeUtil.convertToServerTimeMillis(System.currentTimeMillis()) / 1000;
            builder.append("&timestamp=" + timestamp);
            builder.append("&nonce=nTKhmm9ON");
            builder.append("&secretkey=" + getSecretkey(timestamp, "nTKhmm9ON"));
            builder.append("&3rd_userid=" + SystemUtil.getMd5RealImei());
            builder.append("&version=" + SDKContants.API_VER);
            builder.append("&net=" + NetUtil.getNetTypeString(ContextUtils.getApplicationContext()));
            builder.append("&platform=1");
        }
        return builder.toString();
    }
}
