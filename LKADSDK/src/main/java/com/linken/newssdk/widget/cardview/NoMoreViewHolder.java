package com.linken.newssdk.widget.cardview;

import android.view.View;

import com.linken.newssdk.R;
import com.linken.newssdk.libraries.bra.BaseViewHolder;


/**
 * @author zhangzhun
 * @date 2018/3/15
 */

public class NoMoreViewHolder extends BaseViewHolder {

    public NoMoreViewHolder(View view) {
        super(view);
    }

    public void onBind() {
        setVisible(R.id.root_empty_cardview, false);
    }
}
