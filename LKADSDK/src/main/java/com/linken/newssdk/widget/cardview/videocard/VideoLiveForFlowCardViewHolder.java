package com.linken.newssdk.widget.cardview.videocard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.linken.newssdk.R;
import com.linken.newssdk.YdCustomConfigure;
import com.linken.newssdk.adapter.MultipleItemQuickAdapter;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.card.video.VideoLiveCard;
import com.linken.newssdk.libraries.bra.entity.MultiItemEntity;
import com.linken.newssdk.utils.support.ImageLoaderHelper;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.DensityUtil;
import com.linken.newssdk.widget.cardview.base.WeMediaFeedCardBaseViewHolder;
import com.linken.newssdk.widget.feedback.normal.CardBottomPanelWrapper;
import com.linken.newssdk.widget.views.YdRatioImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by yangjuan on 2017/3/28.
 */

// 对应布局 card_video_live_flow  有问题，具体的其实还没赋值
public class VideoLiveForFlowCardViewHolder extends WeMediaFeedCardBaseViewHolder {
    //For Video zone
    protected TextView mVideoTitle;
    protected YdRatioImageView mVideoImage;
    protected ImageView mVideoPlayButton;
    protected TextView mVideoInfo;
    protected int mSourceType ;
    protected View mTitleBackground;
    protected View mChannelVideoLiveItem = null;
    private TextView mNewsTitle;

    public <T extends MultiItemEntity> VideoLiveForFlowCardViewHolder(Context mContext, MultipleItemQuickAdapter tMultipleItemQuickAdapter, View itemView) {
        super(itemView);
        initWidgetsForVideoZone();
        mNewsTitle = findView(R.id.news_title);
        mVideoTitle = findView(R.id.video_title);
        findView(R.id.title_background).setVisibility(GONE);
        mChannelVideoLiveItem = findView(R.id.channel_video_live_item);
        mChannelVideoLiveItem.setOnClickListener(this);

        if (DensityUtil.getScreenWidth(ContextUtils.getApplicationContext()) < 481) {
            mNewsTitle.setTextSize(16.5f);
        }
    }

    @Override
    protected void setData(Card item) {

    }


    @Override
    public void _onBind() {
        if (!TextUtils.isEmpty(mCard.title)) {
            mNewsTitle.setText(mCard.title);
        }
        setTitleColor(mNewsTitle, false);

        mVideoTitle.setVisibility(GONE);
        mNewsTitle.setTextSize(YdCustomConfigure.getInstance().getFontSize());
        int titleWidth = (int) (Math.min(DensityUtil.getScreenWidth(ContextUtils.getApplicationContext()), DensityUtil.getScreenHeight(ContextUtils.getApplicationContext()) - ContextUtils.getApplicationContext()
                .getResources().getDimension(R.dimen.ydsdk_news_list_padding_left) * 2));
        mBottomPanelWrapper.initPanelWidget(null, mCard, titleWidth, true, mSourceType);
        mBottomPanelWrapper.showPanelWithfbButtonShowInfo(new CardBottomPanelWrapper.IPanelAction() {
            @Override
            public void onClickBadFeedback(boolean reasonGiven) {
                 deleteDoc(!reasonGiven);
            }

            @Override
            public void onClickCard() {

            }
        }, false);

        showItemDataForVideoZone();
    }

    private void showItemDataForVideoZone() {
        if (!TextUtils.isEmpty(mCard.image)) {
            mVideoImage.setVisibility(VISIBLE);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mVideoImage.setLayoutParams(layoutParams);
            ImageLoaderHelper.displayBigImage(mVideoImage, mCard.image);
        }

        mVideoTitle.setTextSize(YdCustomConfigure.getInstance().getFontSize());

        if (!TextUtils.isEmpty(mCard.title)) {
            mVideoTitle.setText(mCard.title);
        }

        mVideoInfo.setVisibility(GONE);
        if (mCard instanceof VideoLiveCard) {
            String playTime = getPlayTimeString(((VideoLiveCard) mCard).playTimes, 'W');
            String duration = getDurationString(((VideoLiveCard) mCard).videoDuration);
            String infoString = null;
            boolean showEye = false;
            // @playTIme | duration
            if (!TextUtils.isEmpty(playTime) && !TextUtils.isEmpty(duration)) {
                showEye = true;
                infoString = playTime + " | " + duration;
            } else if (!TextUtils.isEmpty(playTime)) {
                showEye = true;
                infoString = playTime;
            } else if (!TextUtils.isEmpty(duration)) {
                showEye = false;
                infoString = duration;
            }
            mVideoInfo.setText(infoString);
            mVideoInfo.setVisibility(TextUtils.isEmpty(infoString) ? GONE : VISIBLE);
            mVideoInfo.setCompoundDrawablesWithIntrinsicBounds(
                    showEye ? R.drawable.ydsdk_theme_kuaishou_eye : 0,
                    0, 0, 0);
            mVideoInfo.setCompoundDrawablePadding(showEye ? DensityUtil.dp2px(4) : 0);
        }

    }

    private void initWidgetsForVideoZone() {
        mVideoTitle = findView(R.id.video_title);
        mVideoTitle.setOnClickListener(this);
        mVideoImage = findView(R.id.large_image);
        mVideoImage.setOnClickListener(this);
        mVideoPlayButton = findView(R.id.video_play_button);
        mVideoInfo = findView(R.id.video_duration);
        mVideoPlayButton.setOnClickListener(this);
        mTitleBackground = findView(R.id.title_background);
        mTitleBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams lp = mTitleBackground.getLayoutParams();
                lp.height = (int) (mVideoImage.getHeight() * 0.35);
                mTitleBackground.setLayoutParams(lp);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mTitleBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    mTitleBackground.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    /**
     * 获取时长的文本内容
     * @param videoDuration 时长
     * @return 转化为hh:mm:ss形式
     */
    public static String getDurationString(int videoDuration) {
        if (videoDuration <= 0) {
            return null;
        } else {
            String result;
            int seconds = videoDuration % 60;
            int minutes = videoDuration / 60;
            if (minutes < 60) {
                result = String.format("%02d:%02d", minutes, seconds);
            } else {
                int hours = minutes / 60;
                minutes = minutes % 60;
                result = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
            return result;
        }
    }

    private static final float TEN_THOUSAND = 10_000f;

    @SuppressLint("DefaultLocale")
    public static String getPlayTimeString(int playTime, char unit) {
        if (playTime <= 0) {
            return null;
        }
        if (playTime > TEN_THOUSAND) {
            return String.format("%.1f", (playTime / TEN_THOUSAND)) + unit;
        }
        return String.valueOf(playTime);
    }
}