package com.yidian.newssdk.core.newslist;

import android.content.Context;
import android.util.Log;

import com.yidian.newssdk.YdCustomConfigure;
import com.yidian.newssdk.base.constract.BaseFragmentPresenter;
import com.yidian.newssdk.core.clean.Injection;
import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsRequest;
import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsResponse;
import com.yidian.newssdk.core.clean.commmon.UseCase;
import com.yidian.newssdk.core.clean.feeds.domain.usecase.LoadMoreUseCase;
import com.yidian.newssdk.core.clean.feeds.domain.usecase.ReadCacheUseCase;
import com.yidian.newssdk.core.clean.feeds.domain.usecase.RefreshUseCase;
import com.yidian.newssdk.core.newslist.helper.RefreshErrorCodeHelper;
import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.channel.YdChannel;
import com.yidian.newssdk.utils.RefreshControlUtils;
import com.yidian.newssdk.utils.TimeUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/5/21
 */

public class NewsListPresenter extends BaseFragmentPresenter<NewsListContractView> {

    private Context mContext;
    private ArrayList<Card> mNewsSourceList = new ArrayList<>();
    private YdChannel mChannel;
    private long topArticleTimestamp = System.currentTimeMillis() / 1000;
    private long bottomArticleTimestamp;
    private int refreshCount;

    private RefreshUseCase<FeedsRequest, FeedsResponse> refreshUseCase;
    private LoadMoreUseCase<FeedsRequest, FeedsResponse> loadMoreUseCase;
    private ReadCacheUseCase<FeedsRequest, FeedsResponse> readCacheUseCase;

    public NewsListPresenter(NewsListContractView mContactView) {
        super(mContactView);
    }

