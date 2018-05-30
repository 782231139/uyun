package com.uyun.hummer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uyun.hummer.R;
import com.uyun.hummer.activity.LabelManagerActivity;
import com.uyun.hummer.activity.OtherWebviewActivity;
import com.uyun.hummer.httputils.CookieManager;
import com.uyun.hummer.httputils.FileUtilsMethods;
import com.uyun.hummer.model.bean.LabelInfo;
import com.uyun.hummer.model.bean.LabelWebviewKeyInfo;
import com.uyun.hummer.model.bean.UserBean;
import com.uyun.hummer.utils.ExceptionHandle;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.MyComparator;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.utils.SystemUtils;
import com.uyun.hummer.utils.WebViewUtils;
import com.uyun.hummer.view.CustomToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import rx.Subscriber;

/**
 * Created by Liyun on 2017/3/15.
 */

public class LabelFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private String url = null;
    public FileUtilsMethods mFileUtilsMethod;
    private View viewContainer;
    private LabelInfo mLabelInfo;
    private ArrayList<LabelInfo.LabelData> datas = new ArrayList<>();
    private LayoutInflater mInflater;
    private LinearLayout layout_drag,layout_drag2;
    private ScrollView mScrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layout_no_label;
    private LinearLayout layout_label_error;
    private boolean isremove = false;
    private int refreshindex = 0;
    private boolean isViewCreated = false;
    private ArrayList<WebView> webViews = new ArrayList<WebView>();
    private ArrayList<WebView> webViews2 = new ArrayList<WebView>();
    private boolean isLoaded = false;
    private int pagefinishNum = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFileUtilsMethod = new FileUtilsMethods(getActivity());
        viewContainer = inflater.inflate(R.layout.fragment_label, null);
        mInflater = inflater;
        layout_drag = (LinearLayout)viewContainer.findViewById(R.id.layout_drag);
        layout_drag2 = (LinearLayout)viewContainer.findViewById(R.id.layout_drag2);
        mScrollView = (ScrollView)viewContainer.findViewById(R.id.scrollView);
        layout_no_label = (LinearLayout)viewContainer.findViewById(R.id.no_label);
        layout_label_error = (LinearLayout)viewContainer.findViewById(R.id.label_network_error);
        swipeRefreshLayout = (SwipeRefreshLayout)viewContainer.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        return viewContainer;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        ImageView imageView = (ImageView) viewContainer.findViewById(R.id.image_label);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLabelInfo==null){
                    CustomToast.showToast(getActivity(), R.drawable.warning, R.string.laber_no_data, Toast.LENGTH_SHORT);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(Globe.INTENT_LABEL_INFO, new Gson().toJson(mLabelInfo));
                intent.setClass(getActivity(), LabelManagerActivity.class);
                startActivityForResult(intent,Globe.INTENT_REQUEST_CODE_FRAGMENT);
            }
        });
        /*getLabelData();
        getLabelWebviewKey();*/
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void initWebview(Context context){
        /*if(!isViewCreated){

        }
            Log.i("yunli","========newProgress");
            getLabelData();
            getLabelWebviewKey();
        isViewCreated = true;*/
        Log.i("yunli","========LabelFragment");
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isLoaded){

        }else {
            if (isVisibleToUser ) {
                if(refreshindex == 0||refreshindex == 2){
                    refreshindex = 1;
                }else if(refreshindex == 1){
                    refreshindex = 2;
                }
                Log.i("ttt", "setUserVisibleHint------------------------------getLabelData()");
                getLabelData();
                getLabelWebviewKey();
                //isViewCreated = true;
            }else {
                for(int i =0;i<webViews.size();i++){
                    if(webViews.get(i)!=null){
                        WebViewUtils.destroy(webViews.get(i));
                    }
                }
                for(int i =0;i<webViews2.size();i++){
                    if(webViews2.get(i)!=null){
                        WebViewUtils.destroy(webViews2.get(i));
                    }
                }
            }
        }
    }
    @Override
    public void onRefresh() {
        isLoaded = false;
        isremove = true;
        getLabelData();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshCookiesForView(UserBean bean) {
        Log.i("ttt", "refreshCookiesForView ContactsFragment");
        CookieManager.getInstance(getActivity()).syncCookie(url);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        for(int i =0;i<webViews.size();i++){
            if(webViews.get(i)!=null){
                WebViewUtils.destroy(webViews.get(i));
            }
        }
        for(int i =0;i<webViews2.size();i++){
            if(webViews2.get(i)!=null){
                WebViewUtils.destroy(webViews2.get(i));
            }
        }
    }
    public void getLabelWebviewKey() {
        Subscriber<LabelWebviewKeyInfo> subscriber = new Subscriber<LabelWebviewKeyInfo>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                ExceptionHandle.ResponeThrowable throwable = null;
                if (e instanceof Exception) {
                    throwable = ExceptionHandle.handleException(e);
                } else {
                    throwable = new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    CustomToast.showToast(getActivity(), R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onNext(LabelWebviewKeyInfo labelWebviewKeyInfo) {
                String apiKey = labelWebviewKeyInfo.getData().getApiKeys().get(0).getKey();
                String secretKey = labelWebviewKeyInfo.getData().getApiKeys().get(0).getSecretKey();
                String token = PreferenceUtils.getString(getActivity(),PreferenceUtils.TOKEN,"");
                String configUrl = PreferenceUtils.getString(getActivity(),PreferenceUtils.SERVICE_IP,"");
                HashMap<String,String> paramsMap=new HashMap<>();
                paramsMap.put("apiKey",apiKey);
                paramsMap.put("secretKey",secretKey);
                paramsMap.put("token",token);
                paramsMap.put("configUrl",configUrl);
                Gson gson=new Gson();
                String labelWebviewJson = gson.toJson(paramsMap);
                PreferenceUtils.put(getActivity(), PreferenceUtils.LABEL_WEBVIEW_JSON, labelWebviewJson);
            }
        };
        mFileUtilsMethod.getLabelWebviewKey(subscriber);
    }


    public void getLabelData() {
        Subscriber<LabelInfo> subscriber = new Subscriber<LabelInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                setLayoutLabelError();
                mLabelInfo = null;
                ExceptionHandle.ResponeThrowable throwable = null;
                if (e instanceof Exception) {
                    throwable = ExceptionHandle.handleException(e);
                } else {
                    throwable = new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN);
                }
                CustomToast.showToast(getActivity(), R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
            }

            @Override
            public void onNext(LabelInfo labelInfo) {
                mLabelInfo = labelInfo;
                initView();
            }
        };
        mFileUtilsMethod.getLabelTotalData(subscriber);
    }


    public void setLayoutNoLabel() {
        layout_no_label.setVisibility(View.VISIBLE);
        layout_drag.setVisibility(View.GONE);
        layout_drag2.setVisibility(View.GONE);
        layout_label_error.setVisibility(View.GONE);
        Button manage_label = (Button) viewContainer.findViewById(R.id.manage_label);
        manage_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Globe.INTENT_LABEL_INFO, new Gson().toJson(mLabelInfo));
                intent.setClass(getActivity(), LabelManagerActivity.class);
                startActivityForResult(intent,Globe.INTENT_REQUEST_CODE_FRAGMENT);
            }
        });
    }
    public void setLayoutLabelError() {
        layout_no_label.setVisibility(View.GONE);
        layout_drag.setVisibility(View.GONE);
        layout_drag2.setVisibility(View.GONE);
        layout_label_error.setVisibility(View.VISIBLE);
    }

    public void setRefresh(int layoutIndex){
        refreshindex = layoutIndex;
        if(layoutIndex == 2){
            layout_no_label.setVisibility(View.GONE);
            layout_drag.setVisibility(View.GONE);
            layout_label_error.setVisibility(View.GONE);
            layout_drag2.setVisibility(View.VISIBLE);
        }else {
            layout_no_label.setVisibility(View.GONE);
            layout_drag.setVisibility(View.VISIBLE);
            layout_label_error.setVisibility(View.GONE);
            layout_drag2.setVisibility(View.GONE);
        }
    }

    private void initView() {
        datas.clear();
        if(mLabelInfo.data.size() > 0) {
            Arrays.sort(mLabelInfo.data.toArray(), new MyComparator());
            for (int i = 0; i < mLabelInfo.data.size(); i++) {
                LabelInfo.LabelData data = mLabelInfo.data.get(i);
                if (data.isShow) {
                    datas.add(data);
                }
            }
            if(refreshindex == 1){
                syncDraglayout2();
            }else {
                syncDraglayout();
            }

        }
    }
    private void syncDraglayout(){
        pagefinishNum = 0;
        Log.i("ttt", "syncDraglayout1------------------");
        layout_drag.removeAllViews();
        webViews.clear();
        if(datas.size() > 0) {
            Arrays.sort(datas.toArray(),new MyComparator());
            //setLayoutLabel(2);
            for (final LabelInfo.LabelData data : datas) {
                View subview = mInflater.inflate(R.layout.item_linear, null);
                WebView webView = (WebView) subview.findViewById(R.id.webview);
                int webHeight = SystemUtils.dip2px(getContext(),data.height);
                ViewGroup.LayoutParams lpWeb = webView.getLayoutParams();
                lpWeb.height = webHeight;
                webView.setLayoutParams(lpWeb);
                TextView textView = (TextView) subview.findViewById(R.id.tv_title);
                textView.setText(data.getName());
                LinearLayout ll_more = (LinearLayout) subview.findViewById(R.id.ll_more);
                if(data.getTargetUrl().length()>0){
                    ll_more.setVisibility(View.VISIBLE);
                }else {
                    ll_more.setVisibility(View.GONE);
                }
                ll_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = Globe.SERVER_HOST+data.getTargetUrl();
                        String title = data.getName();
                        Intent intent = new Intent(getActivity(), OtherWebviewActivity.class);
                        intent.putExtra("url", url);
                        intent.putExtra("title", title);
                        getActivity().startActivity(intent);
                    }
                });
                webView.loadUrl(Globe.SERVER_HOST+data.getApi());
                webViews.add(webView);
                layout_drag.addView(subview);
                webView.setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        boolean isaddview = true;
                        if(newProgress==100){
                            if(isremove){
                                //layout_drag.removeAllViews();
                                refreshindex = 1;
                                setRefresh(1);
                                isremove = false;

                                Log.i("ttt", "syncDraglayout1111-------------");
                            }
                        }

                    }
                });
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        pagefinishNum += 1;
                        Log.i("ttt", "syncDraglayout1111-------------onPageFinished----"+pagefinishNum);
                        if(pagefinishNum == datas.size()){
                            isLoaded = true;
                        }
                    }
                });

            }
        }else{
          setLayoutNoLabel();
        }
    }
    private void syncDraglayout2(){
        isLoaded = false;
        pagefinishNum = 0;
        Log.i("ttt", "syncDraglayout2------------------");
        layout_drag2.removeAllViews();
        webViews2.clear();
        if(datas.size() > 0) {
            Arrays.sort(datas.toArray(),new MyComparator());
            //setLayoutLabel(1);
            for (final LabelInfo.LabelData data : datas) {
                View subview = mInflater.inflate(R.layout.item_linear, null);
                WebView webView = (WebView) subview.findViewById(R.id.webview);
                int webHeight = SystemUtils.dip2px(getContext(),data.height);
                ViewGroup.LayoutParams lpWeb = webView.getLayoutParams();
                lpWeb.height = webHeight;
                webView.setLayoutParams(lpWeb);
                TextView textView = (TextView) subview.findViewById(R.id.tv_title);
                textView.setText(data.getName());
                LinearLayout ll_more = (LinearLayout) subview.findViewById(R.id.ll_more);
                if(data.getTargetUrl().length()>0){
                    ll_more.setVisibility(View.VISIBLE);
                }else {
                    ll_more.setVisibility(View.GONE);
                }
                ll_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = Globe.SERVER_HOST+data.getTargetUrl();
                        String title = data.getName();
                        Intent intent = new Intent(getActivity(), OtherWebviewActivity.class);
                        intent.putExtra("url", url);
                        intent.putExtra("title", title);
                        getActivity().startActivity(intent);
                    }
                });
                webView.loadUrl(Globe.SERVER_HOST+data.getApi());
                webViews2.add(webView);
                layout_drag2.addView(subview);
                refreshindex = 2;
                webView.setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        boolean isaddview = true;
                        if(newProgress==100){
                            if(isremove){
                                //layout_drag.removeAllViews();
                                setRefresh(2);
                                isremove = false;

                                Log.i("ttt", "syncDraglayout2222-------------");
                            }
                        }
                    }
                });
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        pagefinishNum += 1;
                        if(pagefinishNum == datas.size()){
                            isLoaded = true;
                        }
                        Log.i("ttt", "syncDraglayout2222-------------onPageFinished----"+pagefinishNum);
                    }
                });
            }
        }else{
            setLayoutNoLabel();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Globe.INTENT_REQUEST_CODE_FRAGMENT && resultCode == Globe.INTENT_RESULT_CODE_MANAGE){
            String label = data.getStringExtra(Globe.INTENT_LABEL_INFO);
            if(TextUtils.isEmpty(label)){
                return;
            }
            mLabelInfo = new Gson().fromJson(label,LabelInfo.class);
            datas.clear();
            if(mLabelInfo.data != null && mLabelInfo.data.size() > 0) {
                for (int i = 0; i < mLabelInfo.data.size(); i++) {
                    LabelInfo.LabelData labelData = mLabelInfo.data.get(i);
                    if (labelData.isShow) {
                        datas.add(labelData);
                    }
                }
            }
            isremove = false;
            /*if(refreshindex == 1){
                setLayoutLabel(1);
                syncDraglayout();
            }else {*/
                setRefresh(2);
                syncDraglayout2();
            //}
        }
    }
}
