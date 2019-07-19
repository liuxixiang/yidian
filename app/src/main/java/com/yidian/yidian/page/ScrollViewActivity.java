package com.yidian.yidian.page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.yidian.newssdk.exportui.NewsPortalFragment;
import com.yidian.yidian.R;

public class ScrollViewActivity extends AppCompatActivity {

    private Fragment fragmentNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view);
        fragmentNavi = new NewsPortalFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sub_fragment, fragmentNavi)
                .commitNowAllowingStateLoss();

    }
}
