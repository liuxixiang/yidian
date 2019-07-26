package com.yidian.newssdk.core.ad;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.widget.feedback.ad.IAdRelatedStatusListener;

/**
 * 广告Module初始化入口，目前作用是设置一个全局的ApplicationContext
 * Created by patrickleong on 11/05/2017.
 */

public class AdvertisementModule {
    @SuppressLint("StaticFieldLeak")
    private static volatile AdvertisementModule sInstance = null;
    private Application mApp;
    private IAdRelatedStatusListener mAdRelatedStateListener;


    private AdvertisementModule(){
    }

    public void init() {
        sInstance.mApp = (Application) ContextUtils.getApplicationContext();
//        mAdRelatedStateListener = adRelatedStatusListener;
    }

    public static AdvertisementModule getInstance(){
        if (sInstance == null) {
            synchronized (AdvertisementModule.class) {
                if (sInstance == null) {
                    sInstance = new AdvertisementModule();
                }
            }
        }
        return sInstance;
    }

    public Application getApplication(){
        if (sInstance.mApp == null) {
            throw new IllegalStateException("Initialization error. AdvertisementModule should call init method first!");
        }
        return mApp;
    }

    public Context getApplicationContext(){
        if (sInstance.mApp == null) {
            throw new IllegalStateException("Initialization error. AdvertisementModule should call init method first!");
        }
        return mApp.getApplicationContext();
    }

    public IAdRelatedStatusListener getAdRelatedStateListener() {
        return mAdRelatedStateListener;
    }

    public boolean isZixunBuild(){
        if (sInstance.mApp == null) {
            throw new IllegalStateException("Initialization error. AdvertisementModule should call init method first!");
        }
        return false;
    }
}
