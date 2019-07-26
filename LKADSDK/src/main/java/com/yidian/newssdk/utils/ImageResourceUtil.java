package com.yidian.newssdk.utils;

import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yidian.newssdk.SDKContants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Created by caichen on 2017/6/22.
 */

public class ImageResourceUtil {

//    public static
//    @DrawableRes
//    int selectBigVIcon(int type) {
//        switch (type) {
//            case 1:
//                return R.drawable.v_blue_big;
//            case 2:
//                return R.drawable.v_orange_big;
//            case 3:
//                return R.drawable.v_red_big;
//            case 4:
//                return R.drawable.v_yellow_big;
//            default:
//                return 0;
//        }
//    }

    public static
    @DrawableRes
    int selectSmallVIcon(int type) {
        switch (type) {
//            case 1:
//                return R.drawable.v_blue_small;
//            case 2:
//                return R.drawable.v_orange_small;
//            case 3:
//                return R.drawable.v_red_small;
//            case 4:
//                return R.drawable.v_yellow_small;
            default:
                return 0;
        }
    }
//
//    public static
//    @DrawableRes
//    int selectBigRoundVIcon(int type) {
//        switch (type) {
//            case 1:
//                return R.drawable.v_blue_round_big;
//            case 2:
//                return R.drawable.v_orange_round_big;
//            case 3:
//                return R.drawable.v_red_round_big;
//            case 4:
//                return R.drawable.v_yellow_round_big;
//            default:
//                return 0;
//        }
//    }
//
//    public static
//    @DrawableRes
//    int selectSmallRoundVIcon(int type) {
//        switch (type) {
//            case 1:
//                return R.drawable.v_blue_round_small;
//            case 2:
//                return R.drawable.v_orange_round_small;
//            case 3:
//                return R.drawable.v_red_round_small;
//            case 4:
//                return R.drawable.v_yellow_round_small;
//            default:
//                return 0;
//        }
//    }
//
//    public static boolean isStaticUrl(String url) {
//        if (url == null) {
//            return false;
//        }
//        Pattern mPattern = Pattern.compile("http://si[0-9]\\.go2yd\\.com/.*");
//        if (mPattern.matcher(url).matches()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static boolean isStaticUrl(String url) {
        if (url == null) {
            return false;
        }
        Pattern mPattern = Pattern.compile("http://si[0-9]\\.go2yd\\.com/.*");
        if (mPattern.matcher(url).matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 静态资源Url不直接支持裁剪，重新拼接Url得到可设置裁剪的Url
     * http://si1.go2yd.com/get-image/0DiuYFIbyUq
     * http://i1.go2yd.com/image.php?type=webp_212x142&url=http://si1.go2yd.com/get-image/0DiuYFIbyUq
     *
     * @param url 静态资源的Url
     * @param needCut 是否需要裁剪
     * @param imageSize 裁剪的大小
     * @return
     * */
    public static String getStaticUrl(@NonNull String url, boolean needCut, ImageFormat imageFormat, int imageSize, String... customSize) {
        if (url == null || TextUtils.isEmpty(url)) {
            return url;
        }
        if (!needCut) {
            return url;
        }
        if (!isStaticUrl(url)) {
            return url;
        }
        StringBuilder newUrl = new StringBuilder();
        ImageFormat format = (Build.VERSION.SDK_INT >= 17) ? ImageFormat.WEBP : ImageFormat.JPEG;
        newUrl.append(SDKContants.HIPU_IMAGE_SERVER);
        newUrl.append((imageFormat == null) ? format.value : imageFormat.value);
//        newUrl.append(ImageSize.IMAGE_TYPE_SMALL);
        switch (imageSize) {
            case ImageSize.IMAGE_SIZE_MIDDLE_LARGER: // 左边大图对应的图片大小
                newUrl.append(ImageSize.IMAGE_TYPE_MIDDLE_LARGE);
                break;
            case ImageSize.IMAGE_SIZE_LARGE:
                newUrl.append(ImageSize.IMAGE_TYPE_LARGE);
                break;
            case ImageSize.IMAGE_SIZE_MIDDLE:
                newUrl.append(ImageSize.IMAGE_TYPE_MIDDLE);
                break;
            case ImageSize.IMAGE_SIZE_SMALL:
                newUrl.append(ImageSize.IMAGE_TYPE_SMALL);
                break;
            case ImageSize.IMAGE_SIZE_TINY:
                newUrl.append(ImageSize.IMAGE_TYPE_TINY);
                break;
            case ImageSize.IMAGE_SIZE_NEWS:
                newUrl.append(ImageSize.IMAGE_TYPE_NEWS);
                break;
            case ImageSize.IMAGE_SIZE_PICTURE_FLOW:
                newUrl.append(ImageSize.IMAGE_TYPE_PICTURE_FLOW);
                break;
            case ImageSize.IMAGE_SIZE_SQUARE:
                newUrl.append(ImageSize.IMAGE_TYPE_SQUARE);
                break;
            case ImageSize.IMAGE_SIZE_CUSTOMIZED:
                if (customSize != null && customSize.length > 0 && !TextUtils.isEmpty(customSize[0])) {
                    newUrl.append(customSize[0]);
                }
                break;
            default:
                newUrl.append(ImageSize.IMAGE_TYPE_SMALL);
                break;
        }
        newUrl.append("url=");
        try {
            url = URLEncoder.encode(url, "utf-8");
            newUrl.append(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
        return newUrl.toString();
    }

}
