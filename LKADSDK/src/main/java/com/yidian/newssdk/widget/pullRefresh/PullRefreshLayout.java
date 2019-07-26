package com.yidian.newssdk.widget.pullRefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yidian.newssdk.R;
import com.yidian.newssdk.theme.ThemeChangeInterface;
import com.yidian.newssdk.theme.ThemeManager;
import com.yidian.newssdk.utils.DensityUtil;
import com.yidian.newssdk.utils.LogUtils;
import com.yidian.newssdk.utils.VibrateUtils;

import java.security.InvalidParameterException;

/**
 * Created by baoyz on 14/10/30.
 */
public class PullRefreshLayout extends ViewGroup implements ThemeChangeInterface {

    private static final String TAG = PullRefreshLayout.class.getSimpleName();
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int DRAG_MAX_DISTANCE = 70;
    private static final int DRAG_ADJUST_DISTANCE = DensityUtil.dp2px(33);
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .3f;
    private static final int LOADING_DISTANCE = 30;  //loading 位置

    public static final int STYLE_LESHI = 0;
    public static final int STYLE_CIRCLES = 1;
    public static final int STYLE_WATER_DROP = 2;
    public static final int STYLE_RING = 3;
    public static final int STYLE_SMARTISAN = 4;

    private boolean hasShock;
    private View mTarget;
    private ImageView mRefreshView;
    private Interpolator mDecelerateInterpolator;
    private int mTouchSlop;
    private boolean mLoading;
    private int mSpinnerFinalOffset;
    private int mTotalDragDistance;
    private int mLoadingFinalOffset;
    private RefreshDrawable mRefreshDrawable;
    private RefreshDrawable mTipDrawable;
    private int mCurrentOffsetTop;
    private boolean mRefreshing;
    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private float mInitialMotionY;
    private int mFrom;
    private boolean mNotify;
    private OnRefreshListener mListener;
    private int[] mColorSchemeColors;

    public int mDurationToStartPosition;
    public int mDurationToCorrectPosition;
    private int mInitialOffsetTop;
    private boolean mDispatchTargetTouchDown;
    private float mDragPercent;
    private final Handler mHandler = new Handler();
    private int refreshTipTextColor;

    public PullRefreshLayout(Context context) {
        this(context, null);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        ThemeManager.registerThemeChange(this);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        int defaultDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mDurationToStartPosition = defaultDuration;
        mDurationToCorrectPosition = defaultDuration;
        mSpinnerFinalOffset = mTotalDragDistance = dp2px(DRAG_MAX_DISTANCE);
        mLoadingFinalOffset = dp2px(LOADING_DISTANCE);

        mColorSchemeColors = new int[]{Color.rgb(0xC9, 0x34, 0x37)};

        mRefreshView = new ImageView(context);
        setRefreshStyle(STYLE_LESHI);
        mRefreshView.setVisibility(View.GONE);
        addView(mRefreshView, 0);
        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        onThemeChanged(ThemeManager.getTheme());
    }

    public void setColorSchemeColors(int... colorSchemeColors) {
        mColorSchemeColors = colorSchemeColors;
        mRefreshDrawable.setColorSchemeColors(colorSchemeColors);
    }

    public void setColor(int color) {
        setColorSchemeColors(color);
    }

    public void setRefreshStyle(int type) {
        setRefreshing(false);
        switch (type) {
            case STYLE_LESHI:
                mRefreshDrawable = new LeDrawable(getContext(), this);
                mTipDrawable = new TipDrawable(getContext(), this);
                break;
            default:
                throw new InvalidParameterException("Type does not exist");
        }
        mRefreshDrawable.setColorSchemeColors(mColorSchemeColors);
        mRefreshView.setImageDrawable(mRefreshDrawable);
    }

    public void setTipBackColor(int color) {
        mTipDrawable.setColor(color);
    }

    public void setLoadingFinalOffset(int offset){
        mLoadingFinalOffset = offset;
    }

    public void setTipResult(String tip, boolean refreshing) {
        ((TipDrawable) mTipDrawable).setText(tip);
        ((TipDrawable) mTipDrawable).setTextColor(refreshTipTextColor);
        mLoading = true;
        mNotify = false;
        mRefreshing = false;

        animateOffsetToTipPosition();
    }

    private void animateOffsetToTipPosition() {
        LogUtils.d(TAG, "animateOffsetToTipPosition");
        mFrom = mCurrentOffsetTop;
        mAnimateToTipPosition.reset();
        mAnimateToTipPosition.setDuration(mDurationToCorrectPosition);
        mAnimateToTipPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToTipPosition.setAnimationListener(mLoadingListener);
        mRefreshView.clearAnimation();
        LogUtils.d(TAG, "animateOffsetToTipPosition--> startAnimation");
        mRefreshView.startAnimation(mAnimateToTipPosition);
    }

