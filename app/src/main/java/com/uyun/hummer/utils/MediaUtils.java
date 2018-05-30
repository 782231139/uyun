package com.uyun.hummer.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Liyun on 2017/11/23.
 */

public class MediaUtils {
    private String userid;
    private Context mContext;
    private String recordFileName;
    private File recordFile;
    private MediaRecorder mMediaRecorder;
    private int recordFileTime;
    private MediaPlayer mMediaPlayer;
    private File recordRootFile;
    public MediaUtils(Context context){
        mContext = context;
        userid = PreferenceUtils.getString(context,PreferenceUtils.USER_ID,"");
        recordRootFile = FileUtils.getDiskCacheDir(mContext,userid);
        if(!recordRootFile.exists()){
            recordRootFile.mkdir();
        }
    }
    public void startMediaRecord(String chatUserId) {
        if(FileUtils.getFolderSize(recordRootFile) > 20*1024*1024){
            DeleteFileUtil.deleteDirectory(recordRootFile.getAbsolutePath());
        }
        recordFileName = userid + "_" + chatUserId + "_" + String.valueOf(System.currentTimeMillis()) + ".amr";
        recordFile = new File(recordRootFile.getAbsolutePath()+"/" + recordFileName);
        Log.i("uploadMultiFile","uploadMultiFile---recordFile---"+recordFile);
        try {
            recordFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setAudioSamplingRate(8000);
        mMediaRecorder.setOutputFile(recordFile.getAbsolutePath());
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopMediaRecord(){

        if (mMediaRecorder !=null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
        }
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(recordFile.getPath());  //recordingFilePath（）为音频文件的路径
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recordFileTime = player.getDuration();//获取音频的时间
        Log.d("uploadMultiFile", "recordFileTime==" + recordFileTime);
        player.release();//记得释放资源
    }
    public File getFileInNative(String recordName){
        File file = new File(recordRootFile+ "/" + recordName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(file.exists() && file.canRead()){
            return file;
        }else {
            return null;
        }
    }
    public void startMediaPlay(File file, final AudioManager audioManager){
        Uri uri = Uri.fromFile(file);
        mMediaPlayer = MediaPlayer.create(mContext, uri);
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                //TODO:finish
                audioManager.setMode(AudioManager.MODE_NORMAL);
            }
        });
    }
    public void stopPlayMedia(){
        if(recordFile != null) {
            if (FileUtils.getFolderSize(recordRootFile) > 20 * 1024 * 1024) {
                DeleteFileUtil.deleteDirectory(recordRootFile.getAbsolutePath());
            }
        }
        if(mMediaPlayer!=null&& mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }
    public void cancelSendMedia(){
        if (mMediaRecorder !=null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
        }
        if (recordFile != null && recordFile.exists()&&recordFile.canWrite()) {
            recordFile.delete();
        }
    }
    public int getRecordFileTime(){
        return recordFileTime;
    }
    public File getRecordFile(){
        return recordFile;
    }

}
