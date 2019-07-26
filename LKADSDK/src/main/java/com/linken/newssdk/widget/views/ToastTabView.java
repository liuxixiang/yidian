package com.linken.newssdk.widget.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.linken.newssdk.R;
import com.linken.newssdk.theme.ThemeChangeInterface;
import com.linken.newssdk.theme.ThemeManager;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.DensityUtil;

/**
 * @author zhangzhun
 * @date 2018/6/6
 */

public class ToastTabView extends LinearLayout implements ThemeChangeInterface {
    private static final long ANIMATION_DURATION_DOWN = 500L;
    private static final long ANIMATION_DURATION_UP = 500L;
    ObjectAnimator animatorIn;
    ObjectAnimator animatorOut;
    ObjectAnimator animatorDown;
    ObjectAnimator animatorUp;
    AnimatorSet animatorSet;
    private TextView textView;
    private static final int INIT_HEIGHT = DensityUtil.dp2px(44);

    public ToastTabView(Context context) {
        this(context, null);
        initView(context);
    }

    public ToastTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        initView(context);
    }

    public ToastTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.ydsdk_refresh_tab_view, this, true);
        textView = rootView.findViewById(R.id.toastTxt);
        ThemeManager.registerThemeChange(this);
        onThemeChanged(ThemeManager.getTheme());
    }

    private ObjectAnimator getAlphaIn(View view) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "alpha", new float[]{0.0f, 1.0f});
        ofFloat.setDuration(ANIMATION_DURATION_DOWN);
        return ofFloat;
    }

    private ObjectAnimator getTransDown(View view) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "translationY",  - INIT_HEIGHT, 0);
        ofFloat.setDuration(0);
        return ofFloat;
    }

    private ObjectAnimator getTransUp(View view) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "translationY", 0, - INIT_HEIGHT);
        ofFloat.setDuration(ANIMATION_DURATION_UP);
        ofFloat.setStartDelay(500L);
        return ofFloat;
    }

    private ObjectAnimator getAlphaOut(View view) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "alpha", new float[]{1.0f, 0.0f});
        ofFloat.setDuration(ANIMATION_DURATION_DOWN);
        ofFloat.setStartDelay(500L);
        return ofFloat;
    }

    public void startShowWithAnim(final String content) {
        textView.setText(content);
        setVisibility(VISIBLE);
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        animatorIn = getAlphaIn(ToastTabView.this);
        animatorOut = getAlphaOut(ToastTabView.this);
        animatorDown = getTransDown(ToastTabView.this);
        animatorUp = getTransUp(ToastTabView.this);
        animatorSet = new AnimatorSet();

        animatorSet.play(animatorIn).before(animatorOut);

        animatorSet.start();
    }

    public void startShowWithAnim(@StringRes int contentId) {
        startShowWithAnim(ContextUtils.getApplicationContext().getString(contentId));
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

        int tipBgcolor = ta.getColor(R.styleable.NewsSDKTheme_newssdk_feedback_success_tip_bg, 0xFF5B5F);
        textView.setBackgroundColor(tipBgcolor);
        ta.recycle();
        invalidate();
    }
}
