package com.uyun.hummer.utils.webview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by zhu on 2018/1/22.
 */

public class JsInteration {
    private Context mContext;
    private Activity mActivity;

    public JsInteration(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
    }
    @JavascriptInterface
    public Object require(String str) {
        Log.i("getheight","str---------"+str+"----------");
        Object object = null;
        switch (str){
            case "webview":
                WebviewUtil webviewUtil = new WebviewUtil(mContext,mActivity);
                object = webviewUtil;
                //return webviewUtil.getClass();
                //Log.i("getheight","str---------"+str+"----------");
                break;
            case "Scanner":
                Scanner scanner = new Scanner(mContext,mActivity);
                object = scanner;
                //return scanner.getClass();
                break;
            case "BaiduMap":
                BaiduMap baiduMap = new BaiduMap(mContext,mActivity);
                object = baiduMap;
                //return baiduMap.getClass();
                break;
        }
        Log.i("getheight","object---------"+object+"----------");
        return object;
        //WebActivity webActivity = new WebActivity();
            /*WebviewUtil webviewUtil = new WebviewUtil(WebActivity.this,WebActivity.this);
            return webviewUtil;*/
    }
}
