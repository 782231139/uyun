package com.uyun.hummer.model.bean;

import java.util.ArrayList;

/**
 * Created by zhu on 2017/11/13.
 */

public class LabelWebviewKeyInfo {

    public int errCode;
    public LabelWebviewKeyData data;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public LabelWebviewKeyData getData() {
        return data;
    }

    public void setData(LabelWebviewKeyData data) {
        this.data = data;
    }

    public static class LabelWebviewKeyData{
        public ArrayList<LabelWebviewKeyData.LabelApiKeys> apiKeys;

        public ArrayList<LabelApiKeys> getApiKeys() {
            return apiKeys;
        }

        public void setApiKeys(ArrayList<LabelApiKeys> apiKeys) {
            this.apiKeys = apiKeys;
        }

        public static class LabelApiKeys{
            public String key;
            public String secretKey;

            public String getSecretKey() {
                return secretKey;
            }

            public void setSecretKey(String secretKey) {
                this.secretKey = secretKey;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }
    }

}
