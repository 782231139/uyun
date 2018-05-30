package com.uyun.hummer.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uyun.hummer.R;

/**
 * Created by Liyun on 2017/4/11.
 */

public class CustomToast {
    public static void showToast(Context context,int imageid, int txtid,int duration){
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        //初始化布局控件
        TextView mTextView = (TextView) toastRoot.findViewById(R.id.message);
        ImageView mImageView = (ImageView) toastRoot.findViewById(R.id.imageView);
        //为控件设置属性
        mTextView.setText(txtid);
        mImageView.setImageResource(imageid);
        //Toast的初始化
        Toast toastStart = new Toast(context);
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        toastStart.setGravity(Gravity.TOP, 0, height / 3);
        toastStart.setDuration(duration);
        toastStart.setView(toastRoot);
        toastStart.show();
    }
    public static void showToast(Context context,int imageid, String name,int duration){
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        //初始化布局控件
        TextView mTextView = (TextView) toastRoot.findViewById(R.id.message);
        ImageView mImageView = (ImageView) toastRoot.findViewById(R.id.imageView);
        //为控件设置属性
        mTextView.setText(name);
        mImageView.setImageResource(imageid);
        //Toast的初始化
        Toast toastStart = new Toast(context);
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        toastStart.setGravity(Gravity.TOP, 0, height / 3);
        toastStart.setDuration(duration);
        toastStart.setView(toastRoot);
        toastStart.show();
    }
}
