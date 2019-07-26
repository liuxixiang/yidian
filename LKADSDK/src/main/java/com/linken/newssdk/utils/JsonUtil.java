package com.linken.newssdk.utils;

import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtil {
    private static final String TAG = JsonUtil.class.getSimpleName();

    private JsonUtil() {
    }



    public static String readStringFromJson(JSONObject json, String name) {
        if (json == null) {
            return "";
        }

        return json.optString(name, "");
    }

    public static int readIntFromJson(JSONObject json, String name, int defaultValue) {
        if (json == null) {
            return defaultValue;
        }

        return json.optInt(name, defaultValue);
    }

    public static float readFloatFromJson(JSONObject json, String name, float defaultValue) {
        if (json == null) {
            return defaultValue;
        }
        float result = defaultValue;
        if (json.has(name)) {
            try {
                result = json.getLong(name);
            } catch (JSONException e) {
            }
        }

        return result;
    }

    public static boolean readBooleanFromJson(JSONObject json, String name, boolean defaultValue) {
        if (json == null) {
            return defaultValue;
        }

        return json.optBoolean(name, defaultValue);
    }

    public static long readLongFromJson(JSONObject json, String name, long defaultValue) {
        if (json == null) {
            return defaultValue;
        }
        long result = defaultValue;
        if (json.has(name)) {
            try {
                result = json.getLong(name);
            } catch (JSONException e) {
            }
        }

        return result;
    }

    public static void putStringToJson(JSONObject json, String name, String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        try {
            json.put(name, value);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static void putIntToJson(JSONObject json, String name, int value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static void putLongToJson(JSONObject json, String name, long value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static void putFloatToJson(JSONObject json, String name, float value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static void putDoubleToJson(JSONObject json, String name, double value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static void putBooleanToJson(JSONObject json, String name, boolean value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static void putStringArray(JSONObject json, String name, String[] values) {
        try {
            JSONArray arr = new JSONArray();
            for (String s : values) {
                if (!TextUtils.isEmpty(s)) {
                    arr.put(s);
                }
            }
            json.put(name, arr);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static JSONObject putJsonToJson(JSONObject json, String key, JSONObject value) {
        try {
            return json.putOpt(key, value);
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return null;
    }

    public static JSONObject putJsonArrayToJson(JSONObject json, String key, JSONArray value) {
        try {
            return json.putOpt(key, value);
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return null;
    }

    public static String[] parseJSONString(JSONArray array) {
        String[] strs;
        if (array != null && array.length() > 0) {
            strs = new String[array.length()];
            int count = 0;
            for (int i = 0; i < array.length(); i++) {
                String str = array.optString(i);
                if (!TextUtils.isEmpty(str)) {
                    strs[count++] = str;
                }
            }
            if (count < array.length()) {
                String[] copy = new String[count];
                System.arraycopy(strs, 0, copy, 0, count);
                strs = copy;
            }
        } else {
            strs = new String[0];
        }
        return strs;
    }

    public static JSONObject toJson(String transInfo) {
        try {
            return new JSONObject(transInfo);
        } catch (Exception e) {
            LogUtils.e("CHI", e.getMessage());
        }
        return null;
    }
}
