package com.uyun.hummer.model.bean;

/**
 * Created by zhu on 2017/4/25.
 */

public class LogoutInfo {

    private String errCode;
    private String message;
    private DataBeans data;
    private String mode;
    private String language;
    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public DataBeans getData() {
        return data;
    }

    public void setData(DataBeans data) {
        this.data = data;
    }

    public static class DataBeans{
        private boolean logout;

        public boolean isLogout() {
            return logout;
        }

        public void setLogout(boolean logout) {
            this.logout = logout;
        }
    }
}
