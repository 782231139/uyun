package com.uyun.hummer.model.bean;

import org.json.JSONObject;

/**
 * Created by Liyun on 2017/3/17.
 */
/*
{
    "errCode": null,
    "message": null,
    "data": {
        "tenantId": "a10adc3949ba59abbe56e057f20f88aa",
        "userId": "a10adc3949ba59abbe56e057f20f88aa",
        "token": "2tf9UNtFZz24YPzryYKRALsQAbX73nvQg5u/HnpYLfhmKOWyXQ4bortoWjyRzNrfeZTsnurhGLSEIzKZhKwxRtCJQfJXWf3KDZSVOy7cyuA="
    },
    "mode": "offline",
    "language": "zh_CN"
}*/

public class UserBean {
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
        private String tenantId;
        private String userId;
        private String token;
        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
