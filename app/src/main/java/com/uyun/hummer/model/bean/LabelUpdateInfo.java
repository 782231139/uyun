package com.uyun.hummer.model.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Liyun on 2017/11/9.
 */

public class LabelUpdateInfo {
    public ArrayList<LabelUpdateData> datas = new ArrayList<>();

    public String toString() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < datas.size(); i++) {
            LabelUpdateData data = datas.get(i);
            array.put(data.toJSONObject());
        }
        return array.toString();
    }

}

