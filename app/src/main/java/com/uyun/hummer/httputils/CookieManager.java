package com.uyun.hummer.httputils;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.uyun.hummer.utils.PreferenceUtils;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by Liyun on 2017/4/7.
 */

public class CookieManager implements CookieJar {


    public static String APP_PLATFORM = "app-platform";
    public static String PATTERN_L2DOMAIN = "\\w*\\.\\w*:";
    public static String PATTERN_IP = "(\\d*\\.){3}\\d*";

    private static Context mContext;
    private static CookieManager instance;
    private static PersistentCookieStore cookieStore;
    public static CookieManager getInstance(Context context){
        if(instance==null){
            synchronized(CookieManager.class){
                instance = new CookieManager(context);
            }
        }
        return instance;
    }

    private CookieManager(Context context) {
        mContext = context;
        if (cookieStore == null) {
            cookieStore = new PersistentCookieStore(mContext);
        }

    }


    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }
    public String getCookieDomain(String url) {
        Pattern ipPattern = Pattern.compile(PATTERN_IP);
        Matcher matcher = ipPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        Pattern pattern = Pattern.compile(PATTERN_L2DOMAIN);
        matcher = pattern.matcher(url);
        if (matcher.find()) {
            String domain =  matcher.group();
            return domain.substring(0, domain.length() - 1);
        }

        return null;
    }
    public void syncCookie(String url){
        try{
            URL uri = new URL(url);
            CookieSyncManager.createInstance(mContext);
            String host = getCookieDomain(uri.getHost());
            if(host == null){
                host = uri.getHost().substring(uri.getHost().indexOf("."));
            }
            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();
            cookieManager.removeAllCookie();
            String oldCookie = cookieManager.getCookie(url);
            if(oldCookie != null){
            }
            String tenantId = PreferenceUtils.getString(mContext,PreferenceUtils.TENANT_ID,"");
            String userId = PreferenceUtils.getString(mContext,PreferenceUtils.USER_ID,"");
            String token = PreferenceUtils.getString(mContext,PreferenceUtils.TOKEN,"");

            cookieManager.setCookie(host, "tenantId=" +tenantId);
            cookieManager.setCookie(host, "userId=" +userId);
            cookieManager.setCookie(host, "token=" +token);
            cookieManager.setCookie(host,"skin=white");
            CookieSyncManager.getInstance().sync();

            String newCookie = cookieManager.getCookie(host);
            if(newCookie != null){
            }
        }catch(Exception e){
        }
    }
}
