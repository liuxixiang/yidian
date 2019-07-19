package com.yidian.newssdk.data.channel;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yidian.newssdk.data.card.base.Card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public class YdChannel implements Comparable<YdChannel>, Serializable {

    public String channelId;
    private String channelName;
    private String checksumName;
    private int errorCode;

    public static final String SPLIT_NAME = "\\/\\/";

    public ArrayList<Card> newsList = new ArrayList<>();


    public YdChannel(String channelName) {
        this.channelName = channelName;
    }

    public YdChannel() {
    }

    @Override
    public int compareTo(@NonNull YdChannel o) {
        return TextUtils.equals(channelId, o.channelId) ? 0 : -1;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChecksumName() {
        if (TextUtils.isEmpty(checksumName)) {
            checksumName = channelName;
        }
        return checksumName;
    }

    public void setChecksumName(String checksumName) {
        this.checksumName = checksumName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public  static String getChannelName(String checksumName) {
        String result = checksumName;
        String[] channels = checksumName.split(SPLIT_NAME);
        if (channels != null && channels.length > 1) {
            result = channels[1];
        }
        return result;
    }
}
