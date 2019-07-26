package com.yidian.newssdk.core.detail.video;

import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yidian.newssdk.R;
import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.card.base.ContentCard;
import com.yidian.newssdk.data.card.video.VideoLiveCard;
import com.yidian.newssdk.libraries.bra.BaseQuickAdapter;
import com.yidian.newssdk.libraries.bra.BaseViewHolder;
import com.yidian.newssdk.libraries.ydvd.YdVideoPlayerStandard;
import com.yidian.newssdk.utils.support.ImageLoaderHelper;
import com.yidian.newssdk.utils.support.LayoutParamsHelper;
import com.yidian.newssdk.utils.LogUtils;

/**
 * Created by chenyichang on 2018/5/23.
 */

public class MoreVideoAdapter extends BaseQuickAdapter<Card, BaseViewHolder> {

    private boolean showShare = true;
    private boolean isFirst = true;
    private int mLastActivePos = -1;
    private VideoContractView contractView;

    public MoreVideoAdapter(VideoContractView contractView, int layoutResId) {
        super(layoutResId);
        this.contractView = contractView;
    }


    @Override
    protected void convert(final BaseViewHolder holder, final Card newsInfo) {

        if (holder.getConvertView() != null && holder.getConvertView().getTag() == null){
            holder.getConvertView().setTag(holder);
        }

        YdVideoPlayerStandard videoPlayer = holder.getView(R.id.jcps_video);
        ImageView maskIv = holder.getView(R.id.iv_mask);
        ImageView shareIV = holder.getView(R.id.iv_video_share);
        ImageView moreIV = holder.getView(R.id.iv_video_more);
        TextView titleTv = holder.getView(R.id.tv_video_title);
        TextView sourceTv = holder.getView(R.id.tv_video_source);
        FrameLayout frameLayout = holder.getView(R.id.fl_item_video);
        ImageView titleMaskIV = holder.getView(R.id.iv_title_mask);

        sourceTv.setText(((VideoLiveCard)newsInfo).source);
        if(TextUtils.isEmpty(newsInfo.title)) {
            newsInfo.title = "";
        }

        final YdVideoPlayerStandard jcPlayer = holder.getView(R.id.jcps_video);
        if(TextUtils.isEmpty(newsInfo.title)) {
            newsInfo.title = "";
        }

        jcPlayer.setOnStatusChangeListener(new YdVideoPlayerStandard.OnStatusChangeListener() {
            @Override
            public void onPlay() {
                
                MoreVideoAdapter.this.onVideoBrowseStrart(newsInfo);
                jcPlayer.showTitle();
            }

            @Override
            public void onError() {
            }

            @Override
            public void onAutoComplete() {
                jcPlayer.showTitle();
                MoreVideoAdapter.this.nextVideo(holder.getConvertView());
                contractView.hideNextVideo();
                MoreVideoAdapter.this.onVideoBrowseEnd(jcPlayer, newsInfo);
            }

            @Override
            public void onPause() {
                MoreVideoAdapter.this.onVideoBrowseEnd(jcPlayer, newsInfo);
            }
        });
        
//        jcPlayer.setOnPlayTimeListener(new OnPlayTimeListener() {
//            public void onLastThreeSecond() {
//                View view = ((VideosActivity)MoreVideoAdapter.this.mContext).hasNextVideo(holder.view);
//                if(view != null) {
//                    ((VideosActivity)MoreVideoAdapter.this.mContext).showNextVideo();
//                }
//
//            }
//        });
        jcPlayer.setTitleMask(titleMaskIV);
        titleTv.setText(newsInfo.title);
        maskIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreVideoAdapter.this.clickView(holder.getConvertView());
            }
        });
        if(this.showShare) {
            shareIV.setVisibility(View.VISIBLE);
            shareIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contractView.onShareClick(newsInfo);
                }
            });
        } else {
            shareIV.setVisibility(View.INVISIBLE);
        }

        moreIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contractView.onMoreFuncClick(newsInfo);
            }
        });
        LayoutParamsHelper.resetVideoParams(jcPlayer, 1, 0);
        jcPlayer.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (!TextUtils.isEmpty(newsInfo.image)) {
            if (!(newsInfo.image.startsWith("http://") || newsInfo.image.startsWith("https://"))) {
                newsInfo.image = "http://i3.go2yd.com/image/" + newsInfo.image;
            }
        }
        ImageLoaderHelper.displayImage(jcPlayer.thumbImageView, ((ContentCard) newsInfo).image);

        if(holder.getPosition() == 0 && this.isFirst) {
            this.activeVideo(holder);
            this.isFirst = false;
        }
    }

    private void nextVideo(View view) {
        contractView.nextVideo(view);
    }

    private void clickView(View view) {
        contractView.clickView(view);
    }

    private void onVideoBrowseEnd(YdVideoPlayerStandard jcPlayer, Card newsInfo) {

    }

    private void onVideoBrowseStrart(Card newsInfo) {

    }

    public void activeVideo(View view) {
        if(view != null) {
            BaseViewHolder holder = (BaseViewHolder) view.getTag();
            if (holder != null){
                this.activeVideo(holder);
            }
        }
    }

    private void activeVideo(BaseViewHolder holder) {
        final YdVideoPlayerStandard videoPlayer = holder.getView(R.id.jcps_video);
        final int adapterPos = holder.getAdapterPosition();
        if(adapterPos >= 0 && adapterPos < getData().size()) {
            if(adapterPos == this.mLastActivePos) {
                LogUtils.d("MoreVideoAdapter", "当前位置@pos = " + adapterPos + "已经被激活过了");
            } else {
                this.showVideo(holder);
                final VideoLiveCard newsInfo = (VideoLiveCard) getData().get(holder.getPosition());

                this.mLastActivePos = adapterPos;
                if(null != newsInfo) {
                    if(videoPlayer.currentState !=
                            YdVideoPlayerStandard.CURRENT_STATE_PREPARING_CHANGING_URL) {
                        videoPlayer.setUp(newsInfo.videoUrl, YdVideoPlayerStandard.SCREEN_WINDOW_LIST, new Object[]{newsInfo.title});
                        videoPlayer.startVideo();
                    }
                }
            }
        } else {
            LogUtils.d("MoreVideoAdapter", "当前位置@pos = " + adapterPos + "越界");
        }
    }

    private void showVideo(BaseViewHolder holder) {
        final ImageView mask = holder.getView(R.id.iv_mask);
        if(mask.getVisibility() != View.INVISIBLE) {
            mask.clearAnimation();
            if(mask.getAnimation() == null) {
                AlphaAnimation animation = new AlphaAnimation(1.0F, 0.0F);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.setDuration(300L);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mask.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                mask.setAnimation(animation);
                animation.start();
            }

        }
    }

    public void showVideo(View view) {
        if(view != null) {
            BaseViewHolder holder = (BaseViewHolder) view.getTag();
            this.showVideo(holder);
        }
    }

    public void vanishVideo(View view){
        if(view != null) {
            BaseViewHolder holder = (BaseViewHolder) view.getTag();
            this.vanishVideo(holder);
        }
    }

    public void vanishVideo(BaseViewHolder holder) {
        final ImageView mask = holder.getView(R.id.iv_mask);
        final YdVideoPlayerStandard videoPlayer = holder.getView(R.id.jcps_video);
        if(videoPlayer.currentState == YdVideoPlayerStandard.CURRENT_STATE_PREPARING_CHANGING_URL) {
            videoPlayer.startButton.performClick();
        }

        if(mask.getVisibility() != View.VISIBLE) {
            mask.clearAnimation();
            if(mask.getAnimation() == null) {
                AlphaAnimation animation = new AlphaAnimation(0.0F, 1.0F);
                animation.setDuration(300L);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mask.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                mask.setAnimation(animation);
                animation.cancel();
                animation.start();
            }

        }
    }
}
