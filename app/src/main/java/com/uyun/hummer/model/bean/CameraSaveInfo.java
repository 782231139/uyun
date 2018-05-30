package com.uyun.hummer.model.bean;

import java.util.ArrayList;

/**
 * Created by zhu on 2018/3/2.
 */

public class CameraSaveInfo {
    private int code;
    private String message;
    private Data data;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{

        private boolean status;
        private ArrayList<String> error;
        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public ArrayList<String> getError() {
            return error;
        }

        public void setError(ArrayList<String> error) {
            this.error = error;
        }
    }

}
