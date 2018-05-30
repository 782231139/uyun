package com.uyun.hummer.model.bean;

import java.util.List;

/**
 * Created by Liyun on 2017/4/10.
 */

public class VerifyInfo {
    private String errCode;
    private String message;
    private String mode;
    private VerifyInfo.DataBeans data;
    public VerifyInfo.DataBeans  getData() {
        return data;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getMessage() {
        return message;
    }

    public static class DataBeans{
        private String tenantId;
        private String userId;

        public String getTenantId() {
            return tenantId;
        }

        public String getUserId() {
            return userId;
        }
    }
}
