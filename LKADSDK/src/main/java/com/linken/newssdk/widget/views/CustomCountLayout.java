package com.linken.newssdk.widget.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.linken.newssdk.R;


public class CustomCountLayout extends FrameLayout {
    public static final String TAG = CustomCountLayout.class.getSimpleName();

    DonutProgress mDonutProgress;
    TextView mTvSecond;
    TextView mTvReward;

    private Handler mHandler = new Handler();
    // 当前的秒数
    private int mSecond;

    // 最大计数
    private int mMaxCount;

    // 是否正在运行
    private boolean isRunning;
    private ValueAnimator mValueAnimator;

    // 为了让进度圈更加圆滑,因此设置一个比例因子
    public static final int FACTOR = 20;

    // 当不可见时停止计时
    private boolean isVisible;

    public CustomCountLayout(@NonNull Context context) {
        this(context, null);
    }

    public CustomCountLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CustomCountLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.ydsdk_layout_custom_count, this);
        mDonutProgress = view.findViewById(R.id.donut_progress);
        mTvSecond = view.findViewById(R.id.tv_second);
        mTvReward = view.findViewById(R.id.reward);
        mTvSecond.setText(getContext().getString(R.string.ydsdk_count_second, 0));
        mDonutProgress.setProgress(0);
    }

    /**
     * 设置最大值
     *
     * @param maxCount
     */
    public void setMaxCount(int maxCount) {
        mMaxCount = maxCount;
        mDonutProgress.setMax(mMaxCount * FACTOR);
    }

    /**
     * 设置奖励
     */
    public void setReward(int reward) {
        mTvReward.setText("+" + reward);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            // 执行动画效果
            animationProgress();

        }
    };


    /**
     * 检查是否执行完毕
     *
     * @return
     */
    private boolean checkFinish() {
        if (mSecond >= mMaxCount) {
            return true;
        }
        return false;
    }

    /**
     * 开始计时
     */
    public void startCount() {
        // 如果正在运行,则不执行
        if (isRunning) {
            return;
        }
        // 检查是否加载完毕
        if (checkFinish()) {
            return;
        }

        isVisible = true;
        isRunning = true;
        mTvSecond.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mRunnable, 1000);
    }


    /**
     * 开始执行动画
     */
    private void animationProgress() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        isRunning = true;
        int start = mSecond * FACTOR;
        int end = (mSecond + 1) * FACTOR;
        mValueAnimator = ValueAnimator.ofInt(start, end);
        // 间隔为1秒
        mValueAnimator.setDuration(1000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                mDonutProgress.setProgress(currentValue);
            }
        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSecond++;
                mTvSecond.setText(getContext().getString(R.string.ydsdk_count_second, mSecond));

                // 判断是否结束
                if (checkFinish()) {
                    mHandler.removeCallbacksAndMessages(null);
                    if (mOnFinishListener != null) {
                        mOnFinishListener.onFinish();
                    }
                    isRunning = false;
                    startRewardAnimator();
                    return;
                }

                // 1s后继续执行
                if (isVisible) {
                    mHandler.removeCallbacksAndMessages(null);
                    mHandler.post(mRunnable);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mValueAnimator.start();
    }

    /**
     * 停止计时.
     */
    public void endCount() {
        endCount(true);
    }

    public void endCount(boolean cancelAnimation) {
        if (cancelAnimation) {
            cancelAnimation();
        }
        mHandler.removeCallbacksAndMessages(null);
        isRunning = false;
        isVisible = false;
    }

    /**
     * 取消动画
     */
    private void cancelAnimation() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    private void startRewardAnimator() {
        PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("translationY", 0.0f, -30f);
        PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("alpha", 0.3f, 1.0F);
        PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("textSize", 10f, 30f);

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mTvReward, valuesHolder, valuesHolder1, valuesHolder2);

        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                CustomCountLayout.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mTvReward.setVisibility(VISIBLE);
            }


        });
        objectAnimator.setDuration(2000).start();
    }


    public int getSecond() {
        return mSecond;
    }


    private OnFinishListener mOnFinishListener;

    public interface OnFinishListener {
        void onFinish();
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }
}
