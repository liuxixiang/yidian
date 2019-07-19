package com.yidian.newssdk.data.card.base;


import java.util.ArrayList;

public class ListViewItemData {

    //由内容类型和显示类型决定使用的卡片类型。
    //定义各种卡片类型
    public enum DISPLAY_CARD {
        CARD_UNKNOWN,
        //新闻卡片
        NEWS_SMALL_FONT, //无图小字体
        NEWS_NO_IMAGE, //文章ITEM无图片
        NEWS_ONE_IMAGE, //文章ITEM显示1张小图片
        NEWS_BIG_IMAGE, //文章ITEM显示1张大图片
        NEWS_MULTI_IMAGE, //文章ITEM显示3张小图片
        NEWS_2_EQUAL_SIZE_IMAGE,//文章ITEM显示2张一样大小的大图片
        NEWS_3_EQUAL_SIZE_IMAGE,//文章ITEM显示3张一样大小的大图片
        NEWS_1_LEFT_BIG_IMAGE, //文章ITEM左边显示1张大图
        NEWS_1_LEFT_BIG_IMAGE_AND_2_EQUAL_SIZE_IMAGE,//文章ITEM显示3张图片,其中左边一张大图，右边两个一样大小的小图
        JOKE, //段子
        // TODO 添加Display_Type
        JOKE_WITH_PIC, // 带图片的段子图
        JOKE_WITH_MULTI_PIC, //带多图的段子
        JOKE_TITLE_CARD, // 段子头部卡片
        JOKE_GIF, // gif 段子
        PICTURE, //美女图
        IMAGE_COLLECTION, //图集
        REC_CHANNEL, //推荐频道卡片
        PK, //PK卡片
        TESTING, //开心考场卡片
        HOT_SEARCH, //热门搜索
        FUNCTION_BIG_IMAGE,  //功能体验卡:单个大图
        FUNCTION_SMALL_IMAGE,//功能体验卡:单个小图
        FUNC_RICH_BIG_IMG,
        FUNC_RICH_SMALL_IMG,
        FUNC_BANNER_BIG_IMG,
        LOADING_FOOTER,
        BAIKE,   //百科
        EXTERNAL_SEARCH, // 外部搜索
        SEARCH_CHANNEL_LIST, // 搜索推荐
        STOCK_INDEX, //股指
        STOCK_INDEX_NEW, //新股指卡片
        GROUP_HEADER, //分组频道列表
        GROUP_APP_HEADER, //应用分组频道列表
        // Commented on Mar-3rd-2016
        //INTERESTS_FOLDER,   // 兴趣夹卡片
        STOCK_MARKET,  //股票行情
        LOCAL_58_CARD, //58同城广告
        AUDIO,   // 音频卡片
        AUDIO_FM_PAY, // FM付费音频卡片
        AUDIO_FM_PAY_LIKE_FREE,     // FM付费音频卡片, 和免费的音频卡片很像。
        CIRCLE_RECOMMENDED, // 推荐的圈子卡片
        LAST_REFRESH_POS, //上次刷新位置
        SINGLE_QA_CARD, //知识问答卡片
        SECTION_DIVIDER, //卡片分割线
        COMMON_HEADER, //通用卡片头
        COMMON_FOOTER, //通用卡片尾部
        OLYMPIC_NEWSLIST_HEADER, // 奥运新闻卡片头
        RECOMMEND_CHANNEL_LIST, //频道推频道列表
        RECOMMEND_CHANNEL_LIST_TOP, //频道推频道列表, 显示在最顶上
        FORUM_CARD, //论坛卡片
        EDITOR_SPECIAL, //要闻置顶卡片
        WEIBO_CARD,  //微博卡片
        INTEREST_GRAPH_CARD,    //兴趣图谱卡片
        ITINERARY_CARD, //明星行程卡片
        MAIN_PAGE_ITINERARY_CARD, //首页明星行程卡片
        LIVE_SPORTS_CARD, // 体育直播卡片
        LIVE_SPORTS_TWO_CARD,
        WEATHER_CARD,   //天气卡片
        TOMORROW_WEATHER_CARD,  //首页信息流明日天气卡片
        APP_RECOMMEND_FOUR_IMAGE_CARD, //推荐卡片（四个图）
        APP_RECOMMEND_ONE_IMAGE_CARD, // 推荐卡片（一个图片）
        MUSIC_ITEM_CARD,   // 音乐卡片的内容项
        VIDEO_LIVE_LARGE,   // 视频大图
        VIDEO_LIVE_SMALL,// 视频小图
        VIDEO_LIVE_FLOW, // 视频信息流
        VIDEO_KUAISHOU,  // 视频快手
        COLUMN_CARD,    //专栏卡片
        CHARGEABLE_CLOUMN_CARD, //FM付费专栏
        GALLERY_CARD,   //图片轮播卡片
        GALLERY_CARD2,   //图片轮播卡片2
        PICTURE_GALLERY_OUTSIDE_CHANNEL_BIG_PICTURE,// 图集频道外大图
        PICTURE_GALLERY_OUTSIDE_CHANNEL_SMALL_PICTURE,// 图集频道外大图
        PICTURE_GALLERY_THEME_CHANNEL_BIG_PICTURE, // 主题图集的大图样式
        PICTURE_GALLERY_3_EQUAL_SIZE_PICTURES,// 和三图新闻一样的图集
        PICTURE_GALLERY_1_PICTURE,// 图集频道内单张
        PICTURE_GALLERY_4_PICTURES,// 图集频道内 4张
        PICTURE_GALLERY_1_ABOVE_2_BOTTOM, //图集频道内三张(上方一张,下方两张)
        PICTURE_GALLERY_2_ABOVE_1_BOTTOM,//图集频道内三张(上方两张,下方一张)
        PICTURE_GALLERY_1_LEFT_2_RIGHT,// 主题相关的图集卡片样式
        PICTURE_GALLERY_2_PICTURES,// 图集频道内两张图
        PICTURE_GALLERY_HOMEBOY_PICTURES,// 宅男美女
        RECOMMENDED_APPS_IN_HORIZONTAL, // 推荐一组apps, 水平方向
        GO_CHINA_CARD,//奥运，给中国队点赞的卡片
        ATHELET_INFO_CARD,//奥运，运动员的信息卡片
        APP_RECOMMEND_CHECK,//应用推荐查看卡片
        APP_RECOMMEND_CHECK_WITH_RED_ICON,//有红点的应用推荐查看卡片

//        OLYMPIC_CHAMPION_NAVI_CARD,//奥运冠军运动员的信息卡片
        OLYMPIC_TALLY_CARD,//奖牌榜卡片
        OLYMPIC_GOLD_CARD,//金牌榜卡片
        APPCARD_WITH_NEWS,//   应用新闻卡片
        APPCARD_LIKE_NEWS,// 伪装成新闻卡片的应用新闻卡片
        RECOMMEND_CHANNEL_LIST_TOP_WITH_IMAGE, //频道推频道列表, 显示在最顶上, 它与RECOMMEND_CHANNEL_LIST_TOP
        // 区别是有背景图，以及每一个item都是图形化的（起源自奥运频道中的二级导航卡片）
        HIT_MOVIE_CARD,//近期热映卡片
        // 频道推频道列表, 显示在最顶上, 它与RECOMMEND_CHANNEL_LIST_TOP是每一个item都是带小图片的。但又不像 RECOMMEND_CHANNEL_LIST_TOP_WITH_IMAGE
        // 有一个大的背景图。
        RECOMMEND_IMAGE_CHANNEL_LIST_TOP,
        MOVIE_REVIEW_CARD,// 影评卡片
        MOVIE_DESC_CARD,// 影片介绍卡片（详情页）
        REMINDER_CARD,// 通知类卡片(登录提示卡片,频道提示前移卡片)
        MOVIE_TRAILER_CARD,// 预告片视频列表卡片
        ONLY_TITLE_HEADER,// 仅含标题顶部卡片
        CITY_ACITVITY,
        PHONE_OFFER,//手机报价卡片(fake)
        NOVEL_PROMOTION,//小说卡片
        CHANNEL_RECOMMEND,//频道推荐卡片
        FENDA_CARD,   //分答卡片
        AUDIO_LIST,   // 3张横着的音频构成的一个大卡片。
        OPTIONAL_STOCK,
        SMALL_WEATHER,

