package com.linken.newssdk.widget.cardview;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linken.newssdk.R;
import com.linken.newssdk.adapter.MultipleItemQuickAdapter;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.widget.cardview.base.WeMediaFeedCardBaseViewHolder;

/**
 * Created by liuyue on 2017/4/12.
 */

public class WemediaJokeViewHolder extends WeMediaFeedCardBaseViewHolder {

    private ImageView imgJokePic;
    private ViewGroup frameJokePic;
    private TextView tvClickToShowFull;

    public WemediaJokeViewHolder(MultipleItemQuickAdapter multipleItemQuickAdapter, View itemView) {
        super(itemView);
        this.multipleItemQuickAdapter = multipleItemQuickAdapter;
    }

    @Override
    protected void setData(Card item) {

    }

    @Override
    public void _onBind() {
        if (tvTitle != null) {
            // set title
            if (!TextUtils.isEmpty(mCard.summary)) {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(mCard.summary);
//                boolean hasRead = GlobalDataCache.getInstance().isDocAlreadyRead(
//                        mCard.isSticky() ? mCard.getStickiedDocId() : mCard.id);
//                setTitleColor(tvTitle, hasRead);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
        }

        frameJokePic = (ViewGroup) itemView.findViewById(R.id.picture_joke);
        imgJokePic = (ImageView) itemView.findViewById(R.id.joke_img_view);
        tvClickToShowFull = (TextView) itemView.findViewById(R.id.click_to_show_full);
        findView(R.id.channel_news_normal_item).setOnClickListener(this);
//
//        if (mCard instanceof JokeCard) {
//            JokeCard jokeCard = (JokeCard) mCard;
//
//            if (!TextUtils.isEmpty(jokeCard.image)) {
//                frameJokePic.setVisibility(View.VISIBLE);
//                imgJokePic.setVisibility(View.VISIBLE);
//                PictureSize whPicture = jokeCard.pictureArrayMap.get(jokeCard.image);
//                showJokePic(jokeCard.image, whPicture);
//                frameJokePic.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mPresenter.showBigPic();
//                    }
//                });
//            } else {
//                frameJokePic.setVisibility(View.GONE);
//                imgJokePic.setVisibility(View.GONE);
//                tvClickToShowFull.setVisibility(View.GONE);
//            }
//        }
    }

//    private void showJokePic(String url, PictureSize whPicture) {
//        ViewGroup.LayoutParams params = imgJokePic.getLayoutParams();
//        params.width = mScreenWidth - mLeftPadding * 2;
//        if (whPicture.isNeedCut()) {
//            params.height = (int) (params.width / 1.5);
//            imgJokePic.setLayoutParams(params);
//            imgJokePic.setCustomizedImageSize(whPicture.width, (int) (whPicture.width / 1.5));
//            imgJokePic.setImageUrl(url, ImageSize.IMAGE_SIZE_CUSTOMIZED, false);
//
//            tvClickToShowFull.setVisibility(View.VISIBLE);
//        } else {
//            params.height = params.width * whPicture.height / whPicture.width;
//            imgJokePic.setLayoutParams(params);
//            imgJokePic.setCustomizedImageSize(whPicture.width, whPicture.height);
//            imgJokePic.setImageUrl(url, ImageSize.IMAGE_SIZE_CUSTOMIZED, false);
//            tvClickToShowFull.setVisibility(View.GONE);
//        }
//    }
}
