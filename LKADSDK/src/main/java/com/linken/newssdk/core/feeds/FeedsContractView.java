package com.linken.newssdk.core.feeds;

import com.linken.newssdk.base.constract.BaseContractView;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public interface FeedsContractView extends BaseContractView {
    void initMagicIndicator();

    void onShowError(String errorTip);
}
