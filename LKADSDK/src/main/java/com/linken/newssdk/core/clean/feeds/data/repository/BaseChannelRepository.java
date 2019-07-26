package com.linken.newssdk.core.clean.feeds.data.repository;

import com.linken.newssdk.core.clean.commmon.repository.BaseRepository;
import com.linken.newssdk.core.clean.commmon.repository.IChannelRepository;
import com.linken.newssdk.core.clean.commmon.UseCase;
import com.linken.newssdk.core.clean.feeds.data.bean.FeedsRequest;
import com.linken.newssdk.core.clean.feeds.data.bean.FeedsResponse;
import com.linken.newssdk.core.clean.feeds.data.datasource.NewsLocalDataSource;
import com.linken.newssdk.core.clean.feeds.data.datasource.NewsRemoteDataSource;
import com.linken.newssdk.core.clean.commmon.bean.UseCaseParams;
import com.linken.newssdk.data.channel.YdChannel;
import com.linken.newssdk.utils.RefreshControlUtils;

/**
 * @author zhangzhun
 * @date 2018/9/17
 */

public class BaseChannelRepository<Request extends UseCaseParams.Request, Response extends UseCaseParams.Response> extends BaseRepository implements
        IChannelRepository<Request, Response>{

    private NewsLocalDataSource localDataSource;
    private NewsRemoteDataSource remoteDataSource;
    private YdChannel mChannel;

    public BaseChannelRepository(YdChannel mChannel) {
        this.mChannel = mChannel;
        remoteDataSource = new NewsRemoteDataSource();
        localDataSource = new NewsLocalDataSource(mChannel);
    }

    @Override
    public void fetchItemList(UseCaseParams.Request request, final UseCase.Callback callback) {
        remoteDataSource.getFeedsNews((FeedsRequest) request, new UseCase.Callback<FeedsResponse>() {
            @Override
            public void onSuccess(FeedsResponse feedsResponse) {
                callback.onSuccess(feedsResponse);
                saveLocalListToCache(feedsResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
        RefreshControlUtils.saveIndividualChannelNeedUpdate(YdChannel.getChannelName(mChannel.getChannelName()));
    }

    @Override
    public void loadMoreList(UseCaseParams.Request request, UseCase.Callback callback) {
        remoteDataSource.getFeedsNews((FeedsRequest) request, callback);
        RefreshControlUtils.saveIndividualChannelNeedUpdate(YdChannel.getChannelName(mChannel.getChannelName()));
    }

    @Override
    public void readCache(Request request, UseCase.Callback<Response> callback) {
        localDataSource.getFeedsNews(request, callback);
    }

    protected void saveLocalListToCache(FeedsResponse feedsResponse) {
        mChannel.newsList.clear();
        mChannel.newsList.addAll(feedsResponse.getResult());
        localDataSource.saveNewsListToCache(mChannel);
    }
}
