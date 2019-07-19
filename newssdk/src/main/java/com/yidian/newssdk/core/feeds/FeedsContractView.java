package com.yidian.newssdk.core.feeds;

import com.yidian.newssdk.base.constract.BaseContractView;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public interface FeedsContractView extends BaseContractView {
    void initMagicIndicator();

    void onShowError(String errorTip);
}
