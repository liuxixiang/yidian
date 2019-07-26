package com.yidian.newssdk.data.card.joke;

import android.database.Cursor;
import android.text.TextUtils;

import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.card.base.ContentCard;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by patrickleong on 11/25/15.
 */
public class JokeCard extends ContentCard implements Serializable{

    private static final long serialVersionUID = 3L;

    public List<Comment> amazing_comments;
    public List<String> keywords;
    private boolean isEditAble;
    private boolean isSelected;
//    public AuthorInfo mAuthorInfo; // 段子的头部信息，因为正文页的段子，后端传递不过来 displayScope,所以把头部信息的解析从 UgcJokeCard ,放到了 JokeCard,使用的时候需要判定是否为空。

    public JokeCard() {
        contentType = Card.CARD_JOKE;
    }


    public boolean isEditAble() {
        return isEditAble;
    }

    public void setEditAble(boolean editAble) {
        isEditAble = editAble;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static JokeCard fromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        JokeCard card = new JokeCard();
        JokeCard.fromJSON(card, json);
        return card;
    }

    public static JokeCard fromJSON(JokeCard card, JSONObject json) {
        if (json == null) {
            return null;
        }
        // 父类解析
        ContentCard.fromJSON(card, json);
//        if (TextUtils.isEmpty(card.summary)) {  // 如果后端不返回summary ,jokecard 认为无效
//            return null;
//        }
//        if(card.imageUrls.size() > 0) {
//            parseJokeCardPic(json, card);
//        }

        if(TextUtils.isEmpty(card.image)){
            card.displayType = Card.DISPLAY_TYPE_JOKE;
        }
//        parseAuthorInfo(json, card); // 解析头部信息
        return card;
    }


//    private static void parseAuthorInfo(JSONObject json, JokeCard card) {
//        JSONObject jsonObject = json.optJSONObject("author_info");
//        if (jsonObject != null) {
//            String jsonString = jsonObject.toString();
//            if (!TextUtils.isEmpty(jsonString)) {
//                card.mAuthorInfo = new Gson().fromJson(jsonString, UgcJokeCard.AuthorInfo.class);
//            }
//        }
//    }

    @Override
    public boolean isIntegral() {
        return !TextUtils.isEmpty(summary) || !TextUtils.isEmpty(image);
    }


}
