package com.linken.newssdk.widget.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.core.ad.AdvertisementUtil;
import com.linken.newssdk.utils.DensityUtil;

/**
 * Provide view count report feature, only works on RecyclerView
 * Created by patrickleong on 4/14/15.
 */
public class AdRelativeLayout extends RelativeLayout {

    public AdRelativeLayout(Context context) {
        super(context);
    }

    public AdRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AdRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
//            if (AdvertisementCard.isTencentAd(adCard)) {
//                AdvertisementUtil.reportViewOriginalEvent(adCard);
//            } else {
//                AdvertisementUtil.reportViewEvent(adCard);
//            }
            AdvertisementUtil.reportViewEvent(adCard);

        }
    }
}
