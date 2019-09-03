package com.linken.newssdk.widget.cardview.toutiao;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.R;

public class ToutiaoAdVideoViewHolder extends ToutiaoAdViewHolder {
    FrameLayout videoView;

    public ToutiaoAdVideoViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
        mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
        mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
        videoView = (FrameLayout) itemView.findViewById(R.id.iv_listitem_video);
        mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
        mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
        mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
        mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);

    }

    @Override
    public void onBind(AdvertisementCard card, String docid) {
        super.onBind(card, docid);
        TTFeedAd ttFeedAd = card.getTtFeedAd();
        if (ttFeedAd == null) {
            return;
        }

        bindData(this, ttFeedAd);
        ttFeedAd.setVideoAdListener(new TTFeedAd.VideoAdListener() {
            @Override
            public void onVideoLoad(TTFeedAd ad) {

            }

            @Override
            public void onVideoError(int errorCode, int extraCode) {

            }

            @Override
            public void onVideoAdStartPlay(TTFeedAd ad) {

            }

            @Override
            public void onVideoAdPaused(TTFeedAd ad) {

            }

            @Override
            public void onVideoAdContinuePlay(TTFeedAd ad) {

            }
        });
        if (videoView != null) {
            View video = ttFeedAd.getAdView();
            if (video != null) {
                if (video.getParent() == null) {
                    videoView.removeAllViews();
                    videoView.addView(video);
                }
            }
        }
    }
}
