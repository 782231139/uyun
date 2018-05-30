package com.uyun.hummer.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.uyun.hummer.MainActivity;
import com.uyun.hummer.R;
import com.uyun.hummer.ViewInterface.IMsgFragmentViewInterface;
import com.uyun.hummer.ViewInterface.IPhotoInterface;
import com.uyun.hummer.activity.LoadingActivity;
import com.uyun.hummer.base.fragment.BaseFragment;
import com.uyun.hummer.httputils.CookieManager;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.javaScriptInterface.AllJavaScriptInterface;
import com.uyun.hummer.model.bean.UserBean;
import com.uyun.hummer.photo.MultiImageSelectorActivity;
import com.uyun.hummer.utils.ExceptionHandle;
import com.uyun.hummer.utils.MediaUtils;
import com.uyun.hummer.utils.SystemUtils;
import com.uyun.hummer.utils.WebViewUtils;
import com.uyun.hummer.view.CustomPrograssDialog;
import com.uyun.hummer.view.CustomToast;
import com.uyun.hummer.view.ImageTools;
import com.uyun.hummer.view.TakePhotoPopWin;
import com.uyun.hummer.zxing.ScannerActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Subscriber;

/**
 * Created by Liyun on 2017/3/15.
 */

public class MsgFragment extends BaseFragment implements SensorEventListener,IMsgFragmentViewInterface,IPhotoInterface{
    private String url = null;
    private static String INDEXFILE = "/index.html";
    private boolean isload = false;
    private TimerTask task;
    private Timer timer;

