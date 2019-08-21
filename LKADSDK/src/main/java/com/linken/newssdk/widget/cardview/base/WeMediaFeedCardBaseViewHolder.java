package com.linken.newssdk.widget.cardview.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.linken.newssdk.R;
import com.linken.newssdk.SDKContants;
import com.linken.newssdk.YdCustomConfigure;
import com.linken.newssdk.adapter.MultipleItemQuickAdapter;
import com.linken.newssdk.utils.XClickUtil;
import com.linken.newssdk.utils.support.ImageDownloaderConfig;
import com.linken.newssdk.core.detail.article.gallery.YdGalleryActivity;
import com.linken.newssdk.core.detail.article.news.YdNewsActivity;
import com.linken.newssdk.core.detail.article.video.YdVideoActivity;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.card.base.ContentCard;
import com.linken.newssdk.data.card.news.News;
import com.linken.newssdk.utils.support.ImageLoaderHelper;
import com.linken.newssdk.theme.ThemeChangeInterface;
import com.linken.newssdk.theme.ThemeManager;
import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.DensityUtil;
import com.linken.newssdk.widget.cardview.newscard.SmallImageCardViewHolder;
import com.linken.newssdk.widget.feedback.normal.CardBottomPanelWrapper;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by yangjuan on 2017/3/28.
 */

public abstract class WeMediaFeedCardBaseViewHolder extends WeMediaBaseRecylerHolder implements View.OnClickListener, ThemeChangeInterface {
    protected ContentCard mCard;
    protected Context mContext;
    private int mSingleRightPicWidth;
    private int mSingleRightPicHeight;
    private float mDisplayDensity = 2.0f;
    private boolean mbExposeTimeStamp = false;
    private int mTitleLineWidth;
    protected View middleDivider = null;
    protected TextView tvTitle = null;
    protected MultipleItemQuickAdapter multipleItemQuickAdapter;
    protected boolean mbDelayLoad = false;//是否需要延迟加载Bottom Panel
    private View mOuterBottomPanel;//出现在最底部的bottom panel
    private View mInnerBottomPanel;//出现在图片左边的bottom panel
    private View mContentPanel;
    protected CardBottomPanelWrapper mBottomPanelWrapper;
    private boolean mbBottomPanelShown = false; //Bottom Panel是否已经显示过
    private static int RIGHT_PIC_MAX_TITLE_LINE = 3;
    private int mMaxBottomPanelHeight;

    protected int mLeftPadding = 0;
    private int mTxtAndPicMarginInDp = 0; //文字和单图的距离
    protected int mScreenWidth = 0;

