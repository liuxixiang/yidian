package com.yidian.newssdk.export;

/**
 * @author zhangzhun
 * @date 2018/8/29
 */

public interface IExposeInterface {

    void refreshCurrentChannel();

    void scrollToTopPosition();

    boolean isScrollToTopPosition();

    String getCurrentChannelName();
}
