package com.uyun.hummer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uyun.hummer.base.activity.BaseFragmentActivity;
import com.uyun.hummer.base.fragment.BaseFragment;
import com.uyun.hummer.cache.LocalBitmapCache;
import com.uyun.hummer.fragment.ContactsFragment;
import com.uyun.hummer.fragment.LabelFragment;
import com.uyun.hummer.fragment.MineFragment;
import com.uyun.hummer.fragment.MsgFragment;
import com.uyun.hummer.fragment.OperateFragment;
import com.uyun.hummer.httputils.UserHttpMethods;
import com.uyun.hummer.model.bean.VersionInfo;
import com.uyun.hummer.model.bean.VersionUrlInfo;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.INetworkListner;
import com.uyun.hummer.utils.MyService;
import com.uyun.hummer.utils.NetWorkUtils;
import com.uyun.hummer.utils.NetworkManager;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.utils.SystemUtils;
import com.uyun.hummer.utils.update.UpdateService;
import com.uyun.hummer.view.ImportantVersionPopWin;
import com.uyun.hummer.view.NewestVersionPopWin;
import com.uyun.hummer.view.NoScrollViewPager;
import com.uyun.hummer.view.SmallVersionPopWin;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Subscriber;


public class MainActivity extends BaseFragmentActivity implements INetworkListner{
    public Fragment[] mFragments;
    private TabLayout mTablayout;
    private NoScrollViewPager mViewPager;
    private static String[] ChatTitle;
    private TypedArray mTypedArray;
    public String realHost = null;
    private int isCollect = 1;
    private NetworkManager networkManager;
    private int MINE_FRAGMENT_INDEX = 4;
    private ImportantVersionPopWin importantVersionPopWin;
    private SmallVersionPopWin smallVersionPopWin;
    private NewestVersionPopWin newestVersionPopWin;
    private String updateContent = "";
    private ArrayList<String> updateContentList = new ArrayList<String>();
    private String nativeVersion;
    private String serverVersion;
    private boolean versionUpdate = false;
    private String apkDownloadUrl;
    private String jsonStr = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceUtils.put(MainActivity.this, PreferenceUtils.VERSION_UPDATE, versionUpdate);
        networkManager = NetworkManager.getInstance(this);
        networkManager.registerDateTransReceiver();
        networkManager.register(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Globe.isInMainActivity = true;
        Globe.SERVER_HOST = PreferenceUtils.getString(this, PreferenceUtils.SERVICE_IP, Globe.SERVER_HOST);
        realHost = PreferenceUtils.getString(this,Globe.SERVER_HOST,"");
        if(TextUtils.isEmpty(realHost)) {
            if(NetWorkUtils.isNetworkConnected(this)) {
                initRealHost();
            }
        }else{
            initData();
        }

    }

    private Handler popupHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    importantVersionPopWin = new ImportantVersionPopWin(MainActivity.this, onClickListener, updateContent);

