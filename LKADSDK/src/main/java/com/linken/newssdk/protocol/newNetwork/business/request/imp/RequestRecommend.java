package com.linken.newssdk.protocol.newNetwork.business.request.imp;

import com.linken.newssdk.SDKContants;
import com.linken.newssdk.protocol.newNetwork.business.request.RequestBase;

/**
 * Created by chenyichang on 2018/5/22.
 */

public class RequestRecommend extends RequestBase {

    private String docid;
    private int length;
    private int start;

    public RequestRecommend(String docid, int length, int start){
        super();
        this.docid = docid;
        this.length = length;
        this.start = start;
    }

    @Override
    public String getHost() {
        return SDKContants.API_SERVER;
    }

    @Override
    public String getPath() {
        return "contents/recommend-news";
    }

    @Override
    public String getURI() {
        String baseUrl = super.getURI();
        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append("&sourceId=" + docid);
        builder.append("&docid=" + docid);
        builder.append("&length=" + length);
        builder.append("&start=" + start);
        builder.append("&version=" + SDKContants.API_VER);
        return builder.toString();
    }
}
