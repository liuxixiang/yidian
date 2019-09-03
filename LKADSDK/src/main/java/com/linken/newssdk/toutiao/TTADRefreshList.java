package com.linken.newssdk.toutiao;

import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.NewsConfig;
import com.linken.newssdk.core.newslist.IRefreshList;
import com.linken.newssdk.core.newslist.NewsListContractView;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.utils.ContextUtils;

import java.util.ArrayList;
import java.util.List;

import static com.linken.newssdk.data.card.base.Card.AD_TOUTIAO_TYPE_FULL_VIDEO_205;
import static com.linken.newssdk.data.card.base.Card.AD_TOUTIAO_TYPE_GROUP_PIC_200;
import static com.linken.newssdk.data.card.base.Card.AD_TOUTIAO_TYPE_LARGE_PIC_202;
import static com.linken.newssdk.data.card.base.Card.AD_TOUTIAO_TYPE_SMALL_PIC_201;
import static com.linken.newssdk.data.card.base.Card.AD_TOUTIAO_TYPE_VERTICAL_PIC_204;
import static com.linken.newssdk.data.card.base.Card.AD_TOUTIAO_TYPE_VIDEO_203;


public class TTADRefreshList implements IRefreshList<Card> {

    private static final int AD_TYPE_TT = 1;
    // 用默认长度10/间隔,得到应该加载几条广告
    private int mSize = 2;

    private String mCodeId = NewsConfig.CODE_ID_FEED;
    private final TTAdNative mTTAdNative;
    private List<Card> mCards = new ArrayList<>();
    private NewsListContractView mContactView;

    public TTADRefreshList(NewsListContractView view) {
        mTTAdNative = TTAdManagerHolder.get().createAdNative(ContextUtils.getApplicationContext());
        this.mContactView = view;
    }

    @Override
    public void firstLazyRefresh() {
        loadTTFeed(mCodeId);
    }

    @Override
    public void onRefresh() {
        loadTTFeed(mCodeId);
    }

    @Override
    public void onLoadMoreRefresh() {
        loadTTFeed(mCodeId);
    }

    @Override
    public void onClickErrorRefresh() {
        loadTTFeed(mCodeId);
    }

    @Override
    public List<Card> getTAdapterItems() {
        return mCards;
    }


    public void loadTTFeed(String lastAdId) {
        //feed广告请求类型参数
        AdSlot adSlot = new AdSlot.Builder()
                // 必选参数 代码位id
                .setCodeId(mCodeId)
                .setSupportDeepLink(true)
                // 必选参数 设置广告图片的最大尺寸及期望的图片宽高比，单位Px
                .setImageAcceptedSize(640, 320)
                // 可选参数，针对信息流广告设置每次请求的广告返回个数，最多支持3个
                .setAdCount(mSize)
                .build();

        //调用feed广告异步请求接口
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e("lxh", "onError---" + code + "---" + message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                // 根据类型,变成不同的feedBean.
                List<Card> cards = new ArrayList<>();
                if (ads != null && ads.size() > 0) {
                    for (TTFeedAd ad : ads) {
                        AdvertisementCard adCard = new AdvertisementCard();
                        adCard.contentType = Card.CARD_ADVERTISE;
                        adCard.setTtFeedAd(ad);
                        //3大图 2小图 4 组图 5 视频 其它：未知类型
                        switch (ad.getImageMode()) {
                            case TTAdConstant.IMAGE_MODE_GROUP_IMG:
                                adCard.displayType = AD_TOUTIAO_TYPE_GROUP_PIC_200;
                                break;
                            case TTAdConstant.IMAGE_MODE_SMALL_IMG:
                                adCard.displayType = AD_TOUTIAO_TYPE_SMALL_PIC_201;
                                break;
                            case TTAdConstant.IMAGE_MODE_LARGE_IMG:
                                adCard.displayType = AD_TOUTIAO_TYPE_LARGE_PIC_202;
                                break;
                            case TTAdConstant.IMAGE_MODE_VIDEO:
                                adCard.displayType = AD_TOUTIAO_TYPE_VIDEO_203;
                                break;
                            case TTAdConstant.IMAGE_MODE_VERTICAL_IMG:
                                adCard.displayType = AD_TOUTIAO_TYPE_VERTICAL_PIC_204;
                                break;

                        }
                        cards.add(adCard);
                    }
                    AdvertisementCard adCard = new AdvertisementCard();
                    adCard.title = "看视频得奖励，瞧一瞧 看一看";
                    adCard.source = "今日头条";
                    adCard.contentType = Card.CARD_ADVERTISE;
                    adCard.displayType = AD_TOUTIAO_TYPE_FULL_VIDEO_205;
                    cards.add(adCard);
                }
                mContactView.handleNewsAdResult(AD_TYPE_TT,cards);
            }
        });
    }
}
