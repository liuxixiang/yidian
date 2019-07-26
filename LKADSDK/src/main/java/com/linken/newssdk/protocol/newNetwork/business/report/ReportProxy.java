package com.linken.newssdk.protocol.newNetwork.business.report;

import com.linken.newssdk.NewsFeedsSDK;
import com.linken.newssdk.export.IReportInterface;

/**
 * Created by chenyichang on 2018/3/31.
 */

public class ReportProxy {

    public static final String PAGE_VIDEO = "page_video";
    public static final String PAGE_EMBED = "page_embed";
    public static final String PAGE_FEED = "page_feed";
    public static final String PAGE_LIST = "page_list";
    public static final String PAGE_WEB = "page_web";
    public static final String PAGE_NEWS = "page_news";

    public static IReportInterface defaultIReportInterface = new IReportInterface() {

        @Override
        public void onPageSelected(String channelPageName) {

        }
    };

    public static void onPageSelected(String channelPageName) {
        NewsFeedsSDK.getInstance().getReportInterface().onPageSelected(channelPageName);
    }

}
