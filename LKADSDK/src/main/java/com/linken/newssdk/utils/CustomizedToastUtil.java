package com.linken.newssdk.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.linken.newssdk.R;

public class CustomizedToastUtil {

    private static Context sAppContext;

    public static void init(){
        sAppContext = ContextUtils.getApplicationContext();
    }
    /**
     * this function show customized toast
     *
     * @param stringId the id of the string going to displayed
     * @param succ     with success icon
     */
    public static void showPrompt(int stringId, boolean succ) {
        try {
            CharSequence s = sAppContext.getText(stringId);
            showCustomizedToast(s.toString(), succ);
        } catch (Exception e) {
            //the ctx activity maybe closed.
        }
    }

    /**
     * this function show customized toast
     *
     * @param txt
     * @param succ
     */
    public static void showPrompt(String txt, boolean succ) {

        if (TextUtils.isEmpty(txt)) {
            return;
        }
        showCustomizedToast(txt, succ);
    }

//    public static void showNetworkPrompt() {
//        if (!NetworkUtil.isNetworkAvailable()) {
//            String s = sAppContext.getText(R.string.network_disconnected).toString();
//            showCustomizedToast(s, false);
//        }
//    }

//    public static void showNetworkPromptWithDefaultMessage(String defaultMessage) {
//        try {
//            String s = defaultMessage;
//            if (!NetworkUtil.isNetworkAvailable()) {
//                s = sAppContext.getText(R.string.network_disconnected).toString();
//            }
//            showCustomizedToast(s, false);
//        } catch (Exception e) {
//            showCustomizedToast(defaultMessage, false);
//        }
//    }

    private static void showCustomizedToast(String s, boolean succ) {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            try {
                View toastView = LayoutInflater.from(sAppContext).inflate(R.layout.ydsdk_customized_toast_layout, null);
                TextView tv = (TextView) toastView.findViewById(R.id.toastTxt);
                tv.setText(s);
                if (succ) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ydsdk_notice_succeed, 0, 0, 0);
                } else {
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ydsdk_notice_failure, 0, 0, 0);
                }

                Toast toast = new Toast(sAppContext);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastView);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toastView.setVisibility(View.VISIBLE);

                ToastUtils.showToast(toast);
               // toast.show();
            } catch (Exception e) {
                //the ctx activity maybe closed.
            }
        } else {
            ToastUtils.showShort(sAppContext, s);
        }
    }

    /**
     * 为了处理不同的网络错误及执行失败，需要显示不同的信息

     * @param err
     */
    public static void showApiFailed(int err) {
        int errId = R.string.ydsdk_server_error;
        switch (err) {
            case 700:
                errId = R.string.ydsdk_server_failed;
                break;
            case 701:
                errId = R.string.ydsdk_network_timeout;
                break;
            case 702:
                errId = R.string.ydsdk_webservice_issue;
                break;
            case 703:
                errId = R.string.ydsdk_empty_response;
                break;
            case 704:
                errId = R.string.ydsdk_network_error_retry;
                break;
            default:
                break;
        }

        showPrompt(errId, false);
    }

}
