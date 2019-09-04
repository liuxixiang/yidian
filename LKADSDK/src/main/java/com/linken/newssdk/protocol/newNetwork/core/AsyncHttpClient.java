package com.linken.newssdk.protocol.newNetwork.core;

import com.linken.newssdk.protocol.newNetwork.business.request.IRequest;

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
    public void post(IRequest requestBase, final ResponseHandler handler) {
        addHeaders(requestBase.getHeaders());
        post(requestBase.getURI(), requestBase.getBody(), handler);
    }

    @Override
    public void get(final IRequest requestBase, final ResponseHandler handler) {
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
