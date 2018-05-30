package com.uyun.hummer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uyun.hummer.MainActivity;
import com.uyun.hummer.R;
import com.uyun.hummer.UyunApplication;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.httputils.UserHttpMethods;
import com.uyun.hummer.model.bean.CodeInfo;
import com.uyun.hummer.model.bean.PwdTypeInfo;
import com.uyun.hummer.model.bean.UserBean;
import com.uyun.hummer.model.bean.VerticalInfo;
import com.uyun.hummer.utils.ExceptionHandle;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.NetWorkUtils;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.view.ClearEditText;
import com.uyun.hummer.view.CustomPrograssDialog;
import com.uyun.hummer.view.CustomToast;

import rx.Subscriber;

/**
 * 项目名称：chat-ops
 * 类描述：
 * 创建人：zhupc
 * 创建时间：2017/3/20 13:36
 * 修改人：liyun
 * 修改时间：2017/3/20 13:36
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    public static final String TAG = LoginActivity.class.getSimpleName();
    private ClearEditText login_user;
    private ClearEditText login_password;
    private Button login;
    private String username, password;
    private TextView setservice;
    private ImageView confilm_icon;
    private ImageView confilm_loading;
    private EditText confilmword;
    private Animation hyperspaceJumpAnimation;
    private String code;
    private FileUtilsMethods mFileUtilMethods;
    private String authCode;
    private String pwdType="MD5";
    private TextView text_company;
    private View layoutConfilmword;
    private String codeSwitch = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        String tenentid = PreferenceUtils.getString(this,PreferenceUtils.TENANT_ID,"");
        String userid = PreferenceUtils.getString(this,PreferenceUtils.USER_ID,"");
        if(!TextUtils.isEmpty(tenentid) && !TextUtils.isEmpty(userid)){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("isload",true);
            startActivity(intent);
            finish();
            return;
        }
        getPwdType();
        //getCodeSwitch();
        getConfilmWord();
    }

    private void init() {
        text_company = (TextView) findViewById(R.id.text_company);
        if(text_company.getText().length()==0){
            text_company.setVisibility(View.GONE);
        }else {
            text_company.setVisibility(View.VISIBLE);
        }
        login_user = (ClearEditText) findViewById(R.id.login_user);
        confilm_icon = (ImageView)findViewById(R.id.confilm_icon);
        layoutConfilmword = findViewById(R.id.layoutConfilmword);
        confilmword = (EditText)findViewById(R.id.confilmword);
        username = PreferenceUtils.getString(this,PreferenceUtils.ACCOUNT,"");
        if(!TextUtils.isEmpty(username)){
            login_user.setText(new String(Base64.decode(username,Base64.DEFAULT)));
        }
        login_password = (ClearEditText) findViewById(R.id.login_password);
        setservice = (TextView) findViewById(R.id.setservice);
        if(UyunApplication.isOnline){
            layoutConfilmword.setVisibility(View.GONE);
            setservice.setVisibility(View.INVISIBLE);
        }else {
            setservice.setOnClickListener(this);
            Globe.SERVER_HOST = PreferenceUtils.getString(this, PreferenceUtils.SERVICE_IP, Globe.SERVER_HOST);
            setservice.setText(getString(R.string.setservice) + Globe.SERVER_HOST);
        }
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        login.setClickable(false);
        confilm_icon.setOnClickListener(this);
        confilm_loading = (ImageView)findViewById(R.id.confilm_loading);
        confilm_loading.setOnClickListener(this);
        login_user.addTextChangedListener(new EditChangeListener());
        login_password.addTextChangedListener(new EditChangeListener());
        confilmword.addTextChangedListener(new EditChangeListener());
        hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                this, R.anim.loading_animation);

        codeSwitch = PreferenceUtils.getString(this,PreferenceUtils.CODE_SWICH,"");
        if(!codeSwitch.equals("true")){
            layoutConfilmword.setVisibility(View.GONE);
        }else {
            layoutConfilmword.setVisibility(View.VISIBLE);
        }
    }
    class EditChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            username = login_user.getText().toString().trim();
            password = login_password.getText().toString().trim();
            if(codeSwitch.equals("true")) {
                code = confilmword.getText().toString().trim();
                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(code)){
                    login.setBackgroundResource(R.drawable.login_btn_blue);
                    login.setClickable(true);
                }else {
                    login.setBackgroundResource(R.drawable.login_btn_gray);
                    login.setClickable(false);
                }
            }else{
                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                    login.setBackgroundResource(R.drawable.login_btn_blue);
                    login.setClickable(true);
                }else {
                    login.setBackgroundResource(R.drawable.login_btn_gray);
                    login.setClickable(false);
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                codeSwitch = PreferenceUtils.getString(this,PreferenceUtils.CODE_SWICH,"");
                if(!codeSwitch.equals("true")){
                    layoutConfilmword.setVisibility(View.GONE);
                }else {
                    layoutConfilmword.setVisibility(View.VISIBLE);
                }
                setservice.setText(getString(R.string.setservice) + Globe.SERVER_HOST);
                getPwdType();
                getConfilmWord();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFileUtilMethods != null) {
            mFileUtilMethods.cancelAllUrl();
            mFileUtilMethods = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                loginApp();
                break;
            case R.id.setservice:
                Intent intent = new Intent(LoginActivity.this,FirstConfigServerActivity.class);
                intent.putExtra("isFirst",false);
                startActivityForResult(intent,0);
                break;
            case R.id.confilm_icon:
                getConfilmWord();
                break;
            case R.id.confilm_loading:
                getConfilmWord();
                break;
        }
    }
    private void getCodeSwitch(){
        Subscriber<CodeInfo> subscriber = new Subscriber<CodeInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(CodeInfo codeInfo) {
                codeSwitch = codeInfo.getData().getCodeSwitch();
                Log.i("yunli","codeSwitch ======== " + codeSwitch);
                if(!codeSwitch.equals("true")){
                    layoutConfilmword.setVisibility(View.GONE);
                }else {
                    layoutConfilmword.setVisibility(View.VISIBLE);
                }
            }
        };
        if(mFileUtilMethods != null){
            mFileUtilMethods.cancelAllUrl();
            mFileUtilMethods = null;
        }
        mFileUtilMethods = new FileUtilsMethods(this,Globe.SERVER_HOST);
        mFileUtilMethods.getCode(subscriber);
    }
    private void getConfilmWord(){
        confilm_loading.setVisibility(View.VISIBLE);
        confilm_loading.startAnimation(hyperspaceJumpAnimation);
        confilm_icon.setVisibility(View.GONE);
        Subscriber<VerticalInfo> subscriber = new Subscriber<VerticalInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                confilm_loading.setVisibility(View.INVISIBLE);
                confilm_loading.clearAnimation();
                confilm_icon.setImageResource(R.drawable.load_error);
                confilm_icon.setVisibility(View.VISIBLE);
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                CustomToast.showToast(LoginActivity.this,R.drawable.warning,throwable.message,Toast.LENGTH_SHORT);
            }

            @Override
            public void onNext(VerticalInfo verticalInfo) {
                Log.i("yunli","verticalInfo thread = " + Thread.currentThread());
                String data = verticalInfo.getData().getBaseDate();
                authCode = verticalInfo.getData().getAuthCode();
                Bitmap bitmap= stringtoBitmap(data);
                confilm_loading.setVisibility(View.INVISIBLE);
                confilm_loading.clearAnimation();
                confilm_icon.setVisibility(View.VISIBLE);
                confilm_icon.setImageBitmap(bitmap);
            }
        };
        if(mFileUtilMethods != null){
            mFileUtilMethods.cancelAllUrl();
            mFileUtilMethods = null;
        }
        mFileUtilMethods = new FileUtilsMethods(this,Globe.SERVER_HOST);
        mFileUtilMethods.downloadImageFile(subscriber);
    }
    public Bitmap stringtoBitmap(String string){
        Bitmap bitmap=null;
        try {
            byte[]bitmapArray;
            bitmapArray=Base64.decode(string, Base64.DEFAULT);
            bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    private void getPwdType(){
        Subscriber<PwdTypeInfo> subscriber = new Subscriber<PwdTypeInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(PwdTypeInfo pwdTypeInfo) {
                pwdType = pwdTypeInfo.getData().getPwdType();
            }
        };
        UserHttpMethods.getInstance(getApplicationContext()).getPwdType(subscriber);
    }
    public void loginApp(){
        username = login_user.getText().toString().trim();
        password = login_password.getText().toString().trim();
        code = confilmword.getText().toString().trim();
        if(!NetWorkUtils.isNetworkConnected(this)){
            CustomToast.showToast(LoginActivity.this,R.drawable.warning,R.string.net_error,Toast.LENGTH_SHORT);
            return;
        }
        CustomPrograssDialog.getInstance().createLoadingDialog(this,R.string.login_ing).show();
        Subscriber<UserBean> subscriber = new Subscriber<UserBean>() {
            @Override
            public void onCompleted() {
                CustomPrograssDialog.getInstance().disMissDialog();
            }

            @Override
            public void onError(Throwable e) {
                getConfilmWord();
                if(codeSwitch.equals("true")) {
                    code = confilmword.getText().toString().trim();
                    if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(code)){
                        login.setBackgroundResource(R.drawable.login_btn_blue);
                        login.setClickable(true);
                    }
                }else{
                    if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                        login.setBackgroundResource(R.drawable.login_btn_blue);
                        login.setClickable(true);
                    }
                }
                CustomPrograssDialog.getInstance().disMissDialog();
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                CustomToast.showToast(LoginActivity.this,R.drawable.warning,throwable.message,Toast.LENGTH_SHORT);
            }
            @Override
            public void onNext(UserBean userBean) {
                if (userBean == null || userBean.getData() == null) {
                } else {
                    String tenantId = userBean.getData().getTenantId();
                    if (!TextUtils.isEmpty(tenantId) && !TextUtils.isEmpty(userBean.getData().getUserId()) && !TextUtils.isEmpty(userBean.getData().getToken())) {
                        PreferenceUtils.put(LoginActivity.this,PreferenceUtils.PASSWORD, Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
                        PreferenceUtils.put(LoginActivity.this,PreferenceUtils.ACCOUNT, Base64.encodeToString(username.getBytes(), Base64.DEFAULT));
                        PreferenceUtils.put(LoginActivity.this, PreferenceUtils.TENANT_ID, tenantId);
                        PreferenceUtils.put(LoginActivity.this, PreferenceUtils.USER_ID, userBean.getData().getUserId());
                        PreferenceUtils.put(LoginActivity.this, PreferenceUtils.TOKEN, userBean.getData().getToken());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("isload",false);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        if(!UyunApplication.isOnline) {
            UserHttpMethods.getInstance(getApplicationContext()).loginUser(subscriber, username, password, code,authCode);
        }else{
            UserHttpMethods.getInstance(getApplicationContext()).loginUser(subscriber, username, password,authCode);
        }
    }

}
