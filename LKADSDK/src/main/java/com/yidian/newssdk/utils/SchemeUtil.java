package com.yidian.newssdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by lishuxiang on 2017/8/23.
 */

public class SchemeUtil {
    private static final String TAG = SchemeUtil.class.getSimpleName();

    /**
     * 合作电商的scheme和app名称
     * 由版本4.5.0.0开始，作用仅限于对应app名称，该列表可能滞后于api，若有需要需在发版前更新
     */
    enum ECommerceScheme {
        TAOBAO("taobao", "淘宝"),
        TAOBAO2("tbopen", "淘宝"),
        DIANPING("dianping", "大众点评"),
        SUNING("suning", "苏宁"),
        JD("openapp.jdmobile", "京东"),
        YHD("yhd", "1号店"),
        VIP("vipshop", "唯品会"),
        JDFINANCE("jdmobile", "京东金融"),
        MEITUAN("imeituan", "美团"),
        CTRIP("ctrip", "携程"),
        QUNA("qunaraphone", "去哪儿"),
        YOHO("yohobuy", "有货"),
        BEIBEI("beibeiapp", "贝贝网"),
        KAOLA("kaola", "网易考拉"),
        XZDZ("openapp.xzdz", "小猪短租");

        private String app_scheme;
        private String app_name;

        ECommerceScheme(String app_scheme, String app_name) {
            this.app_scheme = app_scheme;
            this.app_name = app_name;
        }
    }

    /**
     * 白名单中的scheme统一是小写的，但是实际的scheme可能是大小写混合的，所以判断时要忽略大小写
     * 由版本4.5.0.0开始，该名单由api维护，客户端备份
     * @param url 根据url确定是否是合作电商的scheme
     * @return 是否是合作的电商
     */
//    public static boolean isECommerceScheme(String url) {
//        String scheme = Uri.parse(url).getScheme();
//        scheme = scheme == null ? null : scheme.toLowerCase();
//        boolean inBackUp = false;//本地备份，防止清数据或api返回异常
//        for (ECommerceScheme commerceScheme: ECommerceScheme.values()) {
//            if (TextUtils.equals(commerceScheme.app_scheme, scheme)) {
//                inBackUp = true;
//                break;
//            }
//        }
//        Set<String> h5SchemeList = GlobalConfig.getInstance().getH5SchemeWhiteList();
//        return h5SchemeList != null && h5SchemeList.contains(scheme) || inBackUp;
//    }

    /**
     * 白名单中的scheme统一是小写的，但是实际的scheme可能是大小写混合的，所以判断时要忽略大小写
     * 因由版本4.5.0.0开始，白名单由api维护，客户端备份，故名称对应若有需要请手动添加ECommerceScheme
     * @param scheme
     * @return 根据scheme确定app名称
     */
    public static String getAppName(String scheme) {
        for (ECommerceScheme eCommerceScheme : ECommerceScheme.values()) {
            if (eCommerceScheme.app_scheme.equalsIgnoreCase(scheme)) {
                return eCommerceScheme.app_name;
            }
        }
        return "";
    }


    /**
     * 用scheme判定App是否安装，不能用http和https的scheme作检查，否则恒返回true
     *
     * @param url
     * @return
     */
    public static boolean hasAppInstalled(@NonNull String url) {
        if (!TextUtils.equals("http", Uri.parse(url).getScheme()) && !TextUtils.equals("https", Uri.parse(url).getScheme())) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            final PackageManager mgr = ContextUtils.getApplicationContext().getPackageManager();
            List<ResolveInfo> list =
                    mgr.queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } else {
            return false;
        }
    }

    /**
     * 打开第三方app
     *
     * @param context
     * @param url
     * @return
     */
    public static boolean openAppWithUrl(Context context, String url) {
        if (!TextUtils.isEmpty(url) && hasAppInstalled(url)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 调起手机电话拨打界面
     *
     * @param context
     * @param url
     * @return
     */
    public static boolean openDialWithUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            if (intent.resolveActivity(ContextUtils.getApplicationContext().getPackageManager()) != null) {
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 支持调起手机拨号界面
     * @param url
     * @return
     */
    public static boolean isDialScheme(String url) {
        if (!TextUtils.isEmpty(url) && TextUtils.equals("tel", Uri.parse(url).getScheme())) {
            return true;
        }
        return false;
    }

    /**
     * 一台是手机上可能装有我们多个版本的应用，为了返回时"从哪来回哪去"
     * 合作方应用给"一点资讯"加的返回按钮上的链接，需要拼接我们app的scheme信息，用于区分是我们哪个app调起的
     *
     * @param url
     * @return
     */
    public static String appendReturnScheme(String url, String applicationId) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        StringBuilder returnUrl = new StringBuilder(url);
        String appScheme = "ydnormal";
        if (applicationId.contains("xiaomi")) {
            appScheme = "ydxiaomi";
        } else if (applicationId.contains("dress")) {
            appScheme = "yddress";
        } else if (applicationId.contains("slim")) {
            appScheme = "ydslim";
        } else if (applicationId.contains("food")) {
            appScheme = "ydfood";
        }
        try {
            if (TextUtils.equals("zhihu", Uri.parse(url).getScheme())) {
                if (!url.contains("?")) {
                    returnUrl.append("?");
                }
                if (!returnUrl.toString().endsWith("?")) {
                    returnUrl.append("&");
                }
                returnUrl.append("back_url=");//该字段是知乎定义的，用于提取我们拼接的返回按钮的链接

                String encodeScheme = appScheme + "://deeplink?return=true&src=zhihu";
                returnUrl.append(URLEncoder.encode(encodeScheme, "utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            returnUrl = new StringBuilder(url);
        }

        return returnUrl.toString();
    }

    public static boolean isZhiHuScheme(String url) {
        boolean isZhiHu = false;
        if (!TextUtils.isEmpty(url)) {
            isZhiHu = TextUtils.equals("zhihu", Uri.parse(url).getScheme());
        }
        return isZhiHu;
    }

    public static boolean isDianPingScheme(String url) {
        boolean isZhiHu = false;
        if (!TextUtils.isEmpty(url)) {
            isZhiHu = TextUtils.equals("dianping", Uri.parse(url).getScheme());
        }
        return isZhiHu;
    }
}