    public ArrayList<Card> getTAdapterItems() {
        return this.mNewsSourceList;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setRefreshCount(int count) {
        refreshCount = count;
    }

    public void init(final YdChannel mChannel) {
        this.mChannel = mChannel;
        refreshUseCase = new RefreshUseCase<>(Injection.provideChannelRepository(mChannel));
        readCacheUseCase = new ReadCacheUseCase<>(Injection.provideChannelRepository(mChannel));
        loadMoreUseCase = new LoadMoreUseCase<>(Injection.provideChannelRepository(mChannel));
    }

    private UseCase.Callback<FeedsResponse> firstRefreshCallBack = new UseCase.Callback<FeedsResponse>() {
        @Override
        public void onSuccess(FeedsResponse response) {
            if (response != null && response.getResult() != null && response.getResult().size() > 0) {
                handleNewsResult(response);
            } else if (mNewsSourceList.size() == 0) {
                mContactView.onShowEmpty();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            mContactView.showRefreshTip(RefreshErrorCodeHelper.code2String(throwable, false));
            mContactView.onShowError(RefreshErrorCodeHelper.code2String(throwable, true));
        }
    };

    /**
     * 第一次懒加载的情况
     */
    public void firstLazyRefresh() {
        final FeedsRequest firstFetchRequest;
        firstFetchRequest = new FeedsRequest("refresh", mChannel.getChecksumName(),
                refreshCount != 0 ? refreshCount : YdCustomConfigure.getInstance().getRefreshCount(),
                "0", mNewsSourceList.size(), topArticleTimestamp);

        readCacheUseCase.execute(firstFetchRequest, new UseCase.Callback<FeedsResponse>() {

            @Override
            public void onSuccess(FeedsResponse response) {
                if (response != null && response.getResult() != null && response.getResult().size() > 0) {
                    handleNewsCache(response.getResult());
                }
                Log.d("NewsInnerListFragment", "firstLazyRefresh() enter!");

                if (RefreshControlUtils.checkIndividualChannelNeedUpdate(mChannel.getChannelName(), true)) {
                    Log.d("NewsInnerListFragment", "checkIndividualChannelNeedUpdate() enter!");
                    refreshUseCase.execute(firstFetchRequest, firstRefreshCallBack);
                    RefreshControlUtils.saveIndividualChannelNeedUpdate(YdChannel.getChannelName(mChannel.getChannelName()));
                } else if ((response == null || response.getResult() == null || response.getResult().size() == 0)) {
                    Log.d("NewsInnerListFragment", "checkIndividualChannelNeedUpdate() enter2!");
                    refreshUseCase.execute(firstFetchRequest, firstRefreshCallBack);
                    RefreshControlUtils.saveIndividualChannelNeedUpdate(YdChannel.getChannelName(mChannel.getChannelName()));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("NewsInnerListFragment", "readCacheUseCase onError");
                Log.d("NewsInnerListFragment", "throwable is " + throwable.getMessage());

            }
        });
    }

    /**
     * 下拉刷洗的情况
     */
    public void onRefresh() {
        FeedsRequest pull2RefreshRequest;
        pull2RefreshRequest = new FeedsRequest("refresh", mChannel.getChecksumName(),
                refreshCount != 0 ? refreshCount : YdCustomConfigure.getInstance().getRefreshCount(),
                "1", mNewsSourceList.size(), topArticleTimestamp);

        refreshUseCase.execute(pull2RefreshRequest, new UseCase.Callback<FeedsResponse>() {
            @Override
            public void onSuccess(FeedsResponse response) {
                handleNewsResult(response);
            }

            @Override
            public void onError(Throwable throwable) {
                mContactView.showRefreshTip(RefreshErrorCodeHelper.code2String(throwable, false));
                if (mNewsSourceList.size() == 0) {
                    mContactView.onShowError();
                }
            }
        });
    }

    /**
     * 上拉加载更多的情况
     */
    public void onLoadMoreRefresh() {
        FeedsRequest loadMoreRefreshRequest;

        loadMoreRefreshRequest = new FeedsRequest("page_down", mChannel.getChecksumName(),
                refreshCount != 0 ? refreshCount : YdCustomConfigure.getInstance().getRefreshCount(), "1", mNewsSourceList.size(),
                bottomArticleTimestamp);

        loadMoreUseCase.execute(loadMoreRefreshRequest, new UseCase.Callback<FeedsResponse>() {
            @Override
            public void onSuccess(FeedsResponse feedsResponse) {
                handleLoadMoreResult(feedsResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                mContactView.setRefeshEnable(false);
                mContactView.loadMoreFailed();
            }
        });
    }

    /**
     * 错误点击重试的情况
     */
    public void onClickErrorRefresh() {
        FeedsRequest clickErrorRequest;
        clickErrorRequest = new FeedsRequest("refresh", mChannel.getChecksumName(),
                refreshCount != 0 ? refreshCount : YdCustomConfigure.getInstance().getRefreshCount(),
                "0", mNewsSourceList.size(), topArticleTimestamp);
        refreshUseCase.execute(clickErrorRequest, new UseCase.Callback<FeedsResponse>() {
            @Override
            public void onSuccess(FeedsResponse response) {
                handleNewsResult(response);
            }

            @Override
            public void onError(Throwable throwable) {
                mContactView.onShowError(RefreshErrorCodeHelper.code2String(throwable, true));
            }
        });
    }

    /**
     * 数据处理部分：包含下拉刷新、点击重试
     * @param response
     */
    private void handleNewsResult(FeedsResponse response) {
        mContactView.setLoadMoreEnable(true);
        List<Card> cardList = response.getResult();
        mContactView.handleRefreshTab(cardList.size());
        if (cardList.size() > 0) {
            topArticleTimestamp = TimeUtil.Date2s(cardList.get(0).date);
            bottomArticleTimestamp = TimeUtil.Date2s(cardList.get(cardList.size() - 1).date);
            mContactView.onHideLoading();
            if (cardList.size() < 10) {
                mNewsSourceList.addAll(0, cardList);
                mContactView.handleAllNews(false, mNewsSourceList);
            } else {
                mNewsSourceList.clear();
                mNewsSourceList.addAll(cardList);
                mContactView.handleAllNews(false, mNewsSourceList);
            }
        } else {
            if (mNewsSourceList.size() == 0) {
                mContactView.onShowEmpty();
            }
        }
    }

    /**
     * 缓存数据的处理
     * @param newsList
     */
    private void handleNewsCache(List<Card> newsList) {
        mContactView.setRefeshEnable(false);
        if (newsList == null || newsList.size() == 0) {
            return;
        }
        mNewsSourceList.clear();
        mNewsSourceList.addAll(newsList);
        mContactView.handleAllNews(false, mNewsSourceList);
    }

    /**
     * 上拉加载更多的数据处理
     * @param jsonObject
     */
    private void handleLoadMoreResult(FeedsResponse jsonObject) {
        mContactView.setLoadMoreEnable(true);
        List<Card> temp = jsonObject.getResult();
        if (temp == null || temp.size() == 0) {
            mContactView.noLoadMore();
        } else {
            bottomArticleTimestamp = TimeUtil.Date2s(temp.get(temp.size() - 1).date);
            mNewsSourceList.addAll(temp);
            mContactView.handleAllNews(true, mNewsSourceList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
