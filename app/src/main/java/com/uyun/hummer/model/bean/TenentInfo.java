package com.uyun.hummer.model.bean;

import java.util.List;

/**
 * Created by Liyun on 2017/4/10.
 */

public class TenentInfo {
    private String errCode;
    private String message;
    private String mode;
    private TenentInfo.DataBeans data;
    public TenentInfo.DataBeans  getData() {
        return data;
    }
    public static class DataBeans{
        private List<ApiKey> apiKeys;

        public List<ApiKey>  getApiKeys() {
            return apiKeys;
        }
    }
    public static class ApiKey{
        private String key;

        public String getKey() {
            return key;
        }
    }
}
