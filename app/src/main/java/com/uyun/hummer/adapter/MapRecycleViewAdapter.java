package com.uyun.hummer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uyun.hummer.R;
import com.uyun.hummer.model.bean.CameraDetailInfo;
import com.uyun.hummer.utils.OnRecycleManagerClickListener;
import com.uyun.hummer.utils.PreferenceUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/13 0013.
 * E-Mail：543441727@qq.com
 */

public class MapRecycleViewAdapter extends RecyclerView.Adapter<MapRecycleViewAdapter.ViewHolder> {

    //private List<LabelInfo.LabelData> datas;
    private Context mContext;
    private LayoutInflater mLiLayoutInflater;
    private OnRecycleManagerClickListener listener;
    private ArrayList<CameraDetailInfo.CameraList> datas = new ArrayList<>();
    private int pos2 = -1;

    public MapRecycleViewAdapter(ArrayList<CameraDetailInfo.CameraList> datas, Context context) {
        this.datas = datas;
        this.mContext = context;
        this.mLiLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setOnItemLongClickListener(OnRecycleManagerClickListener listener){
        this.listener = listener;
    }

    @Override
    public MapRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MapRecycleViewAdapter.ViewHolder holder =  new ViewHolder(mLiLayoutInflater.inflate(R.layout.camera_list_item, parent, false));
        //Log.i("yunli","onCreateViewHolder  pos = "+pos);
        holder.camera_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pos2 = holder.getLayoutPosition();
                PreferenceUtils.put(mContext, PreferenceUtils.CAMERA_INDEX, pos2);
                Log.i("yunli","onClick  pos2 = "+pos2);
                //holder.camera_lin.setBackgroundResource(R.drawable.shadow);
                notifyDataSetChanged();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final MapRecycleViewAdapter.ViewHolder holder, int position) {
        Log.i("yunli","onBindViewHolder  position = "+position);
        if(pos2 == position){
            holder.camera_lin.setBackgroundResource(R.drawable.shadow);
        }else {
            holder.camera_lin.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        holder.camera_name.setText(datas.get(position).getSBMC());
        if(datas.get(position).getSXJLX().equals("1")||datas.get(position).getSXJLX().equals("2")){
            if(datas.get(position).getSBZT().equals("1")){
                holder.camera_img.setBackgroundResource(R.drawable.camera_normal);
            }else if(datas.get(position).getSBZT().equals("2")){
                holder.camera_img.setBackgroundResource(R.drawable.camera_bad);
            }
        }else if(datas.get(position).getSXJLX().equals("3")||datas.get(position).getSXJLX().equals("4")||datas.get(position).getSXJLX().equals("5")){
            if(datas.get(position).getSBZT().equals("1")){
                holder.camera_img.setBackgroundResource(R.drawable.camera_normal2);
            }else if(datas.get(position).getSBZT().equals("2")){
                holder.camera_img.setBackgroundResource(R.drawable.camera_bad2);
            }
        }

    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView camera_name;
        ImageView camera_img;
        LinearLayout camera_lin;
        public ViewHolder(View itemView) {
            super(itemView);
            camera_name = (TextView) itemView.findViewById(R.id.camera_name);
            camera_img = (ImageView) itemView.findViewById(R.id.camera_img);
            camera_lin = (LinearLayout) itemView.findViewById(R.id.camera_lin);
        }
    }
}
