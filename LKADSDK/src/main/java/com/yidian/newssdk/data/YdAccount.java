package com.yidian.newssdk.data;

import android.text.TextUtils;

import com.yidian.newssdk.data.pref.GlobalAccount;

import org.json.JSONObject;

/**
 * Created by chenyichang on 2018/5/19.
 */

public class YdAccount {

    private String userid;

    private String  cookie;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void fromJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }

        userid = jsonObject.optString("yd_userid");
        cookie = jsonObject.optString("cookie");
    }

    public void fromJson(JSONObject jsonObject, boolean save2Sp) {
        if (jsonObject == null) {
            return;
        }

        userid = jsonObject.optString("yd_userid");
        cookie = jsonObject.optString("cookie");

        if (save2Sp && !TextUtils.isEmpty(userid)) {
            GlobalAccount.saveYduserId(userid);
        }

        if (save2Sp && !TextUtils.isEmpty(cookie)) {
            GlobalAccount.saveCookie(cookie);
        }
    }
}
