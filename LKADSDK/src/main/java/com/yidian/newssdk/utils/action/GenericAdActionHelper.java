package com.yidian.newssdk.utils.action;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.core.ad.AdMonitorHelper;
import com.yidian.newssdk.core.ad.AdvertisementUtil;
import com.yidian.newssdk.data.ad.ADConstants;


/**
 * Created by patrickleong on 24/10/2016.
 */
public class GenericAdActionHelper extends AdActionHelper{

    public GenericAdActionHelper(@NonNull AdvertisementCard card) {
        super(card);
    }

    @Override
    public void downloadApk(@NonNull Context context) {
        if (adCard == null) {
            Log.e(ADConstants.ADVERTISEMENT_LOG, "AdvertiseCard or Context is null.");
            return;
        }

        if (!checkButtonDuration()) {
            return;
        }

        String filename = adCard.title + ".apk";
        AdvertisementUtil.reportClickEvent(adCard, true, null);
        if (!TextUtils.isEmpty(adCard.actionUrl)) {
//            if (HipuConstants.ZIXUN_VERSION) {
//                boolean launchMiStore = false;
//                if (AdExperimentConfig.getInstance().mAppstoreJumpToStore == 1) {
//                    launchMiStore = MiuiAdHelper.launchMIAppStoreWithInstall(context, adCard, true);
//                }
//                if (!launchMiStore) {
//                    AdvertisementUtil.retreiveNameAnddownloadFile(context, adCard);
//                }
//            } else {
                AdvertisementUtil.downloadFile(context, adCard, adCard.title, filename, ADConstants.APP_DOWNLOAD_SOURCE_CARD);
//            }
        }
    }
    /**
     * 客户端逻辑按如下方式处理，
     * 1. 如果 广告的startAppStore=3，
     * a. 尝试调起miniCard，如果调起成功，则使用miniCard服务.
     * i. 上报log时，上报一个startAppStore=3的字段，标识这个事件是来自miniCard。
     * b. 否则， 使用现在的弹窗下载逻辑。
     * i. 上报log时，上报一个startAppStore=2的字段，标识这个事件是来自系统默认弹窗。
     * 2. 如果广告的startAppStore=2,
     * a. 使用现在的系统默认弹窗。
     * i. 上报log时，上报一个startAppStore=2的字段，标识这个事件是来自系统默认弹窗。
     * 3. 如果广告的startAppStore=1,
     * a. 尝试调起应用商店详情页。
     * 上报log时，上报一个startAppStore=1的字段，标识这个事件是来自应用商店详情页。
     * b. 如果不成功，则打开H5的详情页。
     * 上报log时，上报一个startAppStore=0的字段，标识这个事件是来自H5版本的详情页。
     * 4. 如果startAppStore=0，
     * a. 打开H5版本的详情页。
     * i. 上报log时，上报一个startAppStore=0的字段，标识这个事件是来自H5版本的详情页。
     */
    @Override
    void openLink(final Context context, final String url) {
//        adCard.startAppStoreStatus = AdvertisementCard.APP_OPEN_H5;
        //Hack, if this is ends with apk, then start download.
        if (url.endsWith(".apk")) {
            if (openWithDeepLink(context)) {
                return;
            }
            String filename = AdvertisementUtil.getUrlFileName(url);
            AdvertisementUtil.downloadFile(context, adCard, filename, filename + ".apk", ADConstants.APP_DOWNLOAD_SOURCE_CARD);

            //发送下载APK的click url的monitor
            AdMonitorHelper.reportThirdPartyEvents(new String[]{url}, String.valueOf(adCard.getAid()), false);
        } else {
            processAdAction(context, url);
        }
    }

    @Override
    public void destroy() {
        //empty
    }
}
