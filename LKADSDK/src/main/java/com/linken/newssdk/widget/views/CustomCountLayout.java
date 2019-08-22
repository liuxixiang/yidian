package com.linken.newssdk.widget.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.linken.newssdk.R;


public class CustomCountLayout extends FrameLayout {
    public static final String TAG = CustomCountLayout.class.getSimpleName();

    DonutProgress mDonutProgress;
    ImageView mRedPacket;
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
        mRedPacket = view.findViewById(R.id.red_packet);
        mTvReward = view.findViewById(R.id.reward);
//        mTvSecond.setText(getContext().getString(R.string.ydsdk_count_second, 0));
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
    public void setReward(long reward) {
        if(reward > 0) {
            mTvReward.setText("+" + reward);
        }
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
//        mTvSecond.setVisibility(View.VISIBLE);
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
//                mTvSecond.setText(getContext().getString(R.string.ydsdk_count_second, mSecond));

                // 判断是否结束
                if (checkFinish()) {
                    mHandler.removeCallbacksAndMessages(null);
                    if (mOnFinishListener != null) {
                        mOnFinishListener.onFinish();
                    }
                    isRunning = false;
                    startRedPacketAnimator();
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

    /**
     * 奖励动画
     */
    public void startRewardAnimator() {
        PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("translationY", 0.0f, -20f);
        PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("alpha", 0.1f, 1f);
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
        objectAnimator.setDuration(1000).start();
    }

    /**
     * 红包抖动动画
     */
    public void startRedPacketAnimator() {
        float rotation = 20f;
        float scaleX = 0.9f;
        /**
         * 左右震动效果
         */
        Keyframe frame0 = Keyframe.ofFloat(0f, 0);
        Keyframe frame1 = Keyframe.ofFloat(0.1f, -rotation);
        Keyframe frame2 = Keyframe.ofFloat(0.2f, rotation);
        Keyframe frame3 = Keyframe.ofFloat(0.3f, -rotation);
        Keyframe frame4 = Keyframe.ofFloat(0.4f, rotation);
        Keyframe frame5 = Keyframe.ofFloat(0.5f, -rotation);
        Keyframe frame6 = Keyframe.ofFloat(0.6f, rotation);
        Keyframe frame7 = Keyframe.ofFloat(0.7f, -rotation);
        Keyframe frame8 = Keyframe.ofFloat(0.8f, rotation);
        Keyframe frame9 = Keyframe.ofFloat(0.9f, -rotation);
        Keyframe frame10 = Keyframe.ofFloat(1, 0);
        PropertyValuesHolder frameHolder1 = PropertyValuesHolder.ofKeyframe("rotation", frame0, frame1, frame2, frame3, frame4, frame5, frame6, frame7, frame8, frame9, frame10);


        /**
         * scaleX放大1.1倍
         */
        Keyframe scaleXframe0 = Keyframe.ofFloat(0f, 1);
        Keyframe scaleXframe1 = Keyframe.ofFloat(0.1f, scaleX);
        Keyframe scaleXframe2 = Keyframe.ofFloat(0.2f, scaleX);
        Keyframe scaleXframe3 = Keyframe.ofFloat(0.3f, scaleX);
        Keyframe scaleXframe4 = Keyframe.ofFloat(0.4f, scaleX);
        Keyframe scaleXframe5 = Keyframe.ofFloat(0.5f, scaleX);
        Keyframe scaleXframe6 = Keyframe.ofFloat(0.6f, scaleX);
        Keyframe scaleXframe7 = Keyframe.ofFloat(0.7f, scaleX);
        Keyframe scaleXframe8 = Keyframe.ofFloat(0.8f, scaleX);
        Keyframe scaleXframe9 = Keyframe.ofFloat(0.9f, scaleX);
        Keyframe scaleXframe10 = Keyframe.ofFloat(1, 1);
        PropertyValuesHolder frameHolder2 = PropertyValuesHolder.ofKeyframe("ScaleX", scaleXframe0, scaleXframe1, scaleXframe2, scaleXframe3, scaleXframe4, scaleXframe5, scaleXframe6, scaleXframe7, scaleXframe8, scaleXframe9, scaleXframe10);


        /**
         * scaleY放大1.1倍
         */
        Keyframe scaleYframe0 = Keyframe.ofFloat(0f, 1);
        Keyframe scaleYframe1 = Keyframe.ofFloat(0.1f, scaleX);
        Keyframe scaleYframe2 = Keyframe.ofFloat(0.2f, scaleX);
        Keyframe scaleYframe3 = Keyframe.ofFloat(0.3f, scaleX);
        Keyframe scaleYframe4 = Keyframe.ofFloat(0.4f, scaleX);
        Keyframe scaleYframe5 = Keyframe.ofFloat(0.5f, scaleX);
        Keyframe scaleYframe6 = Keyframe.ofFloat(0.6f, scaleX);
        Keyframe scaleYframe7 = Keyframe.ofFloat(0.7f, scaleX);
        Keyframe scaleYframe8 = Keyframe.ofFloat(0.8f, scaleX);
        Keyframe scaleYframe9 = Keyframe.ofFloat(0.9f, scaleX);
        Keyframe scaleYframe10 = Keyframe.ofFloat(1, 1);
        PropertyValuesHolder frameHolder3 = PropertyValuesHolder.ofKeyframe("ScaleY", scaleYframe0, scaleYframe1, scaleYframe2, scaleYframe3, scaleYframe4, scaleYframe5, scaleYframe6, scaleYframe7, scaleYframe8, scaleYframe9, scaleYframe10);

        /**
         * 构建动画
         */
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mRedPacket, frameHolder1, frameHolder2, frameHolder3);
        animator.setDuration(1000);
        //setRepeatCount如何设置为无数次呢
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
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
