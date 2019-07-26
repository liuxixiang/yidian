package com.yidian.newssdk.widget.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.yidian.newssdk.R;
import com.yidian.newssdk.utils.ContextUtils;

public class SimpleDialog extends Dialog {

    private CharSequence mBtnLeftStr;
    private CharSequence mBtnRightStr;
    private CharSequence mMessageStr;
    private CharSequence title;
    private SimpleListener mListener;
    private int mLeftColorRes;
    private int mRightColorRes;
    private int mGravity;
    private int mMaxLines = 2; // 最大行数
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.txv_left_btn) {
                if (mListener != null) {
                    mListener.onBtnLeftClick(SimpleDialog.this);
                }
            } else if (v.getId() == R.id.txv_right_btn) {
                if (mListener != null) {
                    mListener.onBtnRightClick(SimpleDialog.this);
                }
            }
        }
    };

    protected SimpleDialog(Context context) {
        super(context, R.style.ydsdk_SimpleDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ydsdk_simple_dialog);


        TextView vTitle = findViewById(R.id.title);
        if (TextUtils.isEmpty(title)) {
            vTitle.setVisibility(View.GONE);
        } else {
            vTitle.setVisibility(View.VISIBLE);
            vTitle.setText(title);
        }

        TextView txv = (TextView) findViewById(R.id.txv_message);
        txv.setText(mMessageStr);
        txv.setGravity(mGravity);
        txv.setMaxLines(mMaxLines);

        txv = (TextView) findViewById(R.id.txv_left_btn);
        txv.setText(mBtnLeftStr);
        txv.setTextColor(getContext().getResources().getColor(mLeftColorRes));
        txv.setOnClickListener(getOnClickListener());
        txv.setVisibility(TextUtils.isEmpty(mBtnLeftStr) ? View.GONE : View.VISIBLE);

        txv = (TextView) findViewById(R.id.txv_right_btn);
        txv.setText(mBtnRightStr);
        txv.setTextColor(getContext().getResources().getColor(mRightColorRes));
        txv.setOnClickListener(getOnClickListener());
        txv.setVisibility(TextUtils.isEmpty(mBtnRightStr) ? View.GONE : View.VISIBLE);

        findViewById(R.id.middleDivider).setVisibility(
                TextUtils.isEmpty(mBtnLeftStr) || TextUtils.isEmpty(mBtnRightStr) ?
                        View.GONE : View.VISIBLE
        );
    }

    /**
     * 点击按钮响应，子类覆写之
     *
     * @return
     */
    protected View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public interface SimpleListener {
        void onBtnLeftClick(Dialog dialog);

        void onBtnRightClick(Dialog dialog);
    }

    public static class SimpleDialogBuilder {
        private CharSequence mBtnLeftStr;
        private CharSequence mBtnRightStr;
        private CharSequence mMessageStr;
        private CharSequence title;
        private SimpleListener mListener;
        private int mLeftColorRes = R.color.ydsdk_content_other_text;
        private int mRightColorRes = R.color.ydsdk_content_text_hl_nt;
        private int mGravity = Gravity.CENTER;
        private int mMaxLines = Integer.MAX_VALUE; // 设置最大行数

        public SimpleDialogBuilder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public SimpleDialogBuilder setTitle(int title) {
            this.title = ContextUtils.getApplicationContext().getString(title);
            return this;
        }

        public SimpleDialogBuilder setMessage(CharSequence str) {
            mMessageStr = str;
            return this;
        }

        public SimpleDialogBuilder setMessageGravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        public SimpleDialogBuilder setLeftBtnStr(CharSequence str) {
            mBtnLeftStr = str;
            return this;
        }

        public SimpleDialogBuilder setRightBtnStr(CharSequence str) {
            mBtnRightStr = str;
            return this;
        }

        public SimpleDialogBuilder setSimpleListener(SimpleListener listener) {
            mListener = listener;
            return this;
        }

        public SimpleDialogBuilder setMaxLines(int maxLines) {
            mMaxLines = maxLines;
            return this;
        }

        public SimpleDialogBuilder setLeftColorRes(int leftColorRes) {
            mLeftColorRes = leftColorRes;
            return this;
        }

        public SimpleDialogBuilder setRightColorRes(int rightColorRes) {
            mRightColorRes = rightColorRes;
            return this;
        }

        public SimpleDialog createDialog(Context context) {
            if (mMessageStr == null) {
                return null;
            }

            SimpleDialog dialog = genereteInstance(context);
            dialog.mMessageStr = mMessageStr;
            dialog.mBtnLeftStr = mBtnLeftStr;
            dialog.mBtnRightStr = mBtnRightStr;
            dialog.mListener = mListener;
            dialog.mGravity = mGravity;
            dialog.mMaxLines = mMaxLines;
            dialog.mLeftColorRes = mLeftColorRes;
            dialog.mRightColorRes = mRightColorRes;
            dialog.title = title;
            return dialog;
        }

        protected SimpleDialog genereteInstance(Context context) {
            return new SimpleDialog(context);
        }
    }

}
