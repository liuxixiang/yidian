package com.linken.newssdk.widget.cardview.adcard;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.linken.ad.data.AdvertisementCard;
import com.linken.newssdk.R;
import com.linken.newssdk.adapter.MultipleItemQuickAdapter;
import com.linken.newssdk.core.ad.AdvertisementUtil;
import com.linken.newssdk.core.ad.ReserveDelegate;

import java.util.UUID;


/**
 * Created by liuyue on 16/6/12.
 */
public class AdCard15 extends AdCard11 {
    private EditText mNameInput;
    private EditText mPhoneInput;
    private Button mSignUp;

    private ReserveDelegate mDelegate;

    private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (mDelegate != null) {
                mDelegate.changeStatus(v, hasFocus);
            }
        }
    };
    public AdCard15(MultipleItemQuickAdapter adapter, final View itemView) {
        super(adapter, itemView);
        mKeepDefaultTag = true;

        mNameInput = (EditText) itemView.findViewById(R.id.inputName);
        mPhoneInput = (EditText) itemView.findViewById(R.id.inputPhone);

        mDelegate = new ReserveDelegate(mNameInput, mPhoneInput);

        mSignUp = (Button) itemView.findViewById(R.id.signUp);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doViewNSReport();
                if (itemView.getContext() != null && itemView.getContext() instanceof Activity) {
                    Activity a = (Activity) itemView.getContext();
                    InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(a.getWindow().getDecorView().getWindowToken(), 0);
                    }
                }

                String name = mNameInput.getText().toString();
                String phone = mPhoneInput.getText().toString();
                mNameInput.setSelected(false);
                mPhoneInput.setSelected(false);
                boolean noErrorOccurred  = mDelegate.checkCanSignUp();
                if (noErrorOccurred) {
                    AdvertisementUtil.reportClickEvent(mAdCard, true, UUID.randomUUID().toString());
                    getAdHelper(mAdCard).eventReserve(name, phone, v.getContext());
                }  else {
                    mDelegate.errorTipWhenSignUp();
                }
            }
        });

        mNameInput.setOnFocusChangeListener(mFocusChangeListener);
        mPhoneInput.setOnFocusChangeListener(mFocusChangeListener);
    }

    @Override
    public void onBind(AdvertisementCard card, String docId) {
        super.onBind(card, docId);
        int tagPadding = (int) itemView.getResources().getDimension(R.dimen.ydsdk_ad_template_116_tag_padding);
        mTag.setPadding(tagPadding, mTag.getPaddingTop(), tagPadding, mTag.getPaddingBottom());
        if (!TextUtils.isEmpty(mAdCard.huodongButtonDesc)) {
            mSignUp.setText(mAdCard.huodongButtonDesc);
        }
    }


    @Override
    protected int getTagColor() {
        return itemView.getResources().getColor(R.color.ydsdk_ad_white);
    }
}
