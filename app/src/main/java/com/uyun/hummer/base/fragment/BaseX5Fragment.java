package com.uyun.hummer.base.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebView;
import com.uyun.hummer.MainActivity;
import com.uyun.hummer.R;
import com.uyun.hummer.base.view.BaseX5WebView;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.PreferenceUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Liyun on 2017/4/13.
 */

public class BaseX5Fragment extends Fragment{
    private BaseX5WebView mWebView;
    private TextView tit_text;
    private SwipeRefreshLayout swipeLayout;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    public String realHost = null;
    public FileUtilsMethods mFileUtilsMethod;
    public static boolean isClickNotify = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_x5,null);
        mWebView = (BaseX5WebView) view.findViewById(R.id.msg_web);
        tit_text= (TextView) view.findViewById(R.id.tit_text);

        //swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        realHost = PreferenceUtils.getString(getActivity(), Globe.SERVER_HOST,"");
        mFileUtilsMethod = new FileUtilsMethods(getActivity());
        mWebView.initWebView();
        initNotify();
        return view;
    }
    public WebView getWebView(){
        return mWebView;
    }
    public void setText(String tit){
        tit_text.setText(tit);
    }

    @Override
    public void onResume() {
        super.onResume();
        clearNotifyBase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFileUtilsMethod != null) {
            mFileUtilsMethod.cancelAllUrl();
            mFileUtilsMethod = null;
        }
    }

    public void showNotifyBase(String title,String text,long time,String url,String urlTitle,int iconNotify){
        mBuilder.setContentTitle(title)
                .setContentText(text)
                .setTicker(title+"  "+text)//通知首次出现在通知栏，带上升动画效果的
                .setPriority(Notification.PRIORITY_HIGH)//设置该通知优先级
                .setWhen(time);//通知产生的时间，会在通知信息里显示
        Intent intent = new Intent(getActivity(), NotificationClickReceiver .class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");

            Object extraNotification = field.get(notification);

            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);

            method.invoke(extraNotification, iconNotify);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Intent intent = new Intent(getActivity(), ChatWebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url",url);
        intent.putExtra("title",urlTitle);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);*/
        getmNotificationManager().notify(100, notification);
    }
    public NotificationManager getmNotificationManager() {
        return mNotificationManager;
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性:
     * 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(getActivity(), 1, new Intent(), flags);
        return pendingIntent;
    }
    /** 初始化通知栏 */
    private void initNotify(){
        mNotificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getActivity());
        mBuilder.setContentTitle("")
                .setContentText("")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量
                .setTicker("")//通知首次出现在通知栏，带上升动画效果的
                //.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSound(Uri.parse("android.resource://"
                        + getActivity().getPackageName() + "/" + R.raw.notify))
                .setSmallIcon(R.drawable.ic_launcher);
    }
    public void clearNotifyBase(){
        mNotificationManager.cancel(100);
    }
    public static class NotificationClickReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            isClickNotify = true;
            Log.d("activity---", "NotificationClickReceiver");
            Intent newIntent = new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
    }
}
