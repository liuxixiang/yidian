package com.yidian.newssdk.core.web;

/**
 * Created by patrickleong on 1/28/16.
 */
public interface OnOverScrollListener<T>{

    void velocity(int velocity);

    void onOverScrolled(T view, int scrollY, boolean clampedY, int deltaY, int scrollRange, int velocity);
}
