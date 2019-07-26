package com.yidian.newssdk.core.clean.feeds.data;

import android.text.TextUtils;

import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.channel.YdChannel;
import com.yidian.newssdk.data.pref.GlobalDataCache;
import com.yidian.newssdk.utils.SerializeUtil;
import com.yidian.newssdk.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * @author zhangzhun
 * @date 2018/8/10
 */

public class ChannelDataManager {
    public static final String CHANNELS = "/channels";


    public static void saveChannelToCacheFile(YdChannel channel) {
        YdChannel c = channel;
        if(c == null){
            return;
        }

        if ((c.newsList == null || c.newsList.size() < 1)) {
            return;
        }

        String path = StorageUtil.getInternalCacheBasePath() + CHANNELS;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }

        String cacheFileName = path + '/' + c.getChannelName();
        SerializeUtil.saveNewsListToFile(c.newsList, cacheFileName);
        if (channel.newsList != null) {
            channel.newsList.clear();
        }
    }

    public static YdChannel restoreChannelCachedNewsList(String channelName) {
        YdChannel channel = new YdChannel();
        if (TextUtils.isEmpty(channelName)) {
            return null;
        }
        String path = StorageUtil.getInternalCacheBasePath() + CHANNELS + '/' + channelName;
        Object restored = SerializeUtil.restoreObjectFromCache(path);
        try {
            channel.newsList = (ArrayList<Card>) restored;
        } catch (ClassCastException e) {
        }
        if (channel.newsList == null) {
            channel.newsList = new ArrayList<>();
        }
        return channel;
    }
}
