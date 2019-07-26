package com.linken.newssdk.widget.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linken.newssdk.R;

/**
 * Created by chenyichang on 2017/8/31.
 */

public class Toolbar extends RelativeLayout{

    private ImageButton mNavButtonView;
    private TextView mToolBarText;
    public Toolbar(Context context) {
        super(context);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Toolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

       init(attrs);
    }

    private void init(AttributeSet attrs){
        LayoutInflater.from(getContext()).inflate(R.layout.ydsdk_custom_v7_toolbar, this, true);
        mNavButtonView = (ImageButton) findViewById(R.id.backBtn);
        mToolBarText = findViewById(R.id.toolbar_text);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomToolBar);

        final Drawable navIcon = typedArray.getDrawable(R.styleable.CustomToolBar_navigationIcon2);
        if (navIcon != null) {
            setNavigationIcon(navIcon);
        }

        typedArray.recycle();

    }
    public void setNavigationIcon(@Nullable Drawable icon) {
        if (mNavButtonView != null) {
            mNavButtonView.setImageDrawable(icon);
        }
    }

    public void setNavigationOnClickListener(OnClickListener onClickListener) {
        mNavButtonView.setOnClickListener(onClickListener);
    }

    public void setToolBarTxt(String text) {
        if (mToolBarText != null) {
            mToolBarText.setText(text);
        }
    }

    public TextView getToolTextView() {
        return mToolBarText;
    }
}
