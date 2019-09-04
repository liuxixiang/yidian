package com.linken.newssdk.protocol.newNetwork.core;

import com.linken.newssdk.protocol.newNetwork.business.request.IRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SyncHttpClient {
    public static final String DEFAULT_USER_AGENT = "AsyncLiteHttp/1.3";
    public static final String UTF8 = "utf-8";
    private int connectionTimeout = 30000;
    private int dataRetrievalTimeout = 30000;
    private boolean followRedirects = true;
    private Map<String, String> headers;

    public SyncHttpClient() {
        headers = Collections.synchronizedMap(new LinkedHashMap<String, String>());
//        setUserAgent(DEFAULT_USER_AGENT);
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getDataRetrievalTimeout() {
        return dataRetrievalTimeout;
    }

    public void setDataRetrievalTimeout(int dataRetrievalTimeout) {
        this.dataRetrievalTimeout = dataRetrievalTimeout;
    }

    public boolean getFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public String getUserAgent() {
        return headers.get("User-Agent");
    }

    public void setUserAgent(String userAgent) {
        headers.put("User-Agent", userAgent);
    }

    public void setCookie(String cookie) {
        headers.put("Cookie", cookie);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void removeHeader(String name) {
        headers.remove(name);
    }

    private HttpURLConnection buildURLConnection(String url, Method method) throws IOException {
        URL resourceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();

        // Settings
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(dataRetrievalTimeout);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(followRedirects);
        connection.setRequestMethod(method.toString());
        connection.setDoInput(true);

        // Headers
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty( header.getKey(), header.getValue());
        }
        return connection;
    }

    private byte[] encodeParameters(String map) {
        if (map == null) {
            map = "";
        }
        try {
            return map.toString().getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + UTF8, e);
        }
    }

    public void get(final String url, final ResponseHandler handler) {
        request(url, Method.GET, null, handler);
    }


    public void post(IRequest requestBase, final ResponseHandler handler) {
        request(requestBase.getURI(), Method.POST, requestBase.getBody(), handler);
    }

    public void get(final IRequest requestBase, final ResponseHandler handler) {
        request(requestBase.getURI(), Method.GET, null, handler);
    }


    public void post(final String url, final String body, final ResponseHandler handler) {
        request(url, Method.POST, body, handler);
    }

    void request(final String url, final Method method, final String body,
                 final ResponseHandler handler) {
        HttpURLConnection connection = null;
        try {
            connection = buildURLConnection(url, method);

            // Request start
            handler.sendStartMessage();

            if (method == Method.POST) {
                // Send content as form-urlencoded
                byte[] content = encodeParameters(body);
                connection.setRequestProperty("Content-Type", "application/json;charset=" + UTF8);
                connection.setRequestProperty("Content-Length", Long.toString(content.length));

                // Stream the data so we don't run out of memory
                connection.setFixedLengthStreamingMode(content.length);
                OutputStream os = connection.getOutputStream();
                os.write(content);
                os.flush();
                os.close();
            }

            // Process the response in the handler because it can be done in different ways
            handler.processResponse(connection);

        } catch (Exception e) {
            handler.sendFailureMessage(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
            // Request finished
            handler.sendFinishMessage();
        }
    }

    public void addHeaders(Map<String, String> headers) {
        if(headers != null && headers.size() > 0) {
            this.headers.putAll(headers);
        }
    }
}
