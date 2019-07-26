package com.linken.newssdk.protocol.newNetwork.core;

import com.linken.newssdk.protocol.newNetwork.business.request.RequestBase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpClient extends SyncHttpClient {
    private final ExecutorService threadPool;

    public AsyncHttpClient() {
        super();
        threadPool = Executors.newCachedThreadPool();
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

    @Override
    public void post(RequestBase requestBase, final ResponseHandler handler) {
        post(requestBase.getURI(), requestBase.getBody(), handler);
    }

    @Override
    public void get(final RequestBase requestBase, final ResponseHandler handler) {
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
