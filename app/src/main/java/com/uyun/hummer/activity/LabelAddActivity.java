package com.uyun.hummer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uyun.hummer.R;
import com.uyun.hummer.model.bean.LabelInfo;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.MyComparator;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zhu on 2017/11/9.
 */

public class LabelAddActivity extends Activity implements View.OnClickListener {
    private LabelInfo mLabelInfo;
    private ArrayList<LabelInfo.LabelData> datas = new ArrayList<>();
    private LinearLayout layout_drag;
    private String realHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_add);
        realHost = PreferenceUtils.getString(this, Globe.SERVER_HOST, Globe.SERVER_HOST);
        layout_drag = (LinearLayout)findViewById(R.id.layout_drag);
        String labels = getIntent().getStringExtra(Globe.INTENT_LABEL_INFO);
        if (!TextUtils.isEmpty(labels)) {
            mLabelInfo = new Gson().fromJson(labels, LabelInfo.class);
        }
        initView();
    }

    private void initView() {
        TextView titText = (TextView) findViewById(R.id.tit_text);
        titText.setText(R.string.add_label);
        TextView textCancel = (TextView) findViewById(R.id.text_cancel);
        textCancel.setOnClickListener(this);
        TextView text_label = (TextView) findViewById(R.id.text_complete);
        text_label.setOnClickListener(this);

        for (int i = 0; i < mLabelInfo.data.size(); i++) {
            LabelInfo.LabelData data = mLabelInfo.data.get(i);
            if (!data.isShow) {
                datas.add(data);
            }
        }
        syncDraglayout();
    }
    private void syncDraglayout() {
        if (datas.size() > 0) {
            Arrays.sort(datas.toArray(),new MyComparator());
            for (int i = 0; i < datas.size(); i++) {
                final int index = i;
                View subview = LayoutInflater.from(this).inflate(R.layout.add_item_linear, null);
                final RelativeLayout addfloat_layout = (RelativeLayout)subview.findViewById(R.id.add_layout);
                WebView webView = (WebView) subview.findViewById(R.id.webview);
                LinearLayout webview_layout = (LinearLayout)subview.findViewById(R.id.webview_layout);
                int webHeight = SystemUtils.dip2px(this,datas.get(i).height);
                ViewGroup.LayoutParams lpWeb = webview_layout.getLayoutParams();
                lpWeb.height = webHeight;
                webview_layout.setLayoutParams(lpWeb);
                ViewGroup.LayoutParams lpLayout = addfloat_layout.getLayoutParams();
                lpLayout.height = webHeight + SystemUtils.dip2px(this,73);
                addfloat_layout.setLayoutParams(lpLayout);
                webView.loadUrl(realHost+datas.get(i).getApi());
                TextView textView = (TextView) subview.findViewById(R.id.tv_title);
                textView.setText(datas.get(i).getName());
                layout_drag.addView(subview);
                subview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!datas.get(index).isShow) {
                            datas.get(index).isShow = true;
                            addfloat_layout.setVisibility(View.VISIBLE);
                        }else{
                            datas.get(index).isShow = false;
                            addfloat_layout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_complete:
                addLabelToManager();
                break;
            case R.id.text_cancel:
                finish();
                break;
        }
    }

    public void addLabelToManager() {
        ArrayList<String> labelDatas = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            LabelInfo.LabelData data = datas.get(i);
            if(data.isShow){
                labelDatas.add(new Gson().toJson(data));
            }
        }
        Intent intent = new Intent();
        intent.setClass(this,LabelManagerActivity.class);
        intent.putStringArrayListExtra(Globe.INTENT_LABEL_DATAS,labelDatas);
        setResult(Globe.INTENT_RESULT_CODE_ADD,intent);
        finish();
    }
}
