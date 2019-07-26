package com.yidian.newssdk.libraries.simpleRefresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author zhangzhun
 * @date 2018/7/27
 */

public class RefreshDrawable extends View{
    private Bitmap endBitmap;


    public RefreshDrawable(Context context) {
        this(context, null);
    }

    public RefreshDrawable(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshDrawable(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
//        endBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pull_end_image_frame_05));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureWidth(widthMeasureSpec)*endBitmap.getHeight()/endBitmap.getWidth());
    }

    private int measureWidth(int widthMeasureSpec){
        int result = 0;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        }else {
            result = endBitmap.getWidth();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }
}
