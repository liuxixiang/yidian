package com.linken.newssdk.linken;

import android.net.Uri;
import android.text.TextUtils;

import com.linken.newssdk.NewsConfig;
import com.linken.newssdk.core.newslist.IRefreshList;
import com.linken.newssdk.core.newslist.NewsListContractView;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.card.news.News;
import com.linken.newssdk.protocol.newNetwork.business.request.imp.RequestConfig;
import com.linken.newssdk.protocol.newNetwork.core.AsyncHttpClient;
import com.linken.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;
import com.linken.newssdk.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.linken.newssdk.data.card.base.Card.CTYPE_NORMAL_NEWS;
import static com.linken.newssdk.data.card.base.Card.Lk_DISPLAY_TYPE_MULTI_IMAGE;
import static com.linken.newssdk.data.card.base.Card.Lk_DISPLAY_TYPE_ONE_IMAGE;


public class LKTopRefreshList implements IRefreshList<Card> {

    private List<Card> mCards = new ArrayList<>();
    private NewsListContractView mContactView;
    private String mChannel;

    public LKTopRefreshList(NewsListContractView view, String channel) {
        this.mContactView = view;
        this.mChannel = channel;
    }

    @Override
    public void firstLazyRefresh() {
        getTopList();
    }

    @Override
    public void onRefresh() {
        getTopList();
    }

    @Override
    public void onLoadMoreRefresh() {
    }

    @Override
    public void onClickErrorRefresh() {
    }

    @Override
    public List<Card> getTAdapterItems() {
        return mCards;
    }

    /**
     * 获取置顶的列表
     */
    private void getTopList() {
        final RequestConfig requestBase = new RequestConfig(NewsConfig.CODE_699999, Uri.encode(mChannel + ""));
        new AsyncHttpClient().get(requestBase, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response != null && response.has("data")) {
                        JSONObject data = response.getJSONObject("data");
                        List<String> codes = requestBase.getCodes();
                        if (data != null) {
                            for (String code : codes) {
                                if (data.has(code)) {
                                    JSONArray jsonArray = data.getJSONArray(code);
                                    if (jsonArray != null && jsonArray.length() > 0 ) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        if(jsonObject == null) {
                                            return;
                                        }
                                        JSONArray paramValues = jsonObject.getJSONArray("paramValue");
                                        if (paramValues != null && paramValues.length() > 0) {
                                            for (int i = 0; i < paramValues.length(); i++) {
                                                if (paramValues.get(i) instanceof JSONObject) {
                                                    JSONObject jsonParam = (JSONObject) paramValues.get(i);
                                                    if (jsonParam != null) {
                                                        News card = new News();
                                                        card.id = jsonParam.has("id") ? JsonUtil.readStringFromJson(jsonParam, "id") : "";
                                                        card.title = jsonParam.has("title") ? JsonUtil.readStringFromJson(jsonParam, "title") : "";
                                                        card.channel = jsonParam.has("channel") ? JsonUtil.readStringFromJson(jsonParam, "channel") : "";
                                                        card.tag_name = jsonParam.has("tag") ? JsonUtil.readStringFromJson(jsonParam, "tag") : "";
                                                        card.url = jsonParam.has("url") ? JsonUtil.readStringFromJson(jsonParam, "url") : "";
                                                        card.source = jsonParam.has("author") ? JsonUtil.readStringFromJson(jsonParam, "author") : "";
                                                        long time = jsonParam.has("createTime") ? JsonUtil.readLongFromJson(jsonParam, "createTime", 0) : 0;
                                                        card.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
                                                        card.displayType = Lk_DISPLAY_TYPE_ONE_IMAGE;
                                                        Object jsonCoverImgUrl = jsonParam.get("coverImgUrl");
                                                        if(jsonCoverImgUrl != null && jsonCoverImgUrl instanceof JSONArray) {
                                                            if (jsonCoverImgUrl != null) {
                                                                String[] coverimgUrl = JsonUtil.parseJSONString((JSONArray) jsonCoverImgUrl);
                                                                if (coverimgUrl != null) {
                                                                    card.coverImages = Arrays.asList(coverimgUrl);
                                                                    if (card.coverImages != null && card.coverImages.size() > 0) {
                                                                        if (TextUtils.isEmpty(card.image)) {
                                                                            card.image = card.coverImages.get(0);
                                                                        }
                                                                        if (card.coverImages.size() >= 3) {
                                                                            card.displayType = Lk_DISPLAY_TYPE_MULTI_IMAGE;
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }

                                                        card.cType = CTYPE_NORMAL_NEWS;
                                                        card.dtype = "top";
                                                        mCards.add(card);
                                                    }
                                                }
                                            }
                                        }
                                        mContactView.handleTopResultList(mCards);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
            }
        });
    }
}
