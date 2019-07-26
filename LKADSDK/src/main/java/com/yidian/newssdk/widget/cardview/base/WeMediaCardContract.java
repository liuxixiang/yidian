package com.yidian.newssdk.widget.cardview.base;

import com.yidian.newssdk.base.constract.IPresenter;
import com.yidian.newssdk.base.constract.IView;

/**
 * Created by liuyue on 2017/5/12.
 */

public interface WeMediaCardContract {
    public interface View extends IView<Presenter> {
        void updateThumbUI();
    }

    public interface Presenter extends IPresenter {

        void setData(Object obj);



    }
}
