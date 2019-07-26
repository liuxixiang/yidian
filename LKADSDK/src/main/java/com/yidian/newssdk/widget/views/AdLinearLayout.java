package com.yidian.newssdk.widget.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.core.ad.AdvertisementUtil;
import com.yidian.newssdk.utils.DensityUtil;


/**
 * Provide view count report feature, only works on RecyclerView
 * Created by patrickleong on 1/16/16.
 */
public class AdLinearLayout extends LinearLayout {

    public AdLinearLayout(Context context) {
        super(context);
    }

    public AdLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected long viewId = -1L;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AdLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AdLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private ViewTreeObserver.OnScrollChangedListener li = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            Context context = getContext();
            if (context == null || !(context instanceof Activity)) {
                return;
            }
            int bottom = DensityUtil.getScreenHeight();
            int[] lo = new int[2];
            getLocationOnScreen(lo);
            if (lo[1] < bottom && getVisibility() == View.VISIBLE) {
                sendAdLog();
                getViewTreeObserver().removeOnScrollChangedListener(li);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnScrollChangedListener(li);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnScrollChangedListener(li);
    }

    private void sendAdLog() {
        Object o = getTag();
        if (o != null && o instanceof AdvertisementCard) {
            AdvertisementCard adCard = (AdvertisementCard) o;
            AdvertisementUtil.reportViewEvent(adCard);
        } else {
//            Log.e(AdConstants.ADVERTISEMENT_LOG, "No AdCard data in view");
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(IBaseEvent event) {
//        if (event instanceof RecyclerViewOutOfScreenEvent) {
//            getViewTreeObserver().addOnScrollChangedListener(li);
//        }
//    }
}
