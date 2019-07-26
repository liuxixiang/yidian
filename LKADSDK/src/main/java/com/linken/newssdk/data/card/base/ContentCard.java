package com.linken.newssdk.data.card.base;

import android.text.TextUtils;

import com.linken.newssdk.data.card.news.News;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shian on 11/28/15.
 */
public abstract class ContentCard extends Card implements Serializable {
    static final long serialVersionUID = 4L;
    public String contentDate = null; // 新闻的真实时间

    //general fields
    public String summary = null;  //摘要
    public String source = null; // 新闻来源
    public String channelName = null;
    public String content = null;  //只有分享时用到，其他时候都是用fullJsonContent字段
    public String fullJsonContent = null; //JSON格式的全部字段内容
    public List<String> imageUrls = new ArrayList<String>();//图片数组
    public List<String> coverImages = new ArrayList<String>();//封面图,因为图片服务器会对显示的图片进行abtest,所以需要增加一个为封面图准备的字段

    public String coverImage;//封面图,编辑可能会修改单图;

    public boolean smallFront = false; //置顶政治文章显示小字体
    //extension fields
    public int businesssType = 0;   //业务类型，1-软文

    public boolean auth = false;        //记录文章是否有版权，有版权的文章进正文会请求广告。
    public boolean isGov = false;       //是部委
    public String source_channel = null; //猜你喜欢卡片中的文章来自频道标签
//    public transient boolean isDirty = false; //记录新闻热门评过，保存bottom comments时用

    public int weMediaPlusV = 0;  //自媒体来源加V认证

    public boolean isShowSummary = false;  // 快讯是否显示summary

    public static void fromJSON(ContentCard card, JSONObject json) {
        if (card == null || json == null) {
            return;
        }
        Card.fromJson(card, json);
        card.image = json.optString("image");
        if (TextUtils.equals("null", card.image)) {
            card.image = null;
        }
        if (json.has("image_urls")) {
            JSONArray images = json.optJSONArray("image_urls");
            if (images != null) {
                for (int i = 0; i < images.length(); i++) {
                    card.imageUrls.add(images.optString(i));
                    if (i == 0) {
                        if (TextUtils.isEmpty(card.image)) {
                            card.image = card.imageUrls.get(0);
                        }
                    }
                }
            }

        }

        card.channelId = json.optString("channel_id");
        card.channelName = json.optString("channel_name");
        card.auth = json.optBoolean("auth", false);
        card.isGov = json.optBoolean("is_gov", false);

        card.date = json.optString("date");
        card.source = json.optString("source");
        card.url = json.optString("url");
        card.title = json.optString("title");
        card.log_meta = json.optString("meta");

        //图片显示类型
        card.dtype = json.optString("dtype", "singlepic");
        // 新闻摘要
        card.summary = json.optString("summary");
        card.debug = json.optString("debug");
        card.source_channel = json.optString("source_channel");
        card.mediaType = json.optInt("mtype", MEDIA_TYPE_NEWS); //JsonUtil.readIntFromJson(json, "mtype", 0);
        card.businesssType = json.optInt("btype", BUSINESS_TYPE_DEFAULT);
        card.content = json.optString("content");
        String font_size = json.optString("font_size");
        if (font_size.equalsIgnoreCase("small")) {
            card.smallFront = true;
        }

        if (!TextUtils.isEmpty(card.content)) {
            card.fullJsonContent = json.toString();
            card.isFetched = true;
        } else {
            card.isFetched = false;
        }

        JSONObject wemediaInfo = json.optJSONObject("wemedia_info");
        if(wemediaInfo != null) {
            card.weMediaPlusV = wemediaInfo.optInt("plus_v", 0);
        } else {
            wemediaInfo = json.optJSONObject("related_wemedia");
            if (wemediaInfo != null) {
                card.weMediaPlusV = wemediaInfo.optInt("plus_v", 0);
            }
        }
    }

    /**
     * LIST
     * CONTENT
     *
     * @param card
     */
    @Override
    public void copyContent(Card card, boolean isFullCopy) {
        if (!(card instanceof ContentCard)) {
            return;
        }
        super.copyContent(card, isFullCopy);
        ContentCard contentCard = (ContentCard) card;

        this.mediaType = contentCard.mediaType;
        this.contentDate = contentCard.date;
        this.source = contentCard.source;
        this.businesssType = contentCard.businesssType;

        if (isFullCopy) {
            this.isGov = contentCard.isGov;
            this.auth = contentCard.auth;
        }
    }

    public boolean supportPrefetch() {
        if (!TextUtils.equals(cType, CTYPE_NORMAL_NEWS)) {
            return false;
        }

        if (isJoke() || displayType == News.DISPLAY_TYPE_VIDEO) {
            return false;
        }

        return true;
    }

    /**
     * 是否是专题
     * @return displaytype 是否为4或5
     */
    public boolean isTopic(){
        return displayType == 4 || displayType == 5;
    }

    /**
     * 通过 Card 对象自己来判断是否为 Joke
     * @return 是否
     */
    public boolean isJoke() {
        return false;
    }
}
