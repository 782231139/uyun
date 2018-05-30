package com.uyun.hummer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uyun.hummer.R;
import com.uyun.hummer.base.activity.BaseFragmentActivity;

/**
 * Created by zhu on 2017/11/1.
 */

public class UserInfoActivity extends BaseFragmentActivity{
    private LinearLayout back;
    private TextView mail;
    private TextView nickname;
    private TextView account;
    private TextView phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        mail = (TextView) findViewById(R.id.mail);
        nickname = (TextView) findViewById(R.id.nickname);
        account = (TextView) findViewById(R.id.account);
        phone = (TextView) findViewById(R.id.phone);
        mail.setText(intent.getStringExtra("mail"));
        nickname.setText(intent.getStringExtra("nickname"));
        account.setText(intent.getStringExtra("account"));
        phone.setText(intent.getStringExtra("phone"));
        back = (LinearLayout) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
