package com.yidian.newssdk.export;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by chenyichang on 2018/5/18.
 */

public interface IShareInterface {

    String KEY_SHARE_TITLE = "KEY_SHARE_TITLE";
    String KEY_SHARE_URL = "KEY_SHARE_URL";
    String KEY_SHARE_IMG = "KEY_SHARE_IMG";
    String KEY_SHARE_DESC = "KEY_SHARE_DESC";

    void doShare(Context context, Bundle params);
}
