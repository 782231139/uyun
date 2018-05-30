package com.uyun.hummer.model.bean;

/**
 * Created by Liyun on 2017/7/18.
 */

public class VerticalInfo {
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

    public DataBeans getData() {
        return data;
    }

    public void setData(DataBeans data) {
        this.data = data;
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

    public static class DataBeans{
        private String authCode;
        private String baseDate;

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }

        public String getBaseDate() {
            return baseDate;
        }

        public void setBaseDate(String baseDate) {
            this.baseDate = baseDate;
        }
    }
}
