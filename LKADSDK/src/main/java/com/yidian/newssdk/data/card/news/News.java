/**
 * {
 * category:"体育",
 * docid:"news_78d0186b2b39196b9b0464f36a5f202f",
 * keyword_type:"push",
 * related:[
 * "news_73702f1894ecc86d7782fb97f8130a9a",
 * "news_cba50aaffe74c80bf8f0769639828b2d",
 * "news_a29ce1197cdc473f86c886aa3c771dee"
 * ],
 * topic:{
 * containComment:false,
 * containImage:true,
 * id:27511,
 * title:"全明星",
 * tree_size:9
 * },
 * date:"2013-01-16 13:47:00",
 * source:"新民网",
 * title:"专家称林书豪进全明星太荒唐 将受到姚明待遇",
 * url:"http://sports.xinmin.cn/2013/01/16/18186826.html"
 * }
 */
package com.yidian.newssdk.data.card.news;

import android.database.Cursor;
import android.text.TextUtils;


import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.card.base.ContentCard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class News extends ContentCard {

    public static String TAG = News.class.getSimpleName();

    private static final long serialVersionUID = 28L;

    public boolean isTopic;
    public int tType;
    //    public boolean hasContent = false; //正文是否已经下载过了
    public String viewMonitorStrs;

//    public List<News> mRecommendedVideoCards = new ArrayList<>();

//    public ArrayList<Comment> amazingComments;

    public News() {
        contentType = Card.CARD_NEWS;
        isFetched = false;
    }

    public static News fromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }

        News data = new News();
//        data.parseAmazingComment(json);
        parseNewsFields(json, data);
        if (json.has("ttype")) {
            data.isTopic = true;
            data.tType = json.optInt("ttype");
        }

        if (data.id == null) {
            return null;
        }

        return data;
    }

//    private void parseAmazingComment(JSONObject json) {
//        JSONArray array = json.optJSONArray("amazing_comments");
//        if (array != null) {
//            int length = array.length();
//            if (length > 0) {
//                amazingComments = new ArrayList<>(length);
//                for (int i = 0; i < length; i++) {
//                    JSONObject obj = array.optJSONObject(i);
//                    if (obj != null) {
//                        Comment c = Comment.fromJSON(obj);
//                        if (c != null) {
//                            amazingComments.add(c);
//                        }
//                    }
//                }
//            }
//        }
//    }

    /**
     * 因为存在prefetch机制,所以imageUrl可能会更改
     * 在列表页时候拿到的图片会因为以下情况和正文页不同:
     * 1. 图片组进行了图片的abtest
     * 2.编辑对原图进行了修改
     * 将imageUrl拷贝为coverImages, image拷贝为coverImage
     * 只在列表页时候使用
     */
    public void copyCoverImages() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            if (coverImages == null) {
                coverImages = new ArrayList<>();
            } else {
                coverImages.clear();
            }
            coverImages.addAll(imageUrls);
        }

        if (!TextUtils.isEmpty(image)) {
            coverImage = image;
        }
    }

    protected static void parseNewsFields(JSONObject json, News data) {
        ContentCard.fromJSON(data, json);
        if (data.businesssType == News.BUSINESS_TYPE_ADVERTIORIAL) {
            //如果是软文，则需要解析view monitor urls
            JSONArray urls = json.optJSONArray("view_urls");
            if (urls != null) {
                data.viewMonitorStrs = urls.toString();//存入Card中的是urlsStr信息，所以解析的时候将其赋值
            }
        }
        // 其他情况按正常新闻处理
//        data.content = json.optString("content");
//        if (!TextUtils.isEmpty(data.content)) {
//            data.fullJsonContent = json.toString();
//            data.isFetched = true;
//        }

//         Duplicated code, check ContentCard
//        if (json.has("image_urls")) {
//            JSONArray images = json.optJSONArray("image_urls");
//            for (int i = 0; i < images.length(); i++) {
//                data.imageUrls.add(images.optString(i));
//                if (i == 0) {
//                    if (TextUtils.isEmpty(data.image)) {
//                        data.image = data.imageUrls.get(0);
//                    }
//                }
//            }
//        }

        // put more field for video:
        // 考虑：是不是只有videoCard时，才有这个字段，原本应该放入VideoCard中的。
        JSONArray recommendedVideo = json.optJSONArray("recommend_video");
        if (recommendedVideo != null) {
            for (int i = 0; i < recommendedVideo.length(); i++) {
                if (i >= 3) {
                    // 只放3条.
                    break;
                }
                JSONObject tempJson = recommendedVideo.optJSONObject(i);
                if (tempJson == null) {
                    continue;
                }
                /*
                if ( ! CTYPE_VIDEO_CARD.equalsIgnoreCase(tempJson.optString("ctype",
                CTYPE_NORMAL_NEWS))) {
                    // 只关注视频的
                    continue;
                }*/
            }
        }
    }

