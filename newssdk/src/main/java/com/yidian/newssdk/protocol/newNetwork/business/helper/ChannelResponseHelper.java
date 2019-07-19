package com.yidian.newssdk.protocol.newNetwork.business.helper;

import android.text.TextUtils;

import com.yidian.newssdk.data.channel.YdChannel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/7/24
 */

public class ChannelResponseHelper {

    private List<YdChannel> mChannelList = new ArrayList<>(10);

    public List parseChnanelJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return mChannelList;
        }

        JSONArray channealArray = jsonObject.optJSONArray("channels");
        String checksumName = "";
        if (channealArray != null && channealArray.length() > 0) {
            for (int index = 0; index < channealArray.length(); index ++) {
                try {
                    JSONObject channelBean = channealArray.getJSONObject(index);
                    if (channelBean != null) {
                        checksumName = channelBean.optString("channel_name");
                        if (!TextUtils.isEmpty(checksumName)) {
                            YdChannel channel = new YdChannel();
                            channel.setChecksumName(checksumName);;
                            channel.setChannelName(YdChannel.getChannelName(checksumName));
                            mChannelList.add(channel);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        return mChannelList;
    }
}
