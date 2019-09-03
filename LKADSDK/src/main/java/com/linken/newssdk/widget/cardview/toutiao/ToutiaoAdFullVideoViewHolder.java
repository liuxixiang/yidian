package com.linken.newssdk.widget.cardview.toutiao;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.NewsConfig;
import com.linken.newssdk.R;
import com.linken.newssdk.toutiao.TTAdManagerHolder;
import com.linken.newssdk.utils.ToastUtils;

public class ToutiaoAdFullVideoViewHolder extends ToutiaoAdViewHolder {
    private TTRewardVideoAd mttRewardVideoAd;
    private Context mContext;
    private String mCodeId = NewsConfig.CODE_ID_REWARD_VIDEO;
    private boolean mHasShowDownloadActive = false;

    public ToutiaoAdFullVideoViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
        mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
        mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
        mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);

    }

    @Override
    public void onBind(AdvertisementCard card, String docid) {
        super.onBind(card, docid);
        mTitle.setText(card.title);
        mSource.setText(card.source);
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loaloadTTRewardVideodAd(mCodeId, TTAdConstant.VERTICAL);
            }
        });
    }

    public void showVideo() {
        if (mttRewardVideoAd != null) {
            //step6:在获取到广告后展示
            mttRewardVideoAd.showRewardVideoAd((Activity) mContext);
            mttRewardVideoAd = null;
        } else {
            ToastUtils.showShort(mContext, "请先加载广告");
        }

    }


    private void loaloadTTRewardVideodAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        TTAdManagerHolder.get().createAdNative(mContext).loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                ToastUtils.showShort(mContext, message);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                showVideo();
                ToastUtils.showShort(mContext, "rewardVideoAd video cached");
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                ToastUtils.showShort(mContext, "rewardVideoAd loaded");
                mttRewardVideoAd = ad;
//                mttRewardVideoAd.setShowDownLoadBar(false);
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        ToastUtils.showShort(mContext, "rewardVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        ToastUtils.showShort(mContext, "rewardVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        ToastUtils.showShort(mContext, "rewardVideoAd close");
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        ToastUtils.showShort(mContext, "rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        ToastUtils.showShort(mContext, "rewardVideoAd error");
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        ToastUtils.showShort(mContext, "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName);
                    }

                    @Override
                    public void onSkippedVideo() {
                        ToastUtils.showShort(mContext, "rewardVideoAd has onSkippedVideo");
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            ToastUtils.showShort(mContext, "下载中，点击下载区域暂停");
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        ToastUtils.showShort(mContext, "下载暂停，点击下载区域继续");
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        ToastUtils.showShort(mContext, "下载失败，点击下载区域重新下载");
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        ToastUtils.showShort(mContext, "下载完成，点击下载区域重新下载");
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        ToastUtils.showShort(mContext, "安装完成，点击下载区域打开");
                    }
                });
            }
        });

    }


}
