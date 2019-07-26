package com.yidian.newssdk.protocol.newNetwork.business.request.imp;

import com.yidian.newssdk.protocol.newNetwork.business.request.RequestBase;

/**
 * @author zhangzhun
 * @date 2018/8/8
 */

public class RequestThirdAD extends RequestAdReport{

    private String mFullUrl;



    public RequestThirdAD(String mFullUrl) {
        this.mFullUrl = mFullUrl;
    }

    @Override
    protected String getPath() {
        return null;
    }

    @Override
    public String getURI() {
        return mFullUrl;
    }


}
