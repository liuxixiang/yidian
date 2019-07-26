package com.yidian.newssdk.data.card.base;

import android.text.TextUtils;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.data.card.joke.JokeCard;
import com.yidian.newssdk.data.card.news.News;
import com.yidian.newssdk.data.card.pic.PictureGalleryCard;
import com.yidian.newssdk.data.card.pic.WeMediaPictureGalleryCard;
import com.yidian.newssdk.data.card.video.VideoLiveCard;

import org.json.JSONObject;

import static com.yidian.newssdk.data.card.base.CardDisplayInfo.BIG_PIC;
import static com.yidian.newssdk.data.card.base.CardDisplayInfo.SINGLE_PIC;
import static com.yidian.newssdk.data.card.base.CardDisplayInfo.THREE_PIC;
import static com.yidian.newssdk.data.card.base.CardDisplayInfo.serialVersionUID;


/**
 * 创建Card的辅助类
 */
public class CardHelper {
    public static String TAG = CardHelper.class.getSimpleName();

    public static Card parseCard(JSONObject json) {
        if (json == null) {
            return null;
        }

        String contentType = json.optString("ctype");
        Card card;

        if (Card.CTYPE_NORMAL_NEWS.equals(contentType)) {
            card = News.fromJSON(json);
            card.displayType = Card.DISPLAY_TYPE_ONE_IMAGE;
            if (TextUtils.equals(SINGLE_PIC, card.dtype)) {
                card.displayType = Card.DISPLAY_TYPE_ONE_IMAGE;
            } else if (TextUtils.equals(THREE_PIC, card.dtype)) {
                card.displayType = Card.DISPLAY_TYPE_MULTI_IMAGE;
            } else if (TextUtils.equals(BIG_PIC, card.dtype)) {
                card.displayType = Card.DISPLAY_TYPE_BIG_IMAGE;
            }
            //因为存在prefetch的机制,所以需要将imageUrls赋值给coverImages作为卡片显示的封面图
            if (card != null) {
                ((News) card).copyCoverImages();
            }
        } else if (Card.CTYPE_VIDEO_CARD.equals(contentType)
                || Card.CTYPE_VIDEO_LIVE_CARD.equals(contentType)) {
            card = VideoLiveCard.fromJSON(json);
            card.displayType = Card.DISPLAY_TYPE_VIDEO_BIG;
        } else if (Card.CTYPE_PICTURE_GALLERY.equals(contentType)) {
            card = WeMediaPictureGalleryCard.fromJSON(json);
            card.displayType = Card.DISPLAY_TYPE_PICTURE_GALLERY_OUTSIDE_CHANNEL_BIG_IMAGE;
            if (TextUtils.equals(SINGLE_PIC, card.dtype)) {
                card.displayType = Card.DISPLAY_TYPE_PICTURE_GALLERY_OUTSIDE_CHANNEL_SMALL_IMAGE;
            } else if (TextUtils.equals(BIG_PIC, card.dtype)) {
                card.displayType = Card.DISPLAY_TYPE_PICTURE_GALLERY_OUTSIDE_CHANNEL_BIG_IMAGE;
            }
        } else if (Card.CTYPE_ADVERTISEMENT.equals(contentType)) {
            card = AdvertisementCard.fromJSON(json);
        }

        else {
            //不支持的类型，直接返回NULL
            return null;
        }

        if (card != null) {
            card.cType = contentType;
            if (card != null) {
                if (TextUtils.isEmpty(card.id)) {
                    String tmpString = card.cType + card.title + card.url;
                    card.id = String.valueOf(tmpString.hashCode());

                }
            }
        }

        return card;
    }

}
