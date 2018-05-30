package com.uyun.hummer.model.bean;

import java.util.ArrayList;

/**
 * Created by zhu on 2018/1/6.
 */

public class PageParamInfo {

    private PageParam pageParam;
    //private String preloadFile;
    public ArrayList<String> preloadFile = new ArrayList<>();
    private String url;


    public static class PageParam {
        public String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public PageParam getPageParam() {
        return pageParam;
    }

    public void setPageParam(PageParam pageParam) {
        this.pageParam = pageParam;
    }

    public ArrayList<String> getPreloadFile() {
        return preloadFile;
    }

    public void setPreloadFile(ArrayList<String> preloadFile) {
        this.preloadFile = preloadFile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
