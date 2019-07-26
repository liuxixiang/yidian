package com.yidian.newssdk.widget.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.yidian.newssdk.R;

/**
 * Created by patrickleong on 7/23/15.
 */
public class YdRatioImageView extends ImageView {

    private float mLengthWidthRatio = 0.5f;

    public YdRatioImageView(Context context) {
        super(context);
    }

    public YdRatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YdRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.YdRatioImageView, defStyle, 0);
        mLengthWidthRatio = a.getFloat(R.styleable.YdRatioImageView_length_width_ratio, 0.5f);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, (int) (width * mLengthWidthRatio));
    }

    public void setLengthWidthRatio(float ratio) {
        if (ratio == 0 || ratio == mLengthWidthRatio) {
            return;
        }
        mLengthWidthRatio = ratio;
        invalidate();
    }
}