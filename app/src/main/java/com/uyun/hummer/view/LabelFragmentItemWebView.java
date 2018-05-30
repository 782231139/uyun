package com.uyun.hummer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.uyun.hummer.ViewInterface.INeedForOpenApi;
import com.uyun.hummer.javaScriptInterface.AllJavaScriptInterface;
import com.uyun.hummer.utils.PreferenceUtils;

/**
 * Created by Liyun on 2017/3/16.
 */

public class LabelFragmentItemWebView extends WebView implements INeedForOpenApi{
    private Context mContext;
    public LabelFragmentItemWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebView(context);
    }

    public LabelFragmentItemWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LabelFragmentItemWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initWebView(context);
    }

    public LabelFragmentItemWebView(Context context) {
        super(context);
        initWebView(context);
    }

    public void initWebView(Context context) {
        mContext = context;
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient() {
        });
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings webSettings = getSettings();
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        this.addJavascriptInterface(new AllJavaScriptInterface(mContext,this), "Android");
    }

    @Override
    public String _needForOpenApi() {
        String needForOpenApi = PreferenceUtils.getString(mContext,PreferenceUtils.LABEL_WEBVIEW_JSON,"");
        return needForOpenApi;
    }
}
