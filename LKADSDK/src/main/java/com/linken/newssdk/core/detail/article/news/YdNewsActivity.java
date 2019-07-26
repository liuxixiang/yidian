package com.linken.newssdk.core.detail.article.news;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.linken.newssdk.IntentConstants;
import com.linken.newssdk.R;
import com.linken.newssdk.core.detail.article.common.CommonNewsActivity;
import com.linken.newssdk.core.newweb.SimpleWebChromeClient;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.news.INewsType;

import static com.linken.newssdk.data.news.INewsType.NORMAL_NEWS_URL;


/**
 * @author zhangzhun
 * @date 2018/5/25
 */

public class YdNewsActivity extends CommonNewsActivity<YdNewsPresenter> implements YdNewsContractView, SimpleWebChromeClient.OnProgressCallback {

    private ProgressBar mProgress;

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
        new YdNewsPresenter(this);
    }

    @Override
    protected Bundle buildShareData() {
        Bundle bundle = new Bundle();
        return bundle;
    }



    public void setHeader(String title) {
        if (!TextUtils.isEmpty(title)) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mToolbar.getToolTextView(), "alpha", 0.0f, 1.0f);
            mToolbar.setToolBarTxt(title);
            objectAnimator.setDuration(700L);
            objectAnimator.start();
        } else {
            mToolbar.setToolBarTxt(title);
        }
    }


    @Override
    protected void initView() {
        super.initView();
        mProgress = findViewById(R.id.progressBar);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        if (articleType == INewsType.STYLE_THIRD_PARTY || articleType == INewsType.STYLE_NEWS_EXTERNAL) {
            mProgress.setVisibility(View.VISIBLE);
            mWebView.setChromeClientCallback(this);
        }
    }

    public static void startNewsActivity(Context context, Card card) {

        try {
            Intent intent = new Intent(context, YdNewsActivity.class);
            intent.putExtra(IntentConstants.CARD, card);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startNewsActivity(Activity activity, @INewsType.NEWS_STYLE int newsStyle, String urlParams) {

        try {
            Intent intent = new Intent(activity, YdNewsActivity.class);
            intent.putExtra(INewsType.NEWS_STYLE, newsStyle);
            intent.putExtra(NORMAL_NEWS_URL, urlParams);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onProgress(int progress) {
        if (progress > 98) {
            this.mProgress.setVisibility(View.GONE);
        } else {
            mProgress.setProgress(progress);
        }
    }
}
