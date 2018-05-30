package com.uyun.hummer.model.bean;

import java.util.ArrayList;

/**
 * Created by zhu on 2018/3/9.
 */

public class SelectInfo {
    public ArrayList<Data> data;

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public static class Data {
        public String name;
        public boolean isShow;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isShow() {
            return isShow;
        }

        public void setShow(boolean show) {
            isShow = show;
        }
    }
}
