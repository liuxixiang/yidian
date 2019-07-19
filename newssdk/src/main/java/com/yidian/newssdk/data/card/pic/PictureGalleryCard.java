package com.yidian.newssdk.data.card.pic;

import android.text.TextUtils;

import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.card.base.ContentCard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yangjuan on 16/4/27. // 图集卡片
 */
public class PictureGalleryCard extends ContentCard implements Serializable{

    private static final long serialVersionUID = 5L;
    private static final String HOTFLAG = "tag_hotslides";

    public int gallery_items; // 图集显示的内容信息解析
    public ArrayList<RelatedDocsEntry> relatedGallery;
    public int width = -1;// 信息流封面图的宽度
    public int height = -1;// 信息流封面图的高度
    // 自媒体相关信息
    public String sourceId;
    public String sourceFromId;
    public String sourcePic;
    public String sourceType;
    public String sourceName;
    // 是否显示 hot 角标
    public boolean showHotFlag = false;

    public PictureGalleryCard() {
        super();
        contentType = Card.CARD_PICTURE_GALLERY;
    }

    public static PictureGalleryCard fromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        PictureGalleryCard card = new PictureGalleryCard();
        parseCardFields(json, card);
        return card;
    }

    protected static void parseCardFields(JSONObject json, PictureGalleryCard card) {
        ContentCard.fromJSON(card, json);
        card.gallery_items = json.optInt("gallery_item_count", 0);

        JSONArray relatedDocs = json.optJSONArray("related_docs");
        if (relatedDocs != null) {
            ArrayList<RelatedDocsEntry> relatedGallery = new ArrayList<>();
            for (int i = 0; i < relatedDocs.length(); i++) {
                JSONObject obj = relatedDocs.optJSONObject(i);
                if (obj != null) {
                    relatedGallery.add(new RelatedDocsEntry(obj.optString("image"), obj.optString("title"),
                            obj.optString("docid"), obj.optString("impid")));
                }
            }
            card.relatedGallery = relatedGallery;
        }

        // 获取对应封面图的长度和宽度
        card.height = json.optInt("height");
        card.width = json.optInt("width");
//        if (card.displayType == 56) {
//            // 获取对应封面图的长度和宽度
//            JSONArray imageSize = json.optJSONArray("image_info");
//            if (imageSize != null && !TextUtils.isEmpty(card.imageUrls.get(0))) { // 只需要第一副图像
//                JSONObject obj = imageSize.optJSONObject(0);
//                if (obj != null) {
//                    JSONObject sizeInformation = obj.optJSONObject(card.imageUrls.get(0));
//                    if (sizeInformation != null) {
//                        card.height = sizeInformation.optInt("height");
//                        card.width = sizeInformation.optInt("width");
//                    }
//                }
//            }
//
//             //获取对应封面图的长度和宽度
//            JSONObject imageSize = json.optJSONObject("image_info");
//            if (imageSize != null && !TextUtils.isEmpty(card.imageUrls.get(0))) { // 只需要第一副图像
//                if (imageSize != null) {
//                    JSONObject sizeInformation = imageSize.optJSONObject(card.imageUrls.get(0));
//                    if (sizeInformation != null) {
//                        card.height = sizeInformation.optInt("height");
//                        card.width = sizeInformation.optInt("width");
//                    }
//                }
//            }
//
//
//            JSONObject weMediaInfo = json.optJSONObject("wemedia_info");
//            if (weMediaInfo != null) {
//                card.sourceId = weMediaInfo.optString("channel_id");
//                card.sourceFromId = weMediaInfo.optString("fromId");
//                card.sourceType = weMediaInfo.optString("type");
//                card.sourcePic = weMediaInfo.optString("image");
//                card.sourceName = weMediaInfo.optString("name");
//            }
//
//            JSONArray tags = json.optJSONArray("tags");
//            if (tags != null) {
//                for (int i = 0; i < tags.length(); i++) {
//                    String tag = tags.optString(i);
//                    if (!TextUtils.isEmpty(tag) && HOTFLAG.equalsIgnoreCase(tag)) {
//                        card.showHotFlag = true;
//                        break;
//                    }
//                }
//            }
//
//        }

        if (card.height < 10) {
            card.height = 600;
            card.width = 420;
        }
    }

    @Override
    public String toString() {
        return "[id = " + id + ", cType = " + cType + "]";
    }

    @Override
    public boolean isIntegral() {
        if (Card.MEDIA_TYPE_COOPERATE_PAGE == mediaType) {
            return !TextUtils.isEmpty(url);
        }
        return super.isIntegral();
    }

    public static class ImageEntry implements Serializable {
        private static final long serialVersionUID = 1L;

        public final String image;
        public final String description;

        public ImageEntry(String image, String description) {
            this.image = image;
            this.description = description;
        }
    }

    public static class RelatedDocsEntry implements Serializable {
        private static final long serialVersionUID = 2L;
        public final String image;
        public final String title;
        public final String docId;
        public final String impId;

        public RelatedDocsEntry(String image, String title, String docId, String impId) {
            this.image = image;
            this.title = title;
            this.docId = docId;
            this.impId = impId;
        }
    }

}