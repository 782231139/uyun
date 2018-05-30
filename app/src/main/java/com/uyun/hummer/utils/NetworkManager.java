package com.uyun.hummer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import java.util.HashSet;

public class NetworkManager {
	private static NetworkManager sManager;

	private Context mContext;
	private HashSet<INetworkListner> mListners;
	private NetworkReceiver mReceiver;

	public static NetworkManager getInstance(Context context) {
		if (sManager == null) {
			sManager = new NetworkManager(context);
		}else{
			sManager.updateContext(context);
		}
		return sManager;
	}

	public NetworkManager(Context context) {
		mContext = context;
		init();
	}

	/**每次切换Activity时，更新Context，防止Toast等事件无法通知。
	 * @param context
	 */
	public void updateContext(Context context){
		mContext = context;
	}
	
	private void init(){
		mListners = new HashSet<INetworkListner>();
		mReceiver = new NetworkReceiver();
	}
	
	public void registerDateTransReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(mReceiver,filter);
	}
	public void unregisterDateTransReceiver(){
		try {
			mContext.unregisterReceiver(mReceiver);
		}catch (Exception e){

		}
	}


	public void register(INetworkListner listner){
		mListners.add(listner);
	}
	
	public void unregister(INetworkListner listner){
		mListners.remove(listner);
	}
	private class NetworkReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
				boolean status = NetWorkUtils.isNetworkConnected(mContext);
				if(mListners != null){
					for (INetworkListner listner : mListners) {
						listner.onNetworkChanged(status);
					}
				}
				return;
			}
		}

	}
	public void update() {
		boolean status = NetWorkUtils.isNetworkConnected(mContext);
		if(mListners != null){
			for (INetworkListner listner : mListners) {
				listner.onNetworkChanged(status);
			}
		}
	}
	public void update(INetworkListner listner) {
		boolean status = NetWorkUtils.isNetworkConnected(mContext);
		listner.onNetworkChanged(status);
	}

}
