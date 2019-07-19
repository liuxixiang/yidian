package com.yidian.newssdk.export;

/**
 * @author zhangzhun
 * @date 2018/6/14
 */

public interface IMediaInterface {


    void onVideoSizeChanged();

    void onAutoCompletion();

    void onError(final int what, final int extra);

    void onPrepared();

    void onInfo(final int what, final int extra);

    void setBufferProgress(final int percent);

    void onSeekComplete();
}
