package com.uyun.hummer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uyun.hummer.R;
import com.uyun.hummer.UyunApplication;
import com.uyun.hummer.base.activity.BaseFragmentActivity;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.httputils.UserHttpMethods;
import com.uyun.hummer.model.bean.CodeInfo;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.NetWorkUtils;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.view.ClearEditText;
import com.uyun.hummer.view.CustomToast;

import rx.Subscriber;

/**
 * Created by Liyun on 2017/4/10.
 */

public class FirstConfigServerActivity extends BaseFragmentActivity implements View.OnClickListener{
    private ClearEditText service;
    private TextView err_hint;
    private Button into_login;
    private String serviceIp;
    private TextView text_company;
    private FileUtilsMethods mFileUtilMethods;
    private String codeSwitch = "";
    private LinearLayout first_background;
    private RelativeLayout first_titleImage;
    private RelativeLayout first_titile;
    private LinearLayout tit_back;
    private TextView tit_text;
    private boolean isFirst = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_config_server);
        Intent intent = getIntent();
        isFirst = intent.getBooleanExtra("isFirst",true);
        if(UyunApplication.isOnline){
            PreferenceUtils.put(this,PreferenceUtils.SERVICE_IP,Globe.SERVER_HOST);
            startActivity(new Intent(FirstConfigServerActivity.this,LoginActivity.class));
            finish();
            return;
        }
        if(!TextUtils.isEmpty(PreferenceUtils.getString(FirstConfigServerActivity.this, PreferenceUtils.SERVICE_IP, ""))&&isFirst){
            startActivity(new Intent(FirstConfigServerActivity.this,LoginActivity.class));
            finish();
            return;
        }
        init();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFileUtilMethods != null) {
            mFileUtilMethods.cancelAllUrl();
            mFileUtilMethods = null;
        }
    }
    private void init(){
        first_background = (LinearLayout) findViewById(R.id.linearLayout);
        first_titleImage = (RelativeLayout) findViewById(R.id.relativeLayout);
        first_titile = (RelativeLayout) findViewById(R.id.tit_relativeLayout);

        text_company = (TextView) findViewById(R.id.text_company);
        if(text_company.getText().length()==0){
            text_company.setVisibility(View.GONE);
        }else {
            text_company.setVisibility(View.VISIBLE);
        }
        service = (ClearEditText) findViewById(R.id.service);
        err_hint = (TextView) findViewById(R.id.err_hint);
        into_login = (Button) findViewById(R.id.into_login);
        into_login.setOnClickListener(this);
        String serverip = PreferenceUtils.getString(FirstConfigServerActivity.this, PreferenceUtils.SERVICE_IP, "");
        if(!TextUtils.isEmpty(serverip)){
            service.setText(serverip);
        }
        if(!isFirst){
            first_background.setBackgroundColor(Color.parseColor("#ffffff"));
            first_titleImage.setVisibility(View.GONE);
            first_titile.setVisibility(View.VISIBLE);
            into_login.setText(R.string.save);
            tit_text = (TextView) findViewById(R.id.tit_text);
            tit_text.setText(R.string.configservice);
            tit_back = (LinearLayout) findViewById(R.id.back);
            tit_back.setVisibility(View.VISIBLE);
            tit_back.setOnClickListener(this);
            service.setBackgroundResource(R.drawable.server_input);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.into_login:
                intoLogin();
                break;
            case R.id.back:
                finish();
                break;
        }
    }
    private void intoLogin(){
        serviceIp = service.getText().toString().trim();
        if(TextUtils.isEmpty(serviceIp)){
            err_hint.setText(R.string.server_cannot_be_null);
            err_hint.setVisibility(View.VISIBLE);
            return;
        }
        if(!NetWorkUtils.isNetworkConnected(FirstConfigServerActivity.this)){
            CustomToast.showToast(FirstConfigServerActivity.this,R.drawable.warning,R.string.net_error, Toast.LENGTH_SHORT);
            return;
        }
        //new ConnServer().execute();
        getCodeSwitch();
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String str = savedInstanceState.getString("editstr");

        if(!TextUtils.isEmpty(str)){
            if(service != null){
                service.setText(str);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String editStr = service.getText().toString().trim();
        if(!TextUtils.isEmpty(editStr)){
            outState.putString("editstr",editStr);
        }
    }
    private void getCodeSwitch(){
        Subscriber<CodeInfo> subscriber = new Subscriber<CodeInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                err_hint.setVisibility(View.VISIBLE);
                err_hint.setText(R.string.serviceiperr);
            }

            @Override
            public void onNext(CodeInfo codeInfo) {
                codeSwitch = codeInfo.getData().getCodeSwitch();
                err_hint.setVisibility(View.INVISIBLE);
                PreferenceUtils.put(FirstConfigServerActivity.this, PreferenceUtils.SERVICE_IP, serviceIp);
                PreferenceUtils.put(FirstConfigServerActivity.this, PreferenceUtils.CODE_SWICH, codeSwitch);
                Globe.SERVER_HOST = serviceIp;
                UserHttpMethods.releaseMethod();
                Intent intent = new Intent(FirstConfigServerActivity.this, LoginActivity.class);
                if(!isFirst){
                    setResult(RESULT_OK, intent);
                }else {
                    startActivity(intent);
                }
                finish();
            }
        };
        if(mFileUtilMethods != null){
            mFileUtilMethods.cancelAllUrl();
            mFileUtilMethods = null;
        }
        try {
            mFileUtilMethods = new FileUtilsMethods(this, serviceIp);
        }catch (Exception e){
            err_hint.setVisibility(View.VISIBLE);
            err_hint.setText(R.string.serviceiperr);
            return;
        }
        mFileUtilMethods.getCode(subscriber);
    }
}
