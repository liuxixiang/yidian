package com.linken.newssdk.widget.cardview.toutiao;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.R;

public class ToutiaoAdVerticalViewHolder extends ToutiaoAdViewHolder {
    ImageView mVerticalImage;

    public ToutiaoAdVerticalViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
        mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
        mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
        mVerticalImage = (ImageView) itemView.findViewById(R.id.iv_listitem_image);
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
    }
}