    private ViewTreeObserver.OnGlobalLayoutListener mListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (tvTitle.getHeight() < 1) { //还没有能正确地获取TextView setText后的高度，行数信息，还需要继续监听
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                tvTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);//将ViewTreeObserver移除
            } else {
                tvTitle.getViewTreeObserver().removeGlobalOnLayoutListener(this);//将ViewTreeObserver移除

            }
            if (!mbBottomPanelShown) {//如果Bottom Panel没有被显示
                mbBottomPanelShown = true;
                showBottomPanel(tvTitle.getHeight(), 2);
            }
        }
    };


    public WeMediaFeedCardBaseViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        mDisplayDensity = DensityUtil.getScaledDensity();

        mLeftPadding = (int) ContextUtils.getApplicationContext().getResources().getDimension(R.dimen.ydsdk_news_list_padding_left_ns);//信息流两边的留白距离

        mScreenWidth = Math.min(DensityUtil.getScreenHeight(ContextUtils.getApplicationContext()), DensityUtil.getScreenWidth(ContextUtils.getApplicationContext()));//屏幕宽度取宽、高的最小值，防止横屏造成的问题
        mSingleRightPicWidth = (int) ContextUtils.getApplicationContext()
                .getResources().getDimension(R.dimen.ydsdk_news_list_small_img_width_ns);


        mSingleRightPicHeight = (int) (mSingleRightPicWidth * 0.67f);//图片的宽高比是3:2
        mTxtAndPicMarginInDp = 12;
        mTitleLineWidth = (int) (mScreenWidth - mLeftPadding * 2 - mSingleRightPicWidth - mTxtAndPicMarginInDp * mDisplayDensity);

        //根据不同的density，对留白也做出限制
        int emptySpace = mDisplayDensity >= 3 ? 21 : 23;
        mMaxBottomPanelHeight = (int) (emptySpace * mDisplayDensity); // inner bottom panel height is 20dp, leave 3 dp or 1 dp as empty space

        middleDivider = findView(R.id.middleDivider); //分割线
        mContentPanel = findView(R.id.content_panel);
        tvTitle = findView(R.id.news_title);

        ThemeManager.registerThemeChange(this);
    }

    private void initBottomUI(View layoutView) {
        if (layoutView == null || mbDelayLoad) {// 如果延迟加载，则返回
            return;
        }
        if (layoutView instanceof CardBottomPanelWrapper) {
            mBottomPanelWrapper = (CardBottomPanelWrapper) layoutView;
        } else {
            mBottomPanelWrapper = (CardBottomPanelWrapper) layoutView.findViewById(R.id.buttom_panel_wrapper);
        }

        mBottomPanelWrapper.initPanelWidget(null, mCard, mTitleLineWidth, mbExposeTimeStamp, 0);
    }

    public void onBind(Card item, MultipleItemQuickAdapter adapter) {
        this.multipleItemQuickAdapter = adapter;
        if (item == null) {
            return;
        }
        mCard = (ContentCard) item;
        if (mCard == null) {
            return;
        }

        if (middleDivider != null) {
            if (item.getHideDivider()) {
                middleDivider.setVisibility(View.INVISIBLE);
            } else {
                middleDivider.setVisibility(View.VISIBLE);
            }
        }

        setData(item);

        if (mbDelayLoad) {
            setNeedDerralLoad(); // 这重新加载的布局
        }
        if (pictureIsGone()) {
            mTitleLineWidth = (int) (mScreenWidth - mLeftPadding * 2 - mTxtAndPicMarginInDp * mDisplayDensity);
        } else {
            mTitleLineWidth = (int) (mScreenWidth - mLeftPadding * 2 - mSingleRightPicWidth - mTxtAndPicMarginInDp * mDisplayDensity);
        }
        initBottomUI(itemView);
        if (middleDivider != null) {
//            if (item.hideTopLine) {
//                middleDivider.setVisibility(View.GONE);
//            } else {
//                middleDivider.setVisibility(View.VISIBLE);
//            }
        }

        if (!TextUtils.isEmpty(mCard.tag_icon) && !mCard.tag_icon.startsWith("http")) {
            mCard.tag_icon = SDKContants.STATIC_IMAGE_SERVER + mCard.tag_icon;
        }

        if (tvTitle != null) {
            // set title
            tvTitle.setTextSize(YdCustomConfigure.getInstance().getFontSize());
            tvTitle.setText(getHighlightString(mCard.title));
            String title = tvTitle.getText().toString();

            tvTitle.setText(title);
            boolean hasRead = false;
//                    GlobalDataCache.getInstance().isDocAlreadyRead(
//                    mCard.isSticky() ? mCard.getStickiedDocId() : mCard.id);
            setTitleColor(tvTitle, hasRead);
        }
        _onBind();
        if (mbDelayLoad) {
            String title = tvTitle.getText().toString();
            //如果需要延迟加载，则 1. 给View加一个ViewTreeObserver，在OnGlobalLayoutListener中能计算出精确高度，作为无法预估出合理值的保护
            tvTitle.getViewTreeObserver().addOnGlobalLayoutListener(mListener);
            int lineCount = getLineCount(title);//计算出所占行数
            //2. 计算预估Title所占的高度 = 行数 * 每行所占高度 + 行间距的预估
            int height = (int) (lineCount * tvTitle.getLineHeight() + (lineCount - 1) * 2 * mDisplayDensity); //2是XML中定义的LineSpaceExtra
            if (height > 0) {//如果能预估出一个合理值(>0)
                if (!mbBottomPanelShown) {//如果Bottom Panel没有被显示
                    mbBottomPanelShown = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        tvTitle.getViewTreeObserver().removeOnGlobalLayoutListener(mListener);//将ViewTreeObserver移除
                    } else {
                        tvTitle.getViewTreeObserver().removeGlobalOnLayoutListener(mListener);//将ViewTreeObserver移除
                    }
                    showBottomPanel(height, lineCount);
                }
            }
        } else {
            if (tvTitle != null) {
                String title = tvTitle.getText().toString();
                adjustTitlePadding(tvTitle, title);
            }
            bindBottomUI();//不要延迟加载则直接加载Bottom Panel的数据
        }

        onThemeChanged(ThemeManager.getTheme());
    }

    protected abstract void setData(Card item);

    /**
     * 展示Bottom Panel，根据右单图的高度，title部分的高度和Bottom Panel预估的高度选择到底inflate哪个panel
     *
     * @param textHeight：Title部分粗略估计所占高度
     */
    private void showBottomPanel(int textHeight, int lineCount) {
        //如果没有右单图显示，则在Card的最底部加载bottom panel
        //如果标题行数超过2行，则需要在Card的最底部加载bottom panel；反之需要在图的左边加载bottom panel
        boolean needInflateOuterBottomPanel = lineCount > (RIGHT_PIC_MAX_TITLE_LINE - 1);
        // 这里使用局部变量，不可以使用mSingleRightPicHeight，否则就会修改了mSingleRightPicHeight 的值，后期复用时候会出问题。
        int picHeight = mSingleRightPicHeight;
        if (pictureIsGone()) {//  pictureIsGone 和 needInflateOuterBottomPanel 不等价，注意一下
            needInflateOuterBottomPanel = true;
            picHeight = 0;
        } else {
            picHeight = (int) (mSingleRightPicWidth * 0.67f);//图片的宽高比是3:2
        }
        View panel = findView(R.id.content_panel);//小图展示区域
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) panel.getLayoutParams();
        //整个展示区域高度（不包括out panel）取左（title+inner panel（需要out panel时高度为0）+ paddingTop）、右区域（singleRightPic+paddingTop）中的最大值
        layoutParams.height =
                Math.max((needInflateOuterBottomPanel ? 0 : mMaxBottomPanelHeight)
                        + textHeight, picHeight) + panel.getPaddingTop();
        panel.setLayoutParams(layoutParams);

        ViewStub panelStub = null;
        if (needInflateOuterBottomPanel) {
            mbExposeTimeStamp = true;
            if (mOuterBottomPanel != null) { //如果Outer Bottom Panel Stub对应的View已经被inflate，则只用将其设为可见
                mOuterBottomPanel.setVisibility(View.VISIBLE);
            } else {
                panelStub = findView(R.id.outer_bottom_panel_stub);
                if (panelStub != null) {
                    panelStub.inflate();
                }
                mOuterBottomPanel = findView(R.id.outer_bottom_panel);
            }

            if (mContentPanel != null) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentPanel.getLayoutParams();//去掉margin bottom，因为panel在外面
                params.setMargins(0, 0, 0, 0);
                mContentPanel.setLayoutParams(params);
            }
        } else {
            mbExposeTimeStamp = false;
            if (mInnerBottomPanel != null) {//如果Inner Bottom Panel Stub对应的View已经被inflate，则只用将其设为可见
                mInnerBottomPanel.setVisibility(View.VISIBLE);
            } else {
                panelStub = findView(R.id.inner_bottom_panel_stub);
                if (panelStub != null) {
                    panelStub.inflate();
                }
                mInnerBottomPanel = findView(R.id.inner_bottom_panel);
            }
        }

        mbDelayLoad = false;//已经加载过View，将是否需要延迟加载的Flag设为False
        initBottomUI((needInflateOuterBottomPanel ? mOuterBottomPanel : mInnerBottomPanel)); //初始化Bottom Panel中的View
        afterInitBottomPanelWidget();
        bindBottomUI();//设置Bottom Panel中的数据
        afterSetBottomPanelData();
    }

    protected void afterInitBottomPanelWidget() {

    }

    protected void afterSetBottomPanelData() {

    }

    public abstract void _onBind();

    /**
     * 设置bottom panel中的数据
     */
    private void bindBottomUI() {
        if (mbDelayLoad) {
            return;
        }
        //  charCount + contentLength 是为了解决 Bug 5505 - 【时间戳】当评论数为4位时，且时间戳为月日+时间的格式，android最小屏手机（红米1）标签+来源大于5个字，时间会重叠显示
        if (mBottomPanelWrapper != null) {
            // 这个listener 不要设置为 null,因为如果是null 的话，在NormalBottomPanel 中会执行其他的逻辑。
            mBottomPanelWrapper.showPanelWithfbButtonShowInfo(new CardBottomPanelWrapper.IPanelAction() {
                @Override
                public void onClickBadFeedback(boolean reasonGiven) {
                    deleteDoc(!reasonGiven);
                }

                @Override
                public void onClickCard() {

                }
            }, true);
        }
    }


    protected void deleteDoc(boolean needSendDislike) {
        // call delte the news api, this api have retry capability
        if (multipleItemQuickAdapter != null) {
            int position = multipleItemQuickAdapter.getRightCardPosition(mCard.id);
            multipleItemQuickAdapter.removeRow(position);
        }
    }



    /**
     * 计算txt需要的显示宽度
     *
     * @param txt
     * @return
     */
    private float getContentLenght(TextView view, String txt) {
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

    protected boolean getNightMode() {
        return false;
    }


    protected void initImage(ImageView img, String imgUrl, int imageSize) {
        if (!TextUtils.isEmpty(imgUrl)) {
            img.setVisibility(View.VISIBLE);
            if (mCard.displayType == News.DISPLAY_TYPE_VIDEO) {
//                img.setDefaultImageResId(R.drawable.list_video_empty);
            } else {
                //Restore place holder
                if (getNightMode()) {
//                    img.setDefaultImageResId(R.drawable.article_placeholder_nt);
                } else {
//                    img.setDefaultImageResId(R.drawable.article_placeholder);
                }
            }
            ImageLoaderHelper.displayImage(img, imgUrl);
//            img.setImageUrl(imgUrl, imageSize, false);
        } else {
            if (mCard.displayType == News.DISPLAY_TYPE_VIDEO) {
//                img.setImageResource(R.drawable.list_video_empty);
            } else {
                img.setVisibility(View.GONE);
            }
        }
    }

//    protected void adjustSingleRightPictureSize(YdNetworkImageView img) {
//        if (!FeedUiController.getInstance().isCurApplyNewThemeForCard()) {
//            if (img != null) {
//                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) img.getLayoutParams();
//                layoutParams.width = mSingleRightPicWidth;
//                layoutParams.height = mSingleRightPicHeight;
//                img.setLayoutParams(layoutParams);
//
//                View imgFrame = (View) img.getParent();
//                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) imgFrame.getLayoutParams();
//                layoutParams2.width = mSingleRightPicWidth;
//                layoutParams2.height = mSingleRightPicHeight;
//                imgFrame.setLayoutParams(layoutParams2);
//            }
//        }
//    }

    protected void setTitleColor(TextView textView, boolean hasRead) {
        if (textView == null) {
            return;
        }
        if (hasRead) {
            if (getNightMode()) {
                textView.setTextColor(mContext.getResources().getColor(R.color.ydsdk_content_text_readed_nt));
            } else {
                textView.setTextColor(mContext.getResources().getColor(R.color.ydsdk_content_text_readed));
            }
        } else {
            if (getNightMode()) {
                textView.setTextColor(mContext.getResources().getColor(R.color.ydsdk_title_text_nt));
            } else {
                textView.setTextColor(mContext.getResources().getColor(R.color.ydsdk_title_text));
            }
        }
    }


    /**
     * 设置需要延迟加载Bottom Panel，需要做初始化
     * 右单图需要根据title所占的高度来计算bottom panel是加载在图的左边还是card最下面
     */
    public void setNeedDerralLoad() {
        //如果应用新的UI Theme，则不能延时加载
        //因为新UI Theme可以在图片左侧支持3行标题，所以没有必要计算标题行数，再判断要不要坠下去
        mbDelayLoad = false;
        if (!mbDelayLoad) {
            return;
        }

        mbBottomPanelShown = false;

        //初始将OuterBottomPanel和InnerBottomPanel都设为Gone
        if (mOuterBottomPanel != null) {
            mOuterBottomPanel.setVisibility(View.GONE);
        }

        if (mInnerBottomPanel != null) {
            mInnerBottomPanel.setVisibility(View.GONE);
        }

        //将Content Panel margin bottom恢复
        if (mContentPanel != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentPanel.getLayoutParams();//加上margin bottom，因为Panel在里面
            params.setMargins(0, 0, 0, (int) (13 * mDisplayDensity));
            mContentPanel.setLayoutParams(params);
        }
    }

    @Override
    public void onClick(View v) {
        if(XClickUtil.isFastDoubleClick(v,500)) {
            return;
        }
        if (TextUtils.equals(Card.CTYPE_NORMAL_NEWS, mCard.cType)) {
            YdNewsActivity.startNewsActivity(mContext, mCard);
        } else if (TextUtils.equals(Card.CTYPE_VIDEO_CARD, mCard.cType)) {
            YdVideoActivity.startVideoActivity(mContext, mCard);
        } else if (TextUtils.equals(Card.CTYPE_PICTURE_GALLERY, mCard.cType)) {
            YdGalleryActivity.startGalleryActivity(mContext, mCard);
        }
    }

    private void adjustTitlePadding(TextView textView, String title) {
//        if (!FeedUiController.getInstance().isCurApplyNewThemeForCard()) {
//            return;
//        }

        int count = getLineCount(title);
        if (this instanceof SmallImageCardViewHolder) {
            if (count < 3) {
                textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(),
                        ContextUtils.getApplicationContext().getResources().
                                getDimensionPixelOffset(R.dimen.ydsdk_news_list_title_2_line_padding_bottom));
            } else if (count == 3) {
                textView.setPadding(textView.getPaddingLeft(), DensityUtil.dp2px(8), textView.getPaddingRight(),
                        ContextUtils.getApplicationContext().getResources().
                                getDimensionPixelOffset(R.dimen.ydsdk_news_list_title_3_line_padding_bottom));
            }
        }
    }

    /**
     * 计算txt需要的显示行数，最多RIGHT_PIC_MAX_TITLE_LINE行
     *
     * @param txt
     * @return
     */
    private int getLineCount(String txt) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                StaticLayout layout = null;
                layout = new StaticLayout(tvTitle.getText(), 0, txt.length(), tvTitle.getPaint(), mTitleLineWidth,
                        Layout.Alignment.ALIGN_NORMAL, tvTitle.getLineSpacingMultiplier(),
                        tvTitle.getLineSpacingExtra(), tvTitle.getIncludeFontPadding(),
                        tvTitle.getEllipsize(), tvTitle.getMaxLines());

                int rawCount = layout.getLineCount();
                rawCount = rawCount > RIGHT_PIC_MAX_TITLE_LINE ? RIGHT_PIC_MAX_TITLE_LINE : rawCount;
                return rawCount;
            }
        } catch (Exception e) {
        } catch (Error e) {
        }

        int count = 1;
        Paint p = tvTitle.getPaint();
        float[] txtWidths = new float[txt.length()];
        p.getTextWidths(txt, txtWidths);//得到每个字符所占的宽度
        if (pictureIsGone()) {
            mTitleLineWidth = (int) (mScreenWidth - mLeftPadding * 2 - mTxtAndPicMarginInDp * mDisplayDensity);
        } else {
            mTitleLineWidth = (int) (mScreenWidth - mLeftPadding * 2 - mSingleRightPicWidth - mTxtAndPicMarginInDp * mDisplayDensity);
        }
        float tempWidth = 0; // the current line length
        for (int i = 0; i < txt.length(); ) {
            int start = i; // start pos of word
            int end = start + 1;//end pos of word(start pos of next word)
            float wordWidth = txtWidths[start];
            if (String.valueOf(txt.charAt(i)).matches("[0-9]")) {
                //一串数字作为一个整体计算
                for (; end < txt.length() && String.valueOf(txt.charAt(end)).matches("[0-9]"); end++) {
                    wordWidth += txtWidths[end];
                }
            } else if (String.valueOf(txt.charAt(i)).matches("[A-Z,a-z]")) {
                //一串字母作为一个整体计算
                for (; end < txt.length() && String.valueOf(txt.charAt(end)).matches("[A-Z,a-z]"); end++) {
                    wordWidth += txtWidths[end];
                }
            }
            tempWidth += wordWidth;
            if (tempWidth > mTitleLineWidth) {//下一行的开始，重新算
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {//版本大于4.4
                    while (start > 0 && String.valueOf(txt.charAt(start)).matches("\\p{P}")) {//每行第一个字不能是符号
                        start--;
                    }
                } else {
                    while (start > 0 && (!String.valueOf(txt.charAt(start)).matches("^[\\u4E00-\\u9FA5]") || !String.valueOf(txt.charAt(start - 1)).matches("^[\\u4E00-\\u9FA5]"))) {//每行开头和结尾只能是汉字
                        start--;
                    }
                }
                tempWidth = 0;
                count++;
                i = start;
                if (count == RIGHT_PIC_MAX_TITLE_LINE) { //最多就显示3行，所以没必要再向后计算
                    break;
                }
            } else {//不足一行，接着算
                i = end;
            }
        }
        return count;
    }

    // 延迟加载的对于  imgFrame = findViewById(R.id.news_image_frame); 的显示条件最好可以通过这个来控制
    protected boolean pictureIsGone() { // 如果图片不可见的条件,子类可以重写这个方法
        return TextUtils.isEmpty(mCard.image);
    }


    // for search result hightlight
    private CharSequence getHighlightString(String string) {
        if (TextUtils.isEmpty(string)) return string;
        char target = '\u200b';
        if (string.indexOf(target) < 0) return string;

        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        int start = 0;
        int length = string.length();

        int colorResId = R.color.ydsdk_skin_primary_red;

        while (start < length) {
            int begin = string.indexOf(target, start);
            if (begin == -1) break;
            start = begin + 1;
            int end = string.indexOf(target, start);
            if (end == -1) break;
            start = end + 1;

//            StyleSpan span = new StyleSpan(android.graphics.Typeface.BOLD);

            ForegroundColorSpan span = new ForegroundColorSpan(ContextUtils.getApplicationContext().getResources().getColor(colorResId));
            builder.setSpan(span, begin, end + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }


    /**
     *  返回实际展示的展示封面图的url
     * @return
     */
    public List<String> getDisplayedImageUrls(){
        return  null;
    }
    /**
     * 将封面图转化为一个json上报日志
     *
     * @return
     */
    protected String convertImageUrlsToString() {
        List<String> displayedImageUrls = getDisplayedImageUrls();
        if(displayedImageUrls == null || displayedImageUrls.size()==0){
            return "";
        }else{
            JSONArray reportJson = new JSONArray();
            for (String imageUrl : displayedImageUrls) {
                reportJson.put(imageUrl);
            }
            return reportJson.toString();
        }
    }

    @Override
    public void onThemeChanged(int theme) {

        if (tvTitle != null){
            TypedArray ta = null;
            try {
                ta = mContext.getResources().obtainTypedArray(theme);
            } catch (Exception e) {
            }

            if (ta == null) {
                return;
            }

            int color = ta.getColor(R.styleable.NewsSDKTheme_newssdk_common_font_color, 0xffffff);
            float fontSize = ta.getDimension(R.styleable.NewsSDKTheme_newssdk_common_font_size, 12);


            int dividerColor = ta.getColor(R.styleable.NewsSDKTheme_newssdk_card_divider_color, 0xb8b8b8);
            int dividerHeight = (int) ta.getDimension(R.styleable.NewsSDKTheme_newssdk_card_divider_height, 1);

            ViewGroup.LayoutParams divierLayoutParams = middleDivider.getLayoutParams();
            divierLayoutParams.height = dividerHeight;
            middleDivider.setLayoutParams(divierLayoutParams);
            middleDivider.setBackgroundColor(dividerColor);
            ta.recycle();
            tvTitle.setTextColor(color);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);

            color = ThemeManager.getColor(mContext, theme, R.styleable.NewsSDKTheme_newssdk_card_img_bg_color, 0xffffff);
            if (color != 0xffffff) {
                ImageDownloaderConfig.changeThemeForImage(color);
            }
        }
    }
}
