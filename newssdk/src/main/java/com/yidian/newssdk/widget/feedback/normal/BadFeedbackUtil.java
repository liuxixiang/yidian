package com.yidian.newssdk.widget.feedback.normal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.yidian.newssdk.R;
import com.yidian.newssdk.protocol.newNetwork.RequestManager;
import com.yidian.newssdk.theme.ThemeManager;
import com.yidian.newssdk.utils.ContextUtils;
import com.yidian.newssdk.utils.DensityUtil;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BadFeedbackUtil {
    public interface Callback {
        void afterTellReason(boolean reasonsGiven);

        void dismiss();

        void getDislikeReasonForAd(String reason);
    }

    private BadFeedbackUtil() {
    }

    static int testCase = 0;

    static String lastDocId;
    static boolean[] marked = new boolean[6];
    static List<String> reasonsList = new ArrayList<>();
    static TextView tvOK;

    public static PopupWindow generateBadFeedbackWindow(final Context context, final View rootView, View clickedView,
                                                        final BadFeedBackWindow.FeedbackData feedBackData,
                                                        final Callback callback) {
        final String docId = feedBackData.id;
        final String channelId = feedBackData.channelId;
        final String logMeta = feedBackData.log_meta;
        final String impid = feedBackData.impId;
        String src = feedBackData.source;
        List<String> dislikeReasons = feedBackData.reasons;
        final HashMap<String, String> dislikeMap = feedBackData.reasonToFromIdMap;
        if (!TextUtils.isEmpty(lastDocId) && lastDocId.equalsIgnoreCase(docId)) {
            // do nothing.
        } else {
            for (int i = 0; i < 6; i++) {
                marked[i] = false;
            }
            lastDocId = docId;
        }
        reasonsList.clear();

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
        final CheckedTextView[] textViews = new CheckedTextView[6];

        LinearLayout lastLine;
        LinearLayout secondLine;
        LinearLayout firstLine;

        final LinearLayout root = (LinearLayout) inflater.inflate(R.layout.ydsdk_popupwindow_bad_feedback_common, null, false);

        hintTop = (ImageView) root.findViewById(R.id.hint_image_top);
        hintBottom = (ImageView) root.findViewById(R.id.hint_image_bottom);
        tvOK = (TextView) root.findViewById(R.id.btnOK);

        final BroadcastReceiver mNightModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                NightModeUtil.applyThemeAnim(root, context.getTheme(), false);
            }
        };
        final PopupWindow popupWindow = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (callback != null) {
                    callback.dismiss();
                }
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


        if ((dislikeReasons != null) && (dislikeReasons.size() > 0)) {
            for (int i = 0; i < dislikeReasons.size() && i <= 4; i++) {
                reasonsList.add(dislikeReasons.get(i));
            }
        }

        // 标题党
//            reasonsList.add(context.getString(R.string.exaggerated_title));

        // 内容差
