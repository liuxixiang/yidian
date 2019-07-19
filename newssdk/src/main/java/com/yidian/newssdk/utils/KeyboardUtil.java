package com.yidian.newssdk.utils;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */


public class KeyboardUtil {
    public KeyboardUtil() {
    }

    public static void hideSoftKeyboard(Activity activity) {
        if(activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static void setupUI(View view, final Activity activity) {
        if(!(view instanceof EditText) && !(view instanceof Button)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    KeyboardUtil.hideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        if(view instanceof ViewGroup) {
            for(int i = 0; i < ((ViewGroup)view).getChildCount(); ++i) {
                View innerView = ((ViewGroup)view).getChildAt(i);
                setupUI(innerView, activity);
            }
        }

    }

    public static void getFocus(View view, boolean openKeyboard) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        if(openKeyboard) {
            InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, 2);
        }

    }

    public static void showkeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 2);
    }

    public static void hidekeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}