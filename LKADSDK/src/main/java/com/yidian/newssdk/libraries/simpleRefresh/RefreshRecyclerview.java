package com.yidian.newssdk.libraries.simpleRefresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.yidian.newssdk.R;

/**
 * @author zhangzhun
 * @date 2018/7/27
 */

public class RefreshRecyclerview extends RecyclerView{

    private LinearLayout headerView;
    private Context mContext;
    private AnimationDrawable animationDrawable;
    private RefreshDrawable mRefreshDrawable;

    public RefreshRecyclerview(Context context) {
        this(context, null);
    }

    public RefreshRecyclerview(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerview(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    private void init() {
        headerView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ydsdk_simple_refresh_header, null, false);
        mRefreshDrawable = headerView.findViewById(R.id.first_view);
    }
}
