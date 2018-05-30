package com.uyun.hummer.model.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Liyun on 2017/11/9.
 */

public class ModifyData {
    public boolean isShow;
    public int defaultSort;

    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("isShow",isShow);
            object.put("defaultSort",defaultSort);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
