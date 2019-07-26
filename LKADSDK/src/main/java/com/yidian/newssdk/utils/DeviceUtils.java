package com.yidian.newssdk.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by chenyichang on 2018/5/19.
 */

public class DeviceUtils {

    private static final String DEFAULT_MAC = "10:10:10:10:10:10";
    private static String model;


    public static String getMac(Context context) {
        String mac = getMacFromDevice(context);
        if (TextUtils.isEmpty(mac)) {
            mac = DEFAULT_MAC;
        }
        return mac;
    }

    @SuppressLint("WifiManagerPotentialLeak")
    public static String getIpAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        }
        return "0.0.0.0";
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static String getAppVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(pi.versionCode);
            if (TextUtils.isEmpty(versionCode)) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    public static String getModel() {
        if (!TextUtils.isEmpty(model)) {
            return model;
        }

        try {
            String base64Model = Base64.encodeToString(Build.MODEL.getBytes(), Base64.NO_WRAP);
            model = String.valueOf(base64Model);
        } catch (Exception e) {
            //
        }

        return model;
    }

    public static String getOsVer(){
        return String.valueOf(Build.VERSION.SDK);
    }


    private static String getMacFromDevice(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            String mac = getWifiMacAddressForAndroid23();
            if (TextUtils.isEmpty(mac)) {
                mac = DEFAULT_MAC;
            }
            return mac;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String mac = tryGetMAC(wifiManager);

        if (!TextUtils.isEmpty(mac)) {
            return mac;
        }

        boolean isOkWifi = tryOpenMAC(wifiManager);
        for (int index = 0; index < 3; index++) {
            if (index != 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mac = tryGetMAC(wifiManager);
            if (!TextUtils.isEmpty(mac)) {
                break;
            }
        }

        if (isOkWifi) {
            tryCloseMAC(wifiManager);
        }
        return mac;
    }


    private static boolean tryOpenMAC(WifiManager manager) {
        boolean softOpenWifi = false;
//        int state = manager.getWifiState();
//        if (state != WifiManager.WIFI_STATE_ENABLED && state != WifiManager.WIFI_STATE_ENABLING) {
//            manager.setWifiEnabled(true);
//            softOpenWifi = true;
//        }
        return softOpenWifi;
    }

    private static void tryCloseMAC(WifiManager manager) {
//        manager.setWifiEnabled(false);
    }

    private static String tryGetMAC(WifiManager manager) {
        try {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo == null || TextUtils.isEmpty(wifiInfo.getMacAddress())) {
                return null;
            }
            String mac = wifiInfo.getMacAddress();
            return mac;
        } catch (NullPointerException e) {
            //联想手机在4.*.*上可能出现NPE，这里只能加一个保护
            return null;
        }
    }

    /**
     * Android 6.0上增强了数据保护，取到的MAC地址是02:00:00:00:00:00
     * http://stackoverflow.com/questions/31329733/how-to-get-the-missing-wifi-mac-address-on-android-m-preview
     */
    private static String getWifiMacAddressForAndroid23() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

}
