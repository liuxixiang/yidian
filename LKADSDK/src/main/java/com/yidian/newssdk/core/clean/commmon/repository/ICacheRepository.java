package com.yidian.newssdk.core.clean.commmon.repository;

import com.yidian.newssdk.core.clean.commmon.UseCase;
import com.yidian.newssdk.core.clean.commmon.bean.UseCaseParams;
import com.yidian.newssdk.data.channel.YdChannel;

/**
 * @author zhangzhun
 * @date 2018/8/28
 */

public interface ICacheRepository<Request extends UseCaseParams.Request, Response extends UseCaseParams.Response> {

    void  readCache(Request request, UseCase.Callback<Response> callback);

}
