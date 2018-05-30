package com.uyun.hummer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by LIYUN on 2015/10/15.
 */
public class PreferenceUtils {

    public static  final String SERVICE_IP="service_ip";
    public static  final String TENANT_ID="tenant_id";
    public static  final String USER_ID="user_id";
    public static final String TOKEN="token";
    public static final String ACCOUNT="username";
    public static final String PASSWORD="password";
    public static final String USERINFO = "userinfo";
    public static final String CODE_SWICH = "codeswich";
    public static final String LABEL_WEBVIEW_JSON = "label_webview_json";
    public static final String VERSION_UPDATE = "version_update";
    public static final String APK_DOWNLOAD_URL = "apk_download_url";
    public static final String APIKEYS = "apikeys";

    public static  final String CAMERA_INDEX="camera_index";
	
	public static final String WEBVIEW_URL="webview_url";
    public static final String INJECTION_URL="injection_url";
    public static final String PAGE_PARAM="pageParam";
    public static final String PUBLISH_NAME="publish_name";
    public static final String MAP_JSON="map_json";
    public static final String SHOWTIT_JSON="showtit_json";

    public static final String SCAN_JSON_URL="scan_json_url";
    public static final String SCAN_JSON ="scan_json";

    /** 清空数据 */
    public static void reset(final Context ctx) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        edit.clear();
        edit.commit();
    }

    public static String getString(Context ctx, String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(key, defValue);
    }

    public static long getLong(Context ctx, String key, long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getLong(key, defValue);
    }

    public static float getFloat(Context ctx, String key, float defValue) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getFloat(key, defValue);
    }

    public static void put(Context ctx, String key, String value) {
        putString(ctx, key, value);
    }

    public static void put(Context ctx, String key, int value) {
        putInt(ctx, key, value);
    }

    public static void put(Context ctx, String key, float value) {
        putFloat(ctx, key, value);
    }

    public static void put(Context ctx, String key, boolean value) {
        putBoolean(ctx, key, value);
    }

    public static void putFloat(Context ctx, String key, float value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static SharedPreferences getPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static int getInt(Context ctx, String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getInt(key, defValue);
    }

    public static boolean getBoolean(Context ctx, String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(key, defValue);
    }

    public static boolean hasString(Context ctx, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.contains(key);
    }

    public static void putString(Context ctx, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putLong(Context ctx, String key, long value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void putBoolean(Context ctx, String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putInt(Context ctx, String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void remove(Context ctx, String... keys) {
        if (keys != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (String key : keys) {
                editor.remove(key);
            }
            editor.commit();
        }
    }
}

