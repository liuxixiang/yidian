package com.yidian.newssdk.core.clean;

import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsRequest;
import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsResponse;
import com.yidian.newssdk.core.clean.feeds.data.repository.BaseChannelRepository;
import com.yidian.newssdk.data.channel.YdChannel;

/**
 * @author zhangzhun
 * @date 2018/9/17
 */

public class Injection {

    public static BaseChannelRepository<FeedsRequest, FeedsResponse> provideChannelRepository(YdChannel channel) {
        return new BaseChannelRepository<>(channel);
    }
}
