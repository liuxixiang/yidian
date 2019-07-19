package com.yidian.newssdk.core.clean.feeds.data.bean;

import com.yidian.newssdk.core.clean.commmon.bean.UseCaseParams;

/**
 * @author zhangzhun
 * @date 2018/9/15
 */

public class FeedsRequest implements UseCaseParams.Request{
    public String action;
    public String channel;
    public int count;
    public String refresh;
    public int history_count;
    public long history_timestamp;

    public FeedsRequest(String action, String channel, int count, String refresh, int history_count, long history_timestamp) {
        this.action = action;
        this.channel = channel;
        this.count = count;
        this.refresh = refresh;
        this.history_count = history_count;
        this.history_timestamp = history_timestamp;
    }
}
