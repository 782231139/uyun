package com.uyun.hummer.model.bean;

/**
 * Created by zhu on 2018/4/27.
 */

public class VersionUrlInfo {
    private String errCode;
    private DataBeans data;
    public DataBeans  getData() {
        return data;
    }

    public String getErrCode() {
        return errCode;
    }


    public static class DataBeans{
        private String androidMajorVersion;
        private String androidChildVersionUrl;

        public String getAndroidMajorVersion() {
            return androidMajorVersion;
        }

        public void setAndroidMajorVersion(String androidMajorVersion) {
            this.androidMajorVersion = androidMajorVersion;
        }

        public String getAndroidChildVersionUrl() {
            return androidChildVersionUrl;
        }

        public void setAndroidChildVersionUrl(String androidChildVersionUrl) {
            this.androidChildVersionUrl = androidChildVersionUrl;
        }
    }

}
