package com.yidian.newssdk.libraries.ydvd;

/**
 * Put JZVideoPlayer into layout
 * From a JZVideoPlayer to another JZVideoPlayer
 * Created by Nathen on 16/7/26.
 */
public class YdVideoPlayerManager {

    public static YdVideoPlayer FIRST_FLOOR_JZVD;
    public static YdVideoPlayer SECOND_FLOOR_JZVD;

    public static YdVideoPlayer getFirstFloor() {
        return FIRST_FLOOR_JZVD;
    }

    public static void setFirstFloor(YdVideoPlayer jzVideoPlayer) {
        FIRST_FLOOR_JZVD = jzVideoPlayer;
    }

    public static YdVideoPlayer getSecondFloor() {
        return SECOND_FLOOR_JZVD;
    }

    public static void setSecondFloor(YdVideoPlayer jzVideoPlayer) {
        SECOND_FLOOR_JZVD = jzVideoPlayer;
    }

    public static YdVideoPlayer getCurrentJzvd() {
        if (getSecondFloor() != null) {
            return getSecondFloor();
        }
        return getFirstFloor();
    }

    public static void completeAll() {
        if (SECOND_FLOOR_JZVD != null) {
            SECOND_FLOOR_JZVD.onCompletion();
            SECOND_FLOOR_JZVD = null;
        }
        if (FIRST_FLOOR_JZVD != null) {
            FIRST_FLOOR_JZVD.onCompletion();
            FIRST_FLOOR_JZVD = null;
        }
    }
}
