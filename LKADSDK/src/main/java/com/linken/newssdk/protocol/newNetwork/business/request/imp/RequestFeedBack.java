package com.linken.newssdk.protocol.newNetwork.business.request.imp;

import android.text.TextUtils;

import com.linken.newssdk.data.pref.GlobalDataCache;
import com.linken.newssdk.protocol.newNetwork.business.request.RequestBase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by chenyichang on 2018/5/22.
 */

public class RequestFeedBack extends RequestBase {

    private String docid;
    private String reason;

    public RequestFeedBack(String docid, String reason){
        super();
        this.docid = docid;
        this.reason = reason;
    }

    @Override
    public String getPath() {
        return "dislike_news";
    }

    @Override
    public String getURI() {
        String baseUrl = super.getURI();
        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append("&docid=" + docid);
        builder.append("&yd_userid=" + GlobalDataCache.getInstance().getActiveAccount().getUserid());

        if (TextUtils.isEmpty(reason)) {
            builder.append("&reason=" + "");
        } else {
            try {
                reason = URLEncoder.encode(reason, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            builder.append("&reason=" + reason);
        }

        return builder.toString();
    }
}
