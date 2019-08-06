package com.linken.newssdk.export;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface INewsInfoCallback {
    //文章
    String TYPE_ARTICLE = "article";
    //广告
    String TYPE_AD = "Ad";
    //视屏
    String TYPE_VIDEO = "Video";

    void callback(String id, String title, String type, long duration);

    //Retention 是元注解，简单地讲就是系统提供的，用于定义注解的“注解”
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_ARTICLE, TYPE_AD, TYPE_VIDEO})
    @interface NewsTypeDef {
    }
}