        EDITOR_HOT_TOPIC,
        CAR_QUOTED_PRICE_CARD, // 汽车报价卡片
        VIDEO_TOPIC,
        YIDIANHAO_PROMOTION_CARD,// 主页 一点号导流卡片

        CHANNEL_SUBSCRIBE, // 搜索结果页 频道订阅卡片
        KEYWORD_SUBSCRIBE, // 搜索结果页 关键词订阅卡片
        AD_SUBSCRIBE,  //搜索结果页 广告 订阅卡片
        WE_MEDIA_CARD,
        INPUT_STOCK_CARD,
        YIDIANHAO_RECOMMNED,
        HOT_BOOKS,

        WEIBO_WORD_CARD,
        WEIBO_PIC_CARD,
        WEIBO_GIF_CARD,
        WEIBO_VIDEO_CARD,
        AMAZING_COMMENT_CARD,
        THEME_CARD,
        THEME_LIST_HEADER,
        AD_THEME_HEADER,//广告头图卡片
        PUSH_WEATHER_CARD,

        BEST_ARTICLE_REC_CARD,
        FOCUS_IMAGE_NEWS_CARD,
        INTEREST_SELECT_CARD, // 兴趣选择的卡片
        WENDA_CARD, //问答卡片，目前支持知乎 wenda,  dType 70, 71
        WENDA_ZHIHU_MULTIPIC_CARD, //知乎卡片， wenda,  dType 72
        WENDA_ZHIHU_MULTIPIC_CARD2, //知乎卡片， wenda,  dType 73
        WENDA_ZHIHU_NEWSLIKE_CARD, //知乎卡片， wenda,  dType 74, 75, 76
        RELATED_CHANNEL_CARD,//老用户频道信息流频道推荐卡片
        SEARCH_RESULT_WEMEDIA,
        SUICIDE_HELP_CARD, // 自杀救助卡片
        JIKE_PIC_CARD, //负一屏即刻图文卡片
        VIDEO_JIKE, //负一屏即刻视频卡片
        JIKE_NEWS_CARD, //非负一屏即刻图文卡片
        THEME_CHANNEL_VIDEO, // 主题视频卡片
        STATIC_EXTEND_CARD,

