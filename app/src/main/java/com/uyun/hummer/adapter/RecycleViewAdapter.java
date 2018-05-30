package com.uyun.hummer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uyun.hummer.R;
import com.uyun.hummer.model.bean.LabelInfo;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.OnRecycleManagerClickListener;
import com.uyun.hummer.utils.SystemUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 * E-Mail：543441727@qq.com
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private List<LabelInfo.LabelData> datas;
    private Context mContext;
    private LayoutInflater mLiLayoutInflater;
    private OnRecycleManagerClickListener listener;

    public RecycleViewAdapter(List<LabelInfo.LabelData> datas, Context context) {
        this.datas = datas;
        this.mContext = context;
        this.mLiLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setOnItemLongClickListener(OnRecycleManagerClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecycleViewAdapter.ViewHolder holder =  new ViewHolder(mLiLayoutInflater.inflate(R.layout.item_linear_manager, parent, false));
        holder.webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(listener != null) {
                    listener.onItemLongClick(holder);
                }
                return true;
            }
        });
        holder.ll_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(listener != null) {
                    listener.onItemLongClick(holder);
                }
                return true;
            }
        });
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                listener.deleteItem(pos);
                datas.remove(pos);
                notifyDataSetChanged();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleViewAdapter.ViewHolder holder, int position) {
        Log.i("yunli","onBindViewHolder  holder.tv_title.getText() = " + holder.tv_title.getText());
        int webHeight = SystemUtils.dip2px(mContext,datas.get(position).height);
        ViewGroup.LayoutParams lpItem = holder.ll_item.getLayoutParams();
        lpItem.height = webHeight+ holder.item_title.getLayoutParams().height + SystemUtils.dip2px(mContext,1)+ SystemUtils.dip2px(mContext,56);
        holder.ll_item.setLayoutParams(lpItem);
        ViewGroup.LayoutParams lpWeb = holder.webview_layout.getLayoutParams();
        lpWeb.height = webHeight;
        holder.webview_layout.setLayoutParams(lpWeb);
        holder.tv_title.setText(datas.get(position).getName());
        if(holder.webView.getUrl() != null && holder.webView.getUrl().equals(datas.get(position).getApi())) {
        }else{
            holder.webView.loadUrl(Globe.SERVER_HOST+datas.get(position).getApi());
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView img;
        WebView webView;
        LinearLayout ll_item;
        LinearLayout webview_layout;
        RelativeLayout item_title;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            img = (ImageView) itemView.findViewById(R.id.delimg);
            webview_layout = (LinearLayout)itemView.findViewById(R.id.webview_layout);
            webView = (WebView)itemView.findViewById(R.id.webview);
            ll_item = (LinearLayout) itemView.findViewById(R.id.ll_item);
            item_title = (RelativeLayout)itemView.findViewById(R.id.item_title);
        }
    }
}
