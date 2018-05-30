package com.uyun.hummer.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.uyun.hummer.utils.FileUtils;
import com.uyun.hummer.utils.SystemUtils;

import java.io.File;
import java.io.IOException;
/**
 * Created by Liyun on 2017/5/12.
 */

public class LocalBitmapCache {
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;
    private static LocalBitmapCache instance;


    public static LocalBitmapCache getInstance(Context context){
        if(instance==null){
            synchronized(LocalBitmapCache.class){
                instance = new LocalBitmapCache(context);
            }
        }
        return instance;
    }

    private LocalBitmapCache(Context context){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        try {
            File cacheDir = FileUtils.getDiskCacheDir(context, "thumb");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, SystemUtils.getAppVersionCode(context), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    /**
     * 将缓存记录同步到journal文件中。
     */
    public void fluchCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }
    public DiskLruCache.Snapshot getBitmapFromLruCache(String key){
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskLruCache.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return snapshot;
    }
    public DiskLruCache.Editor getEditor(String key){
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return editor;
    }

}
