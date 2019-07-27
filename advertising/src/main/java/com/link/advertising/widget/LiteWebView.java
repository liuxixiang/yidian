package com.link.advertising.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.link.advertising.BuildConfig;
import com.link.advertising.R;
import com.link.advertising.utils.CustomizedToastUtil;


public class LiteWebView extends WebView {

    public static final String JS_HAMMER = "js_hammer";
    public static final String JS_THIRDPARTY = "js_thirdparty";
    private SimpleWebChromeClient mWebChromeClient;
    private Context mContext;
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



    Handler mHandler;
    volatile boolean mTriggerUpdateShowing = false;     //是否已经显示，如果是，则不要再显示了。

    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (BuildConfig.DEBUG) {
                setWebContentsDebuggingEnabled(true);
            }
        }

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
//        settings.setUserAgentString(userAgent + SDKContants.USER_AGENT);
        getSettings().setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getSettings().setAllowFileAccessFromFileURLs(false);
            getSettings().setAllowUniversalAccessFromFileURLs(false);
        }
        mWebChromeClient = new SimpleWebChromeClient();
        setWebChromeClient(mWebChromeClient);


        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
                //super.onReceivedSslError(view, handler, error);
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

            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (TextUtils.isEmpty(url)) {
//                    return true;
//                }
//
//                String _url = url.toLowerCase();
//
//                if (!_url.startsWith("http://") && !_url.startsWith("https://")) {
//                    if (_url.startsWith("intent://")) {//目前在任何情况下都不支持第三方应用打开2016年06月25日
//                        return true;
//                    } else if (SchemeUtil.hasAppInstalled(url)) {
//                        SchemeUtil.openAppWithUrl(mContext, url);
//                        return true;
//                    } else if (SchemeUtil.isDialScheme(url)) {
//                        SchemeUtil.openDialWithUrl(mContext, url);
//                        return true;
//                    } else {
//                        return true;
//                    }
//                }
//                return false;
//            }
        });

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                CustomizedToastUtil.showPrompt(R.string.sdk_webres_unsupport_download, false);

            }
        });
    }

    public void setPageLoadListener(PageLoadListener listener) {
        mPageLoadedListener = listener;
    }

    public interface PageLoadListener {
        void onPageLoadFinished();
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
