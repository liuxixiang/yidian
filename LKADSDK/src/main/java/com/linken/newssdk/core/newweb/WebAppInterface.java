package com.linken.newssdk.core.newweb;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.core.ad.AdMonitorHelper;
import com.linken.newssdk.core.detail.article.gallery.YdGalleryActivity;
import com.linken.newssdk.core.detail.article.news.YdNewsActivity;
import com.linken.newssdk.core.detail.article.video.YdVideoActivity;
import com.linken.newssdk.data.pref.GlobalConfig;
import com.linken.newssdk.protocol.newNetwork.core.TextResponseHandler;
import com.linken.newssdk.utils.action.AdActionHelper;
import com.linken.newssdk.utils.support.ClientInfoHelper;
import com.linken.newssdk.data.news.INewsType;
import com.linken.newssdk.protocol.newNetwork.RequestManager;
import com.linken.newssdk.utils.CustomizedToastUtil;
import com.linken.newssdk.utils.DisplayUtils;
import com.linken.newssdk.utils.ThreadUtils;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;


public class WebAppInterface {
    private final static String TAG = "WebAppInterface";

    private final static String fakedErrorResponse = "{\n" +
            "\t\"status\": \"failed\",\n" +
            "\t\"code\": 12,\n" +
            "}";

    public interface WebViewHost {
        // Define the interface btw webview and host.
//        Consumer<Document> getDocumentConsumer();    // host能消耗什么？

        void triggerUpdate();

        String getHistory();

        void closeMe();

        void jumpToGroup(String groupId);

        void webViewScroll(int pos, int direction);

        void showAnimation(boolean isOn);

    }

    private Activity mContext;
    private WebView mWebview;


    private WebViewHost mWebViewHost;

    private String mHistory;


