package com.uyun.hummer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uyun.hummer.R;
import com.uyun.hummer.model.bean.CameraDetailInfo;
import com.uyun.hummer.utils.Globe;

import java.util.ArrayList;

/**
 * Created by zhu on 2018/2/9.
 */

public class CameraListActivity extends Activity implements View.OnClickListener {
    private LinearLayout layout_camera;
    private CameraDetailInfo cameraDetailInfo;
    private ArrayList<CameraDetailInfo.CameraList> datas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);
        String labels = getIntent().getStringExtra(Globe.CAMERA_DETAIL_INFO);
        if (!TextUtils.isEmpty(labels)) {
            cameraDetailInfo = new Gson().fromJson(labels, CameraDetailInfo.class);
        }
        initView();
    }
    private void initView() {
        LinearLayout back = (LinearLayout) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        TextView titText = (TextView) findViewById(R.id.tit_text);
        titText.setText(R.string.map_service);
        layout_camera= (LinearLayout) findViewById(R.id.layout_camera);
        datas = cameraDetailInfo.getCameraList();
        if(datas.size()>0){
            for(int i =0;i<datas.size();i++){
                final int index = i;
                View subview = LayoutInflater.from(this).inflate(R.layout.camera_item, null);
                TextView name_text  = (TextView) subview.findViewById(R.id.name_text);
                name_text.setText(datas.get(i).getSBMC());
                TextView lon_text  = (TextView) subview.findViewById(R.id.lon_text);
                lon_text.setText(datas.get(i).getJD());
                TextView lat_text  = (TextView) subview.findViewById(R.id.lat_text);
                lat_text.setText(datas.get(i).getWD());
                ImageView camera_img =(ImageView)subview.findViewById(R.id.camera_img);
                /*if(datas.get(i).getSXJLX().equals("1")||datas.get(i).getSXJLX().equals("2")){
                    if(datas.get(i).getSBZT().equals("1")){
                        camera_img.setBackgroundResource(R.drawable.camera_normal);
                    }else if(datas.get(i).getSBZT().equals("2")){
                        camera_img.setBackgroundResource(R.drawable.camera_bad);
                    }else if(datas.get(i).getSBZT().equals("3")){
                        camera_img.setBackgroundResource(R.drawable.camera_null);
                    }
                }else if(datas.get(i).getSXJLX().equals("3")||datas.get(i).getSXJLX().equals("4")||datas.get(i).getSXJLX().equals("5")){
                    if(datas.get(i).getSBZT().equals("1")){
                        camera_img.setBackgroundResource(R.drawable.camera_normal2);
                    }else if(datas.get(i).getSBZT().equals("2")){
                        camera_img.setBackgroundResource(R.drawable.camera_bad2);
                    }else if(datas.get(i).getSBZT().equals("3")){
                        camera_img.setBackgroundResource(R.drawable.camera_null2);
                    }
                }else {
                    if(datas.get(i).getSBZT().equals("1")){
                        camera_img.setBackgroundResource(R.drawable.camera_normal3);
                    }else if(datas.get(i).getSBZT().equals("2")){
                        camera_img.setBackgroundResource(R.drawable.camera_bad3);
                    }else if(datas.get(i).getSBZT().equals("3")){
                        camera_img.setBackgroundResource(R.drawable.camera_null3);
                    }
                }*/
                if(datas.get(i).getSXJLX().equals("球机")||datas.get(i).getSXJLX().equals("半球")){
                    if(datas.get(i).getSBZT().equals("在用")){
                        camera_img.setBackgroundResource(R.drawable.camera_normal);
                    }else if(datas.get(i).getSBZT().equals("维修")){
                        camera_img.setBackgroundResource(R.drawable.camera_bad);
                    }else if(datas.get(i).getSBZT().equals("拆除")){
                        camera_img.setBackgroundResource(R.drawable.camera_null);
                    }
                }else if(datas.get(i).getSXJLX().equals("固定枪机")||datas.get(i).getSXJLX().equals("遥控枪机")||datas.get(i).getSXJLX().equals("卡口枪机")){
                    if(datas.get(i).getSBZT().equals("在用")){
                        camera_img.setBackgroundResource(R.drawable.camera_normal2);
                    }else if(datas.get(i).getSBZT().equals("维修")){
                        camera_img.setBackgroundResource(R.drawable.camera_bad2);
                    }else if(datas.get(i).getSBZT().equals("拆除")){
                        camera_img.setBackgroundResource(R.drawable.camera_null2);
                    }
                }else {
                    if(datas.get(i).getSBZT().equals("在用")){
                        camera_img.setBackgroundResource(R.drawable.camera_normal3);
                    }else if(datas.get(i).getSBZT().equals("维修")){
                        camera_img.setBackgroundResource(R.drawable.camera_bad3);
                    }else if(datas.get(i).getSBZT().equals("拆除")){
                        camera_img.setBackgroundResource(R.drawable.camera_null3);
                    }
                }

                layout_camera.addView(subview);

                subview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("yunli","subview.setOnClickListener ======== ");
                        Intent intent = new Intent();
                        intent.setClass(CameraListActivity.this, CameraDetailActivity.class);
                        intent.putExtra(Globe.CAMERA_DETAIL_LIST, new Gson().toJson(datas.get(index)));
                        startActivity(intent);

                    }
                });
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
