package com.linken.newssdk.protocol.newNetwork.business.request;

import com.linken.newssdk.BuildConfig;
import com.linken.newssdk.SDKContants;

import java.util.Map;

/**
 * @author zhangzhun
 * @date 2018/7/23
 */

public abstract class RequestBaseLinken implements IRequest {

    public RequestBaseLinken() {
    }

    @Override
    public String getHost() {
        return BuildConfig.BASE_URL;
    }

    @Override
    public String getURI() {
        StringBuilder builder = new StringBuilder();
        builder.append(getHost());
        builder.append(getPath());
        builder.append("?appId=" + SDKContants.LK_APPID);
        builder.append("&osType=" + "android");
        builder.append("&appVersion=" + BuildConfig.VERSION_NAME);
        return builder.toString();
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }
}
