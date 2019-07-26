package com.linken.newssdk.utils;

import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by JayRay on 06/11/2017.
 * Info: 一个有关设备信息的工具
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class DeviceHelper {
    private static final String TAG = DeviceHelper.class.getSimpleName();

    // 小米的手机参数
    public final String MIUI_MANUFACTURER = "Xiaomi";
    final String MIUI_PROP_VERSION_NAME = "ro.miui.ui.version.name";
    final String MIUI_PROP_VERSION_CODE = "ro.miui.ui.version.code";

    // 华为的手机参数
    public final String EMUI_MANUFACTURER = "HUAWEI";
    final String EMUI_PROP_VERSION = "ro.build.version.emui";

    // OPPO的手机参数
    public final String COLOR_OS_MANUFACTURER = "OPPO";
    final String COLOR_OS_PROP_VERSION = "ro.build.version.opporom";
    final String COLOR_OS_NAME = "ColorOS";

    public final String USER_AGENT = "http.agent";

    private static volatile DeviceHelper INSTANCE = null;
    private String versionName = "";
    private String versionCode = "";
    private String manufacturer = "";
    private String brand = "";

    public static DeviceHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (DeviceHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DeviceHelper();
                }
            }
        }
        return INSTANCE;
    }

    private DeviceHelper() {
        manufacturer = Build.MANUFACTURER;
        brand = Build.BRAND;
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            switch (manufacturer) {
                case MIUI_MANUFACTURER:
                    versionName = prop.getProperty(MIUI_PROP_VERSION_NAME, "");
                    versionCode = prop.getProperty(MIUI_PROP_VERSION_CODE, "");
                    break;
                case EMUI_MANUFACTURER:
                    String emui = prop.getProperty(EMUI_PROP_VERSION, "");
                    if (emui != null) {
                        int index = emui.indexOf('_');
                        if (index >= 0 && index < emui.length()) {
                            versionName = emui.substring(0, index);
                            versionCode = emui.substring(index + 1);
                        }
                    }
                    break;
                case COLOR_OS_MANUFACTURER:
                    versionCode = prop.getProperty(COLOR_OS_PROP_VERSION, "");
                    versionName = COLOR_OS_NAME;
                    break;
            }
            prop.close();
        } catch (IOException e) {
            printLog(e.getMessage(), true);
        }
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getBrand() {
        return brand;
    }

    public boolean isMIUI() {
        return MIUI_MANUFACTURER.equals(manufacturer);
    }

    /**
     * 原来是 isMIUI6()，但是代码写的 ver != 6，这就奇怪了，所以改名为 notMIUI5
     *
     * @return 不是 MIUI 5
     */
    public boolean notMIUI5() {
        int ver = getMIUIVer();
        return ver != 5 && ver != -1;
    }

    public int getMIUIVer() {
        if (!isMIUI()) {
            return -1;
        }

        if (TextUtils.isEmpty(versionName)) {
            return -1;
        }

        if (versionName.length() > 1) {
            return parse(versionName.substring(1), -1);
        } else if (versionName.length() == 1) {
            return parse(versionName, -1);
        }
        return -1;
    }

    public boolean isEMUI() {
        return EMUI_MANUFACTURER.equals(manufacturer);
    }

    public boolean isColorOS() {
        return COLOR_OS_MANUFACTURER.equals(manufacturer);
    }

    /**
     * 取到MIUI V8的分身信息
     * ？？？原来是上面的注释，但是找不到谁写的，代码中也没有有关8的判断，暂时不认为只有8了？？？
     */
    public int getMIUIUserId() {
        Integer userId = null;
        try {
            Class<?> userHandleClass = Class.forName("android.os.UserHandle");
            Method getUserIdMethod = userHandleClass.getDeclaredMethod("getUserId", int.class);
            getUserIdMethod.setAccessible(true);
            int uid = android.os.Process.myUid();
            userId = (Integer) getUserIdMethod.invoke(null, uid);
            printLog(String.format("getUserId, uid:%d, userId:%d", uid, userId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userId == null) {
            userId = 0; //cannot get id, user default.
        }
        return userId;
    }

    public String getUserAgent() {
        String userAgent = "";
        try {
            userAgent = System.getProperty(USER_AGENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userAgent;
    }

    private void printLog(String log) {
        printLog(log, false);
    }

    private void printLog(String log, boolean trace) {
        LogUtils.d(TAG, log);
    }

    private int parse(String num, int defaultNum) {
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            printLog(versionName + "\n" + e.getMessage(), true);
        }
        return defaultNum;
    }
}
