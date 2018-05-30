package com.uyun.hummer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.google.gson.Gson;
import com.uyun.hummer.R;
import com.uyun.hummer.adapter.MapRecycleViewAdapter;
import com.uyun.hummer.httputils.UserHttpMethods;
import com.uyun.hummer.model.bean.CameraDetailInfo;
import com.uyun.hummer.model.bean.CameraLayoutInfo;
import com.uyun.hummer.utils.ExceptionHandle;
import com.uyun.hummer.utils.GPSUtil;
import com.uyun.hummer.utils.Globe;
import com.uyun.hummer.utils.PreferenceUtils;
import com.uyun.hummer.utils.overlayutil.PoiOverlay;
import com.uyun.hummer.view.CustomToast;
import com.uyun.hummer.view.MapBottomPopWin;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;


/**
 * 演示poi搜索功能
 */
public class PoiSearchDemo extends FragmentActivity implements
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener ,View.OnClickListener{

    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private BaiduMap mBaiduMap = null;
    private List<String> suggest;
    /**
     * 搜索关键字输入窗口
     */
    private EditText editCity = null;
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;

    LatLng center = new LatLng(39.92235, 116.380338);
    int radius = 100;
    LatLng southwest = new LatLng( 39.92235, 116.380338 );
    LatLng northeast = new LatLng( 39.947246, 116.414977);
    LatLngBounds searchbound = new LatLngBounds.Builder().include(southwest).include(northeast).build();

    int searchType = 0;  // 搜索的类型，在显示时区分
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private ArrayList<LatLng> latlngs = new ArrayList<LatLng>();
    private ArrayList<String> latList = new ArrayList<String>();
    private ArrayList<String> lngList = new ArrayList<String>();
    BitmapDescriptor bdA;
    Drawable mCounterDrawable;


    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private BitmapDescriptor bdB;
    private SDKReceiver mReceiver;
    private boolean getCameraList = false;
    private ArrayList<CameraLayoutInfo.Cameralist> mList;
    private ArrayList<CameraDetailInfo.CameraList> detailList;
    private CameraDetailInfo mCameraDetailInfo;
    private double FirstLat = 30.34156583390;
    private double FirstLon = 120.1348334474;

    private LinearLayout alert_layout;
    private LinearLayout location_layout;
    private int showError = 1;
    private AutoCompleteTextView searchAuto;
    private ArrayAdapter<String> searchAdapter = null;
    private List<String> searchStr;
    private RecyclerView mRecyclerView;
    private RelativeLayout map;
    private LinearLayout bottom_lin;
    private LinearLayout back;
    private Bitmap bitmap;
    private ImageView alert_img;
    private boolean isAlert = false;
    public static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alert_layout:

                if(showError == 0 ){
                    showError = 1;
                    alert_img.setImageResource(R.drawable.alert);
                    isAlert = false;
                    if (mList.size()==0){
                        return;
                    }
                    mBaiduMap.clear();
                    latList.clear();
                    lngList.clear();
                    markers.clear();
                    for(int i = 0; i< mList.size(); i++){
                        Log.i("yunli","getCameraLists ======== " + mList.get(i).getXjs());
                        initOverlay(mList.get(i).getLatitude(), mList.get(i).getLongitude(),mList.get(i).getXjs(),mList.get(i).getStatus());
                    }

                }else {
                    showError = 0;
                    alert_img.setImageResource(R.drawable.alert_02);
                    isAlert = true;
                    if (mList.size()==0){
                        return;
                    }
                    mBaiduMap.clear();
                    latList.clear();
                    lngList.clear();
                    markers.clear();
                    for(int i = 0; i< mList.size(); i++){
                        Log.i("yunli","getCameraLists ======== " + mList.get(i).getXjs());
                        if(!mList.get(i).getStatus().equals("1")){
                            initOverlay(mList.get(i).getLatitude(), mList.get(i).getLongitude(),mList.get(i).getXjs(),mList.get(i).getStatus());
                        }
                    }
                }

                break;
            case R.id.location_layout:
                initMapStatus();
                getCameraLayout(mCurrentLon+"",mCurrentLat+"");
                break;


            case R.id.map2:
                bottom_lin.setVisibility(View.GONE);
                Log.i("123","mapmapmapmapmap");
                break;
            case R.id.back:
                finish();
                break;

        }
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.info_revise:
                    int index = PreferenceUtils.getInt(PoiSearchDemo.this,PreferenceUtils.CAMERA_INDEX,0);
                    CameraDetailInfo.CameraList cameraList = mCameraDetailInfo.getCameraList().get(index);

                    Intent intent = new Intent();
                    intent.setClass(PoiSearchDemo.this, CameraDetailActivity.class);
                    intent.putExtra(Globe.CAMERA_DETAIL_LIST, new Gson().toJson(cameraList));
                    startActivity(intent);
                    break;
                case R.id.route_text:
                    CameraDetailInfo.CameraList list = mCameraDetailInfo.getCameraList().get(0);
                    //导航"latitude":"30.296020850855","longitude":"120.12263393094"
                    //如果已安装,
                    Log.i("yunli","list.getJD() ======== " +list.getJD()+"-----"+list.getWD());

                    double[] gps = GPSUtil.bd09_To_gps84(Double.parseDouble(list.getWD()),Double.parseDouble(list.getJD()));
                    if(isAvilible(PoiSearchDemo.this,"com.baidu.BaiduMap")) {//传入指定应用包名
                        //Toast.makeText(PoiSearchDemo.this, "即将用百度地图打开导航", Toast.LENGTH_LONG).show();
                        Uri mUri = Uri.parse("geo:"+gps[0]+","+gps[1]+"?q="+list.getSBMC());
                        Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
                        startActivity(mIntent);
                    }else if(isAvilible(PoiSearchDemo.this,"com.autonavi.minimap")){
                        //Toast.makeText(PoiSearchDemo.this, "即将用高德地图打开导航", Toast.LENGTH_LONG).show();
                        Uri mUri = Uri.parse("geo:"+gps[0]+","+gps[1]+"?q="+list.getSBMC());
                        Intent intent2 = new Intent("android.intent.action.VIEW",mUri);
                        startActivity(intent2);
                    }else {
                        Toast.makeText(PoiSearchDemo.this, "请安装第三方地图方可导航", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case R.id.showlist_text:
                    Intent intent2 = new Intent();
                    intent2.setClass(PoiSearchDemo.this, CameraListActivity.class);
                    intent2.putExtra(Globe.CAMERA_DETAIL_INFO, new Gson().toJson(mCameraDetailInfo));
                    startActivity(intent2);

                    break;
            }
        }
    };

    /**
     * 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName：应用包名
     * @return
     */
    private boolean isAvilible(Context context, String packageName){
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            /*LocationClientOption option = new LocationClientOption();
            option.setIsNeedAddress(true);
            mLocClient.setLocOption(option);*/
            /*Log.i("yunli","onReceiveLocationcitystr ========----------- " +location.getCity());
            Log.i("yunli","onReceiveLocationcitystr ========----------- " +location.getCity().toString());*/
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            MarkerOptions ooB = new MarkerOptions().position(new LatLng(mCurrentLat,mCurrentLon)).icon(bdB)
                    .perspective(false).anchor(0.5f, 0.5f).zIndex(7);
            mBaiduMap.addOverlay(ooB);
            if(mCurrentLat!=0.0&&mCurrentLon!=0.0&&!getCameraList){
                Log.i("yunli","onReceiveLocation ======== " +mCurrentLon+"----"+mCurrentLat);
                getCameraLayout(mCurrentLon+"",mCurrentLat+"");
                //getCameraDetail("111.976944","27.72611");
                getCameraList = true;
                initMapStatus();
            }
        }
    }
    private void registerSDK(){
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(PoiSearchDemo.this,"key 验证出错! 错误码 :" + intent.getIntExtra
                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        +  " ; 请在 AndroidManifest.xml 文件中检查 key 设置",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        registerSDK();
        setContentView(R.layout.activity_poisearch);
        back = (LinearLayout) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        TextView titText = (TextView) findViewById(R.id.tit_text);
        titText.setText(R.string.map_service);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        alert_layout = (LinearLayout)findViewById(R.id.alert_layout);
        alert_layout.setOnClickListener(this);
        alert_img = (ImageView) findViewById(R.id.alert_img);
        location_layout = (LinearLayout)findViewById(R.id.location_layout);
        location_layout.setOnClickListener(this);
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        map = (RelativeLayout)findViewById(R.id.map2);
        map.setOnClickListener(this);
        bottom_lin = (LinearLayout) findViewById(R.id.bottom_lin);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        editCity = (EditText) findViewById(R.id.city);
        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);
        searchAuto = (AutoCompleteTextView) findViewById(R.id.searchAuto);
        searchAuto.setDropDownVerticalOffset(20);
        searchAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        searchAuto.setAdapter(searchAdapter);
        searchAuto.setThreshold(1);
        mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
                                                   .findFragmentById(R.id.map))).getBaiduMap();

        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
        bdB = BitmapDescriptorFactory
                .fromResource(R.drawable.map_local);
        mBaiduMap.setMyLocationEnabled(true);
        //initMapStatus();
        MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(new LatLng(FirstLat,FirstLon));
        mBaiduMap.animateMapStatus(u4);
        searchAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchAuto.getWindowToken(), 0);
                }
                Log.i("123","detailList========");
                Log.i("123","detailList========"+detailList.size());
                CameraDetailInfo.CameraList list = detailList.get(i);
                double lat = Double.parseDouble(detailList.get(i).getWD());
                double lon = Double.parseDouble(detailList.get(i).getJD());
                MapStatus mMapStatus = new MapStatus.Builder().zoom(16).build();//此处写16是显示200米的比例尺;
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
               // MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(new LatLng(27.72611,111.976944));
                MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(new LatLng(lat,lon));
                mBaiduMap.animateMapStatus(u4);
                getCameraLayout(detailList.get(i).getJD(),detailList.get(i).getWD());

                //getCameraLayout("111.976944","27.72611");
            }
        });
        searchAuto.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                getCameraName(searchAuto.getText().toString());
                /*mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(editCity.getText().toString()));*/
            }
        });
        /*initOverlay(39.963175, 116.400244,88,"3");
        initOverlay(39.942821, 116.369199,9,"2");
        initOverlay(39.939723, 116.425541,8,"1");
        initOverlay(39.906965, 116.401394,1,"2");*/
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(int i=0;i<markers.size();i++){
                    if(markers.get(i)== marker){
                        Log.i("123","i========"+i);
                        getCameraDetail(lngList.get(i),latList.get(i));
                        //getCameraDetail("111.976944","27.72611");
                    }
                }
                return true;
            }
        });

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
            }
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
            }
            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                LatLng ptCenter = mBaiduMap.getMapStatus().target;
                /*mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(ptCenter));*/
                getCameraLayout(ptCenter.longitude+"",ptCenter.latitude+"");
                Log.i("yunli","getCameraLists ======== " + ptCenter.latitude);
                Log.i("yunli","getCameraLists ======== " + ptCenter.longitude);
            }
            @Override
            public void onMapStatusChange(MapStatus arg0) {
            }
        });
    }

    private void  initRecyclerView(ArrayList<CameraDetailInfo.CameraList> datas){

        bottom_lin.setVisibility(View.VISIBLE);

        Button info_revise = (Button) findViewById(R.id.info_revise);
        TextView route_text = (TextView) findViewById(R.id.route_text);
        TextView showlist_text = (TextView) findViewById(R.id.showlist_text);
        info_revise.setOnClickListener(this);
        route_text.setOnClickListener(this);
        showlist_text.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);

        LinearLayoutManager ms= new LinearLayoutManager(PoiSearchDemo.this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(ms);
        MapRecycleViewAdapter adapter=new MapRecycleViewAdapter(datas,PoiSearchDemo.this);
        mRecyclerView.setAdapter(adapter);

    }
    private void initMapStatus(){
        MapStatus mMapStatus = new MapStatus.Builder().zoom(16).build();//此处写16是显示200米的比例尺;
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mMapStatus));
        MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(new LatLng(mCurrentLat,mCurrentLon));
        mBaiduMap.animateMapStatus(u4);
    }
    private void getCameraLayout(String lnt,String lat){
        String apikey = PreferenceUtils.getString(PoiSearchDemo.this, PreferenceUtils.APIKEYS, "");
        Subscriber<CameraLayoutInfo> cameraLayoutInfo = new Subscriber<CameraLayoutInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.i("yunli","onError ======== " + e);
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    CustomToast.showToast(PoiSearchDemo.this, R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);

                }
            }
            @Override
            public void onNext(CameraLayoutInfo cameraLayoutInfo) {
                mList = cameraLayoutInfo.getCameraList();
                Log.i("yunli","mList.size() ======== " + mList.size());
                latList.clear();
                lngList.clear();
                markers.clear();
                mBaiduMap.clear();
                if(!isAlert){
                    for(int i = 0; i< mList.size(); i++){

                        initOverlay(mList.get(i).getLatitude(), mList.get(i).getLongitude(),mList.get(i).getXjs(),mList.get(i).getStatus());
                    }
                }else {
                    for(int i = 0; i< mList.size(); i++){
                        if(!mList.get(i).getStatus().equals("1")){
                            initOverlay(mList.get(i).getLatitude(), mList.get(i).getLongitude(),mList.get(i).getXjs(),mList.get(i).getStatus());
                        }
                    }
                }


            }
        };
        UserHttpMethods.getInstance(PoiSearchDemo.this.getApplicationContext()).getCameraLayout(cameraLayoutInfo,apikey,lnt,lat);
    }


    private void getCameraDetail(String jd,String wd){
        if(PoiSearchDemo.this == null){
            return;
        }
        String apikey = PreferenceUtils.getString(PoiSearchDemo.this, PreferenceUtils.APIKEYS, "");
        Subscriber<CameraDetailInfo> cameraDetailInfo = new Subscriber<CameraDetailInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.i("yunli","onError =getCameraDetail======= " + e);
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    CustomToast.showToast(PoiSearchDemo.this, R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
                }
            }
            @Override
            public void onNext(CameraDetailInfo cameraDetailInfo) {


                mCameraDetailInfo = cameraDetailInfo;
                MapBottomPopWin popWin = new MapBottomPopWin(PoiSearchDemo.this, cameraDetailInfo.getCameraList(),onClickListener);
                popWin.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                //initRecyclerView(cameraDetailInfo.getCameraList());
                Log.i("yunli","getCameraLists ======== " + cameraDetailInfo.getCameraList().get(0).getSBMC());
            }
        };
        UserHttpMethods.getInstance(PoiSearchDemo.this.getApplicationContext()).getCameraDetail(cameraDetailInfo,apikey,jd,wd);
    }
    private void getCameraName(String name){
        if(PoiSearchDemo.this == null){
            return;
        }
        String apikey = PreferenceUtils.getString(PoiSearchDemo.this, PreferenceUtils.APIKEYS, "");
        Subscriber<CameraDetailInfo> cameraDetailInfo = new Subscriber<CameraDetailInfo>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.i("yunli","onError ======== " + e);
                ExceptionHandle.ResponeThrowable throwable = null;
                if(e instanceof Exception){
                    throwable = ExceptionHandle.handleException(e);
                }else {
                    throwable = new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN);
                }
                if (Globe.isInMainActivity) {
                    CustomToast.showToast(PoiSearchDemo.this, R.drawable.warning, throwable.message, Toast.LENGTH_SHORT);
                }
            }
            @Override
            public void onNext(CameraDetailInfo cameraDetailInfo) {
                searchStr = new ArrayList<String>();
                detailList = cameraDetailInfo.getCameraList();
                Log.i("yunli","mList.size() ======== " + detailList.size());
                for(int i = 0; i< detailList.size(); i++){
                    searchStr.add(detailList.get(i).getSBMC());
                    //initOverlay(mList.get(i).getWD(), mList.get(i).getJD,mList.get(i).getXjs(),mList.get(i).getStatus());
                }
                searchAdapter = new ArrayAdapter<String>(PoiSearchDemo.this, android.R.layout.simple_dropdown_item_1line, searchStr);
                searchAuto.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();


                Log.i("yunli","searchStr ======== " + searchStr);
            }
        };
        UserHttpMethods.getInstance(PoiSearchDemo.this.getApplicationContext()).getCameraName(cameraDetailInfo,apikey,name);
    }
    public void initOverlay(String lat,String lon,int num,String type) {
        LatLng llA = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        //latlngs.add(llA);
        latList.add(lat);
        lngList.add(lon);
        bdA = BitmapDescriptorFactory.fromBitmap(getNumberBitmap(num,type));
        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA);
        markers.add((Marker) (mBaiduMap.addOverlay(ooA)));
        bitmap.recycle();   // 回收bitmap的内存
        bitmap = null;
    }
    public Bitmap getNumberBitmap(int number,String type) {
        switch( type ) {
            case "1":
                mCounterDrawable = getResources().getDrawable(R.drawable.gj_intact);
                break;
            case "2":
                mCounterDrawable = getResources().getDrawable(R.drawable.gj_fault);
                break;
            case "3":
                mCounterDrawable = getResources().getDrawable(R.drawable.gj_serious);
                break;
        }
        Bitmap bitmapDrawables = ((BitmapDrawable) mCounterDrawable).getBitmap();
        int bitmapX = bitmapDrawables.getWidth();
        int bitmapY = bitmapDrawables.getHeight();
        int padding = bitmapX/10;
        bitmap = Bitmap.createBitmap(bitmapX, bitmapY, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        paint.setDither(true);// 防抖动
        paint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯
        Rect src = new Rect(0, 0, bitmapX, bitmapX);
        Rect dst = new Rect(0, 0, bitmapX, bitmapX);
        canvas.drawBitmap(((BitmapDrawable) mCounterDrawable).getBitmap(), src, dst, paint);
        // draw background
       /* paint.setColor(Color.WHITE);
        canvas.drawOval(rect, paint);
        paint.setColor(Color.RED);
        canvas.drawOval(new RectF(padding, padding, rect.width() - padding, rect.height() - padding), paint);*/
        // draw text
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setTextSize(bitmapX * 0.3f);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float baseline = (float)((rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2.15);
        canvas.drawText(number+"", rect.centerX(), baseline, paint);
        return bitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 响应城市内搜索按钮点击事件
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        searchType = 1;
        String citystr = editCity.getText().toString();
        String keystr = keyWorldsView.getText().toString();
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(citystr).keyword(keystr).pageNum(loadIndex));
    }




    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     * @param result
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(PoiSearchDemo.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();

            switch( searchType ) {
                /*case 2:
                    showNearbyArea(center, radius);
                    break;
                case 3:
                    showBound(searchbound);
                    break;*/
                default:
                    break;
            }

            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(PoiSearchDemo.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
     * @param result
     */
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(PoiSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            /*Toast.makeText(PoiSearchDemo.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();*/
            Toast.makeText(PoiSearchDemo.this, result.getLocation().latitude+"---"+result.getLocation().longitude, Toast.LENGTH_SHORT)
                    .show();
            Log.i("jingweidu",result.getLocation().longitude+"---"+result.getLocation().latitude);
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    /**
     * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
     * @param res
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }    
        suggest = new ArrayList<String>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }
        sugAdapter = new ArrayAdapter<String>(PoiSearchDemo.this, android.R.layout.simple_dropdown_item_1line, suggest);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }


}
