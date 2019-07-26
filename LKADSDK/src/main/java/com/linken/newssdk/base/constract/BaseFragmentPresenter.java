package com.linken.newssdk.base.constract;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public abstract class BaseFragmentPresenter<T extends BaseContractView> extends BasePresenter<T> {
    public BaseFragmentPresenter(T mContactView) {
        super(mContactView);
    }

    public void onAttach() {
    }

    public void onCreateView() {
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    }

    public void onDestroyView() {

    }

    public void onDetach() {
    }
}
