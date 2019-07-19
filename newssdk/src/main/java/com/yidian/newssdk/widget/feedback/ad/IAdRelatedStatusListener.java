package com.yidian.newssdk.widget.feedback.ad;

import android.content.Context;

import com.yidian.ad.data.AdvertisementCard;


/**
 * 此接口负责向外暴露广告相关的各个状态，如用户负反馈了广告等等，方便依赖Advertisement的Module获知，可用于日志上报等工作
 *
 * Created by liuyue on 2017/11/21.
 */

public interface IAdRelatedStatusListener {

    /**
     * 点击广告的负反馈
     * @param context
     */
    void badFeedbackOnAd(Context context);

    /**
     * 静默安装app，app是通过广告下载的
     * @param fileUri：下载后的文件的uri
     * @param success：静默安装是否成功
     */
    void silentInstallApp(String fileUri, boolean success);

    /**
     * 非静默安装app，app是通过广告下载的
     * @param fileUri：下载后的文件的uri
     * @param success：静默安装是否成功
     */
    void nonSilentInstallApp(String fileUri, boolean success);

    /**
     * App是否处于等于安装的状态，app是通过广告下载的
     * @param card：对应的广告卡片
     * @param success：是否能安装
     */
    void waitForInstallApp(AdvertisementCard card, boolean success);

    /**
     * 检查下载后的App是否是劫持劫持过的
     * @param filePath：下载后文件的路径
     * @param pass：是否通过检查
     */
    void checkDownloadAppByPackageName(String filePath, boolean pass);

    /**
     * 上报第三方事件（直接调用某个AdvertisementCard中的url）后的结果
     * @param url
     * @param success
     */
    void reportThirdPartyEvent(String url, boolean success);

}