        //王者荣耀卡片
        HERO_INFO,
        GLORY_LEVEL,

        // 主题头部卡片
        THEME_CHANNEL_HEADER,
        // 主题小图卡片
        THEME_CHANNEL_SMALL_IMAGE,
        // 主题三图新闻卡片卡片
        THEME_CHANNEL_MULTI_CARD,
        // 主题无图新闻卡片
        THEME_CHANNEL_NO_IMAGE,
        // 主题 主题选择卡片
        THEMES_INFO,
        // 主题 每日精选
        THEME_BANNER,
        // 首页互联网行业快讯卡片
        KUAI_XUN_LIST,
        // 快讯结果页卡片
        KUAI_XUN_INTERNET_FLASH,
        HOT_REFRESH_SIGNPOST,
        // 自媒体的头部信息
        WEMEDIA_HEADER_VIEW,
        // 大宗物品交易信息卡片
        DA_ZONG,
        // 新闻直播小图
        NEWSLIVE_SMALL,
        //新闻直播大图
        NEWSLIVE_BIG,

        //端内主题化 综合_大杂烩卡片
        THEMECHANNEL_ARTICLE_AND_PICTURE,
        //端内主题化 综合_横滑图文卡片
        THEMECHANNEL_COMPLEX_GRAPHIC,
        //端内主题化 视频_横滑宽屏卡片
        THEMECHANNEL_NORMAL_VIDEO,
        //端内主题化 视频_横滑竖屏卡片
        THEMECHANNEL_KUAISHOU_VIDEO,
        //端内主题化 短内容_段子美女卡片
        THEMECHANNEL_JOKE_AND_BEAUTY,

