package com.linken.newssdk.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public class StatusBarUtil {
    public StatusBarUtil() {
    }

    @TargetApi(19)
    public static void transparencyBar(Activity activity) {
        Window window;
        if(Build.VERSION.SDK_INT >= 21) {
            window = activity.getWindow();
            window.clearFlags(67108864);
            window.getDecorView().setSystemUiVisibility(1280);
            window.addFlags(-2147483648);
            window.setStatusBarColor(0);
        } else if(Build.VERSION.SDK_INT >= 19) {
            window = activity.getWindow();
            window.setFlags(67108864, 67108864);
        }

    }

    public static void setStatusBarColor(Activity activity, int colorId) {
        transparencyBar(activity);
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        } else if(Build.VERSION.SDK_INT >= 19) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorId);
        }

    }

    public static int statusBarLightMode(Activity activity) {
        int result = 0;
        if(Build.VERSION.SDK_INT >= 19) {
            if(MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                result = 1;
            } else if(FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = 2;
            } else if(Build.VERSION.SDK_INT >= 23) {
                activity.getWindow().getDecorView().setSystemUiVisibility(9216);
                result = 3;
            }
        }

        return result;
    }

    public static void statusBarLightMode(Activity activity, int type) {
        if(type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if(type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if(type == 3 && Build.VERSION.SDK_INT >= 23) {
            activity.getWindow().getDecorView().setSystemUiVisibility(9216);
        }

    }

    public static void statusBarDarkMode(Activity activity, int type) {
        if(type == 1) {
            MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if(type == 2) {
            FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if(type == 3) {
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
        }

    }

    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if(window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt((Object)null);
                int value = meizuFlags.getInt(lp);
                if(dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception var8) {
                ;
            }
        }

        return result;
    }

    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if(window != null) {
            Class clazz = window.getClass();

            try {
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                int darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", new Class[]{Integer.TYPE, Integer.TYPE});
                if(dark) {
                    extraFlagField.invoke(window, new Object[]{Integer.valueOf(darkModeFlag), Integer.valueOf(darkModeFlag)});
                } else {
                    extraFlagField.invoke(window, new Object[]{Integer.valueOf(0), Integer.valueOf(darkModeFlag)});
                }

                result = true;
            } catch (Exception var8) {
                ;
            }
        }

        return result;
    }

    public static void showStatusBar(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(params);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void hideStatusBar(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}

