package com.linken.newssdk.protocol.newNetwork.business.request.imp;


import com.linken.newssdk.protocol.newNetwork.business.request.RequestBase;
import com.linken.newssdk.utils.support.ClientInfoHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Created by chenyichang on 2018/5/19.
 */
public class RequestNews extends RequestBase {


    private String action;
    private String channel;
    private int count;
    private String refresh;
    private int history_count;
    private long history_timestamp;

    /*&action=refesh&channel=视频&count=5&history_timestamp=1502073267*/

    /**
     * @param action            或者page_down
     * @param channel           频道信息，如推荐，视频
     * @param count             文章的数目
     * @param refresh           -3屏自动刷新传-3 手动刷新传1 其他页面自动刷新传0
     * @param history_count     客户端当前该频道下的文章数目
     * @param history_timestamp 下拉时，顶部的文章的时间戳，单位为秒；上拉时，底部的文章的时间戳，单位为秒
     */
    public RequestNews(String action, String channel, int count, String refresh, int history_count, long history_timestamp) {
        super();
        this.action = action;
        this.channel = channel;
        this.count = count;
        this.refresh = refresh;
        this.history_count = history_count;
        this.history_timestamp = history_timestamp;
    }

    @Override
    protected String getPath() {
        return "recommend_channel";
    }

    @Override
    public String getURI() {
        String baseUrl = super.getURI();
        StringBuilder builder = new StringBuilder(baseUrl);

        builder.append("&action=" + action);
        try {
            channel = URLEncoder.encode(channel, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        builder.append("&channel=" + channel);
        builder.append("&count=" + count);
        builder.append("&refresh=" + refresh);

        builder.append("&history_count=" +  history_count);
        builder.append("&history_timestamp=" + history_timestamp);
        return builder.toString();
    }

    /*{
"userInfo": {
  "mac": "10:10:10:10:10:10",
  "imei": "102c78e5e4764285ac72096127c88eae",
  "ip": "183.61.126.46",
  "appVersion": "3.1.2",
  "region": "北京市,北京市,海淀区",
  "cityCode": "1",
  "3rd_ad_version": "1.0"
},
"deviceInfo": {
  "screenHeight": 1280,
  "screenWidth": 720,
  "device": "lcsh92_wet_tdd",
  "androidVersion": "4.2.2",
  "network": "wifi"
}
}
*/
    @Override
    public String getBody() {
        return ClientInfoHelper.getClientInfo();
    }
}
