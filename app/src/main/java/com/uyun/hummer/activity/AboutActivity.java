package com.uyun.hummer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uyun.hummer.R;
import com.uyun.hummer.base.activity.BaseFragmentActivity;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.utils.SystemUtils;
import com.uyun.hummer.utils.update.UpdateService;
import com.uyun.hummer.view.NewestVersionPopWin;
import com.uyun.hummer.view.SmallVersionPopWin;

/**
 * Created by Liyun on 2017/5/11.
 */

public class AboutActivity extends BaseFragmentActivity {
    private LinearLayout back;
    private TextView text_version;
    private TextView tit_text;
    private TextView service_text;
    private String service;
    private RelativeLayout update_layout;
    private ImageView update_hint;
    private SmallVersionPopWin smallVersionPopWin;
    private NewestVersionPopWin newestVersionPopWin;
    private boolean versionUpdate;
    private String apkDownloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        back = (LinearLayout)findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        tit_text = (TextView)findViewById(R.id.tit_text);
        tit_text.setText(R.string.about);
        text_version = (TextView)findViewById(R.id.text_version);
        String version = SystemUtils.getVersionName(this);
        text_version.setText(getString(R.string.cur_version) +"  "+ version);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        service = PreferenceUtils.getString(AboutActivity.this,PreferenceUtils.SERVICE_IP, Globe.SERVER_HOST);
        service_text = (TextView)findViewById(R.id.service_text);
        service_text.setText(service);
        update_hint = (ImageView) findViewById(R.id.update_hint);
        versionUpdate = PreferenceUtils.getBoolean(AboutActivity.this,PreferenceUtils.VERSION_UPDATE,false);
        if(versionUpdate){
            update_hint.setVisibility(View.VISIBLE);
        }else {
            update_hint.setVisibility(View.GONE);
        }
        update_layout = (RelativeLayout) findViewById(R.id.update_layout);
        update_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(AboutActivity.this, PoiSearchDemo.class);
                startActivity(intent);*/
                if(versionUpdate){
                    smallVersionPopWin = new SmallVersionPopWin(AboutActivity.this, onClickListener);
                    smallVersionPopWin.showAtLocation(AboutActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                }else {
                    newestVersionPopWin = new NewestVersionPopWin(AboutActivity.this, onClickListener);
                    newestVersionPopWin.showAtLocation(AboutActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                }
            }
        });
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.update_now:
                    apkDownloadUrl = PreferenceUtils.getString(AboutActivity.this,PreferenceUtils.APK_DOWNLOAD_URL,"");
                    Intent intent = new Intent(AboutActivity.this, UpdateService.class);
                    intent.putExtra("apkUrl",apkDownloadUrl);
                    startService(intent);
                    smallVersionPopWin.dismiss();
                    break;
            }
        }
    };

}
