package com.linken.newssdk.data.card.base;

import android.text.TextUtils;

import com.linken.newssdk.utils.FilterUtils;
import com.linken.newssdk.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/5/22
 */

public class NewsListHelper {

    private List<Card> mResultList;
    private String wemediaHeaderBgColor;//自媒体频道Header背景颜色

    public void parseResponseContent(JSONObject json) {

        if (json == null) {
            return;
        }
        try {
            wemediaHeaderBgColor = JsonUtil.readStringFromJson(json, "bg_color");
            if (!TextUtils.isEmpty(wemediaHeaderBgColor) && !wemediaHeaderBgColor.startsWith("#")) {
                wemediaHeaderBgColor = '#' + wemediaHeaderBgColor;
            }
            JSONArray newsObjList = json.getJSONArray("result");
            String channel = json.getString("channel");
            mResultList = new ArrayList<>(50);
            for (int i = 0; i < newsObjList.length(); i++) {
                JSONObject newsJson = newsObjList.optJSONObject(i);
                if (newsJson == null) {
                    continue;
                }
                Card item = CardHelper.parseCard(newsJson);
                if (item != null) {
                    item.channel = channel;
                    //过滤
                    if (!FilterUtils.filterRegex(item.title)) {
                        mResultList.add(item);
                    }
                }
            }
        } catch (JSONException e) {
            mResultList = new ArrayList<>(5); //防止CRASH发生
            e.printStackTrace();
        }
    }

    public List<Card> getResultList() {
        return mResultList;
    }

}
