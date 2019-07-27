package com.link.advertising.net;


import com.link.advertising.SDKContants;

import java.util.Map;

public abstract class RequestBase {

    protected String getHost() {
        return SDKContants.BASE_URL;
    }

    protected String getURI() {
        return null;
    }

    public Map<String, String> getHeaders() {
        return null;
    }

    public String getBody() {
        return null;
    }

}
