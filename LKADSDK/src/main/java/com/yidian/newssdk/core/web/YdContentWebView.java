package com.yidian.newssdk.core.web;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yidian.newssdk.utils.ThreadUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * 对满足条件的url, 支持Hybrid网址替换
 * Created by patrickleong on 4/29/16.
 */
public class YdContentWebView extends ObservableWebView implements View.OnLongClickListener {
    private static final String EMPTY_PAGE = "about:blank";

    HashMap<String, String> mHeaders = new HashMap<>();
    private URL mURL;
    private String mUrlStr;
    private String mPath;

    private boolean mbLocalPageLoaded = false;
    private View mLoadingView;  //加载页面时显示loading
    private boolean mEnableHybridNightMode = true;

    private boolean mbDisableScrollby;      //阻止ScrollBy

    private int downY;
    static final int Y_MOVEMENT_THRESHOLD = 60;     //px 当Y方向移动距离超过这个值时，阻止父控件拦截事件
    private boolean mAlreadyDisallowIntercept = false;
    private boolean longPressImage = true;
    private boolean needReload = false;
    private boolean isFirstReload = true;
    private final long RELOAD_TIME = 8_000L;
    private boolean isAdPage = false;
    public static final String REFERER_HEADER_FROM_YIDIAN = "http://www.yidianzixun.com/";

    public YdContentWebView(Context context) {
        super(context);
        init();
    }

