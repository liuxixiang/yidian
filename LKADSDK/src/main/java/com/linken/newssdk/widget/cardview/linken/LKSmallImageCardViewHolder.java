package com.linken.newssdk.widget.cardview.linken;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linken.newssdk.R;
import com.linken.newssdk.adapter.MultipleItemQuickAdapter;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.theme.ThemeManager;
import com.linken.newssdk.utils.ImageSize;
import com.linken.newssdk.widget.cardview.base.WeMediaFeedCardBaseViewHolder;

import static android.view.View.VISIBLE;

/**
 * Created by yangjuan on 2017/3/28.
 */

// 对应布局 ydsdk_card_news_item
public class LKSmallImageCardViewHolder extends WeMediaFeedCardBaseViewHolder {
    private ImageView mImageView;
    private View imgFrame;
    //单个小图使用的东西
    private ImageView videoTag;
    private ImageView multiImgTag;
    private TextView tagName;

    public LKSmallImageCardViewHolder(View itemView) {
        super(itemView);
        mShowFbButton = false;
        imgFrame = findView(R.id.news_image_frame);
        mImageView = findView(R.id.news_image);
        videoTag = findView(R.id.video_tag);
        multiImgTag = findView(R.id.multi_img_tag);
        tagName = findView(R.id.tag_name);
        findView(R.id.channel_news_normal_item).setOnClickListener(this);
    }

    public LKSmallImageCardViewHolder(Context context, MultipleItemQuickAdapter multipleItemQuickAdapter, View itemView) {
        super(itemView);
        mShowFbButton = false;
        this.multipleItemQuickAdapter = multipleItemQuickAdapter;
        imgFrame = findView(R.id.news_image_frame);
        mImageView = findView(R.id.news_image);
        videoTag = findView(R.id.video_tag);
        multiImgTag = findView(R.id.multi_img_tag);
        tagName = findView(R.id.tag_name);
        findView(R.id.channel_news_normal_item).setOnClickListener(this);
    }


    @Override
    public void onBind(Card item, MultipleItemQuickAdapter adapter) {
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
        if (!TextUtils.isEmpty(mCard.tag_name) && !"null".equals(mCard.tag_name.toLowerCase().toLowerCase())) {
            tagName.setVisibility(VISIBLE);
            tagName.setText(mCard.tag_name);

            int color = ThemeManager.getColor(itemView.getContext(), ThemeManager.getTheme(), R.styleable.NewsSDKTheme_newssdk_sliding_tab_checked_txt_color, R.color.ydsdk_list_item_other_text);
            tagName.setTextColor(color);
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
