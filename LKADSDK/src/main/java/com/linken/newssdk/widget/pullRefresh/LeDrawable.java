/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.linken.newssdk.widget.pullRefresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.util.TypedValue;

import com.linken.newssdk.R;
import com.linken.newssdk.utils.ContextUtils;

/**
 * Fancy progress indicator for Material theme.
 *
 * @hide
 */
class LeDrawable extends RefreshDrawable {

    private int mTop;
    private int mDiameter;
    private  int loadingResource = R.drawable.loading_anim_img_00;
    private Bitmap scaledBitmap;
    private static final int LOADING_DRAWABLE_WIDTH = 45;
    private int WIDTH = dp2px(LOADING_DRAWABLE_WIDTH);
    private int HEIGHT = dp2px(LOADING_DRAWABLE_WIDTH);
    private static final long LOADING_REFRESHING_TIME = 1500L;
    private static final int DRAWABLE_COUNT = 35;
    private Paint paint = new Paint();
    private ColorFilter filter;

    public LeDrawable(Context context, PullRefreshLayout parent) {
        super(context, parent);
        mTop = -mDiameter - (getRefreshLayout().getFinalOffset() - mDiameter) / 2;
    }

    @Override
    public void setPercent(float percent) {
        loadingResource= getLoadingDrawable(percent);
        invalidateSelf();
    }

    @Override
    public void setColorSchemeColors(int[] colorSchemeColors) {

    }

    @Override
    public void setColor(int color) {
        filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        mTop += offset;
        invalidateSelf();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        int w = right - left;
        super.setBounds(w / 2 - mDiameter / 2, top, w / 2 + mDiameter / 2, mDiameter + top);
    }

    @Override
    public void draw(Canvas c) {
        c.translate(0, mTop);
        paint.setColorFilter(filter);
        scaledBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(ContextUtils.getApplicationContext().getResources(),
                loadingResource));
        //将等比例缩放后的圆形画在画布上面
        final int x = LeDrawable.this.getBounds().centerX();
        if (scaledBitmap.getConfig() == Bitmap.Config.RGB_565) {
            scaledBitmap = RGB565toARGB888(scaledBitmap);
        }
        c.drawBitmap(scaledBitmap,x - WIDTH / 2,HEIGHT / 8,paint);
    }

    private static Bitmap RGB565toARGB888(Bitmap img) {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    private int getLoadingDrawable(float currentProgress) {
        int index = (int) (currentProgress  * DRAWABLE_COUNT);
        int temp = index % DRAWABLE_COUNT;
        if (temp < 0) {
            temp = 0;
        }

        if (temp > DRAWABLE_COUNT) {
            temp = DRAWABLE_COUNT;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("loading_anim_img_");
        if (temp < 10) {
            builder.append("0").append(temp);
        } else {
            builder.append(temp);
        }
        return ContextUtils.getApplicationContext().getResources().getIdentifier(builder.toString(),
                "drawable", ContextUtils.getApplicationContext().getPackageName());
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }


    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


    @Override
    public void start() {
        repeatAnimmator.cancel();
        startRepeatRefresh();
    }
    ValueAnimator repeatAnimmator = ValueAnimator.ofFloat(0.0f, 1.0f);

    public void startRepeatRefresh() {
        repeatAnimmator = ValueAnimator.ofFloat(0.0f, 1.0f);
        repeatAnimmator.setRepeatCount(ValueAnimator.INFINITE);
        repeatAnimmator.setDuration(LOADING_REFRESHING_TIME);
        repeatAnimmator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float percent = (float) animation.getAnimatedValue();
                setPercent(percent);
            }
        });
        repeatAnimmator.start();
    }

    @Override
    public void stop() {
        repeatAnimmator.cancel();
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
