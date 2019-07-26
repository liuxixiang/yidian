package com.linken.newssdk.utils.install;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.IPackageInstallObserver;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.miui.server.IPackageManagerDelegate;
import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.core.ad.AdMonitorHelper;
import com.linken.newssdk.core.ad.AdvertisementUtil;
import com.linken.newssdk.data.ad.ADConstants;
import com.linken.newssdk.data.ad.db.AdvertisementDbUtil;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.linken.newssdk.core.ad.AdvertisementUtil.EVENT_APP_INSTALL_SUCCESS;

/**
 * 修复MIUI 7.1上没有静默安装权限问题
 * Created by patrickleong on 3/5/16.
 */
public class AdPMDelegateInstaller {
    private ServiceConnection mAdPMDelegateServiceConnection = null;
    private WeakReference<Context> mContextReference;

    private DelegateInstallerListener mListener;

    public AdPMDelegateInstaller(@NonNull Context context, @NonNull DelegateInstallerListener listener) {
        mContextReference = new WeakReference<>(context);
        mListener = listener;
    }

    public interface DelegateInstallerListener {
        void grantPermission(boolean permissionGranted);
    }

    public void installPackage(final File apkFile, final AdvertisementCard card) {
        Intent intent = new Intent("miui.intent.action.ad.PACKAGE_MANAGER");
        intent.setPackage("com.miui.systemAdSolution");
        mAdPMDelegateServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(ADConstants.AD_LOG, "DInstaller: service connected");
                IPackageManagerDelegate pm = IPackageManagerDelegate.Stub.asInterface(service);
                try {
                    IPackageInstallObserver observer = new IPackageInstallObserver.Stub() {
                        @Override
                        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
                            Log.i(ADConstants.AD_LOG, "DInstaller: package installed : " + packageName + " " + returnCode);

                            // 安装完成 unbindService
                            unbindService();

                            if (card != null) {
                                AdvertisementUtil.reportEvent(card, EVENT_APP_INSTALL_SUCCESS, true);
                                AdMonitorHelper.reportThirdPartyEvents(card.finishInstallMonitorUrls, String.valueOf(card.getAid()), false);

                                //AdDbUtil.removeAdRecord(card.downloadId);
                                AdvertisementDbUtil.removeAdRecord(card.getDownloadId());
                            }
                        }

                        @Override
                        public IBinder asBinder() {
                            return this;
                        }
                    };

                    boolean ret = pm.installPackage(
                            Uri.fromFile(apkFile),
                            observer,
                            0);
                    Log.i(ADConstants.AD_LOG, "DInstaller：try delegate install package ret : " + ret);
                    if (!ret) {
                        unbindService();
//                        tryAnotherWay();
                    }
                    if (mListener != null) {
                        mListener.grantPermission(ret);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(ADConstants.AD_LOG, "DInstaller: service disconnected");
            }
        };

        Context context = mContextReference.get();
        if (context != null) {
            if (!context.bindService(intent, mAdPMDelegateServiceConnection, Activity.BIND_AUTO_CREATE)) {
                mAdPMDelegateServiceConnection = null;

//                tryAnotherWay();
                if (mListener != null) {
                    mListener.grantPermission(false);
                }
            }
        } else {
            Log.e(ADConstants.AD_LOG, "DInstaller: Context is release.");
        }
    }

    public void unbindService() {
        if (mAdPMDelegateServiceConnection != null) {
            Log.i(ADConstants.AD_LOG, "DInstaller: unbind service");
            Context context = mContextReference.get();
            if (context != null) {
                context.unbindService(mAdPMDelegateServiceConnection);
            } else {
                Log.e(ADConstants.AD_LOG, "DInstaller: Context is release.");
            }
            mAdPMDelegateServiceConnection = null;
        }
    }
}
