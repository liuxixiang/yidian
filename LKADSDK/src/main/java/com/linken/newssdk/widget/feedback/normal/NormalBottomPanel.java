package com.linken.newssdk.widget.feedback.normal;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linken.newssdk.R;
import com.linken.newssdk.data.card.base.ContentCard;
import com.linken.newssdk.data.card.base.ListViewItemData;
import com.linken.newssdk.utils.ColorUtil;
import com.linken.newssdk.utils.DensityUtil;
import com.linken.newssdk.utils.ImageResourceUtil;
import com.linken.newssdk.utils.TimeUtil;

/**
 * Created by liuyue on 2017/8/1.
 */

public class NormalBottomPanel extends LinearLayout implements CardBottomPanelWrapper.IBottomPanel, View.OnClickListener {
    protected ImageView hotFlag = null; // 热或荐
    protected TextView tvSource = null; // news source
    protected TextView tvDate = null; // news date
    protected TextView tvCommentCount = null; // comment count
    protected TextView customizedTag = null;
    //FOR feedback
    View fbButton = null; //feedback trigger
    protected boolean mbEnableFeedback = false;
    private Context mContext;
    private CardBottomPanelWrapper.IPanelAction mActionListener;
    private int mTitleWidth;
    private boolean mbExposeTimeStamp;
    private ContentCard mCard;
    private ListViewItemData.DISPLAY_CARD mCardDisplayType;

    public NormalBottomPanel(Context context) {
        super(context);
        mContext = context;
    }

