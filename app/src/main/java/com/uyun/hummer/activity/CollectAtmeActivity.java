package com.uyun.hummer.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uyun.hummer.R;
import com.uyun.hummer.base.activity.BaseFragmentActivity;
import com.uyun.hummer.base.view.BaseWebView;
import com.uyun.hummer.httputils.CookieManager;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.utils.SystemUtils;
import com.uyun.hummer.utils.WebViewUtils;

import java.util.Locale;

/**
 * Created by zhu on 2017/7/27.
 */

public class CollectAtmeActivity extends BaseFragmentActivity {
    private String title;
    private String url;
    private BaseWebView mWebView;
    private TextView tit_text;
    private LinearLayout back;
    public FileUtilsMethods mFileUtilsMethod;
    public String realHost = null;
    private String indevFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_atme);
        mFileUtilsMethod = new FileUtilsMethods(this);
        realHost = PreferenceUtils.getString(this, Globe.SERVER_HOST,"");
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        indevFile = intent.getStringExtra("url");
        url = SystemUtils.getUrlWithName(indevFile,realHost);
        tit_text = (TextView) findViewById(R.id.tit_text);
        if(title.length()<=20){
            tit_text.setText(title);
        }else {
            title = title.substring(0,19)+"...";
            tit_text.setText(title);
        }

        back = (LinearLayout) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mWebView.canGoBack()) {
                    mWebView.goBack();
                }else {
                    finish();
                }
            }
        });
        mWebView = (BaseWebView) findViewById(R.id.son_web);
        mWebView.initWebView();
        mWebView.clearCache(true);
        mWebView.addJavascriptInterface(new JsInteration(), "Android");

        String string = url.substring(0,1);
        if(string.equals("/")){
            CookieManager.getInstance(CollectAtmeActivity.this).syncCookie(Globe.SERVER_HOST+url);
            mWebView.loadUrl(Globe.SERVER_HOST+url);
        }else {
            CookieManager.getInstance(CollectAtmeActivity.this).syncCookie(url);
            mWebView.loadUrl(url);

        }

        mWebView.setWebViewClient(new WebViewClient() {
            /*@Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                view.clearHistory();
            }*/
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                Log.i("CollectAtmeActivity","onPageFinished cookies other= " + cookies);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse response = WebViewUtils.getInstance(CollectAtmeActivity.this).shouldOverrideInterceptRequest(mFileUtilsMethod,view,request.getUrl().toString());
                if(response != null){
                    return response;
                }else {
                    return super.shouldInterceptRequest(view, request);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse response = WebViewUtils.getInstance(CollectAtmeActivity.this).shouldOverrideInterceptRequest(mFileUtilsMethod,view,url);
                if(response != null){
                    return response;
                }else {
                    return super.shouldInterceptRequest(view, url);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFileUtilsMethod != null) {
            mFileUtilsMethod.cancelAllUrl();
            mFileUtilsMethod = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(mWebView.canGoBack()) {
                mWebView.goBack();
            }else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public class JsInteration {

        @JavascriptInterface
        public  void close(){
            finish();
        }

        @JavascriptInterface
        public  void back(){
            if(mWebView.canGoBack()) {
                mWebView.goBack();
            }else {
                finish();
            }
        }

        @JavascriptInterface
        public String getLanguage(){
            Locale locale = getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            Log.d("language", "language--------"+language);
            return language;
        }
    }
}
