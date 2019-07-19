package com.yidian.newssdk.protocol.newNetwork.core;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonObjectResponseHandler extends TextResponseHandler {

    @Override
    public void onSuccess(String response) {
        try {
            JSONObject json = new JSONObject(response);
            int code = json.optInt("code", -1);
            String reson = json.optString("reason");
            if (code == 0) {
                onSuccess(json);
            } else {
                Exception exception = new ApiException(code, reson);
                onFailure(exception);
            }
        } catch (JSONException e) {
            onFailure(e);
        }

    }

    public abstract void onSuccess(JSONObject response);

}
