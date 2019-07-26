package com.yidian.newssdk.utils.action;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.core.ad.AdMonitorHelper;
import com.yidian.newssdk.core.ad.AdvertisementUtil;
import com.yidian.newssdk.core.detail.ad.LandingPageActivity;
import com.yidian.newssdk.core.detail.article.news.YdNewsActivity;
import com.yidian.newssdk.data.ad.ADConstants;
import com.yidian.newssdk.core.ad.AdvertisementModule;
import com.yidian.newssdk.data.ad.tencent.TencentClickParamData;
import com.yidian.newssdk.utils.ApkDownloadHelper;
import com.yidian.newssdk.utils.SchemeUtil;


import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 把广告相关的下载，打开页面的逻辑都在这里
 * Created by patrickleong on 3/31/16.
 */
public abstract class AdActionHelper {
    protected AdvertisementCard adCard;
    private long lastClickTime;

    AdActionHelper(@NonNull AdvertisementCard card) {
        adCard = card;
    }

    public static AdActionHelper newInstance(@NonNull AdvertisementCard card){
        AdActionHelper helper;
//        if (AdvertisementModule.getInstance().isZixunBuild() && DeviceHelper.getInstance().isMIUI()) {
//            helper = new XiaomiAdActionHelper(card);
//        } else {
//            helper = new GenericAdActionHelper(card);
//        }
        helper = new GenericAdActionHelper(card);

        return helper;
    }

    public abstract void downloadApk(@NonNull final Context context);

    /**
     * 用deeplink或者packageName打开app，注：1)deeplink打开的app和packageName打开的app可能不是同一个app
     * 例如：deeplink打开的可能是小米应用商店（打开可能在商店内直接下载），packageName打开的是王者荣耀
     * 2)优先用deeplink打开
     * @param context
     * @return
     */
    public void directOpenApk(Context context) {
        if (!openWithDeepLink(context)) {
            doStartApplicationWithPackageName(context, adCard.getPackageName());
        }
    }

    public boolean openWithDeepLink(Context context) {
        String deepLinkUrl = adCard.getDeeplinkUrl();
        if (!TextUtils.isEmpty(deepLinkUrl)) {
            AdvertisementUtil.reportAppLaunchStartEvent(adCard, 0);
            if (AdvertisementUtil.isAppInstalledAccordingToDeepLinkUrl(deepLinkUrl, context)) {
                if (SchemeUtil.openAppWithUrl(context, deepLinkUrl)) {
                    AdvertisementUtil.reportAppLaunchSuccessEvent(adCard, 0);
                    return true;
                } else {
                    AdvertisementUtil.reportAppLaunchFailEvent(adCard, 0);
                    return false;
                }
            } else {
                AdvertisementUtil.reportAppLaunchFailEvent(adCard, 0);
                return false;
            }
        } else {
            return false;
        }
    }

