package com.uyun.hummer.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;
import com.uyun.hummer.R;
import com.uyun.hummer.ViewInterface.IActivityViewInterface;
import com.uyun.hummer.ViewInterface.INeedForOpenApi;
import com.uyun.hummer.ViewInterface.IOtherWebViewInterface;
import com.uyun.hummer.ViewInterface.IPhotoInterface;
import com.uyun.hummer.base.activity.BaseFragmentActivity;
import com.uyun.hummer.base.view.BaseX5WebView;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.javaScriptInterface.AllJavaScriptInterface;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.utils.webview.JsInteration;
import com.uyun.hummer.view.BaseApplication;
import com.uyun.hummer.view.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Subscriber;


public class WebActivity extends BaseFragmentActivity implements View.OnClickListener ,IOtherWebViewInterface,INeedForOpenApi,IPhotoInterface,IActivityViewInterface {
    public final static int RESULT_CLOSE = 1000;
    private EditText etWebsite;
    private TextView tvEnter, tvStatus;
    private com.tencent.smtt.sdk.WebView webView;
    private String url = "https://www.baidu.com/";
    private String injectionJS;
    private boolean isInjection = false;
    private String jsUrl;
    private FileUtilsMethods mFileUtilsMethods;
    private String pageParam;

    private TextView backText,openText;
    private TextView tit_text;
    //private LinearLayout back;
    private static final int OPEN_MAP = 2;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private RelativeLayout tit_relativeLayout;
    private boolean isSetTit = false;
    private LinearLayout back;
    private boolean isOpenScan = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_web);
        getInjectionJS();
        mFileUtilsMethods = new FileUtilsMethods(this);
        LogUtil.i("WebActivity.onCreate()");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getBundle();
        initView();
        initTitle();
        loadUrl(url);
        openWebsite();
        IntentFilter publish = new IntentFilter();
        publish.addAction("publish");
        registerReceiver(jsreceiver, publish);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("scanUrl");
        registerReceiver(receiver, intentFilter);
        init();
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isOpenScan){
                return;
            }
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            String scanUrl = PreferenceUtils.getString(WebActivity.this,PreferenceUtils.SCAN_JSON_URL,"");
            String scanJson = PreferenceUtils.getString(WebActivity.this,PreferenceUtils.SCAN_JSON,"");
            Log.d("onReceive", "scanUrl11111--------"+scanUrl);
            Log.d("onReceive", "scanJson111111--------"+scanJson);
            webView.loadUrl("javascript:postMessageToWebView('" + scanUrl + "','" + scanJson + "')");
            isOpenScan = false;
        }
    };
    BroadcastReceiver jsreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = PreferenceUtils.getString(WebActivity.this,PreferenceUtils.PUBLISH_NAME,"");
            //String message = PreferenceUtils.getString(OtherWebActivity.this,PreferenceUtils.PUBLISH_MESSAGE,"");
            Log.i("getheight","onReceive---------"+name+"----------");
            //webView.loadUrl("javascript:webviewSubscribe('" + name + "' , '" + message + "')");
            webView.loadUrl("javascript:webviewSubscribe('" + name + "' , '" + "" + "')");
        }
    };
    public void  initTitle(){
        tit_relativeLayout.setVisibility(View.GONE);
        String titJson = PreferenceUtils.getString(WebActivity.this,PreferenceUtils.SHOWTIT_JSON,"");
        try {
            JSONObject jsonObject = new JSONObject(titJson);
            int i = jsonObject.getInt("orientation");
            setOrientation(i);
            String tit = jsonObject.getString("title");
            tit_relativeLayout.setVisibility(View.VISIBLE);
            tit_text.setText(tit);
            isSetTit = true;
        } catch (JSONException e) {
            e.printStackTrace();
            tit_relativeLayout.setVisibility(View.GONE);
        }
        PreferenceUtils.put(WebActivity.this, PreferenceUtils.SHOWTIT_JSON, "");
    }
    public void setOpenScan(boolean scan){
        isOpenScan = scan;
    }
    public void setTitle(String tit){
        tit_text.setText(tit);
    }
    public void  showTitle(boolean isShow){
        if(isShow){
            tit_relativeLayout.setVisibility(View.VISIBLE);
        }else {
            tit_relativeLayout.setVisibility(View.GONE);
        }

    }
    public void getInjectionJS(){
        try {
            InputStream in = getClass().getResourceAsStream("/assets/mobile/bridge.js");
            byte buff[] = new byte[1024];
            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
            do {
                int numread = in.read(buff);
                if (numread <= 0) {
                    break;
                }
                fromFile.write(buff, 0, numread);
            } while (true);
            injectionJS = fromFile.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");
        jsUrl = bundle.getString("jsUrl");
        //Log.i("onReceivedError","@@@@@@@@"+jsUrl);
        //downloadAudioFile(jsUrl);
    }

    public static void gotoActivity(String url,String jsUrl) {
        if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
            //ToastUtil.show("网址错误!");
            return;
        }
        Intent intent = new Intent(BaseApplication.getInstance(), WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("jsUrl", jsUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApplication.getInstance().startActivity(intent);
    }
    private void downloadAudioFile(String downloadUrl) {
        Subscriber<Response<ResponseBody>> subscriber = new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Response<ResponseBody> responseBodyResponse) {
                try {
                    BufferedInputStream bufis = new BufferedInputStream(responseBodyResponse.body().byteStream());
                    getInjectionJS(bufis);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mFileUtilsMethods.downloadAudioFile(subscriber, downloadUrl);
    }
    private void getInjectionJS(BufferedInputStream in){
        try {
            byte buff[] = new byte[1024];
            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
            do {
                int numread = in.read(buff);
                if (numread <= 0) {
                    break;
                }
                fromFile.write(buff, 0, numread);
            } while (true);
            injectionJS = fromFile.toString();
            Log.i("onReceivedError","----------"+injectionJS);
            //Log.i("onReceivedError","injectionJS@@@@@@@@"+injectionJS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void openWebsite() {
        etWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWebsite.setText("");
                tvEnter.setText("进入");
            }
        });
    }

    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        tit_relativeLayout = (RelativeLayout)findViewById(R.id.tit_relativeLayout);
        tit_text = (TextView) findViewById(R.id.tit_text);
        etWebsite = (EditText) findViewById(R.id.etWebsite);
        tvEnter = (TextView) findViewById(R.id.tvEnter);
        openText = (TextView) findViewById(R.id.open);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        webView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.addJavascriptInterface(new AllJavaScriptInterface(this,this), "Android");
        webView.addJavascriptInterface(new JsInteration(this,this), "native");

        tvEnter.setOnClickListener(this);
        openText.setOnClickListener(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        int tbsVersion = QbSdk.getTbsVersion(this);
        String TID = QbSdk.getTID();
        String qBVersion = QbSdk.getMiniQBVersion(this);
        tvStatus.setText("TbsVersion:" + tbsVersion + "\nTID:" + TID + "\nMiniQBVersion:" + qBVersion);

        /*tit_text = (TextView) findViewById(R.id.tit_text);
        back = (LinearLayout) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(webView.canGoBack()) {
                    webView.goBack();
                }else {
                    finish();
                }
            }
        });*/
    }

    private void loadUrl(String url) {
        etWebsite.setText(url);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                if(!isSetTit){
                    String title = webView.getTitle();
                    tit_text.setText(title);
                }
                if(!isInjection){
                    webView.loadUrl("javascript:" + injectionJS);
                    //webView.loadUrl("javascript:nativeload()");
                    Log.i("onReceivedError","@@@@@@@@"+injectionJS);
                    //webView.loadUrl("javascript:_mobile.dialog.close()");
                    isInjection = true;
                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("mSelectPath", "onActivityResult-----"+resultCode);
        if (resultCode == this.RESULT_OK) {
            switch (requestCode) {
                case OPEN_MAP:

                    String mapJson = data.getStringExtra("mapJson");
                    Log.i("mSelectPath", "path-----" + mapJson);
                    String json  = PreferenceUtils.getString(WebActivity.this,PreferenceUtils.MAP_JSON,"");
                    webView.loadUrl("javascript:postMessageToWebView('" + mapJson + "','" + json + "')");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFileUtilsMethods != null) {
            mFileUtilsMethods.cancelAllUrl();
            mFileUtilsMethods = null;
        }
        unregisterReceiver(jsreceiver);
        unregisterReceiver(receiver);
    }
    public void webviewBack(){
        if (webView.canGoBack()) {
            webView.goBack();
        }else {
            finish();
        }
    }
    public void setOrientation(int orientation){
        if(orientation==1){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else if(orientation==0){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
    public void setScrollViewHeight(int height){
        if(webView == null){
            webView = (BaseX5WebView)findViewById(R.id.webView);
        }
        int webHeight = height;
        ViewGroup.LayoutParams lpWeb = webView.getLayoutParams();
        lpWeb.height = webHeight;
        webView.setLayoutParams(lpWeb);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //webView.loadUrl("javascript:_mobile.dialog.close()");
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvEnter:
                String url = etWebsite.getText().toString();
                if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
                    //ToastUtil.show("网址错误!");
                    return;
                }
                loadUrl(url);
                break;
            case R.id.open:
                //OtherWebActivity.gotoActivity("https://www.baidu.com","");
                /*Intent intent = new Intent(BaseApplication.getInstance(), OtherWebActivity.class);
                intent.putExtra("url", "https://www.baidu.com");
                intent.putExtra("jsUrl", jsUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                //BaseApplication.getInstance().startActivity(intent);
                break;
            case R.id.back:
                webviewBack();
                break;
        }
    }
    @Override
    public void headerChange(String title) {
    }

    @Override
    public String _needForOpenApi() {
        String needForOpenApi = PreferenceUtils.getString(WebActivity.this,PreferenceUtils.LABEL_WEBVIEW_JSON,"");
        return needForOpenApi;
    }

    @Override
    public void showPopupWindow() {
    }

    @Override
    public void openPhoto() {
    }

    @Override
    public void openCamera() {
    }

    @Override
    public void back() {
        if(webView.canGoBack()) {
            webView.goBack();
        }else {
            finish();
        }
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void openMap(String jsonData) {
        Intent intent = new Intent(this, BaiduMapActivity.class);
        intent.putExtra("mapJson", jsonData);
        startActivityForResult(intent,2);
    }

    @Override
    public void screenRotation(String jsonData) {

    }

    public class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void init() {
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
                                       JsResult arg3) {
                return super.onJsConfirm(arg0, arg1, arg2, arg3);
            }

            View myVideoView;
            View myNormalView;
            IX5WebChromeClient.CustomViewCallback callback;

            @Override
            public void onHideCustomView() {
                if (callback != null) {
                    callback.onCustomViewHidden();
                    callback = null;
                }
                if (myVideoView != null) {
                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
                    viewGroup.removeView(myVideoView);
                    viewGroup.addView(myNormalView);
                }
            }

            @Override
            public boolean onJsAlert(WebView arg0, String arg1, String arg2,
                                     JsResult arg3) {
                /**
                 * 这里写入你自定义的window alert
                 */
                return super.onJsAlert(null, arg1, arg2, arg3);
            }
        });

        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                //TbsLog.d(TAG, "url: " + arg0);
                new AlertDialog.Builder(WebActivity.this)
                        .setTitle("allow to download？")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(
                                                WebActivity.this,
                                                "fake message: i'll download...",
                                                1000).show();
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(
                                                WebActivity.this,
                                                "fake message: refuse download...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(
                                                WebActivity.this,
                                                "fake message: refuse download...",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
            }
        });

        WebSettings webSetting = webView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        /*if (mIntentUrl == null) {
            mWebView.loadUrl(mHomeUrl);
        } else {
            mWebView.loadUrl(mIntentUrl.toString());
        }*/
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }
}
