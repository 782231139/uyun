package com.uyun.hummer.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.uyun.hummer.MainActivity;

/**
 * Created by zhu on 2018/3/15.
 */

public class MyService extends Service {
    private static final String TAG = "wxx";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.d(TAG, "MyService: onCreate()");

        //定义一个notification
        Notification notification = new Notification();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        //notification.setLatestEventInfo(this, "My title", "My content", pendingIntent);
        //把该service创建为前台service
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d(TAG, "MyService: onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

}