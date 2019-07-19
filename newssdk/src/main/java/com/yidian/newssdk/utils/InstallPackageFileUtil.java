package com.yidian.newssdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.core.ad.AdvertisementUtil;
import com.yidian.newssdk.data.ad.ADConstants;
import com.yidian.newssdk.core.ad.AdvertisementModule;
import com.yidian.newssdk.data.ad.db.AdvertisementDbUtil;
import com.yidian.newssdk.utils.install.AdPMDelegateInstaller;
import com.yidian.newssdk.utils.install.Installer;
import com.yidian.newssdk.utils.install.MiuiAdHelper;
import com.yidian.newssdk.widget.feedback.ad.IAdRelatedStatusListener;

import java.io.File;
import java.util.List;

/**
 * Created by liuyue on 16/3/1.
 */
public class InstallPackageFileUtil {
    public static boolean silentInstall(String fileUri, AdvertisementCard card) {
        return MiuiAdHelper.installPackage(fileUri, card);
    }

    @Nullable
    public static String waitForInstall(AdvertisementCard card) {
        IAdRelatedStatusListener listener = AdvertisementModule.getInstance().getAdRelatedStateListener();
        String packageName = card.getPackageName();
        //List<String> localFiles = AdDbUtil.getDownloadPackageRecord(packageName);
        List<String> localFiles = AdvertisementDbUtil.getDownloadPackageRecord(packageName);
        if (localFiles == null || localFiles.size() < 1) {
            if(listener != null) {
                listener.waitForInstallApp(card, false);
            }
            return null;
        }
        for (String fileUri : localFiles) {
            if (TextUtils.isEmpty(fileUri) || !(new File(fileUri).exists())) {
                Log.d(ADConstants.ADVERTISEMENT_LOG, "delete file uri : " + fileUri);
                //如果是fileUri已经是不能使用的，则从DB中删除这条记录
                //AdDbUtil.deleteDownloadPackageRecord(card.packageName, fileUri);
                AdvertisementDbUtil.deleteDownloadPackageRecord(card.getPackageName(), fileUri);
                continue;
            }
            if(listener != null) {
                listener.waitForInstallApp(card, true);
            }
            return fileUri;
        }
        if(listener != null) {
            listener.waitForInstallApp(card, false);
        }
        return null;
    }

