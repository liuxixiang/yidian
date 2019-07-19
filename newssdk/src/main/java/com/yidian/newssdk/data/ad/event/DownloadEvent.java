package com.yidian.newssdk.data.ad.event;

import android.app.DownloadManager;
import android.support.annotation.IntDef;

import com.yidian.newssdk.utils.broadcastbus.BaseEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by liuyue on 16/2/25.
 */
public class DownloadEvent implements IBaseAdEvent, BaseEvent {

    @IntDef({STATUS_DEFAULT, STATUS_INSTALLING, STATUS_OPEN, STATUS_INSTALL, STATUS_RUNNING, STATUS_PENDING
        , STATUS_SUCCESSFUL, STATUS_FAILED, STATUS_PAUSED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface STATUS {
    }

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_INSTALLING = 101;
    public static final int STATUS_OPEN = 102;
    public static final int STATUS_INSTALL = 103;
    public static final int STATUS_RUNNING = DownloadManager.STATUS_RUNNING;
    public static final int STATUS_PENDING = DownloadManager.STATUS_PENDING;
    public static final int STATUS_SUCCESSFUL = DownloadManager.STATUS_SUCCESSFUL;
    public static final int STATUS_PAUSED = DownloadManager.STATUS_PAUSED;
    public static final int STATUS_FAILED = DownloadManager.STATUS_FAILED;

    public final long aid;
    public final int status;
    public final int downloadProgress;
    public final boolean isFromXiaomiCard;

    public DownloadEvent(long aid, int status, int downloadProgress) {
        this.aid = aid;
        this.status = status;
        this.downloadProgress = downloadProgress;
        this.isFromXiaomiCard = false;
    }

    public DownloadEvent(long aid, int status, int downloadProgress, boolean isFromXiaomiCard) {
        this.aid = aid;
        this.status = status;
        this.downloadProgress = downloadProgress;
        this.isFromXiaomiCard = isFromXiaomiCard;
    }
}
