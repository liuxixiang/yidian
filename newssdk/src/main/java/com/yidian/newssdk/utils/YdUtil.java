package com.yidian.newssdk.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author lishouxian on 2017/11/30. 作为基本的大妈类，找不到妈的或者比较轻量的方法放到这里吧
 */

public final class YdUtil {
    private YdUtil() {
    }

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showKeyboard(View vFocus) {
        InputMethodManager imm = (InputMethodManager) ContextUtils.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null
//                && !imm.isActive()
                && vFocus != null) {
            imm.showSoftInput(vFocus, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideKeyboard(View vFocus) {
        InputMethodManager imm = (InputMethodManager) ContextUtils.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null
                && imm.isActive()
                && vFocus != null) {
            imm.hideSoftInputFromWindow(vFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager) ContextUtils.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void setCopyText(CharSequence text) {
        ClipboardManager clipboard =
                (ClipboardManager) ContextUtils.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) {
            return;
        }
        ClipData clip = ClipData.newPlainText(null, text);
        clipboard.setPrimaryClip(clip);
    }

    @Nullable
    public static CharSequence getCopyText() {
        ClipboardManager clipboard =
                (ClipboardManager) ContextUtils.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) {
            return null;
        }
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            return clip.getItemAt(0).getText();
        } else {
            return null;
        }
    }

    public static String readAndClose(InputStream is) {
        String result = read(is);
        close(is);
        return result;
    }

    @Nullable
    public static String read(InputStream is) {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
