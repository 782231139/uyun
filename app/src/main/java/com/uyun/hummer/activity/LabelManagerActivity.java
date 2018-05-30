package com.uyun.hummer.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uyun.hummer.R;
import com.uyun.hummer.adapter.RecycleViewAdapter;
import com.uyun.hummer.fragment.LabelFragment;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.model.bean.LabelInfo;
import com.uyun.hummer.model.bean.LabelUpdateData;
import com.uyun.hummer.model.bean.LabelUpdateInfo;
import com.uyun.hummer.model.bean.LabelUpdateResultInfo;
import com.uyun.hummer.model.bean.ModifyData;
import com.uyun.hummer.utils.ExceptionHandle;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.OnRecycleManagerClickListener;
import com.uyun.hummer.view.CustomPrograssDialog;
import com.uyun.hummer.view.CustomToast;
import com.uyun.hummer.view.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.RequestBody;
import rx.Subscriber;

/**
 * Created by Liyun on 2017/11/8.
 */

public class LabelManagerActivity extends Activity implements OnRecycleManagerClickListener, View.OnClickListener {
    private LabelInfo mLabelInfo;
    private SwipeRecyclerView mRecyclerView;
    private RecycleViewAdapter myAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ArrayList<LabelInfo.LabelData> datas = new ArrayList<>();
    private FileUtilsMethods mFileUtilsMethod;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_manager);
        mFileUtilsMethod = new FileUtilsMethods(this);
        String labels = getIntent().getStringExtra(Globe.INTENT_LABEL_INFO);
        if (!TextUtils.isEmpty(labels)) {
            mLabelInfo = new Gson().fromJson(labels, LabelInfo.class);
        }
        initView();
    }

    private void initView() {
        Button addLabel = (Button) findViewById(R.id.addLabel);
        addLabel.setOnClickListener(this);
        TextView textCancel = (TextView) findViewById(R.id.text_cancel);
        textCancel.setOnClickListener(this);
        TextView text_label = (TextView) findViewById(R.id.text_complete);
        text_label.setOnClickListener(this);

        mRecyclerView = (SwipeRecyclerView) findViewById(R.id.swipeRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemViewCacheSize(Globe.RECYCLE_POOL_SIZE);
        if(mLabelInfo.data.size() > 0) {
            for (int i = 0; i < mLabelInfo.data.size(); i++) {
                LabelInfo.LabelData data = mLabelInfo.data.get(i);
                if (data.isShow) {
                    datas.add(data);
                }
            }
        }
        if (datas.size() == 0) {
            setLayoutNoLabel();
        }
        myAdapter = new RecycleViewAdapter(datas, this);
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemLongClickListener(this);
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(datas, i, i + 1);

                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(datas, i, i - 1);
                    }
                }
                Collections.swap(mLabelInfo.data, findPosInLabelInfo(fromPosition), findPosInLabelInfo(toPosition));
                myAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                Log.i("yunli", "viewHolder.getLayoutPosition() = " + viewHolder.getLayoutPosition() + ",viewHolder.getAdapterPosition() = " + viewHolder.getAdapterPosition());

                viewHolder.itemView.setBackgroundColor(0);
            }
        });

        mItemTouchHelper.attachToRecyclerView(mRecyclerView);


    }

    private int findPosInLabelInfo(int pos){
        LabelInfo.LabelData data = datas.get(pos);
        for(int i =0;i<mLabelInfo.data.size();i++){
            if(data.code.equals(mLabelInfo.data.get(i).code)){
                return i;
            }
        }
        return  -1;
    }
    @Override
    public void onItemLongClick(RecyclerView.ViewHolder vh) {
        mItemTouchHelper.startDrag(vh);
        Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
        vib.vibrate(70);
    }

    @Override
    public void deleteItem(int pos) {
        LabelInfo.LabelData data = datas.get(pos);
        if(data != null && mLabelInfo.data.size() > 0) {
            for (int i = 0; i < mLabelInfo.data.size(); i++) {
                LabelInfo.LabelData labelData = mLabelInfo.data.get(i);
                if (data.code.equals(labelData.code)) {
                    mLabelInfo.data.get(i).isShow = false;
                    mLabelInfo.data.remove(labelData);
                    mLabelInfo.data.add(labelData);
                    break;
                }
            }
        }
        if(datas.size() == 1){
            setLayoutNoLabel();
        }
    }

    public void setLayoutNoLabel() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.no_label);
        layout.setVisibility(View.VISIBLE);
        Button manage_label = (Button) findViewById(R.id.manage_label);
        manage_label.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }
    public void setLayoutLabel() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.no_label);
        layout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addLabel:
                if (datas.size() == mLabelInfo.data.size()) {
                    CustomToast.showToast(this,R.drawable.warning,getString(R.string.all_label_added), Toast.LENGTH_SHORT);
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(this, LabelAddActivity.class);
                intent.putExtra(Globe.INTENT_LABEL_INFO, new Gson().toJson(mLabelInfo));
                startActivityForResult(intent, Globe.INTENT_REQUEST_CODE_MANAGE);
                break;
            case R.id.text_complete:
                updateLabelData();
                break;
            case R.id.text_cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Globe.INTENT_REQUEST_CODE_MANAGE && resultCode == Globe.INTENT_RESULT_CODE_ADD) {
            ArrayList<String> labels = data.getStringArrayListExtra(Globe.INTENT_LABEL_DATAS);
            if (labels.size() > 0) {
                setLayoutLabel();
                for (int i = 0; i < labels.size(); i++) {
                    LabelInfo.LabelData labelData = new Gson().fromJson(labels.get(i), LabelInfo.LabelData.class);
                    datas.add(labelData);
                    for (int j = 0; j < mLabelInfo.data.size(); j++) {
                        if(labelData.code.equals(mLabelInfo.data.get(j).code)){
                            mLabelInfo.data.get(j).isShow = true;
                            Collections.swap(mLabelInfo.data,j,datas.size()-1);
                        }
                    }
                }
            }
            
            if(datas.size() == 0){
                setLayoutNoLabel();
            }
            myAdapter.notifyDataSetChanged();
        }
    }

    public void updateLabelData() {
        CustomPrograssDialog.getInstance().createLoadingDialog(this,getString(R.string.loading)).show();
        LabelUpdateInfo labelUpdateInfo = new LabelUpdateInfo();
        for (int i = 0; i < mLabelInfo.data.size(); i++) {
            LabelUpdateData data = new LabelUpdateData();
            data.code = mLabelInfo.data.get(i).code;
            ModifyData modifyData = new ModifyData();
            modifyData.isShow = mLabelInfo.data.get(i).isShow;
            modifyData.defaultSort = mLabelInfo.data.get(i).defaultSort = i;
            data.modeify = modifyData;
            labelUpdateInfo.datas.add(data);
        }
        String strEntity = labelUpdateInfo.toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity);
        Subscriber<LabelUpdateResultInfo> subscriber = new Subscriber<LabelUpdateResultInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                CustomPrograssDialog.getInstance().disMissDialog();
                ExceptionHandle.ResponeThrowable throwable = null;
                if (e instanceof Exception) {
                    throwable = ExceptionHandle.handleException(e);
                } else {
                    throwable = new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    CustomToast.showToast(LabelManagerActivity.this, R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onNext(LabelUpdateResultInfo labelInfo) {
                CustomPrograssDialog.getInstance().disMissDialog();
                if (labelInfo.errCode == 200) {
                    Intent intent = new Intent();
                    intent.setClass(LabelManagerActivity.this, LabelFragment.class);
                    intent.putExtra(Globe.INTENT_LABEL_INFO,new Gson().toJson(mLabelInfo));
                    setResult(Globe.INTENT_RESULT_CODE_MANAGE,intent);
                    finish();
                } else {
                    CustomToast.showToast(LabelManagerActivity.this, R.drawable.warning, labelInfo.msg, Toast.LENGTH_SHORT);
                    finish();
                }
            }
        };

        mFileUtilsMethod.updateLabelData(subscriber, body);
    }

}
