package com.link.advertising.net;


import com.link.advertising.AdvertisingSDK;
import com.link.advertising.utils.EncryptUtil;
import com.link.advertising.utils.SystemUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpClient extends SyncHttpClient {
    private final ExecutorService threadPool;

    public AsyncHttpClient() {
        super();
        threadPool = Executors.newCachedThreadPool();
        addHeaders();
    }

    /**
     * 公用的头文件
     */
    private void addHeaders() {
        addHeader("osType", "andorid");
        addHeader("appId", AdvertisingSDK.getInstance().getAppId());
        addHeader("deviceId", SystemUtil.generateFakeImei());
        addHeader("thirdUid", EncryptUtil.getMD5_32(SystemUtil.generateFakeImei() + AdvertisingSDK.getInstance().getAppKey()));
    }

    @Override
    public void get(final String url, final ResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                request(url, Method.GET, null, handler);
            }
        });
    }

    public void post(RequestBase requestBase, final ResponseHandler handler) {
        addHeaders(requestBase.getHeaders());
        post(requestBase.getURI(), requestBase.getBody(), handler);
    }

    public void get(final RequestBase requestBase, final ResponseHandler handler) {
        addHeaders(requestBase.getHeaders());
        get(requestBase.getURI(), handler);
    }

    @Override
    public void post(final String url, final String map, final ResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                request(url, Method.POST, map, handler);
            }
        });
    }
}
