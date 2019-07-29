package com.link.advertising;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.link.advertising.net.AsyncHttpClient;
import com.link.advertising.net.JsonObjectResponseHandler;
import com.link.advertising.net.RequestBase;
import com.link.advertising.widget.LiteWebView;
import com.link.advertising.widget.SimpleWebChromeClient;

import org.json.JSONObject;


public class AdvertisingActivity extends FragmentActivity implements SimpleWebChromeClient.OnProgressCallback, LiteWebView.PageLoadListener {
    private LiteWebView mWebView;
    private ImageView mPrevBtn;
    private ImageView mNextBtn;
    private TextView mTimeText;
    private TextView mClosePageText;
    private String mId;
    private String mAdId;
    private String mUrl;
    private int mReadSecond;
    private ProgressBar mProgress;
    private boolean isPageLoadFinished;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mReadSecond--;
            if (mReadSecond > 0) {
                mTimeText.setVisibility(View.VISIBLE);
                mTimeText.setText(mReadSecond + "s");
                AdvertisingActivity.this.sendMessage();
            } else {
                //跳转到主界面
//                        goHome();
                mTimeText.setVisibility(View.GONE);
                adComplete(mId);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising);
        initViews();
        initWebView();
        distributead();
    }

    private void initViews() {
        mPrevBtn = findViewById(R.id.prev_btn);
        mNextBtn = findViewById(R.id.next_btn);
        mWebView = findViewById(R.id.webView);
        mClosePageText = findViewById(R.id.close_page);
        mTimeText = findViewById(R.id.time);
        mProgress = findViewById(R.id.progressBar);
        mPrevBtn.setEnabled(false);
        mNextBtn.setEnabled(false);
        mClosePageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWebView.canGoBack()) {
                    mWebView.goBack();
                }
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWebView.canGoForward()) {
                    mWebView.goForward();
                }
            }
        });

    }

    private void distributead() {
        new AsyncHttpClient().get(SDKContants.URL_DISTRIBUTEAD, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response != null) {
                    if (response.has("data")) {
                        JSONObject jsonObject = response.optJSONObject("data");
                        if (jsonObject != null) {
                            mId = jsonObject.optString("id");
                            mAdId = jsonObject.optString("adId");
                            mUrl = jsonObject.optString("url");
                            mReadSecond = jsonObject.optInt("readSecond");
                            onAistributeadSuccess();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
            }
        });
    }


    private void adComplete(final String mId) {
        if(TextUtils.isEmpty(mId) || mId.trim().equals("")) {
            return;
        }
        RequestBase requestBase = new RequestBase() {
            @Override
            protected String getPath() {
                return SDKContants.URL_COMPLETE_AD;
            }

            @Override
            public String getURI() {
                String baseUrl = super.getURI();
                StringBuilder builder = new StringBuilder(baseUrl);
                builder.append("/");
                builder.append(mId);
                return builder.toString();
            }
        };
        new AsyncHttpClient().get(requestBase, new JsonObjectResponseHandler() {
            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onSuccess(JSONObject response) {

            }
        });
    }

    private void onAistributeadSuccess() {
        loadUrl(mUrl);
    }

    /**
     * 倒计时
     */
    private void sendMessage() {
        Message message = handler.obtainMessage();
        handler.sendMessageDelayed(message, 1000);
    }

    /**
     * 初始化webview
     */
    private void initWebView() {
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.init();
        mWebView.setChromeClientCallback(this);
        mWebView.setReloadUrlListener(new LiteWebView.ReloadUrlListener() {
            @Override
            public boolean reloadUrl() {
                mWebView.loadUrl(mUrl);
                return true;
            }
        });
        mWebView.setPageLoadListener(this);
    }

    public void loadUrl(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
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
        if (!isPageLoadFinished) {
            Log.e("lxh", "onPageLoadFinished");
            isPageLoadFinished = true;
            mTimeText.setVisibility(View.VISIBLE);
            mTimeText.setText(mReadSecond + "s");
            sendMessage();
        }
        //显示上一个控件显示
        if(mWebView.canGoBack()) {
            mPrevBtn.setEnabled(true);
        }else {
            mPrevBtn.setEnabled(false);
        }
        if(mWebView.canGoForward()) {
            mNextBtn.setEnabled(true);
        }else {
            mNextBtn.setEnabled(false);
        }
    }


    @Override
    public void onPause() {
        if (mWebView != null) {
            if (Build.VERSION.SDK_INT >= 11) {
                if (mWebView != null) {
                    mWebView.onPause();
                }
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        super.onDestroy();
    }

}
