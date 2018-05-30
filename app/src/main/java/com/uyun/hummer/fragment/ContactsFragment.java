package com.uyun.hummer.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.uyun.hummer.MainActivity;
import com.uyun.hummer.R;
import com.uyun.hummer.base.fragment.BaseFragment;
import com.uyun.hummer.httputils.CookieManager;
import com.uyun.hummer.javaScriptInterface.AllJavaScriptInterface;
import com.uyun.hummer.model.bean.UserBean;
import com.uyun.hummer.utils.SystemUtils;
import com.uyun.hummer.utils.WebViewUtils;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by Liyun on 2017/3/15.
 */

public class ContactsFragment extends BaseFragment {
    private String url = null;
    private static String CONTACTS = "#/contacts/home";
    private boolean isViewCreated;
    private boolean isLoad= false;
    //private int isCollect = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //if (getUserVisibleHint()) {
            //loadWebView();
        //}
        //getRefreshLayout().setOnRefreshListener(this);
        EventBus.getDefault().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void initWebview(Context context){
        loadWebView();
        isViewCreated = true;
        Log.i("ttt", "Webview------- LabelFragment");
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshCookiesForView(UserBean bean){
        Log.i("ttt","refreshCookiesForView ContactsFragment");
        CookieManager.getInstance(getActivity()).syncCookie(url);
        getWebView().loadUrl(url);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if(getWebView()!=null){
            WebViewUtils.destroy(getWebView());
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isViewCreated ) {
            Log.i("ttt", "setUserVisibleHint------------------------------getLabelData()");
            loadWebView();
            isViewCreated = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CookieManager.getInstance(getActivity()).syncCookie(url);
    }

    public void loadWebView(){
        //if(getUserVisibleHint()) {
            getWebView().addJavascriptInterface(new AllJavaScriptInterface(getActivity(),null), "Android");
            //CustomPrograssDialog.getInstance().createLoadingDialog(getActivity(),getString(R.string.loading)).show();
            url = SystemUtils.getUrlWithName(CONTACTS, realHost);
            if(isLoad){
                getWebView().reload();
            }else {
                //getWebView().loadUrl(url);
                getWebView().loadUrl(url);
                isLoad = true;
            }
            setText(getString(R.string.collect));
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
                    Log.i("yunli","onPageFinished cookies = " + cookies);
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    WebResourceResponse response = WebViewUtils.getInstance(getContext()).shouldOverrideInterceptRequest(mFileUtilsMethod,view, request.getUrl().toString());
                    if (response != null) {
                        return response;
                    } else {
                        return super.shouldInterceptRequest(view, request);
                    }
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    WebResourceResponse response = WebViewUtils.getInstance(getContext()).shouldOverrideInterceptRequest(mFileUtilsMethod,view, url);
                    if (response != null) {
                        return response;
                    } else {
                        return super.shouldInterceptRequest(view, url);
                    }
                }
            });
        //}
    }



    public class JsInteration {

        @JavascriptInterface
        public void changeMsgpage(final String url){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) getActivity()).changeMsgpage();
                    Log.d("showToast", "changeMsgpage--------"+url);
                    //getWebView().loadUrl("javascript:contactsGetChatInfo('" + url + "')");
                    EventBus.getDefault().post(url);
                }
            });
        }
    }
}
