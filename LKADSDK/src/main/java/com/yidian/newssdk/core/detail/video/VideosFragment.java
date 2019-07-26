package com.yidian.newssdk.core.detail.video;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.yidian.newssdk.NewsFeedsSDK;
import com.yidian.newssdk.R;
import com.yidian.newssdk.base.fragment.LazyLoadPresenterFragment;
import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.export.IShareInterface;
import com.yidian.newssdk.protocol.newNetwork.business.report.ReportProxy;

import java.util.List;

/**
 * Created by chenyichang on 2018/5/22.
 */

public class VideosFragment extends LazyLoadPresenterFragment<VideoPresenter>
        implements RecyclerView.OnChildAttachStateChangeListener, VideoContractView {


    private RecyclerView mVideoRV;
    private LinearLayout mNextVideoLL;
    private LinearLayoutManager mLinearLayoutManager;
    private View mCurrentActiveView;
    private View mNextActiveView;
    private MoreVideoAdapter moreVideoAdapter;
    private View errorContainer;

    private String docId;
    private boolean isFromClick;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            docId = bundle.getString("docId");
        }
    }

    @Override
    protected ViewGroup placeHolderParent(View rootView) {
        return (ViewGroup) rootView;
    }

    @Override
    public void lazyFetchData() {
        mPresenter.lazyFetchData(getContext(), docId);
    }

    @Override
    protected void initPresenter() {
        mPresenter = new VideoPresenter(this);
    }

    @Override
    protected void initWidget(View view) {
        //this.errorContainer = view.findViewById(R.id.error_container);
        this.mVideoRV = (RecyclerView) view.findViewById(R.id.nnf_related_video_rv);
        this.mNextVideoLL = (LinearLayout) view.findViewById(R.id.ll_next_video);
        this.mNextVideoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideosFragment.this.clickView(VideosFragment.this.mNextActiveView);
            }
        });
        this.mLinearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.mVideoRV.setLayoutManager(this.mLinearLayoutManager);
        this.moreVideoAdapter = new MoreVideoAdapter(this, R.layout.ydsdk_yd_item_video);
        this.mVideoRV.setAdapter(this.moreVideoAdapter);
        this.mVideoRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    if (VideosFragment.this.isFromClick) {
                        VideosFragment.this.isFromClick = false;
                    } else {
                        VideosFragment.this.calculateView();
                        VideosFragment.this.moreVideoAdapter.activeVideo(VideosFragment.this.mCurrentActiveView);
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!VideosFragment.this.isFromClick) {
                    if (VideosFragment.this.isLowValue(dy)) {
                        VideosFragment.this.calculateView();
                        VideosFragment.this.moreVideoAdapter.activeVideo(VideosFragment.this.mCurrentActiveView);
                    } else {
                        VideosFragment.this.calculateView();
                    }
                }

            }
        });
        this.mVideoRV.addOnChildAttachStateChangeListener(this);

    }

    @Override
    protected int attachLayoutId() {
        return R.layout.ydsdk_fragment_video;
    }

    private boolean isLowValue(int dy) {
        return dy >= 0 && dy <= 5 || dy >= -5 && dy < 0;
    }

    private void calculateView() {
        boolean isActive = false;
        int count = this.mVideoRV.getChildCount();

        for (int i = 0; i < count; ++i) {
            View view = this.mVideoRV.getChildAt(i);
            if (this.isActive(view) && !isActive) {
                this.moreVideoAdapter.showVideo(view);
                this.mCurrentActiveView = view;
                isActive = true;
            } else {
                this.moreVideoAdapter.vanishVideo(view);
            }
        }
    }

    private boolean isActive(View view) {
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        int height = view.getHeight();
        int top = rect.top;
        int bottom = rect.bottom;
        int visiableHeight = bottom - top;
        int percents = visiableHeight * 100 / height;
        return percents >= 60;
    }
    @Override
    public void clickView(View view) {
        int height = view.getHeight();
        int top = view.getTop();
        int bottom = view.getBottom();
        int scrollLength;
        if (bottom <= height) {
            scrollLength = -(height - bottom);
        } else {
            scrollLength = top;
        }

        this.isFromClick = true;
        this.mVideoRV.smoothScrollBy(0, scrollLength, new DecelerateInterpolator());
        this.refreshActiveView(view);
        this.hideNextVideo();
    }

    @Override
    public void hideNextVideo() {
        this.mNextVideoLL.setVisibility(View.INVISIBLE);
    }

    private void refreshActiveView(View view) {
        int count = this.mVideoRV.getChildCount();

        for (int i = 0; i < count; ++i) {
            View view1 = this.mVideoRV.getChildAt(i);
            if (view1 == view) {
                this.moreVideoAdapter.activeVideo(view1);
                this.mCurrentActiveView = view;
            } else {
                this.moreVideoAdapter.vanishVideo(view1);
            }
        }
    }

    public View hasNextVideo(View view) {
        int count = this.mVideoRV.getChildCount();

        for (int i = 0; i < count; ++i) {
            View tmp = this.mVideoRV.getChildAt(i);
            if (view == tmp) {
                if (i != count - 1) {
                    this.mNextActiveView = this.mVideoRV.getChildAt(i + 1);
                    return this.mNextActiveView;
                }
                break;
            }
        }

        return null;
    }

    @Override
    public void nextVideo(View view) {
        int count = this.mVideoRV.getChildCount();

        for (int i = 0; i < count; ++i) {
            View tmp = this.mVideoRV.getChildAt(i);
            if (view == tmp) {
                if (i == count - 1) {
                    break;
                }

                this.clickView(this.mVideoRV.getChildAt(i + 1));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onChildViewAttachedToWindow(View view) {

    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }

    @Override
    public void onLoadData(List<Card> news) {
        moreVideoAdapter.setNewData(news);
    }

    @Override
    public void onShareClick(Card newsInfo) {
        NewsFeedsSDK.getInstance().getShareInterface().doShare(getActivity(), buildShareData(newsInfo));
    }

    private Bundle buildShareData(Card newsInfo) {
        Bundle bundle = new Bundle();
        if (newsInfo != null) {
            bundle.putString(IShareInterface.KEY_SHARE_TITLE, newsInfo.title);
            bundle.putString(IShareInterface.KEY_SHARE_IMG, newsInfo.image);
            bundle.putString(IShareInterface.KEY_SHARE_URL, newsInfo.url);
        }
        return bundle;
    }

    @Override
    public void onMoreFuncClick(Card newsInfo) {

    }

    @Override
    protected boolean inViewPager() {
        return false;
    }

    @Override
    protected boolean attachToRoot() {
        return true;
    }

    @Override
    protected String getPageReportId() {
        return ReportProxy.PAGE_VIDEO;
    }

    @Override
    public void onThemeChanged(int theme) {
    }
}