        //世界杯赛事直播卡片
        WORLDCUP_MATCH_LIVES,
        //世界杯视频
        WORLDCUP_VIDEO,

        //列表广告模版
        //广告模版定义, http://192.168.18.5/bin/view/Main/API_V1/AdvertisementTemplate
        AD_TP_3(3, false),
        AD_TP_4(4, false),
        AD_TP_5(5, true),
        AD_TP_7(7, false),
        AD_TP_8(8, false),
        AD_TP_9(9, false),
        AD_TP_10(10, true),
        AD_TP_11(11, false),
        AD_TP_13(13, false),
        AD_TP_15(15, false),
        AD_TP_19(19, false),
        AD_TP_20(20, true),
        AD_TP_21(21, true),
        AD_TP_23(23, false),
        AD_TP_25(25, true),
        AD_TP_26(26, false),
        AD_TP_29(29, false),
        AD_TP_30(30, true),
        AD_TP_31(31, true),
        AD_TP_33(33, false),
        AD_TP_37(37, false),
        AD_TP_39(39, false),
        AD_TP_40(40, false),
        AD_TP_41(41, true),
        AD_TP_43(43, false),
        AD_TP_44(44, false),
        AD_TP_45(45, false),
        AD_TP_46(46, false),
        AD_TP_49(49, false),
        AD_TP_53(53, false),
        AD_TP_56(56, false),
        AD_TP_59(59, true),
        AD_TP_63(63, false),
        AD_TP_70(70, false),
        AD_TP_203(203, false),
        AD_TP_207(207, false),
        AD_TP_221(221, true),
        AD_TP_241(241, true);

        public final int adTemplateId;
        public final boolean supportedDownload;

        DISPLAY_CARD() {
            this(-1, false);
        }

        /**
         * 广告列表Card专用
         *
         * @param adTemplateNumber
         * @param supportedDownload whether it is a download type ad, support slient install if true
         */
        DISPLAY_CARD(int adTemplateNumber, boolean supportedDownload) {
            adTemplateId = adTemplateNumber;
            this.supportedDownload = supportedDownload;
        }

        public boolean isAdCard() {
            return adTemplateId != -1;
        }

        public static boolean isSupportedNewsFeedAdTemplate(int templateId) {
            for (DISPLAY_CARD card : supportAdCards) {
                if (templateId == card.adTemplateId) {
                    return true;
                }
            }
            return false;
        }

        public static final DISPLAY_CARD values[] = values();
        private static DISPLAY_CARD[] supportAdCards;

        static {
            if (supportAdCards == null) {
                //Initial ad array.
                ArrayList<DISPLAY_CARD> cardList = new ArrayList<>();
                for (DISPLAY_CARD c : values) {
                    if (c.adTemplateId != -1) {
                        cardList.add(c);
                    }
                }
                supportAdCards = cardList.toArray(new DISPLAY_CARD[cardList.size()]);
                for (int i = 0; i < cardList.size(); i++) {
                    supportAdCards[i] = cardList.get(i);
                }
                cardList.clear();
            }
        }

        public static DISPLAY_CARD getAdCard(int templateId) {
            for (DISPLAY_CARD card : supportAdCards) {
                if (templateId == card.adTemplateId) {
                    return card;
                }
            }
            return null;
        }

