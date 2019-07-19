package com.yidian.newssdk.widget.feedback.ad;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yidian.newssdk.R;
import com.yidian.newssdk.core.ad.AdvertisementModule;
import com.yidian.newssdk.theme.ThemeManager;
import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.utils.DensityUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuyue on 2017/6/5.
 */

public class AdBadFeedbackUtil {
    public interface Callback {
        void afterTellReason(boolean reasonsGiven);

        void dismiss();

        void getDislikeReasonForAd(String reason, String reasonsCode);
    }

    private AdBadFeedbackUtil() {
    }

    static int testCase = 0;
    final static int REASON_MAX_SIZE = 6;
    static String lastDocId;
    static boolean[] marked = new boolean[REASON_MAX_SIZE];
    static List<String> reasonsList = new ArrayList<>();
    static HashMap<String, Integer> reasonsMap = new HashMap<>();
    static TextView tvOK;

    public static PopupWindow generateBadFeedbackWindow(final Context context, final View rootView, View clickedView,
                                                        final String docId, final String channelId, final String logMeta,
                                                        final String impid, String src, List<String> dislikeReasons,
                                                        final Callback callback) {
        if (!TextUtils.isEmpty(lastDocId) && lastDocId.equalsIgnoreCase(docId)) {
            // do nothing.
        } else {
            for (int i = 0; i < 6; i++) {
                marked[i] = false;
            }
            lastDocId = docId;
        }
        reasonsList.clear();
        reasonsMap.clear();

        boolean debug = false;
        if (debug) {
            if (testCase == 12) {
                testCase = 0;
            }
            dislikeReasons = new ArrayList<>();
            switch (testCase) {
                case 0:
                    src = "来源报纸";
                    dislikeReasons.clear();
                    break;
                case 1:
                    src = "来源报纸1";
                    dislikeReasons.clear();
                    dislikeReasons.add("原因1");
                    break;
                case 2:
                    src = "来源报纸2";
                    dislikeReasons.clear();
                    dislikeReasons.add("原因1");
                    dislikeReasons.add("原因2");
                    break;
                case 3:
                    src = "来源报纸3";
                    dislikeReasons.clear();
                    dislikeReasons.add("你问我原因有多长");
                    dislikeReasons.add("原因2");
                    dislikeReasons.add("原因3");
                    break;
                case 4:
                    src = "来源报纸4";
                    dislikeReasons.clear();
                    dislikeReasons.add("原因1");
                    dislikeReasons.add("原因2");
                    dislikeReasons.add("原因3");
                    dislikeReasons.add("原因4");
                    break;
                case 5:
                    src = "";
                    dislikeReasons.clear();
                    break;

                case 6:
                    src = null;
                    dislikeReasons.clear();
                    dislikeReasons.add("原因1");
                    break;
                case 7:
                    src = null;
                    dislikeReasons.clear();
                    dislikeReasons.add("原因1");
                    dislikeReasons.add("原因2");
                    break;
                case 8:
                    src = null;
                    dislikeReasons.clear();
                    dislikeReasons.add("原因1");
                    dislikeReasons.add("原因2");
                    dislikeReasons.add("原因3");
                    break;
                case 9:
                    src = null;
                    dislikeReasons.clear();
                    dislikeReasons.add("原因1");
                    dislikeReasons.add("原因2");
                    dislikeReasons.add("原因3");
                    dislikeReasons.add("原因4");
                    break;
                case 10:
                    src = null;
                    dislikeReasons.clear();
                    dislikeReasons.add("原因1");
                    dislikeReasons.add("原因2");
                    dislikeReasons.add("原因3");
                    dislikeReasons.add("原因4");
                    dislikeReasons.add("原因5");
                    break;
                case 11:
                    src = null;
                    dislikeReasons.clear();
                    dislikeReasons.add("原因1");
                    dislikeReasons.add("原因2");
                    dislikeReasons.add("原因3");
                    dislikeReasons.add("原因4");
                    dislikeReasons.add("原因5");
                    dislikeReasons.add("原因6");
                default:
                    break;

            }
            testCase++;
        }

        float density = DensityUtil.getDensity();

        int[] locs = new int[2];
        clickedView.getLocationOnScreen(locs);

        boolean isTopHalf;
        if (locs[1] * 2 > DensityUtil.getScreenHeight(ContextUtils.getApplicationContext())) {
            isTopHalf = false;
        } else {
            isTopHalf = true;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        //LinearLayout root;
        ImageView hintBottom;
        ImageView hintTop;
        final CheckedTextView[] textViews = new CheckedTextView[REASON_MAX_SIZE];

        LinearLayout lastLine;
        LinearLayout secondLine;
        LinearLayout firstLine;

        final LinearLayout root = (LinearLayout) inflater.inflate(R.layout.ydsdk_ad_popupwindow_bad_feedback_common, null, false);

//        if ( NightModeConfig.getInstance().getNightMode()) {
//            root = (LinearLayout) inflater.inflate(R.layout.popupwindow_bad_feedback_nt, null, false);
//        } else {
//            root = (LinearLayout) inflater.inflate(R.layout.popupwindow_bad_feedback, null, false);
//        }

        hintTop = (ImageView) root.findViewById(R.id.hint_image_top);
        hintBottom = (ImageView) root.findViewById(R.id.hint_image_bottom);
        tvOK = (TextView) root.findViewById(R.id.btnOK);

//        final BroadcastReceiver mNightModeReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                NightModeUtil.applyThemeAnim(root, context.getTheme(), false);
//            }
//        };
        final PopupWindow popupWindow = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (callback != null) callback.dismiss();
//                NMAssistBroadcastUtil.unregisterNMAssistBroadcast(context,mNightModeReceiver);
            }
        });

