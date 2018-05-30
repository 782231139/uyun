package com.uyun.hummer.utils;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Liyun on 2017/11/2.
 */

public interface OnRecycleManagerClickListener {
    public void onItemLongClick(RecyclerView.ViewHolder vh);
    public void deleteItem(int pos);
}
