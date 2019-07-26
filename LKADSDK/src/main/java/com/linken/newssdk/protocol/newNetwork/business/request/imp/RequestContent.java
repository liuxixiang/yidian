package com.linken.newssdk.protocol.newNetwork.business.request.imp;

import com.linken.newssdk.SDKContants;
import com.linken.newssdk.protocol.newNetwork.business.request.RequestBase;
import com.linken.newssdk.utils.SystemUtil;

/**
 * Created by chenyichang on 2018/5/22.
 */

public class RequestContent extends RequestBase {

    private String docid;

    public RequestContent(String docid){
        super();
        this.docid = docid;
    }

    @Override
    protected String getHost() {
        return SDKContants.API_SERVER;
    }

    @Override
    protected String getPath() {
        return "contents/content";
    }

    @Override
    public String getURI() {
        String baseUrl = super.getURI();
        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append("&userInfo=" + SystemUtil.getMd5RealImei());
        builder.append("&docid=" + docid);
        builder.append("&version=" + SDKContants.API_VER);
        return builder.toString();
    }
}

