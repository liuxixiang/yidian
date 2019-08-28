package com.linken.yidian;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.linken.newssdk.NewsFeedsSDK;
import com.linken.newssdk.export.INewsInfoCallback;
import com.linken.newssdk.export.IReportInterface;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


/**
 * Created by chenyichang on 2018/5/18.
 */

public class YdApplication extends Application {
    private static final String TAG = YdApplication.class.getSimpleName();


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(
                this,
                BuildConfig.UMENG_KEY,
                BuildConfig.CHANNEL,
                MobclickAgent.EScenarioType.E_UM_NORMAL);

        MobclickAgent.startWithConfigure(config);
        MobclickAgent.setCatchUncaughtExceptions(true);
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        MobclickAgent.openActivityDurationTrack(false);  //禁止默认的页面统计

        initLeakCanary();

        /**
         * 初始化SDK
         */
        new NewsFeedsSDK.Builder()
                .setContext(getApplicationContext())
                .setFilter("趣步")
                .setDebugEnabled(BuildConfig.DEBUG)
                .build();

        NewsFeedsSDK.getInstance().setReportInterface(new IReportInterface() {

            @Override
            public void onPageSelected(String channelPageName) {
                Log.d(TAG, channelPageName);
            }
        });

        NewsFeedsSDK.getInstance().setNewsInfoCallback(new INewsInfoCallback() {

            @Override
            public void getConfig(final Config config) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AfferentInfo afferentinfo_article = new AfferentInfo(INewsInfoCallback.TYPE_ARTICLE, 10, 8);
                        AfferentInfo afferentinfo_video = new AfferentInfo(INewsInfoCallback.TYPE_VIDEO, 5, 4);
                        AfferentInfo afferentinfo_ad = new AfferentInfo(INewsInfoCallback.TYPE_AD, 3, 3);

                        config.setTotalRewardNum(50);
                        config.setShowCountDown(true);
                        config.setAfferentInfos(afferentinfo_article, afferentinfo_video, afferentinfo_ad);
                    }
                },5000);

            }


            @Override
            public void callback(int event, String id, String title, String type, String channel, int rewardNum, int expectDuration, int realDuration) {
                Log.e("lxh", "id=" + id + "----title=" + title
                        + "----channel=" + channel + "---type=" + type
                        + "---rewardNum=" + rewardNum
                        + "---expectDuration=" + expectDuration
                        + "---realDuration=" + realDuration);
            }

        });

        NewsFeedsSDK.getInstance().setReportInterface(new IReportInterface() {

            @Override
            public void onPageSelected(String channelPageName) {
                Log.d(TAG, channelPageName);
            }
        });


    }

    private void initLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }
}