    public int getFinalOffset() {
        return mSpinnerFinalOffset;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ensureTarget();
        if (mTarget == null)
            return;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTarget.measure(widthMeasureSpec, heightMeasureSpec);
        mRefreshView.measure(widthMeasureSpec, heightMeasureSpec);
//        mRefreshView.measure(MeasureSpec.makeMeasureSpec(mRefreshViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mRefreshViewHeight, MeasureSpec.EXACTLY));
    }

    private void ensureTarget() {
        if (mTarget != null) {
            return;
        }
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != mRefreshView) {
                    mTarget = child;
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!isEnabled() || (canChildScrollUp() && !mRefreshing)) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mRefreshing) {
                    setTargetOffsetTop(0, true);
                }
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
                mInitialOffsetTop = mCurrentOffsetTop;
                mDispatchTargetTouchDown = false;
                mDragPercent = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialMotionY;
                if (mRefreshing) {
                    mIsBeingDragged = !(yDiff < 0 && mCurrentOffsetTop <= 0);
                } else if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev);
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = y - mInitialMotionY;
                int targetY;
                if (mRefreshing || mLoading) {
                    targetY = (int) (mInitialOffsetTop + yDiff);
                    if (canChildScrollUp()) {
                        targetY = -1;
                        mInitialMotionY = y;
                        mInitialOffsetTop = 0;
                        if (mDispatchTargetTouchDown) {
                            mTarget.dispatchTouchEvent(ev);
                        } else {
                            MotionEvent obtain = MotionEvent.obtain(ev);
                            obtain.setAction(MotionEvent.ACTION_DOWN);
                            mDispatchTargetTouchDown = true;
                            mTarget.dispatchTouchEvent(obtain);
                        }
                    } else {
                        if (targetY < 0) {
                            if (mDispatchTargetTouchDown) {
                                mTarget.dispatchTouchEvent(ev);
                            } else {
                                MotionEvent obtain = MotionEvent.obtain(ev);
                                obtain.setAction(MotionEvent.ACTION_DOWN);
                                mDispatchTargetTouchDown = true;
                                mTarget.dispatchTouchEvent(obtain);
                            }
                            targetY = 0;
                        }  else if (mLoading && targetY > mLoadingFinalOffset) {
                            targetY = mLoadingFinalOffset;
                        }  else if (targetY > mTotalDragDistance) {
                            targetY = mTotalDragDistance;
                        } else {
                            if (mDispatchTargetTouchDown) {
                                MotionEvent obtain = MotionEvent.obtain(ev);
                                obtain.setAction(MotionEvent.ACTION_CANCEL);
                                mDispatchTargetTouchDown = false;
                                mTarget.dispatchTouchEvent(obtain);
                            }
                        }
                    }
                } else {
                    final float scrollTop = yDiff * DRAG_RATE;
                    float originalDragPercent = scrollTop / mTotalDragDistance;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    mDragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
                    float slingshotDist = mSpinnerFinalOffset;
                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;
                    targetY = (int) ((slingshotDist * mDragPercent) + extraMove);
                    if (mRefreshView.getVisibility() != View.VISIBLE) {
                        mRefreshView.setVisibility(View.VISIBLE);
                    }
                    if (scrollTop < mTotalDragDistance) {
                        mRefreshDrawable.setPercent(mDragPercent);
                    } else {
                        if (!hasShock) {
                            VibrateUtils.Vibrate();
                            hasShock = true;
                        }
                        mRefreshDrawable.setPercent(1f);
                    }
                }
                if ((mRefreshing || mLoading) &&  targetY > mSpinnerFinalOffset - DRAG_ADJUST_DISTANCE) {
                    break;
                } else {
                    setTargetOffsetTop(targetY - mCurrentOffsetTop, true);
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                hasShock = false;
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                if (mRefreshing || mLoading) {
                    if (mDispatchTargetTouchDown) {
                        mTarget.dispatchTouchEvent(ev);
                        mDispatchTargetTouchDown = false;
                    }
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;

                if (mRefreshing || mLoading) {
                    animateOffsetToAdjustPosition();
                } else if (overscrollTop > mTotalDragDistance) {
                    setRefreshing(true, true);
                } else {
                    LogUtils.d(TAG, "onTouchEvent clear");
                    animateOffsetToStartPosition();
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

    public void setDurations(int durationToStartPosition, int durationToCorrectPosition) {
        mDurationToStartPosition = durationToStartPosition;
        mDurationToCorrectPosition = durationToCorrectPosition;
    }

    private void animateOffsetToStartPosition() {
        mFrom = mCurrentOffsetTop;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(mDurationToStartPosition);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(mToStartListener);
        LogUtils.d(TAG, "animateOffsetToStartPosition ---> clearAnimation");
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToStartPosition);
    }

    private void animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(mDurationToCorrectPosition);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToCorrectPosition.setAnimationListener(mRefreshListener);
        mRefreshView.clearAnimation();
        mRefreshing = true;
        mRefreshView.startAnimation(mAnimateToCorrectPosition);
    }

    private void animateOffsetToAdjustPosition() {
        mFrom = mCurrentOffsetTop;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(mDurationToCorrectPosition);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToCorrectPosition.setAnimationListener(mAdjustListener);
        mRefreshView.clearAnimation();
        LogUtils.d(TAG, "animateOffsetToAdjustPosition");
        mRefreshView.startAnimation(mAnimateToCorrectPosition);
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget = mSpinnerFinalOffset - DRAG_ADJUST_DISTANCE;
            int targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTop();
            setTargetOffsetTop(offset, false /* requires update */);
        }
    };

    private final Animation mAnimateToTipPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget = mLoadingFinalOffset;
            int targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTop();
            setTargetOffsetTop(offset, false /* requires update */);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        int offset = targetTop - mTarget.getTop();
        setTargetOffsetTop(offset, false);


        if (mLoading) {
            int targetTop1 = -(int) (mLoadingFinalOffset * interpolatedTime);
            int offset1 = targetTop1 - mRefreshView.getTop();
            mRefreshView.offsetTopAndBottom(offset1);
        }

        mRefreshDrawable.setPercent(mDragPercent * (1 - interpolatedTime));
    }

    public void wrap(View... child) {
        LayoutParams childParams = child[0].getLayoutParams();
        if (childParams != null) {
            this.setLayoutParams(childParams);
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        child[0].setLayoutParams(lp);

        this.addView(child[0]);
    }

    private int paddingHorizontal;
    private int paddingVertical;
    public void setPaddingHorizontal(int paddingHorizontal){
        this.paddingHorizontal = paddingHorizontal;
    }

    public void setPaddingVertical(int paddingVertical){
        this.paddingVertical = paddingVertical;
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    public void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            LogUtils.d(TAG, "setRefreshing = " + refreshing);
            mRefreshing = refreshing;
            if (mRefreshing) {
                mRefreshDrawable.setPercent(1f);
                LogUtils.d(TAG, "setRefreshing inner =" + mRefreshing);
                animateOffsetToCorrectPosition();
            } else {
                animateOffsetToStartPosition();
            }
        }
    }

    public boolean isRefreshing() {
        return mRefreshing;
    }

    private Animation.AnimationListener mLoadingListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            LogUtils.d(TAG, "onAnimationStart");
            mRefreshView.setVisibility(View.VISIBLE);
            mRefreshDrawable.stop();
            mRefreshView.setImageDrawable(mTipDrawable);
            mTipDrawable.start();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mCurrentOffsetTop = mTarget.getTop();
            mTipDrawable.stop();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d(TAG, "mLoadingListener clear");
                    animateOffsetToStartPosition();
                }
            }, 1500);
        }
    };

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mRefreshView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                mRefreshDrawable.start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
            } else {
                if (mLoading) {
                    animateOffsetToTipPosition();
                } else {
                    mRefreshDrawable.stop();
                    mRefreshView.setVisibility(View.GONE);
                    LogUtils.d(TAG, "mRefreshListener clear");
                    animateOffsetToStartPosition();
                }

            }
            mCurrentOffsetTop = mTarget.getTop();
        }
    };

    private Animation.AnimationListener mAdjustListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mRefreshView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                mRefreshDrawable.start();
            } else {
                mRefreshDrawable.stop();
                mRefreshView.setVisibility(View.GONE);
                LogUtils.d(TAG, "mAdjustListener clear");
                animateOffsetToStartPosition();
            }
            mCurrentOffsetTop = mTarget.getTop();
        }
    };



    private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mRefreshDrawable.stop();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
