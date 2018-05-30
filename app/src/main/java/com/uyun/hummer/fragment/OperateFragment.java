package com.uyun.hummer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.uyun.hummer.R;
import com.uyun.hummer.base.fragment.BaseFragment;
import com.uyun.hummer.httputils.CookieManager;
import com.uyun.hummer.javaScriptInterface.AllJavaScriptInterface;
import com.uyun.hummer.model.bean.UserBean;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.SystemUtils;
import com.uyun.hummer.utils.WebViewUtils;
import com.uyun.hummer.utils.webview.JsInteration;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by Liyun on 2017/3/15.
 */

public class OperateFragment extends BaseFragment{
    private String url = null;
    private static String MESSAGE = "#/operate";
    private boolean isViewCreated;
    //private boolean isLoadDataCompleted = false;
    private GoJsPageReceiver mReceiver;
    private RefreshJsPageReceiver reReceiver;
    private static final String TAG = "OperateFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isViewCreated = true;
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //if (getUserVisibleHint()) {
            //isLoadDataCompleted = true;
            //loadWebView();
        //}
        EventBus.getDefault().register(this);
        loadWebView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Globe.MESSAGE_UYUNJS);
        mReceiver = new GoJsPageReceiver();
        getActivity().registerReceiver(mReceiver, filter);

        //init(getWebView(),getActivity());
		IntentFilter reFilter = new IntentFilter();
        reFilter.addAction(Globe.MESSAGE_UYUNJS_REFRESHJS);
        reReceiver = new RefreshJsPageReceiver();
        getActivity().registerReceiver(reReceiver, reFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if(getWebView()!=null){
            destroy(getWebView());
        }
    }
    public static void destroy(WebView webview) {
        webview.stopLoading();
        webview.removeAllViews();
        webview.clearCache(true);
        webview.clearHistory();
        webview.destroy();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        /*if(isVisibleToUser){
            loadWebView();
            //((MainActivity)getActivity()).atMeNotify(false);
        }*/
       /* if (isVisibleToUser && isViewCreated && !isLoadDataCompleted) {
            isLoadDataCompleted = true;
            loadWebView();
        }*/
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void loadingWebview(Context context){
            Log.i("yunli","========newProgress");
            //loadWebView();
        Log.i("yunli","========OperateFragment");
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshCookiesForView(UserBean bean){
        Log.i("ttt","refreshCookiesForView OperateFragment");
        CookieManager.getInstance(getActivity()).syncCookie(url);
        getWebView().loadUrl(url);
    }
    public void loadWebView(){
        getWebView().addJavascriptInterface(new AllJavaScriptInterface(getActivity(),null), "Android");
        getWebView().addJavascriptInterface(new JsInteration(getActivity(),getActivity()), "native");
        //CustomPrograssDialog.getInstance().createLoadingDialog(getActivity(),getString(R.string.loading)).show();
        url = SystemUtils.getUrlWithName(MESSAGE,realHost);
        CookieManager.getInstance(getActivity()).syncCookie(url);
        getWebView().loadUrl(url);
        setText(getString(R.string.operations));
        getWebView().setWebViewClient(new WebViewClient() {
            /*@Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                view.clearHistory();
            }*/

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //CustomPrograssDialog.getInstance().disMissDialog();
                String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                Log.i("yunli","onPageFinished cookies atme= " + cookies);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse response = WebViewUtils.getInstance(getContext()).shouldOverrideInterceptRequest(mFileUtilsMethod,view,request.getUrl().toString());
                if(response != null){
                    return response;
                }else {
                    return super.shouldInterceptRequest(view, request);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse response = WebViewUtils.getInstance(getContext()).shouldOverrideInterceptRequest(mFileUtilsMethod,view,url);
                if(response != null){
                    return response;
                }else {
                    return super.shouldInterceptRequest(view, url);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        getActivity().unregisterReceiver(reReceiver);
        if(getWebView()!=null){
            WebViewUtils.destroy(getWebView());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        url = SystemUtils.getUrlWithName(MESSAGE,realHost);
        CookieManager.getInstance(getActivity()).syncCookie(url);
        String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
        Log.i("yunli","onResume onPageFinished cookies atme= " + cookies);
    }

    public  class GoJsPageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getWebView().loadUrl("javascript:_goMain()");
            Log.d("showToast", "goJsPageReceiver--------goJsPageReceiver");
        }
    }
    public  class RefreshJsPageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getWebView().loadUrl("javascript:refreshResultInApplicationCenter()");
            Log.d("showToast", "goJsPageReceiver--------goJsPageReceiver");
        }
    }
}
