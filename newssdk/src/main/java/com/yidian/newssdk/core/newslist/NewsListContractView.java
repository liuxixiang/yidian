package com.yidian.newssdk.core.newslist;

import com.yidian.newssdk.base.constract.BaseContractView;

import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/5/21
 */

public interface NewsListContractView extends BaseContractView {

    void setRefeshEnable(boolean enable);

    void loadMoreFailed();

    void noLoadMore();

    void setLoadMoreEnable(boolean enable);

    void handleRefreshTab(int count);

    void handleNewsResultRefresh(List newsResult);

    void handleNewsLoadMore(List newsResult);

    void handleAllNews(boolean isLoadMore, List newsResult);

    void showRefreshTip(String refreshErrorTip);

    boolean isVisableToUser();

    void onShowError(String errorTip);

}
