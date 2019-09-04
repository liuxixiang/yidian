package com.linken.newssdk.core.detail.article.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.linken.ad.data.AdvertisementCard;
import com.linken.ad.data.RewardCard;
import com.linken.newssdk.IntentConstants;
import com.linken.newssdk.NewsConfig;
import com.linken.newssdk.NewsFeedsSDK;
import com.linken.newssdk.R;
import com.linken.newssdk.SDKContants;
import com.linken.newssdk.base.activity.BaseActivity;
import com.linken.newssdk.core.detail.article.video.YdVideoActivity;
import com.linken.newssdk.core.newweb.LiteWebView;
import com.linken.newssdk.core.newweb.WebAppInterface;
import com.linken.newssdk.core.newweb.WebAppManager;
import com.linken.newssdk.data.ad.db.AdvertisementDbUtil;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.news.INewsType;
import com.linken.newssdk.export.INewsInfoCallback;
import com.linken.newssdk.toutiao.TTAdManagerHolder;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.DensityUtil;
import com.linken.newssdk.utils.EncryptUtil;
import com.linken.newssdk.utils.SPUtils;
import com.linken.newssdk.utils.TimeUtil;
import com.linken.newssdk.utils.ToastUtils;
import com.linken.newssdk.utils.support.ImageLoaderHelper;
import com.linken.newssdk.widget.views.CustomCountLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.linken.newssdk.data.news.INewsType.NORMAL_NEWS_URL;
import static com.linken.newssdk.data.news.INewsType.STYLE_HBRID_VIDEO;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public abstract class CommonNewsActivity<P extends CommonNewsPresenter> extends BaseActivity<P> implements CommonNewsContractView, WebAppInterface.WebViewHost {

    protected LiteWebView mWebView;
    private String mUri;
    protected int mLocalJsNeedInsert = 0;   // 0: 不需要； 1： 加载针对thirdparty部分的。
    protected Card mCard;
    protected boolean isFromList = false;
    protected int articleType = INewsType.STYLE_HBRID_NEWS;
    protected String mNewsPara = INewsType.NORMAL_NEWS_URL;
    protected int mCurrentScreenMode = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private long landingPageStartTime = 0L;//记录广告landingPage开始时间
    private long landingPageEndTime = 0L;//记录广告landingPage结束时间
    private long duration = 0L;
    private CustomCountLayout mCustomCountLayout;
    private String mType = "";
    private int countDown = 15;//倒计时时间
    private int rewardNum;//奖励个数
    private boolean isFristPageFinish;//网页第一次加载完成
    private String docid;
    private String title;
	
	private FrameLayout mBannerContainer;
    private TTAdNative mTTAdNative;
    private Button mCreativeButton;

    @Override
    protected void initView() {
        mWebView = findViewById(R.id.web_container);
        mBannerContainer = findViewById(R.id.web_banner);

        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }

    @Override
    protected void initData() {
        initWebView(this, mWebView);
        fetchData();

        loadBannerAd(NewsConfig.CODE_ID_BANNER);
    }

    @Override
    protected void initParams() {
        mCard = (Card) getIntent().getSerializableExtra(IntentConstants.CARD);
        articleType = getIntent().getIntExtra(INewsType.NEWS_STYLE, INewsType.STYLE_HBRID_NEWS);
        mNewsPara = getIntent().getStringExtra(NORMAL_NEWS_URL);
        if (mCard != null) {
            isFromList = true;
        } else {
            isFromList = false;
        }
        handleStyleAndUrl();
        mUri = generateUri();

        if (mCard != null) {
            SDKContants.channel = mCard.channel;
            if (Card.CTYPE_VIDEO_LIVE_CARD.equals(mCard.cType) || Card.CTYPE_VIDEO_CARD.equals(mCard.cType)) {
                mType = INewsInfoCallback.TYPE_VIDEO;
            } else if (Card.CTYPE_ADVERTISEMENT.equals(mCard.cType)) {
                mType = INewsInfoCallback.TYPE_AD;
            } else {
                mType = INewsInfoCallback.TYPE_ARTICLE;
            }
        }
        if (!TextUtils.isEmpty(mUri)) {
            Uri uri = Uri.parse(mUri);
            docid = uri.getQueryParameter("docid");
            if (STYLE_HBRID_VIDEO == articleType) {
                mType = INewsInfoCallback.TYPE_VIDEO;
            } else {
                mType = INewsInfoCallback.TYPE_ARTICLE;
            }
        }

    }

    private void handleStyleAndUrl() {
        if (isFromList) {
            try {
                handleNewsPara();
                URL url = new URL(mCard.url);
                String host = url.getHost();
                if (WebAppManager.getInstance().getThirdPartyManager().isInternalSite(host)) {
                    switch (mCard.cType) {
                        case Card.CTYPE_NORMAL_NEWS:
                        case Card.CTYPE_ADVERTISEMENT:
                            articleType = INewsType.STYLE_HBRID_NEWS;
                            break;
                        case Card.CTYPE_VIDEO_CARD:
                            articleType = INewsType.STYLE_HBRID_VIDEO;
                            break;
                        case Card.CTYPE_PICTURE_GALLERY:
                            articleType = INewsType.STYLE_HBRID_GALLERY;
                            break;
                        default:
                            break;
                    }
                } else {
                    articleType = INewsType.STYLE_THIRD_PARTY;
                }
                handleAdCard();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAdCard() {
        if (mCard != null && mCard instanceof AdvertisementCard && !TextUtils.isEmpty(((AdvertisementCard) mCard).getDocId())) {
            articleType = INewsType.STYLE_HBRID_NEWS;
        }
    }

    private void handleNewsPara() {
        if (mCard instanceof AdvertisementCard) {
            mNewsPara = "docid=" + ((AdvertisementCard) mCard).getDocId();
        } else {
            mNewsPara = getNewsPara(mCard.id, mCard.url);
        }
    }

    public void fetchData() {
        if (mWebView != null && !TextUtils.isEmpty(mUri)) {
            mWebView.loadUrl(mUri);
        }
    }

    protected String generateUri() {
        String url = null;
        switch (articleType) {
            case INewsType.STYLE_HBRID_NEWS:
            case INewsType.STYLE_HBRID_VIDEO:
                url = "file:///android_asset/www/html/main/article.html?" + mNewsPara;
                break;
            case INewsType.STYLE_HBRID_GALLERY:
                url = "file:///android_asset/www/html/main/article.html?" + mNewsPara + "&gallery=true";
                break;
            case INewsType.STYLE_NEWS_COMMENTS:
                url = "file:///android_asset/www/html/main/comment.html?" + mNewsPara;
                break;
            case INewsType.STYLE_THIRD_PARTY:
                url = mNewsPara;
                mLocalJsNeedInsert = 1;   //"file:///android_asset/www/build/thirdparty.js";
                break;
            case INewsType.STYLE_NEWS_EXTERNAL:
                url = mNewsPara;
                break;
            default:
                break;
        }
        return url;
    }

    private void initWebView(Activity context, LiteWebView webview) {
        if (context == null || webview == null) {
            return;
        }
        Map<String, String> jsStringMap = new HashMap<>(2);
        String localJsToBeInjected = null;
        InputStream input;
        try {
            // www/js/common/hammer.min.js
            input = ContextUtils.getApplicationContext().getAssets().open("www/js/common/hammer.min.js");
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.DEFAULT);
            localJsToBeInjected = encoded;
        } catch (IOException e) {
            e.printStackTrace();
            localJsToBeInjected = null;
        }
        jsStringMap.put(LiteWebView.JS_HAMMER, localJsToBeInjected);  // 这个要第一个。

        if (mLocalJsNeedInsert == 1) {
            try {
                input = ContextUtils.getApplicationContext().getAssets().open("www/build/thirdparty.js");
                byte[] buffer = new byte[input.available()];
                input.read(buffer);
                input.close();

                // String-ify the script byte-array using BASE64 encoding !!!
                String encoded = Base64.encodeToString(buffer, Base64.DEFAULT);
                localJsToBeInjected = encoded;
            } catch (IOException e) {
                e.printStackTrace();
                localJsToBeInjected = null;
            }
            jsStringMap.put(LiteWebView.JS_THIRDPARTY, localJsToBeInjected);  // 这个是第二个。
        }

        webview.init(jsStringMap, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (!isForExternalPage()) {
            webview.addJavascriptInterface(
                    new WebAppInterface(context, webview, this), "container");
        } else {
            // 告诉Webview，它处于外部网页中，必要的话去拦截一些不必要的url。
            webview.setInExternalPageEnvironment(true);
        }

        if (!allowContextMenu()) {
            webview.setLongClickable(false);
            webview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }
        webview.setPageLoadListener(new LiteWebView.PageLoadListener() {
            @Override
            public void onPageLoadFinished() {
                addCountLayout();
                if (!isFristPageFinish) {
                    isFristPageFinish = true;
                    landingPageStartTime = System.currentTimeMillis();//息屏之后重新计时
                }
                String url = mWebView.getUrl();
                if (!TextUtils.isEmpty(url)) {
                    Uri uri = Uri.parse(url);
                    title = uri.getFragment();
                }
            }

            @Override
            public void shouldInterceptRequest(WebView view, String url) {
                if (CommonNewsActivity.this instanceof YdVideoActivity) {
                    if (url.contains("/video/") && (url.contains(".mp4") || url.contains(".3gp")
                            || url.contains(".avi") || url.contains(".wma"))) {
                        docid = EncryptUtil.getMD5_32(url);
                        addCountLayout();
                    }
                }
            }
        });

    }

    protected boolean isForExternalPage() {
        // 缺省认为不是。
        return false;
    }

    protected boolean allowContextMenu() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
            mWebView.notifyJsAtKeyPoint(LiteWebView.WV_RESUME);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.notifyJsAtKeyPoint(LiteWebView.WV_PAUSE);
        }
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.notifyJsAtKeyPoint(LiteWebView.WV_DESTROY);
            mWebView.destroy();
            mWebView = null;
        }
        if (landingPageStartTime != 0) {
            duration = System.currentTimeMillis() - landingPageStartTime + duration;
        }
        newsInfoCallback(INewsInfoCallback.TYPE_EVENT_DURATION, rewardNum, countDown, (int) (duration / 1000));
		 if (mCreativeButton != null) {
            mCreativeButton = null;
        }
        super.onDestroy();
    }


    public void changeOrientation(boolean usingPortrait) {
        if (usingPortrait) {
            mCurrentScreenMode = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else {
            mCurrentScreenMode = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        setRequestedOrientation(
                usingPortrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void triggerUpdate() {

    }

    @Override
    public String getHistory() {
        return null;
    }

    public String getNewsPara(String docId, String originUrl) {
        if (WebAppManager.getInstance().getThirdPartyManager().isInternalSite(originUrl)) {
            String result = "docid=" + docId;
            try {
                URL url = new URL(originUrl);
                if (!TextUtils.isEmpty(url.getQuery())) {
                    result += "&" + url.getQuery();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return result;
        } else {
            return originUrl;
        }

    }

    @Override
    public void closeMe() {
        finish();
    }

    @Override
    public void jumpToGroup(String groupId) {

    }

    @Override
    public void webViewScroll(int pos, int direction) {

    }

    @Override
    public void showAnimation(boolean isOn) {

    }

    @Override
    public void onShowError() {

    }

    @Override
    public void onHideError() {

    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onShowEmpty() {

    }

    @Override
    public void onHideEmpty() {

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
    public void onStop() {
        super.onStop();
        landingPageEndTime = System.currentTimeMillis();
        if (mCustomCountLayout != null) {
            mCustomCountLayout.endCount();
        }

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(docid)) {
                    return;
                }
                String cardId = AdvertisementDbUtil.getRewardRecordId(docid);

                if (cardId.equals(docid)) {
                    return;
                }

                if (!NewsFeedsSDK.getInstance().getConfig().isShowCountDown()) {
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
                if(mCustomCountLayout != null) {
                    mCustomCountLayout.endCount();
                    mViewGroup.removeView(mCustomCountLayout);
                    mCustomCountLayout = null;
                }
                if (mCustomCountLayout == null && mViewGroup instanceof FrameLayout) {
                    mCustomCountLayout = new CustomCountLayout(CommonNewsActivity.this);
                    mCustomCountLayout.setTag(tag);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                    layoutParams.setMargins(0, 0, DensityUtil.dip2px(CommonNewsActivity.this, 10), DensityUtil.dip2px(CommonNewsActivity.this, 7));
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
                        putRewardCache(rewardNum + reward, System.currentTimeMillis());

                        newsInfoCallback(INewsInfoCallback.TYPE_EVENT_H5_COUNT_DOWN, rewardNum, countDown, countDown);
                        RewardCard rewardCardBean = new RewardCard(null, docid, mType);
                        AdvertisementDbUtil.createRewardRecord(rewardCardBean);

                    }

                });
                mCustomCountLayout.startCount();
            }
        });
    }

    private void putRewardCache(int rewardNum, long rewardTime) {
        SPUtils.put(INewsInfoCallback.REWARD_KEY, rewardNum);
        SPUtils.put(INewsInfoCallback.REWARD_TIME_KEY, rewardTime);
    }
 @SuppressWarnings({"ALL", "SameParameterValue"})
    private void loadBannerAd(String codeId) {
        //step4:创建广告请求参数AdSlot,注意其中的setNativeAdtype方法，具体参数含义参考文档
        final AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 257)
                .setNativeAdType(AdSlot.TYPE_BANNER) //请求原生广告时候，请务必调用该方法，设置参数为TYPE_BANNER或TYPE_INTERACTION_AD
                .setAdCount(1)
                .build();

        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
            @Override
            public void onError(int code, String message) {
//                TToast.show(NativeBannerActivity.this, "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeAdLoad(List<TTNativeAd> ads) {
                if (ads.get(0) == null) {
                    return;
                }
                View bannerView = LayoutInflater.from(CommonNewsActivity.this).inflate(R.layout.native_ad, mBannerContainer, false);
                if (bannerView == null) {
                    return;
                }
                if (mCreativeButton != null) {
                    //防止内存泄漏
                    mCreativeButton = null;
                }
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(bannerView);
                //绑定原生广告的数据
                setAdData(bannerView, ads.get(0));
            }
        });
    }


    @SuppressWarnings("RedundantCast")
    private void setAdData(View nativeView, TTNativeAd nativeAd) {
        ((TextView) nativeView.findViewById(R.id.tv_native_ad_title)).setText(nativeAd.getTitle());
        ((TextView) nativeView.findViewById(R.id.tv_native_ad_desc)).setText(nativeAd.getDescription());
        ImageView imgDislike = nativeView.findViewById(R.id.img_native_dislike);
        bindDislikeAction(nativeAd, imgDislike);
        if (nativeAd.getImageList() != null && !nativeAd.getImageList().isEmpty()) {
            TTImage image = nativeAd.getImageList().get(0);
            if (image != null && image.isValid()) {
                ImageView im = nativeView.findViewById(R.id.iv_native_image);
                ImageLoaderHelper.displayImage(im, image.getImageUrl());
            }
        }
        TTImage icon = nativeAd.getIcon();
        if (icon != null && icon.isValid()) {
            ImageView im = nativeView.findViewById(R.id.iv_native_icon);
            ImageLoaderHelper.displayImage(im, icon.getImageUrl());
        }
        mCreativeButton = (Button) nativeView.findViewById(R.id.btn_native_creative);
        //可根据广告类型，为交互区域设置不同提示信息
        switch (nativeAd.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                nativeAd.setActivityForDownloadApp(this);
                mCreativeButton.setVisibility(View.VISIBLE);
                nativeAd.setDownloadListener(mDownloadListener); // 注册下载监听器
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:
                mCreativeButton.setVisibility(View.VISIBLE);
                mCreativeButton.setText("立即拨打");
                break;
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
                mCreativeButton.setVisibility(View.VISIBLE);
                mCreativeButton.setText("查看详情");
                break;
            default:
                mCreativeButton.setVisibility(View.GONE);
                ToastUtils.showShort(getApplicationContext(), "交互类型异常");
        }

        //可以被点击的view, 也可以把nativeView放进来意味整个广告区域可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(nativeView);

        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
        //creativeViewList.add(nativeView);
        creativeViewList.add(mCreativeButton);

        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        nativeAd.registerViewForInteraction((ViewGroup) nativeView, clickViewList, creativeViewList, imgDislike, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    ToastUtils.showShort(getApplicationContext(), "广告" + ad.getTitle() + "被点击");
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    ToastUtils.showShort(getApplicationContext(), "广告" + ad.getTitle() + "被创意按钮被点击");
                }
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    ToastUtils.showShort(getApplicationContext(), "广告" + ad.getTitle() + "展示");
                }
            }
        });

    }

    //接入网盟的dislike 逻辑，有助于提示广告精准投放度
    private void bindDislikeAction(TTNativeAd ad, View dislikeView) {
        final TTAdDislike ttAdDislike = ad.getDislikeDialog(this);
        if (ttAdDislike != null) {
            ttAdDislike.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    mBannerContainer.removeAllViews();
                }

                @Override
                public void onCancel() {

                }
            });
        }
        dislikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttAdDislike != null)
                    ttAdDislike.showDislikeDialog();
            }
        });
    }

    private final TTAppDownloadListener mDownloadListener = new TTAppDownloadListener() {
        @Override
        public void onIdle() {
            if (mCreativeButton != null) {
                mCreativeButton.setText("开始下载");
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
            if (mCreativeButton != null) {
                if (totalBytes <= 0L) {
                    mCreativeButton.setText("下载中 percent: 0");
                } else {
                    mCreativeButton.setText("下载中 percent: " + (currBytes * 100 / totalBytes));
                }
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
            if (mCreativeButton != null) {
                mCreativeButton.setText("下载暂停 percent: " + (currBytes * 100 / totalBytes));
            }
        }

        @Override
        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
            if (mCreativeButton != null) {
                mCreativeButton.setText("重新下载");
            }
        }

        @Override
        public void onInstalled(String fileName, String appName) {
            if (mCreativeButton != null) {
                mCreativeButton.setText("点击打开");
            }
        }

        @Override
        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
            if (mCreativeButton != null) {
                mCreativeButton.setText("点击安装");
            }
        }
    };
}
