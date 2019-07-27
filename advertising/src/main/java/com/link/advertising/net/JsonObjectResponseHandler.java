package com.link.advertising.net;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonObjectResponseHandler extends TextResponseHandler {

    @Override
    public void onSuccess(String response) {
        try {
            JSONObject json = new JSONObject(response);
            int code = json.optInt("errCode", -1);
            String errMsg = json.optString("errMsg");
            if (code == 0) {
                onSuccess(json);
            } else {
                Exception exception = new ApiException(code, errMsg);
                onFailure(exception);
            }
        } catch (JSONException e) {
            onFailure(e);
        }

    }

    public abstract void onSuccess(JSONObject response);

}
