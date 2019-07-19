package com.yidian.newssdk.widget.pullRefresh;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.yidian.newssdk.R;
import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.utils.DensityUtil;

import static android.graphics.Paint.Align.CENTER;


/**
 * Created by Chenyichang on 2015/12/18.
 */
public class TipDrawable extends RefreshDrawable {

    private static final String TAG = TipDrawable.class.getSimpleName();
    private static final int TEXT_COLOR = ContextUtils.getApplicationContext().getResources().getColor(R.color.ydsdk_red_da3838);
    private RectF mBounds;
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private String mDrawTextFailed;
    private int mTextSize;
    private int mPadding;
    private int mBackColor;
    private Animator animator;
    private int mSize;
    private boolean isRunning;
    private float mPaintTipHalfWidth;//提示背景的半宽
    private ValueAnimator mTipAnimation;
    private int mCenterX;
    private int textColor = TEXT_COLOR;

    TipDrawable(Context context, PullRefreshLayout layout) {
        super(context, layout);
        initParams();
    }

    private void initParams() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mSize = mTextSize = DensityUtil.dp2px(15);
        mPadding = DensityUtil.dp2px(10);
        mCenterX = DensityUtil.getScreenWidth() / 2;
    }

    @Override
    public void setColor(int color) {
        mBackColor = color;
    }

    @Override
    public void start() {
        isRunning = true;
        animator = generateAnimation();
        animator.start();
    }

    @Override
    public void stop() {
        isRunning = false;
        if (animator != null && animator.isRunning()) {
            animator.end();
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mWidth = getRefreshLayout().getFinalOffset();
        mHeight = mWidth;
        bounds.bottom = bounds.top + mHeight;
        mBounds = new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setColor(getAlphaColor(textColor));
        canvas.drawRect(mCenterX-mPaintTipHalfWidth, mBounds.top, mCenterX + mPaintTipHalfWidth, mBounds.top + 3 * mPadding, mPaint);
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(CENTER);
        mPaint.setColor(textColor);
        canvas.drawText(mDrawTextFailed, mBounds.centerX(), mBounds.centerY() - 1.5f * mPadding - (mSize - mTextSize) / 2, mPaint);
    }

    protected void setSize(int size) {
        mTextSize = size;
        invalidateSelf();
    }

    protected float getSize() {
        return mTextSize;
    }

    private Animator generateAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        //alpha animation
        ObjectAnimator sizeAnimator = ObjectAnimator.ofInt(this, "size", (int) (mSize * 0.6f), mSize);
        sizeAnimator.setDuration(200);
        sizeAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mTipAnimation = ValueAnimator.ofFloat(0, mCenterX);
        mTipAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPaintTipHalfWidth = (float) animation.getAnimatedValue();

            }
        });
        mTipAnimation.setDuration(200);
        mTipAnimation.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(sizeAnimator, mTipAnimation);
        return animatorSet;
    }

    /**
     * 显示刷新结果时的半透明背景
     *
     * @param color
     * @return
     */
    private int getAlphaColor(int color) {
        return 0x19000000 | (0x00FFFFFF & color);
    }

    /**
     * 从设计尺寸转化为显示尺寸
     * @param designSize
     * @return
     */
    public float getPxForDifferentScreen(float designSize) {
        return designSize / 2 * DensityUtil.getDensity();
    }

    public void setText(String text) {
        mDrawTextFailed = text;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public void setPercent(float percent) {

    }

    @Override
    public void setColorSchemeColors(int[] colorSchemeColors) {

    }

    @Override
    public void offsetTopAndBottom(int offset) {

    }
}
