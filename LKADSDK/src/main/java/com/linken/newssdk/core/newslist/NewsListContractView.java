package com.linken.newssdk.core.newslist;


import com.linken.newssdk.base.constract.BaseContractView;
import com.linken.newssdk.data.card.base.Card;

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

    void handleAllNews(boolean isLoadMore, List allResult);

    void handleTopResultList(List cards);

    void showRefreshTip(String refreshErrorTip);

    boolean isVisableToUser();

    void onShowError(String errorTip);

    void handleNewsAdResult(int adTypeTt, List<Card> cards);
}
