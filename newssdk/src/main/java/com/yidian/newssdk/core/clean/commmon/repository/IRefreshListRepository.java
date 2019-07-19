package com.yidian.newssdk.core.clean.commmon.repository;

import com.yidian.newssdk.core.clean.commmon.UseCase;
import com.yidian.newssdk.core.clean.commmon.bean.UseCaseParams;

/**
 * @author zhangzhun
 * @date 2018/8/28
 */

public interface IRefreshListRepository<Request extends UseCaseParams.Request, Response extends UseCaseParams.Response> {

    void fetchItemList(Request request, UseCase.Callback<Response> callback);

    void loadMoreList(Request request, UseCase.Callback<Response> callback);

}
