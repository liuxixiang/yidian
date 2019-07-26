package com.linken.newssdk.libraries.flyco.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.linken.newssdk.utils.DensityUtil;


public class ClipPagerTitleView extends View  {
    private String mText;
    private int mTextColor;
    private int mClipColor;
    private boolean mLeftToRight;
    private float mClipPercent;

    private Paint mPaint;
    private Rect mTextBounds = new Rect();

    public ClipPagerTitleView(Context context) {
        super(context);
        init(context);
    }

    public ClipPagerTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClipPagerTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        int textSize = DensityUtil.dp2px(16);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(textSize);
        int padding = DensityUtil.dp2px(10);
        setPadding(padding, 0, padding, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureTextBounds();
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int result = size;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                int width = mTextBounds.width() + getPaddingLeft() + getPaddingRight();
                result = Math.min(width, size);
                break;
            case MeasureSpec.UNSPECIFIED:
                result = mTextBounds.width() + getPaddingLeft() + getPaddingRight();
                break;
            default:
                break;
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result = size;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                int height = mTextBounds.height() + getPaddingTop() + getPaddingBottom();
                result = Math.max(height, size);
                break;
            case MeasureSpec.UNSPECIFIED:
                result = mTextBounds.height() + getPaddingTop() + getPaddingBottom();
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = (getWidth() - mTextBounds.width()) / 2;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        int y = (int) ((getHeight() - fontMetrics.bottom - fontMetrics.top) / 2);

        // 画底层
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        mPaint.setColor(mTextColor);
        if (mLeftToRight) {
            canvas.clipRect(getWidth() * mClipPercent, 0, getWidth(), getHeight());
        } else {
            canvas.clipRect(0, 0, getWidth() * (1 - mClipPercent), getHeight());
        }
        canvas.drawText(mText, x, y, mPaint);
        canvas.restore();

        // 画clip层
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        if (mLeftToRight) {
            canvas.clipRect(0, 0, getWidth() * mClipPercent, getHeight());
        } else {
            canvas.clipRect(getWidth() * (1 - mClipPercent), 0, getWidth(), getHeight());
        }
        mPaint.setColor(mClipColor);
        canvas.drawText(mText, x, y, mPaint);
        canvas.restore();
    }

    public void onSelected(int index, int totalCount) {
    }

    public void onDeselected(int index, int totalCount) {
    }

    public void onLeave(float leavePercent, boolean leftToRight) {
        mLeftToRight = !leftToRight;
        mClipPercent = 1.0f - leavePercent;
        invalidate();
    }

    public void onEnter(float enterPercent, boolean leftToRight) {
        mLeftToRight = leftToRight;
        mClipPercent = enterPercent;
        invalidate();
    }

    private void measureTextBounds() {
        mPaint.getTextBounds(mText, 0, mText == null ? 0 : mText.length(), mTextBounds);
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        requestLayout();
    }

    public float getTextSize() {
        return mPaint.getTextSize();
    }

    public void setTextSize(float textSize) {
        mPaint.setTextSize(textSize);
        requestLayout();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        invalidate();
    }

    public int getClipColor() {
        return mClipColor;
    }

    public void setClipColor(int clipColor) {
        mClipColor = clipColor;
        invalidate();
    }

    public int getContentLeft() {
        int contentWidth = mTextBounds.width();
        return getLeft() + getWidth() / 2 - contentWidth / 2;
    }

    public int getContentTop() {
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 - contentHeight / 2);
    }

    public int getContentRight() {
        int contentWidth = mTextBounds.width();
        return getLeft() + getWidth() / 2 + contentWidth / 2;
    }

    public int getContentBottom() {
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 + contentHeight / 2);
    }
}
