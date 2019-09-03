package com.linken.newssdk.toutiao;

import android.content.Context;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.linken.newssdk.NewsConfig;
import com.linken.newssdk.R;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {


    public static TTAdManager get() {
        return TTAdSdk.getAdManager();
    }

    public static void init(Context context) {
        doInit(context);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context) {
        TTAdSdk.init(context, buildConfig(context));
    }

    private static TTAdConfig buildConfig(Context context) {
        return new TTAdConfig.Builder()
                .appId(NewsConfig.TT_APP_ID)
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .appName(context.getString(R.string.app_name))
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(false) //是否在锁屏场景支持展示广告落地页
                .debug(true) //可以通过日志排查问题
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                .supportMultiProcess(false)//是否支持多进程，true支持
                .build();
    }
}
