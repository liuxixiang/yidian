package com.linken.newssdk.data.card.video;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.card.base.ContentCard;
import com.linken.newssdk.data.card.base.VideoSource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频卡片
 * Created by lishuo on 16/1/14.
 */
public class VideoLiveCard extends ContentCard implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String KUAISHOU_SDK = "kuaishou";

    public static final int PLAY_INLINE = 0;
    public static final int PLAY_DETAIL = 1;
    public static final int PLAY_IMMERSE = 2;
    @IntDef({PLAY_INLINE, PLAY_DETAIL, PLAY_IMMERSE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PlayPosition {
    }

    public static final int FINISH_BASE = 0;
    public static final int FINISH_SHARE = 1;
    @IntDef({FINISH_BASE, FINISH_SHARE})
    @Retention(RetentionPolicy.SOURCE)
    @interface FinishMode {

    }

    public static final String YOUKU_SDK = "youku";
    public int videoDuration;

    public String videoUrl;
    public String sourceId;
    public String sourceFromId;
    public String sourcePic;
    public String sourceType;
    @SuppressWarnings("WeakerAccess")
    public String sourceName;
    public String sourceSummary;
    public String description;
    public String srcDocId; //该视频做为相关视频出现在的视频文章的docid
    public String mSdkProvider;        //视频提供方
    public String mSdkVideoId;        //视频提供方专用id
    public boolean isFromHot;
    // 旧版的
    @PlayPosition
    private int mPlayPosition; //视频的播放位置，0为信息流，1为正文页, 2为沉浸式
    // 新版的
    // 0:'inline' 信息流
    // 1:'detailpage' 详情页
    // 2:'immersive' 沉浸式播放
    @PlayPosition
    private int mDisplayMode;
    // 特殊宽高，快手等 1
    // 普通 16:9，0
    private int specialSize;

    public List<String> mKeywords; //可能为null哦,要注意
    public List<String> mVsctList; //视频分类标记
    public List<String> mVsctShowList;  //用于展示的视频分类

    //发送实时日志使用
    public String mashType = "";
    public String actionSrc = "";
//    public PushMeta pushMeta;

    public boolean mPlayInContent = false;

    public String mCoverPicture;//快手竖图
    public int picWidth = -1;//快手竖图宽
    public int picHeight = -1;//快手竖图高
//    public YoukuVideoCard youkuCard;

    public List<VideoSource> videoUrls = null;

    public int playTimes = 0; // 播放次数

    @FinishMode
    public int finishMode;

    public VideoLiveCard() {
        super();
        contentType = Card.CARD_VIDEO_LIVE;
    }

    public static VideoLiveCard fromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        VideoLiveCard card = new VideoLiveCard();
        parseCardFields(json, card);

        if (card.displayType == DISPLAY_TYPE_VIDEO_KUAISHOU) {
            if (card.picHeight <= 0 || card.picHeight <= 0 || TextUtils.isEmpty(card.mCoverPicture)) {
                return null;
            }
            if (card.getPlayPosition() == PLAY_INLINE) {
                card.setPlayPosition(PLAY_DETAIL);
            }
        }
        return card;
    }

    protected static void parseCardFields(JSONObject json, VideoLiveCard card) {
        ContentCard.fromJSON(card, json);
        card.playTimes = json.optInt("play_count",0);
        card.image = json.optString("image");
        card.title = json.optString("title");
        card.videoUrl = json.optString("video_url");
        card.videoDuration = json.optInt("duration");
        card.description = json.optString("description");
        card.mSdkProvider = json.optString("sdk_provider");
        card.mSdkVideoId = json.optString("sdk_video_id");
        // 播放位置
        card.mPlayPosition = json.optInt("play_position", PLAY_INLINE);
        String displayStr = json.optString("display_mode", "");
        if ("inline".equalsIgnoreCase(displayStr)) {
            card.mDisplayMode = PLAY_INLINE;
        } else if ("detailpage".equalsIgnoreCase(displayStr)) {
            card.mDisplayMode = PLAY_DETAIL;
        } else if ("immersive".equalsIgnoreCase(displayStr)) {
            card.mDisplayMode = PLAY_IMMERSE;
        }
        card.specialSize = json.optInt("special_size", 0);
        String finishModeStr = json.optString("finish_play");
        if ("base".equalsIgnoreCase(finishModeStr)) {
            card.finishMode = FINISH_BASE;
        } else if ("share".equalsIgnoreCase(finishModeStr)) {
            card.finishMode = FINISH_SHARE;
        }

        //信息流里的自媒体的信息是在wemedia_info里保存的
        JSONObject weMediaInfo = json.optJSONObject("wemedia_info");
        if (card.displayType == Card.DISPLAY_TYPE_VIDEO_LIVE_LARGE) {
            extraWemediaInfo(weMediaInfo, card);
        }
//        } else if (card.displayType == Card.DISPLAY_TYPE_VIDEO_KUAISHOU) {
        extraKuiaShouInfo(json, card);
//        }
        // 有 wemedia 也是可以是快手的，所以不可以写成if,else
        if (weMediaInfo != null && weMediaInfo.length() > 0) {
            // 相关视屏的自媒体
            extraWemediaInfo(weMediaInfo, card);
        }

        JSONObject youkuJson = json.optJSONObject("youku_prmt");
//        extraYoukuInfo(youkuJson, card);
        extraSourceInfo(json, card);

        //正文页里自媒体的信息是在related_wemedia来保存的,如果这个字段存在,则从中取数据
        JSONObject relatedMediaInfo = json.optJSONObject("related_wemedia");
        if (relatedMediaInfo != null && relatedMediaInfo.length() > 0) {
            card.sourceId = relatedMediaInfo.optString("channel_id");
            card.sourceFromId = relatedMediaInfo.optString("fromId");
            card.sourcePic = relatedMediaInfo.optString("media_pic");
            card.sourceName = relatedMediaInfo.optString("media_name");
            card.sourceSummary = relatedMediaInfo.optString("media_summary");
            card.sourceType = relatedMediaInfo.optString("type");
        }

        card.mKeywords = parseJsonArray(json, "keywords");
        card.mVsctList = parseJsonArray(json, "vsct");
        card.mVsctShowList = parseJsonArray(json, "vsct_show");
        card.srcDocId = card.id;
    }

    private static void extraWemediaInfo(JSONObject weMediaInfo, VideoLiveCard card) {
        if (weMediaInfo != null && weMediaInfo.length() > 0) {
            card.sourceId = weMediaInfo.optString("channel_id");
            card.sourceFromId = weMediaInfo.optString("fromId");
            card.sourceType = weMediaInfo.optString("type");
            card.sourcePic = weMediaInfo.optString("image");
            card.sourceName = weMediaInfo.optString("name");
            card.sourceSummary = weMediaInfo.optString("summary");
        }
    }

//    private static void extraYoukuInfo(JSONObject json, VideoLiveCard card) {
//        if (json != null && json.length() > 0) {
//            card.youkuCard = new YoukuVideoCard();
//            card.youkuCard.cover = json.optString("l_cover");
//            card.youkuCard.duration = json.optString("l_duration");
//            card.youkuCard.h5 = json.optString("l_h5");
//            card.youkuCard.urlIos = json.optString("l_url_ios");
//            card.youkuCard.title = json.optString("l_title");
//            card.youkuCard.urlAndroid = json.optString("l_url_android");
//        }
//    }

    private static void extraKuiaShouInfo(JSONObject json, VideoLiveCard card) {
        JSONArray images = json.optJSONArray("cover_pictures");
        if (images != null && images.length() > 0) {
            card.mCoverPicture = images.optString(0);
        }
        card.picWidth = json.optInt("v_width");
        card.picHeight = json.optInt("v_height");
    }

    private static void extraSourceInfo(JSONObject json, VideoLiveCard card) {
        JSONArray sourceArray = json.optJSONArray("video_urls");
        if (sourceArray != null) {
            for (int i = 0; i < sourceArray.length(); i++) {
                JSONObject jsonItem = sourceArray.optJSONObject(i);
                if (jsonItem != null) {
                    if (card.videoUrls == null) {
                        card.videoUrls = new ArrayList<>();
                    }
                    VideoSource sourceItem = new VideoSource();
                    sourceItem.quality = jsonItem.optInt("quality", -1);
                    sourceItem.isDefault = jsonItem.optBoolean("default", false);
                    sourceItem.size = jsonItem.optLong("size", 0);
                    sourceItem.url = jsonItem.optString("url", "");
                    card.videoUrls.add(sourceItem);
                }
            }
        }
    }

    private static List<String> parseJsonArray(JSONObject json, String key) {
        List<String> valueList = new ArrayList<>();
        JSONArray array = json.optJSONArray(key);
        if (array != null) {
            int length = array.length();
            if (length > 0) {
                valueList = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    String value = array.optString(i);
                    if (!TextUtils.isEmpty(value)) {
                        valueList.add(value);
                    }
                }
            }
        }
        return valueList;
    }

    @Override
    public boolean isIntegral() {
        // 快手卡片不存在更新信息流数据的问题
        if (isSpecialSize() && displayType == Card.DISPLAY_TYPE_VIDEO_KUAISHOU) {
            return isSpecialIntegral();
        } else { //TODO:这里不能这样修改,会导致相关视频不出现,下次修改因为card引用变化导致信息流上的收藏数字不更新的问题
            return super.isIntegral();
        }
    }

    private boolean isSpecialIntegral() {
        return picHeight > 0 && picHeight > 0 && !TextUtils.isEmpty(videoUrl);
    }

    // 检测是否是有效的快手频道信息,不可以用dtype 判定，理由如下：信息流会下发快手发片的detype !=25 ,但是播放需要按照快手方式播放，
    // 我们DB入库的时候，不支持 mSdkProvider，那么这个时候isInteger会判定不是快手，然后从后端拿数据。
    // 这样做，避免了从后端拿数据，但是 DB 中取出来的会进行网络请求。这个判定方式，对于从DB取出来的卡片是不准确的
    public boolean isSpecialSize() {
        return (specialSize == 1 || (KUAISHOU_SDK.equalsIgnoreCase(mSdkProvider)))
                && picWidth > 0 && picHeight > 0 && !TextUtils.isEmpty(mCoverPicture);
    }

    public boolean isKuaishou() {
        return KUAISHOU_SDK.equalsIgnoreCase(mSdkProvider)
                && picWidth > 0 && picHeight > 0 && !TextUtils.isEmpty(mCoverPicture);
    }
//
//    public boolean isYoukuCard() {
//        return (YOUKU_SDK.equalsIgnoreCase(mSdkProvider))
//                && youkuCard != null && !TextUtils.isEmpty(youkuCard.h5);
//    }

    @PlayPosition
    public int getPlayPosition() {
        return mDisplayMode;
    }

    public void setPlayPosition(@PlayPosition int position) {
        this.mDisplayMode = position;
    }

}