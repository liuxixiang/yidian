package com.yidian.newssdk.widget.newshare;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yidian.newssdk.R;

/**
 * @author zhangzhun
 * @date 2018/10/22
 */

public class ShareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;
    private TextView textView;
    private ImageView imgView;
    private LinearLayout container;
    private OnShareClickListener mShareClickListener;
    private ShareItem mShareItem;

    public ShareViewHolder(View view, OnShareClickListener mShareClickListener) {
        super(view);
        this.mShareClickListener = mShareClickListener;
        context = view.getContext();
        textView = view.findViewById(R.id.txt_share);
        imgView = view.findViewById(R.id.img_share);
        container = view.findViewById(R.id.item_container);
        container.setOnClickListener(this);
    }

    private void start(ShareItem item) {
        Animation mAnimation = new TranslateAnimation(0, 0, -item.getOffset(), 0);
        mAnimation.setInterpolator(new OvershootInterpolator());
        mAnimation.setDuration(800L);
        this.container.startAnimation(mAnimation);
    }

    void bindValue(ShareItem item) {
        mShareItem = item;
        textView.setText(item.getTitle());
        imgView.setImageDrawable(item.getIcon());
        start(item);
    }

    @Override
    public void onClick(View v) {
        mShareClickListener.onShareClick(mShareItem.getShareType());
    }
}
