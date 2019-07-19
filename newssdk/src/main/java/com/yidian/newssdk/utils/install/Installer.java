package com.yidian.newssdk.utils.install;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.yidian.newssdk.utils.LogUtils;

import java.io.File;

/**
 * Created by chenyichang on 2017/10/11.
 */

public class Installer {

    private static final String APK_MIMETPYE_PREFIX = "application/vnd.android.package-archive";
    private static final int VPN_TYPE_TIMES = LogUtils.isDebug() ? 20 : 3;
    static int failedTimes = 0;  //进程内失败3次

    public static void install(Context ctx, String fileUri, boolean needProxy) {

        //按照http://blog.csdn.net/qq_31588719/article/details/70168295
        //没有权限执行解析操作，于是就会出现解析程序包时出现问题。
        //解决方案：
        //通过在代码中写入linux指令修改此apk文件的权限，改为全局可读可写可执行：
        installImp(ctx, fileUri);

    }

    /**
     * 调用系统安装，增加7.0判断（私有目录权限）
     *
     * @param ctx
     * @param fileUri
     */
    static void installImp(Context ctx, String fileUri) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(fileUri);
        i.setDataAndType(Uri.fromFile(file), APK_MIMETPYE_PREFIX);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }

}
