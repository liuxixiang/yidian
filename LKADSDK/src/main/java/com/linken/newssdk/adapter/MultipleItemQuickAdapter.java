package com.linken.newssdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.R;
import com.linken.newssdk.core.ad.ItemExposeUtil;
import com.linken.newssdk.core.ad.ViewReportManager;
import com.linken.newssdk.data.ad.ADConstants;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.libraries.bra.BaseMultiItemQuickAdapter;
import com.linken.newssdk.libraries.bra.BaseViewHolder;
import com.linken.newssdk.libraries.bra.entity.MultiItemEntity;
import com.linken.newssdk.widget.cardview.NoMoreViewHolder;

import com.linken.newssdk.widget.cardview.adcard.AdCard03;
import com.linken.newssdk.widget.cardview.adcard.AdCard04;
import com.linken.newssdk.widget.cardview.adcard.AdCard15;
import com.linken.newssdk.widget.cardview.adcard.AdCard40;
import com.linken.newssdk.widget.cardview.newscard.BigImageCardViewHolder;
import com.linken.newssdk.widget.cardview.newscard.MultiImageCardViewHolder;
import com.linken.newssdk.widget.cardview.PictureGalleryCardViewHolder;
import com.linken.newssdk.widget.cardview.newscard.SmallImageCardViewHolder;
import com.linken.newssdk.widget.cardview.videocard.VideoLiveForFlowCardViewHolder;

import java.util.List;
import java.util.TreeMap;

public class MultipleItemQuickAdapter<T extends MultiItemEntity> extends BaseMultiItemQuickAdapter<T, BaseViewHolder> implements IRecyclerView{

    private Context mContext;
    public MultipleItemQuickAdapter(Context context, List data) {
        super(data);
        this.mContext = context;
        addItemType(Card.DISPLAY_TYPE_ONE_IMAGE, R.layout.ydsdk_card_news_item_ns);
        addItemType(Card.DISPLAY_TYPE_VIDEO_BIG, R.layout.ydsdk_card_video_live_flow_ns);
        addItemType(Card.DISPLAY_TYPE_MULTI_IMAGE, R.layout.ydsdk_card_news_item_imgline_ns);
        addItemType(Card.DISPLAY_TYPE_BIG_IMAGE, R.layout.yidianhao_big_image_card_view);
        addItemType(Card.DISPLAY_TYPE_JOKE, R.layout.yidianhao_joke_card_view_ns);
        addItemType(Card.DISPLAY_TYPE_PICTURE_GALLERY_OUTSIDE_CHANNEL_BIG_IMAGE, R.layout.ydsdk_card_picturegallery_outsidechannel_bigimage_ns);
        addItemType(Card.DISPLAY_TYPE_PICTURE_GALLERY_OUTSIDE_CHANNEL_SMALL_IMAGE, R.layout.ydsdk_card_picturegallery_outsidechannel_smallimage_ns);
        addItemType(Card.AD_TEMPLATE_3, R.layout.ydsdk_ad_news_list_template_3);
        addItemType(Card.AD_TEMPLATE_4, R.layout.ydsdk_ad_news_list_template_4);
        addItemType(Card.AD_TEMPLATE_40, R.layout.ydsdk_ad_news_list_template_40);
        addItemType(Card.AD_TEMPLATE_116, R.layout.ydsdk_ad_news_list_template_15);
//        addItemType(-1, R.layout.ydsdk_cardview_empty);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        BaseViewHolder viewHolder = null;
        if (layoutResId == R.layout.ydsdk_card_news_item_ns) {
            viewHolder = new SmallImageCardViewHolder(mContext, this, getItemView(layoutResId, parent));
        } else if(layoutResId == R.layout.ydsdk_card_video_live_flow_ns) {
            viewHolder = new VideoLiveForFlowCardViewHolder(mContext, this, getItemView(layoutResId, parent));
        } else if (layoutResId == R.layout.ydsdk_card_news_item_imgline_ns) {
            viewHolder = new MultiImageCardViewHolder(this, getItemView(layoutResId, parent));
        } else if (layoutResId == R.layout.yidianhao_big_image_card_view) {
            viewHolder = new BigImageCardViewHolder(this, getItemView(layoutResId, parent));
        } else if (layoutResId == R.layout.ydsdk_card_picturegallery_outsidechannel_bigimage_ns) {
            viewHolder = new PictureGalleryCardViewHolder(this, getItemView(layoutResId, parent));
        } else if (layoutResId == R.layout.ydsdk_card_picturegallery_outsidechannel_smallimage_ns) {
            viewHolder = new PictureGalleryCardViewHolder(this, getItemView(layoutResId, parent));
        } else if (layoutResId == R.layout.ydsdk_ad_news_list_template_3) {
            viewHolder = new AdCard03(this, getItemView(layoutResId, parent));
        } else if (layoutResId == R.layout.ydsdk_ad_news_list_template_4) {
            viewHolder = new AdCard04(this, getItemView(layoutResId, parent));
        }  else if (layoutResId == R.layout.ydsdk_ad_news_list_template_40) {
            viewHolder = new AdCard40(this, getItemView(layoutResId, parent));
        } else if (layoutResId == R.layout.ydsdk_ad_news_list_template_15) {
            viewHolder = new AdCard15(this, getItemView(layoutResId, parent));
        }
        else {
            viewHolder = new NoMoreViewHolder(getItemView(R.layout.ydsdk_cardview_empty, parent));
        }
        return viewHolder;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        if (helper == null) {
            return;
        }
        if (helper instanceof SmallImageCardViewHolder) {
            ((SmallImageCardViewHolder) helper).onBind((Card) item, this);
        } else if (helper instanceof VideoLiveForFlowCardViewHolder) {
            ((VideoLiveForFlowCardViewHolder) helper).onBind((Card) item, this);
        } else if (helper instanceof MultiImageCardViewHolder) {
            ((MultiImageCardViewHolder) helper).onBind((Card) item, this);
        } else if (helper instanceof BigImageCardViewHolder) {
            ((BigImageCardViewHolder) helper).onBind((Card) item, this);
        } else if (helper instanceof PictureGalleryCardViewHolder) {
            ((PictureGalleryCardViewHolder) helper).onBind((Card) item, this);
        } else if (helper instanceof AdCard03) {
            ((AdCard03) helper).onBind((AdvertisementCard) item, ((AdvertisementCard) item).id);
        } else if (helper instanceof AdCard04) {
            ((AdCard04) helper).onBind((AdvertisementCard) item, ((AdvertisementCard) item).id);
        } else if (helper instanceof AdCard40) {
            ((AdCard40) helper).onBind((AdvertisementCard) item, ((AdvertisementCard) item).id);
        } else if (helper instanceof AdCard15) {
            ((AdCard15) helper).onBind((AdvertisementCard) item, ((AdvertisementCard) item).id);
        } else {
            ((NoMoreViewHolder)helper).onBind();
        }
    }