    public NormalBottomPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @TargetApi(11)
    public NormalBottomPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnToggle) {
            BadFeedBackWindow mNewFeedbackWindow = new BadFeedBackWindow(getContext(), mCard);
            mNewFeedbackWindow.setAfterTellReasonListener(new BadFeedBackWindow.AfterTellReasonListener() {
                @Override
                public void afterTellReason(boolean reasonGiven) {
                    // 若没有给reason， 则要调用不感兴趣。
                    if (mActionListener != null) {
                        mActionListener.onClickBadFeedback(reasonGiven);
                    }
                }
            });
            mNewFeedbackWindow.handleBadFeedBack(fbButton.getRootView(), fbButton);
        } else {
            if (mActionListener != null) {
                mActionListener.onClickCard();
            }
        }
    }

    @Override
    public void initPanel(boolean isCurrentPanel) {
        if (!isCurrentPanel) {
            LayoutInflater factory = LayoutInflater.from(mContext);
            int layout = R.layout.ydsdk_card_button_panel_without_right_padding_ns;
            View view = factory.inflate(layout, this);
            customizedTag = (TextView) view.findViewById(R.id.sourceChannelTag);
            tvSource = (TextView) view.findViewById(R.id.news_source);
            tvDate = (TextView) view.findViewById(R.id.news_time);

            hotFlag = (ImageView) view.findViewById(R.id.hotFlag);
            tvCommentCount = (TextView) view.findViewById(R.id.txtCommentCount);

            fbButton = view.findViewById(R.id.btnToggle);
            fbButton.setOnClickListener(this);
        }
    }

    private Drawable getCardLabelColor(String strokeColor, String bgColor) {
        GradientDrawable drawable;
        if (customizedTag.getTag() instanceof GradientDrawable) {
            drawable = (GradientDrawable) customizedTag.getTag();
        } else {
            drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(DensityUtil.dp2px(1));
            customizedTag.setTag(drawable);
        }
        drawable.setStroke(1, ColorUtil.getIntColor(strokeColor, R.color.ydsdk_blue_in_news_list_card));
        drawable.setCornerRadius(9);

        if (TextUtils.isEmpty(bgColor)) {
            drawable.setColor(Color.TRANSPARENT);
        } else {
            drawable.setColor(ColorUtil.getIntColor(bgColor, R.color.ydsdk_blue_in_news_list_card));
        }
        return drawable;
    }

    /**
     * 计算txt需要的显示宽度
     *
     * @param txt
     * @return
     */
    private float getContentLength(TextView view, String txt) {
        if (view == null || TextUtils.isEmpty(txt)) {
            return 0;
        }
        Paint p = view.getPaint();
        float[] txtWidths = new float[txt.length()];
        p.getTextWidths(txt, txtWidths);//得到每个字符所占的宽度

        float width = 0; // the current line length
        for (int i = 0; i < txt.length(); i++) {
            width += txtWidths[i];
        }
        return width;
    }

    @Override
    public void setPanelData(ListViewItemData.DISPLAY_CARD cardDisplayType, ContentCard card, CardBottomPanelWrapper.IPanelAction actionListener, int sourceType) {
        mCard = card;
        mActionListener = actionListener;

        if (fbButton != null) {
            fbButton.setVisibility(needForbiddenFeedBack(cardDisplayType, card, sourceType) ? GONE : VISIBLE);
        }

        if (mCardDisplayType == ListViewItemData.DISPLAY_CARD.PICTURE_GALLERY_3_EQUAL_SIZE_PICTURES
                || mCardDisplayType == ListViewItemData.DISPLAY_CARD.PICTURE_GALLERY_OUTSIDE_CHANNEL_BIG_PICTURE
                || mCardDisplayType == ListViewItemData.DISPLAY_CARD.PICTURE_GALLERY_OUTSIDE_CHANNEL_SMALL_PICTURE) {
            card.tag_icon = "";
            card.cardLabel = null;
        }

        int charCount = 0;
        float contentLength = 0;
        float scaledDensity = DensityUtil.getScaledDensity();
        if(tvSource!=null){  // 每次回复到最初状态，避免出现复用问题
            tvSource.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tvSource.setCompoundDrawablePadding(0);
        }
        if (tvSource != null && card != null) {
            String cardSource = "";

                cardSource = card.source;
                if (!TextUtils.isEmpty(cardSource)) {
                    tvSource.setCompoundDrawablesWithIntrinsicBounds(0, 0, ImageResourceUtil.selectSmallVIcon(card.weMediaPlusV), 0);
                    tvSource.setCompoundDrawablePadding(3);
                }

            tvSource.setText(cardSource);
            tvSource.setTextColor(getResources().getColor(R.color.ydsdk_content_other_text));


            charCount += cardSource.length();
            contentLength += getContentLength(tvSource, cardSource);
        }

        if (card != null && card.cardLabel != null && !TextUtils.isEmpty(card.cardLabel.text)) {
            if (customizedTag != null) {
                customizedTag.setVisibility(View.VISIBLE);
                //新UI主题下，tag是不带边框的，专题例外
//                if (!FeedUiController.getInstance().isCurApplyNewThemeForCard()) {
//                    customizedTag.setBackgroundDrawable(getCardLabelColor(card.cardLabel.borderColor, card.cardLabel.bgColor));
//                }
                if (TextUtils.equals(card.cardLabel.text, "专题") ) {
                    customizedTag.setTextColor(ColorUtil.getIntColor(card.cardLabel.bgColor, R.color.ydsdk_blue_in_news_list_card));
                } else {
                    customizedTag.setTextColor(ColorUtil.getIntColor(card.cardLabel.textColor, R.color.ydsdk_blue_in_news_list_card));
                }
                customizedTag.setText(card.cardLabel.text);
            }
            if (hotFlag != null) {
                hotFlag.setVisibility(View.GONE);
            }
            charCount += card.cardLabel.text.length();
            contentLength += (getContentLength(customizedTag, card.cardLabel.text) + 9 * scaledDensity); //line width 1dp, margin right 8dp

        } else if (card != null && !TextUtils.isEmpty(card.tag_icon)) {
            if (hotFlag != null) {
                hotFlag.setVisibility(View.VISIBLE);
//                hotFlag.setDefaultImageResId(R.drawable.list_tag_recommend);
//                hotFlag.setImageUrl(card.tag_icon, ImageSize.IMAGE_SIZE_ORIGINAL, true);
            }
            if (customizedTag != null) {
                customizedTag.setVisibility(View.GONE);
            }
            charCount += 3;
            contentLength += (29 * scaledDensity);//src width 22dp, margin right 7dp
        } else {
            if (hotFlag != null) {
                hotFlag.setVisibility(View.GONE);
            }
            if (customizedTag != null) {
                customizedTag.setVisibility(View.GONE);
            }
        }

        contentLength += (75 + 10 * scaledDensity); //padding 10dp, src width 75px

        if (tvCommentCount != null) {
            // comment count
            if (card != null && card.commentCount > 0) {
                int paddingLeftInDp =  8 ;
                float commentLength = getContentLength(tvCommentCount, card.commentCount + "评") + paddingLeftInDp * scaledDensity;
                boolean haveEnoughRoomForComment = (mTitleWidth - contentLength) > commentLength;
                if (haveEnoughRoomForComment) {
                    tvCommentCount.setText(String.valueOf(card.commentCount) + "评");
                    tvCommentCount.setVisibility(View.VISIBLE);
                    contentLength += commentLength;
                } else {
                    tvCommentCount.setVisibility(View.GONE);
                }
            } else {
                tvCommentCount.setVisibility(View.GONE);
            }
        }

        if (!mbExposeTimeStamp) {
            mbExposeTimeStamp = charCount < 9;
        }

        String timeStamp = TimeUtil.convertNewsListTime(card.date, getContext(), 0);
        int paddingLeftInDp = 8;
        boolean haveEnoughRoomForTime = (mTitleWidth - contentLength) > (getContentLength(tvDate, timeStamp) + paddingLeftInDp * scaledDensity)
                + 3 * scaledDensity; // 有些手机老是算不对，多一个字，大概估摸这多加3dp吧。

        if (tvDate != null) {
            if (mCardDisplayType == ListViewItemData.DISPLAY_CARD.NEWS_1_LEFT_BIG_IMAGE) {
                if (TextUtils.isEmpty(mCard.coverImage)) {
                    tvDate.setVisibility(VISIBLE);
                } else {
                    tvDate.setVisibility(GONE);
                }
            } else {
                tvDate.setVisibility(VISIBLE);
                tvDate.setText(mbExposeTimeStamp && haveEnoughRoomForTime ? TimeUtil.convertNewsListTime(card.date, getContext(),
                        0) : "");
            }
        }
    }

    private boolean needForbiddenFeedBack(ListViewItemData.DISPLAY_CARD cardDisplayType, ContentCard card, int sourceType) {
        return card.newsFeedBackFobidden
                || cardDisplayType == ListViewItemData.DISPLAY_CARD.APPCARD_LIKE_NEWS;
    }

    @Override
    public void setExtraCardViewData(Object... objs) {
        if (objs.length < 1) {
            return;
        }

        if (objs[0] instanceof Integer) {
            mTitleWidth = (int) objs[0];
        }

        if (objs.length > 1 && objs[1] instanceof Boolean) {
            mbExposeTimeStamp = (boolean) objs[1];
        }
    }

    @Override
    public void showFeedbackHint() {
        if (mCard != null) {
            if (fbButton != null && fbButton.getVisibility() == VISIBLE) {// 从没有显示过。
                int[] locs = new int[2];
                fbButton.getLocationOnScreen(locs);
                BadFeedbackUtil.setFeedbackHint(fbButton.getRootView(), fbButton, locs[0], locs[1], mCard.id);
            }
        }
    }

    @Override
    public void setShowFbButton(boolean ShowFbButton) {
        if (fbButton != null) {
            if (ShowFbButton) {
                fbButton.setVisibility(View.VISIBLE);
            } else {
                fbButton.setVisibility(View.GONE);
            }
        }
    }
}