package com.uyun.hummer.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uyun.hummer.R;
import com.uyun.hummer.ViewInterface.IActivityViewInterface;
import com.uyun.hummer.ViewInterface.INeedForOpenApi;
import com.uyun.hummer.ViewInterface.IOtherWebViewInterface;
import com.uyun.hummer.ViewInterface.IPhotoInterface;
import com.uyun.hummer.base.activity.BaseFragmentActivity;
import com.uyun.hummer.base.view.BaseWebView;
import com.uyun.hummer.httputils.CookieManager;
import com.uyun.hummer.javaScriptInterface.AllJavaScriptInterface;
import com.uyun.hummer.photo.MultiImageSelectorActivity;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.MediaUtils;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.utils.WebViewUtils;
import com.uyun.hummer.view.ImageTools;
import com.uyun.hummer.view.MyWebChromeClient;
import com.uyun.hummer.view.TakePhotoPopWin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by zhu on 2017/7/27.
 */

public class OtherWebviewActivity extends BaseFragmentActivity implements IOtherWebViewInterface,INeedForOpenApi,IPhotoInterface,IActivityViewInterface{
    private String title;
    private String url;
    private BaseWebView mWebView;
    private TextView tit_text;
    private LinearLayout back;
    private ArrayList<String> mSelectPath;

    private MediaUtils mMediaUtils;
    private static final int OPEN_CAMERA = 0;
    private static final int OPEN_PHOTO = 1;
    private static final int OPEN_MAP = 2;
    private File mTmpFile;
    private Uri uri;
    private String fileName;
    private long fileSize;
    private File mFile;
    private TakePhotoPopWin takePhotoPopWin;
    private LinearLayout scan_layout;
    private TextView scan_text;
    private String IS_SCAN = "is_scan_result";
    private MyWebChromeClient mMyWebChromeClient;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private String injectionJS;
    private boolean isInjection = false;
    private RelativeLayout tit;
    private RelativeLayout relativeLayout;
	private Intent reIntent;
    private TextView close_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        getInjectionJS();
        mMediaUtils = new MediaUtils(this);
        tit = (RelativeLayout) findViewById(R.id.title);
        //relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        tit_text = (TextView) findViewById(R.id.tit_text);

        if(title.equals("notitle_")){
            tit.setVisibility(View.GONE);
            //relativeLayout.setVisibility(View.GONE);
        }else {
            tit.setVisibility(View.VISIBLE);
            //relativeLayout.setVisibility(View.VISIBLE);
        }
        if(!title.equals(IS_SCAN)){
            if(title.length()<=20){
                tit_text.setText(title);
            }else {
                title = title.substring(0,19)+"...";
                tit_text.setText(title);
            }
        }

