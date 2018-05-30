package com.uyun.hummer.javaScriptInterface;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uyun.hummer.MainActivity;
import com.uyun.hummer.R;
import com.uyun.hummer.ViewInterface.IActivityViewInterface;
import com.uyun.hummer.ViewInterface.IBaseInterface;
import com.uyun.hummer.ViewInterface.IMsgFragmentViewInterface;
import com.uyun.hummer.ViewInterface.INeedForOpenApi;
import com.uyun.hummer.ViewInterface.IOtherWebViewInterface;
import com.uyun.hummer.ViewInterface.IPhotoInterface;
import com.uyun.hummer.activity.PoiSearchDemo;
import com.uyun.hummer.activity.PrintActivity;
import com.uyun.hummer.activity.SkipWebActivity;
import com.uyun.hummer.model.bean.PageParamInfo;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.view.CustomPrograssDialog;
import com.uyun.hummer.view.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by Liyun on 2017/12/15.
 */

public class AllJavaScriptInterface {
    public Context mContext;

    private IBaseInterface iBaseInterface;

    public AllJavaScriptInterface(Context context,IBaseInterface iBaseInterface) {
        this.iBaseInterface = iBaseInterface;
        this.mContext = context;
    }
    @JavascriptInterface
    public  void openScan(){
       ((IMsgFragmentViewInterface)iBaseInterface).openScan();
    }
    @JavascriptInterface
    public void addMediaRecordData(final String chatUserID){
        ((IMsgFragmentViewInterface)iBaseInterface).addMediaRecordData(chatUserID);
    }
    @JavascriptInterface
    public void sendMediaRecordData(){
        ((IMsgFragmentViewInterface)iBaseInterface).sendMediaRecordData();
    }
    @JavascriptInterface
    public void playMediaRecord(final String downloadUrl, final String recordName){
        ((IMsgFragmentViewInterface)iBaseInterface).playMediaRecord(downloadUrl,recordName);
    }
    @JavascriptInterface
    public void stopPlayMediaRecord(){
        ((IMsgFragmentViewInterface)iBaseInterface).stopPlayMediaRecord();
    }
    @JavascriptInterface
    public void cancelSendMediaRecord(){
        ((IMsgFragmentViewInterface)iBaseInterface).cancelSendMediaRecord();
    }
    @JavascriptInterface
    public void showNotify(String title, String text, long time, String url, String urlTitle, int iconNotify){
        if(iBaseInterface != null){
            ((IMsgFragmentViewInterface)iBaseInterface).showNotify(title,text,time,url,urlTitle,iconNotify);
        }
    }
    @JavascriptInterface
    public void showToast(String msg, int isCollect){
        ((IMsgFragmentViewInterface)iBaseInterface).showToast(msg,isCollect);
    }
    @JavascriptInterface
    public void closeLoad(){
        ((IMsgFragmentViewInterface)iBaseInterface).closeLoad();
    }
    @JavascriptInterface
    public void headerChange(String title){
        ((IOtherWebViewInterface)iBaseInterface).headerChange(title);
    }
    @JavascriptInterface
    public void showPopupWindow(){
        ((IPhotoInterface)iBaseInterface).showPopupWindow();
    }
    @JavascriptInterface
    public void openCamera(){
        ((IPhotoInterface)iBaseInterface).openCamera();
    }
    @JavascriptInterface
    public void openPhoto(){
        ((IPhotoInterface)iBaseInterface).openPhoto();
    }
    @JavascriptInterface
    public String _needForOpenApi(){
        return ((INeedForOpenApi)iBaseInterface)._needForOpenApi();
    }

