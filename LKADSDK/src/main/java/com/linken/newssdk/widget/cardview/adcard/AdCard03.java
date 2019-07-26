package com.linken.newssdk.widget.cardview.adcard;

import android.view.View;

import com.linken.newssdk.R;
import com.linken.newssdk.adapter.MultipleItemQuickAdapter;
import com.linken.newssdk.utils.support.ImageLoaderHelper;
import com.linken.newssdk.widget.cardview.adcard.base.AdBaseCard;
import com.linken.newssdk.widget.views.YdRatioImageView;


/**
 * Created by patrickleong on 4/13/15.
 */
public class AdCard03 extends AdBaseCard {
    private YdRatioImageView imgView;

    public AdCard03(MultipleItemQuickAdapter adapter, final View itemView) {
        super(adapter, itemView);
        imgView = itemView.findViewById(R.id.large_image);
    }

    @Override
    public void loadImage() {
        ImageLoaderHelper.displayBigImage(imgView, mAdCard.getImageUrl());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
