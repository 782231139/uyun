package com.uyun.hummer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.uyun.hummer.R;

/**
 * Created by zhu on 2018/3/2.
 */

public class CameraSaveActivity extends Activity implements View.OnClickListener {
    private LinearLayout back;
    private Button sure_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_save);
        back = (LinearLayout)findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        sure_btn = (Button)findViewById(R.id.sure_btn);
        sure_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.sure_btn:
                finish();
                break;
        }
    }
}