//        NMAssistBroadcastUtil.registerNMAssistBroadcast(context,mNightModeReceiver);

        textViews[0] = (CheckedTextView) root.findViewById(R.id.reason1);
        textViews[1] = (CheckedTextView) root.findViewById(R.id.reason2);
        textViews[2] = (CheckedTextView) root.findViewById(R.id.reason3);
        textViews[3] = (CheckedTextView) root.findViewById(R.id.reason4);
        textViews[4] = (CheckedTextView) root.findViewById(R.id.reason5);
        textViews[5] = (CheckedTextView) root.findViewById(R.id.reason6);

        lastLine = (LinearLayout) root.findViewById(R.id.last_line);
        secondLine = (LinearLayout) root.findViewById(R.id.second_line);
        firstLine = (LinearLayout)root.findViewById(R.id.first_line);


        // 广告
        // 来源屏蔽
        if (!TextUtils.isEmpty(src)) {
            String reasonSrc = context.getString(R.string.ydsdk_ad_forbid_src) + src;
            reasonsList.add(reasonSrc);
            reasonsMap.put(reasonSrc, 6);
        }
        reasonsList.add(context.getString(R.string.ydsdk_ad_dislike_frequency));
        reasonsList.add(context.getString(R.string.ydsdk_ad_dislike_poor_quality));
        reasonsList.add(context.getString(R.string.ydsdk_ad_dislike_seen));
        reasonsList.add(context.getString(R.string.ydsdk_ad_dislike_irrelevance));

        reasonsMap.put(context.getString(R.string.ydsdk_ad_dislike), 1);
        reasonsMap.put(context.getString(R.string.ydsdk_ad_dislike_frequency), 2);
        reasonsMap.put(context.getString(R.string.ydsdk_ad_dislike_poor_quality), 3);
        reasonsMap.put(context.getString(R.string.ydsdk_ad_dislike_seen), 4);
        reasonsMap.put(context.getString(R.string.ydsdk_ad_dislike_irrelevance), 5);

        if ((dislikeReasons != null) && (dislikeReasons.size() > 0)) {
            for (int i = 0; i < dislikeReasons.size() && reasonsList.size() <= REASON_MAX_SIZE; i++) {
                // 原因最多只有2个
                reasonsList.add(dislikeReasons.get(i));
            }
        }
        /*
        if ((dislikeReasons != null) && (dislikeReasons.size() > 0)) {
            for (int i = 0; i < dislikeReasons.size() && i <= 6; i++) {
                reasonsList.add(dislikeReasons.get(i));
            }
        }
        if (reasonsList.size() < 6) {
            // 和小米广告确定过：我们会主动添加这两个字段。
            reasonsList.add(context.getString(R.string.dislike_ad_default_1));
            reasonsList.add(context.getString(R.string.dislike_ad_default_2));
        }
        */



        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                List<String> finalReasons = new ArrayList<String>();
                List<Integer> finalReasonsCode = new ArrayList<>();
                finalReasonsCode.add(1);
                for (int i = 0; i < REASON_MAX_SIZE && i < reasonsList.size(); i++) {
                    if (marked[i]) {
                        finalReasons.add(reasonsList.get(i));
                        finalReasonsCode.add(reasonsMap.get(reasonsList.get(i)));
                    }
                }

                String finalString;
                if (finalReasons.size() > 0) {
                    JSONArray temp = new JSONArray();
                    for (String item : finalReasons) {
                        temp.put(item);
                    }
                    finalString = temp.toString();

                } else {
                    // 和小米广告确定过，如果用户什么都不选，就直接传一个“空”过去
                    //finalString = new JSONArray().put(context.getString(R.string.dislike_ad_no_reason)).toString();
                    // 16年6月22日，把"空"字符换成空数组格式。
                    finalString = "[]";
                    /*
                    finalString = "[ \"" + URLEncoder.encode(context.getString(R.string.dislike_ad_no_reason)) + "\" ]";
                    */
                }


                // 对广告的处理
                if (finalReasons.size() == 0) {
                    callback.afterTellReason(false);
                    callback.getDislikeReasonForAd(finalString, finalReasonsCode.toString());
                } else {
                    callback.afterTellReason(true);
                    callback.getDislikeReasonForAd(finalString, finalReasonsCode.toString());
                }


            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                int i;
                if (id == R.id.reason1) {
                    i = 0;
                } else if (id == R.id.reason2) {
                    i = 1;
                } else if (id == R.id.reason3) {
                    i = 2;
                } else if (id == R.id.reason4) {
                    i = 3;
                } else if (id == R.id.reason5) {
                    i = 4;
                } else if (id == R.id.reason6) {
                    i = 5;
                } else {
                    return;
                }

                boolean selected = textViews[i].isChecked();
                textViews[i].setChecked(!selected);
                marked[i] = !selected;
                updateOKBtn(context);

            }
        };

        int i = 0;
        if (reasonsList.size() > REASON_MAX_SIZE) {
            reasonsList = reasonsList.subList(0, 6);
        }
        for (String oneReason : reasonsList) {
            textViews[i].setText(oneReason);
            textViews[i].setOnClickListener(clickListener);
            i++;
        }

        switch (i) {
            case 0:
                firstLine.setVisibility(View.GONE);
                secondLine.setVisibility(View.GONE);
                lastLine.setVisibility(View.GONE);
                break;
            case 1: // fake 数据现在只有一个来源，所以会出现就一个dislike_reason 问题
                textViews[1].setVisibility(View.INVISIBLE);
                secondLine.setVisibility(View.GONE);
                lastLine.setVisibility(View.GONE);
                break;
            case 2:
                secondLine.setVisibility(View.GONE);
                lastLine.setVisibility(View.GONE);
                break;
            case 3:
                lastLine.setVisibility(View.GONE);
                textViews[3].setVisibility(View.INVISIBLE);
                break;
            case 4:
                lastLine.setVisibility(View.GONE);
                break;
            case 5:
                textViews[5].setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

        for (i = 0; i < REASON_MAX_SIZE; i++) {
            textViews[i].setChecked(marked[i]);
        }
        updateOKBtn(context);
        ////////////////////////////////////
        float paddingRight = (DensityUtil.getScreenWidth(ContextUtils.getApplicationContext()) - locs[0]);
        //paddingRight /= density;
        paddingRight -= 15 * density; // 本来的right padding
        paddingRight -= 20 * density; // 补偿
        paddingRight -= 11 * density; // 补偿    //由于设置了padding = 10dp， 所以再补偿
        if(firstLine.getVisibility() == View.GONE) {
            paddingRight += 11 * density;
        }
        if (isTopHalf) {
            hintBottom.setVisibility(View.GONE);
            hintTop.setPadding(0, 0, (int) paddingRight, 0);
        } else {
            hintTop.setVisibility(View.GONE);
            hintBottom.setPadding(0, 0, (int) paddingRight, 0);
        }

        float yOffset;
        if (isTopHalf) {
            yOffset = 22 * density;
        } else {
            // 多一层，则多 2 + 33 + 2 = 39个dp
            yOffset = -118 * density;   //两层的用109

            yOffset -= 39 * density;      //三层的再多一个 39dp

            if (lastLine.getVisibility() == View.GONE) {
                yOffset += 40 * density;
            }
            if (secondLine.getVisibility() == View.GONE) {
                yOffset += 40 * density;
            }
            if (firstLine.getVisibility() == View.GONE) {
                yOffset += 44 * density;
            }
            // 后来改成多选，多一个“确定”框，
            yOffset -= 40 * density;
        }

        //popupWindow.setAnimationStyle(R.style.fb_popup_from_right);
        int animationSelector = 0;
        if (isTopHalf) {
            if (paddingRight > 100) {
                // 上偏左
                animationSelector = R.style.ydsdk_ad_fb_popup_zoom_in_out_up_left;
            } else {
                // 上偏右
                animationSelector = R.style.ydsdk_ad_fb_popup_zoom_in_out_up_right;
            }
        } else {
            if (paddingRight > 100) {
                // 下偏左
                animationSelector = R.style.ydsdk_ad_fb_popup_zoom_in_out_down_left;
            } else {
                // 下偏右
                animationSelector = R.style.ydsdk_ad_fb_popup_zoom_in_out_down_right;
            }
        }
        popupWindow.setAnimationStyle(animationSelector);

        popupWindow.showAtLocation(rootView, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, locs[1] + (int) yOffset);
        IAdRelatedStatusListener listener = AdvertisementModule.getInstance().getAdRelatedStateListener();
        if(listener != null) {
            listener.badFeedbackOnAd(context);
        }

        // 只要显示过了，就没有必要再告诉用户怎么用了。
//        PopupTipsManager.getInstance().setBadFeedbackHintShown(false);

        return null;
    }

    private static void updateOKBtn(Context context) {
        boolean reasonSelected = false;
        for (int i = 0; i < REASON_MAX_SIZE; i++) {
            if (marked[i]) {
                reasonSelected = true;
                break;
            }
        }
        TypedArray ta = null;
        try {
            ta = ContextUtils.getApplicationContext().getResources().obtainTypedArray(ThemeManager.getTheme());
        } catch (Exception e) {
        }

        if (ta == null) {
            return;
        }
        int feedtxtColor  = ta.getColor(R.styleable.NewsSDKTheme_newssdk_feedback_commontxt_color, context.getResources().getColor(R.color.ydsdk_skin_primary_red));

        ta.recycle();



        if (reasonSelected) {
            tvOK.setTextColor(feedtxtColor);
        } else {
            tvOK.setTextColor(context.getResources().getColor(R.color.ydsdk_text_grey));

//            if (NightModeConfig.getInstance().getNightMode())
//                tvOK.setTextColor(context.getResources().getColor(R.color.text_gray));
//            else
//                tvOK.setTextColor(context.getResources().getColor(R.color.text_grey));
        }

    }

    /* Bad Feedback Hint*/
    static private int xPos;
    static private int yPos;
    static View sRootView;
    static View sFbView;
    static private String hintBasedOnDocId;
    static long hintTimeStart = 0;
    static final long THRESHOLD = 8 * 1000;

