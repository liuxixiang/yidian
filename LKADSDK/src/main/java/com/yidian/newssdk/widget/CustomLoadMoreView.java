package com.yidian.newssdk.widget;


import com.yidian.newssdk.R;
import com.yidian.newssdk.libraries.bra.loadmore.LoadMoreView;


/**
 * @author zhangzhun
 */
public final class CustomLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.ydsdk_view_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
