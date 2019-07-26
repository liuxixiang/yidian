package com.linken.newssdk.data.pref;

import com.linken.newssdk.data.YdAccount;

/**
 * Created by chenyichang on 2018/5/19.
 */

public class GlobalDataCache {

    private static GlobalDataCache instance = new GlobalDataCache();

    private final YdAccount ydAccount;

    public static GlobalDataCache getInstance() {
        return instance;
    }

    private GlobalDataCache() {
        ydAccount = new YdAccount();
        ydAccount.setUserid(GlobalAccount.getYduserId());
        ydAccount.setCookie(GlobalAccount.getCookie());
    }


    public YdAccount getActiveAccount() {
        return ydAccount;
    }
}