//    private static void updateNewsDataForCard(News data, JSONObject json) {
//
//        if (data.contentType == ContentType.PK) {
//            data.commentCount = json.optInt("comment_count", 0); //JsonUtil.readIntFromJson
// (json, "comment_count",0);
//            data.likeCount = json.optInt("like", 0); //JsonUtil.readIntFromJson(json, "like", 0);
//            data.date = json.optString("date"); //JsonUtil.readStringFromJson(json, "date");
//            data.source = json.optString("source"); //JsonUtil.readStringFromJson(json, "source");
//            data.isLike = json.optBoolean("is_like", false); //JsonUtil.readBooleanFromJson
// (json, "is_like", false);
//            data.title = json.optString("title"); //JsonUtil.readStringFromJson(json, "title");
//        }else if(data.contentType == ContentType.TESTING){
//            data.url = json.optString("url");
//        }
//    }

    /**
     * this function decode the related channel items and return it as a array
     */
//    public static RelateChannel[] relateChannelListFromJsonString(
//            String relateChannelText) {
//        RelateChannel[] channels = null;
//        try {
//            // JSONObject json = new JSONObject(relateChannelText);
//            JSONArray items = new JSONArray(relateChannelText);
//
//            channels = new RelateChannel[items.length()];
//            for (int i = 0; i < items.length(); i++) {
//                RelateChannel c = new RelateChannel();
//                JSONObject item = items.getJSONObject(i);
//                channels[i] = c;
//                c.channelId = JsonUtil.readStringFromJson(item, "id");
//                c.title = JsonUtil.readStringFromJson(item, "name");
//            }
//        } catch (JSONException e) {
//            return null;
//        }
//
//        return channels;
//    }
//
//
//    public static WeiboUser[] weiboUserFromJsonString(String weibo) {
//        WeiboUser[] users = null;
//        try {
//            // JSONObject json = new JSONObject(relateChannelText);
//            JSONArray items = new JSONArray(weibo);
//
//            users = new WeiboUser[items.length()];
//            for (int i = 0; i < items.length(); i++) {
//                JSONObject item = items.getJSONObject(i);
//                WeiboUser user = new WeiboUser();
//                users[i] = user;
//                user.uid = JsonUtil.readStringFromJson(item, "uid");
//                user.username = JsonUtil
//                        .readStringFromJson(item, "screen_name");
//            }
//        } catch (JSONException e) {
//            return null;
//        }
//
//        return users;
//    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof News)) {
            return false;
        }

        News news = (News) o;

        return id != null ? id.equals(news.id) : news.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean equalsContent(News newsData) {
        if (equals(newsData)) {
            return TextUtils.equals(fullJsonContent, newsData.fullJsonContent);
        }
        return false;
    }

    // 标识 这条新闻是否来自于离线下载。
    public boolean mOfflineContent;

    @Override
    public void copyContent(Card card, boolean isFullCopy) {
        if (!(card instanceof News)) {
            return;
        }
//        String fakeDate = this.date;//保留原有时间

        super.copyContent(card, isFullCopy);
        News newsCard = (News) card;

        content = newsCard.content;
        fullJsonContent = newsCard.fullJsonContent;
        isFetched = newsCard.isFetched;
        imageUrls = newsCard.imageUrls;

//        if (!TextUtils.isEmpty(fakeDate)) {
//            this.date = fakeDate;//New
// List上显示的date是后端fake的。prefetch之后，date会被赋值为新闻的入库时间，而不是后端fake的时间
//        }
    }

    @Override
    public boolean isIntegral() {
        return !TextUtils.isEmpty(fullJsonContent);
    }

    /**
     * 判断是否专题
     */
    @Override
    public boolean isTopic() {
        return displayType == News.DISPLAY_TYPE_TOPIC_BIG
                || displayType == News.DISPLAY_TYPE_TOPIC_SMALL
                || isTopic;
    }

    /**
     * 确定是用转码还是原文打开，如果是普通新闻，返回TRUE, 转码方式打开。如果是FALSE，用原文打开
     */
    public boolean isNormalNews(Card card) {
        return card.mediaType == News.MEDIA_TYPE_NEWS;
    }
}
