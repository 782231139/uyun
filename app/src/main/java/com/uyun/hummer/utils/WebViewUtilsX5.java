package com.uyun.hummer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.uyun.hummer.cache.DiskLruCache;
import com.uyun.hummer.cache.LocalBitmapCache;
import com.uyun.hummer.httputils.FileUtilsMethods;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Subscriber;

/*import android.webkit.WebResourceResponse;
import android.webkit.WebView;*/

/**
 * Created by Liyun on 2017/5/15.
 */

public class WebViewUtilsX5 {
    private static WebViewUtilsX5 instance;
    private Context mContext;
    private LocalBitmapCache mLocalBitmapCache;

    public static void destroy(WebView webview) {
        webview.stopLoading();
        webview.removeAllViews();
        webview.clearCache(true);
        webview.clearHistory();
        webview.destroy();
    }
    public static WebViewUtilsX5 getInstance(Context context){
        if(instance==null){
            synchronized(LocalBitmapCache.class){
                instance = new WebViewUtilsX5(context);
            }
        }
        return instance;
    }
    private WebViewUtilsX5(Context context){
        mContext = context;
        mLocalBitmapCache = LocalBitmapCache.getInstance(context);
    }
    public WebResourceResponse shouldOverrideInterceptRequest(FileUtilsMethods fileUtilsMethods, WebView view, String url){
        Log.i("yunli","shouldOverrideInterceptRequest url = " + url);
        Uri uri = Uri.parse(url);
        if(uri == null){
            return null;
        }
        String segment = uri.getLastPathSegment();
        String fragment = uri.getFragment();
        if(segment!= null && segment.endsWith("html")) {
            InputStream is = FileUtils.getLocalIndexStream(mContext);
            if (is != null) {
                WebResourceResponse response = new WebResourceResponse("text/html",
                        "utf-8", is);
                return response;
            }
        }
        if(segment!= null && segment.endsWith("js")){
            InputStream is =  FileUtils.getLocalStream(mContext,uri.getPath());
            if (is != null) {
                WebResourceResponse response = new WebResourceResponse("text/javascript",
                        "utf-8", is);
                return response;
            }
        }
        if(fragment!= null && fragment.startsWith("/collect")){
            InputStream is = FileUtils.getLocalIndexStream(mContext);
            if (is != null) {
                WebResourceResponse response = new WebResourceResponse("text/html",
                        "utf-8", is);
                return response;
            }
        }
        if(fragment!= null && fragment.startsWith("/contacts/home")){
            InputStream is = FileUtils.getLocalIndexStream(mContext);
            if (is != null) {
                WebResourceResponse response = new WebResourceResponse("text/html",
                        "utf-8", is);
                return response;
            }
        }
        if(fragment!= null && fragment.startsWith("/operate")){
            InputStream is = FileUtils.getLocalIndexStream(mContext);
            if (is != null) {
                WebResourceResponse response = new WebResourceResponse("text/html",
                        "utf-8", is);
                return response;
            }
        }
        if(fragment!= null && fragment.startsWith("/messageAtMe")){
            InputStream is = FileUtils.getLocalIndexStream(mContext);
            if (is != null) {
                WebResourceResponse response = new WebResourceResponse("text/html",
                        "utf-8", is);
                return response;
            }
        }
        if(fragment!= null && (fragment.contains("chatInfo/dms") ||fragment.contains("chatInfo/room"))){
            InputStream is = FileUtils.getLocalIndexStream(mContext);
            if (is != null) {
                WebResourceResponse response = new WebResourceResponse("text/html",
                        "utf-8", is);
                return response;
            }
        }
        if(segment != null && (segment.endsWith("png") || segment.endsWith("jpg") || segment.endsWith("jpeg")|| segment.endsWith("gif"))) {
            InputStream is = FileUtils.getLocalPicStream(mContext, uri.getPath());
            if (is == null) {
                String key = MD5Utils.encode(url);
                Bitmap bitmap = mLocalBitmapCache.getBitmapFromMemoryCache(url);
                if (bitmap == null) {
                    DiskLruCache.Snapshot snapshot = mLocalBitmapCache.getBitmapFromLruCache(key);
                    if (snapshot == null) {
                        downloadImages(fileUtilsMethods,url);
                        return null;
                    } else {
                        is = snapshot.getInputStream(0);
                    }
                } else {
                    is = FileUtils.BitmapToStream(bitmap);
                }
            }
            if (is != null) {
                WebResourceResponse response = new WebResourceResponse("text/javascript",
                        "utf-8", is);
                return response;
            }
        }
        return null;
    }
    public void downloadImages(FileUtilsMethods fileUtilsMethods,final String url){
        final String key = MD5Utils.encode(url);
        Subscriber<Response<ResponseBody>> subscriber = new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(Response<ResponseBody> responseBodyResponse) {
                final Response<ResponseBody> response = responseBodyResponse;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(response == null || response.body() == null){
                            return;
                        }
                        InputStream inputStream = response.body().byteStream();
                        if(inputStream == null){
                            return;
                        }
                        FileInputStream fileInputStream = null;
                        FileDescriptor fileDescriptor = null;
                        try {
                            DiskLruCache.Editor editor = mLocalBitmapCache.getEditor(key);
                            if(editor != null) {
                                OutputStream outputStream = editor.newOutputStream(0);
                                boolean res = FileUtils.inputWriteToOutputStream(inputStream,outputStream);
                                if(res){
                                    editor.commit();
                                }else{
                                    editor.abort();
                                }
                            }
                            DiskLruCache.Snapshot snapShot = mLocalBitmapCache.getBitmapFromLruCache(key);
                            if (snapShot != null) {
                                fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                                fileDescriptor = fileInputStream.getFD();
                                Bitmap bitmap = null;
                                if (fileDescriptor != null) {
                                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                                }
                                if (bitmap != null) {
                                    mLocalBitmapCache.addBitmapToMemoryCache(url, bitmap);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            if (fileDescriptor == null && fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                    }
                }).start();
            }
        };
        fileUtilsMethods.downloadImageFile(subscriber,url);
    }
}
