package com.linken.newssdk.protocol.newNetwork.business.request.imp;

import android.text.TextUtils;

import com.linken.newssdk.SDKContants;
import com.linken.newssdk.protocol.newNetwork.business.request.RequestBaseLinken;

import java.util.ArrayList;
import java.util.List;


public class RequestConfig extends RequestBaseLinken {

    private String paramCodes;
    private String channel;
    private List<String> codeList = new ArrayList<>();

    public RequestConfig(String paramCodes) {
        this.paramCodes = paramCodes;
        codeList.add(paramCodes);
    }

    public RequestConfig(String paramCodes, String channel) {
        this.paramCodes = paramCodes;
        this.channel = channel;
        codeList.add(paramCodes);
    }

    public RequestConfig(String[] codes) {
        if (codes == null || codes.length == 0) {
            return;
        }
        String code = "";
        for (int i = 0; i < codes.length; i++) {
            codeList.add(codes[i]);
            if (i < codes.length - 1) {
                code = code + codes[i] + ",";
            } else {
                code = code + codes[i];
            }

        }
        this.paramCodes = code;
    }

    public List<String> getCodes() {
        return codeList;
    }

    @Override
    public String getPath() {
        return SDKContants.PATH_CONFIG;
    }


    @Override
    public String getURI() {

        String baseUrl = super.getURI();
        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append("&paramCodes=" + paramCodes);
        if (!TextUtils.isEmpty(channel)) {
            builder.append("&channel=" + channel);
        }
        builder.append("&configVersion=" + 0);
        return builder.toString();
    }
}
