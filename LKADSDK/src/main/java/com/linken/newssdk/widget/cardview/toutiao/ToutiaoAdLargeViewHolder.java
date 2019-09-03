package com.linken.newssdk.widget.cardview.toutiao;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.R;
import com.linken.newssdk.utils.support.ImageLoaderHelper;

public class ToutiaoAdLargeViewHolder extends ToutiaoAdViewHolder {
    ImageView mLargeImage;

    public ToutiaoAdLargeViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
        mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
        mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
        mLargeImage = (ImageView) itemView.findViewById(R.id.iv_listitem_image);
        mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
        mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
        mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
        mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);
    }


    @Override
    public void onBind(AdvertisementCard card, String docid) {
        super.onBind(card, docid);
        TTFeedAd ttFeedAd = card.getTtFeedAd();
        if (ttFeedAd != null) {
            bindData(this, ttFeedAd);
            if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
                TTImage image = ttFeedAd.getImageList().get(0);
                if (image != null && image.isValid()) {
                    ImageLoaderHelper.displayBigImage(mLargeImage, image.getImageUrl());
                }
            }
        }
    }
}