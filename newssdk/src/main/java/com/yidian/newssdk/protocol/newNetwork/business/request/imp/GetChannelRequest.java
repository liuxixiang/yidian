package com.yidian.newssdk.protocol.newNetwork.business.request.imp;

import com.yidian.newssdk.SDKContants;
import com.yidian.newssdk.protocol.newNetwork.business.request.RequestBase;

/**
 * Created by chenyichang on 2018/5/22.
 */

public class GetChannelRequest extends RequestBase {


    @Override
    protected String getPath() {
        return "get_channel_list";
    }

}
