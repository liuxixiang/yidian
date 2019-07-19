package com.yidian.newssdk.protocol.newNetwork.business.request.imp;

import com.yidian.newssdk.SDKContants;
import com.yidian.newssdk.protocol.newNetwork.business.request.RequestBase;

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
    protected String getHost() {
        return SDKContants.API_SERVER;
    }

    @Override
    protected String getPath() {
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
