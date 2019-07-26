package com.linken.newssdk.core.newweb;

import android.content.res.AssetManager;

import com.linken.newssdk.utils.ContextUtils;
import com.linken.newssdk.utils.FilesUtility;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/7/24
 */

public class WebAppManager {

    private volatile static WebAppManager _instance;
    private ThirdPartyManager mThirdPartyManager;

    public static WebAppManager getInstance() {
        if (_instance == null) {
            synchronized (WebAppManager.class) {
                if (_instance == null) {
                    _instance = new WebAppManager();
                }
            }
        }
        return _instance;
    }

    private WebAppManager() {
        //by cyc opt
        mThirdPartyManager = new ThirdPartyManager();
        //初始化Thirdparty Manager
        mThirdPartyManager.loadConfigurations(WebAppManager.this, getConfigJSON());
    }

    private boolean mLastConfigJsonFromAsset = false;

    public boolean isLastConfigJsonFromAsset() {
        return mLastConfigJsonFromAsset ;
    }


    public ThirdPartyManager getThirdPartyManager(){
        return mThirdPartyManager;
    }

    private List<LocationConvert> mLocationConverts = Collections.emptyList();

    public List<LocationConvert> getLocationProperties() {
        return mLocationConverts;
    }


    public synchronized void parseLocationProperties(JSONObject routes) {
        ArrayList<LocationConvert> tempList = LocationConvert.parseLocationProperties(routes);
        if (mLocationConverts == null || mLocationConverts.isEmpty()) {
            mLocationConverts = tempList;
        } else {
            mLocationConverts.addAll(tempList);
        }
    }

    public JSONObject getConfigJSON() {

        //读取配置有问题，改为从asset下读
        JSONObject configObjFromAsset = readConfigFromAssetFolder();

        return configObjFromAsset;
    }


    private JSONObject readConfigFromAssetFolder() {
        AssetManager assetManager = ContextUtils.getApplicationContext().getAssets();

        InputStream inputStream = null;
        JSONObject jsonObject = null;
        try {
            //从config.json读取版本信息
            inputStream = assetManager.open("www/config.json");
            jsonObject = FilesUtility.readJSONFromFile(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }


}
