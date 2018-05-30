package com.uyun.hummer.utils.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.uyun.hummer.activity.PrintActivity;
import com.uyun.hummer.activity.SkipWebActivity;
import com.uyun.hummer.activity.WebActivity;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.zxing.ScannerActivity;

/**
 * Created by zhu on 2018/1/22.
 */

public class Scanner {
    private Context mContext;
    private Activity mActivity;

    public Scanner(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
    }
    @JavascriptInterface
    public void openPrint(String base64Data){
        Log.d("showToast", "openPrint--------"+base64Data);
        Intent intent = new Intent(mContext, PrintActivity.class);
        intent.putExtra("base64Data", base64Data);
        mContext.startActivity(intent);
    }
    @JavascriptInterface
    public void openScan(String scanJson) {
        PreferenceUtils.put(mActivity, PreferenceUtils.SCAN_JSON, scanJson);
        Log.d("showToast", "openScan--------openScan");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mActivity instanceof WebActivity){
                    ((WebActivity) mActivity).setOpenScan(true);
                }else if(mActivity instanceof SkipWebActivity){
                    ((SkipWebActivity) mActivity).setOpenScan(true);
                }

                Intent intent = new Intent(mActivity, ScannerActivity.class);
                mActivity.startActivity(intent);
            }
        });
    }
}
