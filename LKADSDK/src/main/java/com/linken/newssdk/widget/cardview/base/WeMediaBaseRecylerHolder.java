package com.linken.newssdk.widget.cardview.base;

import android.view.View;

import com.linken.newssdk.libraries.bra.BaseViewHolder;


/**
 * Created by liuyue on 2017/5/12.
 */

public class WeMediaBaseRecylerHolder extends BaseViewHolder {
    protected WeMediaCardContract.Presenter mPresenter;


    public WeMediaBaseRecylerHolder(View view) {
        super(view);
    }

    protected <V extends View> V findView(int viewId) {
        return (V) itemView.findViewById(viewId);
    }
}
