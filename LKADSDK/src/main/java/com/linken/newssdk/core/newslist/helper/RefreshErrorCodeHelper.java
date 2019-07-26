package com.linken.newssdk.core.newslist.helper;

import com.linken.newssdk.R;
import com.linken.newssdk.protocol.newNetwork.core.ApiException;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.LogUtils;
import com.linken.newssdk.utils.NetUtil;

/**
 * @author zhangzhun
 * @date 2018/8/10
 */

public class RefreshErrorCodeHelper {

    public static String code2String(Throwable e, boolean canClick) {
        if (!NetUtil.isConnected(ContextUtils.getApplicationContext())) {
            return ContextUtils.getApplicationContext().getResources().getString(R.string.ydsdk_refresh_no_network);
        }

        if (e instanceof ApiException && canClick) {
            int errorCode = ((ApiException) e).getErrorCode();
            return String.format(ContextUtils.getApplicationContext().getResources().getString(R.string.ydsdk_refresh_server_error_code),
                    String.valueOf(errorCode));
        }

        if (e instanceof ApiException && !canClick) {
            int errorCode = ((ApiException) e).getErrorCode();
            return String.format(ContextUtils.getApplicationContext().getResources().getString(R.string.ydsdk_refresh_server_error_code2),
                    String.valueOf(errorCode));
        }

        return ContextUtils.getApplicationContext().getResources().getString(R.string.ydsdk_refresh_no_response);

    }

    public static String code2String2(Throwable e) {
        if (!NetUtil.isConnected(ContextUtils.getApplicationContext())) {
            return ContextUtils.getApplicationContext().getResources().getString(R.string.ydsdk_refresh_no_network);
        }

        if (e instanceof ApiException) {
            int errorCode = ((ApiException) e).getErrorCode();
            return String.format(ContextUtils.getApplicationContext().getResources().getString(R.string.ydsdk_feed_error_error),
                    String.valueOf(errorCode));
        }

        if (LogUtils.isDebug()) {
            StackTraceElement[] elements = e.getStackTrace();
            LogUtils.d("OtherErrorException:", e.getMessage());
            if (elements != null && elements.length > 0) {
                for (StackTraceElement stackTraceElement : elements) {
                    LogUtils.d("OtherErrorException:", stackTraceElement.toString());
                }
            }
        }
        return ContextUtils.getApplicationContext().getResources().getString(R.string.ydsdk_feed_error_empty);

    }
}
