package com.linken.newssdk.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linken.newssdk.R;
import com.linken.newssdk.base.constract.BaseFragmentPresenter;
import com.linken.newssdk.base.constract.IShowErrorView;
import com.linken.newssdk.theme.ThemeChangeInterface;
import com.linken.newssdk.theme.ThemeManager;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public abstract class BaseFragment<T extends BaseFragmentPresenter> extends Fragment implements IShowErrorView, ThemeChangeInterface {
    protected T mPresenter;
    protected View mRootView;
    private View emptyView;
    protected View errorView;
    protected View loadingView;
    protected TextView mErrorTipView;

    public BaseFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(this.mPresenter != null) {
            this.mPresenter.onAttach();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initPresenter();
        if(this.mPresenter != null) {
            this.mPresenter.onCreate();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
        } else {
            mRootView = inflater.inflate(attachLayoutId(), container, false);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
        handlePlaceHolderViews(placeHolderParent(view));
        ThemeManager.registerThemeChange(this);
        onThemeChanged(ThemeManager.getTheme());
    }

    protected abstract ViewGroup placeHolderParent(View rootView);


    protected  void handlePlaceHolderViews(ViewGroup attachView) {
        initLoadingView(attachView);
        initEmptyView(attachView);
        initErrorView(attachView);
    }

    protected boolean attachToRoot(){
        return false;
    }

    protected void initLoadingView(ViewGroup viewGroup) {
        loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.ydsdk_loading_view, viewGroup, false);
        if (attachToRoot()){
            viewGroup.addView(loadingView);
        }
    }


    protected void initEmptyView(ViewGroup viewGroup) {
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.ydsdk_empty_view, viewGroup, false);
        if (attachToRoot()){
            viewGroup.addView(emptyView);
        }
    }

    protected void initErrorView(ViewGroup viewGroup) {
        errorView = LayoutInflater.from(getActivity()).inflate(R.layout.ydsdk_error_view, viewGroup, false);
        mErrorTipView = errorView.findViewById(R.id.error_tip);
        if (attachToRoot()){
            viewGroup.addView(errorView);
        }
    }

    protected abstract String getPageReportId();

    protected abstract int attachLayoutId();

    protected abstract void initWidget(View view);


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(this.mPresenter != null) {
            this.mPresenter.onActivityCreated(savedInstanceState);
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        if(this.mPresenter != null) {
            this.mPresenter.onStart();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.mPresenter != null) {
            this.mPresenter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(this.mPresenter != null) {
            this.mPresenter.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(this.mPresenter != null) {
            this.mPresenter.onStop();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(this.mPresenter != null) {
            this.mPresenter.onDestroyView();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(this.mPresenter != null) {
            this.mPresenter.onDestroy();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(this.mPresenter != null) {
            this.mPresenter.onDetach();
        }

    }

    protected void initPresenter() {
    }

    @Override
    public void onThemeChanged(int theme) {
        if (mRootView != null){
            int color = ThemeManager.getColor(getContext(), theme, R.styleable.NewsSDKTheme_newssdk_common_bg_color, 0xffffff);
            mRootView.setBackgroundColor(color);
        }

//        if (mRootView != null) {
//            View indicator = mRootView.findViewById(R.id.indicator_divider);
//            if (indicator != null) {
//                int color = ThemeManager.getColor(getContext(), theme, R.styleable.NewsSDKTheme_newssdk_sliding_tab_indicator_divider_color, 0xffffff);
//                indicator.setBackgroundColor(color);
//            }
//        }

    }

    @Override
    public void onShowError() {
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onHideError() {
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onShowLoading() {
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideLoading() {
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onShowEmpty() {
        emptyView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onHideEmpty() {
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }


    public View getEmptyView() {
        return emptyView;
    }

    public View getErrorView() {
        return errorView;
    }

    public View getLoadingView() {
        return loadingView;
    }
}

