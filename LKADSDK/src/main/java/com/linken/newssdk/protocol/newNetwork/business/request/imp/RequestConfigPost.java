package com.linken.newssdk.protocol.newNetwork.business.request.imp;

import com.linken.newssdk.SDKContants;
import com.linken.newssdk.protocol.newNetwork.business.request.RequestBaseLinken;


public class RequestConfigPost extends RequestBaseLinken {

    private String paramCodes;

    public RequestConfigPost(String paramCodes) {
        this.paramCodes = paramCodes;
    }

    @Override
    public String getPath() {
        return SDKContants.PATH_CONFIG;
    }


    @Override
    public String getURI() {

        String baseUrl = super.getURI();
        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append("?paramCodes=" + paramCodes);
        builder.append("&configVersion=" + 0);
        return builder.toString();
    }
}
