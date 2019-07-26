package com.yidian.newssdk.core.web;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yidian.newssdk.IntentConstants;
import com.yidian.newssdk.R;
import com.yidian.newssdk.base.fragment.BaseFragment;
import com.yidian.newssdk.core.web.base.BaseYidianChromeClient;
import com.yidian.newssdk.core.web.base.YdSecureWebViewClient;
import com.yidian.newssdk.protocol.newNetwork.business.report.ReportProxy;
import com.yidian.newssdk.utils.SchemeUtil;
import com.yidian.newssdk.utils.ToastUtils;

import java.io.File;


/**
 * @author zhangzhun
 * @date 2018/5/10
 */

public class SimpleWebFragment extends BaseFragment<SimpleWebPresenter> implements SimpleWebConstract {

    private static final String TAG = SimpleWebFragment.class.getSimpleName();
    String mURL;
    private String mCameraFilePath;
    String mPageURL;
    boolean mPageFirstLoad = false;
    private boolean mLoadingFinish = false;
    private boolean mIsFirstLoadUrl = true;
    private PageLoadListener mPageLoadedListener;
    private ProgressBar mProgress;
    private ProgressBar mDefaultProgress;

    public String mInjectScript = "javascript:void(0);";
    public static final int REQUEST_SELECT_FILE = 100;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    private boolean mHideProgressBar = false;//是否需要隐藏ProgressBar

    private boolean mIsAndroid4_4 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    private static final String PAUSED_ALL_SOUND = "javascript:Array.prototype.map.call(document.getElementsByTagName('audio'), function(v){if(v){v.pause();}});void(0);";

    private
    @Nullable
    YdContentWebView mWebView;

    @Override
    protected int attachLayoutId() {
        return R.layout.ydsdk_fragment_web_view;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mURL = bundle.getString(IntentConstants.URL, "");
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startShowContent();
    }

    @Override
    protected ViewGroup placeHolderParent(View rootView) {
        return (ViewGroup) rootView;
    }

    @Override
    protected String getPageReportId() {
        return ReportProxy.PAGE_WEB;
    }

