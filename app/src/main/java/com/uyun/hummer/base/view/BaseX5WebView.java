package com.uyun.hummer.base.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


/**
 * Created by Liyun on 2017/3/16.
 */

public class BaseX5WebView extends WebView {
    public interface IScrollListener
    {
        void onScrollChanged(int scrollY);
    }

    private IScrollListener mScrollListener;

    public void setOnScrollListener(IScrollListener listener)
    {
        mScrollListener = listener;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        //只要是通过webview的滚动距离 t
        if (mScrollListener != null)
        {
            mScrollListener.onScrollChanged(t);
        }
    }

    public BaseX5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebView();
    }

    public BaseX5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/

    public BaseX5WebView(Context context) {
        super(context);
    }

    public void initWebView() {
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient());
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings webSettings = getSettings();
        if(Build.VERSION.SDK_INT >= 19) {
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

        webSettings.setAppCacheEnabled(true);
    }

}
