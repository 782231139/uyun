package com.uyun.hummer.utils.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.uyun.hummer.MainActivity;
import com.uyun.hummer.activity.OtherWebviewActivity;
import com.uyun.hummer.activity.SkipWebActivity;
import com.uyun.hummer.activity.WebActivity;
import com.uyun.hummer.model.bean.PageParamInfo;
import com.uyun.hummer.utils.PreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhu on 2018/1/6.
 */

public class WebviewUtil {
    private PageParamInfo pageParamInfo;
    private Context mContext;
    private Activity mActivity;
    private String pageParam;
    private String subscribeName;
    private String unsubscribeName;

    public WebviewUtil(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
    }
    @JavascriptInterface
    public String getheight(){
        Log.i("getheight","getheight----");
        return  "160";
    }
    @JavascriptInterface
    public void isShowTitle(final boolean isshow){

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("getheight","isShowTitle-----isShowTitle");
                if(mActivity instanceof WebActivity){
                    ((WebActivity) mActivity).showTitle(isshow);
                }else if(mActivity instanceof SkipWebActivity){
                    ((SkipWebActivity) mActivity).showTitle(isshow);
                }
            }
        });
    }
    @JavascriptInterface
    public void setTitle(final String tit){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("getheight","setTitle-----setTitle");
                if(mActivity instanceof WebActivity){
                    ((WebActivity) mActivity).setTitle(tit);
                }else if(mActivity instanceof SkipWebActivity){
                    ((SkipWebActivity) mActivity).setTitle(tit);
                }
            }
        });
    }

    //关闭当前window
    @JavascriptInterface
    public void pop(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("getheight","pop-----pop");
                if(mActivity instanceof WebActivity){
                    ((WebActivity) mActivity).webviewBack();
                }else if(mActivity instanceof SkipWebActivity){
                    ((SkipWebActivity) mActivity).webviewBack();
                }
            }
        });

    }

    //横竖屏
    @JavascriptInterface
    public void setOrientation(final int orientation){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("getheight","setOrientation-----------------"+orientation);
                if(mActivity instanceof WebActivity){
                    ((WebActivity) mActivity).setOrientation(orientation);
                }else if(mActivity instanceof SkipWebActivity){
                    ((SkipWebActivity) mActivity).setOrientation(orientation);
                }
            }
        });

    }

    /*@JavascriptInterface
    public void setScrollViewHeight(final int height){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("getheight","setScrollViewHeight-----------------"+height);
                if(mActivity instanceof WebActivity){
                    ((WebActivity) mActivity).setScrollViewHeight(height*4);
                }else if(mActivity instanceof SkipWebActivity){
                    ((SkipWebActivity) mActivity).setScrollViewHeight(height*4);
                }
            }
        });

    }*/

    //回到跟webview
    @JavascriptInterface
    public void popRoot(){
        /*Log.i("getheight","popRoot-----popRoot");
        mActivity.setResult(WebActivity.RESULT_CLOSE);
        mActivity.finish();*/

        //销毁所有的Activity
        Intent intent = new Intent();
        intent.setAction("com.andly.bro");
        mActivity.sendBroadcast(intent);
    }

    //获取页面间传递的参数
    @JavascriptInterface
    public String getPageParam(){
        pageParam = PreferenceUtils.getString(mActivity,PreferenceUtils.PAGE_PARAM,"");
        Log.i("getheight","getPageParam-----"+pageParam);
        return pageParam;
        //return  "";
    }
    @JavascriptInterface
    public void push(String pageParamJson){
        try {
            boolean isHidden = false;
            JSONObject jsonObject = new JSONObject(pageParamJson);
            String url = jsonObject.getString("url");

            if(jsonObject.has("isHidden")){
                isHidden = jsonObject.getBoolean("isHidden");
            }
            Intent intent = new Intent(mContext, OtherWebviewActivity.class);
            intent.putExtra("url", url);
            if(isHidden){
                intent.putExtra("title", "notitle_");
            }else {
                if(jsonObject.has("title")){
                    String title = jsonObject.getString("title");
                    intent.putExtra("title", title);
                }else {
                    intent.putExtra("title", "notitle_");

                }

            }
            mContext.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*PreferenceUtils.put(mActivity, PreferenceUtils.SHOWTIT_JSON, pageParamJson);
        try {
            JSONObject jsonObject = new JSONObject(pageParamJson);
            pageParam = jsonObject.getJSONObject("pageParam").toString();
            PreferenceUtils.put(mActivity, PreferenceUtils.PAGE_PARAM, pageParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("getheight","push-----"+pageParamJson);
        pageParamInfo = new Gson().fromJson(pageParamJson, PageParamInfo.class);
        //pageParam = pageParamInfo.getPageParam().toString();
        Log.i("getheight","pageParam-----"+pageParam);
        Intent intent = new Intent(mActivity, SkipWebActivity.class);
        intent.putExtra("url", pageParamInfo.getUrl());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //BaseApplication.getInstance().startActivity(intent);
        mActivity.startActivity(intent);*/
    }

    //取消消息订阅
    @JavascriptInterface
    public void unsubscribe(String name){
        //webView.loadUrl("javascript:nativeload()");
    }
    //消息订阅
    @JavascriptInterface
    public void subscribe(String name){
        //webView.loadUrl("javascript:nativeload()");
    }
    //窗口消息发布
    @JavascriptInterface
    public void publish(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            pageParam = jsonObject.getJSONObject("pageParam").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("getheight","publish---------"+json+"----------");
        PreferenceUtils.put(mActivity, PreferenceUtils.PUBLISH_NAME, json);
        //PreferenceUtils.put(mActivity, PreferenceUtils.PUBLISH_MESSAGE, message);
        Intent intent = new Intent();
        intent.setAction("publish");
        mActivity.sendBroadcast(intent);
    }
    @JavascriptInterface
    public void showBottom(final boolean isVisibility) {
        ((MainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) mContext).showTablayout(isVisibility);
            }
        });
    }

    @JavascriptInterface
    public void setEnablePullDownRefresh(final boolean isRefresh){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("getheight","setEnablePullDownRefresh");
                if(mActivity instanceof SkipWebActivity){
                    ((SkipWebActivity) mActivity).setEnablePullDownRefresh(isRefresh);
                }
            }
        });
    }

    @JavascriptInterface
    public void stopPullDownRefresh(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("getheight","stopPullDownRefresh");
                if(mActivity instanceof SkipWebActivity){
                    ((SkipWebActivity) mActivity).stopPullDownRefresh();
                }
            }
        });
    }

    @JavascriptInterface
    public void startPullDownRefresh(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("getheight","starPullDownRefresh");
                if(mActivity instanceof SkipWebActivity){
                    ((SkipWebActivity) mActivity).startPullDownRefresh();
                }
            }
        });
    }
}
