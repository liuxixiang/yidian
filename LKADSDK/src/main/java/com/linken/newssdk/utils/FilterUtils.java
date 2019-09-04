package com.linken.newssdk.utils;

import com.linken.newssdk.NewsFeedsSDK;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterUtils {
    public static boolean filterRegex(String title) {
        List<String> filterRegexs = NewsFeedsSDK.getInstance().getFilterRegex();
        //过滤
        if (filterRegexs != null && filterRegexs.size() > 0) {
            for (String filterRegex : filterRegexs) {
                Pattern pattern = Pattern.compile(filterRegex);
                Matcher matcher = pattern.matcher(title);
                if (!matcher.matches()) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
}
