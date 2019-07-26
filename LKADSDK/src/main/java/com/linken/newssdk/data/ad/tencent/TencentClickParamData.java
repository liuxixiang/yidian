package com.linken.newssdk.data.ad.tencent;

/**
 * 联盟广告点击时需要替换的参数值
 * Created by lishuxiang on 2018/6/19.
 */

public class TencentClickParamData {
    private int downX = -999;
    private int downY = -999;
    private int upX = -999;
    private int upY = -999;
    private int width = 0;
    private int height = 0;

    public TencentClickParamData(int DownX, int downY, int upX, int upY, int width, int height) {
        this.downX = DownX;
        this.downY = downY;
        this.upX = upX;
        this.upY = upY;
        this.width = width;
        this.height = height;
    }

    public int getDownX() {
        return downX;
    }

    public void setDownX(int downX) {
        this.downX = downX;
    }

    public int getDownY() {
        return downY;
    }

    public void setDownY(int downY) {
        this.downY = downY;
    }

    public int getUpX() {
        return upX;
    }

    public void setUpX(int upX) {
        this.upX = upX;
    }

    public int getUpY() {
        return upY;
    }

    public void setUpY(int upY) {
        this.upY = upY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
