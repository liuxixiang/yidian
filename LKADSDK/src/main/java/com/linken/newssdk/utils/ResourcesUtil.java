package com.linken.newssdk.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */


public class ResourcesUtil {
    public ResourcesUtil() {
    }

    public static String getString(Context context, @StringRes int stringId) {
        return null == context?"":context.getResources().getString(stringId);
    }

    public static String[] getStringArray(Context context, @ArrayRes int arrayId) {
        return null == context?null:context.getResources().getStringArray(arrayId);
    }

    public static String stringFormat(Context context, @StringRes int stringId, Object... objects) {
        if(null == context) {
            return "";
        } else {
            String formatString = getString(context, stringId);
            return String.format(formatString, objects);
        }
    }

    public static int getColor(Context context, @ColorRes int colorId) {
        return null == context?0:context.getResources().getColor(colorId);
    }

    public static String getHexColor(Context context, @ColorRes int colorId) {
        return null == context?"0":String.format("#%06X", new Object[]{Integer.valueOf(16777215 & context.getResources().getColor(colorId))});
    }

    public static float getDimen(Context context, @DimenRes int dimenId) {
        return null == context?0.0F:context.getResources().getDimension(dimenId);
    }

    public static int getDimenPxSize(Context context, @DimenRes int dimenId) {
        return null == context?0:context.getResources().getDimensionPixelSize(dimenId);
    }

    public static Drawable getDrawable(Context context, @DrawableRes int drawableID) {
        return context.getResources().getDrawable(drawableID);
    }

    public static Uri getUri(Context context, int resourceId) {
        if(null == context) {
            return null;
        } else {
            Resources r = context.getResources();
            String url = "android.resource://" + r.getResourcePackageName(resourceId) + "/" + r.getResourceTypeName(resourceId) + "/" + r.getResourceEntryName(resourceId);
            return Uri.parse(url);
        }
    }
}

