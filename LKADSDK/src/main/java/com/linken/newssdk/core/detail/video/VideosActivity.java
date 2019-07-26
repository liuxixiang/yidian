package com.linken.newssdk.core.detail.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.linken.newssdk.IntentConstants;
import com.linken.newssdk.R;
import com.linken.newssdk.base.activity.BaseFragmentActivity;
import com.linken.newssdk.libraries.ydvd.YdVideoPlayer;

/**
 * Created by chenyichang on 2018/5/22.
 */

public class VideosActivity extends BaseFragmentActivity {


    public static void startVideoActivity(Context context, String docId) {
        Intent intent = new Intent(context, VideosActivity.class);
        intent.putExtra(IntentConstants.DOCID, docId);
        context.startActivity(intent);
    }

    private String docId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ydsdk_activity_video);
        Intent intent = getIntent();
        docId = intent.getStringExtra(IntentConstants.DOCID);

        VideosFragment fragment = new VideosFragment();
        Bundle bundle = new Bundle();
        bundle.putString("docId", docId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.video_page, fragment)
                .commitNowAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {
        if (!YdVideoPlayer.backPress()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        YdVideoPlayer.releaseAllVideos();
    }

}
