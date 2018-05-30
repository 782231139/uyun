package com.uyun.hummer.model.bean;

import java.util.ArrayList;

/**
 * Created by zhu on 2018/2/1.
 */

public class CameraLayoutInfo {

    private ArrayList<Cameralist> CameraList;

    public ArrayList<Cameralist> getCameraList() {
        return CameraList;
    }

    public void setCameraList(ArrayList<Cameralist> cameraList) {
        CameraList = cameraList;
    }

    public static class Cameralist {
        private int xjs;
        private String latitude;
        private String longitude;
        private String status;

        public int getXjs() {
            return xjs;
        }

        public void setXjs(int xjs) {
            this.xjs = xjs;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
