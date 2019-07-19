package com.yidian.newssdk.protocol.newNetwork;

import android.accounts.Account;
import android.content.Context;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.core.newweb.ThirdPartyManager;
import com.yidian.newssdk.core.newweb.WebAppManager;
import com.yidian.newssdk.data.pref.GlobalAccount;
import com.yidian.newssdk.protocol.newNetwork.business.request.RequestBase;
import com.yidian.newssdk.protocol.newNetwork.business.request.RequestFactory;
import com.yidian.newssdk.protocol.newNetwork.business.request.imp.GetChannelRequest;
import com.yidian.newssdk.protocol.newNetwork.business.request.imp.Request3rdInfo;
import com.yidian.newssdk.protocol.newNetwork.business.request.imp.RequestWebGet;
import com.yidian.newssdk.protocol.newNetwork.business.request.imp.RequestOpParams;
import com.yidian.newssdk.protocol.newNetwork.business.request.imp.RequestWebPost;
import com.yidian.newssdk.protocol.newNetwork.core.AsyncHttpClient;
import com.yidian.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;
import com.yidian.newssdk.protocol.newNetwork.core.ResponseHandler;

import org.json.JSONObject;

/**
 * Created by chenyichang on 2018/5/19.
 */

public class RequestManager {

    private static final String TAG = RequestManager.class.getSimpleName();

    public static void requestOpenPlatform(String appid, final ResponseHandler listener) {
        RequestBase requestBase = new RequestOpParams(appid);
        new AsyncHttpClient().get(requestBase, listener);
    }

    public static void request3rdInfo(String appid, final ResponseHandler listener) {
        RequestBase requestBase = new Request3rdInfo(appid);
        new AsyncHttpClient().get(requestBase, listener);
    }

    public static void requestChannealList(final ResponseHandler listener) {
        RequestBase requestBase = new GetChannelRequest();
        new AsyncHttpClient().get(requestBase, listener);
    }

    public static void requestWebGetContent(String url, boolean isCommentsRequest, final ResponseHandler listener) {
        RequestBase requestBase = new RequestWebGet(url);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        if (isCommentsRequest) {
            httpClient.setCookie(NetworkConstant.COOKIE);
            httpClient.post(requestBase, listener);
        } else {
            if (WebAppManager.getInstance().getThirdPartyManager().isInternalSite(url)) {
                httpClient.setCookie(GlobalAccount.getCookie());
            }
            httpClient.post(requestBase, listener);
        }
    }

    public static void requestWebPostContent(String url, String postBody, boolean needSDKAuth, final ResponseHandler listener) {
        RequestBase requestBase = new RequestWebPost(url, postBody, needSDKAuth);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        if (WebAppManager.getInstance().getThirdPartyManager().isInternalSite(url)) {
            httpClient.setCookie(GlobalAccount.getCookie());
        }
        httpClient.post(requestBase, listener);
    }

    public static void requestNews(String action, String channel,
                                              int count, String refresh, int history_count, long history_timestamp,
                                              final ResponseHandler listener) {

        RequestBase request = RequestFactory.buildNewsReq(action, channel, count, refresh, history_count, history_timestamp);
        new AsyncHttpClient().post(request, listener);

    }


    public static void requestContent(String docid,
                                                final ResponseHandler listener) {
        RequestBase request = RequestFactory.buildContentReq(docid);
        new AsyncHttpClient().get(request, listener);

    }

    public static void requestFeedBack(String docid, String reason) {
        RequestBase request = RequestFactory.buildFeedBackReq(docid, reason);
        new AsyncHttpClient().get(request, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {

            }

            @Override
            public void onFailure(Throwable e) {

            }
        });

    }

    public static void requestRecommend(String docid,
                                                  final ResponseHandler listener) {

        RequestBase request = RequestFactory.buildRecommendReq(docid);
        new AsyncHttpClient().get(request, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {

            }

            @Override
            public void onFailure(Throwable e) {

            }
        });

    }
}