//    public static void setFeedbackHint(View view, View view2, int x, int y, String docId) {
//        if (PopupTipsManager.getInstance().getBadFeedbackHintShown()) {
//            // 若已经做过，就不要做了。
//            return;
//        }
//        if (TextUtils.isEmpty(docId)) {
//            return;
//        }
//        if (view == null || view2 == null) {
//            return;
//        }
//
//        sRootView = view;
//        sFbView = view2;
//        hintBasedOnDocId = docId;
//        xPos = x;
//        yPos = y;
//        hintTimeStart = System.currentTimeMillis();
//    }

    private static PopupWindow mBadFeedbackHintPopupWindow;

//    public static void showFeedbackHint(String lastDocId) {
////        if (PopupTipsManager.getInstance().getBadFeedbackHintShown()) {
////            // 若已经做过，就不要做了。
////            sRootView = null;
////            sFbView = null;
////            mBadFeedbackHintPopupWindow = null;
////            return;
////        }
//
//        if (TextUtils.isEmpty(lastDocId) || !lastDocId.equalsIgnoreCase(hintBasedOnDocId)) {
//            sRootView = null;
//            sFbView = null;
//            mBadFeedbackHintPopupWindow = null;
//            return;
//        }
//
//        if (sRootView == null || sFbView == null) {
//            return;
//        }
//
//        if ((hintTimeStart == 0) || (System.currentTimeMillis() - hintTimeStart > THRESHOLD)) {
//            sRootView = null;
//            sFbView = null;
//            return;
//        }
//
//        dismissBadFeedbackHintPopupWindow();
//        boolean isTopHalf;
//        if (yPos * 2 > DensityUtil.getScreenHeight()) {
//            isTopHalf = false;
//        } else {
//            isTopHalf = true;
//        }
//
//        ImageView imageView = new ImageView(sRootView.getContext());
//        if (isTopHalf) {
//            imageView.setImageResource(R.drawable.toppage_feedback_guide_up);
//        } else {
//            imageView.setImageResource(R.drawable.toppage_feedback_guide_up);
//        }
//        ViewGroup.LayoutParams params =
//                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        imageView.setBackgroundColor(0);
//        imageView.setLayoutParams(params);
//
//        mBadFeedbackHintPopupWindow = new PopupWindow(imageView, imageView.getDrawable().getIntrinsicWidth(),
//                imageView.getDrawable().getIntrinsicHeight());
//        mBadFeedbackHintPopupWindow.setOutsideTouchable(true);
//        mBadFeedbackHintPopupWindow.setFocusable(true);
//        mBadFeedbackHintPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
//
//        if (isTopHalf) {
//            yPos += sFbView.getHeight() * 2 / 3;
//        } else {
//            yPos -= mBadFeedbackHintPopupWindow.getHeight();
//            yPos += sFbView.getHeight() / 3;
//            yPos -= 3;  //补偿，以免相交
//
//            if (yPos > 1450) {
//                // 怕万一太靠底部，显示时不全，所以宁可不显示。
//                sRootView = null;
//                sFbView = null;
//                mBadFeedbackHintPopupWindow = null;
//                return;
//            }
//        }
//
//        xPos -= mBadFeedbackHintPopupWindow.getWidth();
//        xPos += sFbView.getWidth() * 5 / 6;
//
//        try {
//            mBadFeedbackHintPopupWindow.showAtLocation(sRootView, Gravity.TOP | Gravity.LEFT, xPos, yPos);
//        } catch (WindowManager.BadTokenException e) {
//            mBadFeedbackHintPopupWindow = null;
//            sRootView = null;
//            sFbView = null;
//            return;
//        }
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dismissBadFeedbackHintPopupWindow();
//            }
//        }, 4000);
//
////        PopupTipsManager.getInstance().setBadFeedbackHintShown(true);
//
//        sRootView = null;
//        sFbView = null;
//    }

    public static void dismissBadFeedbackHintPopupWindow() {
        if (mBadFeedbackHintPopupWindow != null && mBadFeedbackHintPopupWindow.isShowing()) {
            try {
                mBadFeedbackHintPopupWindow.dismiss();
                mBadFeedbackHintPopupWindow = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mBadFeedbackHintPopupWindow = null;
    }
}
