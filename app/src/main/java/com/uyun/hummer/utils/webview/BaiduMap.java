package com.uyun.hummer.utils.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.uyun.hummer.activity.BaiduMapActivity;
import com.uyun.hummer.utils.PreferenceUtils;

/**
 * Created by zhu on 2018/1/22.
 */

public class BaiduMap {

    private Context mContext;
    private Activity mActivity;
    private static final int OPEN_MAP = 2;
    public BaiduMap(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
    }

    @JavascriptInterface
    public void open(String jsonData) {
        Log.i("getheight","BaiduMap-----"+jsonData);
        PreferenceUtils.put(mActivity, PreferenceUtils.MAP_JSON, jsonData);
        Intent intent = new Intent(mActivity, BaiduMapActivity.class);
        intent.putExtra("mapJson", jsonData);
        mActivity.startActivityForResult(intent,OPEN_MAP);
    }

}
