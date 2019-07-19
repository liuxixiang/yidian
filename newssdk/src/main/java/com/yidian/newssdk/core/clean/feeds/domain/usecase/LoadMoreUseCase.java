package com.yidian.newssdk.core.clean.feeds.domain.usecase;

import com.yidian.newssdk.core.clean.commmon.repository.IRefreshListRepository;
import com.yidian.newssdk.core.clean.commmon.UseCase;
import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsRequest;
import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsResponse;

/**
 * @author zhangzhun
 * @date 2018/9/17
 */

public class LoadMoreUseCase<Request extends FeedsRequest, Response extends FeedsResponse>
        implements UseCase<Request, Response> {


    private IRefreshListRepository<Request, Response> repository;


    public LoadMoreUseCase(IRefreshListRepository<Request, Response> repository) {
        this.repository = repository;
    }

    @Override
    public void execute(Request requestParams, Callback<Response> callback) {
        repository.loadMoreList(requestParams, callback);
    }
}
