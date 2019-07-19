package com.yidian.newssdk.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.TextView;

/**
 * Created by shian on 10/30/14.
 */
public class ColorUtil {

    private final static String TAG = ColorUtil.class.getSimpleName();

    public static int getIntColor(String colorString, @ColorRes int defaultColor) {
        int color = -1;
        try {
            color = Color.parseColor(colorString);
        } catch (Exception e) {
            //Catch NullPointPointerException that may throw.
            if(defaultColor != -1) {
                color = ContextUtils.getApplicationContext().getResources().getColor(defaultColor);
            }
        }
        return color;
    }

    //支持Drawable着色的方法
    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }
    //支持Drawable着色的方法
//    public static Drawable tintDrawable( @DrawableRes int drawableId, @ColorRes int colorId){
//        Drawable drawable = ResourceUtil.getDrawable(drawableId).mutate();
//        return tintDrawable(drawable,ResourceUtil.getColorStateList(colorId));
//    }
//
//    public static void tintDrawables(TextView textView, int color) {
//        Drawable[] drawables = textView.getCompoundDrawables();
//        for (Drawable drawable : drawables) {
//            if (drawable == null) {
//                continue;
//            }
//
//            drawable.mutate();
//            drawable.setColorFilter(new SimpleColorFilter(color));
//        }
//        textView.setCompoundDrawablesWithIntrinsicBounds(
//                drawables[0], drawables[1], drawables[2], drawables[3]);
//    }
}
