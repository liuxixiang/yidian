package com.linken.newssdk.core.detail.video;

import android.content.Context;

import com.linken.newssdk.base.constract.BaseFragmentPresenter;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.card.base.CardHelper;
import com.linken.newssdk.protocol.newNetwork.RequestManager;
import com.linken.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyichang on 2018/5/28.
 */

public class VideoPresenter extends BaseFragmentPresenter<VideoContractView> {

    private List<Card> newsInfoList = new ArrayList<>();
    private int requestContent = 0;
    private int requestRelated = 0;

    public VideoPresenter(VideoContractView mContactView) {
        super(mContactView);
    }

    public void requestContent(Context context, String docId) {
        RequestManager.requestContent(docId, new JsonObjectResponseHandler() {

            @Override
            public void onFailure(Throwable e) {
                requestContent = -1;
                handleResult();
            }

            @Override
            public void onSuccess(JSONObject response) {
                if (response != null) {
                    JSONArray documents = response.optJSONArray("documents");
                    if (documents != null) {
                        for (int i = 0; i < documents.length(); i++) {
                            newsInfoList.add(0, CardHelper.parseCard(documents.optJSONObject(i)));
                        }
                    }
                }

                requestContent = 1;
                handleResult();
            }
        });
    }

    private void handleResult() {
        if (requestContent == 1 && requestRelated != 0) {
            mContactView.onHideLoading();
            if (newsInfoList.isEmpty()) {
                mContactView.onShowEmpty();
            } else {
                mContactView.onLoadData(newsInfoList);
            }

        } else if (requestContent == -1) {
            mContactView.onHideLoading();
            mContactView.onShowError();
        }
    }

    private void loadRelatedNews(Context context, String docId) {
        RequestManager.requestRecommend(docId, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response != null) {
                    JSONArray documents = response.optJSONArray("documents");
                    if (documents != null) {
                        for (int i = 0; i < documents.length(); i++) {
                            newsInfoList.add(CardHelper.parseCard(documents.optJSONObject(i)));
                        }
                    }
                }

                requestRelated = 1;
                handleResult();
            }

            @Override
            public void onFailure(Throwable e) {
                requestRelated = -1;
                handleResult();
            }
        });
    }

    public void lazyFetchData(Context context, String docId) {
        mContactView.onShowLoading();
        requestContent(context, docId);
        loadRelatedNews(context, docId);
    }


}
