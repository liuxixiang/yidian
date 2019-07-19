package com.yidian.newssdk.widget.cardview;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yidian.newssdk.R;
import com.yidian.newssdk.adapter.MultipleItemQuickAdapter;
import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.card.pic.WeMediaPictureGalleryCard;
import com.yidian.newssdk.utils.support.ImageLoaderHelper;
import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.widget.cardview.base.WeMediaFeedCardBaseViewHolder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by yangjuan on 2017/3/28.
 */

// 对应布局 card_picturegallery_outsidechannel_bigimage
public class PictureGalleryCardViewHolder extends WeMediaFeedCardBaseViewHolder {
    private TextView pictureNumber = null;
    private ImageView img;
    private static int mWidth = -1;

    public PictureGalleryCardViewHolder(MultipleItemQuickAdapter multipleItemQuickAdapter, View itemView) {
        super(itemView);
        this.multipleItemQuickAdapter = multipleItemQuickAdapter;
        pictureNumber = findView(R.id.picture_number);
        img = findView(R.id.news_image);
        findView(R.id.channel_news_normal_item).setOnClickListener(this);
        if (mWidth == -1) {
            getScreenParameters(ContextUtils.getApplicationContext());
        }
    }

    @Override
    protected void setData(Card item) {

    }

    @Override
    public void _onBind() {
        if (TextUtils.isEmpty(mCard.image)) {
            img.setVisibility(GONE);
        } else {
            img.setVisibility(VISIBLE);
//            float ratio = (float) ((PictureGalleryCard) mCard).height / ((PictureGalleryCard) mCard).width;
//            img.setLengthWidthRatio(ratio);
            ImageLoaderHelper.displayBigImage(img, mCard.image);
//            img.setCustomizedImageSize(1000, 500);
//            img.setImageUrl(mCard.image, ImageSize.IMAGE_SIZE_CUSTOMIZED, false);
        }
        // 如果允许显示图片，则显示张数
        if (img.getVisibility() == View.VISIBLE && mCard instanceof WeMediaPictureGalleryCard
                && ((WeMediaPictureGalleryCard) mCard).gallery_items != 0) {
            pictureNumber.setVisibility(View.VISIBLE);
            pictureNumber.setText(mContext.getString(R.string.ydsdk_picture_gallery_unit, ((WeMediaPictureGalleryCard) mCard).gallery_items));
        } else {
            pictureNumber.setVisibility(View.GONE);
        }
    }

    private static void getScreenParameters(Context context) {
        // get the screen resolution and show it
        WindowManager wmg = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display display = wmg.getDefaultDisplay();
        int width;
        if (Build.VERSION.SDK_INT >= 13) {
            Point screenSize = new Point();
            display.getSize(screenSize);
            width = screenSize.x;
        } else {
            width = display.getWidth();
        }
        mWidth = (int) (width - 2 * context.getResources().getDimension(R.dimen.ydsdk_news_list_beauty_padding));
    }

}
