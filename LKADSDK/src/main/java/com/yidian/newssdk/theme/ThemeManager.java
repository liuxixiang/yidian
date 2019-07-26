package com.yidian.newssdk.theme;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.yidian.newssdk.R;
import com.yidian.newssdk.YdCustomConfigure;
import com.yidian.newssdk.data.pref.GlobalConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chenyichang on 2018/5/28.
 */

public class ThemeManager {

    /**
     * 默认主题
     */
    public static final int SCENE_THEME_DEFAULT = 0;

    /**
     * 自定义主题
     */
    public static final int SCENE_THEME_CUSTOM = 1;

    /**
     * 夜间模式
     */
    public static final int SCENE_THEME_NIGHT = 2;

    @IntDef({SCENE_THEME_DEFAULT, SCENE_THEME_CUSTOM, SCENE_THEME_NIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface ThemeStyle {

    }

    private static final int THEME_DEFAULT = R.style.ydsdk_DefaultTheme;
    private static final int THEME_NIGHT = R.style.ydsdk_NightTheme;

    private static final Map<String, WeakReference<ThemeChangeInterface>> sChangeListeners =
            new HashMap<>();

    public static void registerThemeChange(ThemeChangeInterface i) {
        if (i != null) {
            sChangeListeners.put(String.valueOf(i.hashCode()), new WeakReference<>(i));
        }
    }

    public static void setThemeId(int themeId) {
        if (themeId >= 0) {
            ThemeItem themeItem = new ThemeItem(themeId);
            notifyThemeChange(themeItem);

            // 持久化
            GlobalConfig.saveThemeId(themeId);
        }
    }

    public static int getTheme() {
        return getTheme(getThemeId());
    }

    public static int getThemeId(){
        return GlobalConfig.getThemeId();
    }

    private static void notifyThemeChange(ThemeItem themeItem) {
        // 通知内部
        Iterator<Map.Entry<String, WeakReference<ThemeChangeInterface>>> itr = sChangeListeners.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, WeakReference<ThemeChangeInterface>> entry = itr.next();
            String key = entry.getKey();
            WeakReference<ThemeChangeInterface> wl = entry.getValue();
            if (!TextUtils.isEmpty(key)) {
                if (wl != null) {
                    ThemeChangeInterface l = wl.get();
                    if (l != null) {
                        l.onThemeChanged(themeItem.theme);
                    } else {
                        itr.remove();
                    }
                } else {
                    itr.remove();
                }
            }

        }
    }

    public static int getColor(Context context, int theme, int index, int defValue) {
        TypedArray ta = null;
        try {
            ta = context.getResources().obtainTypedArray(theme);
        } catch (Exception e) {
        }

        if (ta == null) {
            return defValue;
        }

        int color = ta.getColor(index, defValue);
        ta.recycle();
        return color;
    }


    public static Drawable getDrawable(Context context, int theme, int index) {
        TypedArray ta = null;
        try {
            ta = context.getResources().obtainTypedArray(theme);
        } catch (Exception e) {
        }

        if (ta == null) {
            return null;
        }

        Drawable drawable = ta.getDrawable(index);
        ta.recycle();
        return drawable;
    }


    public static class ThemeItem {

        // 主题id 0 1 2
        public final int themeId;

        // 主题 style
        public final int theme;

        public ThemeItem(int themeId) {
            this.themeId = themeId;
            this.theme = getTheme(themeId);
        }

    }

    private static int getTheme(int themeId) {
        if (themeId == SCENE_THEME_CUSTOM) {
            return YdCustomConfigure.getInstance().getCustomThemeStyle();
        } else if (themeId == SCENE_THEME_NIGHT) {
            return THEME_NIGHT;
        } else {
            return THEME_DEFAULT;
        }
    }

}
