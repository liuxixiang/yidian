package com.linken.newssdk.core.newslist;

import java.util.List;

/**
 * 加载广告操作
 */
public interface IRefreshList<T>  {

    /**
     * 第一次懒加载的情况
     */
    void firstLazyRefresh();

    /**
     * 下拉刷洗的情况
     */
    void onRefresh();

    /**
     * 上拉加载更多的情况
     */
    void onLoadMoreRefresh();

    /**
     * 错误点击重试的情况
     */
    void onClickErrorRefresh();

    List<T> getTAdapterItems();
}
