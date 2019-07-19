package com.yidian.newssdk.widget.cardview;

import android.content.Context;
import android.view.View;

import com.yidian.newssdk.R;
import com.yidian.newssdk.libraries.bra.BaseViewHolder;


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