    public YdContentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YdContentWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public YdContentWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        mHeaders.put("Referer", REFERER_HEADER_FROM_YIDIAN);
//        WoManager.setInfo(getContext(), this);
        mbDisableScrollby = false;
        setOnLongClickListener(this);
    }

    public void disableScrollBy() {
        mbDisableScrollby = true;
    }

    public void setIsAdPage(boolean isAdPage) {
        this.isAdPage = isAdPage;
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

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    /**
     * 打开URL指定URL, 如果URL命中Hybrid则替换为Hybrid地址打开，否则直接打开
     *
     * @param
     */
    @Override
    public void loadUrl(String url) {

        synchronized (scriptQueue) {
            scriptQueue.clear();    //页面改变，放弃所有脚本
        }
        mbLocalPageLoaded = true;

        if (TextUtils.isEmpty(url)) {
            super.loadUrl(EMPTY_PAGE);
            return;
        }
        if (EMPTY_PAGE.equals(url)) {
            super.loadUrl(EMPTY_PAGE);
            return;
        }
        if (url.startsWith("http") || url.startsWith("https")) {
            showLoading();
            //记录下来，可以让其他线程JavaBridge使用，WebView.getUrl只能在UI Thread使用
            mUrlStr = url;
            mPath = "";
            if (url.startsWith("https://m.yidianzixun.com") || url.startsWith("http://m.yidianzixun.com")) {
                if (isNightMode()) {
                    if (url.lastIndexOf('?') != -1) {
                        url += "&night=1";
                    } else {
                        url += "?night=1";
                    }
                } else {
                    url = url.replace("&night=1", "");
                    url = url.replace("?night=1", "");
                }

                if (url.lastIndexOf('?') != -1) {
                    url += "&newuitest_nov_bucketid=" + 0;
                } else {
                    url += "?newuitest_nov_bucketid=" + 0;
                }

                if (url.lastIndexOf('?') != -1) {
                    url += "&theme_type=" + "red";
                } else {
                    url += "?theme_type=" + "red";
                }
            }
            mbLocalPageLoaded = false;
            super.loadUrl(url, mHeaders);
            updateURL(url);

        } else {
            //javascript:, file:///
            if (url.startsWith("javascript:")) {
                super.loadUrl(url);
            } else if (url.startsWith("file://")) { //直接加载file连接，不通过查询
//                if (isSafeSource(url)) {
                super.loadUrl(url);
                updateURL(url);
//                } else {
//                    super.loadUrl(EMPTY_PAGE);
//                    ToastUtils.showShort(getContext(), "文件路径" + url);
//                }
            } else {
                super.loadUrl(url);
                mURL = null;
            }
        }
        if (needReload && !mbLocalPageLoaded) {
            ThreadUtils.postDelayed2UI(reloadRunnable, RELOAD_TIME);
        }
    }

    private boolean isNightMode() {
        return false;
    }


    private Runnable reloadRunnable = new Runnable() {
        @Override
        public void run() {
            if (isFirstReload) {
                isFirstReload = false;
                reload();
//                new Report.Builder(RELOAD_DOC)
//                        .page(Page.PageNewsActivity)
//                        .submit();
            }
        }
    };


    public void updateURL(String path) {
        try {
            mURL = new URL(path);    //使用URL对象，找出hostname
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        if (mURL == null) {
            return "";
        }
        return mURL.getHost();
    }

    public URL getURL() {
        return mURL;
    }

    public String getOpenedOriginalUrl() {
        return mUrlStr;
    }

    public String getOpenedUrl() {
        if (TextUtils.isEmpty(mPath)) {
            return mUrlStr;
        } else {
            return mPath;
        }
    }

    private LinkedList<String> scriptQueue = new LinkedList<>();

    //改用Hybrid页面后，正文需先加载，然后再运行JavaScript去更新内容，很多时JS会加载太快，没有效果
    //解决方法是让页面加载完成调用JsInterface#loadFinished方法，然后再运行相应的脚本。
    public void runJavaScriptAfterPageLoaded(String script) {
        if (script != null && script.startsWith("javascript:")) {
            if (mbLocalPageLoaded) {
                //页面已加载，直接运行script.
                super.loadUrl(script);
            } else {
                synchronized (scriptQueue) {
                    scriptQueue.add(script);
                }
            }
        } else {
        }
    }

    public void onPageLoadFinished() {
        if (needReload) {
            ThreadUtils.cancelUITask(reloadRunnable);
        }
        if (mbLocalPageLoaded) {
            return;
        }
        mbLocalPageLoaded = true;
        runAllScriptsInQueue();
        dismissLoading();
        savePagePerformanceData();
        post(new Runnable() {
            @Override
            public void run() {
                loadUrl("javascript:android && android.scrollTo()");
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mbDisableScrollby && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    scrollBy(0, 1);
                }
            }
        }, 500);
    }

    public void savePagePerformanceData() {

    }

    private void showLoading() {
        setLoadingVisibility(View.VISIBLE);
        //loading框一定时间后会消失
        postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoading();
            }
        }, 2000);
    }

    void dismissLoading() {
        setLoadingVisibility(View.GONE);
    }

    private void setLoadingVisibility(final int visibility) {
        Activity activity = (Activity) getContext();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(visibility);
                }
            }
        });
    }

    public void setLoadingView(View loadingView) {
        mLoadingView = loadingView;
    }

    private void runAllScriptsInQueue() {
        Activity activity = (Activity) getContext();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (scriptQueue) {
                    for (String s : scriptQueue) {
                        YdContentWebView.super.loadUrl(s);
                    }
                    scriptQueue.clear();
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(null);
    }

    private boolean mIsActionDown = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) event.getY();
                mAlreadyDisallowIntercept = false;
                mIsActionDown = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int curY = (int) event.getY();
                if (Math.abs(downY - curY) > Y_MOVEMENT_THRESHOLD && !mAlreadyDisallowIntercept) {
                    requestDisallowInterceptTouchEvent(true);
                    mAlreadyDisallowIntercept = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //用户点击后，需要重新加载页面
                if (mIsActionDown) {
                    mIsActionDown = false;
                    if (mReloadListener != null) {
                        mReloadListener.onWebContentSizeChange();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setLongPressImage(boolean longPressImage) {
        this.longPressImage = longPressImage;
    }

    public void setNeedReload(boolean needReload) {
        this.needReload = needReload;
    }

    public void onDestroyView() {
        ThreadUtils.cancelUITask(reloadRunnable);
    }

    /**
     * 当调用webview的reload方法的时候会回调这个接口，
     * 返回值表示是否要拦截reload事件
     * 如果不需要调用super.reload，那么在方法里return true
     * 如果需要调用super.raload，那么在方法里return false
     */
    public interface ReloadUrlListener {
        boolean reloadUrl();

        void onWebContentSizeChange();
    }

    private ReloadUrlListener mReloadListener;

    public void setReloadUrlListener(ReloadUrlListener reloadUrlListener) {
        mReloadListener = reloadUrlListener;
    }


}
