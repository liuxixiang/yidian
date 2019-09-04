package com.linken.newssdk.protocol.newNetwork.business.request;

import java.util.Map;

public interface IRequest {
    String getHost();

    String getPath();

    String getURI();

    String getBody();

    Map<String, String> getHeaders();
}
