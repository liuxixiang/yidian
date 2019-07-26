package com.yidian.newssdk.widget.feedback.normal;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.card.base.ContentCard;
import com.yidian.newssdk.data.card.base.ListViewItemData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by liuyue on 2017/7/31.
 */

public class CardBottomPanelWrapper extends FrameLayout {

    public static final int UNKOWN = -1;
    public static final int NORMAL = 0;
    public static final int NEAR_BY = 1;
    public static final int CHANNEL_RECOMMEND = 2;
    public static final int WENDA = 3;
    public static final int FENDA = 4;
    public static final int THEME = 5;
    public static final int THEME_CHANNEL = 6;// 主题卡片的底部
    public static final int ZHIBO = 7;// 新闻直播单图的底部

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNKOWN, NORMAL, NEAR_BY, CHANNEL_RECOMMEND, WENDA, FENDA, THEME,THEME_CHANNEL, ZHIBO})
    public @interface PANEL_TYPE {
    }

    private @PANEL_TYPE
    int mCurrentPanelType = UNKOWN;
    private IBottomPanel mCurrentPanel;
    private ContentCard mCard;
    private Context mContext;
    private ListViewItemData.DISPLAY_CARD mCardDisplayType;
    private int mSourceType;

    public CardBottomPanelWrapper(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public CardBottomPanelWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @TargetApi(11)
    public CardBottomPanelWrapper(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void showPanel(IPanelAction actionListener) {
        if (mCurrentPanel != null) {
            mCurrentPanel.setPanelData(mCardDisplayType, mCard, actionListener, mSourceType);
        }
    }

    public void showPanelWithfbButtonShowInfo(IPanelAction actionListener, boolean ShowFbButton) {
        if (mCurrentPanel != null) {
            mCurrentPanel.setPanelData(mCardDisplayType, mCard, actionListener, mSourceType);
            mCurrentPanel.setShowFbButton(ShowFbButton);
        }
    }

    public void initPanelWidget(ListViewItemData.DISPLAY_CARD displayType, ContentCard card, int titleWidth, boolean isExposeTimeStamp, int sourceType) {
        mCard = card;
        mCardDisplayType = displayType;
        mSourceType = sourceType;
        //stategy to choose bottom panel
        @PANEL_TYPE int choosePanelType = NORMAL;

        if (choosePanelType == mCurrentPanelType) {
            mCurrentPanel.initPanel(true);
        } else {
            removeView((View) mCurrentPanel);
            LayoutParams params =
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            switch (choosePanelType) {
                default:
                    insertNormalPanel(titleWidth, isExposeTimeStamp);
                    break;
            }
            mCurrentPanel.initPanel(false);
            ((View) mCurrentPanel).setLayoutParams(params);
            addView((View) mCurrentPanel, 0);
        }
    }

    private void insertNormalPanel(int titleWidth, boolean isExposeTimeStamp) {
        mCurrentPanel = new NormalBottomPanel(mContext);
        mCurrentPanelType = NORMAL;
        mCurrentPanel.setExtraCardViewData(new Object[]{titleWidth, isExposeTimeStamp});
    }

    public void showFeedbackHint() {
        if (mCurrentPanel != null) {
            mCurrentPanel.showFeedbackHint();
        }
    }

    public interface IBottomPanel {
        void initPanel(boolean isCurrentPanel);

        void setPanelData(ListViewItemData.DISPLAY_CARD cardDisplayType, ContentCard card, IPanelAction actionListener, int sourceType);

        void setExtraCardViewData(Object... objs);

        void showFeedbackHint();

        // 控制负反馈按钮是否可见
        void setShowFbButton(boolean ShowFbButton);
    }

    public interface IPanelAction {
        void onClickBadFeedback(boolean reasonGiven);

        void onClickCard();
    }
}