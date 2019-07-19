package com.yidian.newssdk.core.detail.article.comment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yidian.newssdk.IntentConstants;
import com.yidian.newssdk.R;
import com.yidian.newssdk.core.detail.article.common.CommonNewsActivity;
import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.news.INewsType;

import static com.yidian.newssdk.data.news.INewsType.NORMAL_NEWS_URL;


/**
 * @author zhangzhun
 * @date 2018/5/25
 */

public class YdCommentActivity extends CommonNewsActivity<YdCommnetPresenter> implements YdCommentContractView {

    private TextView mToolbarTxt;

    @Override
    protected int getCustomToolbarLayoutId() {
        return R.layout.ydsdk_toolbar_common_layout;
    }

    @Override
    protected int attachLayoutId() {
        return R.layout.ydsdk_activity_web;
    }

    @Override
    public void initPresenter() {
        new YdCommnetPresenter(this);
    }

    @Override
    protected Bundle buildShareData() {
        Bundle bundle = new Bundle();
        return bundle;
    }

    @Override
    protected void initView() {
        super.initView();
        mToolbarTxt = findViewById(R.id.toolbar_text);
        mToolbarTxt.setText("评论");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public static void startCommentActivity(Context context, Card card) {

        try {
            Intent intent = new Intent(context, YdCommentActivity.class);
            intent.putExtra(IntentConstants.CARD, card);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startCommentActivity(Activity activity, @INewsType.NEWS_STYLE int newsStyle, String urlParams) {

        try {
            Intent intent = new Intent(activity, YdCommentActivity.class);
            intent.putExtra(INewsType.NEWS_STYLE, newsStyle);
            intent.putExtra(NORMAL_NEWS_URL, urlParams);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
