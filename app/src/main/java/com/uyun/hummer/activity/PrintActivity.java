package com.uyun.hummer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uyun.hummer.R;
import com.uyun.hummer.adapter.wheelAdapter.NumericWheelAdapter;
import com.uyun.hummer.utils.SystemUtils;
import com.uyun.hummer.view.CustomToast;
import com.uyun.hummer.view.SelectDialog;
import com.uyun.hummer.view.wheelview.WheelView;

import java.util.Calendar;

import hardware.print.printer;


/**
 * Created by Liyun on 2017/12/7.
 */

public class PrintActivity extends Activity implements View.OnClickListener{
    private printer mPrinter = new printer();
    private RelativeLayout layout_print;
    private SelectDialog mDialog;
    private TextView text_sure;
    private TextView cancel;
    private WheelView mWheelView;
    private TextView textSize;
    private EditText editCount;
    private ImageView printMinus;
    private ImageView printAdd;
    private int printNum = 1;
    private int printSize = 20;
    private LinearLayout back;
    private TextView tit_text;
    private RelativeLayout print_btn;
    private String base64Data;
    private ImageView image_qrcode;
    private Bitmap qrBitmap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        getBitmap();
        initView();
    }
    private void getBitmap(){
        Intent intent = getIntent();
        base64Data = intent.getStringExtra("base64Data");
        base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
        byte[] bytes = Base64.decode(base64Data, Base64.NO_PADDING);
        qrBitmap =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private void initView(){
        image_qrcode = (ImageView) findViewById(R.id.image_qrcode);
        image_qrcode.setImageBitmap(qrBitmap);
        print_btn = (RelativeLayout)findViewById(R.id.print_btn);
        layout_print = (RelativeLayout)findViewById(R.id.layout_print);
        layout_print.setOnClickListener(this);
        print_btn.setOnClickListener(this);
        textSize = (TextView)findViewById(R.id.text_size);
        setPrintSize(20);
        editCount = (EditText)findViewById(R.id.edit_count);
        editCount.setSelection(editCount.length());
        printMinus = (ImageView)findViewById(R.id.printMinus);
        printMinus.setOnClickListener(this);
        printAdd = (ImageView)findViewById(R.id.printAdd);
        printAdd.setOnClickListener(this);
        back = (LinearLayout)findViewById(R.id.back);
        back.setOnClickListener(this);
        back.setVisibility(View.VISIBLE);
        tit_text = (TextView) findViewById(R.id.tit_text);
        tit_text.setText(R.string.print_setup);
        editCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String edit = editCount.getText().toString();
                if(edit.length()==0){
                    return;
                }
                printNum = Integer.parseInt(edit);
                if(printNum>100){
                    editCount.setText("100");
                    printNum = 100;
                    CustomToast.showToast(PrintActivity.this, R.drawable.warning, getString(R.string.max_num), Toast.LENGTH_SHORT);
                }
                if(printNum==0){
                    editCount.setText("1");
                    printNum = 1;
                    CustomToast.showToast(PrintActivity.this, R.drawable.warning, getString(R.string.min_num), Toast.LENGTH_SHORT);
                }
                editCount.setSelection(editCount.length());
            }
        });


    }
    public void setPrintSize(int size){
        printSize = size;
        textSize.setText(size+"*"+size+getString(R.string.mm));
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_print:
                showSelectDialog();
                break;
            case R.id.printMinus:
                printNum = Integer.parseInt(editCount.getText().toString());
                if(printNum!=1){
                    printNum--;
                    editCount.setText(printNum+"");
                }
                break;
            case R.id.printAdd:
                printNum = Integer.parseInt(editCount.getText().toString());
                if(printNum!=100){
                    printNum++;
                    editCount.setText(printNum+"");
                }
                break;
            case R.id.back:
                finish();
                break;
            case R.id.text_sure:
                setPrintSize(mWheelView.getCurrentItem()+10);
                mDialog.diss();
                break;
            case R.id.cancel:
                mDialog.diss();
                break;
            case R.id.print_btn:
                //Bitmap bmp= BitmapFactory.decodeResource(getResources(), R.drawable.qr);
                if(printNum < 0){
                    CustomToast.showToast(this,R.drawable.warning,R.string.num_cannot_under_zero,Toast.LENGTH_SHORT);
                    return;
                }
                printTwoBarcode(qrBitmap,printSize,printNum);
                break;
        }
    }
    private void printTwoBarcode(Bitmap bitmap,int size,int num) {
        try {
            Bitmap bit = SystemUtils.zoomImg(bitmap,size*8,size*8);
            int res = mPrinter.Open();
            mPrinter.SetGrayLevel((byte)6);
            if(res == 0) {
                for (int i=0;i< num;i++) {
                    mPrinter.PrintBitmapAtCenter(bit, 48*8, 48*8);
                }
            }else {
                Toast.makeText(this,"打开失败res = " + res,Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this,"连接失败",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void showSelectDialog() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        if (mDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.wheel_date_picker, null);
            text_sure = (TextView)view.findViewById(R.id.text_sure);
            text_sure.setOnClickListener(this);
            cancel = (TextView)view.findViewById(R.id.cancel);
            cancel.setOnClickListener(this);
            mWheelView = (WheelView) view.findViewById(R.id.printNum);
            mWheelView.setVisibleItems(5);
            NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(this, 10, 48, "%02d");
            numericWheelAdapter2.setLabel("");
            numericWheelAdapter2.setTextSize(24);
            mWheelView.setViewAdapter(numericWheelAdapter2);
            mWheelView.setCyclic(true);
            mWheelView.setCurrentItem(10);
            mDialog = new SelectDialog(this).builder(view);
        }
        mDialog.setCancelable(true);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }


    }



}
