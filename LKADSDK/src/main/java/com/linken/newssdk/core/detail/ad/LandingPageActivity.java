package com.linken.newssdk.core.detail.ad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.linken.ad.data.AdvertisementCard;
import com.linken.ad.data.RewardCard;
import com.linken.newssdk.IntentConstants;
import com.linken.newssdk.NewsFeedsSDK;
import com.linken.newssdk.R;
import com.linken.newssdk.SDKContants;
import com.linken.newssdk.core.newweb.LiteWebView;
import com.linken.newssdk.core.newweb.SimpleWebChromeClient;
import com.linken.newssdk.data.ad.db.AdvertisementDbUtil;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.export.INewsInfoCallback;
import com.linken.newssdk.utils.DensityUtil;
import com.linken.newssdk.utils.LogUtils;
import com.linken.newssdk.utils.SPUtils;
import com.linken.newssdk.utils.TimeUtil;
import com.linken.newssdk.widget.newshare.FactoryShareItem;
import com.linken.newssdk.widget.newshare.OnShareClickListener;
import com.linken.newssdk.widget.newshare.ShareFragment;
import com.linken.newssdk.widget.views.CustomCountLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zhangzhun
 * @date 2018/5/10
 */

public class LandingPageActivity extends FragmentActivity implements View.OnClickListener, SimpleWebChromeClient.OnProgressCallback, LiteWebView.PageLoadListener {

    private static final String TAG = LandingPageActivity.class.getSimpleName();
    String mURL;
    private ProgressBar mProgress;
    private boolean mPauseAudio = false;
    private static final String PAUSED_ALL_SOUND = "javascript:Array.prototype.map.call(document.getElementsByTagName('audio'), function(v){if(v){v.pause();}});void(0);";
    private static final String RESUMED_ALL_SOUND = "javascript:Array.prototype.map.call(document.getElementsByTagName('audio'), function(v){if(v.paused){v.play();}});void(0);";
    private ImageButton mbackButton;
    private ImageView mMoreView;
    private ImageButton mCloseButton;

    private LiteWebView mWebView;
    private long landingPageStartTime = 0L;//记录广告landingPage开始时间
    private long landingPageEndTime = 0L;//记录广告landingPage结束时间
    private long duration = 0L;
    private CustomCountLayout mCustomCountLayout;
    private String mType = INewsInfoCallback.TYPE_AD;
    private int countDown = 15;//倒计时时间
    private int rewardNum;//奖励个数
    private boolean isFristPageFinish;//网页第一次加载完成
    private String title;
    private String docid;


