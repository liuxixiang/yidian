package com.linken.newssdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;


import java.util.Map;
import java.util.Set;

/**
 * @author zhangzhun
 * @date 2018/1/18
 */

public class SPUtils {

    private static final String TAG = "SPUtils";
    private static final String FILE_NAME = "share_data";
    private static Context context;

    public static void init(Context c){
        context = c;
    }

    public static void applyEditor(SharedPreferences.Editor editor) {
        editor.apply();
    }

    public static void put(String key, Object value) {
        put(context, key, value);
    }

    public static void put(Context context, String key, Object value) {
        put("share_data", context, key, value);
    }

    public static void put(String spName, Context context, String key, Object value) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        SharedPreferences.Editor editor = sp.edit();
        if(value instanceof String) {
            editor.putString(key, (String)value);
        } else if(value instanceof Integer) {
            editor.putInt(key, ((Integer)value).intValue());
        } else if(value instanceof Boolean) {
            editor.putBoolean(key, ((Boolean)value).booleanValue());
        } else if(value instanceof Float) {
            editor.putFloat(key, ((Float)value).floatValue());
        } else if(value instanceof Long) {
            editor.putLong(key, ((Long)value).longValue());
        } else if(value instanceof Set) {
            if(Build.VERSION.SDK_INT >= 11) {
                editor.putStringSet(key, (Set)value);
            }
        } else if(value == null) {
            editor.remove(key);
        }

        editor.apply();
    }

    public static Object get(String key, Object object) {
        return get(context, key, object);
    }

    public static Object get(Context context, String key, Object object) {
        return get("share_data", context, key, object);
    }

    public static Object get(String spName, Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        return object instanceof String?sp.getString(key, (String)object)
                :(object instanceof Integer?Integer.valueOf(sp.getInt(key, ((Integer)object).intValue()))
                :(object instanceof Boolean?Boolean.valueOf(sp.getBoolean(key, ((Boolean)object).booleanValue()))
                :(object instanceof Float?Float.valueOf(sp.getFloat(key, ((Float)object).floatValue()))
                :(object instanceof Long?Long.valueOf(sp.getLong(key, ((Long)object).longValue()))
                :(null == object?sp.getString(key, (String)null):null)))));
    }

    public static String getString(String spName, Context context, String key, String defaultValuye) {
        return (String)get(spName, context, key, defaultValuye);
    }

    public static void setString(String spName, Context context, String key, String value) {
        put(spName, context, key, value);
    }

    public static int getInt(String spName, Context context, String key, int value) {
        return ((Integer)get(spName, context, key, Integer.valueOf(value))).intValue();
    }

    public static void setInt(String spName, Context context, String key, int value) {
        put(spName, context, key, Integer.valueOf(value));
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getInt("share_data", context, key, defaultValue);
    }

    public static void setInt(Context context, String key, int value) {
        setInt("share_data", context, key, value);
    }

    public static long getLong(String spName, Context context, String key, long value) {
        Object result = get(spName, context, key, Long.valueOf(value));
        long var6 = 0L;
        if(result != null && result instanceof Long) {
            var6 = ((Long)result).longValue();
        }

        return var6;
    }

    public static void setLong(String spName, Context context, String key, long value) {
        put(spName, context, key, Long.valueOf(value));
    }

    public static void remove(String key) {
        remove(context, key);
    }

    public static void remove(Context context, String key) {
        remove("share_data", context, key);
    }

    public static void remove(String spName, Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences("share_data", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static void clear(Context context, String spName) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static boolean contains(String key) {
        return contains(context, key);
    }

    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("share_data", 0);
        return sp.contains(key);
    }

    public static boolean contains(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        return sp.contains(key);
    }

    public static Map<String, ?> getAll(Context context) {
        return getAll(context, "share_data");
    }

    public static Map<String, ?> getAll(Context context, String spName) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        return sp.getAll();
    }

    public static boolean clearKey(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        sp.edit().remove(key).apply();
        return true;
    }

    public static SharedPreferences.Editor getEditor(String spName, Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, 0);
        return sp.edit();
    }
}
