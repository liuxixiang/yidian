package com.linken.newssdk.core.newweb;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.linken.newssdk.BuildConfig;
import com.linken.newssdk.R;
import com.linken.newssdk.SDKContants;
import com.linken.newssdk.utils.CustomizedToastUtil;
import com.linken.newssdk.utils.SchemeUtil;

import java.util.HashMap;
import java.util.Map;


public class LiteWebView extends WebView {

    public static final String JS_HAMMER = "js_hammer";
    public static final String JS_THIRDPARTY = "js_thirdparty";
    private SimpleWebChromeClient mWebChromeClient;
    private Context mContext;
    private Map<String, String> injectJsMap = new HashMap<>(2);
    private boolean mThirdParty;
    private PageLoadListener mPageLoadedListener;

    public LiteWebView(Context context) {
        super(context);
        this.mContext = context;
    }

    public LiteWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public LiteWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private boolean mInExternalPageEnvironment = false; // 这个Webview是否在一个第三方的网页环境；若是的，

    public void setInExternalPageEnvironment(boolean inExternalPageEnvironment) {
        this.mInExternalPageEnvironment = inExternalPageEnvironment;
    }


    public void orientationChanged(final boolean isPortrait) {
        post(new Runnable() {
            @Override
            public void run() {
                //一个是屏幕竖过来后 回调我们的js接口 HB_screen_portrait,
                // 一个是屏幕横过来后 回调我们的js接口 HB_screen_landscape,
                if (isPortrait) {
                    loadUrl("javascript:window.HB_screen_portrait();void(0);");
                } else {
                    loadUrl("javascript:window.HB_screen_landscape();void(0);");
                }
            }
        });
    }

    public void onWifiChannged(final boolean isWifi) {
        post(new Runnable() {
            @Override
            public void run() {
                loadUrl("javascript:window.yidian.HB_onWifiChange(" + isWifi + ");void(0);");
            }
        });
    }

    Handler mHandler;
    volatile boolean mTriggerUpdateShowing = false;     //是否已经显示，如果是，则不要再显示了。

    public void init(final Map<String, String> localJsNeedInsert, boolean isThirdParty) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (BuildConfig.DEBUG) {
                setWebContentsDebuggingEnabled(true);
            }
        }

        mThirdParty = isThirdParty;
        mHandler = new Handler();

        setVerticalScrollBarEnabled(true);
        setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        WebSettings settings = getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDefaultFontSize(18);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent + SDKContants.USER_AGENT);
        getSettings().setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getSettings().setAllowFileAccessFromFileURLs(false);
            getSettings().setAllowUniversalAccessFromFileURLs(false);
        }
        mWebChromeClient = new SimpleWebChromeClient();
        setWebChromeClient(mWebChromeClient);

        injectJsMap = localJsNeedInsert;

        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
                //super.onReceivedSslError(view, handler, error);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (mPageLoadedListener != null) {
                    mPageLoadedListener.shouldInterceptRequest(view, url);
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mPageLoadedListener != null) {
                    mPageLoadedListener.onPageLoadFinished();
                }

                if (injectJsMap == null || injectJsMap.size() == 0) {
                    // 若没有需要加载的js，则do nothing.
                    return;
                }
                if (!TextUtils.isEmpty(injectJsMap.get(JS_HAMMER))) {
                    // 加载第一个。
                    injectScriptFile(view, injectJsMap.get(JS_HAMMER));
                }

                if (mThirdParty && !TextUtils.isEmpty(injectJsMap.get(JS_THIRDPARTY))) {
                    injectScriptFile(view, injectJsMap.get(JS_THIRDPARTY));
                }


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.isEmpty(url)) {
                    return true;
                }

                String _url = url.toLowerCase();

                if (_url.startsWith("zhihu")) {
                    //CustomizedToastUtil.showPrompt("拦截知乎", true);
                    // 直接通过js回调Native方法，不要直接调用Native方法。
                    loadUrl("javascript:window.container.triggerUpgrade();void(0);");
                    return true;
                }

                if (!_url.startsWith("http://") && !_url.startsWith("https://")) {
                    if (_url.startsWith("intent://")) {//目前在任何情况下都不支持第三方应用打开2016年06月25日
                        return true;
                    } else if (SchemeUtil.hasAppInstalled(url)) {
                        SchemeUtil.openAppWithUrl(mContext, url);
                        return true;
                    } else if (SchemeUtil.isDialScheme(url)) {
                        SchemeUtil.openDialWithUrl(mContext, url);
                        return true;
                    } else {
                        return true;
                    }
                }
                return false;
            }
        });

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                CustomizedToastUtil.showPrompt(R.string.ydsdk_webres_unsupport_download, false);

            }
        });
    }

    public void setPageLoadListener(PageLoadListener listener) {
        mPageLoadedListener = listener;
    }

    public interface PageLoadListener {
        void onPageLoadFinished();

        void shouldInterceptRequest(WebView view, String url);
    }

    /*"window.container.showToast('hello');" +*/
    private void injectScriptFile(WebView view, String scriptFile) {
        view.loadUrl("javascript:(function() {" +
                "var parent = document.getElementsByTagName('body').item(0);" +
                "var script = document.createElement('script');" +
                "script.type = 'text/javascript';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "script.innerHTML =  decodeURIComponent(escape(window.atob('" + scriptFile + "')));" +
                "parent.appendChild(script);" +
                "})();void(0);");
    }

    public static final int WV_RESUME = 0;
    public static final int WV_PAUSE = 1;
    public static final int WV_DESTROY = 2;

    public void notifyJsAtKeyPoint(int point) {
        // point = 0 Resume, point = 1 Pause, point = 2 Destory;
        // --> 0 <--> 1,   0 --> 2;  状态变化
        String jsCode;
        switch (point) {
            case WV_RESUME:
                jsCode = "javascript:window.yidian.HB_onWebviewResume();void(0);";
                break;
            case WV_PAUSE:
                jsCode = "javascript:window.yidian.HB_onWebviewPause();void(0);";
                break;
            case WV_DESTROY:
                jsCode = "javascript:window.yidian.HB_onWebviewDestory();void(0);";
                break;
            default:
                return;
        }

        final String js = jsCode;
        post(new Runnable() {
            @Override
            public void run() {
                loadUrl(js);
            }
        });
    }


    public void setChromeClientCallback(SimpleWebChromeClient.OnProgressCallback onProgressCallback) {
        if (mWebChromeClient != null) {
            mWebChromeClient.setOnProgressCallback(onProgressCallback);
        }
    }

    @Override
    public void reload() {
        try {
            if (mReloadListener != null) {
                if (mReloadListener.reloadUrl()) {
                    return;
                }
            }
            super.reload();
        } catch (Exception e) {
        }
    }

    private ReloadUrlListener mReloadListener;

    public void setReloadUrlListener(ReloadUrlListener reloadUrlListener) {
        mReloadListener = reloadUrlListener;
    }

    public interface ReloadUrlListener {
        boolean reloadUrl();
    }

}
