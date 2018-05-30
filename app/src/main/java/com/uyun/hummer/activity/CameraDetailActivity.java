package com.uyun.hummer.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uyun.hummer.R;
import com.uyun.hummer.base.activity.BaseFragmentActivity;
import com.uyun.hummer.httputils.UserHttpMethods;
import com.uyun.hummer.model.bean.CameraDetailInfo;
import com.uyun.hummer.model.bean.CameraSaveInfo;
import com.uyun.hummer.utils.ExceptionHandle;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.view.CameraDetailPopWin;
import com.uyun.hummer.view.CustomToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import rx.Subscriber;

/**
 * Created by zhu on 2018/2/9.
 */

public class CameraDetailActivity extends BaseFragmentActivity implements View.OnClickListener {
    private Button cancel_btn,save_btn;
    private ArrayList<String> titText = new ArrayList<String>();
    private EditText SBBM,SBMC,XZQY;
    private EditText AZDZ,JD,WD,SSXQGAJG,GLDW,GLDWLXFS,LXBCTS;
    private EditText SBXH,DWSC,IPV4,IPV6,MACDZ;
    private CameraDetailInfo cameraDetailInfo;
    private CameraDetailInfo.CameraList list;
    private TextView lon_lat;
    private static final int OPEN_MAP = 2;
    private LinearLayout SXJWZLX_ll,SXJGNLX_ll,SSBMHY_ll;
    private TextView SXJWZLX,SXJGNLX,SSBMHY;

