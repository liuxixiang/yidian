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

public class ToutiaoAdGroupViewHolder extends ToutiaoAdViewHolder {
    ImageView mGroupImage1;
    ImageView mGroupImage2;
    ImageView mGroupImage3;

    @SuppressWarnings("RedundantCast")
    public ToutiaoAdGroupViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
        mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
        mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
        mGroupImage1 = (ImageView) itemView.findViewById(R.id.iv_listitem_image1);
        mGroupImage2 = (ImageView) itemView.findViewById(R.id.iv_listitem_image2);
        mGroupImage3 = (ImageView) itemView.findViewById(R.id.iv_listitem_image3);
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
        if (ttFeedAd.getImageList() != null && ttFeedAd.getImageList().size() >= 3) {
            TTImage image1 = ttFeedAd.getImageList().get(0);
            TTImage image2 = ttFeedAd.getImageList().get(1);
            TTImage image3 = ttFeedAd.getImageList().get(2);
            if (image1 != null && image1.isValid()) {
                ImageLoaderHelper.displayBigImage(mGroupImage1, image1.getImageUrl());
            }
            if (image2 != null && image2.isValid()) {
                ImageLoaderHelper.displayBigImage(mGroupImage2, image2.getImageUrl());
            }
            if (image3 != null && image3.isValid()) {
                ImageLoaderHelper.displayBigImage(mGroupImage3, image3.getImageUrl());
            }
        }
    }
}
