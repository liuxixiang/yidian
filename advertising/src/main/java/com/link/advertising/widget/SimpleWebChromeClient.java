package com.link.advertising.widget;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class SimpleWebChromeClient extends WebChromeClient {


    private OnProgressCallback mProgressCallback;


    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mProgressCallback != null) {
            mProgressCallback.onProgress(newProgress);
        }
    }

    public void setOnProgressCallback(OnProgressCallback progressCallback) {
        this.mProgressCallback = progressCallback;
    }

    public interface OnProgressCallback {

        void onProgress(int progress);
    }
}