                    importantVersionPopWin.showAtLocation(MainActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                    break;
            }
        }

    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.update_now:
                    Intent intent = new Intent(MainActivity.this, UpdateService.class);
                    intent.putExtra("apkUrl",apkDownloadUrl);
                    startService(intent);
                    importantVersionPopWin.dismiss();
                    break;
            }
        }
    };
    public void getVersion() {
        Subscriber<VersionUrlInfo> subscriber = new Subscriber<VersionUrlInfo>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.i("yunli","verison ======== " + e);
            }

            @Override
            public void onNext(VersionUrlInfo versionInfo) {
                Log.i("yunli","getAndroidChildVersionUrl ======== " + versionInfo.getData().getAndroidMajorVersion());
                Log.i("yunli","getMajorAndroidVersion ======== " + versionInfo.getData().getAndroidChildVersionUrl());
                downloadAudioFile(versionInfo.getData().getAndroidChildVersionUrl());
            }
        };
        UserHttpMethods.getInstance(getApplicationContext()).getVersion(subscriber);
    }
    private void updateVersion(String json){
        VersionInfo versionInfo= new Gson().fromJson(json, VersionInfo.class);

        nativeVersion = SystemUtils.getVersionName(MainActivity.this);
        serverVersion = versionInfo.getAndroidVersion();
        apkDownloadUrl = versionInfo.getAndroidDownloadUrl();
        versionUpdate = isSmallVersion(nativeVersion,serverVersion);
        PreferenceUtils.put(MainActivity.this, PreferenceUtils.VERSION_UPDATE, versionUpdate);
        PreferenceUtils.put(MainActivity.this, PreferenceUtils.APK_DOWNLOAD_URL, apkDownloadUrl);
        boolean isImportant = isImportantVersion(nativeVersion,serverVersion);
        updateContentList = versionInfo.getAndroidUpdateContent();
        for(int i =0;i<updateContentList.size();i++){
            if(i == updateContentList.size()-1){
                updateContent += updateContentList.get(i);
            }else {
                updateContent += updateContentList.get(i)+"\n";
            }
        }
        if(isImportant){
            popupHandler.sendEmptyMessageDelayed(0, 1000);
        }
        mineNotify(versionUpdate);
    }
    private void downloadAudioFile(String downloadUrl) {
        Subscriber<Response<ResponseBody>> subscriber = new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Response<ResponseBody> responseBodyResponse) {
                try {
                    BufferedInputStream bufis = new BufferedInputStream(responseBodyResponse.body().byteStream());
                    getInjectionJS(bufis);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        UserHttpMethods.getInstance(getApplicationContext()).downloadAudioFile(subscriber, downloadUrl);
    }
    private void getInjectionJS(BufferedInputStream in){
        try {
            byte buff[] = new byte[1024];
            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
            do {
                int numread = in.read(buff);
                if (numread <= 0) {
                    break;
                }
                fromFile.write(buff, 0, numread);
            } while (true);
            jsonStr = fromFile.toString();
            updateVersion(jsonStr);
            Log.i("jsonStr","jsonStr----------"+jsonStr);
            //Log.i("onReceivedError","injectionJS@@@@@@@@"+injectionJS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private boolean isImportantVersion(String version, String version2){
        if(version.equals(version2)){
            return false;
        }
        String subStr = version.substring(2,4);
        String subStr2 = version2.substring(2,4);
        if(Integer.parseInt(subStr2)>Integer.parseInt(subStr)){
            return true;
        }else {
            return false;
        }
    }
    private boolean isSmallVersion(String version, String version2){
        if(version.equals(version2)){
            return false;
        }
        if(isImportantVersion(version,version2)){
            return true;
        }
        String subStr = version.substring(5,version.length());
        String subStr2 = version2.substring(5,version2.length());
        Log.i("yunli","subStr======== " + subStr+"--");
        if(Integer.parseInt(subStr2)>Integer.parseInt(subStr)){
            return true;
        }else {
            return false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(BaseFragment.isClickNotify&&mViewPager.getCurrentItem()!=0){
            mViewPager.setCurrentItem(0);
            BaseFragment.isClickNotify = false;
        }
        Intent intent = new Intent(MainActivity.this, MyService.class);
        startService(intent);
    }
    public void changeMsgpage(){
        mViewPager.setCurrentItem(0, false);
    }
    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    private void initData() {
        ChatTitle = getResources().getStringArray(R.array.main);
        mTypedArray =getResources().obtainTypedArray(R.array.main_icon);
        mFragments = new Fragment[5];
        mTablayout= (TabLayout) findViewById(R.id.tabLayout);
        mViewPager= (NoScrollViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new ChatViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(ChatTitle.length);
        mTablayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTablayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTablayout.getTabAt(i);
            tab.setCustomView(getTabView(i));
            tab.getCustomView();
        }
        mTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getVersion();
    }
    public void  showTablayout(boolean show){
        if(show){
            mTablayout.setVisibility(View.VISIBLE);
            mViewPager.setNoScroll(false);
        }else {
            mTablayout.setVisibility(View.GONE);
            mViewPager.setNoScroll(true);
        }
    }
    public void setInput(int isBottomUpspring){
        if(isBottomUpspring == 0){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    public void atMeNotify(boolean isVisibility){
        TabLayout.Tab tab = mTablayout.getTabAt(1);
        tab.getCustomView();
        ImageView notify= (ImageView) tab.getCustomView().findViewById(R.id.notify);
        if(isVisibility){
            notify.setVisibility(View.VISIBLE);
        }else {
            notify.setVisibility(View.GONE);
        }
    }
    public void mineNotify(boolean isVisibility){
        TabLayout.Tab tab = mTablayout.getTabAt(4);
        tab.getCustomView();
        ImageView notify= (ImageView) tab.getCustomView().findViewById(R.id.notify);
        if(isVisibility){
            notify.setVisibility(View.VISIBLE);
        }else {
            notify.setVisibility(View.GONE);
        }
    }
    public void msgNotify(String msg){
        TabLayout.Tab tab = mTablayout.getTabAt(0);
        tab.getCustomView();
        TextView notify_msg= (TextView) tab.getCustomView().findViewById(R.id.notify_msg);
        if(msg.equals("0")){
            notify_msg.setVisibility(View.GONE);
        }else {
            notify_msg.setText(msg);
            notify_msg.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBitmapCache.getInstance(this).fluchCache();
        Globe.isInMainActivity = false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserHttpMethods.getInstance(this).cancelAllUrl();
        networkManager.unregisterDateTransReceiver();
        networkManager.unregister(this);
    }

    private void initRealHost(){
        Subscriber<Response<ResponseBody>> subscriber = new Subscriber<Response<ResponseBody>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(Response<ResponseBody> responseBodyResponse) {
                try {
                    realHost = responseBodyResponse.raw().headers().get("Location");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(realHost != null){
                    PreferenceUtils.put(MainActivity.this,Globe.SERVER_HOST,realHost);
                }

                initData();
            }
        };
        UserHttpMethods.getInstance(this).getRealHost(subscriber);
    }
    public View getTabView(int position){
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_item_view, null);
        TextView tv= (TextView) view.findViewById(R.id.textView);
        tv.setText(ChatTitle[position]);
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        img.setImageResource(mTypedArray.getResourceId(position,0));
        return view;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            final int index  = mTablayout.getSelectedTabPosition();
            WebView webview = getWebView();
            /*if(index == 2){
                com.tencent.smtt.sdk.WebView x5webview = ((BaseX5Fragment) mFragments[index]).getWebView();
                x5webview.loadUrl("javascript:_goBack()");
                return true;
            }*/
            if(webview!=null&&index!=MINE_FRAGMENT_INDEX){
                webview.loadUrl("javascript:_goBack()");
                return true;
            }else {
                homeBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void homeBack(){
        Intent i= new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }
    private WebView getWebView(){
        int index  = mTablayout.getSelectedTabPosition();
        if(mFragments[index] instanceof BaseFragment) {
            WebView view = ((BaseFragment) mFragments[index]).getWebView();
            return view;
        }else{
            return null;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onNetworkChanged(boolean available) {
        if(mFragments != null) {
            if (available) {
                if (mFragments[4] != null && mFragments[4] instanceof MineFragment) {
                    ((MineFragment) mFragments[4]).verifyUserData();
                }
                if (mFragments[0] != null && mFragments[0] instanceof MsgFragment) {
                    ((MsgFragment) mFragments[0]).networkChange();
                }
                Log.i("yunli","NetworkChanged ======== "+available);
            }
        }
    }

    class ChatViewPagerAdapter extends FragmentPagerAdapter{

        public ChatViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    mFragments[0] = new MsgFragment();
                    break;
                case 1:
                    mFragments[1] = new LabelFragment();
                    break;
                case 2:
                    mFragments[2] = new OperateFragment();
                    break;
                case 3:
                    mFragments[3] = new ContactsFragment();
                    break;
                case 4:
                    mFragments[4] = new MineFragment();
                    break;
            }
            return mFragments[position];
        }
        @Override
        public int getCount() {
            return ChatTitle.length;
        }
    }
}