        public static boolean supportDownload(int templateId) {
            DISPLAY_CARD card = getAdCard(templateId);
            if (card == null) {
                return false;
            }
            return card.supportedDownload;
        }
    }

    public int id = -1;   //用来判断文章位置

    /**
     * 卡片的显示类型, 如单图，多图，PK等， 由内容类型及显示类型dType决定
     */
    public DISPLAY_CARD cardType = DISPLAY_CARD.CARD_UNKNOWN;

    /**
     * 指向具体的数据， 单条的新闻，广告，频道等卡片数据
     */
    public Card data;

    /**
     * 如果是来自复合的卡片，则提供父卡片的信息
     */


    /**
     * 推荐新闻／频道等在这个推荐列表中的位置
     * 负值表示不是新闻列表内容
     */
//    public int subPosition = -1;

    public String dateString;

    public String todayString;

    public boolean hideTopLine = false; //是否隐藏顶部的分割线

    /**
     * 对于只有一种卡片显示类型的数据，不需要设置CONTENT TYPE
     *
     * @param cardType
     * @param data
     */


    public ListViewItemData(DISPLAY_CARD cardType, Card data) {
        this.cardType = cardType;
        this.data = data;
//        this.parentCard = parentCard;
        if (data != null && data.id != null) {
            id = data.id.hashCode();
        }
    }

    public void setHideTopLine(boolean hide) {
        hideTopLine = hide;
    }

