package com.linken.newssdk;

import android.content.Context;
import android.util.Log;

import com.linken.newssdk.core.ad.AdvertisementModule;
import com.linken.newssdk.core.feeds.FeedFragment;
import com.linken.newssdk.export.INewsInfoCallback;
import com.linken.newssdk.export.IReportInterface;
import com.linken.newssdk.export.IShareInterface;
import com.linken.newssdk.libraries.ydvd.YdMediaInterface;
import com.linken.newssdk.libraries.ydvd.YdVideoPlayer;
import com.linken.newssdk.protocol.newNetwork.business.helper.Get3rdInfoHelper;
import com.linken.newssdk.protocol.newNetwork.business.helper.OpenPlatformHelper;
import com.linken.newssdk.protocol.newNetwork.business.report.ReportProxy;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.CustomizedToastUtil;
import com.linken.newssdk.utils.DensityUtil;
import com.linken.newssdk.utils.HMTAgentUtil;
import com.linken.newssdk.utils.LogUtils;
import com.linken.newssdk.utils.SPUtils;
import com.linken.newssdk.utils.support.ImageDownloaderConfig;
import com.linken.newssdk.utils.support.NetworkHelper;

/**
 * Created by chenyichang on 2018/5/18.
 */

public class NewsFeedsSDK {

    private static volatile NewsFeedsSDK sInstance = null;

    private Context mContext;
    private String mAppKey;
    private String mAppId;
    private boolean debug;
    private String mFilterRegex;

    private IShareInterface iShareInterface;
    private INewsInfoCallback mNewsInfoCallback;
    private IReportInterface iReportInterface = ReportProxy.defaultIReportInterface;
    private INewsInfoCallback.Config mConfig;

    private NewsFeedsSDK(Builder builder) {
        this.mContext = builder.mContext;
        this.mAppKey = builder.mAppKey;
        this.mAppId = builder.mAppId;
        this.debug = builder.debug;
        this.mFilterRegex = builder.mFilterRegex;
        mConfig = new INewsInfoCallback.Config();

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

    public String getFilterRegex() {
        return mFilterRegex;
    }

    public INewsInfoCallback.Config getConfig() {
        return mConfig;
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

    public void setNewsInfoCallback(INewsInfoCallback newsInfoCallback) {
        this.mNewsInfoCallback = newsInfoCallback;
        mNewsInfoCallback.getConfig(mConfig);
    }

    public YdMediaInterface getCustomMediaplayer() {
        return YdVideoPlayer.getMediaInterface();
    }

    public IShareInterface getShareInterface() {
        return this.iShareInterface;
    }

    public IReportInterface getReportInterface() {
        return this.iReportInterface;
    }

    public INewsInfoCallback getNewsInfoCallback() {
        return mNewsInfoCallback;
    }

    public static final class Builder {

        private Context mContext;
        private String mAppKey = "SzDT5xJF8NNJSZVs7izeE7TDTY916yZa";
        private String mAppId = "mVv7l58rwJlrUUcoOvj_JAtc";
        private boolean debug;
        private String mFilterRegex;


        public Builder() {

        }

        public Builder(NewsFeedsSDK feedsSDK) {
            this.mContext = feedsSDK.mContext.getApplicationContext();
            this.mAppKey = feedsSDK.mAppKey;
            this.mAppId = feedsSDK.mAppId;
            this.debug = feedsSDK.debug;
            this.mFilterRegex = feedsSDK.mFilterRegex;
        }

        public Builder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        private Builder setAppKey(String appKey) {
            this.mAppKey = appKey;
            return this;
        }

        private Builder setAppId(String id) {
            this.mAppId = id;
            return this;
        }

        public Builder setDebugEnabled(boolean debugEnabled) {
            this.debug = debugEnabled;
            return this;
        }

        public Builder setFilterRegex(String filterRegex) {
            this.mFilterRegex = filterRegex;
            return this;
        }

        /**
         * 设置单个关键字过滤
         *
         * @param filter
         * @return
         */
        public Builder setFilter(String filter) {
            this.mFilterRegex = ".*(" + filter + ").*";
            return this;
        }


        /**
         * 设置多个关键字过滤
         *
         * @param filters
         * @return
         */
        public Builder setFilter(String[] filters) {
            if (filters != null && filters.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < filters.length; i++) {
                    sb.append(filters[i]);
                    if (i < filters.length - 1) {
                        sb.append("|");
                    }
                }
                setFilter(sb.toString());
            }
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