    @Override
    protected void initWidget(View view) {
        mWebView = (YdContentWebView) mRootView.findViewById(R.id.webView);
        mWebView.setBackgroundColor(Color.TRANSPARENT);

        mDefaultProgress = getInternalProgressBar(mRootView);
        if (mProgress == null) {
            mProgress = mDefaultProgress;
            mDefaultProgress.setVisibility(View.VISIBLE);
        } else {
            mDefaultProgress.setVisibility(View.GONE);
        }
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        WebSettings settings = mWebView.getSettings();


        //websetting设置
        settings.setJavaScriptEnabled(true);    //设置webview支持javascript
        settings.setLoadsImagesAutomatically(true);    //支持自动加载图片
        settings.setUseWideViewPort(true);    //设置webview推荐使用的窗口，使html界面自适应屏幕
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);    //设置webview保存表单数据
        settings.setSavePassword(false);    //设置webview保存密码
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);    //设置中等像素密度，medium=160dpi
        settings.setSupportZoom(false);    //支持缩放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        CookieManager.getInstance().setAcceptCookie(true);

        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);

        // Technical settings
        settings.setSupportMultipleWindows(true);
        mWebView.setLongClickable(false);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);

        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        //webviewsetting设置结束
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }


    }

    private ProgressBar getInternalProgressBar(View rootView) {
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        return progressBar;
    }


    public void startShowContent() {
        if (!TextUtils.isEmpty(mURL)) {
            loadUrl(mURL);
        }
    }

    @Override
    protected void initPresenter() {
        new SimpleWebPresenter(this);
    }

    public void loadUrl(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }


    @Override
    public void onPause() {
        if (mWebView != null) {
            mWebView.loadUrl(PAUSED_ALL_SOUND);
            if (Build.VERSION.SDK_INT >= 11) {
                if (mWebView != null) {
                    mWebView.onPause();
                    // mWebView.pauseTimers();
                }
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
//            if (mPauseAudio) {
//                mWebView.loadUrl(RESUMED_ALL_SOUND);
//                mPauseAudio = false;
//            }
            if (Build.VERSION.SDK_INT >= 11) {
                mWebView.onResume();
                // mWebView.resumeTimers();
            }
        }
    }


    @Override
    public void onDestroyView() {
        if (mWebView != null) {
            mWebView.onDestroyView();
        }
        super.onDestroyView();
    }


    public static SimpleWebFragment getInstance() {
        return new SimpleWebFragment();
    }

    private WebChromeClient mWebChromeClient = new BaseYidianChromeClient(TAG) {


        private Intent createChooserIntent(Intent... intents) {
            Intent chooser = new Intent(Intent.ACTION_CHOOSER);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
            chooser.putExtra(Intent.EXTRA_TITLE, getString(R.string.ydsdk_select_operation));
            return chooser;
        }

        private Intent createCameraIntent() {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File externalDataDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM);
            File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
                    File.separator + "browser-photos");
            cameraDataDir.mkdirs();
            mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                    System.currentTimeMillis() + ".jpg";
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mCameraFilePath)));
            return cameraIntent;
        }

        // For Lollipop 5.0 Devices
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }

            uploadMessage = filePathCallback;

            Intent chooser = createChooserIntent(createCameraIntent());
            chooser.putExtra(Intent.EXTRA_INTENT, fileChooserParams.createIntent());
            try {
                startActivityForResult(chooser, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                ToastUtils.showLong(getActivity(), getString(R.string.ydsdk_select_operation_failed));
                return false;
            }
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int progress) {
            // Activities and WebViews measure progress with different scales.
            // The progress meter will automatically disappear when we reach 100%

//                Log.v(TAG, progress + ":");
            super.onProgressChanged(view,progress);
            if (!mHideProgressBar) {
                mProgress.setProgress(progress);
                if (progress > 98) {
                    mProgress.setVisibility(View.GONE);
                }
            }
        }


        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }

    };

    private WebViewClient mWebViewClient = new YdSecureWebViewClient() {
        private boolean hasFirstPageFinishedEvent = false;

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            android.util.Log.d(TAG, "YdWebViewFragment onReceivedError : code - " + String.valueOf(errorCode) + ", url - " + failingUrl);
            if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME || errorCode == WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME) {
                //修复红米打开问题
                return;
            }
            showErrorPage(view);
        }

        public void showErrorPage(WebView view) {
            if (view != null) {
                view.stopLoading();
                view.clearView();
//                while (view.getChildCount() > 1) {
//                    view.removeViewAt(0);
//                }
//                view.loadUrl("file:///android_asset/offline.html");
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            if (TextUtils.isEmpty(url)) {
                return true;
            }

            String _url = url.toLowerCase();
            if (!_url.startsWith("http://") && !_url.startsWith("https://")) {
                if (_url.startsWith("intent://")) {//目前在任何情况下都不支持第三方应用打开2016年06月25日
                    return true;
                } else if (isPayScheme(url)) {
                    return SchemeUtil.openAppWithUrl(getContext(), url);
                } else if (isDialScheme(url)) {
                    SchemeUtil.openDialWithUrl(getContext(), url);
                    return true;
                } else {
                    return true;
                }
            }
            return false;
        }

        /**
         * 支持微信wap支付
         * weixin://wap/pay?appid=wxef3ef657963be572&noncestr=5cab924ba98c4efd884754254e3dde07&package=WAP&prepayid=wx20160728152308d080e406260550533171&sign=E71B99680B2B90F2A39403921CF0CB05&timestamp=1469690588
         * @param url
         * @return
         */
        public boolean isPayScheme(String url) {
            if (!TextUtils.isEmpty(url) && url.startsWith("weixin://")) {
                return true;
            }
            return false;
        }

        /**
         * 支持调起手机拨号界面
         * @param url
         * @return
         */
        private boolean isDialScheme(String url) {
            if (!TextUtils.isEmpty(url) && TextUtils.equals("tel", Uri.parse(url).getScheme())) {
                return true;
            }
            return false;
        }

        private boolean mWebViewOnPause = false;

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
                                                          String url) {
            return super.shouldInterceptRequest(view, url);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

//            showMask();
            if (!mHideProgressBar) {
                mProgress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            hasFirstPageFinishedEvent = true;
            mLoadingFinish = true;
            mIsFirstLoadUrl = false;

//            ReportClickEvent.reportWebPageLandingPage(mAdCard, url, duration);
            if (mPageLoadedListener != null) {
                mPageLoadedListener.onPageLoadFinished();
            }
            if (!mPageFirstLoad) {
                mPageFirstLoad = true;
                mPageURL = url;
            }
            if (mWebView != null) {
                mWebView.onPageLoadFinished();
            }


            //白名单以外的都需要注入脚本
//            injectJavaScript();

//            hideMask();
        }

        //Support http://xxxx.xx.com/abc.apk?v=1233  request.
        private boolean containApkSuffix(String url) {
            boolean isApkSuffix = false;
            if (!TextUtils.isEmpty(url)) {
                int idx = url.lastIndexOf('/');
                if (idx != -1) {
                    String suffix = url.substring(idx, url.length());
                    if (suffix.contains(".apk")) {
                        isApkSuffix = true;
                    }
                }
            }
            return isApkSuffix;
        }
    };

    /**
     * 针对全网化内容，去掉下载工具条
     */
    private void injectJavaScript() {
        if (mWebView != null) {
            try {
                String script = mInjectScript;
                if (!TextUtils.isEmpty(script)) {
                    mWebView.loadUrl(script);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onShowError() {

    }

    @Override
    public void onHideError() {

    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onShowEmpty() {

    }

    @Override
    public void onHideEmpty() {

    }

    public interface PageLoadListener {
        void onPageLoadFinished();
    }



}
