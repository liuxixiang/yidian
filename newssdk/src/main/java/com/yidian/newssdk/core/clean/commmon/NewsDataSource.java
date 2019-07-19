package com.yidian.newssdk.core.clean.commmon;

import com.yidian.newssdk.core.clean.commmon.bean.UseCaseParams;

/**
 * @author zhangzhun
 * @date 2018/8/28
 */

public interface NewsDataSource<Request extends UseCaseParams.Request, Response extends UseCaseParams.Response> {

    void getFeedsNews(Request params, UseCase.Callback<Response> responseCallback);

}
