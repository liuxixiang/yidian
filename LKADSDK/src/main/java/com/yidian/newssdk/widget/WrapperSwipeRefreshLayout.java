package com.yidian.newssdk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.yidian.newssdk.R;
import com.yidian.newssdk.theme.ThemeChangeInterface;
import com.yidian.newssdk.theme.ThemeManager;
import com.yidian.newssdk.widget.pullRefresh.PullRefreshLayout;

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
