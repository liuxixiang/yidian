package com.linken.newssdk.widget.feedback.normal;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;


import com.linken.newssdk.data.card.base.ContentCard;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JayRay on 01/03/2017.
 * Info: 整合负反馈 popupWindow 展示
 */

public class BadFeedBackWindow {

    // 以下代码为新的负反馈UI部分。
    private PopupWindow mNewFeedbackWindow = null;
    private View mMask;
    private Context mActivity;
    private FeedbackData mFeedBackData;
    /**
     * 负反馈提交之后回调
     */
    private AfterTellReasonListener afterTellReasonListener = null;
    /**
     * 负反馈 AD 之后回调
     */
    private DislikeReasonForADListener dislikeReasonForADListener = null;
    /**
     * 负反馈回调
     */
    private BadFeedbackUtil.Callback callback = new BadFeedbackUtil.Callback() {
        @Override
        public void dismiss() {
            closeFeedbackWindow();
        }

        @Override
        public void afterTellReason(boolean reasonGiven) {
            if (afterTellReasonListener != null) {
                afterTellReasonListener.afterTellReason(reasonGiven);
            }
        }

        @Override
        public void getDislikeReasonForAd(String reason) {
            if (dislikeReasonForADListener != null) {
                dislikeReasonForADListener.dislikeReasonForAD(reason);
            }
        }
    };

    public BadFeedBackWindow(Context context, ContentCard card) {
        mActivity = context;
        mFeedBackData = new FeedbackData(card);
    }

    /**
     * 显示负反馈窗口
     * @param originView 负反馈按钮的 rootView
     * @param clickedView 负反馈按钮
     */
    public void handleBadFeedBack(View originView, View clickedView) {
        if (clickedView == null) {
            return;
        }
        if (mNewFeedbackWindow != null && mNewFeedbackWindow.isShowing()) {
            return;
        } // 提交 docId, channelId，以及UI位置信息给...
        showMask();

        boolean debug = false;
        if (debug) {
            mFeedBackData.source = null;
            mFeedBackData.reasons = null;
        }
        if ((mFeedBackData.reasons == null || mFeedBackData.reasons.size() == 0) && TextUtils.isEmpty(mFeedBackData.source)) {
            mNewFeedbackWindow = BadFeedbackUtil.generateNoReasonBadFeedbackWindow(mActivity, clickedView, callback);
        } else {
            mNewFeedbackWindow = BadFeedbackUtil.generateBadFeedbackWindow(mActivity, originView, clickedView, mFeedBackData, callback);
        }
    }

    public static final int NIGHT_COVER_BG_COLOR = 1526726656;

    private void showMask() {
        Activity activity;
        if (mActivity instanceof Activity) {
            activity = (Activity) mActivity;
        } else {
            return;
        }

        removeMask();
        mMask = new View(activity);
        mMask.setBackgroundColor(NIGHT_COVER_BG_COLOR);
        ((ViewGroup) (activity.getWindow()).getDecorView()).addView(mMask);
    }

    private void closeFeedbackWindow() {
        if (mNewFeedbackWindow != null) {
            mNewFeedbackWindow.dismiss();
            mNewFeedbackWindow = null;
        }
        removeMask();
    }

    private void removeMask() {
        Activity activity;
        if (mActivity instanceof Activity) {
            activity = (Activity) mActivity;
        } else {
            return;
        }
        if (mMask != null) {
            ((ViewGroup) (activity.getWindow()).getDecorView()).removeView(mMask);
            mMask = null;
        }
    }

    public BadFeedBackWindow setAfterTellReasonListener(AfterTellReasonListener afterTellReasonListener) {
        this.afterTellReasonListener = afterTellReasonListener;
        return this;
    }

    public BadFeedBackWindow setDislikeReasonForADListener(DislikeReasonForADListener dislikeReasonForADListener) {
        this.dislikeReasonForADListener = dislikeReasonForADListener;
        return this;
    }

    public interface AfterTellReasonListener {
        void afterTellReason(boolean reasonGiven);
    }

    public interface DislikeReasonForADListener {
        void dislikeReasonForAD(String reason);
    }

    public class FeedbackData {
        String id = null;
        String channelId = null;
        String log_meta = null;
        String impId = null;
        String source = null;
        List<String> reasons = null;
        HashMap<String, String> reasonToFromIdMap = null;

        FeedbackData(ContentCard card) {
            if (card == null) {
                return;
            }
            id = card.id;
            channelId = card.channelId;
            log_meta = card.log_meta;
            impId = card.impId;
            source = card.source;
            reasons = card.dislikeReasons;
            reasonToFromIdMap = card.dislikeReasonMap;
        }
    }
}