    public static void startActivity(Activity activity, Intent intent) {
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            LogUtils.d(TAG, e.getMessage());
        }
    }

    public static void startActivity(Activity activity, AdvertisementCard card, String url, long cid) {
        try {
            Intent intent = new Intent(activity, LandingPageActivity.class);
            intent.putExtra(IntentConstants.AD_CARD, card);
            intent.putExtra(IntentConstants.URL, url);
            intent.putExtra(IntentConstants.CID, cid);
            activity.startActivity(intent);
        } catch (Exception e) {
            LogUtils.d(TAG, e.getMessage());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ydsdk_activity_ad_page);
        handleBundle();
        initWidget();
        startShowContent();
    }

    private void handleBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            AdvertisementCard adCard = (AdvertisementCard) intent.getSerializableExtra(IntentConstants.AD_CARD);
            mURL = intent.getStringExtra(IntentConstants.URL);
            if (!TextUtils.isEmpty(mURL)) {
                Uri uri = Uri.parse(mURL);
                docid = uri.getQueryParameter("aid");
            }
            if (adCard != null) {
                if (!TextUtils.isEmpty(adCard.channel)) {
                    SDKContants.channel = adCard.channel;
                }
                if (Card.CTYPE_VIDEO_LIVE_CARD.equals(adCard.cType) || Card.CTYPE_VIDEO_CARD.equals(adCard.cType)) {
                    mType = INewsInfoCallback.TYPE_VIDEO;
                } else if (Card.CTYPE_ADVERTISEMENT.equals(adCard.cType) || TextUtils.isEmpty(adCard.cType)) {
                    mType = INewsInfoCallback.TYPE_AD;
                } else {
                    mType = INewsInfoCallback.TYPE_ARTICLE;
                }
                docid = adCard.getAid() +"";
            }
        }
    }

    protected void initWidget() {
        mCloseButton = findViewById(R.id.closeBtn);
        mCloseButton.setOnClickListener(this);
        mbackButton = findViewById(R.id.btnBack);
        mbackButton.setOnClickListener(this);
        mMoreView = findViewById(R.id.more_button);
        mMoreView.setOnClickListener(this);
        mWebView = findViewById(R.id.ad_webView);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.init(null, false);
        mWebView.setChromeClientCallback(this);
        mWebView.setReloadUrlListener(new LiteWebView.ReloadUrlListener() {
            @Override
            public boolean reloadUrl() {
                mWebView.loadUrl(mURL);
                return true;
            }
        });
        mWebView.setPageLoadListener(this);
        mProgress = findViewById(R.id.progressBar);
        mProgress.setVisibility(View.VISIBLE);
        CookieManager.getInstance().setAcceptCookie(true);
    }


    public void startShowContent() {
        if (!TextUtils.isEmpty(mURL)) {
            loadUrl(mURL);
        }
    }

    public void loadUrl(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }

    @Override
    public void onPause() {
        if (mWebView != null) {
            mWebView.loadUrl(PAUSED_ALL_SOUND);
            mPauseAudio = true;
            if (Build.VERSION.SDK_INT >= 11) {
                if (mWebView != null) {
                    mWebView.onPause();
                    // mWebView.pauseTimers();
                }
            }
        }
//        if (adCard != null) {//息屏就要报事件
//            Long stayTime = System.currentTimeMillis() - landingPageStartTime;
//            if (stayTime > 100) {//防止息屏之后按电源键导致的伪stay
//                long cid = System.currentTimeMillis();
//                AdvertisementUtil.reportLandingPageStayEvent(adCard, cid, mURL, stayTime);
//            }
//        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            if (mPauseAudio) {
                mWebView.loadUrl(RESUMED_ALL_SOUND);
                mPauseAudio = false;
            }
            if (Build.VERSION.SDK_INT >= 11) {
                mWebView.onResume();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (landingPageStartTime != 0) {
            duration = landingPageEndTime - System.currentTimeMillis();
        }

        if (mCustomCountLayout != null) {
            mCustomCountLayout.startCount();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        landingPageEndTime = System.currentTimeMillis();
        if (mCustomCountLayout != null) {
            mCustomCountLayout.endCount();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        if (landingPageStartTime != 0) {
            duration = System.currentTimeMillis() - landingPageStartTime + duration;
        }
        newsInfoCallback(INewsInfoCallback.TYPE_EVENT_DURATION, rewardNum, countDown, (int) (duration / 1000));

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.closeBtn) {
            finish();
        } else if (v.getId() == R.id.more_button) {
            ShareFragment shareFragment = new ShareFragment();
            shareFragment.show(getSupportFragmentManager(), null);
            shareFragment.setOnShareClickListener(new OnShareClickListener() {
                @Override
                public void onShareClick(int clickType) {
                    switch (clickType) {
                        case FactoryShareItem.REFRESH:
                            mWebView.reload();
                            break;
                        default:
                            break;
                    }
                }
            });
        } else if (v.getId() == R.id.btnBack) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onProgress(int progress) {
        mProgress.setVisibility(View.VISIBLE);
        if (progress > 98) {
            this.mProgress.setVisibility(View.GONE);
        } else {
            mProgress.setProgress(progress);
        }

    }

    @Override
    public void onPageLoadFinished() {
        if (mWebView.canGoBack() && mCloseButton != null) {
            mCloseButton.setVisibility(View.VISIBLE);
        }
        addCountLayout();
        if (!isFristPageFinish) {
            isFristPageFinish = true;
            landingPageStartTime = System.currentTimeMillis();//息屏之后重新计时
        }

        title = mWebView.getTitle();
    }

    @Override
    public void shouldInterceptRequest(WebView view, String url) {

    }

    private void newsInfoCallback(int event, int rewardNum, int countDown, int realDuration) {
        INewsInfoCallback newsInfoCallback = NewsFeedsSDK.getInstance().getNewsInfoCallback();
        if (newsInfoCallback != null) {
            newsInfoCallback.callback(event, docid + "", title, mType, SDKContants.channel,
                    rewardNum, countDown, realDuration);
        }
    }

    /**
     * 增加倒计时
     */
    private void addCountLayout() {
        if (TextUtils.isEmpty(docid)) {
            return;
        }
        String cardId = AdvertisementDbUtil.getRewardRecordId(docid);

        if (cardId.equals(docid)) {
            return;
        }

        if(!NewsFeedsSDK.getInstance().getConfig().isShowCountDown()) {
            return;
        }

        if (SPUtils.contains(INewsInfoCallback.REWARD_TIME_KEY)) {
            long rewardTime = SPUtils.getLong(getApplication(), INewsInfoCallback.REWARD_TIME_KEY, 0L);
            if (!TimeUtil.isToday(rewardTime)) {
                putRewardCache(0, System.currentTimeMillis());
            }
        }

        if (SPUtils.contains(INewsInfoCallback.REWARD_KEY)) {
            int reward = SPUtils.getInt(getApplication(), INewsInfoCallback.REWARD_KEY, 0);
            //今天得到的奖励大于
            if (reward >= NewsFeedsSDK.getInstance().getConfig().getTotalRewardNum()) {
                return;
            }
        }
        String tag = "customCountLayout";
        ViewGroup mViewGroup = (ViewGroup) mWebView.getParent();
        mCustomCountLayout = mViewGroup.findViewWithTag(tag);
        if (mCustomCountLayout == null && mViewGroup instanceof FrameLayout) {
            mCustomCountLayout = new CustomCountLayout(this);
            mCustomCountLayout.setTag(tag);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            layoutParams.setMargins(0, 0, DensityUtil.dip2px(this, 10), DensityUtil.dip2px(this, 7));
            mCustomCountLayout.setVisibility(View.VISIBLE);
            mCustomCountLayout.setClickable(true);
            mViewGroup.addView(mCustomCountLayout, layoutParams);
        }
        INewsInfoCallback.AfferentInfo[] mAfferentInfos = NewsFeedsSDK.getInstance().getConfig().getAfferentInfos();
        if (mAfferentInfos != null && mAfferentInfos.length > 0) {
            for (INewsInfoCallback.AfferentInfo newsInfo : mAfferentInfos) {
                if (newsInfo.getType().equals(mType)) {
                    countDown = newsInfo.getCountDown();
                    rewardNum = newsInfo.getRewardNum();
                    break;
                }
            }
        }
        mCustomCountLayout.setMaxCount(countDown);
        mCustomCountLayout.setOnFinishListener(new CustomCountLayout.OnFinishListener() {
            @Override
            public void onFinish() {
                mCustomCountLayout.setReward(rewardNum);
                int reward = 0;
                if (SPUtils.contains(INewsInfoCallback.REWARD_KEY)) {
                    reward = SPUtils.getInt(getApplication(), INewsInfoCallback.REWARD_KEY, 0);
                }
                putRewardCache(rewardNum + reward,System.currentTimeMillis());

                newsInfoCallback(INewsInfoCallback.TYPE_EVENT_H5_COUNT_DOWN, rewardNum, countDown, countDown);
                RewardCard rewardCardBean = new RewardCard(null, docid, mType);
                AdvertisementDbUtil.createRewardRecord(rewardCardBean);
            }

        });
        mCustomCountLayout.startCount();

    }

    private void putRewardCache(int rewardNum, long rewardTime) {
        SPUtils.put(INewsInfoCallback.REWARD_KEY, rewardNum);
        SPUtils.put(INewsInfoCallback.REWARD_TIME_KEY, rewardTime);
    }
}
