package com.yidian.yidian.page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;
import com.yidian.newssdk.exportui.NewsListFragment;
import com.yidian.yidian.R;

/**
 * Created by chenyichang on 2018/5/22.
 *
 * 单列表样式接入
 */

public class NewsListAcitvity extends FragmentActivity {

    private Fragment fragment;
    private Button btnRefresh;
    private Button btnScroll2Top;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        fragment = NewsListFragment.newInstance("推荐", false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.portal_container, fragment)
                .commitNowAllowingStateLoss();
        btnRefresh = findViewById(R.id.expose_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewsListFragment)fragment).refreshCurrentChannel();
            }
        });
        btnScroll2Top = findViewById(R.id.expose_scroll2top);
        btnScroll2Top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NewsListFragment)fragment).scrollToTopPosition();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
