package com.linken.newssdk.utils;

import android.Manifest;
import android.app.Service;
import android.os.Vibrator;

/**
 * @author zhangzhun
 * @date 2018/8/4
 */

public class VibrateUtils {

    private static long[] shockArray = new long[] {12, 10};
    public static void Vibrate() {
        if (PermissionUtil.hasPermissionGroup(ContextUtils.getApplicationContext(), Manifest.permission.VIBRATE)) {
            Vibrator vib = (Vibrator) ContextUtils.getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(shockArray, -1);
        }
    }
}
