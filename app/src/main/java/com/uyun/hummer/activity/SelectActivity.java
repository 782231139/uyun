package com.uyun.hummer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uyun.hummer.R;
import com.uyun.hummer.model.bean.SelectInfo;

import java.util.ArrayList;

/**
 * Created by zhu on 2018/3/9.
 */

public class SelectActivity extends Activity implements View.OnClickListener {
    private ArrayList<SelectInfo.Data> datas = new ArrayList<>();
    private SelectInfo.Data data;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> indexs = new ArrayList<String>();
    private LinearLayout layout_drag;
    private ArrayList<String> SXJGNLX = new ArrayList<String>();
    private ArrayList<String> SXJWZLX = new ArrayList<String>();
    private ArrayList<String> SSBMHY = new ArrayList<String>();
    private String type;
    private ArrayList<String> typeList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_add);
        initList();

        list = getIntent().getStringArrayListExtra("select");
        type = getIntent().getStringExtra("type");
        if(type.equals("SXJGNLX")){
            typeList = SXJGNLX;
        }else if(type.equals("SXJWZLX")){
            typeList = SXJWZLX;
        }else if(type.equals("SSBMHY")){
            typeList = SSBMHY;
        }
        for(int i =0;i<typeList.size();i++) {
            indexs.add("0");
        }
            /*data.setName(typeList.get(i));
            data.setShow(false);
            datas.add(data);*/
            /*datas.get(i).setName(typeList.get(i));
            datas.get(i).setShow(false);*/
        for(int i =0;i<typeList.size();i++) {
            for (int n = 0;n<list.size();n++){
                if(typeList.get(i).equals(list.get(n))){
                    //datas.get(i).setShow(true);
                    indexs.set(i,"1");
                }
            }
        }
        initView();
    }

    private void initView() {
        TextView titText = (TextView) findViewById(R.id.tit_text);
        titText.setText("多选");
        TextView textCancel = (TextView) findViewById(R.id.text_cancel);
        textCancel.setOnClickListener(this);
        TextView text_label = (TextView) findViewById(R.id.text_complete);
        text_label.setOnClickListener(this);
        layout_drag = (LinearLayout)findViewById(R.id.layout_drag);
        /*for (int i = 0; i < mLabelInfo.data.size(); i++) {
            LabelInfo.LabelData data = mLabelInfo.data.get(i);
            if (!data.isShow) {
                datas.add(data);
            }
        }*/
        syncDraglayout();
    }
    private void syncDraglayout() {
            for (int i = 0; i < typeList.size(); i++) {
                final int index = i;
                View subview = LayoutInflater.from(this).inflate(R.layout.select_item, null);
                final RelativeLayout addfloat_layout = (RelativeLayout)subview.findViewById(R.id.add_layout);
                TextView textView = (TextView) subview.findViewById(R.id.tv_title);
                textView.setText(typeList.get(i));
                layout_drag.addView(subview);
                if(indexs.get(i).equals("1")){
                    addfloat_layout.setVisibility(View.VISIBLE);
                }
                subview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(indexs.get(index).equals("0")) {
                            //datas.get(index).isShow = true;
                            indexs.set(index,"1");
                            addfloat_layout.setVisibility(View.VISIBLE);
                        }else{
                            //datas.get(index).isShow = false;
                            indexs.set(index,"0");
                            addfloat_layout.setVisibility(View.GONE);
                        }
                    }
                });
            }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_complete:
                getSelectList();
                break;
            case R.id.text_cancel:
                finish();
                break;
        }
    }

    public void getSelectList() {
        list.clear();
        /*for (int i = 0; i < datas.size(); i++) {
            if(datas.get(i).isShow){
                list.add(datas.get(i).getName());
            }
        }*/
        for (int i = 0; i < typeList.size(); i++) {
            if(indexs.get(i).equals("1")){
                list.add(typeList.get(i));
            }
        }
        Intent intent = new Intent();
        intent.setClass(this,LabelManagerActivity.class);
        intent.putStringArrayListExtra("select",list);
        intent.putExtra("type",type);
        setResult(RESULT_OK,intent);
        finish();
    }
    public void initList(){
        SXJGNLX.add(getString(R.string.SXJGNLX1));
        SXJGNLX.add(getString(R.string.SXJGNLX2));
        SXJGNLX.add(getString(R.string.SXJGNLX3));
        SXJGNLX.add(getString(R.string.SXJGNLX4));
        SXJGNLX.add(getString(R.string.SXJGNLX5));
        SXJGNLX.add(getString(R.string.SXJGNLX6));

        SXJWZLX.add(getString(R.string.SXJWZLX1));
        SXJWZLX.add(getString(R.string.SXJWZLX2));
        SXJWZLX.add(getString(R.string.SXJWZLX3));
        SXJWZLX.add(getString(R.string.SXJWZLX4));
        SXJWZLX.add(getString(R.string.SXJWZLX5));
        SXJWZLX.add(getString(R.string.SXJWZLX6));
        SXJWZLX.add(getString(R.string.SXJWZLX7));
        SXJWZLX.add(getString(R.string.SXJWZLX8));
        SXJWZLX.add(getString(R.string.SXJWZLX9));
        SXJWZLX.add(getString(R.string.SXJWZLX10));
        SXJWZLX.add(getString(R.string.SXJWZLX11));
        SXJWZLX.add(getString(R.string.SXJWZLX12));
        SXJWZLX.add(getString(R.string.SXJWZLX13));
        SXJWZLX.add(getString(R.string.SXJWZLX14));
        SXJWZLX.add(getString(R.string.SXJWZLX15));
        SXJWZLX.add(getString(R.string.SXJWZLX16));
        SXJWZLX.add(getString(R.string.SXJWZLX17));
        SXJWZLX.add(getString(R.string.SXJWZLX18));
        SXJWZLX.add(getString(R.string.SXJWZLX19));


        SSBMHY.add(getString(R.string.SSBMHY1));
        SSBMHY.add(getString(R.string.SSBMHY2));
        SSBMHY.add(getString(R.string.SSBMHY3));
        SSBMHY.add(getString(R.string.SSBMHY4));
        SSBMHY.add(getString(R.string.SSBMHY5));
        SSBMHY.add(getString(R.string.SSBMHY6));
        SSBMHY.add(getString(R.string.SSBMHY7));
        SSBMHY.add(getString(R.string.SSBMHY8));
        SSBMHY.add(getString(R.string.SSBMHY9));
        SSBMHY.add(getString(R.string.SSBMHY10));
        SSBMHY.add(getString(R.string.SSBMHY11));
        SSBMHY.add(getString(R.string.SSBMHY12));
        SSBMHY.add(getString(R.string.SSBMHY13));
        SSBMHY.add(getString(R.string.SSBMHY14));
        SSBMHY.add(getString(R.string.SSBMHY15));
        SSBMHY.add(getString(R.string.SSBMHY16));
        SSBMHY.add(getString(R.string.SSBMHY17));
        SSBMHY.add(getString(R.string.SSBMHY18));

    }
}
