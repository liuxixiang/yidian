package com.linken.newssdk.core.newweb;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Créé par liusiqian 2017/4/26.
 *
 * Hybrid包url的匹配与替换
 */

public class LocationConvert
{
    public final String key;
    public final String route;
    
    public LocationConvert(String key, String route)
    {
        this.key = key;
        this.route = route;
    }
    
    public static ArrayList<LocationConvert> parseLocationProperties(JSONObject routes){
        ArrayList<LocationConvert> locationConverts = new ArrayList<>(routes.length());
        
        Iterator<String> keys = routes.keys();
        
        while (keys.hasNext()) {
            String key = keys.next();
            String route = routes.optString(key);
            
            if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(route)) {
                if(key.contains("?")){
                    key = key.split("\\?")[0];
                }
                if(route.contains("?")){
                    route = route.split("\\?")[0];
                }
                
                LocationConvert convert = new LocationConvert(key,route);
                locationConverts.add(convert);
            }
        }
        return locationConverts;
    }
    
    public static String getURL(String url, List<LocationConvert> locationConverts){
        if(TextUtils.isEmpty(url)){
            return null;
        }
        String path = null;
        
        for (LocationConvert convert : locationConverts) {
            if (!TextUtils.isEmpty(convert.key) && !TextUtils.isEmpty(convert.route) && url.contains(convert.key)){
                path = url.replace(convert.key, convert.route);
                break;
                }
            }
        return path;
    }
    
    @Override
    public String toString() {
        return "{" +
                "key='" + key + '\'' +
                ", route='" + route + '\'' +
                '}';
    }
}
