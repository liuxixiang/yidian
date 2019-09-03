package com.linkin.mileage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches {

    public static void main(String args []) {
        String str = "JHJSADHJFHJ币快报SJDFHJA ";
        String pattern = ".*(趣步|币快报).*";

        Pattern r = Pattern.compile(pattern);
        System.out.println(r.toString());
        Matcher m = r.matcher(str);
        System.out.println(m.matches());
        System.out.println(setFilter(new String[]{"男","女","人妖"}));
    }

    /**
     * 设置多个关键字过滤
     *
     * @param filters
     * @return
     */
    public static String setFilter(String[] filters) {
        if (filters != null && filters.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < filters.length; i++) {
                sb.append(filters[i]);
                if (i < filters.length - 1) {
                    sb.append("|");
                }
            }
            return  ".*(" + sb.toString() + ").*";
        }
        return "";
    }


}
