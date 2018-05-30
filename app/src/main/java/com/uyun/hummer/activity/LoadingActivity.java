package com.uyun.hummer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.uyun.hummer.R;

import java.util.Locale;


/**
 * Created by zhu on 2017/5/15.
 */

public class LoadingActivity extends Activity{
    public static Activity my = null;
    private TextView text_company;
    private ImageView image_word;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);
        text_company = (TextView) findViewById(R.id.text_company);
        if(text_company.getText().length()==0){
            text_company.setVisibility(View.GONE);
        }else {
            text_company.setVisibility(View.VISIBLE);
        }
        image_word = (ImageView) findViewById(R.id.image_word);
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if(language.equals("zh")){
            image_word.setVisibility(View.VISIBLE);
        }else {
            image_word.setVisibility(View.GONE);
        }
        my = this;
    }
}
