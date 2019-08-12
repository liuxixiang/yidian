package com.linken.newssdk.data.card.base;

import android.text.TextUtils;

import com.linken.newssdk.data.card.news.News;
import com.linken.newssdk.libraries.bra.entity.MultiItemEntity;
import com.linken.newssdk.utils.DiffUiDataCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by shian on 11/28/15.
 */
public class Card implements MultiItemEntity, Serializable, DiffUiDataCallback.UiDataDiffer<Card> {
    //列表卡片的各种类型
    public static final String CTYPE_NORMAL_NEWS = "news";//频道列表卡片
    public static final String CTYPE_ADVERTISEMENT = "advertisement"; //广告卡片
    public static final String CTYPE_VIDEO_CARD = "video";//视频卡片
    public static final String CTYPE_JOKE_CARD = "joke";
    public static final String CTYPE_VIDEO_LIVE_CARD = "video_live"; //视频列表卡片，与CARD_VIDEO不同
    public static final String CTYPE_WENDA = "wenda"; //知乎的问答卡片
    public static final String CTYPE_PICTURE_GALLERY = "picture_gallery"; // 图集卡片
    public static final String CTYPE_FENDA = "fenda";   // 分答卡片
    public static final String CTYPE_THEME = "theme";   //话题卡片
    public final static int CARD_UNKNOWN = -1; //尚不支持的卡片
    public final static int CARD_NEWS = 0;  //普通新闻
    public final static int CARD_ADVERTISE = 5; //广告类型
    public final static int CARD_JOKE = 6; //显示段子用，段子本身是新闻
    public final static int CARD_VIDEO_LIVE = 39; //视频列表卡片，与CARD_VIDEO不同
    public final static int CARD_PICTURE_GALLERY = 42; // 图集卡片
    //Business type， 软文广告使用
    public final static int BUSINESS_TYPE_DEFAULT = 0;
    public final static int BUSINESS_TYPE_ADVERTIORIAL = 1; //软文
    //Media type，指明正文页加载时如何处理
    public final static int MEDIA_TYPE_NEWS = 0;  // 原文转码（包括自媒体文章）
    public final static int MEDIA_TYPE_COOPERATE_PAGE = 1; //原文加载（有合作，包括牛车网，凤凰网等）

    //Display type枚举, 这个字段的值是由SERVER定义的，必须与SERVER一致。
    public final static int DISPLAY_UNKNOWN = -1; //文章ITEM不显示图片
    public final static int DISPLAY_TYPE_ONE_IMAGE = 1; //文章ITEM显示1张小图片
    public final static int DISPLAY_TYPE_BIG_IMAGE = 2; //文章ITEM显示1张大图片
    public final static int DISPLAY_TYPE_MULTI_IMAGE = 3; //文章ITEM显示3张小图片
    public final static int DISPLAY_TYPE_TOPIC_BIG = 4; //专题大图
    public final static int DISPLAY_TYPE_TOPIC_SMALL = 5; //专题小图
    public final static int DISPLAY_TYPE_JOKE = 10; //文章显示为段子
    public final static int DISPLAY_TYPE_VIDEO = 20; //文章显示为视频
    public final static int DISPLAY_TYPE_VIDEO_BIG = 21; //文章显示为视频大图，
    public final static int DISPLAY_TYPE_VIDEO_SMALL = 22; //文章显示为视频小图 等同与20
    public final static int DISPLAY_TYPE_VIDEO_FLOW = 23; //视频大图，同21
    public final static int DISPLAY_TYPE_VIDEO_LIVE_LARGE = 21;//视频大图
    public final static int DISPLAY_TYPE_VIDEO_KUAISHOU = 25;// 快手视频卡片
    public final static int AD_TEMPLATE_ICON_GIF = 38; // 浮标Gif广告
    public final static int AD_TEMPLATE_FLOATING_GIF = 97; // 浮屏Gif广告
    public final static int AD_TEMPLATE_3 = 103;
    public final static int AD_TEMPLATE_4 = 104;
    public final static int AD_TEMPLATE_40 = 140;
    public final static int AD_TEMPLATE_116 = 116;
    public final static int DISPLAY_TYPE_PICTURE_GALLERY_OUTSIDE_CHANNEL_BIG_IMAGE = 49;//图集频道外,单张大图(带留白）
    public final static int DISPLAY_TYPE_PICTURE_GALLERY_OUTSIDE_CHANNEL_SMALL_IMAGE = 50;//图集频道外,单张大图(带留白）
    //文章TAG定义
    public final static int NORMAL_NEWS = 1; // 普通文章， 缺失类型
    public final static int HOT_NEWS = 1 << 1; // 热门文章， 对应tag_weibo_pop
    public final static int RECOMMEND_NEWS = 1 << 2; // 推荐文章，对应tag_personalize
    public final static int STICKY_NEWS = 1 << 3; // 置顶文章，对应tag_sticky
    public final static String FROM_THEME_CHANNEL = "theme_channel"; // 主题卡片的
    public final static String FROM_NEAR_BY = "nearby";
    public final static String FROM_DEFAULT = "";
    static final long serialVersionUID = 9L;

