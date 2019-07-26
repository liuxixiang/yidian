package com.yidian.newssdk.core.clean.feeds.data.datasource;

import com.yidian.newssdk.core.clean.commmon.NewsDataSource;
import com.yidian.newssdk.core.clean.commmon.UseCase;
import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsRequest;
import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsResponse;
import com.yidian.newssdk.data.card.base.NewsListHelper;
import com.yidian.newssdk.data.pref.GlobalDataCache;
import com.yidian.newssdk.protocol.newNetwork.RequestManager;
import com.yidian.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;

import org.json.JSONObject;

/**
 * @author zhangzhun
 * @date 2018/8/28
 */

public class NewsRemoteDataSource implements NewsDataSource<FeedsRequest, FeedsResponse> {


    @Override
    public void getFeedsNews(FeedsRequest request, final UseCase.Callback<FeedsResponse> responseCallback) {
        RequestManager.requestNews(request.action, request.channel,
                request.count, request.refresh, request.history_count, request.history_timestamp,
                new JsonObjectResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        NewsListHelper newsListHelper = new NewsListHelper();
                        newsListHelper.parseResponseContent(response);
                        GlobalDataCache.getInstance().getActiveAccount().fromJson(response, true);
                        responseCallback.onSuccess(new FeedsResponse(newsListHelper.getResultList()));
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        responseCallback.onError(e);
                    }
                });

    }
}
