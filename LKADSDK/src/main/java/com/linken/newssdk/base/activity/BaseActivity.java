package com.linken.newssdk.base.activity;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.linken.newssdk.R;
import com.linken.newssdk.base.constract.BasePresenter;
import com.linken.newssdk.base.constract.IShowErrorView;
import com.linken.newssdk.widget.views.Toolbar;


/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public abstract class BaseActivity<T extends BasePresenter> extends BaseFragmentActivity implements IShowErrorView, View.OnClickListener {
    protected T mPresenter;
    protected LayoutInflater mLayoutInflater;
    protected Toolbar mToolbar;
    protected FrameLayout mToolbarContainer;
    private FrameLayout mBaseToolbarContainer;

    public BaseActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mLayoutInflater = LayoutInflater.from(this);
        this.initPresenter();
        if(this.mPresenter != null) {
            this.mPresenter.onCreate();
        }
        setContentView(attachLayoutId());
        setToolbarContainer();
        initParams();
        initView();
        initData();

    }

    private void setToolbarContainer() {
        mBaseToolbarContainer = findViewById(R.id.base_toolbar_container);
        int customToolbarLayoutId = getCustomToolbarLayoutId();
        if (customToolbarLayoutId == -1) {
            //将通用toolbar的layout填充至BaseActivity的base_toolbar_container里,并进行初始化
            LayoutInflater.from(this).inflate(R.layout.ydsdk_toolbar_common_layout, mBaseToolbarContainer, true);
        } else {
            //将自定义toolbar的layout填充至BaseActivity的base_toolbar_container里.
            LayoutInflater.from(this).inflate(customToolbarLayoutId, mBaseToolbarContainer, true);
        }
        initToolbar();
    }

    protected abstract int getCustomToolbarLayoutId();


    private void initToolbar() {
        mToolbarContainer = findViewById(R.id.toolbar_container);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initParams();

    protected abstract int attachLayoutId();

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
    public void onDestroy() {
        super.onDestroy();
        if(this.mPresenter != null) {
            this.mPresenter.onDestroy();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == 82 || super.onKeyDown(keyCode, event);
    }

    protected abstract void initPresenter();

    protected abstract Bundle buildShareData();

    @Override
    public void onClick(View v) {
    }
}
