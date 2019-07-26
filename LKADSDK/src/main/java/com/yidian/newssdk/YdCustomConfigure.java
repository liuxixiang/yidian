package com.yidian.newssdk;

import android.support.annotation.StyleRes;

import com.yidian.newssdk.data.pref.GlobalConfig;
import com.yidian.newssdk.theme.ThemeManager;

/**
 * @author zhangzhun
 * @date 2018/5/22
 */

public class YdCustomConfigure {


    public static final String ACTON_FONT = "com.yidian.font.ACTION_NIGHTMODE_SWITCH";

    private static final int M_REFRESH_COUNT = 10;
    private static final int M_PRE_LOAD_MORE_COUNT = 3;
    private float mFontSize = GlobalConfig.getFontSize();
    private @StyleRes int themeStyle = R.style.ydsdk_DefaultTheme;
    public static YdCustomConfigure sInstance;

    public static YdCustomConfigure getInstance() {
        if(sInstance == null) {
            synchronized(YdCustomConfigure.class) {
                if(sInstance == null) {
                    sInstance = new YdCustomConfigure();
                }
            }
        }

        return sInstance;
    }

    public int getRefreshCount() {
        return M_REFRESH_COUNT;
    }

    public int getPreLoadMoreCount() {
        return M_PRE_LOAD_MORE_COUNT;
    }

    public float getFontSize() {
        return mFontSize;
    }

    @StyleRes
    public int getCustomThemeStyle() {
        return themeStyle;
    }

    public void setCustomThemeStyle(@StyleRes int themeStyle) {
        this.themeStyle = themeStyle;
        ThemeManager.setThemeId(ThemeManager.SCENE_THEME_CUSTOM);
    }
}
