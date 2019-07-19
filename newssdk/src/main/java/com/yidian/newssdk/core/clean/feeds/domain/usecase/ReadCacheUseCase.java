package com.yidian.newssdk.core.clean.feeds.domain.usecase;

import com.yidian.newssdk.core.clean.commmon.repository.ICacheRepository;
import com.yidian.newssdk.core.clean.commmon.UseCase;
import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsRequest;
import com.yidian.newssdk.core.clean.feeds.data.bean.FeedsResponse;

/**
 * @author zhangzhun
 * @date 2018/9/15
 */

public class ReadCacheUseCase<Request extends FeedsRequest, Response extends FeedsResponse>
        implements UseCase<Request, Response>{

    private ICacheRepository<Request, Response> repository;

    public ReadCacheUseCase(ICacheRepository<Request, Response> repository) {
        this.repository = repository;
    }

    @Override
    public void execute(Request requestParams, Callback<Response> callback) {
        repository.readCache(requestParams, callback);
    }
}
