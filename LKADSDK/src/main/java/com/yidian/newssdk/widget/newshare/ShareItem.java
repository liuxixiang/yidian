package com.yidian.newssdk.widget.newshare;

import android.graphics.drawable.Drawable;

import com.yidian.newssdk.widget.newshare.FactoryShareItem;

public class ShareItem {
    private int offset;
    private String title;
    private Drawable icon;
    public @FactoryShareItem.ShareEnum int shareType;

    public ShareItem() {
    }

    public ShareItem(@FactoryShareItem.ShareEnum int shareType, int id, String title, Drawable icon) {
        this.offset = id;
        this.title = title;
        this.icon = icon;
        this.shareType = shareType;
    }

    public ShareItem(String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }
}
