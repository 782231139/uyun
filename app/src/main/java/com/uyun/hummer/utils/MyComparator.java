package com.uyun.hummer.utils;

import com.uyun.hummer.model.bean.LabelInfo;

/**
 * Created by Liyun on 2017/11/23.
 */

public class MyComparator implements java.util.Comparator{
    @Override
    public int compare(Object o1, Object o2) {
        LabelInfo.LabelData data1 = (LabelInfo.LabelData)o1;
        LabelInfo.LabelData data2 = (LabelInfo.LabelData)o2;
        if(data1.defaultSort > data2.defaultSort){
            return 1;
        }else {
            return -1;
        }
    }
}