//            reasonsList.add(context.getString(R.string.bad_content));

        // 来源屏蔽
        if (!TextUtils.isEmpty(src)) {
            reasonsList.add(context.getString(R.string.ydsdk_forbid_src) + src);
        }


        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                int reasonCount = 0;
                List<String> finalReasons = new ArrayList<String>();
                StringBuilder dislikeFromIdsBuilder = new StringBuilder();
                int length = Math.min(6, reasonsList.size());
                for (int i = 0; i < length; i++) {
                    if (marked[i]) {
                        finalReasons.add(reasonsList.get(i));
                        reasonCount++;
                    }
                }
                String finalString = null;
                String dislikeFromIds = null;
                if (reasonCount != 0) {
                    JSONArray temp = new JSONArray();
                    for (int i = 0; i < finalReasons.size(); i++) {
                        String item = finalReasons.get(i);
                        temp.put(item);
                        if (dislikeMap != null && dislikeMap.containsKey(item)) {
                            dislikeFromIdsBuilder.append(dislikeMap.get(item));
                            if (i != finalReasons.size() - 1) {
                                dislikeFromIdsBuilder.append(",");
                            }
                        }
                    }
                    dislikeFromIds = dislikeFromIdsBuilder.toString();
                    finalString = temp.toString();
                    /*
                    boolean first = true;
                    StringBuilder sb = new StringBuilder();
                    for (String item : finalReasons) {
                        if (first) {
                            first = false;
                        } else {
                            sb.append(",");
                        }
                        sb.append("\"" + URLEncoder.encode(item) + "\"");
                    }
                    finalString = "[ " + sb.toString() + " ]";
                    */
                } else {
                }

                // 可以先调，以便更新UI.
                if (reasonCount == 0) {
                    callback.afterTellReason(false);
                } else {
                    callback.afterTellReason(true);
                }

                RequestManager.requestFeedBack(docId, finalString);
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
        if (reasonsList.size() > 6) {
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

        /*

        textViews[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popupWindow.dismiss();
                boolean selected = textViews[0].isChecked();
                textViews[0].setChecked(!selected);
                marked[0] = !selected;
                updateOKBtn(context);
            }
        });


        textViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = textViews[1].isChecked();
                textViews[1].setChecked(!selected);
                marked[1] = !selected;
                updateOKBtn(context);
            }
        });

        if (!TextUtils.isEmpty(src)) {
            textViews[2].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean selected = textViews[2].isChecked();
                    textViews[2].setChecked(!selected);
                    marked[2] = !selected;
                    updateOKBtn(context);
                }
            });
        }



        int reasonIndex = 3;
        if ( ! TextUtils.isEmpty(src)) {
            String tempReason = context.getString(R.string.forbid_src) + src;
            // 给用户看到的是屏蔽源，但发送的是来源。
            //reasonsList.add("来源：" + src);
            textViews[2].setText(tempReason);
        } else {
            reasonIndex--;
        }
        */

        /*
        if (dislikeReasons == null || dislikeReasons.size() == 0) {
            //未能获得原因：
            textViews[reasonIndex].setVisibility(View.GONE);
            lastLine.setVisibility(View.GONE);
            if (reasonIndex == 2) {
                // 此时，只有两个原因，没有源。
                secondLine.setVisibility(View.GONE);
            } else if (reasonIndex == 3) {
                // 有两个个原因 + 源，
                textViews[3].setVisibility(View.INVISIBLE);
            }
        } else {
            int i = reasonIndex;
            for ( ; i < 6 && (i-reasonIndex) < dislikeReasons.size(); i++) {
                final String reason = dislikeReasons.get(i-reasonIndex);
//                String text = "<font color=#999999>不喜欢：</font> <font color=#222222>" + reason +"</font>";
//                textViews[i].setText(Html.fromHtml(text));
                //String text = "不喜欢：" + reason;
                reasonsList.add(reason);
                textViews[i].setText(reason);

                final int tempI = i;
                textViews[tempI].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean selected = textViews[tempI].isChecked();
                        textViews[tempI].setChecked(!selected);
                        marked[tempI] = !selected;
                        updateOKBtn(context);
                    }
                });
            }
            if (i <= 4) {
                lastLine.setVisibility(View.GONE);
            }
            for(;i<6;i++) {
                textViews[i].setVisibility(View.INVISIBLE);
            }
        }
        */

        for (i = 0; i < 6; i++) {
            textViews[i].setChecked(marked[i]);
        }

        updateOKBtn(context);
        ////////////////////////////////////
        float paddingRight = (DensityUtil.getScreenWidth(ContextUtils.getApplicationContext()) - locs[0]);
        //paddingRight /= density;
        if (paddingRight > clickedView.getWidth()) {
            int defaultEdgePadding = 14; //单位dp
            paddingRight -= defaultEdgePadding * density; // 本来的right padding
        }
        paddingRight -= clickedView.getWidth(); // 补偿
        paddingRight += 5 * density;//负反馈背景有个5dp的弧度
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
                animationSelector = R.style.ydsdk_popup_zoom_in_out_up_left;
            } else {
                // 上偏右
                animationSelector = R.style.ydsdk_popup_zoom_in_out_up_right;
            }
        } else {
            if (paddingRight > 100) {
                // 下偏左
                animationSelector = R.style.ydsdk_popup_zoom_in_out_down_left;
            } else {
                // 下偏右
                animationSelector = R.style.ydsdk_popup_zoom_in_out_down_right;
            }
        }
        popupWindow.setAnimationStyle(animationSelector);

        popupWindow.showAtLocation(rootView, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, locs[1] + (int) yOffset);
//        int pageId = Page.pageUnknown;
//        if (context instanceof ReportPage) {
//            pageId = ((ReportPage) context).getPageEnumId();
//        }
//        new Report.Builder(ActionMethod.A_ClickDislikeInDoc)
//                .page(pageId)
//                .docId(docId)
//                .card(Card.newslist)
//                .impressionId(impid)
//                .submit();

        // 只要显示过了，就没有必要再告诉用户怎么用了。
