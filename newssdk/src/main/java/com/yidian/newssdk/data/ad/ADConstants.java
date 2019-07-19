package com.yidian.newssdk.data.ad;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author zhangzhun
 * @date 2018/7/30
 */

public interface ADConstants {

    long DOWNLOAD_BTN_DISABLE_DURATION = 60000;  //防止用户同一时间内多次点击下载
    int VIEW_1S = 1000;
    String ADVERTISEMENT_LOG = "AdvertisementLog";
    String AD_LOG = "YdLogAdvertisement";
    String AD_ACTION = "/Ext/ads/upload-lingxi-log";
    int APP_DOWNLOAD_SOURCE_CARD = 1;
    int APP_DOWNLOAD_SOURCE_H5 = 2;
    int APP_DOWNLOAD_SOURCE_UNKNOWN = -1;
    @IntDef({APP_DOWNLOAD_SOURCE_CARD, APP_DOWNLOAD_SOURCE_H5, APP_DOWNLOAD_SOURCE_UNKNOWN})
    @Retention(RetentionPolicy.SOURCE)
    @interface AppDownloadSource {}

}
