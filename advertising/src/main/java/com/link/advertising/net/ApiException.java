package com.link.advertising.net;


public class ApiException extends Exception {
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
