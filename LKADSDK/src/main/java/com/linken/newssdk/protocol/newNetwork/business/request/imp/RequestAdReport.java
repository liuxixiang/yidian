package com.linken.newssdk.protocol.newNetwork.business.request.imp;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.NewsFeedsSDK;
import com.linken.newssdk.core.ad.AdvertisementUtil;
import com.linken.newssdk.data.ad.AdReportConstants;
import com.linken.newssdk.data.pref.GlobalAccount;
import com.linken.newssdk.protocol.newNetwork.business.request.RequestBase;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.DeviceUtils;
import com.linken.newssdk.utils.IpUtils;
import com.linken.newssdk.utils.LocationMgr;
import com.linken.newssdk.utils.NetUtil;
import com.linken.newssdk.utils.SystemUtil;
import com.linken.newssdk.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.linken.newssdk.data.ad.AdReportConstants.APP_DOWNLOAD_SOURCE;
import static com.linken.newssdk.data.ad.AdReportConstants.BACK_SOURCE;
import static com.linken.newssdk.data.ad.AdReportConstants.DISLIKE_REASONS;
import static com.linken.newssdk.data.ad.AdReportConstants.DISLIKE_REASONS_CODE;
import static com.linken.newssdk.data.ad.AdReportConstants.EVENT_APP_START_DOWNLOAD;
import static com.linken.newssdk.data.ad.AdReportConstants.EVENT_BACK;
import static com.linken.newssdk.data.ad.AdReportConstants.EVENT_DISLIKE;
import static com.linken.newssdk.data.ad.AdReportConstants.EVENT_LANDING_PAGE;
import static com.linken.newssdk.data.ad.AdReportConstants.EVENT_SHARE;
import static com.linken.newssdk.data.ad.AdReportConstants.SHARE_SOURCE;

/**
 * Created by chenyichang on 2018/5/22.
 */

public class RequestAdReport extends RequestBase {


    private JSONObject mRequestObj;

    public RequestAdReport() {
    }

    @Override
    protected String getPath() {
        return "ads_log";
    }

    @Override
    public String getURI() {
        StringBuilder builder = new StringBuilder();
        builder.append(getHost());
        builder.append(getPath());
        builder.append("?appid=" + NewsFeedsSDK.getInstance().getAppId());
        long timestamp = TimeUtil.convertToServerTimeMillis(System.currentTimeMillis()) / 1000;
        builder.append("&timestamp=" + timestamp);
        builder.append("&nonce=nTKhmm9ON");
        builder.append("&secretkey=" + getSecretkey(timestamp, "nTKhmm9ON"));
//        builder.append("&log=json");
        builder.append("&yd_userid=" + GlobalAccount.getYduserId());
        return builder.toString();
    }

    @Override
    public String getBody() {
        if (mRequestObj != null) {
            JSONObject log = new JSONObject();
            try {
                log.put("log", mRequestObj);
                String requestString = log.toString();
                return requestString;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void setLandingPageEvent(AdvertisementCard card, long cid, String url, String uuid) {
        setAdInputs(card, EVENT_LANDING_PAGE, uuid);
        try {
            mRequestObj.put("cid", cid);
            mRequestObj.put("url", URLEncoder.encode(url, "UTF-8"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setDislikeEvent(AdvertisementCard card, String uuid, String reasons, String reasonsCode) {
        setAdInputs(card, EVENT_DISLIKE, uuid);
        try {
            mRequestObj.put(DISLIKE_REASONS, reasons);
            mRequestObj.put(DISLIKE_REASONS_CODE, reasonsCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setShareEvent(AdvertisementCard card, long cid, String url, String uuid, int share_source) {
        setAdInputs(card, EVENT_SHARE, uuid);
        try {
            mRequestObj.put("cid", cid);
            mRequestObj.put("url", URLEncoder.encode(url, "UTF-8"));
            mRequestObj.put(SHARE_SOURCE, share_source);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setBackEvent(@NonNull String back_source) {
        mRequestObj = new JSONObject();
        try {
            mRequestObj.put("net", NetUtil.getNetTypeString(ContextUtils.getApplicationContext()));
            mRequestObj.put("imei", SystemUtil.getMd5RealImei());
            mRequestObj.put("mac", DeviceUtils.getMac(ContextUtils.getApplicationContext()));
            mRequestObj.put("event", EVENT_BACK);
            String cityName = "";//LocationUtils.getCityName();
            mRequestObj.put("region", cityName);
            mRequestObj.put("cityCode", LocationMgr.getCityCode());
            String ip = IpUtils.getIP();
            if (!TextUtils.isEmpty(ip)) {
                mRequestObj.put("ip", ip);
            }
            mRequestObj.put(BACK_SOURCE, back_source);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAppStartDownload(AdvertisementCard card, int downloadSource, String uuid) {
        setAdInputs(card, EVENT_APP_START_DOWNLOAD, uuid);
        try {
            mRequestObj.put(APP_DOWNLOAD_SOURCE, downloadSource);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addCustomizedParameters(@AdReportConstants.CUSTOMIZED_PARAMETERS String paramName, String paramValues) {
        try {
            mRequestObj.put(paramName, paramValues);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setAdInputs(AdvertisementCard card, String appEvent, String uuid) {
        mRequestObj = AdvertisementUtil.buildAdEntity(card, appEvent, uuid, "");
    }

}
