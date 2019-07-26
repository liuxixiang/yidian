package com.yidian.newssdk.widget.newshare;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yidian.newssdk.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/10/22
 */

public class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements  OnShareClickListener {
    private List<ShareItem> mItems = Collections.emptyList();
    private LayoutInflater mInflater;
    private OnShareClickListener mOnShareClickListener;

    public ShareAdapter(Context context, List<ShareItem> mItems, OnShareClickListener mOnShareClickListener) {
        setList(mItems);
        this.mOnShareClickListener = mOnShareClickListener;
        mInflater = LayoutInflater.from(context);
    }

    private void setList(List<ShareItem> items) {
        mItems = items == null ? new ArrayList<ShareItem>() : items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = mInflater.inflate(R.layout.ydsdk_share_item, parent, false);
        ShareViewHolder holder = new ShareViewHolder(itemView, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ShareItem item = mItems.get(position);
        ShareViewHolder topHolder = (ShareViewHolder) holder;
        topHolder.bindValue(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onShareClick(int clickType) {
        mOnShareClickListener.onShareClick(clickType);
    }
}