    protected boolean doStartApplicationWithPackageName(Context context, String packagename) {
        AdvertisementUtil.reportAppLaunchStartEvent(adCard, 1);
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            AdvertisementUtil.reportAppLaunchFailEvent(adCard, 1);
            return false;
        }
        if (packageinfo == null) {
            AdvertisementUtil.reportAppLaunchFailEvent(adCard, 1);
            return false;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        Iterator<ResolveInfo> iterator = resolveinfoList.iterator();
        ResolveInfo resolveinfo = null;
        if (iterator.hasNext()) {
            resolveinfo = iterator.next();
        }
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
            AdvertisementUtil.reportAppLaunchSuccessEvent(adCard, 1);
            return true;
        } else {
            AdvertisementUtil.reportAppLaunchFailEvent(adCard, 1);
            return false;
        }
    }

    public void setAdCard(AdvertisementCard card) {
        this.adCard = card;
    }

    public boolean openLandingPage(@NonNull final Context context) {
        return openLandingPage(context, false);
    }

    public boolean openLandingPage(@NonNull final Context context, boolean fromH5Article) {
        if (adCard == null || adCard.getType() == AdvertisementCard.TYPE_NO_ACTION) {
            Log.e(ADConstants.ADVERTISEMENT_LOG, "AdvertiseCard or Context is null.");
            return false;
        }

        if (!fromH5Article) {
            AdvertisementUtil.reportClickEvent(adCard, true, UUID.randomUUID().toString());
        }

        //下载类广告不支持其他类型打开
        if (AdvertisementUtil.supportDownload(adCard)) {
            _openLink(context, adCard);
        } else {
            adCard.accessDeepLink = false;
            Intent i = null;

            if (adCard.getType() == AdvertisementCard.TYPE_NEWS) {
                YdNewsActivity.startNewsActivity(context, adCard);
            } else {
                _openLink(context, adCard);
            }
        }
        return true;
    }

    public boolean openTencentLandingPage(@NonNull final Context context, TencentClickParamData clickParamData) {
        if (adCard == null || adCard.getType() == AdvertisementCard.TYPE_NO_ACTION) {
            Log.e(ADConstants.ADVERTISEMENT_LOG, "AdvertiseCard or Context is null.");
            return false;
        }

        AdvertisementUtil.reportTencentClickEvent(adCard, true, UUID.randomUUID().toString(), clickParamData);

        //下载类广告不支持其他类型打开
        if (AdvertisementUtil.supportDownload(adCard)) {
            _openLink(context, adCard);
        } else {
            adCard.accessDeepLink = false;
            Intent i = null;
           if (adCard.getType() == AdvertisementCard.TYPE_NEWS) {
                YdNewsActivity.startNewsActivity(context, adCard);
            } else {
                _openLink(context, adCard);
            }
        }
        return true;
    }

    private void _openLink(final Context context, AdvertisementCard card){
        String url = AdvertisementCard.isTencentAd(card) ? card.getClickUrl() : AdMonitorHelper.getUrlWithMacro(card.getClickUrl(), String.valueOf(card.getAid()), true);

        if (TextUtils.isEmpty(url)) {
            return;
        }
        openLink(context, url);
        //重置clickUrl，否则点击url参数被替换之后，下次点击url无法替换点击参数
        if (AdvertisementCard.isTencentAd(card)) {
            card.setClickUrl(card.tencentClickUrl);
        }
    }

    abstract void openLink(final Context context, final String url);

    /**
     * 调起手机拨号页面
     * @param context
     */
    public void openDial(Context context) {
        if (context == null || TextUtils.isEmpty(adCard.phoneNumber)) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +
                adCard.phoneNumber));
        if (intent.resolveActivity(AdvertisementModule.getInstance().getApplication().getPackageManager()) != null) {
            context.startActivity(intent);
        }

        String clickId = UUID.randomUUID().toString();
        AdvertisementUtil.reportClickEvent(adCard, true, clickId);
        AdvertisementUtil.reportReserveEvent(adCard, null, null, clickId);
    }

    public void eventReserve(String name, String phone, Context context) {
        if (context == null) {
            return;
        }

        long cid = System.currentTimeMillis();
        String url = !TextUtils.isEmpty(adCard.huodongFormUrl) ? adCard.huodongFormUrl : adCard.getClickUrl();
        if (!TextUtils.isEmpty(url) && !"null".equalsIgnoreCase(url)) {
            String clickId = UUID.randomUUID().toString();
            try {
                name = URLEncoder.encode(name);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            if (url.contains("?")) {
                url += ("&clientName=" + name + "&clientPhone=" + phone + "&clickId=" + clickId);
            } else {
                url += ("?clientName=" + name + "&clientPhone=" + phone + "&clickId=" + clickId);
            }

            AdvertisementUtil.reportReserveEvent(adCard, name, phone, clickId);
            AdvertisementUtil.reportSimpleClickMonitors(adCard);
            AdvertisementUtil.reportLandingPageEvent(adCard, cid, url);
            LandingPageActivity.startActivity((Activity) context, adCard,
                    AdMonitorHelper.getUrlWithMacro(url, String.valueOf(adCard.getAid()), true), cid);

        }
    }

    /**
     * 支持deepLinkUrl和landingPageUrl用scheme打开第三方app
     * @param context
     * @param url
     */
    void processAdAction(final Context context, String url) {
        if (!openWithDeepLink(context)) {
            if (!TextUtils.equals("http", Uri.parse(url).getScheme()) && !TextUtils.equals("https", Uri.parse(url).getScheme())) {
                if (AdvertisementUtil.isAppInstalledAccordingToDeepLinkUrl(url, context)) {
                    SchemeUtil.openAppWithUrl(context, url);
                }
            } else {
                long cid = System.currentTimeMillis();
                launchCardInWebView(context, adCard, cid);
                AdvertisementUtil.reportLandingPageEvent(adCard, cid, url);
            }
        }
    }

    //根据类型决定是在本进程还是第三方进程打开
    private void launchCardInWebView(Context context, AdvertisementCard adCard, long cid){
        String path;
        if (adCard.getPlaySound() == 1) {
//            //各种奇葩预约卡片，各种破音乐、视频、试驾等、不好说，
            path = "/m/exwebview";
        } else {
            path = "/m/adwebview";
        }

        LandingPageActivity.startActivity((Activity) context, adCard,
                AdMonitorHelper.getUrlWithMacro(adCard.getClickUrl(), String.valueOf(adCard.getAid()), true), cid);
//
    }

    boolean checkButtonDuration(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < ApkDownloadHelper.DISABLE_DOWNLOAD_DURATION) {
            return false;
        }
        lastClickTime = currentTime;
        return true;
    }

    public abstract void destroy();
}
