package com.yidian.newssdk.core.detail.article.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yidian.newssdk.IntentConstants;
import com.yidian.newssdk.R;
import com.yidian.newssdk.core.detail.article.comment.YdCommentActivity;
import com.yidian.newssdk.core.detail.article.common.CommonNewsActivity;
import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.news.INewsType;

import static com.yidian.newssdk.data.news.INewsType.NORMAL_NEWS_URL;


/**
 * @author zhangzhun
 * @date 2018/5/25
 */

public class YdGalleryActivity extends CommonNewsActivity<YdGalleryPresenter> implements YdGalleryContractView {

    private FrameLayout mCommentContainer;
    private TextView mCommnetTxt;

    @Override
    protected int getCustomToolbarLayoutId() {
        return R.layout.ydsdk_toolbar_common_gallery_layout;
    }

    @Override
    protected int attachLayoutId() {
        return R.layout.ydsdk_activity_web2;
    }

    @Override
    public void initPresenter() {
        new YdGalleryPresenter(this);
    }

    @Override
    protected Bundle buildShareData() {
        Bundle bundle = new Bundle();
        return bundle;
    }

    @Override
    protected void initView() {
        super.initView();
        mCommentContainer = findViewById(R.id.gallery_comment);
        mCommentContainer.setOnClickListener(this);
        mCommnetTxt = findViewById(R.id.text_comment);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.gallery_comment) {
            handleClickComment();
        }
    }

    private void handleClickComment() {
        YdCommentActivity.startCommentActivity(YdGalleryActivity.this, INewsType.STYLE_NEWS_COMMENTS, mNewsPara);
    }

    public void showCommentCount(int count) {
        if (mCommentContainer != null) {
            mCommentContainer.setVisibility(View.VISIBLE);
            if (count <= 0) {
                mCommnetTxt.setVisibility(View.GONE);
            } else {
                if (count < 10000) {
                    mCommnetTxt.setText(String.valueOf(count));
                } else if (count < 100000) {
                    String checksum = String.format("%.1f万", (float) count / 10000);
                    mCommnetTxt.setText(checksum);
                } else {
                    mCommnetTxt.setText("10万+");
                }
            }
        }
    }


    public static void startGalleryActivity(Context context, Card card) {

        try {
            Intent intent = new Intent(context, YdGalleryActivity.class);
            intent.putExtra(IntentConstants.CARD, card);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startGalleryActivity(Activity activity, @INewsType.NEWS_STYLE int newsStyle, String urlParams) {

        try {
            Intent intent = new Intent(activity, YdGalleryActivity.class);
            intent.putExtra(INewsType.NEWS_STYLE, newsStyle);
            intent.putExtra(NORMAL_NEWS_URL, urlParams);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
