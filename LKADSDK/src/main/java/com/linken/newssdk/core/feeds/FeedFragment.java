package com.linken.newssdk.core.feeds;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.linken.newssdk.R;
import com.linken.newssdk.adapter.NaviPagerAdapter;
import com.linken.newssdk.base.fragment.LazyLoadPresenterFragment;
import com.linken.newssdk.adapter.NoScrollViewPager;
import com.linken.newssdk.core.newslist.IRefreshableNewsList;
import com.linken.newssdk.libraries.flyco.SlidingTabLayout;
import com.linken.newssdk.libraries.flyco.listener.OnTabSelectListener;
import com.linken.newssdk.protocol.newNetwork.business.report.ReportProxy;
import com.linken.newssdk.utils.LocationMgr;
import com.linken.newssdk.utils.LogUtils;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public class FeedFragment extends LazyLoadPresenterFragment<FeedsPresenter> implements FeedsContractView, IRefreshableNewsList {
    private static final String TAG = FeedFragment.class.getSimpleName();

    private SlidingTabLayout slidingTabLayout;
    private NoScrollViewPager mViewPager;
    private NaviPagerAdapter naviPagerAdapter;
    private FrameLayout mErrorFrameLayout;
    private FragmentManager mFragmentManager;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    @Override
    protected ViewGroup placeHolderParent(View rootView) {
        return mErrorFrameLayout;
    }

    @Override
    protected String getPageReportId() {
        return ReportProxy.PAGE_FEED;
    }

    @Override
    protected int attachLayoutId() {
        return R.layout.ydsdk_fragment_feeds;
    }

    @Override
    protected void initWidget(View view) {
        mErrorFrameLayout = view.findViewById(R.id.channel_list_error);
        this.mViewPager = view.findViewById(R.id.view_pager);
        this.slidingTabLayout = view.findViewById(R.id.sliding_tab);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentManager = getChildFragmentManager();
        initCustomView();
        LocationMgr.getInstance().getLocation();
    }

    @Override
    protected void initPresenter() {
        this.mPresenter = new FeedsPresenter(this);
    }

    @Override
    public void initMagicIndicator() {
        naviPagerAdapter = new NaviPagerAdapter(mFragmentManager, getContext(), mPresenter.getChannels());
        mViewPager.setAdapter(naviPagerAdapter);
        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ReportProxy.onPageSelected(getChannelName(position));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        slidingTabLayout.setViewPager(mViewPager);
        slidingTabLayout.setSnapOnTabClick(true);
        slidingTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                LogUtils.d(TAG, "onTabSelect");
            }

            @Override
            public void onTabReselect(int position) {
                updateCurrentNewsList(position);
            }
        });
        mOnPageChangeListener.onPageSelected(0);
    }

    public void updateCurrentNewsList(int position) {
        if (mViewPager != null && naviPagerAdapter != null) {
            IRefreshableNewsList fragment = naviPagerAdapter.getListFragment(position);
            if (fragment != null) {
                fragment.refreshData(false);
            }
        }
    }

    public static FeedFragment newInstanceInner() {
        FeedFragment feedFragment = new FeedFragment();
        return feedFragment;
    }

    private void initCustomView() {
        //view导入配置的代码
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHideError();
                onShowLoading();
                mPresenter.loadChannels();
            }
        });
    }

    @Override
    public void lazyFetchData() {
        mPresenter.loadChannels();
    }

    @Override
    public void onShowError(String errorTip) {
        if (mErrorTipView != null) {
            mErrorTipView.setText(errorTip);
        }
        onShowError();
    }

    @Override
    protected void initErrorView(ViewGroup viewGroup) {
        errorView = LayoutInflater.from(getActivity()).inflate(R.layout.ydsdk_error_view, viewGroup, false);
        mErrorTipView = errorView.findViewById(R.id.error_tip);
    }

    @Override
    protected void initLoadingView(ViewGroup viewGroup) {
        loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.ydsdk_loading_view, viewGroup, false);
    }

    @Override
    public void onShowError() {
        mErrorFrameLayout.setVisibility(View.VISIBLE);
        mErrorFrameLayout.removeAllViews();
        mErrorFrameLayout.addView(errorView);
        super.onShowError();
    }

    @Override
    public void onShowLoading() {
        mErrorFrameLayout.setVisibility(View.VISIBLE);
        mErrorFrameLayout.removeAllViews();
        mErrorFrameLayout.addView(loadingView);
        super.onShowLoading();
    }

    @Override
    public void onHideLoading() {
        mErrorFrameLayout.setVisibility(View.GONE);
        super.onHideLoading();
    }

    @Override
    public void onHideError() {
        mErrorFrameLayout.setVisibility(View.GONE);
        super.onHideError();
    }

    @Override
    protected boolean inViewPager() {
        return false;
    }

    @Override
    public void refreshCurrentChannel() {
        refreshData(false);
    }

    @Override
    public void scrollToTopPosition() {
        if (naviPagerAdapter != null && naviPagerAdapter.getCurrentFragment() != null) {
            naviPagerAdapter.getCurrentFragment().scrollToTopPosition();
        }
    }

    @Override
    public boolean isScrollToTopPosition() {
        if (naviPagerAdapter != null && naviPagerAdapter.getCurrentFragment() != null) {
            return naviPagerAdapter.getCurrentFragment().isScrollToTopPosition();
        }
        return false;
    }

    @Override
    public String getCurrentChannelName() {
        if (naviPagerAdapter != null && naviPagerAdapter.getCurrentFragment() != null) {
            return naviPagerAdapter.getCurrentFragment().getCurrentChannelName();
        }
        return "";
    }

    private String getChannelName(int position) {
        if (position < 0 || position >= naviPagerAdapter.getCount()) {
            return "";
        }

        return naviPagerAdapter.getItemData(position).getChannelName();
    }

    @Override
    public void refreshData(boolean isAutoRefresh) {
        if (naviPagerAdapter != null && naviPagerAdapter.getCurrentFragment() != null) {
            naviPagerAdapter.getCurrentFragment().refreshData(false);
        }
    }
}
