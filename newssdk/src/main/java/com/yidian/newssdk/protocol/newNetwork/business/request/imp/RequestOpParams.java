package com.yidian.newssdk.protocol.newNetwork.business.request.imp;

import com.yidian.newssdk.SDKContants;
import com.yidian.newssdk.protocol.newNetwork.business.request.RequestBase;

/**
 * @author zhangzhun
 * @date 2018/8/2
 */

public class RequestOpParams extends RequestBase {

    private String mAppid;

    public RequestOpParams(String mAppid) {
        this.mAppid = mAppid;
    }

    @Override
    protected String getPath() {
        return "get_root_path";
    }

    @Override
    protected String getHost() {
        return SDKContants.URL_GET_OP;
    }

    @Override
    public String getURI() {
        StringBuilder builder = new StringBuilder();
        builder.append(getHost());
        builder.append(getPath());
        builder.append("?appid=" + mAppid);
        return builder.toString();
    }
}
