package com.uyun.hummer.httputils;


import android.content.Context;

import com.google.gson.Gson;
import com.uyun.hummer.httpinterface.UserInterface;
import com.uyun.hummer.model.bean.CameraDetailInfo;
import com.uyun.hummer.model.bean.CameraLayoutInfo;
import com.uyun.hummer.model.bean.CameraSaveInfo;
import com.uyun.hummer.model.bean.LogoutInfo;
import com.uyun.hummer.model.bean.PwdTypeInfo;
import com.uyun.hummer.model.bean.UserBean;
import com.uyun.hummer.model.bean.UserDetailsInfo;
import com.uyun.hummer.model.bean.VerifyInfo;
import com.uyun.hummer.model.bean.VersionUrlInfo;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.PreferenceUtils;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Liyun on 2017/3/17.
 */

public class UserHttpMethods extends BaseHttpMethod{
    private Retrofit retrofit_user;
    private UserInterface mUserInterface;
    private static UserHttpMethods instance;
    private OkHttpClient httpClient;
    public static UserHttpMethods getInstance(Context context){
        if(instance==null){
            synchronized(UserHttpMethods.class){
                instance = new UserHttpMethods(context);
            }
        }
        return instance;
    }
    public static void releaseMethod(){
        instance = null;
    }
    public UserHttpMethods(Context context){
        Globe.SERVER_HOST = PreferenceUtils.getString(context, PreferenceUtils.SERVICE_IP, Globe.SERVER_HOST);
        httpClient = getHttpClientBuilder(context).build();
        retrofit_user = getOkhttpRetrofit(Globe.SERVER_HOST,httpClient);
        mUserInterface = retrofit_user.create(UserInterface.class);
    }
    public void getRealHost(Subscriber<Response<ResponseBody>> subscriber){
        mUserInterface.getRealHost()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void getPwdType(Subscriber<PwdTypeInfo> subscriber){
        mUserInterface.getPwdType()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void verifyUser(Subscriber<VerifyInfo> subscriber){
        mUserInterface.verifyUser()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void loginUser(Subscriber<UserBean> subscriber, String name, String passwd,String authCode) {
        loginUser(subscriber,name,passwd,null,authCode);
    }
    public void loginUser(Subscriber<UserBean> subscriber, String name, String passwd,String code,String authCode) {
        HashMap<String,String> paramsMap=new HashMap<>();
        if(code != null) {
            paramsMap.put("code",code);
        }
        paramsMap.put("email",name);
        paramsMap.put("passwd",passwd);
        paramsMap.put("authCode",authCode);
        Gson gson=new Gson();
        String strEntity = gson.toJson(paramsMap);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
        mUserInterface.login(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void saveCamera(Subscriber<CameraSaveInfo> subscriber, String key, String data) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),data);
        mUserInterface.saveCamera(key,body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    /*获取用户信息*/
    public void getUserInfo(Subscriber<UserDetailsInfo> subscriber, String userid){
        mUserInterface.getUserInfo(String.valueOf(System.currentTimeMillis()),userid)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    public void getCameraLayout(Subscriber<CameraLayoutInfo> subscriber,String apikey, String lnt, String lat){
        mUserInterface.getCameraLayout(apikey,lnt,lat)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getCameraDetail(Subscriber<CameraDetailInfo> subscriber, String key,String jd, String wd){
        mUserInterface.getCameraDetail(key,jd,wd)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void getCameraName(Subscriber<CameraDetailInfo> subscriber, String key, String name){
        mUserInterface.getCameraName(key,name)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /*退出登录*/
    public void logoutUser(Subscriber<LogoutInfo> subscriber){
        mUserInterface.logout()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void getVersion(Subscriber<VersionUrlInfo> subscriber){
        mUserInterface.getVersion()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void cancelAllUrl(){
        if(httpClient != null) {
            httpClient.dispatcher().cancelAll();
        }
    }
    public void downloadAudioFile(Subscriber<Response<ResponseBody>> subscriber, String downloadUrl) {
        mUserInterface.downloadAudioFile(downloadUrl)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
