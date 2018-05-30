package com.uyun.hummer.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Liyun on 2017/5/11.
 */

public class SystemUtils {
    public static Bitmap zoomImg(Bitmap bm, int newWidth , int newHeight){
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
    public static String getVersionName(Context context){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
    public static boolean isAppToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public static String getUrlWithName(String name,String realHost){
        String url = null;
        if(TextUtils.isEmpty(realHost)) {
            url = Globe.SERVER_HOST + Globe.SECOND_HOST + name;
        }else{
            if(realHost.endsWith("/")){
                realHost = realHost.substring(0,realHost.length()-1);
            }
            if(!name.startsWith("/")){
                name = "/" + name;
            }
            url = realHost + "/mobile" + name;
        }
        return url;
    }
    public static boolean isConnByHttp(String uri) {
        boolean isConn = false;
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(uri);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000 * 8);
            if (conn.getResponseCode() == 200) {
                isConn = true;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("","libzone.cn exception:" + e);
        }
        return isConn;
    }
}