//        PopupTipsManager.getInstance().setBadFeedbackHintShown(false);

        return null;
    }

    public static PopupWindow generateNoReasonBadFeedbackWindow(final Context context, View clickedView, final Callback callback) {
        float density = DensityUtil.getDensity();
        int[] locs = new int[2];
        clickedView.getLocationOnScreen(locs);

        LayoutInflater inflater = LayoutInflater.from(context);
        final LinearLayout root = (LinearLayout) inflater.inflate(R.layout.ydsdk_list_feedback_noreason, null, false);
        tvOK = (TextView) root.findViewById(R.id.btnOK);

        final BroadcastReceiver mNightModeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                NightModeUtil.applyThemeAnim(root, context.getTheme(), false);
            }
        };
        final PopupWindow popupWindow = new PopupWindow(root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
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
        //负反馈总宽度75px，'x'占21px，左边有54px，要求距'x'30px
        popupWindow.showAtLocation(clickedView, Gravity.NO_GRAVITY, (int) (locs[0] + 24 + clickedView.getPaddingLeft() - 100 * density), locs[1]);
        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                callback.afterTellReason(false);
            }
        });

        return null;
    }

    private static void handleFeedback(String docId, String channelId, String logMeta, String reasons, String impid) {
//        APIExecutor.Send_Dislike_Reasons(docId, channelId, logMeta, impid, reasons);
    }

    private static void updateOKBtn(Context context) {
        boolean reasonSelected = false;
        for (int i = 0; i < 6; i++) {
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
        int feedtxtColor  = ta.getColor(R.styleable.NewsSDKTheme_newssdk_feedback_commontxt_color, Color.BLACK);

        ta.recycle();


        if (reasonSelected) {
            tvOK.setTextColor(feedtxtColor);
        } else {
            tvOK.setTextColor(context.getResources().getColor(R.color.ydsdk_text_grey));
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

    public static void setFeedbackHint(View view, View view2, int x, int y, String docId) {
//        if (PopupTipsManager.getInstance().getBadFeedbackHintShown()) {
//            // 若已经做过，就不要做了。
//            return;
//        }
        if (TextUtils.isEmpty(docId)) {
            return;
        }
        if (view == null || view2 == null) {
            return;
        }

        sRootView = view;
        sFbView = view2;
        hintBasedOnDocId = docId;
        xPos = x;
        yPos = y;
        hintTimeStart = System.currentTimeMillis();
    }

    private static PopupWindow mBadFeedbackHintPopupWindow;

    public static void showFeedbackHint(String lastDocId) {
//        if (PopupTipsManager.getInstance().getBadFeedbackHintShown()) {
//            // 若已经做过，就不要做了。
//            sRootView = null;
//            sFbView = null;
//            mBadFeedbackHintPopupWindow = null;
//            return;
//        }

        if (TextUtils.isEmpty(lastDocId) || !lastDocId.equalsIgnoreCase(hintBasedOnDocId)) {
            sRootView = null;
            sFbView = null;
            mBadFeedbackHintPopupWindow = null;
            return;
        }

        if (sRootView == null || sFbView == null) {
            return;
        }

        if ((hintTimeStart == 0) || (System.currentTimeMillis() - hintTimeStart > THRESHOLD)) {
            sRootView = null;
            sFbView = null;
            return;
        }

        dismissBadFeedbackHintPopupWindow();
        boolean isTopHalf;
        if (yPos * 2 > DensityUtil.getScreenHeight(ContextUtils.getApplicationContext())) {
            isTopHalf = false;
        } else {
            isTopHalf = true;
        }

        ImageView imageView = new ImageView(sRootView.getContext());
//        if (isTopHalf) {
//            imageView.setImageResource(R.drawable.toppage_feedback_guide_up);
//        } else {
//            imageView.setImageResource(R.drawable.toppage_feedback_guide_down);
//        }
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundColor(0);
        imageView.setLayoutParams(params);

        mBadFeedbackHintPopupWindow = new PopupWindow(imageView, imageView.getDrawable().getIntrinsicWidth(),
                imageView.getDrawable().getIntrinsicHeight());
        mBadFeedbackHintPopupWindow.setOutsideTouchable(true);
        mBadFeedbackHintPopupWindow.setFocusable(true);
        mBadFeedbackHintPopupWindow.setBackgroundDrawable(new ColorDrawable(0));

        if (isTopHalf) {
            yPos += sFbView.getHeight() * 2 / 3;
        } else {
            yPos -= mBadFeedbackHintPopupWindow.getHeight();
            yPos += sFbView.getHeight() / 3;
            yPos -= 3;  //补偿，以免相交

            if (yPos > 1450) {
                // 怕万一太靠底部，显示时不全，所以宁可不显示。
                sRootView = null;
                sFbView = null;
                mBadFeedbackHintPopupWindow = null;
                return;
            }
        }

        xPos -= mBadFeedbackHintPopupWindow.getWidth();
        xPos += sFbView.getWidth() * 5 / 6;

        try {
            mBadFeedbackHintPopupWindow.showAtLocation(sRootView, Gravity.TOP | Gravity.LEFT, xPos, yPos);
        } catch (WindowManager.BadTokenException e) {
            mBadFeedbackHintPopupWindow = null;
            sRootView = null;
            sFbView = null;
            return;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissBadFeedbackHintPopupWindow();
            }
        }, 4000);

//        PopupTipsManager.getInstance().setBadFeedbackHintShown(true);

        sRootView = null;
        sFbView = null;
    }

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

