package com.linken.newssdk.widget.newshare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linken.newssdk.R;

/**
 * @author zhangzhun
 * @date 2018/10/22
 */

public class ShareFragment extends BaseBottomSheetDialogFragment implements View.OnClickListener, OnShareClickListener {


    private RecyclerView mRecyclerView;
    private ShareAdapter mShareAdapter;
    private View mCancleButton;
    private OnShareClickListener mOnShareClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ydsdk_fragment_share, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
    }

    private void initWidget(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_share);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mShareAdapter = new ShareAdapter(getContext(), FactoryShareItem.createShareItems(getContext()), this);
        mRecyclerView.setAdapter(mShareAdapter);
        mCancleButton = view.findViewById(R.id.cancle);
        mCancleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (R.id.cancle == v.getId()) {
            dismiss();
        }
    }

    public void setOnShareClickListener(OnShareClickListener onShareClickListener) {
        this.mOnShareClickListener = onShareClickListener;
    }

    @Override
    public void onShareClick(int clickType) {
        switch (clickType) {
            case FactoryShareItem.REFRESH:
                mOnShareClickListener.onShareClick(clickType);
                break;
            default:
                break;
        }
        dismiss();
    }
}
