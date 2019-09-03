package com.linken.newssdk.core.newslist;

import android.content.Context;

import com.linken.newssdk.base.constract.BaseFragmentPresenter;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.channel.YdChannel;
import com.linken.newssdk.toutiao.TTADRefreshList;
import com.linken.newssdk.toutiao.TTAdManagerHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/5/21
 */

public class NewsListPresenter extends BaseFragmentPresenter<NewsListContractView> {

    private Context mContext;
    private ArrayList<Card> mSourceList = new ArrayList<>();
    private YdChannel mChannel;
    private int refreshCount;
    private NewsListContractView mContactView;
    private List<IRefreshList<Card>> mIRefreshLists;


    public NewsListPresenter(Context context, NewsListContractView contactView) {
        super(contactView);
        this.mContext = context;
        this.mContactView = contactView;
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(mContext);
    }

    public ArrayList<Card> getTAdapterItems() {
        return this.mSourceList;
    }

    public void setRefreshCount(int count) {
        refreshCount = count;
    }

    public void init(final YdChannel mChannel) {
        this.mChannel = mChannel;
        mIRefreshLists = new ArrayList<>();
        mIRefreshLists.add(new TTADRefreshList(mContactView));
        mIRefreshLists.add(new NewsRefreshList(mContactView, mChannel, refreshCount));
    }


    /**
     * 第一次懒加载的情况
     */
    public void firstLazyRefresh() {
        if (mIRefreshLists != null && mIRefreshLists.size() > 0) {
            for (IRefreshList<Card> iRefreshList : mIRefreshLists) {
                iRefreshList.firstLazyRefresh();
            }
        }
    }

    /**
     * 下拉刷洗的情况
     */
    public void onRefresh() {
        if (mIRefreshLists != null && mIRefreshLists.size() > 0) {
            for (IRefreshList<Card> iRefreshList : mIRefreshLists) {
                iRefreshList.onRefresh();
            }
        }
    }

    /**
     * 上拉加载更多的情况
     */
    public void onLoadMoreRefresh() {
        if (mIRefreshLists != null && mIRefreshLists.size() > 0) {
            for (IRefreshList<Card> iRefreshList : mIRefreshLists) {
                iRefreshList.onLoadMoreRefresh();
            }
        }
    }

    /**
     * 错误点击重试的情况
     */
    public void onClickErrorRefresh() {
        if (mIRefreshLists != null && mIRefreshLists.size() > 0) {
            for (IRefreshList<Card> iRefreshList : mIRefreshLists) {
                iRefreshList.onClickErrorRefresh();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