    @NonNull
    @Override
    public List<T> getData() {
        return super.getData();
    }

    public void setData(List mData) {
        this.mData = mData;
    }


    @Override
    public void removeRow(int position) {
        if (position == -1) {
            return;
        }
        if (mOnToastCallbaclk != null) {
            mOnToastCallbaclk.onToast();
        }
        getRecyclerView().setItemAnimator(new YdItemAnimator());
        getData().remove(position);
        notifyItemRemoved(position);
    }


    private final TreeMap<Integer, ItemExposeUtil.ItemInfo> visibleItems = new TreeMap<>();
    private final TreeMap<Integer, ItemExposeUtil.ItemInfo> disappearItems = new TreeMap<>();

    public void handleADScrollListener() {

        getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                ItemExposeUtil.onScroll(recyclerView, visibleItems, disappearItems, 0);
                for (ItemExposeUtil.ItemInfo itemInfo : visibleItems.values()) {
                    if (itemInfo == null || itemInfo.view == null) {
                        continue;
                    }
                    Object tag = itemInfo.view.getTag(R.id.ydsdk_ad_view_report);
                    if (tag instanceof ViewReportManager) {
                        ViewReportManager reportManager = ((ViewReportManager) tag);
                        reportManager.doViewDelayReport(ADConstants.VIEW_1S);
                        if (itemInfo.visiblePercentage > 50) {
                            reportManager.doTencentViewDelayReport(1000);
                        } else {
                            reportManager.cancelTencentViewDelayReport();
                        }
                    }
                }
                for (ItemExposeUtil.ItemInfo itemInfo : disappearItems.values()) {
                    if (itemInfo == null || itemInfo.view == null) {
                        continue;
                    }

                    Object tag = itemInfo.view.getTag(R.id.ydsdk_ad_view_report);
                    if (tag instanceof ViewReportManager) {
                        ViewReportManager reportManager = ((ViewReportManager) tag);
                        reportManager.cancelViewDelayReport();
                        reportManager.cancelTencentViewDelayReport();
                    }

                }

            }
        });
    }

    public int getRightCardPosition(String docId) {
        int position = -1;
        if (TextUtils.isEmpty(docId)) {
            return -1;
        }

        for (MultiItemEntity card : mData) {
            if (card instanceof Card) {
                if (TextUtils.equals(docId, ((Card) card).id)) {
                    position = mData.indexOf(card);
                    break;
                }
            }
        }
        return position;
    }

    public interface OnToastCallbaclk {
        void onToast();
    }

    private OnToastCallbaclk mOnToastCallbaclk;
    public void setOnToastCallback(OnToastCallbaclk onToastCallback) {
        this.mOnToastCallbaclk = onToastCallback;
    }
}