//            mRefreshDrawable.stop();
//            mRefreshView.setVisibility(View.GONE);
//            mCurrentOffsetTop = mTarget.getTop();
            mLoading = false;
            mRefreshView.setImageDrawable(mRefreshDrawable);
            mCurrentOffsetTop = mTarget.getTop();
            int top = mRefreshView.getTop();
            mRefreshView.offsetTopAndBottom(0 - top);
        }
    };

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
//        mRefreshView.bringToFront();
        mTarget.offsetTopAndBottom(offset);
        mCurrentOffsetTop = mTarget.getTop();
        mRefreshDrawable.offsetTopAndBottom(offset);
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        ensureTarget();
        if (mTarget == null)
            return;

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        mTarget.layout(left + this.paddingHorizontal, top + mTarget.getTop(), left - this.paddingHorizontal + width - right, top + height - bottom + mTarget.getTop());
        mRefreshView.layout(left, top, left + width - right, top + height - bottom);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public void reset() {
        mCurrentOffsetTop = mTarget.getTop();
        mTipDrawable.stop();
        moveToStart(1);
        animateOffsetToStartPosition();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
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

        int tipTextcolor = ta.getColor(R.styleable.NewsSDKTheme_newssdk_refresh_tip_color, 0xFF5B5F);
        this.refreshTipTextColor = tipTextcolor;
        ((TipDrawable) mTipDrawable).setTextColor(refreshTipTextColor);
        mRefreshDrawable.setColor(refreshTipTextColor);

        ta.recycle();
        invalidate();
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}
