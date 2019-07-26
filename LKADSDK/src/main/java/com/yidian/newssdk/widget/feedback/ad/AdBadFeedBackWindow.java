package com.yidian.newssdk.widget.feedback.ad;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.yidian.ad.data.AdvertisementCard;
import com.yidian.newssdk.R;
import com.yidian.newssdk.utils.CustomizedToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyue on 2017/6/5.
 */

public class AdBadFeedBackWindow {
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
    private AdBadFeedbackUtil.Callback callback = new AdBadFeedbackUtil.Callback() {
        @Override
        public void dismiss() {
            closeFeedbackWindow();
            FeedbackViewManager.closeFeedbackView();
        }

        @Override
        public void afterTellReason(boolean reasonGiven) {
//            CustomizedToastUtil.showPrompt(R.string.ad_feedback_dislike_tip, true);
            if (afterTellReasonListener != null) {
                afterTellReasonListener.afterTellReason(reasonGiven);
            }
        }

        @Override
        public void getDislikeReasonForAd(String reason, String reasonsCode) {
            if (dislikeReasonForADListener != null) {
                dislikeReasonForADListener.dislikeReasonForAD(reason, reasonsCode);
            }
        }
    };

    public AdBadFeedBackWindow(Context context, AdvertisementCard adCard) {
        mActivity = context;
        mFeedBackData = new FeedbackData(adCard);
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

        mNewFeedbackWindow = AdBadFeedbackUtil.generateBadFeedbackWindow(mActivity, originView, clickedView,
                mFeedBackData.id, mFeedBackData.channelId, mFeedBackData.log_meta,
                mFeedBackData.impId, mFeedBackData.source, mFeedBackData.reasons,
                callback);
        FeedbackViewManager.setCurrentFeedbackView(mNewFeedbackWindow);

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

    public AdBadFeedBackWindow setAfterTellReasonListener(AfterTellReasonListener afterTellReasonListener) {
        this.afterTellReasonListener = afterTellReasonListener;
        return this;
    }

    public AdBadFeedBackWindow setDislikeReasonForADListener(DislikeReasonForADListener dislikeReasonForADListener) {
        this.dislikeReasonForADListener = dislikeReasonForADListener;
        return this;
    }

    public interface AfterTellReasonListener {
        void afterTellReason(boolean reasonGiven);
    }

    public interface DislikeReasonForADListener {
        void dislikeReasonForAD(String reason, String reasonsCode);
    }

    private class FeedbackData {
        String id = null;
        String channelId = null;
        String log_meta = null;
        String impId = null;
        String source = null;
        private List<String> reasons = null;

        FeedbackData(AdvertisementCard adCard) {
            if (adCard == null) {
                return;
            }
            source = adCard.source;
            List<String> dislikeReasons = new ArrayList<>(50);
            if ((adCard.dislikeReasons != null) && adCard.dislikeReasons.size() != 0) {
                for (int i = 0; i < adCard.dislikeReasons.size(); i++) {
                    dislikeReasons.add(adCard.dislikeReasons.get(i));
                }
            }
            reasons = dislikeReasons;
        }
    }
}