    private static final int OPEN_CAMERA = 0;
    private static final int OPEN_PHOTO = 1;
    private TakePhotoPopWin takePhotoPopWin;
    private static String IMAGE_FILE_NAME = System.currentTimeMillis() + ".jpg";
    private ArrayList<String> mSelectPath;
    private FileUtilsMethods mFileUtilsMethods;
    private File mTmpFile;
    private Uri uri;
    private int SERVICE_ERROR = 0;
    private int SERVICE_SUCCESS = 1;
    private MediaUtils mMediaUtils;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private AudioManager audioManager;
    private boolean isInitLabel = true;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFileUtilsMethods = new FileUtilsMethods(getActivity());
        mMediaUtils = new MediaUtils(getActivity());
        url = SystemUtils.getUrlWithName(INDEXFILE, realHost);
        initWebView();
        Intent intent = getActivity().getIntent();
        isload = intent.getBooleanExtra("isload", false);
        if (isload) {
            startActivity(new Intent(getActivity(), LoadingActivity.class));
        }
        showMyDialog(getString(R.string.loading));
        EventBus.getDefault().register(this);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        getPermission();

    }
    public void networkChange(){
        if(getWebView()!=null){
            Log.i("yunli","NetworkChanged ======== _manualReconnect");
            getWebView().loadUrl("javascript:_manualReconnect()");
        }
    }
    private void getPermission(){
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
        };
        List<String> mPermissionList = new ArrayList<>();
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(getActivity(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (!mPermissionList.isEmpty()) {
            permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 100);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BaseFragment.isClickNotify) {
            getWebView().loadUrl("javascript:_gotoMessage()");
            BaseFragment.isClickNotify = false;
        }
        CookieManager.getInstance(getActivity()).syncCookie(url);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshCookiesForView(UserBean bean) {
        CookieManager.getInstance(getActivity()).syncCookie(url);
        getWebView().loadUrl(url);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void changeMsgpage(String url) {
        getWebView().loadUrl("javascript:contactsGetChatInfo('" + url + "')");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mSensorManager.unregisterListener(this);
    }

    private void initWebView() {
        getWebView().addJavascriptInterface(new AllJavaScriptInterface(getActivity(),this), "Android");
        getWebView().setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根
                Log.i("yunli","========"+newProgress);
                if(newProgress==100){
                    if(isInitLabel){
                        TimerTask task = new TimerTask() {
                            public void run() {
                                Log.i("yunli","========newProgress==100");
                                if(getActivity() == null){
                                    isInitLabel = true;
                                }else {
                                    isInitLabel = false;
                                    EventBus.getDefault().post(getActivity());
                                }
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(task, 10000);
                    }
                }
            }
        });
        getWebView().setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                CustomPrograssDialog.getInstance().disMissDialog();
                if (isload) {
                    task = new TimerTask() {
                        public void run() {
                            LoadingActivity.my.finish();
                            isload = false;
                            task.cancel();
                        }
                    };
                    timer = new Timer();
                    timer.schedule(task, 4000);
                }
                if (!getWebView().getSettings().getLoadsImagesAutomatically()) {
                    getWebView().getSettings().setLoadsImagesAutomatically(true);
                }
                Log.i("yunli","========onPageFinished");
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse response = WebViewUtils.getInstance(getContext()).shouldOverrideInterceptRequest(mFileUtilsMethod,view,request.getUrl().toString());
                if(response != null){
                    return response;
                }else {
                    return super.shouldInterceptRequest(view, request);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse response = WebViewUtils.getInstance(getContext()).shouldOverrideInterceptRequest(mFileUtilsMethod,view,url);
                if(response != null){
                    return response;
                }else {
                    return super.shouldInterceptRequest(view, url);
                }
            }

        });
        setText(getString(R.string.message));
        CookieManager.getInstance(getActivity()).syncCookie(url);
        getWebView().loadUrl(url);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFileUtilsMethods != null) {
            mFileUtilsMethods.cancelAllUrl();
            mFileUtilsMethods = null;
        }
        if(getWebView()!=null){
            WebViewUtils.destroy(getWebView());
        }
    }


    private void takePicture() {
        mTmpFile = mMediaUtils.getFileInNative(String.valueOf(System.currentTimeMillis()) + ".jpg");
        Intent intent = ImageTools.takePicture(getActivity(),mTmpFile);
        startActivityForResult(intent, OPEN_CAMERA);
    }
    public void choosePicture() {
        Intent intent = ImageTools.choosePicture(getActivity());
        startActivityForResult(intent, OPEN_PHOTO);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.openCamera:
                    takePhotoPopWin.dismiss();
                    takePicture();
                    break;
                case R.id.openPhoto:
                    takePhotoPopWin.dismiss();
                    choosePicture();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case OPEN_CAMERA:
                    showMyDialog(getString(R.string.upload_ing));
                    Bitmap bitmap = BitmapFactory.decodeFile(mTmpFile.toString());
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
                    String fileName2 = String.valueOf(System.currentTimeMillis());
                    ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), fileName2);
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName2 + ".png");
                    uploadMultiFile(file.toString());
                    break;

                case OPEN_PHOTO:
                    showMyDialog(getString(R.string.upload_ing));
                    if (resultCode == getActivity().RESULT_OK) {
                        mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                        for (String p : mSelectPath) {
                            uploadMultiFile(ImageTools.getFile(p));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void showMyDialog(String msg) {
        CustomPrograssDialog.getInstance().createLoadingDialog(getActivity(), msg).show();
    }

    private void uploadMultiFile(String imgUrl) {

        Log.i("uploadMultiFile", "uploadMultiFile---" + imgUrl);
        Subscriber<Response<ResponseBody>> subscriber = new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ExceptionHandle.ResponeThrowable throwable = null;
                if (e instanceof Exception) {
                    throwable = ExceptionHandle.handleException(e);
                } else {
                    throwable = new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN);
                }
                CustomToast.showToast(getActivity(), R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
            }

            @Override
            public void onNext(Response<ResponseBody> responseBodyResponse) {
                try {
                    getWebView().loadUrl("javascript:send('" + "" + "','" + responseBodyResponse.body().string() + "')");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mFileUtilsMethods.uploadMultiFile(subscriber, imgUrl);
    }

    private void uploadAudioFile() {
        Subscriber<Response<ResponseBody>> subscriber = new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                getWebView().loadUrl("javascript:_H5GetVoiceInfo('" + "" + "' , '" + "" + "','" + SERVICE_ERROR + "')");
                ExceptionHandle.ResponeThrowable throwable = null;
                if (e instanceof Exception) {
                    throwable = ExceptionHandle.handleException(e);
                } else {
                    throwable = new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN);
                }
                CustomToast.showToast(getActivity(), R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
            }

            @Override
            public void onNext(Response<ResponseBody> responseBodyResponse) {
                try {
                    String serviceData = responseBodyResponse.body().string();
                    Log.i("uploadMultiFile", "serviceData==" + serviceData);
                    getWebView().loadUrl("javascript:_H5GetVoiceInfo('" + serviceData + "' , '" + mMediaUtils.getRecordFileTime() + "','" + SERVICE_SUCCESS + "')");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("uploadMultiFile", "e==" + e);
                    getWebView().loadUrl("javascript:_H5GetVoiceInfo('" + "" + "' , '" + "" + "','" + SERVICE_ERROR + "')");
                }
            }
        };
        mFileUtilsMethods.uploadAudioFile(subscriber, mMediaUtils.getRecordFile().toString());
    }

    private void downloadAudioFile(String downloadUrl, final String recordName) {
        final File file = mMediaUtils.getFileInNative(recordName);
        getWebView().loadUrl("javascript:_H5StartPlayVoice('" + recordName + "')");
        if(file != null&&file.length()>0){
            mMediaUtils.startMediaPlay(file ,audioManager);
            return;
        }

        Subscriber<Response<ResponseBody>> subscriber = new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                ExceptionHandle.ResponeThrowable throwable = null;
                if (e instanceof Exception) {
                    throwable = ExceptionHandle.handleException(e);
                } else {
                    throwable = new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN);
                }
                CustomToast.showToast(getActivity(), R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
            }

            @Override
            public void onNext(Response<ResponseBody> responseBodyResponse) {
                try {
                    Log.i("uploadMultiFile", "recordName==" + recordName);
                    BufferedInputStream bufis = new BufferedInputStream(responseBodyResponse.body().byteStream());
                    BufferedOutputStream bufos = new BufferedOutputStream(new FileOutputStream(file));
                    int count;
                    byte[] by = new byte[4096];
                    while ((count = bufis.read(by)) != -1) {
                        bufos.write(by, 0, count);
                    }
                    bufos.flush();
                    bufos.close();
                    bufis.close();
                    mMediaUtils.startMediaPlay(file ,audioManager);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("uploadMultiFile", "e==" + e);
                }
            }
        };
        mFileUtilsMethods.downloadAudioFile(subscriber, downloadUrl);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if (range == mSensor.getMaximumRange()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
        } else {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }else {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void scanQRcode() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getActivity(), ScannerActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void showPopupWindow() {
        takePhotoPopWin = new TakePhotoPopWin(getActivity(), onClickListener);

        takePhotoPopWin.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void openPhoto() {
        choosePicture();
    }

    @Override
    public void openCamera() {
        takePicture();
    }

    @Override
    public void openScan() {
        scanQRcode();
    }

    @Override
    public void addMediaRecordData(final String chatUserID) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMediaUtils.startMediaRecord(chatUserID);
                }
            });
        }
    }

    @Override
    public void sendMediaRecordData() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMediaUtils.stopMediaRecord();
                    uploadAudioFile();
                }
            });
        }
    }

    @Override
    public void playMediaRecord(final String downloadUrl, final String recordName) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    downloadAudioFile(downloadUrl, recordName);
                }
            });
        }
    }

    @Override
    public void stopPlayMediaRecord() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMediaUtils.stopPlayMedia();
                }
            });
        }
    }

    @Override
    public void cancelSendMediaRecord() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMediaUtils.cancelSendMedia();
                }
            });
        }
    }

    @Override
    public void showNotify(String title, String text, long time, String url, String urlTitle, int iconNotify) {
        if (SystemUtils.isAppToBackground(getActivity())) {
            showNotifyBase(title, text, time, url, urlTitle, iconNotify);
        }
    }

    @Override
    public void showToast(String msg, int isCollect) {
        Log.d("showToast", "showToast----" + msg);
        CustomToast.showToast(getActivity(), R.drawable.success, msg, Toast.LENGTH_SHORT);
        ((MainActivity) getActivity()).setIsCollect(isCollect);
    }

    @Override
    public void closeLoad() {
        if (isload) {
            Log.i("yunli", "closeLoad");
            LoadingActivity.my.finish();
            isload = false;
        }
    }
}
