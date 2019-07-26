package com.linken.newssdk.core.ad;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.SDKContants;
import com.linken.newssdk.protocol.newNetwork.business.request.imp.RequestThirdAD;
import com.linken.newssdk.protocol.newNetwork.core.AsyncHttpClient;
import com.linken.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.DeviceUtils;
import com.linken.newssdk.utils.EncryptUtil;
import com.linken.newssdk.utils.IpUtils;
import com.linken.newssdk.utils.SystemUtil;
import com.linken.newssdk.widget.feedback.ad.IAdRelatedStatusListener;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by patrickleong on 7/2/15.
 */
public class AdMonitorHelper {
    public static final String APP_NAME = "yidian";
    public static final String MIAOZHEN_COM = "miaozhen.com";
    public static final String ADMASTER_COM = "admaster.com.cn";
    public static final String FW4ME_COM = "fw4.me";//转化大师
    public static final String MARCO_PREFIX = "macro";
    public static boolean logEnabled = false;

    private static volatile String mAdMasterStr = null;
    private static volatile String mMiaoZhenStr = null;

    interface ReplaceMacro {
        String OS = "__OS__";
        String IP = "__IP__";
        String IMEI = "__IMEI__";
        String MAC = "__MAC__";
        String IDFA = "__IDFA__";
        String ANDROIDID = "__ANDROIDID__";
        String AndroidID = "__AndroidID__";
        String TS = "__TS__";
        String TERM = "__TERM__";
        String APP = "__APP__";
        String IESID = "__IESID__";
    }

