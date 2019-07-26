package com.linken.newssdk.exportui;

import android.os.Bundle;

import com.linken.newssdk.core.embed.NewsInnerEmbedFragment;
import com.linken.newssdk.data.channel.YdChannel;

/**
 * Created by chenyichang on 2018/5/31.
 *
 * 以view 方式输出
 */

public class NewsEmbedFragment extends NewsInnerEmbedFragment{


    public static NewsEmbedFragment newInstance(String channelName, int count) {
        NewsEmbedFragment fragment = new NewsEmbedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("channelName", channelName);
        bundle.putSerializable("newsCount", count);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        String channelName = "推荐";
        if (null != bundle) {
            channelName = getArguments().getString("channelName");
        } else {
            bundle = new Bundle();
        }
        YdChannel channel = new YdChannel(channelName);
        bundle.putSerializable("channelInfo", channel);
        setArguments(bundle);
        super.onCreate(savedInstanceState);
    }

}
