package com.yidian.newssdk.utils;

/**
 * Created by patrickleong on 17/05/2017.
 */
public enum ImageFormat {
    WEBP("type=webp_"),
    JPEG("type=jpeg_"),
    PNG("type=png_"),
    THUMBNAIL("type=thumbnail_"),
    GIF("");

    public final String value;

    ImageFormat(String v) {
        value = v;
    }
}