    public static boolean nonSilentInstall(String fileUri, Context ctx, boolean needProxy) {
        IAdRelatedStatusListener listener = AdvertisementModule.getInstance().getAdRelatedStateListener();
        //检查包名不通过,直接退出安装并且删除文件, 如果没有adcard标明是直接安装
        if(!canDownloadedNewVersionVerifiedByName(ctx,fileUri)){
            try {
                new File(fileUri).delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(listener != null) {
                listener.nonSilentInstallApp(fileUri, false);
            }
            return false;
        }
        Log.d(ADConstants.ADVERTISEMENT_LOG, "NON SLIENT INSTALL : " + fileUri);

        Installer.install(ctx, fileUri, needProxy);
        if(listener != null) {
            listener.nonSilentInstallApp(fileUri, true);
        }
        return true;
    }

    public static void trySilentInstall(@NonNull final Context ctx, final @NonNull String fileUri, final @NonNull AdvertisementCard card) {
        final IAdRelatedStatusListener listener = AdvertisementModule.getInstance().getAdRelatedStateListener();
        try {
            Log.d(ADConstants.ADVERTISEMENT_LOG, "SInstall: Start install file through Uri:" + fileUri);
            boolean supportedSlientInstall = false;
            if (AdvertisementUtil.supportDownload(card)) {
                //只对应用下载使用
                supportedSlientInstall = InstallPackageFileUtil.silentInstall(fileUri, card);
            }

            if (!supportedSlientInstall) {
                Log.d(ADConstants.ADVERTISEMENT_LOG, "SInstall: Not supported.");
                AdPMDelegateInstaller pmDelegateInstaller = new AdPMDelegateInstaller(AdvertisementModule.getInstance().getApplicationContext(),
                        new AdPMDelegateInstaller.DelegateInstallerListener() {
                            @Override
                            public void grantPermission(boolean permissionGranted) {
                                //没有权限，使用另一种安装
                                if (!permissionGranted) {
                                    Log.d("SInstall", "Slient install not supported.");
                                    String message = MiuiAdHelper.getLogMessage();
//                                    ReportClickEvent.reportClickWithParam(HipuApplication.getInstanceApplication().getApplicationContext(),
//                                            "hipuReceiver", "platforminfo", message);
                                    if(listener != null) {
                                        listener.silentInstallApp(fileUri, false);
                                    }
                                    InstallPackageFileUtil.nonSilentInstall(fileUri, ctx, true);
                                }
                            }
                        });
                pmDelegateInstaller.installPackage(new File(fileUri), card);
            }

            if(listener != null) {
                listener.silentInstallApp(fileUri, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(listener != null) {
                listener.silentInstallApp(fileUri, false);
            }
        }
    }

    public static boolean isValidApk(Context ctx,String apkPath) {
        PackageInfo pkInfo = getPackageInfo(ctx, apkPath);
        return pkInfo != null;
    }

    private static PackageInfo getPackageInfo(Context ctx,String apkPath) {
        try {
            PackageManager pm = ctx.getPackageManager();
            return pm.getPackageArchiveInfo(apkPath, PackageManager.GET_CONFIGURATIONS);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * 通过包名判断下载的安装包是否被劫持
     *
     * @param context
     * @param filePath
     * @return
     */
    public static boolean canDownloadedNewVersionVerifiedByName(Context context, String filePath) {
        IAdRelatedStatusListener listener = AdvertisementModule.getInstance().getAdRelatedStateListener();
        if (TextUtils.isEmpty(filePath)) {
            if(listener != null) {
                listener.checkDownloadAppByPackageName(filePath, false);
            }
            return false;
        }


        PackageInfo packageInfo = getPackageInfo(context, filePath);
        if (packageInfo == null){
            if(listener != null) {
                listener.checkDownloadAppByPackageName(filePath, false);
            }
            //解决线上出现的包解析错误的问题，既然解析失败就删掉吧
            return false;
        }
//        String currentPackageName = context.getPackageName();
//        PackageManager packageManager = context.getPackageManager();
//        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filePath, 0);
//        if (packageInfo == null) {
//            if(listener != null) {
//                listener.checkDownloadAppByPackageName(filePath, false);
//            }
//            return true;//此处为何返回true？ 明明没有拿到package info呀
//        }

        String downloadedPackageName = packageInfo.packageName;
        if (!TextUtils.isEmpty(downloadedPackageName) && isPackageNameInBlackList(downloadedPackageName)) {
            if(listener != null) {
                listener.checkDownloadAppByPackageName(filePath, false);
            }
            return false;
        }

        if(listener != null) {
            listener.checkDownloadAppByPackageName(filePath, true);
        }
        return true;

    }

    /**
     * 阻拦竞品公司的替换
     * @param packageName
     * @return
     */
    private static boolean isPackageNameInBlackList(String packageName){
        if (TextUtils.isEmpty(packageName)){
            return false;
        }
        if (packageName.contains("com.ss.android")//今日头条
                || packageName.contains("com.netease.newsreader")//网易新闻
                || packageName.contains("com.UCMobile") //UC
                || packageName.contains("com.tencent.reading")// 快报
                || packageName.contains("com.tencent.news")//腾讯新闻
                ){
            return true;
        }
        return false;
    }


//    /**
//     * 通过签名判断下载的版本是否正确
//     * 由于存在V2的签名方式而过期
//     *
//     * @param context
//     * @param filePath
//     * @return
//     */
//    @Deprecated
//    public static boolean canDownloadedNewVersionVerified(Context context, String filePath){
//        Signature[] downLoadedFileSignatures = PackageVerifyer.collectCertificates(filePath, false);
//        //没有获取到签名
//        if (downLoadedFileSignatures == null){
//            try {
//                new File(filePath).delete();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return false;
//        }
//        Signature [] yidianSignatures;
//        try {
//            yidianSignatures = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return PackageVerifyer.isSignaturesSame(downLoadedFileSignatures, yidianSignatures);
//    }
}