    /**
     * Instantiate the interface and set the context
     */
    public WebAppInterface(Activity c, WebView webview, WebViewHost webViewHost) {
        mContext = c;
        mWebview = webview;
        mWebViewHost = webViewHost;

    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(final String toastString) {
        CustomizedToastUtil.showPrompt(toastString, true);
    }

    @JavascriptInterface
    public void jumpToGroup(final String groupId) {
        if (mWebViewHost != null) {
            mWebViewHost.closeMe();
            mWebViewHost.jumpToGroup(groupId);
        }
    }

    @JavascriptInterface
    public void changeOrientation(final boolean usingPortrait) {
        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                if (mContext instanceof YdVideoActivity) {
                    ((YdVideoActivity) mContext).changeOrientation(usingPortrait);
                }
            }
        });

    }


    @JavascriptInterface
    public void setCurrentBrightness(final float percent) {
        if (mContext instanceof YdVideoActivity) {
            ThreadUtils.post2UI(new Runnable() {
                @Override
                public void run() {
                    DisplayUtils.changeLight(mContext, percent, mContext.getWindow().getAttributes());
                }
            });
        }
    }

    @JavascriptInterface
    public void getCurrentBrightness(String callback) {
        String responseStr = String.valueOf(((int) DisplayUtils.getCurrentLight()));
        final String mapfunction = "javascript:" + callback + "(0, " + responseStr + ");void(0);";
        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                mWebview.loadUrl(mapfunction);
            }
        });

    }

    @JavascriptInterface
    public void getCurrentVolume(String callback) {
        String responseStr = String.valueOf(((int) DisplayUtils.getCurrentVolume()));
        final String mapfunction = "javascript:" + callback + "(0, " + responseStr + ");void(0);";
        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                mWebview.loadUrl(mapfunction);

            }
        });
    }

    @JavascriptInterface
    public void setCurrentVolume(final float percent) {
        if (mContext instanceof YdVideoActivity) {
            ThreadUtils.post2UI(new Runnable() {
                @Override
                public void run() {
                    DisplayUtils.changeVolume(mContext, percent);
                }
            });
        }
    }

    @JavascriptInterface
    public void openUri(String category, String paraList, boolean isThirdGallery) {
        /*
        "/hybrid_mini/main/search?xxx=xxx",
        "/hybrid_mini/main/article?xxx=xxx",  such as hybrid_mini/main/article?docid=0HCaGILW
        "/hybrid_mini/main/channel?xxx=xxx",
        "/hybrid_mini/main/wemedia?xxx=xxx"
        "/hybrid_mini/main/recent"
        */
        if (TextUtils.isEmpty(category)) {
            Log.d(TAG, "openUri invoked with empty uri");
            return;
        }
        if ("video".equalsIgnoreCase(category)) {
            YdVideoActivity.startVideoActivity(mContext, INewsType.STYLE_HBRID_VIDEO, paraList);
        } else if ("article".equalsIgnoreCase(category)) {
            YdNewsActivity.startNewsActivity(mContext, INewsType.STYLE_HBRID_NEWS, paraList);
        } else if ("thirdparty".equalsIgnoreCase(category)) {
            if (isThirdGallery) {
                YdGalleryActivity.startGalleryActivity(mContext, INewsType.STYLE_THIRD_PARTY, paraList);
            } else {
                YdNewsActivity.startNewsActivity(mContext, INewsType.STYLE_THIRD_PARTY, paraList);
            }
        } else if ("gallery".equalsIgnoreCase(category)) {
            YdGalleryActivity.startGalleryActivity(mContext, INewsType.STYLE_HBRID_GALLERY, paraList);
        } else if ("external".equalsIgnoreCase(category)) {
            if (isThirdGallery) {
                YdGalleryActivity.startGalleryActivity(mContext, INewsType.STYLE_NEWS_EXTERNAL, paraList);
            } else {
                YdNewsActivity.startNewsActivity(mContext, INewsType.STYLE_NEWS_EXTERNAL, paraList);
            }
        }
    }


    @JavascriptInterface
    public void setArticleHeader(final String title, String imagePath, String lImgPath) {
        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                if (!checkAccessRight("setArticleHeader")) {
                    return;
                }

                if (mContext instanceof YdNewsActivity) {
                    ((YdNewsActivity) mContext).setHeader(title);
                }
            }
        });

    }


    @JavascriptInterface
    public void post(String url, String bodyString, boolean needSDKAuth, final String callback) {
        RequestManager.requestWebPostContent(url, bodyString, needSDKAuth, new TextResponseHandler() {
            @Override
            public void onSuccess(String response) {
                final String mapfunction = "javascript:" + callback + "(0, " + response + ");void(0);";
                ThreadUtils.post2UI(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl(mapfunction);
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                final String errorInvokeString = "javascript:" + callback + "(1, " + e.getMessage() + ");void(0);";
                ThreadUtils.post2UI(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl(errorInvokeString);
                    }
                });
            }
        });
        return;
    }

    @JavascriptInterface
    public void get(String url, boolean isCommentsRequest, final String callback) {
        RequestManager.requestWebGetContent(url, isCommentsRequest, new TextResponseHandler() {
            @Override
            public void onSuccess(String response) {
                final String mapfunction = "javascript:" + callback + "(0, " + response + ");void(0);";
                ThreadUtils.post2UI(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl(mapfunction);
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                final String errorInvokeString = "javascript:" + callback + "(1, " + e.getMessage() + ");void(0);";
                ThreadUtils.post2UI(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl(errorInvokeString);
                    }
                });
            }
        });
        return;
    }

    @JavascriptInterface
    public void showCommentsInHeader(final int count) {
        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                if (count < 0) {
                    return;
                }
                if (mContext instanceof YdGalleryActivity) {
                    ((YdGalleryActivity) mContext).showCommentCount(count);
                }
            }
        });

    }

    //重新加载页面
    @JavascriptInterface
    public void webViewReloadUrl() {
        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                if (!checkAccessRight("webViewReloadUrl")) {
                    return;
                }

                mWebview.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO: Need Verify
                        mWebview.reload();
                    }
                });
            }
        });


    }

    @JavascriptInterface
    public void getClientInfo(final String callback) {
        String finalResult = ClientInfoHelper.getHybridClientInfo();
        final String mapFunction = "javascript:" + callback + "(0, " + finalResult + ");void(0);";
        loadUrlMainLooper(mapFunction);
    }

    private void loadUrlMainLooper(final String url) {
        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                mWebview.loadUrl(url);
            }
        });
    }

    @JavascriptInterface
    public void close() {
        if (mWebViewHost != null) {
            mWebViewHost.closeMe();
        }
    }

    @JavascriptInterface
    public void webViewScroll(int pos, int direction) {
        // pos == 0, 频道信息流
        // pos == 1, 正文
        // pos == *， 其他待定
        // direction = 0, 向下滚动(手指头向上)
        // direction = 1, 向上滚动(手指头向下)
        if (mWebViewHost != null) {
            mWebViewHost.webViewScroll(pos, direction);
        }
    }

    protected boolean checkAccessRight(String methodName) {

        boolean allowAccess = false;
        if (mWebview != null) {
            ThirdPartyManager manager = WebAppManager.getInstance().getThirdPartyManager();
            try {
                URL url = new URL(mWebview.getOriginalUrl());
                if (mWebview.getUrl() != null
                        && "file".equals(url.getProtocol())) {
                    //Hybrid app使用本地file:// 开始
                    allowAccess = true;
                } else if (manager.isInternalSite(mWebview.getUrl())) {
                    //白名单上的URL所有方法都可以使用，只需判host
                    allowAccess = true;
                } else if (needCheckThirdParty(mWebview.getUrl())) {
                    //第三方网站需要同时判断host和方法名
                    allowAccess = manager.hasMethodAccessRight(url.getHost(), methodName);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
        return allowAccess;
    }

    private boolean needCheckThirdParty(String methodName) {
        return !"deeplink".equals(methodName);
    }

    /**
     * 打开正文页底部H5广告
     *
     * @param adCardJsonString 该字符串是经过stringify之后的
     * @param viewId           用于发日志(click和landingPage)，同一个广告在一次展示过程中该值必须与view事件的viewid相同
     */
    @JavascriptInterface
    public void openH5Ad(final String adCardJsonString, final long viewId) {

        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                if (!checkAccessRight("openAd")) {
                    return;
                }

                Log.v(TAG, "openAdCard with JSON:" + adCardJsonString);
                try {
                    JSONObject jsonObject = new JSONObject(adCardJsonString);
                    AdvertisementCard adCard = AdvertisementCard.fromJSON(jsonObject);
                    if (adCard == null) {
                        throw new IllegalArgumentException("It is not a valid ad card");
                    }
                    adCard.viewId = viewId;
                    final AdActionHelper actionHelper = AdActionHelper.newInstance(adCard);
                    ThreadUtils.post2UI(new Runnable() {
                        @Override
                        public void run() {
                            actionHelper.openLandingPage(mContext, true);
                        }
                    });
                } catch (Exception ignored) {
                    Log.e(TAG, "Cannot convert to json");
                }
            }
        });

    }

    /**
     * 正文页底部h5预约试驾广告信息提交
     *
     * @param stringifyAdCard 该字符串是经过stringify之后的
     * @param viewId          与view事件的viewId值相同
     * @param name            h5页面填写的姓名
     * @param phone           h5页面填写的手机号码
     */
    @JavascriptInterface
    public void reserveH5Ad(final String stringifyAdCard, final long viewId, final String name, final String phone) {
        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                if (!checkAccessRight("reserveH5Ad")) {
                    return;
                }

                Log.v(TAG, "reserveAdCard with JSON:" + stringifyAdCard);
                try {
                    JSONObject jsonObject = new JSONObject(stringifyAdCard);
                    AdvertisementCard adCard = AdvertisementCard.fromJSON(jsonObject);
                    if (adCard == null) {
                        throw new IllegalArgumentException("It is not a valid ad card");
                    }
                    adCard.viewId = viewId;
                    final AdActionHelper actionHelper = AdActionHelper.newInstance(adCard);
                    ThreadUtils.post2UI(new Runnable() {
                        @Override
                        public void run() {
                            actionHelper.eventReserve(name, phone, mContext);
                        }
                    });
                } catch (Exception ignored) {
                    Log.e(TAG, "Cannot convert to json");
                }
            }
        });

    }


    @JavascriptInterface
    public void thirdPartyAdListener(String[] thirdPartyUrls) {
        if (thirdPartyUrls == null) {
            return;
        }
        for (int i = 0; i < thirdPartyUrls.length; i++) {
            thirdPartyUrls[i] = AdMonitorHelper.updateThirdPartyClickUrl(thirdPartyUrls[i], "");
        }
        AdMonitorHelper.reportThirdPartyEvents(thirdPartyUrls, "", false);
    }


    @JavascriptInterface
    public void set3rdAppInfo(final String appInfo) {
        ThreadUtils.post2UI(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(appInfo)) {
                    GlobalConfig.saveAppInfo(appInfo);
                }
            }
        });
    }

}
