package com.linken.newssdk.utils.support;

import android.view.View;
import android.view.ViewGroup;

import com.linken.newssdk.utils.DensityUtil;
import com.linken.newssdk.utils.ResourcesUtil;

/**
 * Created by chenyichang on 2018/5/24.
 */

public class LayoutParamsHelper {

    public static final int VIDEO_PLAYSIZE_S = 0;
    public static final int VIDEO_PLAYSIZE_R = 1;

    public LayoutParamsHelper() {
    }


    public static void resetParams(View view, float ratio, int marginResource) {
        int margin = 0;
        if (marginResource != 0) {
            margin = ResourcesUtil.getDimenPxSize(view.getContext(), marginResource) * 2;
        }

        resetParams(view, margin, ratio);
    }

    private static void resetParams(View view, int margin, float ratio) {
        int width = DensityUtil.getScreenWidth(view.getContext()) - margin;
        int height = (int) ((float) width * ratio);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    public static void resetVideoParams(View view, int playType, int marginResource) {
        switch (playType) {
            case 0:
                resetParams(view, 0.75F, marginResource);
                break;
            case 1:
                resetParams(view, 0.5625F, marginResource);
            default:
                break;
        }

    }
}
