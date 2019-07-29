package com.linken.yidian;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.linken.yidian.page.CustomThemeNewsPortalActivity;
import com.linken.yidian.page.CustomThemeNewsPortalActivity2;
import com.linken.yidian.page.ExposePagerActivity;
import com.linken.yidian.page.NewsListAcitvity;
import com.linken.yidian.page.NewsPortalActivity;
import com.linken.yidian.page.NewsViewActivity;
import com.linken.yidian.page.ScrollViewActivity;


public class MainActivity extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);


        findViewById(R.id.protal_test).setOnClickListener(this);
        findViewById(R.id.list_test).setOnClickListener(this);
        findViewById(R.id.view_test).setOnClickListener(this);
        findViewById(R.id.customtheme_test).setOnClickListener(this);
        findViewById(R.id.customtheme_test2).setOnClickListener(this);
        findViewById(R.id.scrollview_test).setOnClickListener(this);
        findViewById(R.id.viewpager_test).setOnClickListener(this);

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
    public void onClick(View view) {
        Intent intent = new Intent();

        switch (view.getId()) {
            case R.id.protal_test:
                intent.setClass(this, NewsPortalActivity.class);
                break;
            case R.id.list_test:
                intent.setClass(this, NewsListAcitvity.class);
                break;
            case R.id.view_test:
                intent.setClass(this, NewsViewActivity.class);
                break;
            case R.id.scrollview_test:
                intent.setClass(this, ScrollViewActivity.class);
                break;
            case R.id.viewpager_test:
                intent.setClass(this, ExposePagerActivity.class);
                break;
            case R.id.customtheme_test:
                intent.setClass(this, CustomThemeNewsPortalActivity.class);
                break;
            case R.id.customtheme_test2:
                intent.setClass(this, CustomThemeNewsPortalActivity2.class);
                break;
            default:
                break;
        }

        startActivity(intent);
    }
}
