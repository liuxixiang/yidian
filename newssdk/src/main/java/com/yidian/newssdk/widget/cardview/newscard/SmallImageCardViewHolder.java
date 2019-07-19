package com.yidian.newssdk.widget.cardview.newscard;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.yidian.newssdk.R;
import com.yidian.newssdk.adapter.MultipleItemQuickAdapter;
import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.utils.ImageSize;
import com.yidian.newssdk.widget.cardview.base.WeMediaFeedCardBaseViewHolder;

import static android.view.View.VISIBLE;

/**
 * Created by yangjuan on 2017/3/28.
 */

// 对应布局 ydsdk_card_news_item
public class SmallImageCardViewHolder extends WeMediaFeedCardBaseViewHolder {
    private ImageView mImageView;
    private View imgFrame = null;
    //单个小图使用的东西
    private ImageView videoTag = null;
    private ImageView multiImgTag = null;

    public SmallImageCardViewHolder(View itemView){
        super(itemView);
        imgFrame = findView(R.id.news_image_frame);
        mImageView = findView(R.id.news_image);
        videoTag = findView(R.id.video_tag);
        multiImgTag = findView(R.id.multi_img_tag);
        findView(R.id.channel_news_normal_item).setOnClickListener(this);
    }

    public SmallImageCardViewHolder(Context context, MultipleItemQuickAdapter multipleItemQuickAdapter, View itemView){
        super(itemView);
        this.multipleItemQuickAdapter = multipleItemQuickAdapter;
        imgFrame = findView(R.id.news_image_frame);
        mImageView = findView(R.id.news_image);
        videoTag = findView(R.id.video_tag);
        multiImgTag = findView(R.id.multi_img_tag);
        findView(R.id.channel_news_normal_item).setOnClickListener(this);
    }


    @Override
    public void onBind(Card item,  MultipleItemQuickAdapter adapter) {
        super.onBind(item, adapter);
    }

    @Override
    protected void setData(Card item) {

    }

    private void setTitleTopPadding(int topPadding) {
        if (tvTitle != null) {
            tvTitle.setPadding(tvTitle.getPaddingLeft(), topPadding, tvTitle.getPaddingRight(), tvTitle.getPaddingBottom());
        }
    }

    @Override
    public void _onBind() {
        if (TextUtils.isEmpty(mCard.coverImage) && !TextUtils.isEmpty(mCard.image)) {
            mCard.coverImage = mCard.image;
        }
        if (pictureIsGone()) {
            setTitleTopPadding(mContext.getResources().getDimensionPixelOffset(R.dimen.ydsdk_title_top_padding));
            imgFrame.setVisibility(View.GONE);
        } else {
            setTitleTopPadding(0);
//            adjustSingleRightPictureSize(mImageView);
            imgFrame.setVisibility(VISIBLE);
            initImage(mImageView, mCard.coverImage, ImageSize.IMAGE_SIZE_SMALL);
        }
        // 小图单独处理的
        if (videoTag != null) {
            if (mCard.displayType == Card.DISPLAY_TYPE_VIDEO || mCard.displayType == Card.DISPLAY_TYPE_VIDEO_SMALL
                    || mCard.displayType == Card.DISPLAY_TYPE_VIDEO_BIG || mCard.displayType == Card.DISPLAY_TYPE_VIDEO_FLOW) {
                videoTag.setVisibility(View.VISIBLE);
            } else {
                videoTag.setVisibility(View.GONE);
            }
        }
        //在2G网络下显示多图标签
        if (multiImgTag != null) {
//            if (mCard.imageUrls != null && mCard.imageUrls.size() >= 3) {
//                multiImgTag.setVisibility(View.VISIBLE);
//            } else {
//                multiImgTag.setVisibility(View.GONE);
//            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.channel_news_normal_item) {
//           mPresenter.showDetailContent();
        }
        super.onClick(v);
    }

}
