package com.linken.newssdk.protocol.newNetwork.business.request.imp;

import com.linken.newssdk.SDKContants;
import com.linken.newssdk.protocol.newNetwork.business.request.RequestBase;

/**
 * @author zhangzhun
 * @date 2018/8/14
 */

public class Request3rdInfo extends RequestBase{

    private String mAppid;

    public Request3rdInfo(String mAppid) {
        this.mAppid = mAppid;
    }


    @Override
    protected String getPath() {
        return null;
    }

    @Override
    public String getURI() {
        StringBuilder stringBuilder = new StringBuilder(SDKContants.URL_3RD_INFO);
        stringBuilder.append("?appid=")
                .append(mAppid);
        return stringBuilder.toString();
    }
}
