package com.linken.newssdk.data.card.base;

import java.io.Serializable;

/**
 * Created by JayRay on 14/09/2017.
 * Info: 视频源
 */

public class VideoSource implements Serializable {
    public static final int DEFAULT = -1;
    public static final int LOW = 0;
    public static final int NORMAL = 1;
    public static final int HIGH = 2;

    // 来源质量
    public int quality;
    // 是否是默认的设置
    public boolean isDefault;
    // 视频大小
    public long size;
    // 视频地址
    public String url;
}
