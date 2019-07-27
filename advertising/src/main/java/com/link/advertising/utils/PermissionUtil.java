package com.link.advertising.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Créé par liusiqian 2016/12/2.
 */

public class PermissionUtil {
    public static List<String> permissionsNeedToApply;


    public static boolean hasPermissionGroup(Context context, String permission) {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && context.checkSelfPermission(
                permission) != PackageManager.PERMISSION_GRANTED);
    }

    public static void checkNecessaryPermissions(Context context) {
        permissionsNeedToApply = new ArrayList<>();
        if (!PermissionUtil.hasPermissionGroup(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeedToApply.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!PermissionUtil.hasPermissionGroup(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeedToApply.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        //无论如何都去申请Manifest.permission.READ_PHONE_STATE，如果之前已经申请到了，就直接读取电话号码，iemi等敏感信息
        //防止用户启动后，收回授权
        permissionsNeedToApply.add(Manifest.permission.READ_PHONE_STATE);

    }
}
