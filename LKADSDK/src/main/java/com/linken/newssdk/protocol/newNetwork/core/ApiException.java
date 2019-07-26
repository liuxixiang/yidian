package com.linken.newssdk.protocol.newNetwork.core;

/**
 * @author zhangzhun
 * @date 2018/8/10
 */

public class ApiException extends Exception{
    private int errorCode;
    private String reaseon;

    public ApiException(int errorCode, String reaseon) {
        this.errorCode = errorCode;
        this.reaseon = reaseon;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getReaseon() {
        return reaseon;
    }
}