    @JavascriptInterface
    public void back(){
        ((IActivityViewInterface)iBaseInterface).back();
    }
    @JavascriptInterface
    public void close(){
        ((IActivityViewInterface)iBaseInterface).close();
    }
    @JavascriptInterface
    public void changeMsgpage(final String url){
        ((MainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) mContext).changeMsgpage();
                Log.d("showToast", "changeMsgpage--------"+url);
                //getWebView().loadUrl("javascript:contactsGetChatInfo('" + url + "')");
                EventBus.getDefault().post(url);
            }
        });
    }

    @JavascriptInterface
    public String getLanguage() {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        Log.d("language", "language--------" + language);
        return language;
    }
    @JavascriptInterface
    public void setInput(final int isBottomUpspring) {
        ((MainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) mContext).setInput(isBottomUpspring);
            }
        });
    }
    @JavascriptInterface
    public void atMeNotify(final boolean isVisibility) {
        ((MainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) mContext).atMeNotify(isVisibility);
            }
        });
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
    public void msgNotify(final String msg){
        if(mContext != null) {
            ((MainActivity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) mContext).msgNotify(msg);
                }
            });
        }
    }
    @JavascriptInterface
    public String  getLocalData(String key){
        Log.i("chromium","getLocalData key = " + key );
        return PreferenceUtils.getString(mContext,key,null);
    }
    @JavascriptInterface
    public void setLocalData(String key,String value){
        Log.i("chromium","setLocalData key = " + key + ",value = " + value);
        PreferenceUtils.put(mContext,key,value);
    }
    @JavascriptInterface
    public void  showDialog(String msg){
        Log.d("showToast", "showToast--------"+msg);
        CustomPrograssDialog.getInstance().createLoadingDialog(mContext,msg).show();
    }

    @JavascriptInterface
    public void  closeDialog(){
        CustomPrograssDialog.getInstance().disMissDialog();
    }
    @JavascriptInterface
    public void  showToast(String msg){
        Log.d("showToast", "showToast----"+msg);
        CustomToast.showToast(mContext, R.drawable.success,msg, Toast.LENGTH_SHORT);
    }


    @JavascriptInterface
    public void  homeBack(){
        ((MainActivity)mContext).homeBack();
    }
    @JavascriptInterface
    public void  openOtherView(String pageParamJson){
        Log.d("showToast", "openOtherView--------"+pageParamJson);
        PreferenceUtils.put(mContext, PreferenceUtils.SHOWTIT_JSON, pageParamJson);
        try {
            JSONObject jsonObject = new JSONObject(pageParamJson);
            String pageParam = jsonObject.getJSONObject("pageParam").toString();
            PreferenceUtils.put(mContext, PreferenceUtils.PAGE_PARAM, pageParam);
            Log.i("getheight","pageParam-----"+pageParam);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("getheight","push-----"+pageParamJson);
        PageParamInfo pageParamInfo = new Gson().fromJson(pageParamJson, PageParamInfo.class);
        //pageParam = pageParamInfo.getPageParam().toString();

        Intent intent = new Intent(mContext, SkipWebActivity.class);
        intent.putExtra("url", pageParamInfo.getUrl());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //BaseApplication.getInstance().startActivity(intent);
        mContext.startActivity(intent);
        /*try {
            boolean isHidden = false;
            JSONObject jsonObject = new JSONObject(pageParamJson);
            String url = jsonObject.getString("url");
            String title = jsonObject.getString("title");
            if(jsonObject.has("isHidden")){
                isHidden = jsonObject.getBoolean("isHidden");
            }
            Intent intent = new Intent(mContext, OtherWebviewActivity.class);
            intent.putExtra("url", url);
            if(isHidden){
                intent.putExtra("title", "notitle_");
            }else {
                intent.putExtra("title", title);
            }
            mContext.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @JavascriptInterface
    public void openMap(String jsonData){
        ((IOtherWebViewInterface)iBaseInterface).openMap(jsonData);
    }
    @JavascriptInterface
    public void openPrint(String base64Data){
        Log.d("showToast", "openPrint--------"+base64Data);
        Intent intent = new Intent(mContext, PrintActivity.class);
        intent.putExtra("base64Data", base64Data);
        mContext.startActivity(intent);
    }
    @JavascriptInterface
    public void screenRotation(String json){
        ((IOtherWebViewInterface)iBaseInterface).screenRotation(json);
    }

    @JavascriptInterface
    public void setAppStorageItem(String key,String val){
        Log.d("6666", key+"--------存储数据--------"+val);
        //存储数据
        PreferenceUtils.put(mContext, key, val);


        int listSize = PreferenceUtils.getInt(mContext,"listSize",0);
        PreferenceUtils.put(mContext,"listSize", listSize+1);
        PreferenceUtils.put(mContext,"item_"+listSize, key);
        Log.d("6666", key+"--------存储数据--------"+listSize);
    }
    @JavascriptInterface
    public String getAppStorageItem(String key){
        //读取数据
        String val = PreferenceUtils.getString(mContext,key,"");
        Log.d("6666", key+"--------读取数据--------"+val);
        return val;
    }
    @JavascriptInterface
    public void removeAppStorageItem(String key){
        Log.d("6666", "removeAppStorageItem--------删除数据");
        //删除数据
        PreferenceUtils.remove(mContext,key);
    }
    @JavascriptInterface
    public void clearAppStorage(){
        //清空数据

        int listSize = PreferenceUtils.getInt(mContext,"listSize",0);
        for (int i = 0; i < listSize; i++){
            String str = PreferenceUtils.getString(mContext,"item_"+i, "");
            PreferenceUtils.remove(mContext,"item_"+i);
            PreferenceUtils.remove(mContext,str);
            Log.d("6666", "clearAppStorage--------清空数据"+str);
            Log.d("6666", "clearAppStorage--------清空数据"+i);
        }
        PreferenceUtils.remove(mContext,"listSize");
    }

    @JavascriptInterface
    public void lanzhou03(){
        Intent intent = new Intent(mContext, PoiSearchDemo.class);
        mContext.startActivity(intent);
    }
}
