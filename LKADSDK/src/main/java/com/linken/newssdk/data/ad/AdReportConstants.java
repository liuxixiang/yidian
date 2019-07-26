package com.linken.newssdk.data.ad;

import android.support.annotation.StringDef;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class AdReportConstants {

    public static final String EVENT_VIEW = "view";
    public static final String EVENT_VIEW_NS = "view_ns";
    public static final String EVENT_VIEW_ORIGINAL = "view_original";
    public static final String EVENT_CLICK = "click";
    public static final String EVENT_APP_START_DOWNLOAD = "app_start_download";
    public static final String EVENT_APP_DOWNLOAD_SUCCESS = "app_download_success";
    public static final String EVENT_APP_DOWNLOAD_CANCEL = "app_download_cancel";
    public static final String EVENT_APP_START_INSTALL = "app_start_install";
    public static final String EVENT_APP_INSTALL_SUCCESS = "app_install_success";
    public static final String EVENT_DOWNLOAD_FAIL = "app_download_fail";
    public static final String EVENT_LANDING_PAGE = "landing_page";
    public static final String EVENT_LANDING_PAGE_STAY = "stay";
    public static final String EVENT_DISLIKE = "dislike";
    public static final String EVENT_RESERVE = "reserve";
    public static final String EVENT_CLOSE = "close";
    public static final String EVENT_APP_LAUNCH_START = "app_launch_start";
    public static final String EVENT_APP_LAUNCH_SUCCESS = "app_launch_success";
    public static final String EVENT_APP_LAUNCH_FAIL = "app_launch_fail";
    public static final String EVENT_SPLASH_FAIL = "fail";
    public static final String EVENT_SPLASH_SKIP = "skip";
    public static final String EVENT_SHARE = "share";
    public static final String EVENT_BACK = "back";
    public static final String EVENT_SPLASH_TIME = "splashtime";

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

    public static final String EVENT_PICTURE_GALLERY_VIEW = "viewnum";

    @StringDef({EVENT_VIDEO_CLICK, EVENT_VIDEO_FAIL, EVENT_VIDEO_START, EVENT_VIDEO_PAUSE, EVENT_VIDEO_FINISH, EVENT_VIDEO_END,
            EVENT_CLICK, EVENT_VIDEO_RESUME, EVENT_VIDEO_FORWARD, EVENT_VIDEO_FIRST_QUARTILE,
            EVENT_VIDEO_MIDPOINT, EVENT_VIDEO_THIRD_QUARTILE, EVENT_VIDEO_S5, EVENT_VIDEO_S15, EVENT_VIDEO_S30})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoEvent {
    }

    @StringDef({CLICK_ID, CLIENT_NAME, CLIENT_PHONE, ACTION_SRC, DOC_SOURCE, SPLASH_FAIL_REASON, SPLASH_TYPE, STAY_TIME, OPEN_TYPE, SHARE_SOURCE, BACK_SOURCE, SPLASH_OPEN_TIME, SPLASH_REQUEST_TIME, SPLASH_DOWNLOAD_TIME,
            SPLASH_PRESENT_TIME, SPLASH_TOTAL_TIME, SPLASH_NOAD_TIME, SPLASH_IS_CACHE, SPLASH_IS_REALTIME, SPLASH_DOWNLOAD_STATUS, SPLASH_REQUEST_STATUS, OPEN_LINK_TYPE, DEEPLINK_URL, APP_DOWNLOAD_SOURCE,
            DISLIKE_REASONS, DISLIKE_REASONS_CODE, SKIP_TIME, WIDTH, HEIGHT, DOWN_X, DOWN_Y, UP_X, UP_Y})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CUSTOMIZED_PARAMETERS {

    }

    public static final String CLICK_ID = "clickid";
    public static final String CLIENT_NAME = "clientname";
    public static final String CLIENT_PHONE = "clientphone";
    public static final String ACTION_SRC = "audio_src";
    public static final String DOC_SOURCE = "doc_source";
    public static final String SPLASH_FAIL_REASON = "fail_reason";
    public static final String SPLASH_TYPE = "splash_type";
    public static final String STAY_TIME = "stay_time";
    public static final String OPEN_TYPE = "otype";//0：用deepLink打开，1：用packageName打开
    public static final String SHARE_SOURCE = "share_source";
    public static final String BACK_SOURCE = "back_source";
    public static final String SPLASH_OPEN_TIME = "splash_open_time";
    public static final String SPLASH_REQUEST_TIME = "splash_request_time";
    public static final String SPLASH_DOWNLOAD_TIME = "splash_download_time";
    public static final String SPLASH_PRESENT_TIME = "splash_present_time";
    public static final String SPLASH_TOTAL_TIME = "splash_total_time";
    public static final String SPLASH_IS_CACHE = "is_cache";
    public static final String SPLASH_IS_REALTIME = "is_realtime";
    public static final String SPLASH_REQUEST_STATUS = "request_status";
    public static final String SPLASH_DOWNLOAD_STATUS = "download_status";
    public static final String SPLASH_NOAD_TIME = "splash_noad_time";
    public static final String OPEN_LINK_TYPE = "open_link_type";//deeplink/h5_deeplink_fail／h5_no_deeplink
    public static final String DEEPLINK_URL = "deeplinkUrl";
    public static final String APP_DOWNLOAD_SOURCE = "app_download_source";
    public static final String DISLIKE_REASONS = "dr";
    public static final String DISLIKE_REASONS_CODE = "reason";
    public static final String SKIP_TIME = "skip_time";
    public static final String WIDTH = "__WIDTH__";
    public static final String HEIGHT = "__HEIGHT__";
    public static final String DOWN_X = "__DOWN_X__";
    public static final String DOWN_Y = "__DOWN_Y__";
    public static final String UP_X = "__UP_X__";
    public static final String UP_Y = "__UP_Y__";

    static final String[] DOWNLOAD_EVENTS = new String[]{EVENT_APP_START_DOWNLOAD, EVENT_APP_DOWNLOAD_SUCCESS,
            EVENT_APP_INSTALL_SUCCESS, EVENT_APP_DOWNLOAD_CANCEL, EVENT_DOWNLOAD_FAIL, EVENT_APP_START_INSTALL,
            EVENT_APP_LAUNCH_START, EVENT_APP_LAUNCH_SUCCESS, EVENT_APP_LAUNCH_FAIL};

    private static final String[] VIDEO_EVENTS = new String[]{EVENT_VIDEO_FAIL, EVENT_VIDEO_START,
            EVENT_VIDEO_PAUSE, EVENT_VIDEO_FINISH, EVENT_VIDEO_END, EVENT_VIDEO_FIRST_QUARTILE,
            EVENT_VIDEO_MIDPOINT, EVENT_VIDEO_THIRD_QUARTILE, EVENT_VIDEO_S5, EVENT_VIDEO_S15, EVENT_VIDEO_S30};

}