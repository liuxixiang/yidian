package com.link.advertising.net;

import com.link.advertising.SDKContants;

import java.util.Map;


public abstract class RequestBase {

    protected String getHost() {
        return SDKContants.BASE_URL;
    }

    protected abstract String getPath();

    protected String getURI() {
        StringBuilder builder = new StringBuilder();
        builder.append(getHost());
        builder.append(getPath());
        return builder.toString();
    }

    protected Map<String, String> getHeaders() {
        return null;
    }

    protected String getBody() {
        return null;
    }

}
