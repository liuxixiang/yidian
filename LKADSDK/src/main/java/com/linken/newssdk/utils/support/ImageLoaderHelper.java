package com.linken.newssdk.utils.support;

import android.widget.ImageView;

import com.linken.newssdk.libraries.imageloader.core.ImageLoader;

/**
 * Created by chenyichang on 2018/5/21.
 */

public class ImageLoaderHelper {

    public static void displayImage(ImageView imageView, String url) {
        ImageLoader.getInstance().displayImage(url, imageView,
                ImageDownloaderConfig.getDefaultIconOptions());
    }


    public static void displayBigImage(ImageView imageView, String url) {
        ImageLoader.getInstance().displayImage(url, imageView,
                ImageDownloaderConfig.getDefaultLargeIconOptions());
    }

}
