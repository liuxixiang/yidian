package com.linken.newssdk.widget.cardview.toutiao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.DownloadStatusController;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.libraries.bra.BaseViewHolder;
import com.linken.newssdk.utils.ToastUtils;
import com.linken.newssdk.utils.support.ImageLoaderHelper;
import com.linken.newssdk.widget.cardview.adcard.base.ContentAdCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static com.linken.newssdk.libraries.ydvd.YdVideoPlayer.TAG;

public class ToutiaoAdViewHolder extends BaseViewHolder implements ContentAdCard {
    ImageView mIcon;
    Button mCreativeButton;
    TextView mTitle;
    TextView mDescription;
    TextView mSource;
    Button mStopButton;
    Button mRemoveButton;
    private Context mContext;
    private Map<ToutiaoAdViewHolder, TTAppDownloadListener> mTTAppDownloadListenerMap = new WeakHashMap<>();

    public ToutiaoAdViewHolder(View view) {
        super(view);
        mContext = view.getContext();
    }

    @Override
    public void onBind(AdvertisementCard card, String docid) {

    }

    @Override
    public void setDivider(boolean hasDivider) {

    }


    public void bindData(final ToutiaoAdViewHolder adViewHolder, TTFeedAd ad) {
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(adViewHolder.itemView);
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(adViewHolder.mCreativeButton);
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
//            creativeViewList.add(convertView);
        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ad.registerViewForInteraction((ViewGroup) adViewHolder.itemView, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    ToastUtils.showShort(mContext, "广告" + ad.getTitle() + "被点击");
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    ToastUtils.showShort(mContext, "广告" + ad.getTitle() + "被创意按钮被点击");
                }
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    ToastUtils.showShort(mContext, "广告" + ad.getTitle() + "展示");
                }
            }
        });
        adViewHolder.mTitle.setText(ad.getTitle());
        adViewHolder.mDescription.setText(ad.getDescription());
        adViewHolder.mSource.setText("头条---"+ (ad.getSource() == null ? "广告来源" : ad.getSource()));
        TTImage icon = ad.getIcon();
        if (icon != null && icon.isValid()) {
            ImageLoaderHelper.displayBigImage(adViewHolder.mIcon, icon.getImageUrl());
        }
        Button adCreativeButton = adViewHolder.mCreativeButton;
        switch (ad.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                if (mContext instanceof Activity) {
                    ad.setActivityForDownloadApp((Activity) mContext);
                }
                adCreativeButton.setVisibility(View.VISIBLE);
                adViewHolder.mStopButton.setVisibility(View.VISIBLE);
                adViewHolder.mRemoveButton.setVisibility(View.VISIBLE);
                bindDownloadListener(adCreativeButton, adViewHolder, ad);
                //绑定下载状态控制器
                bindDownLoadStatusController(adViewHolder, ad);
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText("立即拨打");
                adViewHolder.mStopButton.setVisibility(View.GONE);
                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                break;
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
//                    adCreativeButton.setVisibility(View.GONE);
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText("查看详情");
                adViewHolder.mStopButton.setVisibility(View.GONE);
                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                break;
            default:
                adCreativeButton.setVisibility(View.GONE);
                adViewHolder.mStopButton.setVisibility(View.GONE);
                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                ToastUtils.showShort(mContext, "交互类型异常");
        }
    }

    private void bindDownLoadStatusController(ToutiaoAdViewHolder adViewHolder, final TTFeedAd ad) {
        final DownloadStatusController controller = ad.getDownloadStatusController();
        adViewHolder.mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null) {
                    controller.changeDownloadStatus();
                    ToastUtils.showShort(mContext, "改变下载状态");
                    Log.d(TAG, "改变下载状态");
                }
            }
        });

        adViewHolder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null) {
                    controller.cancelDownload();
                    ToastUtils.showShort(mContext, "取消下载");
                    Log.d(TAG, "取消下载");
                }
            }
        });
    }

    private void bindDownloadListener(final Button adCreativeButton, final ToutiaoAdViewHolder adViewHolder, TTFeedAd ad) {
        TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                if (!isValid()) {
                    return;
                }
                adCreativeButton.setText("开始下载");
                adViewHolder.mStopButton.setText("开始下载");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                if (totalBytes <= 0L) {
                    adCreativeButton.setText("下载中 percent: 0");
                } else {
                    adCreativeButton.setText("下载中 percent: " + (currBytes * 100 / totalBytes));
                }
                adViewHolder.mStopButton.setText("下载中");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                if (totalBytes <= 0L) {
                    adCreativeButton.setText("下载中 percent: 0");
                } else {
                    adCreativeButton.setText("下载暂停 percent: " + (currBytes * 100 / totalBytes));
                }
                adViewHolder.mStopButton.setText("下载暂停");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                adCreativeButton.setText("重新下载");
                adViewHolder.mStopButton.setText("重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                adCreativeButton.setText("点击打开");
                adViewHolder.mStopButton.setText("点击打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                if (!isValid()) {
                    return;
                }
                adCreativeButton.setText("点击安装");
                adViewHolder.mStopButton.setText("点击安装");
            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            private boolean isValid() {
                return mTTAppDownloadListenerMap.get(adViewHolder) == this;
            }
        };
        //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
        ad.setDownloadListener(downloadListener); // 注册下载监听器
        mTTAppDownloadListenerMap.put(adViewHolder, downloadListener);
    }

}