    /*
    * 这个方法会影响到列表到正文页的数据传递，虽然不会影响业务，但相关的数据上报会有问题
    * 所以加新卡片时，必须添加
    * */
    public boolean isContentCard() {
        // TODO: 这个函数需要优化, 有空时做。
        if (cardType == DISPLAY_CARD.NEWS_BIG_IMAGE
                || cardType == DISPLAY_CARD.NEWS_MULTI_IMAGE
                || cardType == DISPLAY_CARD.NEWS_NO_IMAGE
                || cardType == DISPLAY_CARD.NEWS_ONE_IMAGE
                || cardType == DISPLAY_CARD.NEWS_2_EQUAL_SIZE_IMAGE
                || cardType == DISPLAY_CARD.NEWS_3_EQUAL_SIZE_IMAGE
                || cardType == DISPLAY_CARD.NEWS_1_LEFT_BIG_IMAGE
                || cardType == DISPLAY_CARD.NEWS_1_LEFT_BIG_IMAGE_AND_2_EQUAL_SIZE_IMAGE
                || cardType == DISPLAY_CARD.NEWS_SMALL_FONT
                || cardType == DISPLAY_CARD.PK
                || cardType == DISPLAY_CARD.PICTURE
                || cardType == DISPLAY_CARD.JOKE
                || cardType == DISPLAY_CARD.TESTING
                || cardType == DISPLAY_CARD.AUDIO
                || cardType == DISPLAY_CARD.VIDEO_LIVE_LARGE
                || cardType == DISPLAY_CARD.VIDEO_LIVE_SMALL
                || cardType == DISPLAY_CARD.VIDEO_LIVE_FLOW
                || cardType == DISPLAY_CARD.GALLERY_CARD
                || cardType == DISPLAY_CARD.GALLERY_CARD2
                || cardType == DISPLAY_CARD.COLUMN_CARD
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_OUTSIDE_CHANNEL_BIG_PICTURE
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_OUTSIDE_CHANNEL_SMALL_PICTURE
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_3_EQUAL_SIZE_PICTURES
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_1_PICTURE
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_4_PICTURES
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_1_ABOVE_2_BOTTOM
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_2_ABOVE_1_BOTTOM
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_2_PICTURES
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_THEME_CHANNEL_BIG_PICTURE
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_1_LEFT_2_RIGHT
                || cardType == DISPLAY_CARD.RECOMMENDED_APPS_IN_HORIZONTAL
                || cardType == DISPLAY_CARD.APP_RECOMMEND_CHECK
                || cardType == DISPLAY_CARD.PICTURE_GALLERY_HOMEBOY_PICTURES
                || cardType == DISPLAY_CARD.APP_RECOMMEND_CHECK_WITH_RED_ICON
                || cardType == DISPLAY_CARD.APP_RECOMMEND_ONE_IMAGE_CARD
                || cardType == DISPLAY_CARD.APP_RECOMMEND_FOUR_IMAGE_CARD
                || cardType == DISPLAY_CARD.MOVIE_REVIEW_CARD
                || cardType == DISPLAY_CARD.MOVIE_DESC_CARD
                || cardType == DISPLAY_CARD.MOVIE_TRAILER_CARD
                || cardType == DISPLAY_CARD.HIT_MOVIE_CARD
                || cardType == DISPLAY_CARD.NOVEL_PROMOTION
                || cardType == DISPLAY_CARD.FENDA_CARD
                || cardType == DISPLAY_CARD.AUDIO_LIST
                || cardType == DISPLAY_CARD.VIDEO_TOPIC
                || cardType == DISPLAY_CARD.JOKE_WITH_PIC //添加带卡片的joke样式
                || cardType == DISPLAY_CARD.WE_MEDIA_CARD
                || cardType == DISPLAY_CARD.JOKE_TITLE_CARD
                || cardType == DISPLAY_CARD.TOMORROW_WEATHER_CARD
                || cardType == DISPLAY_CARD.JOKE_GIF
                || cardType == DISPLAY_CARD.VIDEO_KUAISHOU
                || cardType == DISPLAY_CARD.WEIBO_PIC_CARD
                || cardType == DISPLAY_CARD.WEIBO_WORD_CARD
                || cardType == DISPLAY_CARD.WEIBO_GIF_CARD
                || cardType == DISPLAY_CARD.LIVE_SPORTS_TWO_CARD
                || cardType == DISPLAY_CARD.BEST_ARTICLE_REC_CARD
                || cardType == DISPLAY_CARD.JOKE_WITH_MULTI_PIC
                || cardType == DISPLAY_CARD.FOCUS_IMAGE_NEWS_CARD
                || cardType == DISPLAY_CARD.JIKE_PIC_CARD
                || cardType == DISPLAY_CARD.VIDEO_JIKE
                || cardType == DISPLAY_CARD.THEME_CHANNEL_MULTI_CARD
                || cardType == DISPLAY_CARD.THEME_CHANNEL_SMALL_IMAGE
                || cardType == DISPLAY_CARD.THEME_CHANNEL_NO_IMAGE
                || cardType == DISPLAY_CARD.THEME_CHANNEL_VIDEO
                || cardType == DISPLAY_CARD.WENDA_CARD
                || cardType == DISPLAY_CARD.WENDA_ZHIHU_MULTIPIC_CARD
                || cardType == DISPLAY_CARD.WENDA_ZHIHU_MULTIPIC_CARD2
                || cardType == DISPLAY_CARD.WENDA_ZHIHU_NEWSLIKE_CARD
                || cardType == DISPLAY_CARD.SEARCH_RESULT_WEMEDIA
                || cardType == DISPLAY_CARD.JIKE_NEWS_CARD
                || cardType == DISPLAY_CARD.KUAI_XUN_INTERNET_FLASH
                || cardType == DISPLAY_CARD.THEMECHANNEL_COMPLEX_GRAPHIC
                || cardType == DISPLAY_CARD.THEMECHANNEL_JOKE_AND_BEAUTY
                || cardType == DISPLAY_CARD.WORLDCUP_VIDEO
                ) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(cardType);
        builder.append(", id = ");
        if (data != null) {
            builder.append(data.toString());
        }
        return builder.toString();
    }
}
