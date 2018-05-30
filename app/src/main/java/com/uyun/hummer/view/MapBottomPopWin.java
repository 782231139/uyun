package com.uyun.hummer.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.uyun.hummer.R;
import com.uyun.hummer.model.bean.CameraDetailInfo;
import com.uyun.hummer.utils.OnRecycleManagerClickListener;
import com.uyun.hummer.utils.PreferenceUtils;

import java.util.ArrayList;

/**
 * Created by zhu on 2018/3/1.
 */

public class MapBottomPopWin extends PopupWindow{

    private Context mContext;
    private TextView camera_num;
    private View view;

    //private Button know;
    private RecyclerView mRecyclerView;
    private Button info_revise;

    public MapBottomPopWin(Context mContext, ArrayList<CameraDetailInfo.CameraList> datas,View.OnClickListener itemsOnClick) {

        this.view = LayoutInflater.from(mContext).inflate(R.layout.map_buttom, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);

        info_revise = (Button) view.findViewById(R.id.info_revise);
        TextView route_text = (TextView) view.findViewById(R.id.route_text);
        TextView showlist_text = (TextView) view.findViewById(R.id.showlist_text);
        info_revise.setOnClickListener(itemsOnClick);
        info_revise.setClickable(false);
        route_text.setOnClickListener(itemsOnClick);
        showlist_text.setOnClickListener(itemsOnClick);

        camera_num = (TextView) view.findViewById(R.id.camera_num);
        camera_num.setText("("+datas.size()+")");

        LinearLayoutManager ms= new LinearLayoutManager(mContext);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(ms);
        MapRecycleViewAdapter adapter=new MapRecycleViewAdapter(datas,mContext);
        mRecyclerView.setAdapter(adapter);

        /*know = (Button) view.findViewById(R.id.know);
        // 取消按钮
        know.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });*/

        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.my_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });


    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(00000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.take_photo_anim);

    }

    class MapRecycleViewAdapter extends RecyclerView.Adapter<MapRecycleViewAdapter.ViewHolder> {

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
            final MapRecycleViewAdapter.ViewHolder holder =  new MapRecycleViewAdapter.ViewHolder(mLiLayoutInflater.inflate(R.layout.camera_list_item, parent, false));
            //Log.i("yunli","onCreateViewHolder  pos = "+pos);
            holder.camera_lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    pos2 = holder.getLayoutPosition();
                    PreferenceUtils.put(mContext, PreferenceUtils.CAMERA_INDEX, pos2);
                    Log.i("yunli","onClick  pos2 = "+pos2);
                    //holder.camera_lin.setBackgroundResource(R.drawable.shadow);
                    notifyDataSetChanged();
                    info_revise.setBackgroundColor(Color.parseColor("#4988E9"));
                    info_revise.setClickable(true);
                }
            });
            return holder;
        }


        @Override
        public void onBindViewHolder(final MapRecycleViewAdapter.ViewHolder holder, int position) {
            Log.i("yunli","onBindViewHolder  position = "+position);
            /*if(pos2!=-2){

            }*/
            if(pos2 == position){
                holder.camera_lin.setBackgroundResource(R.drawable.shadow);
            }else {
                holder.camera_lin.setBackgroundColor(Color.parseColor("#ffffff"));
            }
            holder.camera_name.setText(datas.get(position).getSBMC());
            if(datas.get(position).getSXJLX().equals("球机")||datas.get(position).getSXJLX().equals("半球")){
                if(datas.get(position).getSBZT().equals("在用")){
                    holder.camera_img.setBackgroundResource(R.drawable.camera_normal);
                }else if(datas.get(position).getSBZT().equals("维修")){
                    holder.camera_img.setBackgroundResource(R.drawable.camera_bad);
                }else if(datas.get(position).getSBZT().equals("拆除")){
                    holder.camera_img.setBackgroundResource(R.drawable.camera_null);
                }
            }else if(datas.get(position).getSXJLX().equals("固定枪机")||datas.get(position).getSXJLX().equals("遥控枪机")||datas.get(position).getSXJLX().equals("卡口枪机")){
                if(datas.get(position).getSBZT().equals("在用")){
                    holder.camera_img.setBackgroundResource(R.drawable.camera_normal2);
                }else if(datas.get(position).getSBZT().equals("维修")){
                    holder.camera_img.setBackgroundResource(R.drawable.camera_bad2);
                }else if(datas.get(position).getSBZT().equals("拆除")){
                    holder.camera_img.setBackgroundResource(R.drawable.camera_null2);
                }
            }else {
                if(datas.get(position).getSBZT().equals("在用")){
                    holder.camera_img.setBackgroundResource(R.drawable.camera_normal3);
                }else if(datas.get(position).getSBZT().equals("维修")){
                    holder.camera_img.setBackgroundResource(R.drawable.camera_bad3);
                }else if(datas.get(position).getSBZT().equals("拆除")){
                    holder.camera_img.setBackgroundResource(R.drawable.camera_null3);
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
}
