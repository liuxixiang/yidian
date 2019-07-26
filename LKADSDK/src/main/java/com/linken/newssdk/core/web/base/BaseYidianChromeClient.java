package com.linken.newssdk.core.web.base;

import android.Manifest;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.CustomizedToastUtil;
import com.linken.newssdk.utils.PermissionUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Créé par liusiqian 2017/5/2.
 */

public class BaseYidianChromeClient extends WebChromeClient
{
    private final String TAG;
    private final Set<String> mNegativeSet;
    
    public BaseYidianChromeClient(final String TAG)
    {
        this.TAG = TAG;
        mNegativeSet = new HashSet<>();
        initNegativeWords();
    }
    
    private void initNegativeWords()
    {
        mNegativeSet.add("错误");
        mNegativeSet.add("未发现");
        mNegativeSet.add("未找到");
        mNegativeSet.add("丢失");
        mNegativeSet.add("无效");
        mNegativeSet.add("失败");
        mNegativeSet.add("缺少");
        mNegativeSet.add("未登录");
        mNegativeSet.add("不能");
        mNegativeSet.add("无权");
        mNegativeSet.add("超时");
        mNegativeSet.add("不合法");
    }
    
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result)
    {
        result.confirm();
        if(!TextUtils.isEmpty(message)){
            boolean positive = true;
            for(String s : mNegativeSet){
                if(message.contains(s)){
                    positive = false;
                    break;
                }
            }
            CustomizedToastUtil.showPrompt(message, positive);
        }
        return true;
    }
    
//    @Override
//    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result)
//    {
//        Context context = view.getContext();
//        SimpleDialog dialog = new SimpleDialog.SimpleDialogBuilder().setMaxLines(2).setMessage(message)
//                .setLeftBtnStr(context.getResources().getString(R.string.web_dialog_cancel))
//                .setRightBtnStr(context.getResources().getString(R.string.web_dialog_confirm))
//                .setSimpleListener(new SimpleDialog.SimpleListener() {
//                    @Override
//                    public void onBtnLeftClick(Dialog dialog)
//                    {
//                        result.cancel();
//                        dialog.dismiss();
//                    }
//
//                    @Override
//                    public void onBtnRightClick(Dialog dialog)
//                    {
//                        result.confirm();
//                        dialog.dismiss();
//                    }
//                }).createDialog(context);
//        dialog.show();
//        return true;
//    }
//
//    @Override
//    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result)
//    {
//        Context context = view.getContext();
//        SimplePromptDialog.SimplePromptDialogBuilder builder = (SimplePromptDialog.SimplePromptDialogBuilder)new SimplePromptDialog.SimplePromptDialogBuilder()
//                .setDefalutMessage(defaultValue)
//                .setSimplePromptListener(new SimplePromptDialog.SimplePromptListener() {
//                    @Override
//                    public void onBtnLeftClick(Dialog dialog, String strRes)
//                    {
//                        result.cancel();
//                        dialog.dismiss();
//                    }
//
//                    @Override
//                    public void onBtnRightClick(Dialog dialog, String strRes)
//                    {
//                        result.confirm(strRes);
//                        dialog.dismiss();
//                    }
//                })
//                .setMaxLines(2).setMessage(message)
//                .setLeftBtnStr(context.getResources().getString(R.string.web_dialog_cancel))
//                .setRightBtnStr(context.getResources().getString(R.string.web_dialog_confirm));
//        builder.createDialog(context).show();
//        return true;
//    }
    
    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result)
    {
        result.confirm();
        return true;
    }
    
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
    {
        boolean hasPermission = PermissionUtil.hasPermissionGroup(ContextUtils.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        callback.invoke(origin,hasPermission,false);
    }
    
    @Override
    public void onProgressChanged(WebView view, int newProgress)
    {
        super.onProgressChanged(view, newProgress);
    }
    
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage)
    {
        if(consoleMessage != null){
            switch (consoleMessage.messageLevel())
            {
                case ERROR:
                    break;
                case WARNING:
                    break;
            }
        }
        return super.onConsoleMessage(consoleMessage);
    }
}
