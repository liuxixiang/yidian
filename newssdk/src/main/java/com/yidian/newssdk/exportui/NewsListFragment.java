package com.yidian.newssdk.exportui;

import android.os.Bundle;
import android.text.TextUtils;

import com.yidian.newssdk.core.newslist.NewsInnerListFragment;
import com.yidian.newssdk.data.channel.YdChannel;
import com.yidian.newssdk.export.IExposeInterface;
import com.yidian.newssdk.protocol.newNetwork.business.helper.Get3rdInfoHelper;
import com.yidian.newssdk.utils.LocationMgr;

/**
 * @author zhangzhun
 * @date 2018/5/30
 */

public class NewsListFragment extends NewsInnerListFragment {

    private boolean mInViewPager;

    public static NewsListFragment newInstance(String channelName, boolean inViewPager) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        YdChannel channel= new YdChannel();
        if (!TextUtils.isEmpty(channelName)) {
            channel.setChannelName(channelName);
        } else {
            channel.setChannelName("推荐");
        }
        bundle.putBoolean("inViewPager", inViewPager);
        bundle.putSerializable("channelInfo", channel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (null != bundle) {
            this.mInViewPager = bundle.getBoolean("inViewPager", false);
        }
        LocationMgr.getInstance().getLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        Get3rdInfoHelper.onFragmentOnResume();
    }

    @Override
    protected boolean inViewPager() {
        return mInViewPager;
    }

    @Override
    public void refreshCurrentChannel() {
        super.refreshCurrentChannel();
    }

    @Override
    public void scrollToTopPosition() {
        super.scrollToTopPosition();
    }

    @Override
    public boolean isScrollToTopPosition() {
        return super.isScrollToTopPosition();
    }
}
