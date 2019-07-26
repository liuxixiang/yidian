package com.linken.newssdk.core.newweb;

import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.linken.newssdk.utils.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 负责提供注入检查、第三方接口判断
 * Created by patrickleong on 5/19/16.
 */
public class ThirdPartyManager {
    public HashSet<String> mAPIMethodsWhiteListSet = new HashSet<>();
    public HashSet<String> mAPISitesWhiteListSet = new HashSet<>();
    public ArrayList<Pattern> mAPISitesWhiteListPatternList = new ArrayList<>();

    public ArrayList<Pattern> mWhiteListUrlPatternList = new ArrayList<>();

    public String mInjectScript = "javascript:void(0);";

    private static final String WHITE_LIST_PATTERN = "^(http[s]?://)?([a-zA-Z0-9_-]+\\.)*?%s/?.*";

    ThirdPartyManager(){
    }

    public void loadConfigurations(WebAppManager webAppManager, JSONObject configJSON){
        if (configJSON != null) {
            String[] apiMethodsWhiteList = JsonUtil.parseJSONString(configJSON.optJSONArray("thirdparty_apilist"));
            for (String s : apiMethodsWhiteList) {
                mAPIMethodsWhiteListSet.add(s);
            }
            String[] apiSitesWhiteList = JsonUtil.parseJSONString(configJSON.optJSONArray("thirdparty_whitelist"));
            for (String s : apiSitesWhiteList) {
                mAPISitesWhiteListSet.add(s);
                mAPISitesWhiteListPatternList.add(Pattern.compile(String.format(WHITE_LIST_PATTERN, s)));
            }
            String[] whiteList = JsonUtil.parseJSONString(configJSON.optJSONArray("whitelist"));
            if (whiteList != null) {
                loadWhiteListURLs(whiteList);
            }
            JSONObject routes = configJSON.optJSONObject("routes");
            if (routes != null) {
                webAppManager.parseLocationProperties(routes);
            }
        }
    }

    public boolean matchWhiteListURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        for (Pattern pattern : mWhiteListUrlPatternList) {
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }


    private void loadWhiteListURLs(String[] whiteListUrl) {
        for (String url : whiteListUrl) {
            mWhiteListUrlPatternList.add(Pattern.compile(String.format(WHITE_LIST_PATTERN, url)));
        }
    }


    public String getInjectScript() {
        return mInjectScript;
    }


    public boolean hasMethodAccessRight(@NonNull String host, String methodName) {
        if (!mAPIMethodsWhiteListSet.contains(methodName)) {
            return false;
        }

        for (Pattern pattern : mAPISitesWhiteListPatternList) {
            Matcher matcher = pattern.matcher(host);
            if (matcher.matches()) {
                return true;
            }
        }

        return false;
    }


    public boolean isInternalSite(@NonNull String url) {
        return matchWhiteListURL(url);
    }
}
