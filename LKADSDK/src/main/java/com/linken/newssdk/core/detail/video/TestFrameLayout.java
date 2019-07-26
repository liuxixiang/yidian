package com.linken.newssdk.core.detail.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by chenyichang on 2018/6/4.
 */

public class TestFrameLayout extends FrameLayout{
    public TestFrameLayout(@NonNull Context context) {
        super(context);
    }

    public TestFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }
}
