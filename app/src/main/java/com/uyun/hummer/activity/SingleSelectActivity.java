package com.uyun.hummer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class SingleSelectActivity extends Activity implements View.OnClickListener {
    private ArrayList<SelectInfo.Data> datas = new ArrayList<>();
    private SelectInfo.Data data;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> indexs = new ArrayList<String>();
    private LinearLayout layout_drag;
    private ArrayList<String> SXJLX = new ArrayList<String>();
    private ArrayList<String> JSFW = new ArrayList<String>();
    private ArrayList<String> SXJBMGS = new ArrayList<String>();
    private ArrayList<String> BGSX = new ArrayList<String>();
    private ArrayList<String> SBZT = new ArrayList<String>();
    private ArrayList<String> JKDWLX = new ArrayList<String>();
    private ArrayList<String> LWSX = new ArrayList<String>();
    private ArrayList<String> SBCS = new ArrayList<String>();


    private String type;
    private ArrayList<String> typeList = new ArrayList<String>();
    private  String select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_add);
        initList();

        select = getIntent().getStringExtra("select");
        type = getIntent().getStringExtra("type");
        if(type.equals("SXJLX")){
            typeList = SXJLX;
        }else if(type.equals("JSFW")){
            typeList = JSFW;
        }else if(type.equals("SXJBMGS")){
            typeList = SXJBMGS;
        }else if(type.equals("BGSX")){
            typeList = BGSX;
        }else if(type.equals("SBZT")){
            typeList = SBZT;
        }else if(type.equals("JKDWLX")){
            typeList = JKDWLX;
        }else if(type.equals("LWSX")){
            typeList = LWSX;
        }else if(type.equals("SBCS")){
            typeList = SBCS;
        }



        for(int i =0;i<typeList.size();i++) {
            if(typeList.get(i).equals(select)){
                indexs.add("1");
            }else {
                indexs.add("0");
            }
        }
        initView();
    }

    private void initView() {
        TextView titText = (TextView) findViewById(R.id.tit_text);
        titText.setText("单选");
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
            }else {
                addfloat_layout.setVisibility(View.GONE);
            }
            subview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(indexs.get(index).equals("0")) {
                        //datas.get(index).isShow = true;
                        indexs.clear();
                        //addfloat_layout.setVisibility(View.VISIBLE);
                        for(int i =0;i<typeList.size();i++) {
                            indexs.add("0");
                        }
                        indexs.set(index,"1");
                        layout_drag.removeAllViews();
                        syncDraglayout();
                        Log.i("yunli","indexs ======== " +indexs.size());
                        /*for (int i = 0; i < typeList.size(); i++) {
                            if (indexs.get(i).equals("1")) {
                                addfloat_layout.setVisibility(View.VISIBLE);
                            } else {
                                addfloat_layout.setVisibility(View.GONE);
                            }
                        }*/
                    }else{
                        /*//datas.get(index).isShow = false;
                        indexs.set(index,"0");
                        addfloat_layout.setVisibility(View.GONE);*/
                    }
                }
            });
        }
    }
    private void refresh(){
        for (int i = 0; i < typeList.size(); i++) {
            View subview = LayoutInflater.from(this).inflate(R.layout.select_item, null);
            final RelativeLayout addfloat_layout = (RelativeLayout)subview.findViewById(R.id.add_layout);
            if(indexs.get(i).equals("1")){
                addfloat_layout.setVisibility(View.VISIBLE);
            }else {
                addfloat_layout.setVisibility(View.GONE);
            }
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
                select = typeList.get(i);
            }
        }
        Intent intent = new Intent();
        intent.setClass(this,LabelManagerActivity.class);
        intent.putExtra("select",select);
        intent.putExtra("type",type);
        setResult(RESULT_OK,intent);
        finish();
    }
    public void initList(){
        JSFW.add("东");
        JSFW.add("西");
        JSFW.add("南");
        JSFW.add("北");
        JSFW.add("东南");
        JSFW.add("东北");
        JSFW.add("西南");
        JSFW.add("西北");
        JSFW.add("全向");

        SXJLX.add("球机");
        SXJLX.add("半球");
        SXJLX.add("固定枪机");
        SXJLX.add("遥控枪机");
        SXJLX.add("卡口枪机");
        SXJLX.add("未知");

        SXJBMGS.add("MPEG-4");
        SXJBMGS.add("H.264");
        SXJBMGS.add("SVAC");
        SXJBMGS.add("H.265");

        BGSX.add("无补光");
        BGSX.add("红外补光");
        BGSX.add("白光补光");
        BGSX.add("其他补光");

        SBZT.add("在用");
        SBZT.add("维修");
        SBZT.add("拆除");

        JKDWLX.add("一类适配监控点");
        JKDWLX.add("二类适配监控点");
        JKDWLX.add("三类适配监控点");
        JKDWLX.add("公安内部视频监控点");
        JKDWLX.add("其他点位");

        LWSX.add("已联网");
        LWSX.add("未联网");

        SBCS.add("海康威视");
        SBCS.add("大华");
        SBCS.add("天地伟业");
        SBCS.add("科达");
        SBCS.add("安讯士");
        SBCS.add("博世");
        SBCS.add("亚安");
        SBCS.add("英飞拓");
        SBCS.add("宇视");
        SBCS.add("海信");
        SBCS.add("中信电子");
        SBCS.add("明景");
        SBCS.add("联想");
        SBCS.add("中兴");
        SBCS.add("索尼");
        SBCS.add("三星");
        SBCS.add("其他");

    }
}
