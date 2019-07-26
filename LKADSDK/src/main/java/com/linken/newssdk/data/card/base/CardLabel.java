package com.linken.newssdk.data.card.base;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by liuyue on 2017/7/27.
 */

public class CardLabel implements Serializable {
    static final long serialVersionUID = 1L;

    public String textColor;
    public String bgColor;
    public String borderColor;
    public String text;

    public static CardLabel fromJSON(JSONObject obj) {
        if(obj == null) {
            return null;
        }

        CardLabel label = new CardLabel();
        label.text = obj.optString("text");
        label.textColor = obj.optString("text_color");
        label.borderColor = obj.optString("border_color");
        label.bgColor = obj.optString("background_color");
        return label;
    }

}
