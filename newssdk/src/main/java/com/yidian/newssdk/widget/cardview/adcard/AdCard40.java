package com.yidian.newssdk.widget.cardview.adcard;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.yidian.newssdk.R;
import com.yidian.newssdk.adapter.MultipleItemQuickAdapter;
import com.yidian.newssdk.utils.support.ImageLoaderHelper;
import com.yidian.newssdk.widget.cardview.adcard.base.AdBaseCard;


/**
 * Created by patrickleong on 4/13/15.
 */
public class AdCard40 extends AdBaseCard {
    ImageView imgViews[];
    private static final int MAX_IMAGE_COUNT = 3;

    public AdCard40(MultipleItemQuickAdapter adapter, final View itemView) {
        super(adapter, itemView);
        imgViews = new ImageView[3];
        imgViews[0] = itemView.findViewById(R.id.news_img1);
        imgViews[1] = itemView.findViewById(R.id.news_img2);
        imgViews[2] = itemView.findViewById(R.id.news_img3);
    }

    @Override
    public void loadImage() {
        if (mAdCard.image_urls != null) {
            int length = MAX_IMAGE_COUNT;
            if (mAdCard.image_urls.length < MAX_IMAGE_COUNT) {
                length = mAdCard.image_urls.length;
            }
            for (int i = 0; i < length; i++) {
                String url = mAdCard.image_urls[i];
                if (!TextUtils.isEmpty(url)) {
                    ImageLoaderHelper.displayImage(imgViews[i], url);
                }
            }
        }

    }
}
