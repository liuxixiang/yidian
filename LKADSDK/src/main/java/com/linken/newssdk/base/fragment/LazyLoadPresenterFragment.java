package com.linken.newssdk.base.fragment;

import android.os.Bundle;
import android.util.Log;

import com.linken.newssdk.base.constract.BaseFragmentPresenter;


public abstract class LazyLoadPresenterFragment<T extends BaseFragmentPresenter> extends BaseFragment<T> {

    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;
    protected boolean isDataInitiated;

    protected boolean inViewPager(){
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareFetchData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareFetchData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public abstract void lazyFetchData();


    public boolean prepareFetchData() {
        Log.d("NewsInnerListFragment", "isVisibleToUser=" + isVisibleToUser);
        Log.d("NewsInnerListFragment", "isViewInitiated=" + isViewInitiated);
        Log.d("NewsInnerListFragment", "isDataInitiated=" + isDataInitiated);

        if ((isVisibleToUser || !inViewPager())
                && isViewInitiated
                && (!isDataInitiated)) {
            lazyFetchData();
            isDataInitiated = true;
            return true;
        }
        return false;
    }


}
