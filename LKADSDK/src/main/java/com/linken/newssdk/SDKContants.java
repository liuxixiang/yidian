package com.linken.newssdk;

import com.linken.newssdk.protocol.newNetwork.business.helper.OpenPlatformHelper;

/**
 * Created by chenyichang on 2018/5/19.
 */

public class SDKContants {

    public static final String SDK_VER = BuildConfig.VERSION_NAME;
    public static final String API_VER = "010000";

    public static final String USER_AGENT = " YidianZixun";
    public static String channel;

    public static final String LK_APPID = "qblkadsdk";

    public static final String URL_HOST2 = "http://o.go2yd.com/open-api/op285/";
    public static String URL_HOST = OpenPlatformHelper.getOpenPath() + "/";

    public static final String URL_GET_OP = BuildConfig.OPEN_ROOT_PATH;//"http://open_test.go2yd.com/open-api/open-platform/";
    public static final String API_SERVER = "http://a3.go2yd.com/Website/";
    public static final String HIPU_IMAGE_SERVER = "http://i3.go2yd.com/image.php?";
    public final static String STATIC_IMAGE_SERVER = "http://s.go2yd.com/c/"; //一点的静态图片服务器

    public static final String URL_3RD_INFO = "http://api-small-3rd.go2yd.com/Website/user/get3rd-info";
    public static final String PATH_CONFIG = "/config/common/getConfigs";//云控



}
