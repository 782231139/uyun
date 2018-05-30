package com.uyun.hummer.model.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Liyun on 2017/11/9.
 */

public class LabelUpdateData {
    public String code;
    public ModifyData modeify;
    public JSONObject toJSONObject(){
        JSONObject object = new JSONObject();
        try {
            object.put("code",code);
            object.put("modeify",modeify.toJSONObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