    private LinearLayout JKDWLX_ll,SBZT_ll,SXJLX_ll,JSFW_ll,SXJBMGS_ll,BGSX_ll,LWSX_ll;
    private TextView JKDWLX,SBZT,SXJLX,JSFW,SXJBMGS,BGSX,LWSX,SBCS,AZSJ;
    private ArrayList<String> selectList = new ArrayList<String>();
    private String type;
    private CameraDetailPopWin cameraDetailPopWin;
    private String data_time;
    private Date date=new Date();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_detail);

        cancel_btn = (Button)findViewById(R.id.cancel_btn);
        save_btn = (Button)findViewById(R.id.save_btn);
        lon_lat = (TextView)findViewById(R.id.lon_lat);
        cancel_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        lon_lat.setOnClickListener(this);

        SXJWZLX_ll = (LinearLayout)findViewById(R.id.SXJWZLX_ll);
        SXJWZLX_ll.setOnClickListener(this);
        SXJGNLX_ll = (LinearLayout)findViewById(R.id.SXJGNLX_ll);
        SXJGNLX_ll.setOnClickListener(this);
        SSBMHY_ll = (LinearLayout)findViewById(R.id.SSBMHY_ll);
        SSBMHY_ll.setOnClickListener(this);

        /*String labels = getIntent().getStringExtra(Globe.CAMERA_DETAIL_INFO);
        if (!TextUtils.isEmpty(labels)) {
            cameraDetailInfo = new Gson().fromJson(labels, CameraDetailInfo.class);
        }
        int index = PreferenceUtils.getInt(CameraDetailActivity.this,PreferenceUtils.CAMERA_INDEX,0);
        list = cameraDetailInfo.getCameraList().get(index);*/
        String listStr =  getIntent().getStringExtra(Globe.CAMERA_DETAIL_LIST);
        list = new Gson().fromJson(listStr, CameraDetailInfo.CameraList.class);

        SBBM = (EditText)findViewById(R.id.device_code);
        SBMC = (EditText)findViewById(R.id.device_name);
        XZQY = (EditText)findViewById(R.id.administrative_division);
        SBCS = (TextView)findViewById(R.id.SBCS);
        JKDWLX = (TextView)findViewById(R.id.JKDWLX);
        AZDZ = (EditText)findViewById(R.id.install_address);
        JD = (EditText)findViewById(R.id.longitude);
        WD = (EditText)findViewById(R.id.latitude);
        SXJWZLX = (TextView)findViewById(R.id.SXJWZLX);
        LWSX = (TextView)findViewById(R.id.LWSX);
        SSXQGAJG = (EditText)findViewById(R.id.police);
        AZSJ = (TextView)findViewById(R.id.AZSJ);
        GLDW = (EditText)findViewById(R.id.MUs);
        GLDWLXFS = (EditText)findViewById(R.id.MUs_phone);
        LXBCTS = (EditText)findViewById(R.id.save_days);
        SBZT= (TextView)findViewById(R.id.SBZT);
        SBBM.setText(list.getSBBM());
        SBMC.setText(list.getSBMC());
        XZQY.setText(list.getXZQY());
        SBCS.setText(list.getSBCS());
        JKDWLX.setText(list.getJKDWLX());
        AZDZ.setText(list.getAZDZ());
        JD.setText(list.getJD());
        WD.setText(list.getWD());
        SXJWZLX.setText(list.getSXJWZLX().get(0));
        LWSX.setText(list.getLWSX());
        SSXQGAJG.setText(list.getSSXQGAJG());
        AZSJ.setText(list.getAZSJ());
        GLDW.setText(list.getGLDW());
        GLDWLXFS.setText(list.getGLDWLXFS());
        LXBCTS.setText(list.getLXBCTS());
        SBZT.setText(list.getSBZT());

        SBXH = (EditText)findViewById(R.id.device_model);
        DWSC = (EditText)findViewById(R.id.location_name);
        IPV4 = (EditText)findViewById(R.id.IPV4_address);
        IPV6 = (EditText)findViewById(R.id.IPV6_address);
        MACDZ = (EditText)findViewById(R.id.mac_address);
        SXJLX = (TextView) findViewById(R.id.SXJLX);
        SXJGNLX = (TextView) findViewById(R.id.SXJGNLX);
        BGSX = (TextView)findViewById(R.id.BGSX);
        SXJBMGS = (TextView)findViewById(R.id.SXJBMGS);
        JSFW = (TextView)findViewById(R.id.JSFW);
        SSBMHY = (TextView) findViewById(R.id.SSBMHY);
        SBXH.setText(list.getSBXH());
        DWSC.setText(list.getDWSC());
        IPV4.setText(list.getIPV4());
        IPV6.setText(list.getIPV6());
        MACDZ.setText(list.getMACDZ());
        SXJLX.setText(list.getSXJLX());
        SXJGNLX.setText(list.getSXJGNLX().get(0));
        BGSX.setText(list.getBGSX());
        SXJBMGS.setText(list.getSXJBMGS());
        JSFW.setText(list.getJSFW());
        SSBMHY.setText(list.getSSBMHY().get(0));

        AZSJ.setOnClickListener(this);
        JKDWLX.setOnClickListener(this);
        SBZT.setOnClickListener(this);
        SXJLX.setOnClickListener(this);
        JSFW.setOnClickListener(this);
        SXJBMGS.setOnClickListener(this);
        BGSX.setOnClickListener(this);
        LWSX.setOnClickListener(this);
        SBCS.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initView();
    }
    private void show() {
        data_time = "";
        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//当前年
        int month = calendar.get(Calendar.MONTH);//当前月
        int day = calendar.get(Calendar.DAY_OF_MONTH);//当前日
        //new一个日期选择对话框的对象,并设置默认显示时间为当前的年月日时间.
        DatePickerDialog dialog = new DatePickerDialog(this, mdateListener, year, month, day);
        dialog.show();

    }
    private void timeshow(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//当前年
        int minute = calendar.get(Calendar.MINUTE);//当前年
        TimePickerDialog timePickerDialog= new TimePickerDialog(this, timeListener, hour, minute, true);
        timePickerDialog.show();
    }
    private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {

            data_time = data_time+" "+getStr(hour)+":"+getStr(minute)+":"+"59";
            AZSJ.setText(data_time);
        }

    };
    private String getStr(int i){
        String str = "";
        if(i<10){
            str = "0"+i;
        }else {
            str = i+"";
        }
        return str;
    }
    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
            // TODO: 2017/8/17 这里有选择后的日期回调,根据具体要求写不同的代码,我就直接打印了
            data_time = years+"-"+getStr(monthOfYear+1)+"-"+getStr(dayOfMonth);
            Log.i("com.uyun.hummer", "年" +years+ "月" +monthOfYear+ "日"+dayOfMonth);//这里月份是从0开始的,所以monthOfYear的值是0时就是1月.以此类推,加1就是实际月份了.
            timeshow();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_MAP:

                    String longitude = data.getStringExtra("longitude");
                    String latitude = data.getStringExtra("latitude");
                    Log.i("mSelectPath", "path-----" + longitude+"---"+latitude);
                    JD.setText(longitude);
                    WD.setText(latitude);
                    //mWebView.loadUrl("javascript:_mobile.map.transLocationData('" + mapJson + "')");
                    Toast.makeText(CameraDetailActivity.this, "更新经纬度成功!", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 1005:
                    type = data.getStringExtra("type");
                    selectList = data.getStringArrayListExtra("select");
                        if(type.equals("SXJGNLX")){
                            if(selectList.size()==1){
                                SXJGNLX.setText(selectList.get(0));
                            }else if(selectList.size()>1){
                                SXJGNLX.setText(selectList.get(0) + "...");
                            }else if(selectList.size()==0){
                                SXJGNLX.setText("");
                            }
                            list.setSXJGNLX(selectList);
                        }else if(type.equals("SXJWZLX")){
                            if(selectList.size()==1){
                                SXJWZLX.setText(selectList.get(0));
                            }else if(selectList.size()>1){
                                SXJWZLX.setText(selectList.get(0) + "...");
                            }else if(selectList.size()==0){
                                SXJWZLX.setText("");
                            }
                            list.setSXJWZLX(selectList);
                        }else if(type.equals("SSBMHY")){
                            if(selectList.size()==1){
                                SSBMHY.setText(selectList.get(0));
                            }else if(selectList.size()>1){
                                SSBMHY.setText(selectList.get(0) + "...");
                            }else if(selectList.size()==0){
                                SSBMHY.setText("");
                            }
                            list.setSSBMHY(selectList);
                        }
                    break;
                case 1006:
                    type = data.getStringExtra("type");
                    String select = data.getStringExtra("select");
                    if(type.equals("SXJLX")){
                        list.setSXJLX(select);
                        SXJLX.setText(select);
                    }else if(type.equals("JSFW")){
                        list.setJSFW(select);
                        JSFW.setText(select);
                    }else if(type.equals("SXJBMGS")){
                        list.setSXJBMGS(select);
                        SXJBMGS.setText(select);
                    }else if(type.equals("BGSX")){
                        list.setBGSX(select);
                        BGSX.setText(select);
                    }else if(type.equals("SBZT")){
                        list.setSBZT(select);
                        SBZT.setText(select);
                    }else if(type.equals("JKDWLX")){
                        list.setJKDWLX(select);
                        JKDWLX.setText(select);
                    }else if(type.equals("LWSX")){
                        list.setLWSX(select);
                        LWSX.setText(select);
                    }else if(type.equals("SBCS")){
                        list.setSBCS(select);
                        SBCS.setText(select);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void initView(){
        LinearLayout back = (LinearLayout) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        TextView titText = (TextView) findViewById(R.id.tit_text);
        titText.setText(R.string.info_revise);

        SBCS.setText(list.getSBCS());
        JKDWLX.setText(list.getJKDWLX());
        AZDZ.setText(list.getAZDZ());
        //SXJWZLX.setText(list.getSXJWZLX().get(0));
        LWSX.setText(list.getLWSX());
        SSXQGAJG.setText(list.getSSXQGAJG());
        AZSJ.setText(list.getAZSJ());
        GLDW.setText(list.getGLDW());
        GLDWLXFS.setText(list.getGLDWLXFS());
        LXBCTS.setText(list.getLXBCTS());
        SBZT.setText(list.getSBZT());
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_btn:
                if(!TextUtils.isEmpty(SBCS.getText())
                        &&!TextUtils.isEmpty(JKDWLX.getText())
                        &&!TextUtils.isEmpty(AZDZ.getText())
                        &&!TextUtils.isEmpty(SXJWZLX.getText())
                        &&!TextUtils.isEmpty(LWSX.getText())
                        &&!TextUtils.isEmpty(SSXQGAJG.getText())
                        &&!TextUtils.isEmpty(AZSJ.getText())
                        &&!TextUtils.isEmpty(GLDW.getText())
                        &&!TextUtils.isEmpty(GLDWLXFS.getText())
                        &&!TextUtils.isEmpty(LXBCTS.getText())
                        &&!TextUtils.isEmpty(SBZT.getText())){
                    //Toast.makeText(CameraDetailActivity.this, "请填写所有必填项2222", Toast.LENGTH_SHORT).show();
                    list.setSBCS(SBCS.getText().toString());
                    list.setJKDWLX(JKDWLX.getText().toString());
                    list.setAZDZ(AZDZ.getText().toString());
                    list.setJD(JD.getText().toString());
                    list.setWD(WD.getText().toString());
                    //list.setSXJWZLX(SXJWZLX.getText().toString());
                    list.setLWSX(LWSX.getText().toString());
                    list.setSSXQGAJG(SSXQGAJG.getText().toString());
                    list.setAZSJ(AZSJ.getText().toString());
                    list.setGLDW(GLDW.getText().toString());
                    list.setGLDWLXFS(GLDWLXFS.getText().toString());
                    list.setLXBCTS(LXBCTS.getText().toString());
                    list.setSBZT(SBZT.getText().toString());

                    list.setSBXH(SBXH.getText().toString());
                    list.setDWSC(DWSC.getText().toString());
                    list.setIPV4(IPV4.getText().toString());
                    list.setIPV6(IPV6.getText().toString());
                    list.setMACDZ(MACDZ.getText().toString());
                    list.setSXJLX(SXJLX.getText().toString());
                    //list.setSXJGNLX(SXJGNLX.getText().toString());
                    list.setBGSX(BGSX.getText().toString());
                    list.setSXJBMGS(SXJBMGS.getText().toString());
                    list.setJSFW(JSFW.getText().toString());
                    //list.setSSBMHY(SSBMHY.getText().toString());
                    String data = new Gson().toJson(list);
                    Log.i("yunli","data ======== " + data);
                    saveCamera(data);
                }else {
                    list.setAZDZ(AZDZ.getText().toString());
                    String data = new Gson().toJson(list);
                    Log.i("yunli","data ======== " + data);
                    Toast.makeText(CameraDetailActivity.this, "请填写所有必填项", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case R.id.cancel_btn:
                cameraDetailPopWin = new CameraDetailPopWin(CameraDetailActivity.this, onClickListener);
                cameraDetailPopWin.showAtLocation(CameraDetailActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                //finish();
                break;
            case R.id.back:
                cameraDetailPopWin = new CameraDetailPopWin(CameraDetailActivity.this, onClickListener);
                cameraDetailPopWin.showAtLocation(CameraDetailActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                //finish();
                break;
            case R.id.lon_lat:
                String longitude = JD.getText().toString();
                String latitude = WD.getText().toString();

                Intent intent = new Intent(this, MapReviseActivity.class);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                startActivityForResult(intent,OPEN_MAP);
                break;
            case R.id.SXJWZLX_ll:
                Intent intentWz = new Intent();
                intentWz.setClass(this,SelectActivity.class);
                intentWz.putStringArrayListExtra("select",list.getSXJWZLX());
                intentWz.putExtra("type","SXJWZLX");
                startActivityForResult(intentWz,Globe.INTENT_RESULT_CODE_SELECT);
                break;
            case R.id.SXJGNLX_ll:
                Intent intentGn = new Intent();
                intentGn.setClass(this,SelectActivity.class);
                intentGn.putStringArrayListExtra("select",list.getSXJGNLX());
                intentGn.putExtra("type","SXJGNLX");
                startActivityForResult(intentGn,Globe.INTENT_RESULT_CODE_SELECT);
                break;
            case R.id.SSBMHY_ll:
                Intent intentBm = new Intent();
                intentBm.setClass(this,SelectActivity.class);
                intentBm.putStringArrayListExtra("select",list.getSSBMHY());
                intentBm.putExtra("type","SSBMHY");
                startActivityForResult(intentBm,Globe.INTENT_RESULT_CODE_SELECT);
                break;
            case R.id.JKDWLX:
                toSingleSelect("JKDWLX",list.getJKDWLX());
                break;
            case R.id.SBZT:
                toSingleSelect("SBZT",list.getSBZT());
                break;
            case R.id.SXJLX:
                toSingleSelect("SXJLX",list.getSXJLX());
                break;
            case R.id.JSFW:
                toSingleSelect("JSFW",list.getJSFW());
                break;
            case R.id.SXJBMGS:
                toSingleSelect("SXJBMGS",list.getSXJBMGS());
                break;
            case R.id.BGSX:
                toSingleSelect("BGSX",list.getBGSX());
                break;
            case R.id.LWSX:
                toSingleSelect("LWSX",list.getLWSX());
                break;
            case R.id.SBCS:
                toSingleSelect("SBCS",list.getSBCS());
                break;
            case R.id.AZSJ:
                show();
                break;

        }
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.give_up:
                    cameraDetailPopWin.dismiss();
                    finish();
                    break;
            }
        }
    };
    private void toSingleSelect(String type,String select){
        Intent intentGn = new Intent();
        intentGn.setClass(this,SingleSelectActivity.class);
        intentGn.putExtra("select",select);
        intentGn.putExtra("type",type);
        startActivityForResult(intentGn,Globe.INTENT_RESULT_CODE_SELECT_SINGLE);
    }
    private void saveCamera(String data){
        String apikey = PreferenceUtils.getString(CameraDetailActivity.this, PreferenceUtils.APIKEYS, "");
        Subscriber<CameraSaveInfo> cameraSaveInfo = new Subscriber<CameraSaveInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.i("yunli","onError ======== " + e);
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    CustomToast.showToast(CameraDetailActivity.this, R.drawable.warning, throwable.message, Toast.LENGTH_LONG);
                }
            }
            @Override
            public void onNext(CameraSaveInfo cameraSaveInfo) {
                String errorStr = "";
                Log.i("yunli","getMessage ======== " + cameraSaveInfo.getMessage());
                if(cameraSaveInfo.getData().isStatus()){
                    startActivity(new Intent(CameraDetailActivity.this,CameraSaveActivity.class));
                }else {
                    for(int i=0;i<cameraSaveInfo.getData().getError().size();i++){
                        if(i == cameraSaveInfo.getData().getError().size()-1) {
                            errorStr += cameraSaveInfo.getData().getError().get(i);
                        }else {
                            errorStr += cameraSaveInfo.getData().getError().get(i)+"\n";
                        }
                    }
                    Toast.makeText(CameraDetailActivity.this, errorStr, Toast.LENGTH_SHORT)
                            .show();
                }
                /*mList = cameraLayoutInfo.getCameraList();
                for(int i = 0; i< mList.size(); i++){
                    Log.i("yunli","getCameraLists ======== " + mList.get(i).getXjs());
                    initOverlay(mList.get(i).getLatitude(), mList.get(i).getLongitude(),mList.get(i).getXjs(),mList.get(i).getStatus());
                }*/
            }
        };
        UserHttpMethods.getInstance(CameraDetailActivity.this.getApplicationContext()).saveCamera(cameraSaveInfo,apikey,data);
    }
}
