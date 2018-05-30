package com.uyun.hummer.model.bean;

import java.util.ArrayList;

/**
 * Created by zhu on 2018/1/16.
 */

public class VersionInfo {
        private String androidVersion;
        private String androidDownloadUrl;
        private ArrayList<String> androidUpdateContent;


        public String getAndroidVersion() {
            return androidVersion;
        }

        public String getAndroidDownloadUrl() {
            return androidDownloadUrl;
        }

        public ArrayList<String> getAndroidUpdateContent() {
            return androidUpdateContent;
        }

        public void setAndroidUpdateContent(ArrayList<String> androidUpdateContent) {
            this.androidUpdateContent = androidUpdateContent;
    }
}
