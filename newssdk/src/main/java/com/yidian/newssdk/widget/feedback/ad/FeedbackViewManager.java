package com.yidian.newssdk.widget.feedback.ad;

import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

/**
 * Created by shian on 14-10-30.
 */
public class FeedbackViewManager {
    static WeakReference<PopupWindow> currentPopupWindow = null;


    /**
     * set the current feedback view
     *
     * @param pop
     */
    public static void setCurrentFeedbackView(PopupWindow pop) {
        if (pop == null) {
            //创建者已经自行关闭了POPUP WINDOW
            currentPopupWindow = null;
        } else {
            closeFeedbackView();
            currentPopupWindow = new WeakReference<PopupWindow>(pop);
        }
    }


    /**
     * close all feedback view
     */
    public static void closeFeedbackView() {
        if (currentPopupWindow != null) {
            PopupWindow old = currentPopupWindow.get();
            if (old != null) {
                old.dismiss();
            }
            currentPopupWindow = null;
        }

    }

    public static boolean hasFeedbackView() {
        if (currentPopupWindow != null) {
            PopupWindow old = currentPopupWindow.get();
            if (old != null) {
                return true;
            }
        }
        return false;
    }
}
