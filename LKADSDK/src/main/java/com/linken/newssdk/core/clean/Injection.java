package com.linken.newssdk.core.clean;

import com.linken.newssdk.core.clean.feeds.data.bean.FeedsRequest;
import com.linken.newssdk.core.clean.feeds.data.bean.FeedsResponse;
import com.linken.newssdk.core.clean.feeds.data.repository.BaseChannelRepository;
import com.linken.newssdk.data.channel.YdChannel;

/**
 * @author zhangzhun
 * @date 2018/9/17
 */

public class Injection {

    public static BaseChannelRepository<FeedsRequest, FeedsResponse> provideChannelRepository(YdChannel channel) {
        return new BaseChannelRepository<>(channel);
    }
}
