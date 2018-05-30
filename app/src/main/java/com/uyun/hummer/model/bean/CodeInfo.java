package com.uyun.hummer.model.bean;

/**
 * Created by zhu on 2017/9/5.
 */

public class CodeInfo {


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

    public DataBeans getData() {
        return data;
    }

    public void setData(DataBeans data) {
        this.data = data;
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

    public static class DataBeans{
        private String skinSwitch;
        private String pwdStrengthChoice;
        private String pwdType;
        private String mobileControl;
        private String pwdFind;
        private String codeSwitch;

        public String getSkinSwitch() {
            return skinSwitch;
        }

        public void setSkinSwitch(String skinSwitch) {
            this.skinSwitch = skinSwitch;
        }

        public String getPwdStrengthChoice() {
            return pwdStrengthChoice;
        }

        public void setPwdStrengthChoice(String pwdStrengthChoice) {
            this.pwdStrengthChoice = pwdStrengthChoice;
        }

        public String getPwdType() {
            return pwdType;
        }

        public void setPwdType(String pwdType) {
            this.pwdType = pwdType;
        }

        public String getMobileControl() {
            return mobileControl;
        }

        public void setMobileControl(String mobileControl) {
            this.mobileControl = mobileControl;
        }

        public String getPwdFind() {
            return pwdFind;
        }

        public void setPwdFind(String pwdFind) {
            this.pwdFind = pwdFind;
        }

        public String getCodeSwitch() {
            return codeSwitch;
        }

        public void setCodeSwitch(String codeSwitch) {
            this.codeSwitch = codeSwitch;
        }
    }
    /*{"errCode":null,"message":null,
            "data":{"skinSwitch":"true","pwdStrengthChoice":"1",
            "pwdType":"MD5","mobileControl":"true","pwdFind":"true","codeSwitch":"false"},
        "mode":"offline","language":"zh_CN"}*/
}
