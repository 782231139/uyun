package com.uyun.hummer.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.uyun.hummer.R;
import com.uyun.hummer.activity.AboutActivity;
import com.uyun.hummer.activity.CollectAtmeActivity;
import com.uyun.hummer.activity.LoginActivity;
import com.uyun.hummer.activity.UserInfoActivity;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.httputils.UserHttpMethods;
import com.uyun.hummer.model.bean.CameraDetailInfo;
import com.uyun.hummer.model.bean.CameraLayoutInfo;
import com.uyun.hummer.model.bean.LogoutInfo;
import com.uyun.hummer.model.bean.UserBean;
import com.uyun.hummer.model.bean.UserDetailsInfo;
import com.uyun.hummer.model.bean.VerifyInfo;
import com.uyun.hummer.model.bean.VerticalInfo;
import com.uyun.hummer.utils.ExceptionHandle;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.NetWorkUtils;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.view.CustomToast;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import rx.Subscriber;

/**
 * Created by Liyun on 2017/3/15.
 */

public class MineFragment extends Fragment {
    private TextView tit_text;
    private ImageView imgtit;
    private TextView text_name;
    //private TextView text_company;
    private TextView text_mail;
    /*private TextView text_phone;
    private TextView text_qq;
    private TextView text_wechar;
    private TextView text_web;*/
    private RelativeLayout layoutUserinfo;
    private RelativeLayout layoutAbout,layoutMymsg,layoutCollect;
    private TextView logout;
    private boolean isload = false;
    private final Timer timer = new Timer();
    private TimerTask task;
    private Handler handle;
    private String errorMsg = null;
    private String nickname,mail,account,phone;
    private static String COLLECT = "#/collect";
    private static String MSGATME = "#/messageAtMe";
    private ImageView update_hint;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine,null);
        init(view);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         initData();
    }
    public void initData(){
        String userInfo = PreferenceUtils.getString(getActivity(), PreferenceUtils.USERINFO, "");
        if (!TextUtils.isEmpty(userInfo)) {
            UserDetailsInfo.DataBeans data = new UserDetailsInfo.DataBeans();
            data.parseStrToDataBeans(userInfo);
            setUserData(data);
        }
        if(!NetWorkUtils.isNetworkConnected(getActivity())){
            return;
        }
        Intent intent = getActivity().getIntent();
        isload = intent.getBooleanExtra("isload",false);
        if(isload){
            verifyUserData();
        }else {
            getData();
        }
        handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                verifyUserData();
            }
        };
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handle.sendMessage(message);
            }
        };
        timer.schedule(task, 60*1000*10, 60*1000*10);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            boolean versionUpdate = PreferenceUtils.getBoolean(getActivity(),PreferenceUtils.VERSION_UPDATE,false);
            if(versionUpdate){
                update_hint.setVisibility(View.VISIBLE);
            }else {
                update_hint.setVisibility(View.GONE);
            }
        }
    }

    private void init(View view){
        update_hint = (ImageView) view.findViewById(R.id.update_hint);
        tit_text = (TextView) view.findViewById(R.id.tit_text);
        tit_text.setText(R.string.mine);
        imgtit = (ImageView) view.findViewById(R.id.imgtit);
        text_name = (TextView) view.findViewById(R.id.text_name);
        text_mail = (TextView) view.findViewById(R.id.text_mail);

        logout = (TextView) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getActivity(), BaiduMapActivity.class);
                startActivity(intent);
                getCameraDetail("30.32037670600338","120.08617285576626");
                getCameraLayout("e10adc3949ba59abbe56e057f2gg88dd","115.252236","29.662584");*/
                logoutDialog();
            }
        });

        layoutMymsg = (RelativeLayout)view.findViewById(R.id.layoutMymsg);
        layoutCollect = (RelativeLayout)view.findViewById(R.id.layoutCollect);
        layoutAbout = (RelativeLayout)view.findViewById(R.id.layoutAbout);
        layoutUserinfo = (RelativeLayout)view.findViewById(R.id.layoutUserinfo);
        layoutAbout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });
        layoutMymsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CollectAtmeActivity.class);
                intent.putExtra("url", MSGATME);
                intent.putExtra("title", getString(R.string.message_at_me));
                startActivity(intent);
            }
        });
        layoutCollect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CollectAtmeActivity.class);
                intent.putExtra("url", COLLECT);
                intent.putExtra("title", getString(R.string.collect));
                startActivity(intent);
            }
        });
        layoutUserinfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("mail", mail);
                intent.putExtra("account", account);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("yunli","MineFragment onResume errorMsg = "+ errorMsg);
        if(!TextUtils.isEmpty(errorMsg)){
            CustomToast.showToast(getActivity(),R.drawable.warning,errorMsg, Toast.LENGTH_SHORT);
            errorMsg = null;
        }
    }

    private void getData(){
        if(getActivity() == null){
            return;
        }
        Subscriber<UserDetailsInfo> userSubscriber = new Subscriber<UserDetailsInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    //CustomToast.showToast(getActivity(), R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
                }
                if(throwable.code == 401){
                    quickLogin();
                }
            }
            @Override
            public void onNext(UserDetailsInfo userDetailsInfo) {
                UserDetailsInfo.DataBeans data = userDetailsInfo.getData();
                setUserData(data);
                JSONObject obj = data.toJSONObject();
                PreferenceUtils.put(getActivity(),PreferenceUtils.USERINFO,obj.toString());
                if(data.getApiKeys().size()>0){
                    PreferenceUtils.put(getActivity(),PreferenceUtils.APIKEYS,data.getApiKeys().get(0).getKey());
                }
            }
        };
        String userId = PreferenceUtils.getString(getActivity(), PreferenceUtils.USER_ID,"");
        UserHttpMethods.getInstance(getActivity().getApplicationContext()).getUserInfo(userSubscriber,userId);
    }



    private void getCameraLayout(String key,String lnt,String lat){
        if(getActivity() == null){
            return;
        }
        Subscriber<CameraLayoutInfo> cameraLayoutInfo = new Subscriber<CameraLayoutInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.i("yunli","onError ======== " + e);
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    CustomToast.showToast(getActivity(), R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
                }
                if(throwable.code == 401){
                    quickLogin();
                }
            }
            @Override
            public void onNext(CameraLayoutInfo cameraLayoutInfo) {
                Log.i("yunli","getCameraLayout ======== " + cameraLayoutInfo.getCameraList().get(0).getLatitude());
            }
        };
        UserHttpMethods.getInstance(getActivity().getApplicationContext()).getCameraLayout(cameraLayoutInfo,key,lnt,lat);
    }


    private void getCameraDetail(String jd,String wd){
        if(getActivity() == null){
            return;
        }
        String apikey = PreferenceUtils.getString(getActivity(), PreferenceUtils.APIKEYS, "");
        Subscriber<CameraDetailInfo> cameraDetailInfo = new Subscriber<CameraDetailInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.i("yunli","onError ======== " + e);
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    CustomToast.showToast(getActivity(), R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
                }
                if(throwable.code == 401){
                    quickLogin();
                }
            }
            @Override
            public void onNext(CameraDetailInfo cameraDetailInfo) {
                Log.i("yunli","getCameraDetail ======== " + cameraDetailInfo);
                Log.i("yunli","getCameraDetail ======== " + cameraDetailInfo.getCameraList().get(0).getSBMC());
            }
        };
        UserHttpMethods.getInstance(getActivity().getApplicationContext()).getCameraDetail(cameraDetailInfo,apikey,jd,wd);
    }
    private void getCameraName(String name){
        if(getActivity() == null){
            return;
        }
        String apikey = PreferenceUtils.getString(getActivity(), PreferenceUtils.APIKEYS, "");
        Subscriber<CameraDetailInfo> cameraDetailInfo = new Subscriber<CameraDetailInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.i("yunli","onError ======== " + e);
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    CustomToast.showToast(getActivity(), R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
                }
                if(throwable.code == 401){
                    quickLogin();
                }
            }
            @Override
            public void onNext(CameraDetailInfo cameraDetailInfo) {
                Log.i("yunli","getCameraName ======== " + cameraDetailInfo);
                Log.i("yunli","getCameraName ======== " + cameraDetailInfo.getCameraList().get(0).getSBMC());
            }
        };
        UserHttpMethods.getInstance(getActivity().getApplicationContext()).getCameraName(cameraDetailInfo,apikey,name);
    }
    private void setUserData(UserDetailsInfo.DataBeans data){
        nickname = data.getRealname();
        mail = data.getEmail();
        account = data.getUserNo();
        phone = data.getMobile();
        text_name.setText(data.getRealname());
        text_mail.setText(data.getEmail());
        //Picasso.with(getActivity()).load(Globe.SERVER_HOST+data.getImagePath()).transform(new CircleTransform()).into(imgtit);
        Glide.with(getActivity())
                .load(Globe.SERVER_HOST+data.getImagePath())
                .transform(new GlideCircleTransform(getActivity()))
                .into(imgtit);
    }
    private void logoutDialog(){
        DialogInterface.OnClickListener dialogOnclicListener=new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case Dialog.BUTTON_POSITIVE:
                        logout();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                }
            }
        };
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity()); 
        builder.setMessage(R.string.hint);
        builder.setPositiveButton(R.string.sure,dialogOnclicListener);
        builder.setNegativeButton(R.string.cancel, dialogOnclicListener);
        builder.create().show();
    }
    private void logout(){
        goToLoginActivity();
        if(getActivity() != null) {
            if (!NetWorkUtils.isNetworkConnected(getActivity())) {
                CustomToast.showToast(getActivity(), R.drawable.warning, R.string.net_error, Toast.LENGTH_SHORT);
                return;
            }
            Subscriber<LogoutInfo> subscriber = new Subscriber<LogoutInfo>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(LogoutInfo logoutInfo) {

                }
            };
            UserHttpMethods.getInstance(getActivity().getApplicationContext()).logoutUser(subscriber);
        }

    }
    class GlideCircleTransform extends BitmapTransformation {
        public GlideCircleTransform(Context context) {
            super(context);
        }

        @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private  Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override public String getId() {
            return getClass().getName();
        }
    }
    private void quickLoginWithAuthCode(String authCode){
        String username = PreferenceUtils.getString(getActivity(),PreferenceUtils.ACCOUNT,"");
        String login_user= new String(Base64.decode(username,Base64.DEFAULT));
        String password = PreferenceUtils.getString(getActivity(),PreferenceUtils.PASSWORD,"");
        String login_password= new String(Base64.decode(password,Base64.DEFAULT));
        Log.i("goNewPage","quickLogin---");

        Subscriber<UserBean> subscriber = new Subscriber<UserBean>() {
            @Override
            public void onCompleted() {
                Log.i("goNewPage","onCompleted---");
            }
            @Override
            public void onError(Throwable e) {
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }

                if(throwable.code == 401){
                    goToLoginActivity();
                }
            }
            @Override
            public void onNext(UserBean userBean) {
                Log.i("ttt","onNext---");
                if (userBean != null) {
                    if ("401".equals(userBean.getErrCode())) {
                        CustomToast.showToast(getActivity(), R.drawable.warning, userBean.getMessage(), Toast.LENGTH_SHORT);
                        goToLoginActivity();
                    }else{
                        PreferenceUtils.put(getActivity(), PreferenceUtils.TENANT_ID, userBean.getData().getTenantId());
                        PreferenceUtils.put(getActivity(), PreferenceUtils.USER_ID, userBean.getData().getUserId());
                        PreferenceUtils.put(getActivity(), PreferenceUtils.TOKEN, userBean.getData().getToken());
                        EventBus.getDefault().post(userBean);
                    }
                }
            }
        };
        UserHttpMethods.getInstance(getActivity().getApplicationContext()).loginUser(subscriber, login_user, login_password, "uyun",authCode);
    }
    private void quickLogin(){
        Subscriber<VerticalInfo> subscriber = new Subscriber<VerticalInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(VerticalInfo verticalInfo) {
                if(verticalInfo != null) {
                    String authCode = verticalInfo.getData().getAuthCode();
                    if(authCode != null){
                        quickLoginWithAuthCode(authCode);
                    }else{
                        if("401".equals(verticalInfo.getErrCode())) {
                            CustomToast.showToast(getActivity(), R.drawable.warning, verticalInfo.getMessage(), Toast.LENGTH_SHORT);
                            goToLoginActivity();
                        }
                    }
                }
            }
        };
        FileUtilsMethods mFileUtilMethods = new FileUtilsMethods(getActivity());
        mFileUtilMethods.downloadImageFile(subscriber);
    }
    private void goToLoginActivity(){
        PreferenceUtils.put(getActivity(), PreferenceUtils.TENANT_ID, "");
        PreferenceUtils.put(getActivity(), PreferenceUtils.USER_ID, "");
        PreferenceUtils.put(getActivity(), PreferenceUtils.TOKEN, "");
        Intent intent = new Intent(getActivity(),LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    public void verifyUserData(){
        if(!NetWorkUtils.isNetworkConnected(getActivity())){
            return;
        }
        Subscriber<VerifyInfo> subscriber = new Subscriber<VerifyInfo>(){
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
               if(e instanceof java.net.UnknownHostException){
               }else {
                   quickLogin();
               }
            }

            @Override
            public void onNext(VerifyInfo verifyInfo) {
                if(verifyInfo != null){
                    String errorCode = verifyInfo.getErrCode();
                    if("401".equals(errorCode)){
                        quickLogin();
                    }
                }
            }
        };
        UserHttpMethods.getInstance(getActivity().getApplicationContext()).verifyUser(subscriber);
    }
}
