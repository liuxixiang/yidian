package com.linken.newssdk.exportui;

import android.os.Bundle;
import android.text.TextUtils;

import com.linken.newssdk.data.channel.YdChannel;

/**
 * @author zhangzhun
 * @date 2018/9/29
 */

public class NewsListForViewPagerFragment extends NewsListFragment{



    public static NewsListForViewPagerFragment newInstance(String channelName) {
        NewsListForViewPagerFragment fragment = new NewsListForViewPagerFragment();
        Bundle bundle = new Bundle();
        YdChannel channel= new YdChannel();
        if (!TextUtils.isEmpty(channelName)) {
            channel.setChannelName(channelName);
        } else {
            channel.setChannelName("推荐");
        }
        bundle.putSerializable("channelInfo", channel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected boolean inViewPager() {
        return true;
    }
}
