package com.linken.newssdk.core.detail.article.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.linken.newssdk.IntentConstants;
import com.linken.newssdk.R;
import com.linken.newssdk.core.detail.article.common.CommonNewsActivity;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.news.INewsType;
import com.linken.newssdk.utils.StatusBarUtil;
import com.linken.newssdk.utils.support.NetworkHelper;
import com.linken.newssdk.utils.DisplayUtils;

import static com.linken.newssdk.data.news.INewsType.NORMAL_NEWS_URL;


/**
 * @author zhangzhun
 * @date 2018/5/25
 */

public class YdVideoActivity extends CommonNewsActivity<YdVideoPresenter> implements YdVideoContractView, NetworkHelper.OnWifiCallBackListener {


    @Override
    protected int getCustomToolbarLayoutId() {
        return R.layout.ydsdk_toolbar_common_video_layout;
    }

    @Override
    protected int attachLayoutId() {
        return R.layout.ydsdk_activity_web2;
    }

    @Override
    public void initPresenter() {
        new YdVideoPresenter(this);
    }

    @Override
    protected Bundle buildShareData() {
        Bundle bundle = new Bundle();
        return bundle;
    }

    @Override
    protected void initParams() {
        super.initParams();
        DisplayUtils.init(YdVideoActivity.this);
        NetworkHelper.getInstance().registerWifiCallbackListener(this);
    }

    @Override
    public void onDestroy() {
        NetworkHelper.getInstance().unRegisterWifiCallbackListener(this);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState ) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }

    private int lastOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        boolean needChange = false;
        if (newConfig.orientation != lastOrientation) {
            lastOrientation = newConfig.orientation;
            needChange = true;
        }
        if (mWebView != null && needChange) {
            mWebView.orientationChanged(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onBackPressed() {
        if (mCurrentScreenMode == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            changeOrientation(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void changeOrientation(boolean usingPortrait) {
        super.changeOrientation(usingPortrait);
        if (mCurrentScreenMode == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            StatusBarUtil.showStatusBar(YdVideoActivity.this);
        } else if (mCurrentScreenMode == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            StatusBarUtil.hideStatusBar(YdVideoActivity.this);
        }
    }

    public static void startVideoActivity(Context context, Card card) {

        try {
            Intent intent = new Intent(context, YdVideoActivity.class);
            intent.putExtra(IntentConstants.CARD, card);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startVideoActivity(Activity activity, @INewsType.NEWS_STYLE int newsStyle, String urlParams) {

        try {
            Intent intent = new Intent(activity, YdVideoActivity.class);
            intent.putExtra(INewsType.NEWS_STYLE, newsStyle);
            intent.putExtra(NORMAL_NEWS_URL, urlParams);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWifiChange(boolean isWifi) {
        mWebView.onWifiChannged(isWifi);
    }
}
