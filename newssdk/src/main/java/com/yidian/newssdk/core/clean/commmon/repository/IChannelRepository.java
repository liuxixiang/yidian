package com.yidian.newssdk.core.clean.commmon.repository;

import com.yidian.newssdk.core.clean.commmon.bean.UseCaseParams;

/**
 * @author zhangzhun
 * @date 2018/9/15
 */

public interface IChannelRepository<Request extends UseCaseParams.Request, Response extends UseCaseParams.Response>
        extends ICacheRepository<Request, Response>, IRefreshListRepository{


}
