package com.yidian.newssdk.widget.cardview.adcard;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.R;
import com.yidian.newssdk.adapter.MultipleItemQuickAdapter;
import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.utils.DensityUtil;
import com.yidian.newssdk.utils.support.ImageLoaderHelper;
import com.yidian.newssdk.widget.cardview.adcard.base.AdBaseCard;

/**
 * Created by patrickleong on 4/13/15.
 */
public class AdCard04 extends AdBaseCard {
    ImageView imgView;

    public AdCard04(MultipleItemQuickAdapter adapter, final View itemView) {
        super(adapter, itemView);
        imgView = itemView.findViewById(R.id.small_image);
        imgView.setOnClickListener(this);
    }

    @Override
    public void loadImage() {
        ImageLoaderHelper.displayImage(imgView, mAdCard.getImageUrl());
    }

    @Override
    public void onBind(AdvertisementCard card, String docId) {
        super.onBind(card, docId);
    }

}
