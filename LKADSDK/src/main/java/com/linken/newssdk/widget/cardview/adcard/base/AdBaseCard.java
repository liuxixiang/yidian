package com.linken.newssdk.widget.cardview.adcard.base;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.R;
import com.linken.newssdk.adapter.MultipleItemQuickAdapter;
import com.linken.newssdk.core.ad.AdvertisementUtil;
import com.linken.newssdk.core.ad.ViewReportManager;
import com.linken.newssdk.data.ad.ADConstants;
import com.linken.newssdk.libraries.bra.BaseViewHolder;
import com.linken.newssdk.theme.ThemeChangeInterface;
import com.linken.newssdk.theme.ThemeManager;
import com.linken.newssdk.utils.DensityUtil;
import com.linken.newssdk.utils.action.AdActionHelper;
import com.linken.newssdk.utils.support.ImageDownloaderConfig;
import com.linken.newssdk.widget.feedback.ad.AdBadFeedBackWindow;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by patrickleong on 4/13/15.
 */
public abstract class AdBaseCard extends BaseViewHolder implements View.OnClickListener,
        ContentAdCard, ThemeChangeInterface {

    protected TextView mTitle;
    protected TextView mSource;
    protected TextView mTag;
    protected TextView mCount;
    protected View mFeedback;
    protected View mTencentLogo;
    protected String mDocId;
    protected AdvertisementCard mAdCard;
    protected AdActionHelper mActionHelper;
    protected boolean mKeepDefaultTag = false;//是否保持xml中的tag样式
    protected View mDivider;
    protected ViewReportManager reportManager;
    protected MultipleItemQuickAdapter mAdapter;

    public AdBaseCard(MultipleItemQuickAdapter adapter, final View itemView) {
        super(itemView);
        this.mAdapter = adapter;
        itemView.setOnClickListener(this);
        reportManager = new ViewReportManager();
        itemView.setTag(R.id.ydsdk_ad_view_report, reportManager);
        mDivider = itemView.findViewById(R.id.middleDivider); //分割线

        mTitle = itemView.findViewById(R.id.title);
        mTag = itemView.findViewById(R.id.tag);
        mSource = itemView.findViewById(R.id.source);
        mTencentLogo = itemView.findViewById(R.id.ad_tencent_logo);

        mCount = itemView.findViewById(R.id.txtCount);
        mFeedback = itemView.findViewById(R.id.btnToggle);
        if (mFeedback != null) {
            mFeedback.setOnClickListener(this);
        }
        ThemeManager.registerThemeChange(this);
    }

    @Override
    public void onBind(AdvertisementCard card, String docId) {
        reportManager.setAdCard(card);
        mAdCard = card;
        mAdCard.startAppStoreStatus = -1; //重置状态
        mDocId = docId;
        itemView.setTag(mAdCard);    //For view event report.

        if(mFeedback != null){
            if (supportFeedback()) {
                mFeedback.setVisibility(View.VISIBLE);
            } else {
                mFeedback.setVisibility(View.GONE);
            }
        }

        if (mSource != null) {
            mSource.setText("");
            if (!TextUtils.isEmpty(mAdCard.source) && !TextUtils.isEmpty(mAdCard.source.trim())) {
                mSource.setText(mAdCard.source);
            }
        }

        if (mTitle != null) {
            if (!TextUtils.isEmpty(mAdCard.title)) {
                mTitle.setVisibility(VISIBLE);
                mTitle.setText(mAdCard.title);
            } else if (!TextUtils.isEmpty(mAdCard.summary)) {
                mTitle.setVisibility(VISIBLE);
                mTitle.setText(mAdCard.summary);
            } else {
                mTitle.setVisibility(GONE);
            }
        }

        if (mCount != null && !TextUtils.isEmpty(mAdCard.actionDescription)) {
            mCount.setText(mAdCard.actionDescription);
            mCount.setVisibility(VISIBLE);
        }

        loadImage();

        if (mTag != null) {
            if (mAdCard.noAdTag) {
                mTag.setVisibility(View.GONE);
                if (mSource != null) {
                    mSource.setPadding(0, mSource.getPaddingTop(),
                            mSource.getPaddingRight(), mSource.getPaddingBottom());
                }
            } else {
                mTag.setVisibility(VISIBLE);

                if (mSource != null) {
                    mSource.setPadding((int) (10 * DensityUtil.getScaledDensity()),
                            mSource.getPaddingTop(), mSource.getPaddingRight(), mSource.getPaddingBottom());
                }

                mAdCard.adTag = TextUtils.isEmpty(mAdCard.adTag) ? mTag.getResources().getString(R.string.ydsdk_ad_default_tag) : mAdCard.adTag;
                mTag.setText(mAdCard.adTag);
                if (!mKeepDefaultTag) {
                    setTagBackground();
                    mTag.setTextColor(getTagColor());
                }
            }

        }

        if (AdvertisementCard.isTencentAd(mAdCard)) {
            if (mTencentLogo != null) {
                mTencentLogo.setVisibility(VISIBLE);
            }
        } else {
            if (mTencentLogo != null) {
                mTencentLogo.setVisibility(GONE);
            }
        }
        onThemeChanged(ThemeManager.getTheme());
    }

    private boolean supportFeedback() {
        int template = mAdCard == null ? -1 : mAdCard.getTemplate();
        return template == 3 || template == 4 || template == 40 || template == 15 || template == 126 || template == 131 || template == 140;
    }

    /**
     * 设置广告tag的背景色／图
     * @return 描边和文字的颜色
     */
    protected void setTagBackground() {
        GradientDrawable drawable = (GradientDrawable) itemView.getResources().getDrawable(R.drawable.ydsdk_ad_dynamic_tag);
        drawable.setStroke(1, getTagColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTag.setBackground(drawable);
        } else {
            mTag.setBackgroundDrawable(drawable);
        }
    }

    protected int getTagColor() {
        int color = itemView.getResources().getColor(R.color.ydsdk_ad_tag_text);
        if (!TextUtils.isEmpty(mAdCard.flagColor)) {
            try {
                color = Color.parseColor(mAdCard.flagColor);
            } catch (Exception e) {
                Log.e(ADConstants.ADVERTISEMENT_LOG, "Can't parse color : " + mAdCard.flagColor + " for AdCard " + mAdCard.toString());
            }
        }
        return color;
    }

    /**
     * 广告底部是否显示分割线
     * @param hasDivider
     */
    @Override
    public void setDivider(boolean hasDivider) {

    }

    public abstract void loadImage();

    protected AdActionHelper getAdHelper(AdvertisementCard card) {
        if (mActionHelper == null) {
            mActionHelper = AdActionHelper.newInstance(card);
        } else {
            mActionHelper.setAdCard(card);
        }
        return mActionHelper;
    }

    /**
     * 发曝光n秒日志（点击算有效曝光）
     */
    protected void doViewNSReport() {
        reportManager.doViewReport();
        //todo 暂时注释广告联盟
    }

    @Override
    public void onClick(View v) {
        doViewNSReport();
        if (v.getId() == R.id.btnToggle) {
            handleShowFeedbackView(itemView, mFeedback);
        } else {
            launchActivity();
        }
    }

    protected void launchActivity() {
        getAdHelper(mAdCard).openLandingPage(itemView.getContext());
    }

    protected void handleShowFeedbackView(View originView, View clickedView) {
        AdBadFeedBackWindow feedbackWindow = new AdBadFeedBackWindow(itemView.getContext(), mAdCard);
        feedbackWindow.setAfterTellReasonListener(new AdBadFeedBackWindow.AfterTellReasonListener() {
            @Override
            public void afterTellReason(boolean reasonGiven) {
                deleteAdDoc();
            }
        }).setDislikeReasonForADListener(new AdBadFeedBackWindow.DislikeReasonForADListener() {
            @Override
            public void dislikeReasonForAD(String reason, String reasonsCode) {
                if (TextUtils.isEmpty(reason)) {
                    return;
                }
                AdvertisementUtil.reportDislikeEvent(mAdCard, reason, reasonsCode);
            }
        });
        feedbackWindow.handleBadFeedBack(originView, clickedView);
    }

    private void deleteAdDoc() {
        if (mAdapter != null) {
            mAdapter.removeRow(getAdapterPosition());
        }
    }


    @Override
    public void onThemeChanged(int theme) {

        if (mTitle != null){
            TypedArray ta = null;
            try {
                ta = itemView.getContext().getResources().obtainTypedArray(theme);
            } catch (Exception e) {
            }

            if (ta == null) {
                return;
            }

            int color = ta.getColor(R.styleable.NewsSDKTheme_newssdk_common_font_color, 0xffffff);
            float fontSize = ta.getDimension(R.styleable.NewsSDKTheme_newssdk_common_font_size, 12);


            int dividerColor = ta.getColor(R.styleable.NewsSDKTheme_newssdk_card_divider_color, 0xb8b8b8);
            int dividerHeight = (int) ta.getDimension(R.styleable.NewsSDKTheme_newssdk_card_divider_height, 1);

            ViewGroup.LayoutParams divierLayoutParams = mDivider.getLayoutParams();
            divierLayoutParams.height = dividerHeight;
            mDivider.setLayoutParams(divierLayoutParams);
            mDivider.setBackgroundColor(dividerColor);
            ta.recycle();
            mTitle.setTextColor(color);
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);

            color = ThemeManager.getColor(itemView.getContext(), theme, R.styleable.NewsSDKTheme_newssdk_card_img_bg_color, 0xffffff);
            if (color != 0xffffff) {
                ImageDownloaderConfig.changeThemeForImage(color);
            }
        }
    }
}
