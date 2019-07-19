package com.yidian.newssdk.widget.cardview.newscard;

import android.view.View;
import android.widget.ImageView;

import com.yidian.newssdk.R;
import com.yidian.newssdk.adapter.MultipleItemQuickAdapter;
import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.utils.ImageSize;
import com.yidian.newssdk.widget.cardview.base.WeMediaFeedCardBaseViewHolder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by yangjuan on 2017/3/28.
 */
// 对应布局 card_news_item_imgline
public class MultiImageCardViewHolder extends WeMediaFeedCardBaseViewHolder {
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private View imgLine;
    private View mChannelNewsItem;

    public MultiImageCardViewHolder(MultipleItemQuickAdapter multipleItemQuickAdapter, View itemView) {
        super(itemView);
        this.multipleItemQuickAdapter = multipleItemQuickAdapter;
        img1 = findView(R.id.news_img1);
        img2 = findView(R.id.news_img2);
        img3 = findView(R.id.news_img3);
        imgLine = findView(R.id.imgLine);
        findView(R.id.picture_number).setVisibility(GONE);
        mChannelNewsItem = findView(R.id.channel_news_normal_item);
        mChannelNewsItem.setOnClickListener(this);
    }

    @Override
    protected void setData(Card item) {

    }


    @Override
    public void onBind(Card item,  MultipleItemQuickAdapter adapter) {
        super.onBind(item, adapter);
    }

    @Override
    public void _onBind() {
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
