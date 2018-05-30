package com.uyun.hummer.ViewInterface;

/**
 * Created by Liyun on 2017/12/15.
 */

public interface IMsgFragmentViewInterface extends IBaseInterface{
    public  void openScan();
    public void addMediaRecordData(final String chatUserID);
    public void sendMediaRecordData();
    public void playMediaRecord(final String downloadUrl, final String recordName);
    public void stopPlayMediaRecord();
    public void cancelSendMediaRecord();
    public void showNotify(String title, String text, long time, String url, String urlTitle, int iconNotify);
    public void showToast(String msg, int isCollect);
    public void closeLoad();
}
