package com.uyun.hummer.activity;

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
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.DownloadListener;
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
import com.uyun.hummer.httputils.CookieManager;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.javaScriptInterface.AllJavaScriptInterface;
import com.uyun.hummer.model.bean.PageParamInfo;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.utils.WebViewUtilsX5;
import com.uyun.hummer.utils.webview.JsInteration;
import com.uyun.hummer.view.BaseApplication;
import com.uyun.hummer.view.LogUtil;
import com.uyun.hummer.view.refresh.SuperSwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Subscriber;

public class SkipWebActivity extends BaseFragmentActivity implements View.OnClickListener,IOtherWebViewInterface,INeedForOpenApi,IPhotoInterface,IActivityViewInterface, SwipeRefreshLayout.OnRefreshListener{
    protected final static int RESULT_CLOSE = 1000;
    private EditText etWebsite;
    private TextView tvEnter, tvStatus;
    private BaseX5WebView webView;
    private String url = "https://www.baidu.com/";
    private String injectionJS;
    private boolean isInjection = false;
    private String jsUrl;
    private FileUtilsMethods mFileUtilsMethods;
    private String pageParam;

    //private TextView backText,openText;
    private PageParamInfo pageParamInfo;
    private static final int OPEN_MAP = 2;

    private TextView tit_text;
    private RelativeLayout tit_relativeLayout;
    private boolean isSetTit = false;
    private LinearLayout back;
    private boolean isOpenScan = false;
    //private XScrollView mScrollView;
    //private View view;
    private int tit;

    private SuperSwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar progressBar;

    private TextView textView;

