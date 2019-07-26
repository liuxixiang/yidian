package com.linken.newssdk.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */


public class NoScrollViewPager extends ViewPager {
    private boolean scrollable = true;

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent ev) {
        return !this.scrollable?false:super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !this.scrollable?false:super.onInterceptTouchEvent(ev);
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }
}
