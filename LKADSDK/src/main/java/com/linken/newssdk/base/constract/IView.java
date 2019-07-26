package com.linken.newssdk.base.constract;

/**
 * Created by liuyue on 2017/3/24.
 */

public interface IView<T extends IPresenter> {
    void setPresenter(T presenter);
    boolean isAlive();
}
