package com.yidian.newssdk.utils;

/**
 * Created by patrickleong on 17/05/2017.
 */

public final class ImageSize {

    public final static int IMAGE_SIZE_ORIGINAL = 0;// 原图
    public final static int IMAGE_SIZE_LARGE = 1;  // large and xlarge image use the same constant
    public final static int IMAGE_SIZE_MIDDLE = 2;
    public final static int IMAGE_SIZE_SMALL = 3;
    public final static int IMAGE_SIZE_TINY = 4;
    public final static int IMAGE_SIZE_CUSTOMIZED = 5;
    public final static int IMAGE_SIZE_NEWS = 6;  //新闻正文中使用的580x000
    public final static int IMAGE_SIZE_PICTURE_FLOW = 7; //列表页瀑布流图
    public final static int IMAGE_SIZE_SQUARE = 8;
    public final static int IMAGE_SIZE_MIDDLE_LARGER = 9;// 左边大图对应的尺寸
    public final static int IMAGE_SIZE_GIF = 10; // GIF 对应尺寸，原图大小
    
    public final static String IMAGE_TYPE_ORIGINAL = ""; // don't set type to get original image
    public final static String IMAGE_TYPE_LARGE = "1000x500&"; //等比缩放
    public final static String IMAGE_TYPE_XLARGE = "960x284&"; //等比缩放 for pad
    public final static String IMAGE_TYPE_MIDDLE = "290x150&";
    public final static String IMAGE_TYPE_SMALL = "212x142&";  //与OPPO版本保持一致
    public final static String IMAGE_TYPE_TINY = "136x136&";
    public final static String IMAGE_TYPE_NEWS = "580x000&";
    public final static String IMAGE_TYPE_PICTURE_FLOW = "960x000&";
    public final static String IMAGE_TYPE_SQUARE = "200x200&";
    public final static String IMAGE_TYPE_MIDDLE_LARGE = "360x300&";

//    public final static int IMAGE_SIZE_ORIGINAL = 0;// 原图
//    public final static int IMAGE_SIZE_LARGE = 1;  // large and xlarge image use the same constant
//    public final static int IMAGE_SIZE_MIDDLE = 2;
//    public final static int IMAGE_SIZE_SMALL = 3;
//    public final static int IMAGE_SIZE_TINY = 4;
//    public final static int IMAGE_SIZE_CUSTOMIZED = 5;
//    public final static int IMAGE_SIZE_NEWS = 6;  //新闻正文中使用的580x000
//    public final static int IMAGE_SIZE_PICTURE_FLOW = 7; //列表页瀑布流图
//    public final static int IMAGE_SIZE_SQUARE = 8;
//    public final static int IMAGE_SIZE_MIDDLE_LARGER = 9;// 左边大图对应的尺寸
//    public final static int IMAGE_SIZE_GIF = 10; // GIF 对应尺寸，原图大小
//
//
//    public final static String IMAGE_TYPE_ORIGINAL = ""; // don't set type to get original image
//    public final static String IMAGE_TYPE_LARGE = "1000x500&"; //等比缩放
//    public final static String IMAGE_TYPE_XLARGE = "960x284&"; //等比缩放 for pad
//    public final static String IMAGE_TYPE_MIDDLE = "290x150&";
//    public final static String IMAGE_TYPE_SMALL = "212x142&";  //与OPPO版本保持一致
//    public final static String IMAGE_TYPE_TINY = "136x136&";
//    public final static String IMAGE_TYPE_NEWS = "580x000&";
//    public final static String IMAGE_TYPE_PICTURE_FLOW = "960x000&";
//    public final static String IMAGE_TYPE_SQUARE = "200x200&";
//    public final static String IMAGE_TYPE_MIDDLE_LARGE = "360x300&";
//
}
