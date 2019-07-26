package com.linken.newssdk.core.newslist;

import com.linken.newssdk.export.IExposeInterface;

/**
 * Created by liuyue on 2017/4/6.
 */

public interface IRefreshableNewsList extends IExposeInterface {

    void refreshData(boolean isAutoRefresh);

}
