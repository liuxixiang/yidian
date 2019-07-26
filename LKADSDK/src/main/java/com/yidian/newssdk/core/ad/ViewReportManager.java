package com.yidian.newssdk.core.ad;

import android.os.Handler;
import android.os.Looper;

import com.yidian.ad.data.AdvertisementCard;

/**
 * 用来辅助发送recyclerview中广告view日志
 * 通过tag设置给广告view
 * Created by lishuo on 2017/11/16.
 */

public class ViewReportManager {
    private boolean hasReportView = false;
    private boolean hasSendDelayReport = false;
    private Handler mHandler;
    private AdvertisementCard mAdCard;
    private Runnable mViewReportRunnable;
    private boolean hasReportTencentView = false;
    private boolean hasTencentViewDelayReport = false;
    private Runnable mTencentReportRunnable;

    public ViewReportManager() {
        mHandler = new Handler(Looper.getMainLooper());
        mViewReportRunnable = new Runnable() {
            @Override
            public void run() {
                doViewReport();
            }
        };
        mTencentReportRunnable = new Runnable() {
            @Override
            public void run() {
                //todo 暂时注释广告联盟
//                doTencentViewReport();
            }
        };
    }

    public void setAdCard(AdvertisementCard card) {
        mAdCard = card;
    }

    public void doViewReport() {
        if (!hasReportView) {
            mHandler.removeCallbacks(mViewReportRunnable);
            AdvertisementUtil.reportViewNSEvent(mAdCard);
            hasReportView = true;
        }
    }

    public void doViewDelayReport(int delay) {
        if (!hasSendDelayReport && !hasReportView && !adsFromTencent()) {
            mHandler.postDelayed(mViewReportRunnable, delay);
            hasSendDelayReport = true;
        }
    }

    public void cancelViewDelayReport() {
        if (hasSendDelayReport) {
            mHandler.removeCallbacks(mViewReportRunnable);
            hasSendDelayReport = false;
            hasReportView = false;
        }
    }

    public void doTencentViewDelayReport(int delay) {
        if (!hasTencentViewDelayReport && !hasReportTencentView && adsFromTencent()) {
            mHandler.postDelayed(mTencentReportRunnable, delay);
            hasTencentViewDelayReport = true;
        }
    }

    public void cancelTencentViewDelayReport() {
        if (hasTencentViewDelayReport) {
            mHandler.removeCallbacks(mTencentReportRunnable);
            hasTencentViewDelayReport = false;
            hasReportTencentView = false;
        }
    }

    private boolean adsFromTencent() {
        return AdvertisementCard.isTencentAd(mAdCard);
    }

    public void doTencentViewReport() {
        if (!hasReportTencentView && adsFromTencent()) {
            mHandler.removeCallbacks(mTencentReportRunnable);
            AdvertisementUtil.reportTencentViewEvent(mAdCard);
            hasReportTencentView = true;
        }
    }
}
