package com.yidian.newssdk;

import android.content.Context;
import android.util.Log;

import com.yidian.newssdk.core.ad.AdvertisementModule;
import com.yidian.newssdk.core.feeds.FeedFragment;
import com.yidian.newssdk.export.IReportInterface;
import com.yidian.newssdk.export.IShareInterface;
import com.yidian.newssdk.libraries.ydvd.YdMediaInterface;
import com.yidian.newssdk.libraries.ydvd.YdVideoPlayer;
import com.yidian.newssdk.protocol.newNetwork.business.helper.Get3rdInfoHelper;
import com.yidian.newssdk.protocol.newNetwork.business.helper.OpenPlatformHelper;
import com.yidian.newssdk.protocol.newNetwork.business.report.ReportProxy;
import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.utils.CustomizedToastUtil;
import com.yidian.newssdk.utils.DensityUtil;
import com.yidian.newssdk.utils.HMTAgentUtil;
import com.yidian.newssdk.utils.LogUtils;
import com.yidian.newssdk.utils.SPUtils;
import com.yidian.newssdk.utils.support.ImageDownloaderConfig;
import com.yidian.newssdk.utils.support.NetworkHelper;

/**
 * Created by chenyichang on 2018/5/18.
 */

public class NewsFeedsSDK {

    private static volatile NewsFeedsSDK sInstance = null;

    private Context mContext;
    private String mAppKey;
    private String mAppId;
    private boolean debug;
    private IShareInterface iShareInterface;
    private IReportInterface iReportInterface = ReportProxy.defaultIReportInterface;

    private NewsFeedsSDK(Builder builder) {
        this.mContext = builder.mContext;
        this.mAppKey = builder.mAppKey;
        this.mAppId = builder.mAppId;
        this.debug = builder.debug;

        ContextUtils.init(this.mContext);
        HMTAgentUtil.init();
        AdvertisementModule.getInstance().init();
        CustomizedToastUtil.init();
        SPUtils.init(this.mContext);
        LogUtils.setDebug(this.debug);
        ImageDownloaderConfig.init(this.mContext);
        DensityUtil.init();
        NetworkHelper.getInstance().init(this.mContext);
        OpenPlatformHelper.getOp(mAppId);
        Get3rdInfoHelper.request3rdInfo(mAppId);
    }

    public static NewsFeedsSDK getInstance() {
        if (sInstance == null) {
            String msg = "SDK还未初始化呢，请先通过 NewsFeedsSDK.Builder 进行初始化";
            throw new RuntimeException(msg);
        }

        return sInstance;
    }

    public static FeedFragment createFeedsFragment() {
        return FeedFragment.newInstanceInner();
    }

    public String getAppId() {
        return mAppId;
    }

    public String getAppKey() {
        return mAppKey;
    }

    public void setShareInterface(IShareInterface iShareInterface) {
        this.iShareInterface = iShareInterface;
    }

    public void setReportInterface(IReportInterface iReportInterface) {
        this.iReportInterface = iReportInterface;
    }

    public NewsFeedsSDK setCustomMediaplayer(YdMediaInterface jzMediaInterface) {
        YdVideoPlayer.setMediaInterface(jzMediaInterface);
        return this;
    }

    public YdMediaInterface getCustomMediaplayer() {
        return YdVideoPlayer.getMediaInterface();
    }

    public IShareInterface getShareInterface(){
        return this.iShareInterface;
    }

    public IReportInterface getReportInterface(){
        return this.iReportInterface;
    }

    public static final class Builder {

        private Context mContext;
        private String mAppKey;
        private String mAppId;
        private boolean debug;


        public Builder() {

        }

        public Builder(NewsFeedsSDK feedsSDK) {
            this.mContext = feedsSDK.mContext.getApplicationContext();
            this.mAppKey = feedsSDK.mAppKey;
            this.mAppId = feedsSDK.mAppId;
            this.debug = feedsSDK.debug;
        }

        public Builder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setAppKey(String appKey) {
            this.mAppKey = appKey;
            return this;
        }

        public Builder setAppId(String id) {
            this.mAppId = id;
            return this;
        }

        public Builder setDebugEnabled(boolean debugEnabled) {
            this.debug = debugEnabled;
            return this;
        }

        public NewsFeedsSDK build() {
            if (this.mContext == null) {
                if (this.debug) {
                    Log.e("NewsFeedsSDK", "Context cannot be null!");
                }
                return null;
            } else if (this.mAppKey != null && !this.mAppKey.isEmpty()) {
                if (this.mAppId != null && !this.mAppKey.isEmpty()) {
                    if (NewsFeedsSDK.sInstance == null) {
                        Class var1 = NewsFeedsSDK.class;
                        synchronized (NewsFeedsSDK.class) {
                            if (NewsFeedsSDK.sInstance == null) {
                                NewsFeedsSDK.sInstance = new NewsFeedsSDK(this);
                            }
                        }
                    }

                    return NewsFeedsSDK.sInstance;
                } else {
                    if (this.debug) {
                        Log.e("NewsFeedsSDK", "AppSecret cannot be null or empty!");
                    }
                    return null;
                }
            } else {
                if (this.debug) {
                    Log.e("NewsFeedsSDK", "AppKey cannot be null or empty!");
                }
                return null;
            }
        }
    }
}
