package com.linken.newssdk.widget.views;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.R;
import com.linken.newssdk.core.ad.AdvertisementUtil;
import com.linken.newssdk.core.ad.ViewReportManager;
import com.linken.newssdk.data.ad.ADConstants;
import com.linken.newssdk.core.ad.AdvertisementModule;
import com.linken.newssdk.data.ad.event.DownloadEvent;
import com.linken.newssdk.utils.InstallPackageFileUtil;
import com.linken.newssdk.utils.WeakHandler;
import com.linken.newssdk.utils.action.AdActionHelper;


import java.util.UUID;


/**
 * Created by liuyue on 16/2/25.
 */
public class AdDownloadButton implements View.OnClickListener {
    private static final int PACKAGE_SCHEDULE_DURATION = 6 * 1000;//下载成功后，每隔6s检查一次app是否安装成功
    private static final int PACKAGE_RETRY_LIMIT = 20;//2分钟没有安装成功的话，就停止package check
    private TextView mDownloadTextView;
    private AdvertisementCard mAdData;
    private boolean mbDownloadInProgress;
    private int retryCount = 0;
    private int lastEvent = DownloadEvent.STATUS_DEFAULT;
    private ViewReportManager mViewReportManager;

    private AdActionHelper mActionHelper;

    public AdDownloadButton(TextView textView) {
        if (textView == null) {
            throw new NullPointerException("AdDownloadButton's TextView Can't Be Null");
        }

        mDownloadTextView = textView;
        mDownloadTextView.setOnClickListener(this);
    }

    public void bindData(AdvertisementCard card, ViewReportManager viewReportManager) {
        mViewReportManager = viewReportManager;
        mAdData = card;
        if (TextUtils.isEmpty(mAdData.huodongButtonName)) {
            mAdData.huodongButtonName = mDownloadTextView.getResources().getString(R.string.ydsdk_ad_download_default);
        }
        mDownloadTextView.setText(mAdData.huodongButtonName);
        mActionHelper = AdActionHelper.newInstance(mAdData);
        lastEvent = DownloadEvent.STATUS_DEFAULT;
        mDownloadTextView.removeCallbacks(mProgressRunnable);
        updateDownloadStatusFromMiniCardStore(card);
        if (AdvertisementUtil.appCanOpen(card, mDownloadTextView.getContext())) {
            card.setDownloadStatus(DownloadEvent.STATUS_OPEN);
            card.setDownloadProgress(100);
            lastEvent = card.getDownloadStatus();
        } else {
            if (shouldUseMiniCard()) {
                if (card.getDownloadStatus() == DownloadEvent.STATUS_OPEN) {
                    card.setDownloadStatus(DownloadEvent.STATUS_DEFAULT);
                    card.setDownloadProgress(0);
                    lastEvent = card.getDownloadStatus();
                }
            } else if (!TextUtils.isEmpty(InstallPackageFileUtil.waitForInstall(card))) {
                card.setDownloadStatus(DownloadEvent.STATUS_INSTALL);
                card.setDownloadProgress(100);
                lastEvent = card.getDownloadStatus();
            }
        }

        onProgressChange(card.getDownloadStatus(), card.getDownloadProgress());
    }

    private void updateDownloadStatusFromMiniCardStore(AdvertisementCard card) {
        if (shouldUseMiniCard()) {
//            AdvertisementCard cardInStore = MiniCardAdCardStore.getInstance().get(card.getAid());
//            if (cardInStore != null && cardInStore.getAid() == card.getAid()) {
//                cardInStore.setDownloadProgress(card.getDownloadProgress());
//                cardInStore.setDownloadStatus(card.getDownloadStatus());
//                lastEvent = cardInStore.getDownloadStatus();
//            }
        }
    }

    private boolean shouldUseMiniCard() {
        boolean shouldUseMiniCard = AdvertisementModule.getInstance().isZixunBuild();
//        if (mActionHelper instanceof XiaomiAdActionHelper) {
//            XiaomiAdActionHelper actionHelper = (XiaomiAdActionHelper) mActionHelper;
//            shouldUseMiniCard |= actionHelper.isMiniCardServiceConnected();
//        }
        return shouldUseMiniCard;
    }

