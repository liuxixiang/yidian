package com.yidian.newssdk.utils.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

import static android.net.ConnectivityManager.TYPE_WIFI;


public class NetworkHelper {

    private List<OnWifiCallBackListener> callbackList = new ArrayList<>();
    private volatile int mNetworkType = ConnectivityManager.TYPE_MOBILE;
    private static NetworkHelper sNetworkHelper;


    public void init(Context context) {
        context.registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mNetworkType = getActiveNetworkType(context);
    }

    public static NetworkHelper getInstance() {
        if (sNetworkHelper == null) {
            synchronized (NetworkHelper.class) {
                if (sNetworkHelper == null) {
                    sNetworkHelper = new NetworkHelper();
                }
            }
        }
        return sNetworkHelper;
    }


    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mNetworkType = getActiveNetworkType(context);
            boolean isWifi = mNetworkType == TYPE_WIFI ? true : false;
            for (OnWifiCallBackListener listener : callbackList) {
                if (listener != null) {
                    listener.onWifiChange(isWifi);
                }
            }
        }

    };

    public void destroy(Context context){
        try {
            if (context != null) {
                context.unregisterReceiver(mNetworkReceiver);
            }
        }catch(Exception e){

        }
    }

    /**
     * this function return the active network type. If no active network,
     * return -1;
     *
     * @return network type: refer ConnectivityManager -1: no active network
     */
     private int getActiveNetworkType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.getType();
        } else {
            return -1;
        }
    }

    public void registerWifiCallbackListener(OnWifiCallBackListener onWifiCallBackListener) {
         if (onWifiCallBackListener != null) {
             if (!callbackList.contains(onWifiCallBackListener)) {
                 callbackList.add(onWifiCallBackListener);
             }
         }
    }

    public void unRegisterWifiCallbackListener(OnWifiCallBackListener onWifiCallBackListener) {
         if (onWifiCallBackListener != null && callbackList.contains(onWifiCallBackListener)) {
             callbackList.remove(onWifiCallBackListener);
         }
    }

    public int getNetworkType() {
        return mNetworkType;
    }

    public interface OnWifiCallBackListener {
         void onWifiChange(boolean isWifi);
    }
}
