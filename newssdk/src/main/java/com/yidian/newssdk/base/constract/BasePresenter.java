package com.yidian.newssdk.base.constract;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public abstract class BasePresenter<T extends BaseContractView> {
    protected T mContactView;

    public BasePresenter(T mContactView) {
        this.mContactView = mContactView;
    }

    public void onCreate() {
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }
}

