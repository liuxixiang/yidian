package com.linken.newssdk.widget.cardview.linken;

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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by yangjuan on 2017/3/28.
 */
// 对应布局 card_news_item_imgline
public class LKMultiImageCardViewHolder extends WeMediaFeedCardBaseViewHolder {
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private View imgLine;
    private View mChannelNewsItem;
    private TextView tagName;

    public LKMultiImageCardViewHolder(MultipleItemQuickAdapter multipleItemQuickAdapter, View itemView) {
        super(itemView);
        mShowFbButton = false;
        this.multipleItemQuickAdapter = multipleItemQuickAdapter;
        img1 = findView(R.id.news_img1);
        img2 = findView(R.id.news_img2);
        img3 = findView(R.id.news_img3);
        imgLine = findView(R.id.imgLine);
        findView(R.id.picture_number).setVisibility(GONE);
        mChannelNewsItem = findView(R.id.channel_news_normal_item);
        tagName = findView(R.id.tag_name);
        mChannelNewsItem.setOnClickListener(this);
    }

    @Override
    protected void setData(Card item) {

    }


    @Override
    public void onBind(Card item, MultipleItemQuickAdapter adapter) {
        super.onBind(item, adapter);
        findView(R.id.btnToggle).setVisibility(GONE);
    }

    @Override
    public void _onBind() {
        if (!TextUtils.isEmpty(mCard.tag_name) && !"null".equals(mCard.tag_name)) {
            tagName.setVisibility(VISIBLE);
            tagName.setText(mCard.tag_name);
            int color = ThemeManager.getColor(itemView.getContext(), ThemeManager.getTheme(), R.styleable.NewsSDKTheme_newssdk_sliding_tab_checked_txt_color, R.color.ydsdk_list_item_other_text);
            tagName.setTextColor(color);
        }
        if (mCard.coverImages == null || mCard.coverImages.size() < 3) {
            imgLine.setVisibility(GONE);
        } else {
            //多图
            imgLine.setVisibility(VISIBLE);
            initImage(img1, mCard.coverImages.get(0), ImageSize.IMAGE_SIZE_SMALL);
            initImage(img2, mCard.coverImages.get(1), ImageSize.IMAGE_SIZE_SMALL);
            initImage(img3, mCard.coverImages.get(2), ImageSize.IMAGE_SIZE_SMALL);
        }
    }
}
