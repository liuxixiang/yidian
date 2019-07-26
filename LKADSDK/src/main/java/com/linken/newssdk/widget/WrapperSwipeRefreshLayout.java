package com.linken.newssdk.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.linken.newssdk.widget.pullRefresh.PullRefreshLayout;

/**
 * @author zhangzhun
 * @date 2018/6/8
 */

public class WrapperSwipeRefreshLayout extends PullRefreshLayout{


    public WrapperSwipeRefreshLayout(Context context) {
        this(context, null);


    }

    public WrapperSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



}