        close_text= (TextView) findViewById(R.id.close_text);
        close_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        back = (LinearLayout) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mWebView.canGoBack()) {
                    mWebView.goBack();
                }else {
                    sendBroadcast(reIntent);
                    finish();
                }
            }
        });
        mWebView = (BaseWebView) findViewById(R.id.son_web);
        mWebView.initWebView();
        mWebView.clearCache(true);
        mWebView.addJavascriptInterface(new AllJavaScriptInterface(this,this), "Android");
        scan_layout = (LinearLayout)findViewById(R.id.scan_layout);
        scan_text = (TextView) findViewById(R.id.scan_text);



        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                Log.i("onReceivedError","onPageFinished cookies other= " + view.getTitle());
                if(!isInjection){
                    mWebView.loadUrl("javascript:" + injectionJS);
                    Log.i("onReceivedError","@@@@@@@@"+injectionJS);
                    //isInjection = true;
                }
                if(title.equals(IS_SCAN)){
                    title = view.getTitle();
                    if(title.length()<=20){
                        tit_text.setText(title);
                    }else {
                        title = title.substring(0,19)+"...";
                        tit_text.setText(title);
                    }
                    title = IS_SCAN;
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.i("onReceivedError", "onReceivedError: ------->errorCode" + errorCode + ":" + description);
            }
        });

        /*mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.i("onReceivedError", "onReceivedError: -------onReceivedTitle"+title);
                if (title.contains("404")){
                    Log.i("onReceivedError", "onReceivedError: -------404");
                }
            }
        });*/
        mMyWebChromeClient = new MyWebChromeClient(OtherWebviewActivity.this);
        //mWebView.setWebChromeClient(mMyWebChromeClient);
        Intent sendIntent = new  Intent();
        sendIntent.setAction(Globe.MESSAGE_UYUNJS);
        sendBroadcast(sendIntent);

        reIntent = new  Intent();
        reIntent.setAction(Globe.MESSAGE_UYUNJS_REFRESHJS);
        //mWebView.loadUrl("http://10.1.11.6:8080/guide/");
        String string = url.substring(0,1);
        if(string.equals("/")){
            scan_layout.setVisibility(View.GONE);
            CookieManager.getInstance(OtherWebviewActivity.this).syncCookie(Globe.SERVER_HOST+url);
            mWebView.loadUrl(Globe.SERVER_HOST+url);
        }else if(url.substring(0,4).equals("http")) {
            scan_layout.setVisibility(View.GONE);
            CookieManager.getInstance(OtherWebviewActivity.this).syncCookie(url);
            mWebView.loadUrl(url);
        }else {
            scan_layout.setVisibility(View.VISIBLE);
            scan_text.setText(url);
            tit_text.setText(R.string.scan_result);
        }
    }
    private void getInjectionJS(){
        try {
            /*URL url = new URL("file:///android_asset/fuck.js");
            InputStream in = url.openStream();*/
            InputStream in = getClass().getResourceAsStream("/assets/mobile/bridge.js");
            byte buff[] = new byte[1024];
            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
            //FileOutputStream out = null;
            do {
                int numread = in.read(buff);
                if (numread <= 0) {
                    break;
                }
                fromFile.write(buff, 0, numread);
            } while (true);
            injectionJS = fromFile.toString();
            //Log.e("currentapiVersion", "wholeJS====>" + wholeJS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }else {
                sendBroadcast(reIntent);
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_CHOOSER_RESULT_CODE:
                mMyWebChromeClient.onActivityResult(requestCode,resultCode,data);
                break;
        }
        if (resultCode == this.RESULT_OK) {
            switch (requestCode) {
                case OPEN_CAMERA:
                    Bitmap bitmap = BitmapFactory.decodeFile(mTmpFile.toString());
                    if (bitmap == null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
                    String fileName2 = String.valueOf(System.currentTimeMillis());
                    ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), fileName2);
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName2 + ".png");
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        fileSize = (long)fis.available();
                        fileName = file.getName();
                        Log.i("mSelectPath", "path-----" + file.toString());
                        Log.i("mSelectPath", "fileName-----" + fileName);
                        Log.i("mSelectPath", "fileSize-----" + fileSize);
                        mWebView.loadUrl("javascript:_mobile.album.transImgData('" + ImageTools.getImageDataJson(file.toString(),fileSize,fileName) + "')");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case OPEN_PHOTO:
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    StringBuilder sb = new StringBuilder();
                    for (String path : mSelectPath) {
                        try {
                            mFile = new File(path);
                            fileName = mFile.getName();
                            FileInputStream fis = new FileInputStream(mFile);
                            fileSize = (long)fis.available();
                            Log.i("mSelectPath", "path-----" + path);
                            Log.i("mSelectPath", "fileName-----" + fileName);
                            Log.i("mSelectPath", "fileSize-----" + fileSize);
                            mWebView.loadUrl("javascript:_mobile.album.transImgData('" + ImageTools.getImageDataJson(path,fileSize,fileName) + "')");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case OPEN_MAP:
                    
                    String mapJson = data.getStringExtra("mapJson");
                    Log.i("mSelectPath", "path-----" + mapJson);
                    mWebView.loadUrl("javascript:_mobile.map.transLocationData('" + mapJson + "')");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebViewUtils.destroy(mWebView);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.openCamera:
                    takePhotoPopWin.dismiss();
                    openCamera();
                    break;
                case R.id.openPhoto:
                    takePhotoPopWin.dismiss();
                    openPhoto();
                    break;
            }
        }
    };
    @Override
    public void headerChange(String title) {
        tit_text.setText(title);
    }

    @Override
    public String _needForOpenApi() {
        String needForOpenApi = PreferenceUtils.getString(OtherWebviewActivity.this,PreferenceUtils.LABEL_WEBVIEW_JSON,"");
        return needForOpenApi;
    }

    @Override
    public void showPopupWindow() {
        takePhotoPopWin = new TakePhotoPopWin(OtherWebviewActivity.this, onClickListener);

        takePhotoPopWin.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void openPhoto() {
         
		Intent intent = ImageTools.choosePicture(this);
        startActivityForResult(intent, OPEN_PHOTO);
    }

    @Override
    public void openCamera() {
        
		mTmpFile = mMediaUtils.getFileInNative(String.valueOf(System.currentTimeMillis()) + ".jpg");
        Intent intent = ImageTools.takePicture(this,mTmpFile);
        startActivityForResult(intent, OPEN_CAMERA);
    }

    @Override
    public void back() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        }else {
            sendBroadcast(reIntent);
            finish();
        }
    }

    @Override
    public void close() {
        sendBroadcast(reIntent);
        finish();
    }

    @Override
    public void openMap(String jsonData) {
        Intent intent = new Intent(this, BaiduMapActivity.class);
        intent.putExtra("mapJson", jsonData);
        startActivityForResult(intent,OPEN_MAP);
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


}
