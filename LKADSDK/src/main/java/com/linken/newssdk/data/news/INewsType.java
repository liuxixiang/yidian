package com.linken.newssdk.data.news;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author zhangzhun
 * @date 2018/7/23
 */

public interface INewsType {

    int STYLE_UNSUPPORTED = -1;
    int STYLE_HBRID_NEWS = 0;
    int STYLE_HBRID_VIDEO = 1;
    int STYLE_HBRID_GALLERY = 2;
    int STYLE_THIRD_PARTY = 3;
    int STYLE_NEWS_COMMENTS = 4;
    int STYLE_NEWS_EXTERNAL = 5;

    @IntDef({STYLE_UNSUPPORTED, STYLE_HBRID_NEWS, STYLE_HBRID_VIDEO, STYLE_HBRID_GALLERY, STYLE_THIRD_PARTY, STYLE_NEWS_COMMENTS, STYLE_NEWS_EXTERNAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NEWS_STYLE {

    }


    String NEWS_STYLE = "style";
    String NORMAL_NEWS_URL = "news_url";
    String NEWS_DOCID = "docid";
}
