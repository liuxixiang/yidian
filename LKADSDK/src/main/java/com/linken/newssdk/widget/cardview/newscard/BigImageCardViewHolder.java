package com.linken.newssdk.widget.cardview.newscard;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linken.newssdk.R;
import com.linken.newssdk.adapter.MultipleItemQuickAdapter;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.utils.support.ImageLoaderHelper;
import com.linken.newssdk.widget.cardview.base.WeMediaFeedCardBaseViewHolder;
import com.linken.newssdk.widget.views.YdRatioImageView;

import static android.view.View.GONE;

/**
 * Created by yangjuan on 2017/3/28.
 */

// 对应布局 yidianhao_big_image_card_view OK
public class BigImageCardViewHolder extends WeMediaFeedCardBaseViewHolder {
    private TextView tvDescribe;
    private ImageView mImageView;
    private View mChannelNewsItem;

    public BigImageCardViewHolder(MultipleItemQuickAdapter multipleItemQuickAdapter, View itemView) {
        super(itemView);
        this.multipleItemQuickAdapter = multipleItemQuickAdapter;
//        FeedUiController.getInstance().adjustCardViewPadding(itemView);
        tvDescribe = findView(R.id.news_describe);
        mImageView = findView(R.id.large_news_image);
        mChannelNewsItem = findView(R.id.channel_news_normal_item);
        mChannelNewsItem.setOnClickListener(this);
        if (mImageView instanceof YdRatioImageView) {
            ((YdRatioImageView) mImageView).setLengthWidthRatio(0.5625f);
        }
    }

    @Override
    protected void setData(Card item) {

    }


    @Override
    public void _onBind() {
        tvDescribe.setText(mCard.title);
        if (TextUtils.isEmpty(mCard.coverImage)) {
            mImageView.setVisibility(GONE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            ImageLoaderHelper.displayBigImage(mImageView, mCard.coverImage);
//            mImageView.setCustomizedImageSize(960, 540);
//            initImage(mImageView, mCard.coverImage, ImageSize.IMAGE_SIZE_CUSTOMIZED);
        }
    }

}