    public static String getAdMasterReportString(@NonNull String url) {
        //如果监控地址最后带落地页地址，需要截出来，放到最后。admaster是,u
        String landingSuffix = null;
        int idx = url.lastIndexOf(",uhttp");
        if (idx != -1) {
            landingSuffix = url.substring(idx);
            url = url.substring(0, idx);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(url);
        //这部分增加的是静态的部分
        if (mAdMasterStr == null) {
            synchronized (AdMonitorHelper.class) {
                if (mAdMasterStr == null) {
                    StringBuilder strBuilder = new StringBuilder();
                    strBuilder.append(",0a0");

                    String md5Imei = EncryptUtil.getMD5_32(SystemUtil.getIMEI());
                    strBuilder.append(",0c");
                    strBuilder.append(md5Imei);

                    String mac = getAdMasterMac();
                    strBuilder.append(",n");
                    strBuilder.append(mac);

                    String deviceMd5 = EncryptUtil.getMD5_32(SystemUtil.getAndroidId());
                    strBuilder.append(",0d");
                    strBuilder.append(deviceMd5);

                    strBuilder.append(",r");
                    try {
                        strBuilder.append(URLEncoder.encode(Build.MODEL, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    mAdMasterStr = strBuilder.toString();
                }
            }
        }
        builder.append(mAdMasterStr);

        //因为IP，TS是非静态，所以这部分不能放入上面
        String ip = "";
        if (!TextUtils.isEmpty(ip)) {
            builder.append(",f");
            builder.append(ip);
        }

        builder.append(",t");
        builder.append(System.currentTimeMillis());

        if (landingSuffix != null) {
            builder.append(landingSuffix);
        }

        return builder.toString();
    }

    private static String getAdMasterMac() {
        String mac;
//        if (Build.VERSION.SDK_INT <= 8) {
        mac = DeviceUtils.getMac(ContextUtils.getApplicationContext());
//        } else {
//            mac = getMACAddress("eth0");
//        }
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.replace(":", "");
        }
        return mac;
    }

    private static String getIpAddress(Application application) {
        WifiManager wm = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public static String getMiaoZhenReportString(@NonNull String url) {
        //如果监控地址最后带落地页地址，需要截出来，放到最后。秒针是&o=
        String landingSuffix = null;
        int idx = url.lastIndexOf("&o=");
        if (idx != -1) {
            landingSuffix = url.substring(idx);
            url = url.substring(0, idx);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(url);
        if (mMiaoZhenStr == null) {
            synchronized (AdMonitorHelper.class) {
                if (mMiaoZhenStr == null) {
                    StringBuilder tmpBuilder = new StringBuilder();
                    tmpBuilder.append("&mo=0");


                    String deviceMd5 = EncryptUtil.getMD5_32(SystemUtil.getAndroidId());
                    tmpBuilder.append("&m1a=");
                    tmpBuilder.append(deviceMd5);

                    String md5Imei = EncryptUtil.getMD5_32(SystemUtil.getIMEI());
                    tmpBuilder.append("&m2=");
                    tmpBuilder.append(md5Imei);

                    tmpBuilder.append("&nn=");
                    tmpBuilder.append(APP_NAME);

                    String mac = getAdMasterMac();
                    tmpBuilder.append("&m6a=");
                    tmpBuilder.append(mac);

                    mMiaoZhenStr = tmpBuilder.toString();
                }
            }
        }
        builder.append(mMiaoZhenStr);
        String ip = "";
        if (!TextUtils.isEmpty(ip)) {
            builder.append("&ns=");
            builder.append(ip);
        }
        if (landingSuffix != null) {
            builder.append(landingSuffix);
        }

        return builder.toString();
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    private static String addThirdPartyParameters(String url, boolean isExposal) {
        if (logEnabled) {
            String msg;
            if (isExposal) {
                msg = "reportExposalEvents";
            } else {
                msg = "reportClickEvents";
            }
        }

        String reportUrl;
        if (url.contains(MIAOZHEN_COM)) {
            reportUrl = getMiaoZhenReportString(url);
        } else if (url.contains(ADMASTER_COM) || url.contains(FW4ME_COM)) {
            reportUrl = getAdMasterReportString(url);
        } else {
            reportUrl = url;
        }


        return reportUrl;

    }

    /**
     * _____AdMaster_____
     * Sample view url
     * http://v.admaster.com.cn/v/a17298,b03753846,c3194,i0,m201,h,0a__OS__,0c__IMEI__,0d__AndroidID__,0e__DUID__,n__MAC__,o__OUID__,z__IDFA_ _,f__IP__,t__TS__,r__TERM__,l__LBS__
     * <p/>
     * Sample click url
     * http://c.admaster.com.cn/c/a17298,b03753846,c3194,i0,m201,h,0a__OS__,0c__IMEI__,0d__AndroidID__,0e__DUID__,n__MAC__,o__OUID__,z__IDFA_ _,f__IP__,t__TS__,r__TERM__,l__LBS__
     * <p/>
     * <p/>
     * http://g.mbm.cn.miaozhen.com/x.gif?k=2060&p=8nj&ni=__IESID__&mo=__OS__&ns=__IP__&m0=__OPENUDID__&m0a=__DUID__&m1=__ANDROIDID1__&m1a=__ANDROIDID__&m2=__IMEI__&m4=__AAID__&m5=__IDFA__&m6=__MAC1__&m6a=__MAC__&nn=__APP__&rt=2&o=
     *
     * @param url 输入url
     * @return
     */
    public static String updateThirdPartyClickUrl(String url, String aid) {//负责落地页url的处理，和普通第三方monitor url的通用替换规则的处理
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        String returnUrl = url;
        if (hasMacroToReplace(url)) {//判断还有没有替换的标志
            returnUrl = generalParameterReplacement(url, aid);

        }
        //应用宏替换原则的url可能含有“macro”prefix
        return returnUrl.startsWith(MARCO_PREFIX) ? returnUrl.substring(MARCO_PREFIX.length()) : returnUrl;
    }

    public static boolean hasGeneralMacroToReplace(@NonNull String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.contains(ReplaceMacro.ANDROIDID) || url.contains(ReplaceMacro.AndroidID)
                || url.contains(ReplaceMacro.APP) || url.contains(ReplaceMacro.IMEI)
                || url.contains(ReplaceMacro.MAC) || url.contains(ReplaceMacro.OS)
                || url.contains(ReplaceMacro.TERM);
    }

    public static boolean hasMacroToReplace(@NonNull String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.contains(ReplaceMacro.IP) || url.contains(ReplaceMacro.TS)
                /*|| url.contains(ReplaceMacro.IESID)*/ || hasGeneralMacroToReplace(url);
    }

    private static String generalParameterReplacement(@NonNull String url, String aid) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        if (hasGeneralMacroToReplace(url)) {//AdvertisementCard解析时，没有替换static field成功，则需要重新替换static field部分
            url = replaceGeneralRuleStaticFields(url);
        }

        //替换非static filed，即在发送时间时才能确定的信息：
        //__TS__, __IP__
        String ip = IpUtils.getIP();
        if (!TextUtils.isEmpty(ip)) {
            url = url.replace(ReplaceMacro.IP, ip);
        }
        url = url.replace(ReplaceMacro.TS, String.valueOf(System.currentTimeMillis()));
//        url = getReplaceMacroOfIESID(url, aid);
        return url;
    }

    @Deprecated
    /**
     * 去掉客户端对IESID的替换规则，改为由adserver做替换
     */
    private static String getReplaceMacroOfIESID(@NonNull String url, String aid) {
        if (url.contains(MIAOZHEN_COM)) {
            url = url.replace(ReplaceMacro.IESID, "yidian_" + aid);
        } else if (url.contains(ADMASTER_COM)) {
            url = url.replace(ReplaceMacro.IESID, "PUB_" + aid);
        }
        return url;
    }

    public static boolean shouldReportView(@NonNull AdvertisementCard card) {
        String key = card.getTid() + "_" + card.getAid();
        Long previousShowTime = mViewReportMap.get(key);
        long currentTime = System.currentTimeMillis();
        if (previousShowTime == null || currentTime - previousShowTime > ONE_DAY_MS) {
            mViewReportMap.put(key, currentTime);
            return true;
        } else {
            return false;
        }
    }

    private static Map<String, Long> mViewReportMap = new HashMap<>(); //记录信息流和正文页展示过的广告，24小时或应用杀掉后只报告一次VIEW事件
    public static final int ONE_DAY_MS = 24 * 3600 * 1000;

    public static String replaceGeneralRuleStaticFields(String url) {
        //替换static filed，即在解析时就可以确定的信息：
        //__OS__, __IMEI__, __MAC__, __ANDROIDID__, __AndroidID__, __TERM__, __APP__
        //__IDFA__对android端来说没有意义，跟PM商定后就不管了

        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("__\\w+__");
        Matcher matcher = pattern.matcher(url);
        String model = "";
        try {
            model = URLEncoder.encode(Build.MODEL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }

        while (matcher.find()) {
            String str = matcher.group();
            if (ReplaceMacro.OS.equals(str)) {
                matcher.appendReplacement(sb, "0");
            } else if (ReplaceMacro.IMEI.equals(str)) {
                matcher.appendReplacement(sb, EncryptUtil.getMD5_32(SystemUtil.getIMEI()));
            } else if (ReplaceMacro.ANDROIDID.equals(str) || ReplaceMacro.AndroidID.equals(str)) {
                matcher.appendReplacement(sb, EncryptUtil.getMD5_32(SystemUtil.getAndroidId()));
            } else if (ReplaceMacro.MAC.equals(str)) {
                String mac = getAdMasterMac();
                if (!TextUtils.isEmpty(mac)) {
                    matcher.appendReplacement(sb, mac);
                }
            } else if (ReplaceMacro.APP.equals(str)) {
                matcher.appendReplacement(sb, APP_NAME);
            } else if (ReplaceMacro.TERM.equals(str)) {
                if (!TextUtils.isEmpty(model)) {
                    matcher.appendReplacement(sb, model);
                }
            }
        }

        matcher.appendTail(sb);

        return sb.toString();
    }

    public static void reportThirdPartyEvents(String[] urls, String aid, boolean isExposal) {
        if (urls == null) return;

        for (int i = 0; i < urls.length; i++) {
            final String macroUrl = getUrlWithMacro(urls[i], aid, isExposal);
            RequestThirdAD originGet = new RequestThirdAD(macroUrl);
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setUserAgent(System.getProperty("http.agent") + SDKContants.USER_AGENT);
            asyncHttpClient.get(originGet, new JsonObjectResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    IAdRelatedStatusListener listener = AdvertisementModule.getInstance().getAdRelatedStateListener();
                    if(listener != null) {
                        listener.reportThirdPartyEvent(macroUrl, true);
                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    IAdRelatedStatusListener listener = AdvertisementModule.getInstance().getAdRelatedStateListener();
                    if(listener != null) {
                        listener.reportThirdPartyEvent(macroUrl, false);
                    }
                }
            });
        }
    }

    public static String getUrlWithMacro(@NonNull String url, String aid, boolean isExposal) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        String result;
        if (url.startsWith(MARCO_PREFIX)) {
            result = updateThirdPartyClickUrl(url, aid);
        } else {
            result = url;
        }
        return result;
    }
}