    //Attention Here：实时、离线LOG中需要把此项作为“docid”和“itemid”传给后端！！！
    public String id; //卡片的ID。如果有docid就是docid，没有docid就是itemid。
    public String cType = ""; //卡片TYPE名字
    public int contentType = CARD_UNKNOWN; //整形的ContentType
    public boolean isHideFeedback = false; //是否隐藏负反馈按钮
    //comment-like-up-down
    public int commentCount = -1; // 评论数, 表示未初始化
    public int likeCount = 0; // 喜欢数
    public int up = 0;    // 用户顶正文数
    public int down = 0;  // 用户踩正文数
    public boolean isFavorite = false; //是否收藏
    public boolean isLike = false;     //是否推荐
    public boolean isUp = false; // 用户是否顶正文
    public boolean isDown = false; // 用户是否踩正文
    public String factor;   //后端要求传的单词向量
    //双标题使用
    public int displayType = Card.DISPLAY_UNKNOWN; //不现示图，显示小图，大图，3张图

    public int title_sn = 0;    //双标题使用
    //文章是否置顶及底部ICON标记
    public int tag = NORMAL_NEWS; // 普通新闻/热门/推荐新闻/推荐频道／推荐新闻列表
    public String tag_icon; //左下脚图标, 新闻等使用
    public String tag_name; //用来标识精品文章 cell 表示精品文章
    public int display_flag; //
    //log & debug fields
    public String log_meta = ""; //log信息
    public String transInfo = null;
    public String detail = null;
    public String debug = "";
    public List<String> dislikeReasons = null; // 不喜欢原因数组
    public HashMap<String, String> dislikeReasonMap = null;//原因-fromid的映射
    public int mediaType = -1;   //内容类型，-1 不用取正文 0－正常调用js显示正文 1－显示原文＋相关文章及标签, 2 - 视频
    public CardDisplayInfo mDisplayInfo = null;//DisplayInfo部分
    public String image = "";// 新闻图片
    public String title = "";// 新闻标题
    public String url = "";// 新闻链接
    public String date = "";// 新闻日期
    public String impId = "";//impression id
    //为了日志
    public String channel = ""; //频道名称
    public String channelId = ""; //频道ID
    public String channelFromId = "";//频道的FromID
    public String groupId = "";//所属应用的ID
    public String groupFromId = "";//所属应用的FromID
    public String pageId = ""; //对应信息流所在页面的id，统计数据需要,
    public String wmCopyright = ""; //自媒体文章类型:原创等
    public boolean newsFeedBackFobidden = false;
    public String displayScope = "";
    // // card_position 后端传递过来是json array 以后里面可能有多个值，用于表明今后更具体的位置。比方说 之后可以加 static 那么theme_channel+static就代表主题频道内的预览，theme_channel就代表已经订阅了主题频道。
    public List<String> cardPosition = new ArrayList<String>();//图片数组
    public CardLabel cardLabel;
    public String nearbyReaders;
    protected transient boolean isFetched = true;    //标记内容是否取已"拉取"过, 调用过fromJson, 从网络上取或者从数据库恢复时会设成true
    private long lastUpdateTsAsSticky = 0L; //若是置顶文章,则表示这篇文章最后修改时间
    public boolean isDisableThumbups = false; //是否禁止赞
    public String dtype = "";

    public boolean hideDivider = false;
    public boolean getHideDivider() {
        return hideDivider;
    }

