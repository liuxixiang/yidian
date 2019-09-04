package com.linken.newssdk.protocol.newNetwork.business.request.imp;

import com.linken.newssdk.SDKContants;
import com.linken.newssdk.protocol.newNetwork.business.request.RequestBaseLinken;

import java.util.ArrayList;
import java.util.List;


public class RequestConfigPost extends RequestBaseLinken {

    private String paramCodes;
    private List<String> codeList = new ArrayList<>();

    public RequestConfigPost(String paramCodes) {
        this.paramCodes = paramCodes;
        codeList.add(paramCodes);
    }

    public RequestConfigPost(String[] codes) {
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
        builder.append("?paramCodes=" + paramCodes);
        builder.append("&configVersion=" + 0);
        return builder.toString();
    }
}
