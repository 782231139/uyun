package com.uyun.hummer.ViewInterface;


/**
 * Created by Liyun on 2017/12/15.
 */

public interface IOtherWebViewInterface extends IBaseInterface{
    public void headerChange(String title);
    public void openMap(String jsonData);
    public void screenRotation(String jsonData);
}