    private ImageView imageView;
    private boolean isRefresh = false;
    private TextView close_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skipweb2);
        init();
        /*view = LayoutInflater.from(this).inflate(R.layout.activity_skipweb, null);
        mScrollView.setView(view);*/
        getInjectionJS();
        webView.loadUrl("javascript:" + injectionJS);
        mFileUtilsMethods = new FileUtilsMethods(this);
        LogUtil.i("WebActivity.onCreate()");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getBundle();
        initView();

        initTitle();

        openWebsite();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.andly.bro");
        registerReceiver(receiver, intentFilter);

        IntentFilter publish = new IntentFilter();
        publish.addAction("publish");
        registerReceiver(jsreceiver, publish);

        IntentFilter scanUrl = new IntentFilter();
        scanUrl.addAction("scanUrl");
        registerReceiver(scanReceiver, scanUrl);
        loadUrl();
    }
    private ScrollView mScrollView;
    private void init(){
        /*mScrollView = (XScrollView) findViewById(R.id.scroll_view);
        mScrollView.setPullRefreshEnable(true);
        mScrollView.setPullLoadEnable(false);
        mScrollView.setAutoLoadEnable(false);
        mScrollView.setIXScrollViewListener(this);
        mScrollView.setRefreshTime(getTime());
        mScrollView.setFillViewport(true);*/
        swipeRefreshLayout = (SuperSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        View child = LayoutInflater.from(swipeRefreshLayout.getContext())
                .inflate(R.layout.layout_head, null);
        progressBar = (ProgressBar) child.findViewById(R.id.pb_view);
        textView = (TextView) child.findViewById(R.id.text_view);
        textView.setText("下拉刷新");
        imageView = (ImageView) child.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.arrow_up);
        progressBar.setVisibility(View.GONE);
        //swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setHeaderView(child);
        swipeRefreshLayout.setTargetScrollWithLayout(true);
        swipeRefreshLayout.setEnabled(false);
        stopPullDownRefresh();
        swipeRefreshLayout
                .setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {

                    @Override
                    public void onRefresh() {

                        textView.setText("正在刷新");
                        imageView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        webView.loadUrl("javascript:onPullDownRefresh()");
                        System.out.println("debug:onRefresh");
                        /*new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                System.out.println("debug:stopRefresh");
                            }
                        }, 2000);*/
                    }

                    @Override
                    public void onPullDistance(int distance) {
                        System.out.println("debug:distance = " + distance);
                        //myAdapter.updateHeaderHeight(distance);
                    }

                    @Override
                    public void onPullEnable(boolean enable) {
                        if(webView.getView().getScrollY() <= 0) {
                            textView.setText(enable ? "松开刷新" : "下拉刷新");
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setRotation(enable ? 180 : 0);
                        }
                    }
                });
        webView = (BaseX5WebView) findViewById(R.id.webView);
        webView.setOnScrollListener(new BaseX5WebView.IScrollListener()
        {
            @Override
            public void onScrollChanged(int scrollY)
            {
                //这是我项目的刷新（到时候去掉，用下面的判断）
                if (scrollY == 0)//webView在顶部
                {
                    if(isRefresh){
                        swipeRefreshLayout.setEnabled(true);
                    }
                } else{
                    swipeRefreshLayout.setEnabled(false);
                }
                //swiperefreshLayout刷新
//                if (t == 0) {//webView在顶部
//                    swipeLayout.setEnabled(true);
//                } else {//webView不是顶部
//                    swipeLayout.setEnabled(false);
//                }
            }
        });

        /*webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:{
                        if(webView.getView().getScrollY() <= 0){
                            swipeRefreshLayout.setEnabled(true);
                        } else {
                            swipeRefreshLayout.setEnabled(false);
                        }
                    }
                    default:
                        break;

                }
                return false;
            }
        });*/

    }
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                System.out.println("debug:stopRefresh");
            }
        }, 2000);
    }
    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }
    @Override
    public void onResume() {
        super.onResume();
        String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
        CookieManager.getInstance(SkipWebActivity.this).syncCookie(url);
    }
    public void setEnablePullDownRefresh(boolean isrefresh){
        //mScrollView.setPullRefreshEnable(isrefresh);
        isRefresh = isrefresh;
        if(isrefresh){
            swipeRefreshLayout.setEnabled(true);
        }else {
            swipeRefreshLayout.setEnabled(false);
        }
    }

    public void stopPullDownRefresh(){
        /*mScrollView.stopRefresh();
        mScrollView.setRefreshTime(getTime());*/
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    public void startPullDownRefresh(){
        webView.loadUrl("javascript:onPullDownRefresh()");
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        //mScrollView.autoRefresh();
    }
    BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isOpenScan){
                return;
            }
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            String scanUrl = PreferenceUtils.getString(SkipWebActivity.this, PreferenceUtils.SCAN_JSON_URL,"");
            String scanJson = PreferenceUtils.getString(SkipWebActivity.this, PreferenceUtils.SCAN_JSON,"");
            Log.d("onReceive", "scanUrl22222--------"+scanUrl);
            Log.d("onReceive", "scanJson222222--------"+scanJson);
            webView.loadUrl("javascript:postMessageToWebView('" + scanUrl + "','" + scanJson + "')");
            isOpenScan = false;
        }
    };
    public void setOpenScan(boolean scan){
        isOpenScan = scan;
    }
    public void  initTitle(){
        String titJson = PreferenceUtils.getString(SkipWebActivity.this, PreferenceUtils.SHOWTIT_JSON,"");
        try {
            JSONObject jsonObject = new JSONObject(titJson);
            if(jsonObject.has("orientation")){
                int i = jsonObject.getInt("orientation");
                setOrientation(i);
            }
            tit_relativeLayout.setVisibility(View.VISIBLE);
            if(jsonObject.has("title")){
                String tit = jsonObject.getString("title");
                if(tit.equals("auto")){
                    isSetTit = false;
                }else {
                    tit_text.setText(tit);
                    isSetTit = true;
                }
            }else {
                tit_relativeLayout.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            tit_relativeLayout.setVisibility(View.GONE);
        }
        PreferenceUtils.put(SkipWebActivity.this, PreferenceUtils.SHOWTIT_JSON, "");
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
    public void webviewBack(){
        if (webView.canGoBack()) {
            webView.goBack();
        }else {
            finish();
        }
    }
    BroadcastReceiver jsreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = PreferenceUtils.getString(SkipWebActivity.this, PreferenceUtils.PUBLISH_NAME,"");
            //String message = PreferenceUtils.getString(SkipWebActivity.this,PreferenceUtils.PUBLISH_MESSAGE,"");
            Log.i("getheight","onReceive---------"+name+"----------");
            //webView.loadUrl("javascript:webviewSubscribe('" + name + "' , '" + message + "')");
            webView.loadUrl("javascript:webviewSubscribe('" + name + "' , '" + "" + "')");
        }
    };

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }

    };
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");
        jsUrl = bundle.getString("jsUrl");
        Log.i("onReceivedError","@@@@@@@@"+jsUrl);
        //downloadAudioFile(jsUrl);

        Intent sendIntent = new  Intent();
        sendIntent.setAction(Globe.MESSAGE_UYUNJS);
        sendBroadcast(sendIntent);
        //mWebView.loadUrl("http://10.1.11.6:8080/guide/");

    }
    public void gotoActivity(String url, String jsUrl) {
        if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
            //ToastUtil.show("网址错误!");
            return;
        }
        Intent intent = new Intent(BaseApplication.getInstance(), SkipWebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("jsUrl", jsUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //BaseApplication.getInstance().startActivity(intent);
        startActivityForResult(intent,0);
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
            //Log.i("onReceivedError","injectionJS@@@@@@@@"+injectionJS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void openWebsite() {
        /*etWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWebsite.setText("");
                tvEnter.setText("进入");
            }
        });*/
    }

    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        close_text= (TextView) findViewById(R.id.close_text);
        close_text.setOnClickListener(this);
        tit_relativeLayout = (RelativeLayout)findViewById(R.id.tit_relativeLayout);
        tit_text = (TextView) findViewById(R.id.tit_text);
        //etWebsite = (EditText) view.findViewById(R.id.etWebsite);
        //tvEnter = (TextView) view.findViewById(R.id.tvEnter);
        //backText = (TextView) findViewById(R.id.back);
        //openText = (TextView) findViewById(R.id.open);
        //tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        webView = (BaseX5WebView) findViewById(R.id.webView);

        /*ViewTreeObserver vto = back.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                back.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                back.getHeight();
                tit = back.getHeight();
                WindowManager wm = SkipWebActivity.this.getWindowManager();
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                int height = dm.heightPixels;
                tit = back.getHeight();
                int webHeight = height - tit;
                ViewGroup.LayoutParams lpWeb = webView.getLayoutParams();
                lpWeb.height = webHeight;
                webView.setLayoutParams(lpWeb);
            }
        });*/
       /* ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                tit = mScrollView.getHeight();
                WindowManager wm = SkipWebActivity.this.getWindowManager();
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                int height = dm.heightPixels;
                //tit = back.getHeight();
                int webHeight = tit+2;
                //int webHeight = webView.getContentHeight();
                ViewGroup.LayoutParams lpWeb = webView.getLayoutParams();
                //lpWeb.height = webHeight;
                //webView.setLayoutParams(lpWeb);

                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                //layout.setLayoutParams(params);
                //webView.setLayoutParams(params);
            }
        });*/




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }


        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.addJavascriptInterface(new AllJavaScriptInterface(this,this), "Android");
        webView.addJavascriptInterface(new JsInteration(this,this), "native");
        /// tvEnter.setOnClickListener(this);
        //backText.setOnClickListener(this);
        //openText.setOnClickListener(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        /*int tbsVersion = QbSdk.getTbsVersion(this);
        String TID = QbSdk.getTID();
        String qBVersion = QbSdk.getMiniQBVersion(this);
        tvStatus.setText("TbsVersion:" + tbsVersion + "\nTID:" + TID + "\nMiniQBVersion:" + qBVersion);*/
        init(webView,SkipWebActivity.this);
    }

    private void init(com.tencent.smtt.sdk.WebView webView, final Context context) {
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
                return super.onJsAlert(null, arg1, arg2, arg3);
            }
        });

        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                //TbsLog.d(TAG, "url: " + arg0);
                new AlertDialog.Builder(context)
                        .setTitle("allow to download？")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(
                                                context,
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
                                                context,
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
                                                context,
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
        webSetting.setAppCachePath(context.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(context.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(context.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        //CookieSyncManager.createInstance(context);
        //CookieSyncManager.getInstance().sync();
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
                    String json  = PreferenceUtils.getString(SkipWebActivity.this, PreferenceUtils.MAP_JSON,"");
                    webView.loadUrl("javascript:postMessageToWebView('" + mapJson + "','" + json + "')");
                    break;
                default:
                    break;
            }
        }
    }
    private void getInjectionJS(){
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
    private void loadUrl() {
        Log.i("mSelectPath", "-----------------------------------------------------------" + url);
        String string = url.substring(0,1);
        if(string.equals("/")){
            url = Globe.SERVER_HOST+url;
            String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
            CookieManager.getInstance(SkipWebActivity.this).syncCookie(url);
            webView.loadUrl(url);
            //webView.loadUrl("http://10.1.60.111"+url);
            //webView.loadUrl("http://10.1.60.111/cmdb/mobile.html#/ci/5a54a5e1ec57ae1aa17050af?device=mobile");
            Log.i("mSelectPath", "loadUrl-----" + url);
        }else if(url.substring(0,4).equals("http")) {
            String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
            CookieManager.getInstance(SkipWebActivity.this).syncCookie(url);
            webView.loadUrl(url);
            Log.i("cookies", "cookies---1--" + cookies);
        }
        //webView.loadUrl("http://10.1.241.63:3000/#/ci/111");
        //webView.loadUrl("http://10.1.60.111/cmdb/mobile.html#/ci/5a54a5e1ec57ae1aa17050ab?device=mobile");

        //etWebsite.setText(url);
        //webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根

                if(newProgress==100){
                    String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                    CookieManager.getInstance(SkipWebActivity.this).syncCookie(url);
                    Log.i("cookies", "cookies--3---" + cookies);
                    Log.i("ttt", "@@@@@@@@-------------"+newProgress);
                    //layout_drag.removeAllViews();
                    //layout_drag.addView(subview);
                    //pg1.setVisibility(View.GONE);//加载完网页进度条消失
                    if(!isInjection){
                        webView.loadUrl("javascript:" + injectionJS);
                        //webView.loadUrl("javascript:nativeload()");
                        Log.i("onReceivedError","@@@@@@@@"+injectionJS);
                        isInjection = true;
                    }
                }else{
                    Log.i("ttt", "@@@@@@@@-------------"+newProgress);
                    //pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    //pg1.setProgress(newProgress);//设置进度值
                }

            }
        });
        webView.setWebViewClient(new WebViewClient() {
             @Override
             public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                 Log.i("cookies", "cookies--4---" + s);
                 /*String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                 CookieManager.getInstance(SkipWebActivity.this).syncCookie(url);
                 webView.loadUrl(url);
                 return true;*/
                 return super.shouldOverrideUrlLoading(webView, s);
 
             }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                CookieManager.getInstance(SkipWebActivity.this).syncCookie(url);
                Log.i("cookies", "cookies--2---" + cookies);
                int h = webView.getContentHeight();
                if(!isSetTit){
                    String title = webView.getTitle();
                    tit_text.setText(title);
                }
                if(!isInjection){
                    webView.loadUrl("javascript:" + injectionJS);
                    Log.i("onReceivedError","@@@@@@@@"+injectionJS);
                    //isInjection = true;
                }
                /*if(!isInjection){
                    webView.loadUrl("javascript:" + injectionJS);
                    //webView.loadUrl("javascript:nativeload()");
                    Log.i("onReceivedError","@@@@@@@@"+injectionJS);
                    isInjection = true;
                }*/
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFileUtilsMethods != null) {
            mFileUtilsMethods.cancelAllUrl();
            mFileUtilsMethods = null;
        }
        unregisterReceiver(receiver);
        unregisterReceiver(jsreceiver);
        unregisterReceiver(scanReceiver);
        if(webView!=null){
            WebViewUtilsX5.destroy(webView);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.tvEnter:
                String url = etWebsite.getText().toString();
                if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
                    //ToastUtil.show("网址错误!");
                    return;
                }
                loadUrl(url);
                break;*/
            case R.id.back:
                webviewBack();
                break;
            case R.id.close_text:
                finish();
                break;
            /*case R.id.back:
                *//*setResult(RESULT_CLOSE);
                finish();*//*
                *//*WebviewUtil webviewUtil = new WebviewUtil(SkipWebActivity.this,SkipWebActivity.this);
                webviewUtil.popRoot();*//*
                //setResult(1);
                finish();
                break;
            case R.id.open:
                gotoActivity("https://www.baidu.com","");
                break;*/
        }
    }
    @Override
    public void headerChange(String title) {
    }

    @Override
    public String _needForOpenApi() {
        String needForOpenApi = PreferenceUtils.getString(SkipWebActivity.this, PreferenceUtils.LABEL_WEBVIEW_JSON,"");
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
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            boolean isLock = jsonObject.getBoolean("isLock");
            if(isLock){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*public class JsInteration {
        @JavascriptInterface
        public WebviewUtil require(String str) {
            WebviewUtil webviewUtil = new WebviewUtil(SkipWebActivity.this,SkipWebActivity.this);
            return webviewUtil;
        }
    }*/
    /*public class JsInteration {
        @JavascriptInterface
        public Object require(String str) {
            Log.i("getheight","SkipWebActivity2str---------"+str+"----------");
            Object object = null;
            switch (str){
                case "webview":
                    WebviewUtil webviewUtil = new WebviewUtil(SkipWebActivity.this,SkipWebActivity.this);
                    object = webviewUtil;
                    //return webviewUtil.getClass();
                    break;
                case "Scanner":
                    Scanner scanner = new Scanner(SkipWebActivity.this,SkipWebActivity.this);
                    object = scanner;
                    //return scanner.getClass();
                    break;
                case "BaiduMap":
                    BaiduMap baiduMap = new BaiduMap(SkipWebActivity.this,SkipWebActivity.this);
                    object = baiduMap;
                    //return baiduMap.getClass();
                    break;
            }
            Log.i("getheight","SkipWebActivity2object---------"+object+"----------");
            return object;
        }
    }*/
    public class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

}
