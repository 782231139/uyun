package com.uyun.hummer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Liyun on 2017/4/28.
 */

public class FileUtils {
    public static long getFolderSize(File file){
        long size = 0;
        File[] fileList = file.listFiles();
        if (fileList.length == 0) {
            return 0;
        }
        for (int i = 0; i < fileList.length; i++)
        {
            if (fileList[i].isDirectory())
            {
                size = size + getFolderSize(fileList[i]);
            } else
            {
                size = size + fileList[i].length();
            }
        }
        return size;
    }
    /**
     * 获取缓存目录。
     *
     * @param context android Context
     * @return 缓存目录路径
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            try{
                cachePath = context.getExternalCacheDir().getPath();
            }catch (Exception e){
                cachePath = context.getCacheDir().getPath();
            }
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File file = new File(cachePath + File.separator + uniqueName);
        return file;
    }
    /*name = "/mobile/vendor.js"*/
    public static InputStream getLocalStream(Context context,String name) {
        if(name.startsWith("/mobile")){
            name = name.replaceFirst("/","");
        }else if(name.startsWith("/chatops/mobile")){
            name = name.replaceFirst("/chatops/","");
        }
        try {
            InputStream is = context.getResources().getAssets().open(name);
            return is;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*name = "/mobile/assets/images/smile.png"*/
    public static InputStream getLocalPicStream(Context context,String name) {
       if(name.startsWith("/mobile")){
           name = name.replaceFirst("/","");
       }else if(name.startsWith("/chatops/mobile")){
           name = name.replaceFirst("/chatops/","");
       }
        try {
            InputStream is = context.getResources().getAssets().open(name);
            return is;
        } catch (IOException e) {
            return null;
        }
    }
    public static InputStream getLocalIndexStream(Context context) {
        try {
            InputStream is = context.getResources().getAssets().open("mobile/index.html");
            return is;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static InputStream BitmapToStream(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return isBm;
    }
    public static boolean inputWriteToOutputStream(InputStream input, OutputStream output){
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(input);
            out = new BufferedOutputStream(output);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
