package com.uyun.hummer.httputils;

import android.content.Context;

import com.uyun.hummer.httpinterface.ChatOpsInterface;
import com.uyun.hummer.model.bean.CodeInfo;
import com.uyun.hummer.model.bean.LabelInfo;
import com.uyun.hummer.model.bean.LabelUpdateResultInfo;
import com.uyun.hummer.model.bean.LabelWebviewKeyInfo;
import com.uyun.hummer.model.bean.VersionInfo;
import com.uyun.hummer.model.bean.VerticalInfo;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.PreferenceUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Liyun on 2017/4/25.
 */

public class FileUtilsMethods extends BaseHttpMethod {
    private Retrofit retrofit_user;
    private ChatOpsInterface mFileInterface;
    private OkHttpClient httpClient;
    public FileUtilsMethods(Context context){
        String realHost = PreferenceUtils.getString(context, Globe.SERVER_HOST, Globe.SERVER_HOST);
        initMethods(context,realHost);
    }
    public FileUtilsMethods(Context context,String host){
        initMethods(context,host);
    }
    public void initMethods(Context context,String host) {
        httpClient = getHttpClientBuilder(context).build();
        retrofit_user = getOkhttpRetrofit(host,httpClient);
        mFileInterface = retrofit_user.create(ChatOpsInterface.class);
    }

    public void uploadMultiFile(Subscriber<Response<ResponseBody>> subscriber, String imgUrl) {
        File file = new File(imgUrl);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestBody);
        mFileInterface.uploadFile(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void uploadAudioFile(Subscriber<Response<ResponseBody>> subscriber, String audioUrl) {
        File file = new File(audioUrl);
        RequestBody requestBody = RequestBody.create(MediaType.parse("audio/mpeg"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("audio", file.getName(), requestBody);
        mFileInterface.uploadAudioFile(body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void downloadAudioFile(Subscriber<Response<ResponseBody>> subscriber, String downloadUrl) {
        mFileInterface.downloadAudioFile(downloadUrl)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void downloadImageFile(Subscriber<VerticalInfo> subscriber) {
        mFileInterface.getImageFile()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void getLabelTotalData(Subscriber<LabelInfo> subscriber){
        mFileInterface.getLabelTotalData()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getLabelWebviewKey(Subscriber<LabelWebviewKeyInfo> subscriber){
        mFileInterface.getLabelWebviewKey()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void updateLabelData(Subscriber<LabelUpdateResultInfo> subscriber,RequestBody requestBody){
        mFileInterface.updateLabelData(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getCode(Subscriber<CodeInfo> subscriber) {
        mFileInterface.getCodeSwitch()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void downloadImageFile(Subscriber<Response<ResponseBody>> subscriber, String url) {
        mFileInterface.getImageFile(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void getVersion(Subscriber<VersionInfo> subscriber){
        mFileInterface.getVersion()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void cancelAllUrl() {
        if (httpClient != null) {
            httpClient.dispatcher().cancelAll();
        }
    }

}