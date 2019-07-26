package com.linken.newssdk.widget.newshare;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;

import com.linken.newssdk.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/1/22
 */

public class FactoryShareItem {

    public static final int REFRESH = 1;


    @IntDef({REFRESH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShareEnum {}

    public static List<ShareItem> createShareItems(Context context) {
        ShareItem moments = new ShareItem(REFRESH, -100, "刷新", ContextCompat.getDrawable(context, R.drawable.ydsdk_share_refresh));
//        Item weChat = new Item(WECHAT, -200, "微信好友", ContextCompat.getDrawable(context, R.drawable.video_player_fenxiang_weixin));
//        Item qq = new Item(QQ, -300, "QQ好友", ContextCompat.getDrawable(context, R.drawable.video_player_fenxiang_qq));
//        Item qZone = new Item(QZONE, -200, "QQ空间", ContextCompat.getDrawable(context, R.drawable.video_player_fenxiang_qqkj));
//        Item weibo = new Item(WEIBO, -100, "微博", ContextCompat.getDrawable(context, R.drawable.video_player_fenxiang_weibo));
        List<ShareItem> list = new ArrayList<>(5);
        list.add(moments);
//        list.add(weChat);
//        list.add(qq);
//        list.add(qZone);
//        list.add(weibo);
        return list;
    }
}
