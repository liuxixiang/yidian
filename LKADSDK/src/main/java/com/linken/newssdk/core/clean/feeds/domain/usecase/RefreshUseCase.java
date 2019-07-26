package com.linken.newssdk.core.clean.feeds.domain.usecase;

import com.linken.newssdk.core.clean.commmon.repository.IRefreshListRepository;
import com.linken.newssdk.core.clean.commmon.UseCase;
import com.linken.newssdk.core.clean.feeds.data.bean.FeedsRequest;
import com.linken.newssdk.core.clean.feeds.data.bean.FeedsResponse;

/**
 * @author zhangzhun
 * @date 2018/9/15
 */

public class RefreshUseCase<Request extends FeedsRequest, Response extends FeedsResponse> implements UseCase<Request, Response> {

    private IRefreshListRepository<Request, Response> repository;


    public RefreshUseCase(IRefreshListRepository<Request, Response> repository) {
        this.repository = repository;
    }

    @Override
    public void execute(Request requestParams, Callback<Response> callback) {
        repository.fetchItemList(requestParams, callback);
    }
}
