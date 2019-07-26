package com.linken.newssdk.core.ad;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.linken.newssdk.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;

/**
 * Created by liuyue on 16/7/8.
 */
public class ReserveDelegate {
    public static final int NAME_INPUT = 0;
    public static final int PHONE_NUMBER_INPUT = 1;

    public static final int STATUS_ERROR = 11;
    public static final int STATUS_SUSPEND = 12;
    public static final int STATUS_NORMAL = 13;

    @IntDef({NAME_INPUT, PHONE_NUMBER_INPUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface INPUT_TYPE {
    }

    @IntDef({STATUS_ERROR, STATUS_SUSPEND, STATUS_NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface INPUT_BOX_STATUS {
    }

    private final Pattern nameRegx = Pattern.compile("[\u4e00-\u9fa5]{2,20}");
    private final Pattern phoneNumberRegx = Pattern.compile("[0-9]{11}");
    private final Pattern chineseCharacterRegx = Pattern.compile("[\u4e00-\u9fa5]*");
    private final Pattern numberRegx = Pattern.compile("[\0-9]*");
    private final int maxChineseCharacterLength = 20;
    private final int phoneNumberLength = 11;
    private final String emptyString = "";

    private EditText mNameInput;
    private EditText mPhoneInput;

    private @INPUT_TYPE int mType = NAME_INPUT;

    private @INPUT_BOX_STATUS int mNameInputStatus;
    private @INPUT_BOX_STATUS int mPhoneInputStatus;

    private TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑状态中只判断长度有没有超过限制和出现非法字符
            checkLegalStateDuringInput(ReserveDelegate.NAME_INPUT, s);
        }
    };

    private TextWatcher phoneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑状态中只判断长度有没有超过限制和出现非法字符
            checkLegalStateDuringInput(ReserveDelegate.PHONE_NUMBER_INPUT, s);
        }
    };

    public void changeStatus(View v, boolean hasFocus) {
        if (v.equals(mNameInput)) {
            focusChanged(ReserveDelegate.NAME_INPUT, hasFocus);
            mNameInputStatus = hasFocus ? STATUS_SUSPEND : STATUS_NORMAL;
        } else if (v.equals(mPhoneInput)) {
            focusChanged(ReserveDelegate.PHONE_NUMBER_INPUT, hasFocus);
            mPhoneInputStatus = hasFocus ? STATUS_SUSPEND : STATUS_NORMAL;
        }
    }

    public ReserveDelegate(EditText nameInput, EditText phoneInput) {
        mNameInput = nameInput;
        mPhoneInput = phoneInput;

        mNameInput.addTextChangedListener(nameWatcher);
        mPhoneInput.addTextChangedListener(phoneWatcher);
    }

    public boolean checkLegalStateDuringInput(@INPUT_TYPE int type, Editable s) {
        boolean status = true;
        mType = type;
        if (NAME_INPUT == type) {
            status = s.length() <= maxChineseCharacterLength && chineseCharacterRegx.matcher(s).matches();
            setLegalStateDuringInput(mNameInput, status);
            mNameInputStatus = status ? STATUS_NORMAL : STATUS_ERROR;
        } else {
            status = s.length() <= phoneNumberLength && numberRegx.matcher(s).matches();
            setLegalStateDuringInput(mPhoneInput, status);
            mPhoneInputStatus = status ? STATUS_NORMAL : STATUS_ERROR;
        }
        return status;
    }

    public boolean checkLegalStateAfterInput(@INPUT_TYPE int type) {
        boolean status = true;
        mType = type;
        if (NAME_INPUT == type) {
            status = nameRegx.matcher(mNameInput.getText()).matches();
            setLegalStateAfterInput(mNameInput, status);
            mNameInputStatus = status ? STATUS_NORMAL : STATUS_ERROR;
        } else {
            status = phoneNumberRegx.matcher(mPhoneInput.getText()).matches();
            setLegalStateAfterInput(mPhoneInput, status);
            mPhoneInputStatus = status ? STATUS_NORMAL : STATUS_ERROR;
        }
        return status;
    }

    public boolean checkCanSignUp() {
        return nameRegx.matcher(mNameInput.getText()).matches() && phoneNumberRegx.matcher(mPhoneInput.getText()).matches();
    }
    private void setLegalStateDuringInput(EditText text, boolean legal) {
        if (legal) {
            text.setBackgroundResource(getSuspendBg());
            text.setHint(getHintMsg());
        } else {
            text.setText(emptyString);
            text.setHint(getErrorMsg());
            text.setBackgroundResource(getErrorBg());
        }
    }

    private void setLegalStateAfterInput(EditText text, boolean legal) {
        if (legal) {
            text.setBackgroundResource(getNormalBg());
            text.setHint(getHintMsg());
        } else {
            text.setText(emptyString);
            text.setHint(getErrorMsg());
            text.setBackgroundResource(getErrorBg());
        }
    }

    public void focusChanged(@INPUT_TYPE int type, boolean hasFocus) {
        mType = type;
        EditText text = NAME_INPUT == type ? mNameInput : mPhoneInput;
        text.setHint(getHintMsg());
        if (hasFocus) {
            text.setBackgroundResource(getSuspendBg());
        } else {
            checkLegalStateAfterInput(type);
//            text.setBackgroundResource(getNormalBg());
        }
    }

    public void errorTipWhenSignUp() {
        checkLegalStateAfterInput(NAME_INPUT);
        checkLegalStateAfterInput(PHONE_NUMBER_INPUT);
    }

    private int getErrorBg() {
        return R.drawable.ydsdk_ad_template_116_input_error_bg;
    }

    private int getSuspendBg() {
        return R.drawable.ydsdk_ad_17_input_suspend_bg;
    }

    private int getNormalBg() {
        return R.drawable.ydsdk_ad_template_116_input_bg;
    }

    private int getErrorMsg() {
        return NAME_INPUT == mType ?
                R.string.ydsdk_ad_template_116_error_name :
                R.string.ydsdk_ad_template_116_error_phone;
    }

    private int getHintMsg() {
        return NAME_INPUT == mType ?
                R.string.ydsdk_ad_template_116_name_input :
                R.string.ydsdk_ad_template_116_phone_input;
    }

    public void receivedNightModeChange(){
        getBgForInputStatus(mNameInputStatus, mNameInput);
        getBgForInputStatus(mPhoneInputStatus, mPhoneInput);
    }

    private void getBgForInputStatus(@INPUT_BOX_STATUS int status, EditText input){
        switch (status){
            case STATUS_NORMAL:
                input.setBackgroundResource(getNormalBg());
                break;
            case STATUS_ERROR:
                input.setBackgroundResource(getErrorBg());
                break;
            case STATUS_SUSPEND:
                input.setBackgroundResource(getSuspendBg());
                break;
            
        }
    }

    public void resetStatus() {
        mNameInput.setText(emptyString);
        mNameInput.setBackgroundResource(getNormalBg());
        mNameInput.setHint(R.string.ydsdk_ad_template_116_name_input);
        mPhoneInput.setText(emptyString);
        mPhoneInput.setBackgroundResource(getNormalBg());
        mPhoneInput.setHint(R.string.ydsdk_ad_template_116_phone_input);
    }
}
