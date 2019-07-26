package com.yidian.newssdk.core.ad;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.NewsFeedsSDK;
import com.yidian.newssdk.R;
import com.yidian.newssdk.SDKContants;
import com.yidian.newssdk.data.ad.ADConstants;
import com.yidian.newssdk.data.ad.AdReportConstants;
import com.yidian.newssdk.data.ad.db.AdvertisementDbUtil;
import com.yidian.newssdk.data.ad.event.DownloadEvent;
import com.yidian.newssdk.data.ad.tencent.TencentAdMonitorHelper;
import com.yidian.newssdk.data.ad.tencent.TencentClickParamData;
import com.yidian.newssdk.data.card.base.ListViewItemData;
import com.yidian.newssdk.data.pref.GlobalAccount;
import com.yidian.newssdk.protocol.newNetwork.business.request.imp.RequestAdReport;
import com.yidian.newssdk.protocol.newNetwork.core.AsyncHttpClient;
import com.yidian.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;
import com.yidian.newssdk.utils.AdUtils;
import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.utils.CustomizedToastUtil;
import com.yidian.newssdk.utils.DeviceUtils;
import com.yidian.newssdk.utils.InstallPackageFileUtil;
import com.yidian.newssdk.utils.IpUtils;
import com.yidian.newssdk.utils.LocationMgr;
import com.yidian.newssdk.utils.NetUtil;
import com.yidian.newssdk.utils.SystemUtil;
import com.yidian.newssdk.utils.ThreadUtils;
import com.yidian.newssdk.utils.broadcastbus.BroadcastBus;
import com.yidian.newssdk.widget.views.SimpleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AdvertisementUtil {

    private static final int RETRY_LIMIT = 1;

    public static final String EVENT_VIEW = "view";
    public static final String EVENT_VIEW_NS = "view_1s";
    public static final String EVENT_CLICK = "click";
    public static final String EVENT_RESERVE = "reserve";
    public static final String DEEPLINK_URL = "deeplinkUrl";
    public static final String DOC_SOURCE = "doc_source";
    public static final String EVENT_APP_START_DOWNLOAD = "app_start_download";
    public static final String EVENT_APP_DOWNLOAD_SUCCESS = "app_download_success";
    public static final String EVENT_APP_INSTALL_SUCCESS = "app_install_success";
    public static final String OPEN_TYPE = "otype";//0：用deepLink打开，1：用packageName打开
    public static final String EVENT_APP_LAUNCH_START = "app_launch_start";
    public static final String EVENT_APP_LAUNCH_SUCCESS = "app_launch_success";
    public static final String EVENT_APP_LAUNCH_FAIL = "app_launch_fail";
    public static final String CLICK_ID = "clickid";
    public static final String EVENT_LANDING_PAGE = "landing_page";
    public static final String CLIENT_NAME = "clientname";
    public static final String CLIENT_PHONE = "clientphone";
    public static final String DISLIKE_REASONS = "dr";
    public static final String DISLIKE_REASONS_CODE = "reason";
    public static final String EVENT_DISLIKE = "dislike";

    public static final String EVENT_VIDEO_CLICK = "video_click";
    public static final String EVENT_VIDEO_FAIL = "video_fail";
    public static final String EVENT_VIDEO_START = "video_start";
    public static final String EVENT_VIDEO_PAUSE = "video_pause";
    public static final String EVENT_VIDEO_FINISH = "video_finish";
    public static final String EVENT_VIDEO_END = "video_end";
    public static final String EVENT_VIDEO_RESUME = "video_resume";
    public static final String EVENT_VIDEO_LOADING = "video_loading";
    public static final String EVENT_VIDEO_FORWARD = "video_forward";
    public static final String EVENT_VIDEO_FIRST_QUARTILE = "first_quartile";
    public static final String EVENT_VIDEO_MIDPOINT = "midpoint";
    public static final String EVENT_VIDEO_THIRD_QUARTILE = "third_quartile";
    public static final String EVENT_VIDEO_S5 = "s5";
    public static final String EVENT_VIDEO_S15 = "s15";
    public static final String EVENT_VIDEO_S30 = "s30";
    public static final String APP_DOWNLOAD_SOURCE = "app_download_source";

    private static final String[] VIDEO_EVENTS = new String[]{EVENT_VIDEO_FAIL, EVENT_VIDEO_START,
            EVENT_VIDEO_PAUSE, EVENT_VIDEO_FINISH, EVENT_VIDEO_END, EVENT_VIDEO_FIRST_QUARTILE,
            EVENT_VIDEO_MIDPOINT, EVENT_VIDEO_THIRD_QUARTILE, EVENT_VIDEO_S5, EVENT_VIDEO_S15, EVENT_VIDEO_S30};
    public static final String EVENT_PICTURE_GALLERY_VIEW = "viewnum";
    public static boolean mbReportAdEvent = true;

    private static DownloadManager downloadManager;
    private static long downloadId;
    public static Map<AdvertisementCard, Long> mDownloadProgressMap = new HashMap<>();
    private static DownloadProgressMonitor mProgressMonitor = null;

    public static void reportViewNSEvent(AdvertisementCard card) {
        reportViewEvent(card, EVENT_VIEW_NS, true);
    }

    public static void reportViewEvent(AdvertisementCard card) {
        reportViewEvent(card, EVENT_VIEW, true);
    }

    public static void reportViewClickEvent(AdvertisementCard card) {
        reportViewEvent(card, EVENT_CLICK, true);
    }


    /**
     * Use http request to get filename and downloadFile later.
     *
     * @param mCtx
     * @param card
     */
    public static void retreiveNameAnddownloadFile(Context mCtx, AdvertisementCard card, @ADConstants.AppDownloadSource int downloadSource) {
        retreiveNameAnddownloadFile(mCtx, card, true, true, downloadSource);
    }


    public static void retreiveNameAnddownloadFile(Context mCtx, AdvertisementCard card, boolean reportEvent, boolean downloadAfterRedirect, @ADConstants.AppDownloadSource int downloadSource) {
        new AppDownloadTask(mCtx, card, reportEvent, downloadAfterRedirect, downloadSource).execute(card);
    }

    public static boolean appCanOpen(AdvertisementCard adCard, Context context) {
        return isAppInstalledAccordingToDeepLinkUrl(adCard.getDeeplinkUrl(), context)
                || isAppInstalledAccordingToPackageName(adCard.getPackageName(), context);
    }

    public static boolean isAppInstalledAccordingToPackageName(String packageName, Context context) {
        if (packageName == null || context == null) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(packageName, PackageManager.GET_DISABLED_COMPONENTS) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void reportDislikeEvent(final AdvertisementCard card, String reasons, String reasonsCode) {
        if (card != null) {
            Map<String, String> extendParameters = new HashMap<>();
            extendParameters.put(DISLIKE_REASONS, reasons);
            extendParameters.put(DISLIKE_REASONS_CODE, reasonsCode);
            reportEvent(card, EVENT_DISLIKE, -1, null, 0, UUID.randomUUID().toString(), extendParameters, true);
        }
    }

    public static void reportTencentClickEvent(final AdvertisementCard card, boolean reportExposal, String clickId, TencentClickParamData clickParamData) {
        HashMap<String, String> params = null;
        if (clickParamData != null) {
            params = new HashMap<>();
            params.put(AdReportConstants.WIDTH, String.valueOf(clickParamData.getWidth()));
            params.put(AdReportConstants.HEIGHT, String.valueOf(clickParamData.getHeight()));
            params.put(AdReportConstants.DOWN_X, String.valueOf(clickParamData.getDownX()));
            params.put(AdReportConstants.DOWN_Y, String.valueOf(clickParamData.getDownY()));
            params.put(AdReportConstants.UP_X, String.valueOf(clickParamData.getUpX()));
            params.put(AdReportConstants.UP_Y, String.valueOf(clickParamData.getUpY()));

        }
        reportClickEvent(card, reportExposal, clickId, true, params);
    }

    /**
     * 腾讯联盟的广告曝光日志，要求像素出现50%以上且持续一秒，广告展示期间仅曝光一次
     * <p>
     * 给腾讯联盟上报的同时也给我们的服务器上报一份
     *
     * @param card
     */
    public static void reportTencentViewEvent(final AdvertisementCard card) {
        if (card != null) {
            boolean shouldReportEvent = TencentAdMonitorHelper.shouldReportView(card);
            if (!shouldReportEvent || card.viewMonitorUrls == null || card.viewMonitorUrls.length == 0) {
                return;
            }
            if (!TextUtils.isEmpty(card.docSource)) {
                String source = card.docSource;
                try {
                    source = URLEncoder.encode(card.docSource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Map<String, String> map = new HashMap<>();
                map.put(DOC_SOURCE, source);
                reportEvent(card, EVENT_VIEW, -1, null, 0, UUID.randomUUID().toString(), map, true);
            } else {
                reportEvent(card, EVENT_VIEW, true);
            }
            TencentAdMonitorHelper.reportThirdPartyEvents(card.viewMonitorUrls);
        }
    }

    public static void reportLandingPageEvent(final AdvertisementCard card, long cid, String url) {
        reportLandingPageEvent(card, cid, url, true);
    }

    public static void reportLandingPageEvent(final AdvertisementCard card, long cid, String url, boolean needRetry) {
        //Report event only if event report is enable.
        reportEvent(card, AdReportConstants.EVENT_LANDING_PAGE, cid, url, 0, UUID.randomUUID().toString(), null, needRetry);
    }

    public static void reportLandingPageStayEvent(final AdvertisementCard card, long cid, String url, long stayTime) {

        Map<String, String> params = new HashMap<>();
        params.put(AdReportConstants.STAY_TIME, String.valueOf(stayTime));
        reportEvent(card, AdReportConstants.EVENT_LANDING_PAGE_STAY, cid, url, 0, UUID.randomUUID().toString(), params, true);
    }


    private static class AppDownloadTask extends AsyncTask<AdvertisementCard, Void, String> {

        private boolean mReportEvent;
        private Context mContext;
        private AdvertisementCard mAdCard;
        private boolean mDownloadAfterRedirect;
        private int mDownloadSource;

        public AppDownloadTask(Context context, AdvertisementCard adCard, boolean reportEvent, boolean downloadAfterRedirect, int downloadSource) {
            this.mReportEvent = reportEvent;
            this.mAdCard = adCard;
            this.mContext = context;
            this.mDownloadAfterRedirect = downloadAfterRedirect;
            this.mDownloadSource = downloadSource;
        }

        @Override
        protected String doInBackground(AdvertisementCard... cards) {
            String filename = null;
            AdvertisementCard card = cards[0];
            if (!TextUtils.isEmpty(card.actionUrl)) {
                try {
                    URL url = new URL(card.actionUrl);
                    HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
                    ucon.setInstanceFollowRedirects(false);
                    String redirectUrl = ucon.getHeaderField("Location");
                    if (redirectUrl == null) {
                        redirectUrl = ucon.getHeaderField("location");
                    }

                    if (!TextUtils.isEmpty(redirectUrl)) {
                        filename = getUrlFileName(redirectUrl);
                    } else {
                        Log.e(ADConstants.ADVERTISEMENT_LOG, "Cannot get file url:" + card.actionUrl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (filename == null) {
                filename = card.title;
            }

            return filename;
        }

        @Override
        protected void onPostExecute(String filename) {
            if (mDownloadAfterRedirect) {
                AdvertisementUtil.downloadFile(mContext, mAdCard, filename, filename + ".apk", mReportEvent, mDownloadSource);
            }
        }
    }


    public static void reportSimpleClickMonitors(final AdvertisementCard card) {
        if (card != null) {
//            if (logEnabled) {
//                Log.v(AdConstants.AD_LOG, "reportEvent:" + BaseAdReportAPI.EVENT_CLICK + ",template:" + card.getTemplate());
//            }
            if (card.clickMonitorUrls != null && card.clickMonitorUrls.length > 0) {
                AdMonitorHelper.reportThirdPartyEvents(card.clickMonitorUrls, String.valueOf(card.getAid()), false);
            }

        }
    }

    public static void reportReserveEvent(final AdvertisementCard card, String clientName, String clientPhone, String clickId) {
        if (card != null) {

            if (AdvertisementUtil.mbReportAdEvent) {
                Map<String, String> extendParameters = new HashMap<>();
                if (!TextUtils.isEmpty(clientName)) {
                    extendParameters.put(CLIENT_NAME, clientName);
                }
                if (!TextUtils.isEmpty(clientPhone)) {
                    extendParameters.put(CLIENT_PHONE, clientPhone);
                }
                if (!TextUtils.isEmpty(clickId)) {
                    extendParameters.put(CLICK_ID, clickId);
                }
                if (!TextUtils.isEmpty(card.docSource)) {
                    String source = card.docSource;
                    try {
                        source = URLEncoder.encode(card.docSource);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    extendParameters.put(DOC_SOURCE, source);
                }
                reportEvent(card, EVENT_RESERVE, -1, null, 0, UUID.randomUUID().toString(), extendParameters, true);
            }
        }
    }

    public static void reportAppLaunchStartEvent(final AdvertisementCard card, int launchType) {
        if (card != null) {
            Map<String, String> params = new HashMap<>();
            params.put(OPEN_TYPE, String.valueOf(launchType));
            reportEvent(card, EVENT_APP_LAUNCH_START, -1, null, 0, UUID.randomUUID().toString(), params, true);
        }
    }

    public static void reportAppLaunchFailEvent(final AdvertisementCard card, int launchType) {
        if (card != null) {
            Map<String, String> params = new HashMap<>();
            params.put(OPEN_TYPE, String.valueOf(launchType));
            reportEvent(card, EVENT_APP_LAUNCH_FAIL, -1, null, 0, UUID.randomUUID().toString(), params, true);
        }
    }

    public static void reportAppLaunchSuccessEvent(final AdvertisementCard card, int launchType) {
        if (card != null) {
            Map<String, String> params = new HashMap<>();
            params.put(OPEN_TYPE, String.valueOf(launchType));
            reportEvent(card, EVENT_APP_LAUNCH_SUCCESS, -1, null, 0, UUID.randomUUID().toString(), params, true);
        }
    }

    public static boolean isAppInstalledAccordingToDeepLinkUrl(String deepLinkUrl, Context context) {
        if (context != null
                && !TextUtils.isEmpty(deepLinkUrl)
                && !TextUtils.equals("http", Uri.parse(deepLinkUrl).getScheme())
                && !TextUtils.equals("https", Uri.parse(deepLinkUrl).getScheme())) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(deepLinkUrl));
            final PackageManager mgr = context.getPackageManager();
            List<ResolveInfo> list =
                    mgr.queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);
            return list != null && list.size() > 0;
        } else {
            return false;
        }
    }

    private static void reportViewEvent(final AdvertisementCard card, String eventType, boolean needRetry) {
        if (card != null) {
            if (TextUtils.equals(eventType, EVENT_VIEW) && card.shouldResetViewId) {
                card.viewId = System.currentTimeMillis();//重新设置AdCard的view id
            }
            boolean shouldReportEvent = true;
            if (card.viewType == AdvertisementCard.VIEW_TYPE_ONCE) {
                shouldReportEvent = AdMonitorHelper.shouldReportView(card);
                if (shouldReportEvent) {
                    card.viewno = 0;
                } else {
                    card.viewno = 1;
                }
            }

            if (!TextUtils.isEmpty(card.docSource)) {
                String source = card.docSource;
                try {
                    source = URLEncoder.encode(card.docSource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Map<String, String> map = new HashMap<>();
                map.put(DOC_SOURCE, source);
                reportEvent(card, eventType, -1, null, 0, UUID.randomUUID().toString(), map, needRetry);
            } else {
                reportEvent(card, eventType, needRetry);
            }


            if (TextUtils.equals(eventType, EVENT_VIEW) && shouldReportEvent && card.viewMonitorUrls != null
                    && card.viewMonitorUrls.length > 0) {
                AdMonitorHelper.reportThirdPartyEvents(card.viewMonitorUrls, String.valueOf(card.getAid()), true);
            }
        }
    }


    /**
     * 汇报广告事件
     *
     * @param card      广告
     * @param eventType 广告事件
     */
    public static void reportEvent(final AdvertisementCard card, final String eventType, boolean needRetry) {
        //Report event only if event report is enable.
        if (card == null) {
            return;
        }

        reportEvent(card, eventType, -1, null, 0, UUID.randomUUID().toString(), null, needRetry);

    }

    private static void reportEvent(final AdvertisementCard card, final String eventType, final long cid,
                                    final String url, final int count, final String uuid,
                                    final Map<String, String> extendParameters,
                                    final boolean needRetry) {
        RequestAdReport requestAdReport = new RequestAdReport();
        if (AdReportConstants.EVENT_LANDING_PAGE.equals(eventType)) {
            requestAdReport.setLandingPageEvent(card, cid, url, uuid);
        } else if (AdReportConstants.EVENT_DISLIKE.equals(eventType)) {
            requestAdReport.setDislikeEvent(card, uuid, extendParameters.get(AdReportConstants.DISLIKE_REASONS), extendParameters.get(AdReportConstants.DISLIKE_REASONS_CODE));
        } else if (AdReportConstants.EVENT_SHARE.equals(eventType)) {
            requestAdReport.setShareEvent(card, cid, url, uuid, Integer.valueOf(extendParameters.get(AdReportConstants.SHARE_SOURCE)));
        } else if (AdReportConstants.EVENT_BACK.equals(eventType)) {
            requestAdReport.setBackEvent(extendParameters.get(AdReportConstants.BACK_SOURCE));
        } else if (AdReportConstants.EVENT_APP_START_DOWNLOAD.equals(eventType)) {
            requestAdReport.setAppStartDownload(card, Integer.valueOf(extendParameters.get(AdReportConstants.APP_DOWNLOAD_SOURCE)), uuid);
        } else {
            requestAdReport.setAdInputs(card, eventType, uuid);
        }

        if (AdReportConstants.EVENT_RESERVE.equals(eventType) && extendParameters != null) {
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.CLIENT_NAME))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.CLIENT_NAME, extendParameters.get(AdReportConstants.CLIENT_NAME));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.CLIENT_PHONE))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.CLIENT_PHONE, extendParameters.get(AdReportConstants.CLIENT_PHONE));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.CLICK_ID))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.CLICK_ID, extendParameters.get(AdReportConstants.CLICK_ID));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.DOC_SOURCE))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.DOC_SOURCE, extendParameters.get(AdReportConstants.DOC_SOURCE));
            }
        }

        if ((AdReportConstants.EVENT_CLICK.equals(eventType) || AdReportConstants.EVENT_VIDEO_CLICK.equals(eventType)) && extendParameters != null) {
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.CLICK_ID))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.CLICK_ID, extendParameters.get(AdReportConstants.CLICK_ID));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.DOC_SOURCE))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.DOC_SOURCE, extendParameters.get(AdReportConstants.DOC_SOURCE));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.OPEN_LINK_TYPE))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.OPEN_LINK_TYPE, extendParameters.get(AdReportConstants.OPEN_LINK_TYPE));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.WIDTH))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.WIDTH, extendParameters.get(AdReportConstants.WIDTH));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.HEIGHT))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.HEIGHT, extendParameters.get(AdReportConstants.HEIGHT));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.DOWN_X))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.DOWN_X, extendParameters.get(AdReportConstants.DOWN_X));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.DOWN_Y))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.DOWN_Y, extendParameters.get(AdReportConstants.DOWN_Y));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.UP_X))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.UP_X, extendParameters.get(AdReportConstants.UP_X));
            }
            if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.UP_Y))) {
                requestAdReport.addCustomizedParameters(AdReportConstants.UP_Y, extendParameters.get(AdReportConstants.UP_Y));
            }

            if ((AdReportConstants.EVENT_VIEW.equals(eventType) || AdReportConstants.EVENT_VIEW_NS.equals(eventType)) && extendParameters != null) {
                if (!TextUtils.isEmpty(extendParameters.get(AdReportConstants.DOC_SOURCE))) {
                    requestAdReport.addCustomizedParameters(AdReportConstants.DOC_SOURCE, extendParameters.get(AdReportConstants.DOC_SOURCE));
                }
            }

            if (AdReportConstants.EVENT_LANDING_PAGE_STAY.equals(eventType) && extendParameters != null) {
                requestAdReport.addCustomizedParameters(AdReportConstants.STAY_TIME, extendParameters.get(AdReportConstants.STAY_TIME));
            }

            if ((AdReportConstants.EVENT_APP_LAUNCH_START.equals(eventType)
                    || AdReportConstants.EVENT_APP_LAUNCH_SUCCESS.equals(eventType)
                    || AdReportConstants.EVENT_APP_LAUNCH_FAIL.equals(eventType)
            ) && extendParameters != null) {
                requestAdReport.addCustomizedParameters(AdReportConstants.OPEN_TYPE, extendParameters.get(AdReportConstants.OPEN_TYPE));
            }

            if (AdReportConstants.EVENT_SPLASH_SKIP.equals(eventType) && extendParameters != null) {
                requestAdReport.addCustomizedParameters(AdReportConstants.SKIP_TIME, extendParameters.get(AdReportConstants.SKIP_TIME));
            }
        }

        new AsyncHttpClient().post(requestAdReport, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {

            }

            @Override
            public void onFailure(Throwable e) {
                //Retry n times;
                if (needRetry && count < RETRY_LIMIT) {
                    reportEvent(card, eventType, cid, url, count + 1, uuid, extendParameters, needRetry);
                }
            }
        });
    }

    public static void reportClickEvent(final AdvertisementCard card, boolean reportExposal, String clickId) {
        reportClickEvent(card, reportExposal, clickId, true, null);
    }

    public static void reportClickEvent(final AdvertisementCard card, boolean reportExposal, String clickId, boolean needRetry) {
        reportClickEvent(card, reportExposal, clickId, needRetry, null);
    }

    public static void reportClickEvent(final AdvertisementCard card, boolean reportExposal, String clickId, boolean needRetry, HashMap<String, String> params) {
        if (card != null) {

            if (AdvertisementUtil.mbReportAdEvent) {
                Map<String, String> extendParameters;
                if (params != null) {
                    extendParameters = params;
                } else {
                    extendParameters = new HashMap<>();
                }

                if (!TextUtils.isEmpty(clickId)) {
                    extendParameters.put(CLICK_ID, clickId);
                }

                if (!TextUtils.isEmpty(card.docSource)) {
                    String source = card.docSource;
                    try {
                        source = URLEncoder.encode(card.docSource);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    extendParameters.put(DOC_SOURCE, source);
                }
                reportEvent(card, EVENT_CLICK, -1, null, 0, UUID.randomUUID().toString(), extendParameters, needRetry);
            }
            if (reportExposal) {
                if (card.clickMonitorUrls != null && card.clickMonitorUrls.length > 0) {
                    AdMonitorHelper.reportThirdPartyEvents(card.clickMonitorUrls, String.valueOf(card.getAid()), false);
                }
            }
        }
    }


    public static JSONObject buildAdEntity(AdvertisementCard card, String appEvent, String uuid, final String url) {
        JSONObject mRequestObj = new JSONObject();
        try {
            mRequestObj.put("platform", 1);
            mRequestObj.put("appid", NewsFeedsSDK.getInstance().getAppId());
            mRequestObj.put("aid", card.getAid());
            if (isVideoEvent(appEvent)) {
                mRequestObj.put("duration", card.videoDuration);
                mRequestObj.put("playtime", card.currentPosition);
                setVideoMonitorUrls(mRequestObj, card, appEvent);
            } else if (EVENT_VIDEO_LOADING.equals(appEvent)) {
                mRequestObj.put("loading", card.loadingTime);
            }
            if (EVENT_PICTURE_GALLERY_VIEW.equals(appEvent)) {
                mRequestObj.put("num", card.viewedNum);
                mRequestObj.put("allnum", card.allNum);
            }
            mRequestObj.put("event", appEvent);
            mRequestObj.put("template", card.getTemplate());
            mRequestObj.put("adsfrom", card.getAdsFrom());
            if (!TextUtils.isEmpty(uuid)) {
                mRequestObj.put("uuid", uuid);
            }
            mRequestObj.put("region", LocationMgr.getInstance().getRegion());
            mRequestObj.put("ex", card.getEx());
            mRequestObj.put("net", NetUtil.getNetTypeString(ContextUtils.getApplicationContext()));
            mRequestObj.put("imei", SystemUtil.getMd5RealImei());
            mRequestObj.put("imei1", SystemUtil.getMd5Imei());
            mRequestObj.put("mac", DeviceUtils.getMac(ContextUtils.getApplicationContext()));
            mRequestObj.put("pos", card.getPosition());
            mRequestObj.put("pn", card.getPackageName());
            //下载类广告
            if (card.getExpireTime() > 0) {
                mRequestObj.put("expireTime", card.getExpireTime());
            }
            if (EVENT_VIEW.equals(appEvent) || EVENT_VIEW_NS.equals(appEvent)) {
                if (card.viewMonitorUrls != null && card.viewMonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String viewMonitorUrl : card.viewMonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(viewMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("viewmonitor_urls", array);
                }
            } else if (EVENT_CLICK.equals(appEvent) || EVENT_RESERVE.equals(appEvent) || EVENT_VIDEO_CLICK.equals(appEvent)) {
                if (card.clickMonitorUrls != null && card.clickMonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String clickMonitorUrl : card.clickMonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(clickMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("clickmonitor_urls", array);
                }

                if (!TextUtils.isEmpty(card.getDeeplinkUrl())) {
                    try {
                        mRequestObj.put(DEEPLINK_URL, URLEncoder.encode(card.getDeeplinkUrl(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!TextUtils.isEmpty(url)) {
                mRequestObj.put("url", url);
            }
            mRequestObj.put("version", SDKContants.API_VER);
            mRequestObj.put("3rd_ad_version", AdUtils.getAdVersion());
            mRequestObj.put("cv", SDKContants.SDK_VER);
            mRequestObj.put("action", ADConstants.AD_ACTION);
            mRequestObj.put("ip", IpUtils.getIP());
            mRequestObj.put("userid", GlobalAccount.getYduserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mRequestObj;
    }

    /**
     * 判断广告是否支持下载
     *
     * @param card
     * @return
     */
//    public static boolean supportDownload(AdvertisementCard card) {
//        return card != null && (ListViewItemData.DISPLAY_CARD.supportDownload(card.getTemplate())/**列表**/
//                || supportContentInstall(card)) /**正文**/ || VideoInsertAdType.isSupportDownload(card.getTemplate())/**视频后贴**/;
//    }
    private static void setVideoMonitorUrls(JSONObject mRequestObj, AdvertisementCard card, String appEvent) {
        try {
            if (EVENT_VIDEO_START.equals(appEvent)) {
                if (card.playMonitorUrls != null && card.playMonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String viewMonitorUrl : card.playMonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(viewMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("playMonitorUrls", array);
                }
            } else if (EVENT_VIDEO_FINISH.equals(appEvent)) {
                if (card.finishMonitorUrls != null && card.finishMonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String viewMonitorUrl : card.finishMonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(viewMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("finishMonitorUrls", array);
                }
            } else if (EVENT_VIDEO_FIRST_QUARTILE.equals(appEvent)) {
                if (card.firstMonitorUrls != null && card.firstMonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String viewMonitorUrl : card.firstMonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(viewMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("firstMonitorUrls", array);
                }
            } else if (EVENT_VIDEO_MIDPOINT.equals(appEvent)) {
                if (card.midMonitorUrls != null && card.midMonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String viewMonitorUrl : card.midMonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(viewMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("midMonitorUrls", array);
                }
            } else if (EVENT_VIDEO_THIRD_QUARTILE.equals(appEvent)) {
                if (card.thirdMonitorUrls != null && card.thirdMonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String viewMonitorUrl : card.thirdMonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(viewMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("thirdMonitorUrls", array);
                }
            } else if (EVENT_VIDEO_S5.equals(appEvent)) {
                if (card.s5MonitorUrls != null && card.s5MonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String viewMonitorUrl : card.s5MonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(viewMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("s5MonitorUrls", array);
                }
            } else if (EVENT_VIDEO_S15.equals(appEvent)) {
                if (card.s15MonitorUrls != null && card.s15MonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String viewMonitorUrl : card.s15MonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(viewMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("s15MonitorUrls", array);
                }
            } else if (EVENT_VIDEO_S30.equals(appEvent)) {
                if (card.s30MonitorUrls != null && card.s30MonitorUrls.length > 0) {
                    JSONArray array = new JSONArray();
                    for (String viewMonitorUrl : card.s30MonitorUrls) {
                        array.put(AdMonitorHelper.getUrlWithMacro(viewMonitorUrl, String.valueOf(card.getAid()), true));
                    }
                    mRequestObj.put("s30MonitorUrls", array);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isVideoEvent(String appEvent) {
        for (String videoEvent : VIDEO_EVENTS) {
            if (videoEvent.equals(appEvent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Download file and report event.
     *
     * @param mCtx
     * @param card
     * @param title
     * @param fileName
     */
    public static void downloadFile(Context mCtx, AdvertisementCard card, String title, String fileName, @ADConstants.AppDownloadSource int downloadSource) {
        downloadFile(mCtx, card, title, fileName, true, downloadSource);
    }


    @TargetApi(11)
    public static void downloadFile(Context mCtx, final AdvertisementCard card, String title, String fileName,
                                    final boolean reportEvent, @ADConstants.AppDownloadSource final int downloadSource) {
        if (card == null || TextUtils.isEmpty(card.actionUrl)) {
            return;
        }
        String url = card.actionUrl;
        if (android.os.Build.VERSION.SDK_INT < 9) {
            //download manager added in sdk 9
            //launch browser to download
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            mCtx.startActivity(i);
            return;
        }
        downloadManager = (DownloadManager) mCtx.getSystemService(Context.DOWNLOAD_SERVICE);
        final DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(card.actionUrl));

        downloadRequest.setTitle(title);
        downloadRequest.setDescription(card.summary);
//        downloadRequest.setDestinationInExternalPublicDir("/download/", fileName);
        downloadRequest.setVisibleInDownloadsUi(true);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        try {
            downloadRequest.setDestinationInExternalFilesDir(mCtx, null, fileName);
            boolean needStartNewWork = false;
            //long downloadId = AdDbUtil.getAdCardDownloadId(card);
            downloadId = AdvertisementDbUtil.getAdvertisementCardDownloadId(card);
            getDownloadInitalState(downloadId, card);
            Log.d(ADConstants.ADVERTISEMENT_LOG,
                    "downloadFile - status : " + String.valueOf(card.getDownloadStatus()) + ", current progress: " + String.valueOf(card.getDownloadProgress()));
            if (card.getDownloadStatus() == DownloadEvent.STATUS_DEFAULT) {
                //没有开始下载
                needStartNewWork = true;
            } else if (card.getDownloadStatus() == DownloadEvent.STATUS_FAILED) {
                //上次下载尝试失败，且DownloadManager不会再尝试下载。需要重新开始
                needStartNewWork = true;
                AdvertisementDbUtil.deleteAdRecordByDownloadId(downloadId);
                //AdDbUtil.deleteAdRecordByDownloadId(downloadId);
            } else {
                if (card.getDownloadStatus() == DownloadEvent.STATUS_SUCCESSFUL && TextUtils.isEmpty(InstallPackageFileUtil.waitForInstall(card))) {
                    //虽然下载成功，但是用户已经把安装包文件删除，则依然需要重新下载。
                    needStartNewWork = true;
                    //AdDbUtil.deleteAdRecordByDownloadId(downloadId);
                    AdvertisementDbUtil.deleteAdRecordByDownloadId(downloadId);
                }
            }

            if (needStartNewWork) {
                if (NetUtil.isNetworkMetered(ContextUtils.getApplicationContext())) {//计费网络
                    SimpleDialog.SimpleListener listener = new SimpleDialog.SimpleListener() {
                        @Override
                        public void onBtnLeftClick(Dialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onBtnRightClick(Dialog dialog) {
                            dialog.dismiss();
                            enqueueDownloadRequest(downloadRequest, card, reportEvent, downloadSource);
                        }
                    };
                    showDownloadAutoTip(mCtx, listener);
                } else {
                    enqueueDownloadRequest(downloadRequest, card, reportEvent, downloadSource);
                }
            }
            registerDownloadProgressView(mCtx, downloadId, card, downloadManager);
        } catch (Exception e) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(card.actionUrl));
            mCtx.startActivity(i);

            if (reportEvent) {
                reportAppStartDownload(card, downloadSource);
                AdMonitorHelper.reportThirdPartyEvents(card.startDownloadMonitorUrls, String.valueOf(card.getAid()), false);
                //Browser无法跟纵，故不写DB.
                //AdvertisementUtil.createAdRecord(card, UploadXiaomiLogApi.EVENT_APP_START_DOWNLOAD, -1, false);
            }
        }
    }

    /**
     * 处于计费网络时，广告自动下载apk弹窗提示
     *
     * @param context
     * @param listener
     */
    public static void showDownloadAutoTip(Context context, SimpleDialog.SimpleListener listener) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            SimpleDialog.SimpleDialogBuilder builder = new SimpleDialog.SimpleDialogBuilder();
            builder.setMessage(context.getString(R.string.ydsdk_ad_tip_download_auto))
                    .setLeftBtnStr(context.getString(R.string.ydsdk_ad_cancel))
                    .setRightBtnStr(context.getString(R.string.ydsdk_ad_ok))
                    .setSimpleListener(listener)
                    .createDialog(context)
                    .show();
        }
    }

    private static void enqueueDownloadRequest(DownloadManager.Request downloadRequest, AdvertisementCard card, boolean reportEvent, int downloadSource) {
        downloadId = downloadManager.enqueue(downloadRequest);
        CustomizedToastUtil.showPrompt(R.string.ydsdk_ad_begin_download, false);

        if (reportEvent) {
            AdvertisementUtil.reportAppStartDownload(card, downloadSource);
            AdMonitorHelper.reportThirdPartyEvents(card.startDownloadMonitorUrls, String.valueOf(card.getAid()), false);
        }
        AdvertisementDbUtil.createAdRecord(card, EVENT_APP_START_DOWNLOAD, downloadId, reportEvent);
    }

    public static void registerDownloadProgressView(final Context ctx, long downloadId, final AdvertisementCard advertisementCard, DownloadManager downloadManager) {
        mDownloadProgressMap.put(advertisementCard, downloadId);

        if (mProgressMonitor != null) {
            mProgressMonitor.updateQuery();
        } else {
            mProgressMonitor = new DownloadProgressMonitor(ctx, downloadManager);
            Thread monitorThread = new Thread(mProgressMonitor, "downloadMonitorThread");
            monitorThread.start();
        }
    }

    private static void getDownloadInitalState(long downloadId, AdvertisementCard card) {
        card.setDownloadStatus(DownloadEvent.STATUS_DEFAULT);
        card.setDownloadProgress(-1);
        if (downloadId == 0) {
            return;
        }
        DownloadManager downloadManager = (DownloadManager) AdvertisementModule.getInstance().getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(q);
            if (cursor != null && cursor.moveToFirst()) {
                int progress = 0;
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                if (bytes_total > 0) {
                    progress = (int) (cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)) * 100.f / bytes_total);
                }
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                card.setDownloadStatus(status);
                card.setDownloadProgress(progress);
                return;
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static void reportAppStartDownload(AdvertisementCard card, int downloadSource) {
        if (ADConstants.APP_DOWNLOAD_SOURCE_UNKNOWN == downloadSource) {
            return;
        }

        Map<String, String> extendParameters = new HashMap<>();
        extendParameters.put(APP_DOWNLOAD_SOURCE, String.valueOf(downloadSource));
        reportEvent(card, EVENT_APP_START_DOWNLOAD, -1, null, 0, UUID.randomUUID().toString(), extendParameters, true);
    }


    /**
     * 判断广告是否支持下载
     *
     * @param card
     * @return
     */
    public static boolean supportDownload(AdvertisementCard card) {
        return card != null && (ListViewItemData.DISPLAY_CARD.supportDownload(card.getTemplate()));
    }

    private static class DownloadProgressMonitor implements Runnable {
        private static final int QUERY_DURATION = 2000;//2s
        private ContentObserver mDownloadObserver;
        private Cursor mDownloadCursor;
        private long mLastCheckDuration = -1;
        private volatile boolean isChecking = false;
        private volatile boolean mHasScheduledUpdate = false;

        private DownloadManager mDownloadManager;
        private Context mContext;

        public DownloadProgressMonitor(Context context, DownloadManager downloadManager) {
            mContext = context;
            mDownloadManager = downloadManager;
        }

        private void _checkProgress() {
            isChecking = true;
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            Set<Map.Entry<AdvertisementCard, Long>> entrySet = mDownloadProgressMap.entrySet();

            Iterator<Map.Entry<AdvertisementCard, Long>> it = entrySet.iterator();
            while (it.hasNext()) {
                Map.Entry<AdvertisementCard, Long> entry = it.next();
                long id = entry.getValue();
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(id);
                AdvertisementCard card = entry.getKey();
                Cursor cursor = null;
                try {
                    cursor = downloadManager.query(q);

                    if (cursor != null && cursor.moveToFirst()) {
                        int progress = 0;
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        if (bytes_total > 0) {
                            progress = (int) (cursor.getInt(cursor
                                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)) * 100.f / bytes_total);
                        }
                        int status = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_STATUS));

                        card.setDownloadStatus(status);
                        card.setDownloadProgress(progress);

                        if (status == DownloadEvent.STATUS_SUCCESSFUL) {
                            it.remove();
                        }
                        BroadcastBus.getDefault().post(new DownloadEvent(card.getAid(), status, progress));
                    }

                } catch (Exception e) {

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }

            isChecking = false;
            mHasScheduledUpdate = false;
            mLastCheckDuration = System.currentTimeMillis();
        }

        public void checkProgress() {
            long currentTime = System.currentTimeMillis();
            if (mLastCheckDuration != -1 && (currentTime - mLastCheckDuration) < QUERY_DURATION) {
                if (isChecking) {
                    return;
                } else if (!mHasScheduledUpdate) {
                    mHasScheduledUpdate = true;
                    ThreadUtils.postDelayed2UI(new Runnable() {
                        @Override
                        public void run() {
                            if (!isChecking) {
                                _checkProgress();
                            }
                        }
                    }, QUERY_DURATION);
                }
            } else {

                ThreadUtils.post2UI(new Runnable() {
                    @Override
                    public void run() {
                        _checkProgress();
                    }
                });

            }
        }

        public void updateQuery() {
            if (mDownloadObserver != null) {
                mDownloadCursor.unregisterContentObserver(mDownloadObserver);
            }

            mDownloadObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    checkProgress();
                }
            };

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadEvent.STATUS_RUNNING | DownloadEvent.STATUS_PENDING | DownloadEvent.STATUS_PAUSED);
            Collection<Long> downloadIdSet = mDownloadProgressMap.values();
            long[] downloadIds = new long[downloadIdSet.size()];

            int i = 0;
            for (Long aLong : downloadIdSet) {
                downloadIds[i++] = aLong;
            }

            query.setFilterById(downloadIds);

            try {
                if (mDownloadCursor != null) {
                    mDownloadCursor.close();
                }
                mDownloadCursor = mDownloadManager.query(query);

            } catch (Exception e) {
                if (mDownloadCursor != null) {
                    mDownloadCursor.close();
                }
                mDownloadCursor = null;
            }

            if (mDownloadCursor != null) {
                mDownloadCursor.registerContentObserver(mDownloadObserver);
            }


        }

        @Override
        public void run() {
            Looper.prepare();
            updateQuery();
            Looper.loop();
        }

        public void stop() {
            Looper.myLooper().quit();
        }
    }

    /**
     * Special case:
     * http://cdn3.ops.baidu.com/new-repackonline/baidunuomi/AndroidPhone/5.13.2.0/1/1006903d/20150929122920/baidunuomi_AndroidPhone_5-13-2-0_1006903d.apk?response-content-disposition=attachment;filename=baidunuomi_AndroidPhone_1006903d.apk&response-content-type=application/vnd.android.package-archive&request_id=1444274246_3409227401&type=static
     */
    public static String getUrlFileName(String url) {
        int index = url.lastIndexOf('/');
        int dotIndex = url.lastIndexOf(".apk");
        if (dotIndex == -1) {
            dotIndex = url.length();
        }

        if (index + 1 > dotIndex) {
            return url;
        }

        String filename = url.substring(index + 1, dotIndex);
        try {
            //文件名可能被encode多次，这里进行修正, 最多做4次
            for (int count = 0; count < 4; count++) {
                String str = URLDecoder.decode(filename, "UTF-8");
                if (filename.equals(str)) {
                    break;
                } else {
                    filename = str;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return filename;
    }

}
