package com.linken.newssdk.widget.feedback.normal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linken.newssdk.R;
import com.linken.newssdk.theme.ThemeChangeInterface;
import com.linken.newssdk.theme.ThemeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyichang on 2018/6/1.
 */

public class PopupLinearLayout extends LinearLayout implements ThemeChangeInterface {

    private ImageView imageViewHintTop;
    private ImageView imageViewHintBottom;
    private TextView topTipTextView;
    private LinearLayout winBg;
    private View feedbackDivider;
    private List<CheckedTextView> checkedTextViews = new ArrayList<>();

    public PopupLinearLayout(Context context) {
        super(context);
    }

    public PopupLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageViewHintTop = findViewById(R.id.hint_image_top);
        imageViewHintBottom = findViewById(R.id.hint_image_bottom);
        winBg = findViewById(R.id.win_bg);
        topTipTextView = findViewById(R.id.feedback_top_tip);
        feedbackDivider = findViewById(R.id.feedback_divider);
        checkedTextViews.add((CheckedTextView) findViewById(R.id.reason1));
        checkedTextViews.add((CheckedTextView) findViewById(R.id.reason2));
        checkedTextViews.add((CheckedTextView) findViewById(R.id.reason3));
        checkedTextViews.add((CheckedTextView) findViewById(R.id.reason4));
        checkedTextViews.add((CheckedTextView) findViewById(R.id.reason5));
        checkedTextViews.add((CheckedTextView) findViewById(R.id.reason6));

        ThemeManager.registerThemeChange(this);
        onThemeChanged(ThemeManager.getTheme());
    }

    @Override
    public void onThemeChanged(int theme) {
        TypedArray ta = null;
        try {
            ta = getContext().getResources().obtainTypedArray(theme);
        } catch (Exception e) {
        }

        if (ta == null) {
            return;
        }

        Drawable feedBg = ta.getDrawable(R.styleable.NewsSDKTheme_newssdk_feedback_bg);
        Drawable feedBgDown = ta.getDrawable(R.styleable.NewsSDKTheme_newssdk_feedback_bg_bottom);
        Drawable feedBgUp = ta.getDrawable(R.styleable.NewsSDKTheme_newssdk_feedback_bg_top);

        int badfeedback_state = ta.getResourceId(R.styleable.NewsSDKTheme_newssdk_feedback_state, R.drawable.ydsdk_badfeedback_state);
        int badfeedback_textcolor = ta.getResourceId(R.styleable.NewsSDKTheme_newssdk_feedback_textcolor, R.drawable.ydsdk_badfeedback_textcolor_state);
        int feedtxtColor  = ta.getColor(R.styleable.NewsSDKTheme_newssdk_feedback_commontxt_color, Color.BLACK);
        int feedDivider  = ta.getColor(R.styleable.NewsSDKTheme_newssdk_card_divider_color, Color.BLACK);

        ta.recycle();

        topTipTextView.setTextColor(feedtxtColor);
        if (feedBg != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                winBg.setBackground(feedBg);
            } else {
                winBg.setBackgroundDrawable(feedBg);
            }

        }

        if (feedBgDown != null) {
            imageViewHintTop.setImageDrawable(feedBgDown);
        }

        if (feedBgUp != null) {
            imageViewHintBottom.setImageDrawable(feedBgUp);
        }
        if (feedbackDivider != null) {
            feedbackDivider.setBackgroundColor(feedDivider);
        }

        for (CheckedTextView textView : checkedTextViews) {

            textView.setTextColor(getResources().getColorStateList(badfeedback_textcolor));
            textView.setBackgroundResource(badfeedback_state);
        }
    }
}
