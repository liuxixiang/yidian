package com.yidian.newssdk.data.card.pic;


import org.json.JSONObject;
import java.io.Serializable;

/**
 * Created by liuyue on 2017/3/28.
 */

public class WeMediaPictureGalleryCard extends PictureGalleryCard implements Serializable {
    static final long serialVersionUID = 1L;

    public static PictureGalleryCard fromJSON(JSONObject json) {
        if (json == null) return null;
        WeMediaPictureGalleryCard card = new WeMediaPictureGalleryCard();
        PictureGalleryCard.parseCardFields(json, card);
        return card;
    }


}
