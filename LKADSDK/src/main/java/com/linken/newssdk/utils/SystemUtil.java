package com.linken.newssdk.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.linken.newssdk.data.pref.GlobalConfig;


/**
 * Created by caichen on 2017/9/15.
 */

public class SystemUtil {

    private static String sDeviceId;
    private static String sAndroidId;
    private static String sImei;
    private static String sRealImei;

    private static String mMD5Imei = null;
    private static String mMD5RealImei = null;
    private static String mMd5AndroidId;

    public static String generateFakeImei() {
        if (TextUtils.isEmpty(sDeviceId)) {
            String deviceId = GlobalConfig.getDeviceId();
            if (TextUtils.isEmpty(deviceId)) {
                String imei = getRealIMEI();
                String androidId = getAndroidId();
                String serialNo = Build.SERIAL;
                sDeviceId = buildM2(imei, androidId, serialNo);
                GlobalConfig.saveDeviceId(sDeviceId);
            } else {
                sDeviceId = deviceId;
            }
        }
        return sDeviceId;
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI() {
        try {
            if (TextUtils.isEmpty(sImei)) {
                if (PermissionUtil.hasPermissionGroup(ContextUtils.getApplicationContext(), Manifest.permission.READ_PHONE_STATE)) {
                    TelephonyManager telephonyManager = (TelephonyManager) ContextUtils.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    sImei = telephonyManager.getDeviceId();
                }
                if (TextUtils.isEmpty(sImei)) {
                    sImei = generateFakeImei();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (TextUtils.isEmpty(sImei)) {
                sImei = generateFakeImei();
            }
        }
        return sImei;
    }

    @SuppressLint("MissingPermission")
    public static String getRealIMEI() {
        try {
            if (TextUtils.isEmpty(sRealImei)) {
                if (PermissionUtil.hasPermissionGroup(ContextUtils.getApplicationContext(), Manifest.permission.READ_PHONE_STATE)) {
                    TelephonyManager telephonyManager = (TelephonyManager) ContextUtils.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    sRealImei = telephonyManager.getDeviceId();
                }
                if (TextUtils.isEmpty(sRealImei)) {
                    sRealImei = "0000000000000000";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sRealImei;
    }

    public static String getMd5Imei() {
        if (mMD5Imei == null) {
            mMD5Imei = EncryptUtil.getMD5_32(getIMEI());
        }
        return mMD5Imei;
    }

    public static String getMd5RealImei() {
        if (mMD5RealImei == null) {
            mMD5RealImei = EncryptUtil.getMD5_32(getRealIMEI());
        }
        return mMD5RealImei;
    }

    public static String getAndroidId() {
        try {
            if (TextUtils.isEmpty(sAndroidId)) {
                sAndroidId = android.provider.Settings.Secure.getString(ContextUtils.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sAndroidId;
    }


    public static String getMd5AndroidId() {
        if (mMd5AndroidId == null) {
            mMd5AndroidId = EncryptUtil.getMD5_32(getAndroidId());
        }

        return mMd5AndroidId;
    }

    private static String buildM2(String imei, String androidId, String serialNo) {
        return EncryptUtil.getMD5_32(imei + androidId + serialNo);
    }
}
