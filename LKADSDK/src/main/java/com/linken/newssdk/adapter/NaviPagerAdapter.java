package com.linken.newssdk.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.linken.newssdk.core.newslist.IRefreshableNewsList;
import com.linken.newssdk.data.channel.YdChannel;
import com.linken.newssdk.core.newslist.NewsInnerListFragment;

import java.util.List;


public class NaviPagerAdapter extends GeekFragmentStatePagerAdapter {

    private Context mContext;
    private List<YdChannel> mDataList;
    private IRefreshableNewsList mCurrentFragment;

    public NaviPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public NaviPagerAdapter(FragmentManager fm, Context mContext, List<YdChannel> mDataList) {
        super(fm);
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public Fragment getItem(int position) {
        if (position < 0 || position >= getCount()) {
            return null;
        }
        NewsInnerListFragment fragment = NewsInnerListFragment.newInstance(position, (YdChannel) this.mDataList.get(position));
        return fragment;
    }

    public YdChannel getItemData(int position) {
        return (YdChannel) this.mDataList.get(position);
    }

    @Override
    public int getCount() {
        return this.mDataList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((YdChannel)this.mDataList.get(position)).getChannelName();
    }

    public IRefreshableNewsList getListFragment(int position) {
        if (position < 0 || position >= getCount()) {
            return null;
        }
        Fragment fragment = mFragments.get(position);
        if (fragment instanceof IRefreshableNewsList) {
            return (IRefreshableNewsList) fragment;
        }
        return null;

    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentFragment = (IRefreshableNewsList) object;
        super.setPrimaryItem(container, position, object);
    }

    public IRefreshableNewsList getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
