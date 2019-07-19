package com.yidian.newssdk.utils.support;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

import com.yidian.newssdk.R;
import com.yidian.newssdk.libraries.imageloader.cache.disc.naming.FileNameGenerator;
import com.yidian.newssdk.libraries.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.yidian.newssdk.libraries.imageloader.core.DisplayImageOptions;
import com.yidian.newssdk.libraries.imageloader.core.ImageLoader;
import com.yidian.newssdk.libraries.imageloader.core.ImageLoaderConfiguration;
import com.yidian.newssdk.libraries.imageloader.core.assist.QueueProcessingType;
import com.yidian.newssdk.libraries.imageloader.core.display.SimpleBitmapDisplayer;
import com.yidian.newssdk.utils.ContextUtils;

/**
 * Created by chenyichang on 2018/5/21.
 */

public class ImageDownloaderConfig {

    private static final DisplayImageOptions OPTIONS_DEFAULT = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisc(true).build();

    public static int sCardPlaceHolderBg = ContextUtils.getApplicationContext().getResources().getColor(R.color.ydsdk_card_img_bg);


    private static DisplayImageOptions options_default_icon;

    private static DisplayImageOptions options_default_large_icon;

    private static FileNameGenerator sFileNameGenerator;

    public static void init(Context c) {
        sFileNameGenerator = new Md5FileNameGenerator();
        try {
            DisplayImageOptions.Builder optionsBuilder = new DisplayImageOptions.Builder();
            optionsBuilder.cacheOnDisc(true);
            DisplayImageOptions options = optionsBuilder.build();

            ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(c);
            configBuilder.defaultDisplayImageOptions(options);
            configBuilder.threadPriority(Thread.NORM_PRIORITY - 2);
            configBuilder.denyCacheImageMultipleSizesInMemory();
            configBuilder.discCacheFileNameGenerator(sFileNameGenerator);
            configBuilder.discCacheSize(24 * 1024 * 1024); // 24 MiB
            configBuilder.tasksProcessingOrder(QueueProcessingType.LIFO);
            ImageLoader.getInstance().init(configBuilder.build());
        } catch (Throwable t) {
            //
        }
    }

    public static DisplayImageOptions getDefaultIconOptions() {
        if (options_default_icon == null) {
            Drawable defaultIcon = new ColorDrawable(sCardPlaceHolderBg);
            options_default_icon = new DisplayImageOptions.Builder()
                    .cloneFrom(OPTIONS_DEFAULT).showImageOnLoading(defaultIcon)
                    .showImageForEmptyUri(defaultIcon)
                    .showImageOnFail(defaultIcon)
                    .displayer(new SimpleBitmapDisplayer()).build();
        }
        return options_default_icon;
    }

    public static DisplayImageOptions getDefaultLargeIconOptions() {
        if (options_default_large_icon == null) {
            Drawable defaultIcon = new ColorDrawable(sCardPlaceHolderBg);
            options_default_large_icon = new DisplayImageOptions.Builder()
                    .cloneFrom(OPTIONS_DEFAULT)
                    .showImageOnLoading(defaultIcon)
                    .showImageForEmptyUri(defaultIcon)
                    .showImageOnFail(defaultIcon)
                    .displayer(new SimpleBitmapDisplayer()).build();
        }
        return options_default_large_icon;
    }

    public static void changeThemeForImage(@ColorInt int cardImgBg) {
        sCardPlaceHolderBg = cardImgBg;
        Drawable defaultIcon = new ColorDrawable(sCardPlaceHolderBg);
        options_default_large_icon = new DisplayImageOptions.Builder()
                .cloneFrom(OPTIONS_DEFAULT)
                .showImageOnLoading(defaultIcon)
                .showImageForEmptyUri(defaultIcon)
                .showImageOnFail(defaultIcon)
                .displayer(new SimpleBitmapDisplayer()).build();

        options_default_icon = new DisplayImageOptions.Builder()
                .cloneFrom(OPTIONS_DEFAULT).showImageOnLoading(defaultIcon)
                .showImageForEmptyUri(defaultIcon)
                .showImageOnFail(defaultIcon)
                .displayer(new SimpleBitmapDisplayer()).build();
    }

}
