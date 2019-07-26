package com.linken.newssdk.core.web.base;

import android.annotation.TargetApi;
import android.support.annotation.CallSuper;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.linken.newssdk.utils.ContextUtils;

/**
 * @author lishouxian on 2018/2/1.
 *         解决WebView控件跨域访问高危漏洞（js调用本地文件的问题）
 */

public class YdSecureWebViewClient extends WebViewClient {

    /**
     * 子类覆写该方法，需要父类方法的返回值，不为null的话，需要直接返回该结果。
     */
    @CallSuper
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      String url) {
        return isSafeSource(url) ? null : new WebResourceResponse(null, null, null);
    }

    @TargetApi(21)
    @Override
    public final WebResourceResponse shouldInterceptRequest(WebView view,
                                                            WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    private boolean isSafeSource(String url) {
        //禁止打开白名单外的本地文件
        return !url.startsWith("file")
                || url.startsWith("file:///android_asset")
                || url.startsWith("file:///data/data/" + ContextUtils.getApplicationContext().getPackageName())
                //  /data/user/0/pkgName/file
                || url.startsWith("file://" + ContextUtils.getApplicationContext().getFilesDir())
                //  /data/user/0/pkgName/cache
                || url.startsWith("file://" + ContextUtils.getApplicationContext().getCacheDir())
                // /storage/emulated/0/Android/data/pkgName/cache
                || url.startsWith("file://" + ContextUtils.getApplicationContext().getExternalCacheDir());
    }
}
