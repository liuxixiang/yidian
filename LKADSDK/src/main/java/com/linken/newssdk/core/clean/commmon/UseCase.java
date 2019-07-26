package com.linken.newssdk.core.clean.commmon;

import com.linken.newssdk.core.clean.commmon.bean.UseCaseParams;

/**
 * @author zhangzhun
 * @date 2018/8/9
 */

public interface UseCase<Request extends UseCaseParams.Request, Response extends UseCaseParams.Response> {

    interface Callback<Response> {
        void onSuccess(Response response);

        void onError(Throwable throwable);
    }

    void execute(Request requestParams, Callback<Response> callback);

}
