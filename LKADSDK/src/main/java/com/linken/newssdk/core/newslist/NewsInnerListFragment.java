package com.linken.newssdk.core.newslist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.linken.newssdk.R;
import com.linken.newssdk.YdCustomConfigure;
import com.linken.newssdk.adapter.MultipleItemQuickAdapter;
import com.linken.newssdk.adapter.WrapContentLinearLayoutManager;
import com.linken.newssdk.base.fragment.LazyLoadPresenterFragment;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.channel.YdChannel;
import com.linken.newssdk.libraries.bra.BaseQuickAdapter;
import com.linken.newssdk.libraries.imageloader.core.ImageLoader;
import com.linken.newssdk.theme.ThemeManager;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.ThreadUtils;
import com.linken.newssdk.widget.CustomLoadMoreView;
import com.linken.newssdk.widget.WrapperSwipeRefreshLayout;
import com.linken.newssdk.widget.pullRefresh.PullRefreshLayout;
import com.linken.newssdk.widget.views.ToastTabView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenyichang on 2018/5/18.
 */

//輸出的單列表
public class NewsInnerListFragment extends LazyLoadPresenterFragment<NewsListPresenter> implements NewsListContractView,
        View.OnClickListener, PullRefreshLayout.OnRefreshListener, IRefreshableNewsList {

    private static final String TAG = NewsInnerListFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    protected WrapperSwipeRefreshLayout mSwipeRefreshLayout;
    protected MultipleItemQuickAdapter multipleItemAdapter;
    private YdChannel mChannelInfo;
    private boolean hasLoadinged = false;
    private BroadcastReceiver mFontChangeReceiver;
    private ToastTabView mToastTabView;
    private int insertAdPosition = 0;
    private boolean isLoadNews;
    private boolean isLoadTopNews;
    private List<Card> mAdCard;

    public static NewsInnerListFragment newInstance(int position, YdChannel channelInfo) {
        NewsInnerListFragment fragment = new NewsInnerListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("channelInfo", channelInfo);
        bundle.putInt("channelPosition", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (null != bundle) {
            this.mChannelInfo = (YdChannel) bundle.getSerializable("channelInfo");
        }
        mPresenter.init(mChannelInfo);
    }

    @Override
    protected int attachLayoutId() {
        return R.layout.ydsdk_fragment_refresh_view;
    }

    @Override
    protected void initWidget(View view) {
        mToastTabView = view.findViewById(R.id.toast_tabview);
        mSwipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        setRefeshEnable(false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        multipleItemAdapter = generateAdapter(getContext());
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        multipleItemAdapter.bindToRecyclerView(mRecyclerView);
        multipleItemAdapter.setPreLoadNumber(YdCustomConfigure.getInstance().getPreLoadMoreCount());
        multipleItemAdapter.handleADScrollListener();
        multipleItemAdapter.setOnToastCallback(new MultipleItemQuickAdapter.OnToastCallbaclk() {
            @Override
            public void onToast() {
                if (mToastTabView != null) {
                    mToastTabView.startShowWithAnim(R.string.ydsdk_feedback_dislike_tip_leshi);
                }
            }
        });
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //正在滑动
                        ImageLoader.getInstance().pause();
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //滑动停止
                        ImageLoader.getInstance().resume();
                        break;
                }
            }
        });
    }

    @Override
    protected void initPresenter() {
        this.mPresenter = new NewsListPresenter(getContext(), this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleLoadMoreView();
        hasLoadinged = false;
        if (multipleItemAdapter.getData().size() == 0) {
            onShowLoading();
        }
        ThemeManager.registerThemeChange(this);
        registerFontChangeReceiver();
        getErrorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleItemAdapter.setEmptyView(getLoadingView());
                setLoadMoreEnable(false);
                mRecyclerView.setItemAnimator(null);
                mPresenter.onClickErrorRefresh();
            }
        });
    }

    @Override
    public void onDestroyView() {
        mChannelInfo.newsList = mPresenter.getTAdapterItems();
        LocalBroadcastManager.getInstance(ContextUtils.getApplicationContext()).unregisterReceiver(mFontChangeReceiver);
        mSwipeRefreshLayout.setOnRefreshListener(null);
        super.onDestroyView();
    }

    @Override
    protected ViewGroup placeHolderParent(View rootView) {
        return (ViewGroup) mRecyclerView.getParent();
    }

    @Override
    protected String getPageReportId() {
        return YdChannel.getChannelName(mChannelInfo.getChecksumName());
    }

    private void handleLoadMoreView() {
        if (multipleItemAdapter != null) {
            multipleItemAdapter.setLoadMoreView(new CustomLoadMoreView());
            multipleItemAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    loadMore();
                }
            });
        }
    }

    private void loadMore() {
        mPresenter.onLoadMoreRefresh();
    }

    protected MultipleItemQuickAdapter generateAdapter(Context context) {
        return new MultipleItemQuickAdapter(context, mPresenter.getTAdapterItems());
    }

    @Override
    public void lazyFetchData() {
        Log.d("NewsInnerListFragment", "lazyFetchData() enter!");
        setLoadMoreEnable(false);
        setRefeshEnable(false);
        onShowLoading();
        mPresenter.firstLazyRefresh();
    }

    @Override
    public void onRefresh() {
        onRefrshWrapper();
    }

    private void onRefrshWrapper() {
        setLoadMoreEnable(false);
        mRecyclerView.setItemAnimator(null);
        mPresenter.onRefresh();
    }

    @Override
    public void refreshData(boolean isAutoRefresh) {
        mRecyclerView.scrollToPosition(0);
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true, false);
            onRefrshWrapper();
        }
    }

    public void exposeRefresh() {
        refreshData(false);
    }

    @Override
    public void setRefeshEnable(boolean enable) {
        mSwipeRefreshLayout.setRefreshing(enable);
    }

    @Override
    public void loadMoreFailed() {
        multipleItemAdapter.loadMoreFail();
    }

    @Override
    public void noLoadMore() {
        multipleItemAdapter.loadMoreEnd(false);
    }

    @Override
    public void setLoadMoreEnable(boolean enable) {
        multipleItemAdapter.setEnableLoadMore(enable);//这里的作用是防止下拉刷新的时候还可以上拉加载

    }

    @Override
    public void handleRefreshTab(int count) {
//        mSwipeRefreshLayout.setRefreshing(false);
        StringBuilder builder = new StringBuilder();

        if (count == 0) {
            builder.append("暂无更新");
        } else {
            builder.append("为你推荐了")
                    .append(count)
                    .append("篇新内容");
        }
        if (isVisableToUser() || !inViewPager()) {
            if (count == 0) {
                setTipResultRunOnUI(builder.toString(), 0L);
            } else {
                setTipResultRunOnUI(builder.toString());
            }
        } else {
            setRefeshEnable(false);
        }
    }

    private void setTipResultRunOnUI(final String tipString, long delayTime) {
        ThreadUtils.postDelayed2UI(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setTipResult(tipString, false);
            }
        }, delayTime);
    }

    private void setTipResultRunOnUI(final String tipString) {
        ThreadUtils.postDelayed2UI(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setTipResult(tipString, false);
//                setRefeshEnable(false);
            }
        }, 0L);
    }

    @Override
    public void showRefreshTip(final String refreshErrorTip) {
        setTipResultRunOnUI(refreshErrorTip, 400L);
    }

    @Override
    public boolean isVisableToUser() {
        return isVisibleToUser;
    }


    @Override
    public void handleNewsResultRefresh(List newsResult) {
        multipleItemAdapter.setNewData(newsResult);
        multipleItemAdapter.disableLoadMoreIfNotFullPage();
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void handleNewsLoadMore(List newsResult) {
        multipleItemAdapter.setNewData(newsResult);
        multipleItemAdapter.disableLoadMoreIfNotFullPage();
        multipleItemAdapter.loadMoreComplete();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onShowError(String errorTip) {
        if (mErrorTipView != null) {
            mErrorTipView.setText(errorTip);
        }
        onShowError();
    }


    @Override
    public void onShowError() {
        multipleItemAdapter.setEmptyView(getErrorView());
    }

    @Override
    public void onHideError() {

    }

    @Override
    public void onShowEmpty() {
        multipleItemAdapter.setEmptyView(getEmptyView());
    }

    @Override
    public void onHideEmpty() {
        super.onHideEmpty();
    }

    @Override
    public void onShowLoading() {
        if (hasLoadinged == true) {
            return;
        }
        multipleItemAdapter.setEmptyView(getLoadingView());
        hasLoadinged = true;
    }

    @Override
    public void onHideLoading() {
        super.onHideLoading();
    }

    @Override
    public void onThemeChanged(int theme) {
        super.onThemeChanged(theme);
        int color = ThemeManager.getColor(getContext(), theme, R.styleable.NewsSDKTheme_newssdk_common_bg_color, 0xffffff);
        if (getEmptyView() != null) {
            getEmptyView().setBackgroundColor(color);
        }

        if (getErrorView() != null) {
            getErrorView().setBackgroundColor(color);
        }

        if (getLoadingView() != null) {
            getLoadingView().setBackgroundColor(color);
        }
    }

    private void registerFontChangeReceiver() {
        if (mFontChangeReceiver == null) {
            mFontChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (TextUtils.equals(YdCustomConfigure.ACTON_FONT, intent.getAction())) {
                        if (multipleItemAdapter != null) {
                            multipleItemAdapter.notifyDataSetChanged();
                        }
                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(YdCustomConfigure.ACTON_FONT);
        LocalBroadcastManager.getInstance(ContextUtils.getApplicationContext()).registerReceiver(mFontChangeReceiver, filter);
    }

    @Override
    public void refreshCurrentChannel() {
        refreshData(false);
    }

    @Override
    public void scrollToTopPosition() {
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public boolean isScrollToTopPosition() {
        if (mRecyclerView == null) {
            return false;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        //屏幕中第一个可见子项的position
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = mRecyclerView.getScrollState();
        if (visibleItemCount > 0 && totalItemCount > 0 && firstVisibleItemPosition <= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCurrentChannelName() {
        return getPageReportId();
    }


    @Override
    public void handleAllNews(boolean isLoadMore, List newsResult) {
//        mSwipeRefreshLayout.setRefreshing(false);
        ArrayList<Card> adapterAllItems = mPresenter.getTAdapterItems();
        if (isLoadMore) {
            adapterAllItems.addAll(newsResult);
            handleNewsLoadMore(adapterAllItems);
        } else {
            insertAdPosition = 0;
            //清除非置顶数据
            removeItems(false, adapterAllItems);
//            adapterAllItems.clear();
            adapterAllItems.addAll(newsResult);
            handleNewsResultRefresh(adapterAllItems);
        }
        if (multipleItemAdapter.getData().size() == 0) {
            onShowEmpty();
            return;
        }
//        if (mAdCard == null) {
//            isLoadNews = true;
//        }
//        insertADCard(mAdCard);
    }

    @Override
    public void handleTopResultList(List cards) {
        isLoadTopNews = true;
        ArrayList<Card> adapterAllItems = mPresenter.getTAdapterItems();
        //先清除置顶数据
        removeItems(true,adapterAllItems);
        adapterAllItems.addAll(0, cards);
        multipleItemAdapter.notifyDataSetChanged();
    }

    private void removeItems(boolean isTop, ArrayList<Card> adapterAllItems) {
        for (Iterator<Card> it = adapterAllItems.iterator(); it.hasNext(); ) {
            Card card = it.next();
            if(isTop) {
                if ("top".equals(card.dtype)) {
                    it.remove();
                }
            }else {
                if (!"top".equals(card.dtype)) {
                    it.remove();
                }
            }

        }
    }


    @Override
    public void handleNewsAdResult(int adTypeTt, List<Card> cards) {
//        mAdCard = cards;
//        if (isLoadNews) {
//            insertADCard(mAdCard);
//            mAdCard = null;
//            isLoadNews = false;
//        }
    }

    private void insertADCard(List<Card> adCards) {
//        //隔三插一条
//        if (multipleItemAdapter.getItemCount() - insertAdPosition > 3) {
//            if (adCards != null && adCards.size() > 0) {
//                for (Card card : adCards) {
//                    insertAdPosition = insertAdPosition + 4;
//                    multipleItemAdapter.getData().add(insertAdPosition -1, card);
//                }
//                multipleItemAdapter.notifyDataSetChanged();
//            }
//        }
    }


}
