package com.uyun.hummer.model.bean;

import java.util.ArrayList;

/**
 * Created by Liyun on 2017/11/8.
 */

public class LabelInfo {
    public int errCode;
    public ArrayList<LabelData> data;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public ArrayList<LabelData> getData() {
        return data;
    }

    public void setData(ArrayList<LabelData> data) {
        this.data = data;
    }

    public static class LabelData{
        public String code;
        public String name;
        public int defaultSort;
        public String api;
        public String targetUrl;
        public boolean isShow;
        public int height;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDefaultSort() {
            return defaultSort;
        }

        public void setDefaultSort(int defaultSort) {
            this.defaultSort = defaultSort;
        }

        public String getApi() {
            return api;
        }

        public void setApi(String api) {
            this.api = api;
        }

        public String getTargetUrl() {
            return targetUrl;
        }

        public void setTargetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
        }

        public boolean isShow() {
            return isShow;
        }

        public void setShow(boolean show) {
            isShow = show;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
