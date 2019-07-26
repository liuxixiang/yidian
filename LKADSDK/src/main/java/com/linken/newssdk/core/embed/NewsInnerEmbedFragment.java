package com.linken.newssdk.core.embed;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.linken.newssdk.R;
import com.linken.newssdk.base.fragment.BaseFragment;
import com.linken.newssdk.core.newslist.NewsListContractView;
import com.linken.newssdk.core.newslist.NewsListPresenter;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.channel.YdChannel;
import com.linken.newssdk.libraries.bra.BaseViewHolder;
import com.linken.newssdk.protocol.newNetwork.business.report.ReportProxy;
import com.linken.newssdk.widget.cardview.PictureGalleryCardViewHolder;
import com.linken.newssdk.widget.cardview.WemediaJokeViewHolder;
import com.linken.newssdk.widget.cardview.newscard.BigImageCardViewHolder;
import com.linken.newssdk.widget.cardview.newscard.MultiImageCardViewHolder;
import com.linken.newssdk.widget.cardview.newscard.SmallImageCardViewHolder;
import com.linken.newssdk.widget.cardview.videocard.VideoLiveForFlowCardViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyichang on 2018/5/31.
 */

public class NewsInnerEmbedFragment extends BaseFragment<NewsListPresenter> implements NewsListContractView, SwipeRefreshLayout.OnRefreshListener {

    private YdChannel mChannelInfo;
    private LinearLayout contentContainer;
    private int count;
    private List<Card> mCards = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (null != bundle) {
            this.mChannelInfo = (YdChannel) bundle.getSerializable("channelInfo");
            this.count = bundle.getInt("newsCount", 3);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ViewGroup placeHolderParent(View rootView) {
        return (ViewGroup) rootView;
    }

    @Override
    protected String getPageReportId() {
        return ReportProxy.PAGE_EMBED;
    }

    @Override
    protected int attachLayoutId() {
        return R.layout.ydsdk_fragment_embed_view;
    }

    @Override
    protected void initWidget(View view) {
        contentContainer = view.findViewById(R.id.content_layout);
        mPresenter.init(mChannelInfo);
        mPresenter.firstLazyRefresh();
    }

    @Override
    protected void initPresenter() {
        this.mPresenter = new NewsListPresenter(this);
        this.mPresenter.setContext(getContext());
        this.mPresenter.setRefreshCount(count);
    }

    @Override
    public void onRefresh() {
        mPresenter.onRefresh();
    }


    @Override
    public void handleAllNews(boolean isLoadMore, List newsResult) {
        handleFetchComplete(newsResult);
    }

    @Override
    public void showRefreshTip(String refreshErrorTip) {

    }

    @Override
    public boolean isVisableToUser() {
        return false;
    }

    @Override
    public void onShowError(String errorTip) {

    }

    @Override
    public void handleNewsResultRefresh(List newsResult) {

    }

    @Override
    public void handleNewsLoadMore(List newsResult) {
    }


    public void handleFetchComplete(List<Card> resultList) {

        if (mCards.size() == 0 && resultList.size() == 0) {
            onShowEmpty();
        }

        setRefeshEnable(false);
        mCards.clear();
        if (resultList.size() > count) {
            mCards.addAll(resultList.subList(0, count));
        } else {
            mCards.addAll(resultList);
        }

        contentContainer.removeAllViews();
        for (Card card : mCards){

            int layoutResId = getLayoutId(card);
            View child = null;
            BaseViewHolder viewHolder = null;
            if (layoutResId == R.layout.ydsdk_card_news_item_ns) {
                child = getItemView(layoutResId);
                viewHolder = new SmallImageCardViewHolder(getContext(), null, child);
                ((SmallImageCardViewHolder) viewHolder).onBind( card, null);
            } else if(layoutResId == R.layout.ydsdk_card_video_live_flow_ns) {
                child = getItemView(layoutResId);
                viewHolder = new VideoLiveForFlowCardViewHolder(getContext(), null, child);
                ((VideoLiveForFlowCardViewHolder) viewHolder).onBind( card, null);
            }else if (layoutResId == R.layout.ydsdk_card_news_item_imgline_ns) {
                child = getItemView(layoutResId);
                viewHolder = new MultiImageCardViewHolder(null, child);
                ((MultiImageCardViewHolder) viewHolder).onBind( card, null);
            } else if (layoutResId == R.layout.yidianhao_big_image_card_view) {
                child = getItemView(layoutResId);
                viewHolder = new BigImageCardViewHolder(null, child);
                ((BigImageCardViewHolder) viewHolder).onBind( card, null);
            } else if (layoutResId == R.layout.ydsdk_card_picturegallery_outsidechannel_bigimage_ns) {
                child = getItemView(layoutResId);
                viewHolder = new PictureGalleryCardViewHolder(null, child);
                ((PictureGalleryCardViewHolder) viewHolder).onBind( card, null);
            } else if (layoutResId == R.layout.yidianhao_joke_card_view_ns) {
                child = getItemView(layoutResId);
                viewHolder = new WemediaJokeViewHolder(null, child);
                ((WemediaJokeViewHolder) viewHolder).onBind( card, null);
            }

            if (child != null){
                contentContainer.addView(child);
            }
        }
    }

    private View getItemView(int layoutResId) {
        return LayoutInflater.from(getActivity()).inflate(layoutResId, null, false);
    }

    private SparseIntArray layouts;
    private int getLayoutId(Card card) {
        if (layouts == null){
            layouts = new SparseIntArray();
            addItemType(Card.DISPLAY_TYPE_ONE_IMAGE, R.layout.ydsdk_card_news_item_ns);
            addItemType(Card.DISPLAY_TYPE_VIDEO_BIG, R.layout.ydsdk_card_video_live_flow_ns);
            addItemType(Card.DISPLAY_TYPE_MULTI_IMAGE, R.layout.ydsdk_card_news_item_imgline_ns);
            addItemType(Card.DISPLAY_TYPE_BIG_IMAGE, R.layout.yidianhao_big_image_card_view);
            addItemType(Card.DISPLAY_TYPE_JOKE, R.layout.yidianhao_joke_card_view_ns);
        }

        return layouts.get(card.displayType, -1);
    }

    private void addItemType(int type, int layoutResId) {
        layouts.put(type, layoutResId);
    }

    @Override
    public void setRefeshEnable(boolean enable) {

    }

    @Override
    public void loadMoreFailed() {

    }

    @Override
    public void noLoadMore() {

    }

    @Override
    public void setLoadMoreEnable(boolean enable) {

    }

    @Override
    public void handleRefreshTab(int count) {
    }


}