    public static void fromJson(Card card, JSONObject json) {
        if (card == null || json == null) return;

        //get itemId, itemId = docid, in future version, docid will be removed.
        card.id = json.optString("itemid");//根据mingjun的说法，itemid不可能为空。有docid就是docid，没有docid就是填入itemid
        if (TextUtils.isEmpty(card.id)) {
            card.id = json.optString("docid"); //id可能是docid
        }
        if (TextUtils.isEmpty(card.id)) {
            card.id = json.optString("id");
        }

        card.commentCount = json.optInt("comment_count", 0);
        card.isLike = json.optBoolean("is_like", false);
        card.likeCount = json.optInt("like", 0);
        card.up = json.optInt("up", 0);
        card.isUp = json.optBoolean("is_up", false);
        card.down = json.optInt("down", 0);
        card.isDown = json.optBoolean("is_down", false);
        card.title_sn = json.optInt("title_sn", 0);
        card.cType = json.optString("ctype", CTYPE_NORMAL_NEWS);
        card.pageId = json.optString("pageid");
        card.newsFeedBackFobidden = json.optBoolean("feedback_forbidden", false);
        card.tag_icon = json.optString("tag_icon");
        card.tag_name = json.optString("tag_name");
        card.display_flag = json.optInt("display_flag");
        card.cardLabel = CardLabel.fromJSON(json.optJSONObject("card_label"));

        JSONObject detail = json.optJSONObject("detail");
        if (detail != null) {
            card.detail = detail.toString();
            JSONObject transInfo = detail.optJSONObject("trans_info");
            if (transInfo != null) {
                card.transInfo = transInfo.toString();
            }
            card.factor = detail.optString("factor", "");
        }

        // 获取热和荐标记
        try {
            JSONArray tags = json.optJSONArray("tags");
            if (tags != null && tags.length() > 0) {
                for (int i = 0; i < tags.length(); i++) {
                    String tag = tags.getString(i);
                    if (tag.equals("tag_personalize")) {
                        card.tag |= RECOMMEND_NEWS;
                    } else if (tag.equals("tag_weibo_pop")) {
                        card.tag |= HOT_NEWS;
                    } else if (tag.equals("tag_sticky")) {
                        card.tag |= STICKY_NEWS;
                    }
                }
            }
        } catch (Exception e) {
        }

        if (card.isSticky()) {
            card.lastUpdateTsAsSticky = json.optLong("update_title_time", 0L);
        }

        JSONArray dislike_reasons = json.optJSONArray("dislike_reasons");
        JSONArray dislike_fromids = json.optJSONArray("dislike_fromids");
        if (dislike_reasons != null) {
            card.dislikeReasons = new LinkedList<>();
            card.dislikeReasonMap = new HashMap<>();
            for (int i = 0; i < dislike_reasons.length(); i++) {
                String reason = dislike_reasons.optString(i);
                String fromId = "";
                if (dislike_fromids != null && i < dislike_fromids.length()) {
                    fromId = dislike_fromids.optString(i);
                }
                if (reason != null) {
                    card.dislikeReasons.add(reason);
                    card.dislikeReasonMap.put(reason, fromId);
                }
            }
        }

        card.isFavorite = json.optBoolean("is_favorite", false);
        card.isFetched = true; //普通的卡片是不需要取正文内容的，所以直接就将isFetched置为TRUE, 标示不需要取

        card.impId = json.optString("impid", "");//算法组需要的字段
        card.log_meta = json.optString("meta", "");//算法组需要的字段

        card.wmCopyright = json.optString("wm_copyright", "");
        card.displayScope = json.optString("display_scope", FROM_DEFAULT);
        card.nearbyReaders = json.optString("nearby_readers");
        card.isDisableThumbups = json.optBoolean("dis_thumbsup", false);
        json.optJSONArray("card_position");
        parseCardPosition(card, json);
    }

    private static void parseCardPosition(Card card, JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        if (jsonObject.has("card_position")) {
            JSONArray cardPosition = jsonObject.optJSONArray("card_position");
            if (cardPosition != null) {
                for (int i = 0; i < cardPosition.length(); i++) {
                    card.cardPosition.add(cardPosition.optString(i));
                }
            }
        }
    }


    /**
     * 内容拷贝, 为文章内容拷贝做准备
     */
    public void copyContent(Card card, boolean isFullCopy) {
        if (card == null) {
            return;
        }
        this.id = card.id;
        this.cType = card.cType;

        if (isFullCopy) {
            this.image = card.image;
            this.date = card.date;
            this.impId = card.impId;
            this.log_meta = card.log_meta;
            this.down = card.down;
            this.up = card.up;
            this.isUp = card.isUp;
            this.isDown = card.isDown;
            this.title = card.title;
            this.commentCount = card.commentCount;
            this.likeCount = card.likeCount;
            this.title_sn = card.title_sn;
            this.isLike = card.isLike;
            this.pageId = card.pageId;
            this.channelFromId = card.channelFromId;
            this.channel = card.channel;
            this.channelId = card.channelId;
            this.groupFromId = card.groupFromId;
            this.groupId = card.groupId;
            this.isHideFeedback = card.isHideFeedback;
            this.wmCopyright = card.wmCopyright;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;

        Card card = (Card) o;

        return !(id != null ? !id.equals(card.id) : card.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "[id = " + id + ", cType = " + cType + "]";
    }

    /**
     * 子类重载这个方法，决定card数据是否完整，之后判断是否需要抓取。
     *
     * @return
     */
    public boolean isIntegral() {
        return false;
    }

    // 这一个卡片是否置顶
    public boolean isSticky() {
        return (tag & News.STICKY_NEWS) == News.STICKY_NEWS;
    }

    @Override
    public int getItemType() {
        return displayType;
    }

    @Override
    public boolean isSame(Card old) {
        return TextUtils.equals(old.id, this.id);
    }

    @Override
    public boolean isUiContentSame(Card old) {
        return TextUtils.equals(old.id, this.id);
    }

}
