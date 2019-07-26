package com.yidian.newssdk.core.newslist;

import com.yidian.newssdk.export.IExposeInterface;

/**
 * Created by liuyue on 2017/4/6.
 */

public interface IRefreshableNewsList extends IExposeInterface {

    void refreshData(boolean isAutoRefresh);

}
