package com.yidian.newssdk.data.card.base;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by liuyue on 15/12/2.
 */
public class CardDisplayInfo implements Serializable {
    static final long serialVersionUID = 1L;
    public static String TAG = CardDisplayInfo.class.getSimpleName();
    //通用字段
    public String headerName;
    public String headerNameColor;
    public String headerTitle;
    public String headerTitleColor;
    public String headerIcon;
    public String footerTitle;
    public String action;
    public String actionType;
    public String targetName; //如频道名字
    public String targetImageUrl; //如频道图片URL
    public String description = null; //兼容旧版本
    public String name = null; //卡片名字, 用于日志
    public String adImage = null; // 顶部的广告图
    public String headerImage = null; //  headerimage
    public String headerBgImage = null;// 背景图
    public int targetDisplayFlag = 0;// 用来控制实现展示等功能。

    public static void fromJSON(Card card, JSONObject properties) {

        if (properties == null || card == null) {
            return;
        }
        CardDisplayInfo info = new CardDisplayInfo();
        JSONObject cardInfo = properties.optJSONObject("cardDisplayInfo");
        if (cardInfo != null) {
            info.headerName = cardInfo.optString("headerName");
            info.headerNameColor = cardInfo.optString("headerNameColor");
            info.headerTitle = cardInfo.optString("headerTitle");
            info.headerTitleColor = cardInfo.optString("headerTitleColor");
            info.headerIcon = cardInfo.optString("headerIcon");
            info.footerTitle = cardInfo.optString("footerTitle");
            info.actionType = cardInfo.optString("actionType");
            info.action = cardInfo.optString("action");
            info.targetName = cardInfo.optString("targetName");
            info.targetImageUrl = cardInfo.optString("targetImageUrl");

            info.adImage = cardInfo.optString("adImage");
            info.headerImage = cardInfo.optString("headerImage");
            info.headerBgImage = cardInfo.optString("headerBgImage");
            info.targetDisplayFlag = cardInfo.optInt("targetDisplayFlag");
            card.mDisplayInfo = info;
        }
    }


    public static final String SINGLE_PIC = "singlepic";
    public static final String THREE_PIC = "threepic";
    public static final String BIG_PIC = "bigpic";


    public static final int AD_TEMPLATE3 = 3;
    public static final int AD_TEMPLATE4 = 4;
    public static final int AD_TEMPLATE40 = 40;
    public static final int AD_TEMPLATE15 = 15;


}