    @SuppressLint("HandlerLeak")
    private WeakHandler<AdDownloadButton> schedulePackageCheck = new WeakHandler<AdDownloadButton>(this) {
        @Override
        protected void handleMessage(Message msg, @NonNull AdDownloadButton referent) {
            if (msg.what == 0) {
                retryCount++;
                if (retryCount < PACKAGE_RETRY_LIMIT) {
                    if (AdvertisementUtil.isAppInstalledAccordingToPackageName(mAdData.getPackageName(), mDownloadTextView.getContext())) {
                        onProgressChange(DownloadEvent.STATUS_OPEN, 100);
                    } else {
                        sendEmptyMessageDelayed(0, PACKAGE_SCHEDULE_DURATION);
                    }
                } else {
                    //如果过了2分钟APP还没有安装成功，则认为用户放弃了这次安装
                    onProgressChange(DownloadEvent.STATUS_INSTALL, 100);
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        mViewReportManager.doViewReport();
        if (Build.VERSION.SDK_INT < 9) {
            return;
        }

        if (mbDownloadInProgress) {
            return;
        }

        if (lastEvent == DownloadEvent.STATUS_FAILED) {
            onProgressChange(DownloadEvent.STATUS_DEFAULT, -1);
            return;
        }

        if (lastEvent == DownloadEvent.STATUS_OPEN) {
            AdvertisementUtil.reportClickEvent(mAdData, true, null);
            AdvertisementUtil.reportReserveEvent(mAdData, null, null, UUID.randomUUID().toString());
            AdActionHelper.newInstance(mAdData).directOpenApk(v.getContext());
            return;
        }

        if (lastEvent == DownloadEvent.STATUS_INSTALL || lastEvent == DownloadEvent.STATUS_SUCCESSFUL) {
            //点击“等待安装”后，要么开始安装，说明安装包还在；要么无法开始安装，说明安装包不在，则需要重新开始下载
            String fileUri = InstallPackageFileUtil.waitForInstall(mAdData);
            if (!TextUtils.isEmpty(fileUri)) {
                InstallPackageFileUtil.trySilentInstall(mDownloadTextView.getContext(), fileUri, mAdData);
                onProgressChange(DownloadEvent.STATUS_INSTALLING, 100);
            } else {
                onProgressChange(DownloadEvent.STATUS_DEFAULT, 0);
            }
            return;
        }

        if (lastEvent == DownloadEvent.STATUS_DEFAULT) {
            AdvertisementUtil.reportReserveEvent(mAdData, null, null, UUID.randomUUID().toString());
            mActionHelper.downloadApk(mDownloadTextView.getContext());
        }
    }

    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            mbDownloadInProgress = false;
            onProgressChange(DownloadEvent.STATUS_DEFAULT, -1);
        }
    };

    public void onProgressChange(Integer status, Integer downloadProgress) {
        if (status == null || downloadProgress == null) {
            return;
        }
        android.util.Log.d(ADConstants.ADVERTISEMENT_LOG, "onProgressChange : [status - " + status + ", progress - " + downloadProgress + ", time - " + System.currentTimeMillis() + "]");
        String oldMsg = mDownloadTextView.getText().toString();
        String message = null;
        if (status == DownloadEvent.STATUS_PAUSED) {
            message = mDownloadTextView.getResources().getString(R.string.ydsdk_ad_download_pasued);
        } else if (status == DownloadEvent.STATUS_FAILED) {
            message = mDownloadTextView.getResources().getString(R.string.ydsdk_ad_download_failed);
            mDownloadTextView.postDelayed(mProgressRunnable, 1000);
        } else if (status == DownloadEvent.STATUS_PENDING) {
            message = mDownloadTextView.getResources().getString(R.string.ydsdk_ad_download_pending);
        } else if (status == DownloadEvent.STATUS_DEFAULT) {
            message = mAdData.huodongButtonName;
        } else if (status == DownloadEvent.STATUS_OPEN) {
            //针对deeplink可以打开，packageName对应的app并没有安装，按钮文案为初始配置文案
            if (AdvertisementUtil.isAppInstalledAccordingToPackageName(mAdData.getPackageName(), mDownloadTextView.getContext())) {
                message = mDownloadTextView.getResources().getString(R.string.ydsdk_ad_download_open);
            } else {
                message = mAdData.huodongButtonName;
            }
        } else if (status == DownloadEvent.STATUS_INSTALLING) {
            message = mDownloadTextView.getResources().getString(R.string.ydsdk_ad_download_installing);
            retryCount = 0;
            schedulePackageCheck.sendEmptyMessageDelayed(0, PACKAGE_SCHEDULE_DURATION);
        } else if (status == DownloadEvent.STATUS_INSTALL || status == DownloadEvent.STATUS_SUCCESSFUL) {
            String fileUri = InstallPackageFileUtil.waitForInstall(mAdData);
            if (TextUtils.isEmpty(fileUri)) {
                onProgressChange(DownloadEvent.STATUS_DEFAULT, -1);
                return;
            }
            message = mDownloadTextView.getResources().getString(R.string.ydsdk_ad_download_install);
        } else {
            if (downloadProgress >= 0) {
                message = downloadProgress + " %";
            }
        }

        lastEvent = status;

        if (!TextUtils.isEmpty(message) && !oldMsg.equals(message)) {
            mDownloadTextView.setText(message);
        }

        if (status == DownloadEvent.STATUS_DEFAULT || status == DownloadEvent.STATUS_OPEN || status == DownloadEvent.STATUS_INSTALL
                || status == DownloadEvent.STATUS_FAILED || status == DownloadEvent.STATUS_SUCCESSFUL) {
            mbDownloadInProgress = false;
            mDownloadTextView.setBackgroundResource(R.drawable.ydsdk_ad_selector_download_btn);

            mDownloadTextView.setTextColor(mDownloadTextView.getResources().getColor(R.color.ydsdk_navi_tab_color_h));
        } else {
            mbDownloadInProgress = true;
            mDownloadTextView.setBackgroundResource(R.drawable.ydsdk_ad_btn_corner_disable);
            mDownloadTextView.setTextColor(mDownloadTextView.getResources().getColor(R.color.ydsdk_divider_bg));
        }
    }
}
