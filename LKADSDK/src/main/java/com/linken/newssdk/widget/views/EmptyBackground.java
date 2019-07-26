package com.linken.newssdk.widget.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.linken.newssdk.R;


/**
 * Created by caichen on 2017/6/5.
 * 正文页空白页面
 */

public class EmptyBackground extends FrameLayout {

    private FrameLayout mWholeView = null;
    private ImageView mEmptyImg = null;
    private TextView mErrorTip = null;
    private TextView mEmptyTip = null;
    private OnClickErrorListener clickErrorListener;

    public EmptyBackground(Context context) {
        this(context, null);
    }

    public EmptyBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmptyBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ydsdk_error_tip, this);
        mWholeView = (FrameLayout) findViewById(R.id.empty_bg);
        mEmptyImg = (ImageView) findViewById(R.id.empty_img);
        mErrorTip = (TextView) findViewById(R.id.empty_tip);
        mEmptyTip = (TextView) findViewById(R.id.txtEmpty);
        mEmptyTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickErrorListener != null) {
                    clickErrorListener.onCommmentClick();
                }
            }
        });
    }

    public void setOnClickCommentListener(OnClickErrorListener onClickErrorListener) {
        this.clickErrorListener = onClickErrorListener;
    }

    public interface OnClickErrorListener {
        void onCommmentClick();
    }

//    public void switchShowTip(boolean showEmptyTip) {
//
//        if (showEmptyTip) {
//            if (mEmptyImg != null) {
//                mEmptyImg.setImageResource(R.drawable.empty_comment);
//            }
//
////            if (mEmptyTip != null) {
////                mEmptyTip.setVisibility(VISIBLE);
////            }
//
//            mErrorTip.setText(getContext().getResources().getString(R.string.news_load_failed_tip3));
//
////            if (mErrorTip != null) {
////                mErrorTip.setVisibility(GONE);
////            }
//        } else {
//            if (mEmptyImg != null) {
//                mEmptyImg.setImageResource(R.drawable.ydsdk_news_load_fail);
//            }
//
//            mErrorTip.setText(getContext().getResources().getString(R.string.news_load_failed_tip2));
//
////            if (mEmptyTip != null) {
////                mEmptyTip.setVisibility(GONE);
////            }
//
////            if (mErrorTip != null) {
////                mErrorTip.setVisibility(VISIBLE);
////            }
//        }

//    }

}
