package com.linken.newssdk.protocol.newNetwork.core;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonObjectResponseHandler extends TextResponseHandler {

    @Override
    public void onSuccess(String response) {
        try {
            JSONObject json = new JSONObject(response);
            int code = json.optInt("code", -1);
            String reson = json.optString("reason");
            int errCode = json.optInt("errCode", -2);
            String errMsg = json.optString("errMsg");
            if (code == 0 || errCode == 0) {
                onSuccess(json);
            } else {
                if(errCode == -2) {
                    Exception exception = new ApiException(code, reson);
                    onFailure(exception);
                }else {
                    //linken
                    Exception exception = new ApiException(errCode, errMsg);
                    onFailure(exception);
                }

            }
        } catch (JSONException e) {
            onFailure(e);
        }

    }

    public abstract void onSuccess(JSONObject response);

}
