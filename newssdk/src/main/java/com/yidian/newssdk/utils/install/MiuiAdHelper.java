package com.yidian.newssdk.utils.install;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.core.ad.AdMonitorHelper;
import com.yidian.newssdk.core.ad.AdvertisementUtil;
import com.yidian.newssdk.data.ad.db.AdvertisementDbUtil;
import com.yidian.newssdk.data.ad.event.DownloadEvent;
import com.yidian.newssdk.utils.DeviceHelper;
import com.yidian.newssdk.utils.broadcastbus.BroadcastBus;


import java.io.File;
import java.lang.reflect.Method;


/**
 * Created by patrickleong on 12/19/14.
 */
public class MiuiAdHelper {
    private static String TAG = "SInstall";

    public static boolean installPackage(String fileUrl, AdvertisementCard card) {
        File newFile = new File(fileUrl);
        return installPackage(newFile, card);
    }

    public static boolean installPackage(File apkFile, AdvertisementCard card) {
        boolean permissionGranted = false;
        if (apkFile == null) {
            return false;
        }
        try {
            Method installPackageMethod = getSlientInstallMethod();
            if (installPackageMethod != null) {
                Log.i(TAG, "installPackageMethod existed ");
                int flag = 1;
                try {
                    //Get static flag
                    flag = Class.forName("android.content.pm.PackageManager")
                            .getDeclaredField("INSTALL_REPLACE_EXISTING").getInt(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (apkFile.exists()) {
                    Log.i(TAG, "File:" + apkFile + " is existed.");
                    Uri fileUri = Uri.fromFile(apkFile);
                    permissionGranted = (Boolean) installPackageMethod.invoke(null, fileUri,
                            new InstallPackageListener(card), flag);    //Permission granted.
                    Log.i(TAG, "Start Installing package, permissionGranted: " + permissionGranted + " , install apk:" + apkFile);
                    BroadcastBus.getDefault().post(new DownloadEvent(card.getAid(), DownloadEvent.STATUS_INSTALLING, 100));
                } else {
                    Log.i(TAG, "File:" + apkFile + " is not existed.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Method no existed.");
        }
        return permissionGranted;
    }

    private static Method getSlientInstallMethod() {
        Method installPackageMethod = null;

        try {
            Class observerClazz = Class.forName("android.content.pm.IPackageInstallObserver");
            installPackageMethod = Class.forName("com.miui.whetstone.WhetstoneManager")
                    .getMethod("installPackage", Uri.class, observerClazz, int.class);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        }
        return installPackageMethod;
    }

    private static String getSystemMessage(String key) {
        try {
            Method systemGetMethod = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if (systemGetMethod != null) {
                return (String) systemGetMethod.invoke(null, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLogMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("device:");
        builder.append(getSystemMessage("ro.product.device"));
        builder.append(", incremental:");
        builder.append(getSystemMessage("ro.build.version.incremental"));
        builder.append(", model:");
        builder.append(getSystemMessage("ro.product.model"));

        return builder.toString();
    }

    public static class InstallPackageListener extends IPackageInstallObserver.Stub {
        private AdvertisementCard mAdCard;

        public InstallPackageListener(AdvertisementCard adCard) {
            mAdCard = adCard;
        }

        @Override
        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
            Log.i(TAG, packageName + " installed.");
            if (mAdCard != null) {
                AdvertisementUtil.reportEvent(mAdCard, AdvertisementUtil.EVENT_APP_INSTALL_SUCCESS, true);
                AdMonitorHelper.reportThirdPartyEvents(mAdCard.finishInstallMonitorUrls, String.valueOf(mAdCard.getAid()), false);

                //AdDbUtil.removeAdRecord(mAdCard.downloadId);
                AdvertisementDbUtil.removeAdRecord(mAdCard.getDownloadId());
                BroadcastBus.getDefault().post(new DownloadEvent(mAdCard.getAid(), DownloadEvent.STATUS_OPEN, 100));
            }
        }
    }

    public static boolean launchMIAppStore(@NonNull Context context, @NonNull AdvertisementCard card) {
        return launchMIAppStoreWithInstall(context, card, false);
    }

    /**
     * Return true if launch success.
     *
     * @return
     */
    public static boolean launchMIAppStoreWithInstall(@NonNull Context context, @NonNull AdvertisementCard card, final boolean autoDownload) {
        boolean launchStoreSuccess = false;
        if (DeviceHelper.getInstance().notMIUI5()) {
            if (card.getStartAppStore() == AdvertisementCard.APP_OPEN_APP_STORE ||
                    card.getStartAppStore() == AdvertisementCard.APP_MINI_CARD_TITLE_DOWNLOAD) {
                String refStr = "?ref=";
                int idx = card.url.lastIndexOf(refStr);
                if (idx != -1) {
                    String ref = card.url.substring(idx + refStr.length(), card.url.length());
                    if (!TextUtils.isEmpty(card.getPackageName())) {
                        try {
//                                        String marketUrl = "mimarket://details?id=" + card.packageName + "&back=true&" + ref;
//                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl));
//                                        startActivity(intent);
//
                            String marketUrl = "mimarket://details?id=" + card.getPackageName();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl));
                            intent.setPackage("com.xiaomi.market");
                            intent.putExtra("ref", ref);
                            intent.putExtra("back", true);
                            if (autoDownload) {
                                intent.putExtra("startDownload", true);
                            }
                            context.startActivity(intent);
                            launchStoreSuccess = true;
                            card.startAppStoreStatus = AdvertisementCard.APP_OPEN_APP_STORE;
//                            card.setStartAppStore(AdvertisementCard.APP_OPEN_APP_STORE);
                        } catch (Exception e) {
//                            ReportProxy.logEvent(context, "startMiuiStoreFailed");
                        }
                    }
                }
            }
        }
        return launchStoreSuccess;
    }


}