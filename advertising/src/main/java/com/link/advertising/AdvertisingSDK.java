package com.link.advertising;

import android.content.Context;
import android.util.Log;

import com.link.advertising.utils.ContextUtils;
import com.link.advertising.utils.LogUtils;


public class AdvertisingSDK {

    private static volatile AdvertisingSDK sInstance = null;

    private Context mContext;
    private String mAppKey;
    private String mAppId;
    private boolean debug;
    private String mFilterRegex;

    private AdvertisingSDK(Builder builder) {
        this.mContext = builder.mContext;
        this.mAppKey = builder.mAppKey;
        this.mAppId = builder.mAppId;
        this.debug = builder.debug;
        this.mFilterRegex = builder.mFilterRegex;

        ContextUtils.init(this.mContext);
        LogUtils.setDebug(this.debug);
    }

    public static AdvertisingSDK getInstance() {
        if (sInstance == null) {
            String msg = "SDK还未初始化呢，请先通过 AdvertisingSDK.Builder 进行初始化";
            throw new RuntimeException(msg);
        }

        return sInstance;
    }


    public String getAppId() {
        return mAppId;
    }

    public String getAppKey() {
        return mAppKey;
    }


    public static final class Builder {

        private Context mContext;
        private String mAppKey;
        private String mAppId;
        private boolean debug;
        private String mFilterRegex;


        public Builder() {

        }

        public Builder(AdvertisingSDK feedsSDK) {
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

        public Builder setFilterRegex(String filterRegex) {
            this.mFilterRegex = filterRegex;
            return this;
        }


        public AdvertisingSDK build() {
            if (this.mContext == null) {
                if (this.debug) {
                    Log.e("AdvertisingSDK", "Context cannot be null!");
                }
                return null;
            } else if (this.mAppKey != null && !this.mAppKey.isEmpty()) {
                if (this.mAppId != null && !this.mAppKey.isEmpty()) {
                    if (AdvertisingSDK.sInstance == null) {
                        Class var1 = AdvertisingSDK.class;
                        synchronized (AdvertisingSDK.class) {
                            if (AdvertisingSDK.sInstance == null) {
                                AdvertisingSDK.sInstance = new AdvertisingSDK(this);
                            }
                        }
                    }

                    return AdvertisingSDK.sInstance;
                } else {
                    if (this.debug) {
                        Log.e("AdvertisingSDK", "AppSecret cannot be null or empty!");
                    }
                    return null;
                }
            } else {
                if (this.debug) {
                    Log.e("AdvertisingSDK", "AppKey cannot be null or empty!");
                }
                return null;
            }
        }
    }
}
