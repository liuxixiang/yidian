package com.linken.newssdk.protocol.newNetwork.business.request;


import com.linken.newssdk.protocol.newNetwork.business.request.imp.RequestContent;
import com.linken.newssdk.protocol.newNetwork.business.request.imp.RequestFeedBack;
import com.linken.newssdk.protocol.newNetwork.business.request.imp.RequestNews;
import com.linken.newssdk.protocol.newNetwork.business.request.imp.RequestRecommend;


/**
 * Created by chenyichang on 2018`/5/19.
 */
public class RequestFactory {

    public static RequestBase buildNewsReq(String action, String channel, int count, String refresh, int history_count, long history_timestamp) {
        return new RequestNews(action, channel, count, refresh, history_count, history_timestamp);
    }

    public static RequestBase buildContentReq(String docid) {
        return new RequestContent(docid);
    }

    public static RequestBase buildRecommendReq(String docid) {
        return new RequestRecommend(docid, 10, 0);
    }

    public static RequestBase buildFeedBackReq(String docid, String reason) {
        return new RequestFeedBack(docid, reason);
    }
}
