package com.yidian.newssdk.core.web;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.yidian.newssdk.utils.VelocityHelper;

import java.util.LinkedList;
import java.util.Map;

/**
 * "可观测"的WebView，支持Overscroll时提供速度，滚动距离等信息
 * Created by patrickleong on 1/28/16.
 */
public class ObservableWebView extends WebView {

    private static final String TAG = ObservableWebView.class.getSimpleName();

    private DistanceWrapper mPrevDistance;
    private int mDeltaY;
    private int mScrollRange;
    private int mScrollRangeY;
    private boolean mHorizontalScrollBarEnabled = true;
    private LinkedList<DistanceWrapper> mVelocityTracker = new LinkedList<>();
    private OnOverScrollListener<ObservableWebView> mOverscrollListener;
    private boolean isFakeDown = false;

    public ObservableWebView(Context context) {
        super(context);
        init();
    }

    public ObservableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ObservableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ObservableWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            setWebChromeClient(new MyWebChromeClient());
        }
    }

    public void enableOverScroll(boolean enabled) {
        if (enabled) {
            setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        } else {
            setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        computeVerticalScrollRange();
    }

    @Override
    public int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mWebviewVerticalScrollListener != null) {
            mWebviewVerticalScrollListener.onWebviewVerticalScroll(t, computeVerticalScrollExtent(), computeVerticalScrollRange());
        }
    }

    public interface OnWebviewVerticalScrollListener {
        void onWebviewVerticalScroll(int offset, int screenHeight, int totalHeight);
    }

    private OnWebviewVerticalScrollListener mWebviewVerticalScrollListener;

    public void setOnWebviewVerticalScrollListener(OnWebviewVerticalScrollListener listener) {
        mWebviewVerticalScrollListener = listener;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrolledRangeX, int scrolledRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        mDeltaY = deltaY;
        mScrollRangeY = scrolledRangeY;
        int velocity = 0;
        if (mPrevDistance == null) {
            mPrevDistance = new DistanceWrapper(scrollY, SystemClock.uptimeMillis());
        } else {
            long currentTimeMills = SystemClock.uptimeMillis();
            long duration = currentTimeMills - mPrevDistance.time;

            if (duration != 0) {
                velocity = (int) ((scrollY - mPrevDistance.distance) * 500 / duration);

            } else {
                velocity = (scrollY - mPrevDistance.distance) * 700;

            }
            mPrevDistance.distance = scrollY;
            mPrevDistance.time = currentTimeMills;
        }

        if (!mHorizontalScrollBarEnabled) {
            deltaX = 0;
        }
        boolean clampedY = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrolledRangeX, scrolledRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        if (mOverscrollListener != null) {
            velocity = VelocityHelper.adjustVelocity(getContext(), velocity);
            int scrollRange = computeVerticalScrollRange();
            mOverscrollListener.velocity(velocity);
            mOverscrollListener.onOverScrolled(this, scrollY, clampedY, deltaY, scrollRange, (int) (velocity * 0.5));
        }

        return clampedY;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isFakeDown && ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            isFakeDown = false;
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public static class DistanceWrapper {
        public int distance;
        public long time;

        public DistanceWrapper(int distance, long time) {
            this.time = time;
            this.distance = distance;
        }
    }

    @Override
    public void setHorizontalScrollBarEnabled(boolean enabled) {
        mHorizontalScrollBarEnabled = enabled;
    }

    @Override
    public void loadUrl(String url) {
        try {
            super.loadUrl(url);
        } catch (Exception e) {
        }
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
//        Log.e(TAG, "Url1 = " + url);
        try {
            super.loadUrl(url, additionalHttpHeaders);
        } catch (Exception e) {
        }
    }

    public void onDestroy() {
        // try to remove this view from its parent first
        try {
            ((ViewGroup) getParent()).removeView(this);
        } catch (Exception ignored) {
        }

        // then try to remove all child views from this view
        try {
            removeAllViews();
        } catch (Exception ignored) {
        }

        // and finally destroy this view
        destroy();
    }

    private boolean gettingText = false;
    private Handler selectionHandler = null;

    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (gettingText && selectionHandler != null) {
                gettingText = false;
                Message msg = selectionHandler.obtainMessage();
                msg.obj = message;
                selectionHandler.sendMessage(msg);
                selectionHandler = null;
                result.confirm();
                return true;
            }
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            result.confirm();
            return true;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }
}
