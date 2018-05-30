package com.uyun.hummer.model.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Liyun on 2017/4/7.
 *{
 "errCode": null,
 "message": null,
 "data": {
 "realname": "lalal",
 "username": "demo",
 "tenantName": "12233费分d",
 "contacts": null,
 "email": "demo@uyunsoft.cn",
 "mobile": "15957196656",
 "qq": "22112sssdddss",
 "weixin": "ssssdfsdfwwwss",
 "site": "2211s144",
 "userExcess": null,
 "userNo": null,
 "userCount": null,
 "imagePath": "/tenant/userimages/category2/a10adc3949ba59abbe56e057f20f88aa.jpeg"
 },
 "mode": "offline",
 "language": "zh_CN"
 }
 */

public class UserDetailsInfo {
    private String errCode;
    private String message;
    private DataBeans data;

    public String getErrCode() {
        return errCode;
    }

    public String getMessage() {
        return message;
    }

    public DataBeans getData() {
        return data;
    }

    public static class DataBeans{
        private String realname;
        private String username;
        private String tenantName;
        private String email;
        private String mobile;
        private String qq;
        private String weixin;
        private String site;
        private String imagePath;
        private String userNo;
        private ArrayList<ApiKey> apiKeys;

        public static class ApiKey{
            private String key;

            public String getKey() {
                return key;
            }
        }

        public String getUserNo() {
            return userNo;
        }

        public ArrayList<ApiKey> getApiKeys() {
            return apiKeys;
        }

        public void setUserNo(String userNo) {
            this.userNo = userNo;
        }

        public String getRealname() {
            return realname;
        }

        public String getUsername() {
            return username;
        }

        public String getTenantName() {
            return tenantName;
        }

        public String getEmail() {
            return email;
        }

        public String getMobile() {
            return mobile;
        }

        public String getQq() {
            return qq;
        }

        public String getWeixin() {
            return weixin;
        }

        public String getSite() {
            return site;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void parseStrToDataBeans(String str){
            try {
                JSONObject jsonObject = new JSONObject(str);
                this.realname = jsonObject.optString("realname");
                this.username = jsonObject.optString("username");
                this.tenantName = jsonObject.optString("tenantname");
                this.email = jsonObject.optString("email");
                this.mobile = jsonObject.optString("mobile");
                this.qq = jsonObject.optString("qq");
                this.weixin = jsonObject.optString("weixin");
                this.site = jsonObject.optString("site");
                this.imagePath = jsonObject.optString("imagePath");
                this.userNo = jsonObject.optString("userNo");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public JSONObject toJSONObject(){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("realname",this.realname);
                jsonObject.put("username",this.username);
                jsonObject.put("tenantname",this.tenantName);
                jsonObject.put("email",this.email);
                jsonObject.put("mobile",this.mobile);
                jsonObject.put("qq",this.qq);
                jsonObject.put("weixin",this.weixin);
                jsonObject.put("site",this.site);
                jsonObject.put("imagePath",this.imagePath);
                jsonObject.put("userNo",this.userNo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }
}
