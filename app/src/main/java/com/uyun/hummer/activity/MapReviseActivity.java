package com.uyun.hummer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
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
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.uyun.hummer.R;
import com.uyun.hummer.base.activity.BaseFragmentActivity;
import com.uyun.hummer.utils.overlayutil.PoiOverlay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liyun on 2017/12/14.
 */

public class MapReviseActivity extends BaseFragmentActivity implements OnGetGeoCoderResultListener,OnGetPoiSearchResultListener, OnGetSuggestionResultListener {
    private MapView mMapView ;
    private BaiduMap mBaiduMap;
    private Marker mMarkerB;
    private TextView text_lat,text_lon;
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private SDKReceiver mReceiver;
    private GeoCoder mSearch = null;
    private BitmapDescriptor bdB;
    private double FirstLat = 30.34156583390;
    private double FirstLon = 120.1348334474;
    private double lastLat;
    private double lastLon;
    private String name_title;
    private TextView text_name,text_address;


    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private List<String> suggest;
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;
    private String citystr;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maprevise);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);
        keyWorldsView.setDropDownVerticalOffset(20);
        /*mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map))).getBaiduMap();*/
        keyWorldsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(keyWorldsView.getWindowToken(), 0);
                }
                String keystr = keyWorldsView.getText().toString();
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(citystr).keyword(keystr).pageNum(loadIndex));
            }
        });
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {

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
                Log.i("yunli","citystr ========----------- " +citystr);
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(citystr));
            }
        });
        /*this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
            Intent intent = getIntent();
            String longitude = intent.getStringExtra("longitude");
            String latitude = intent.getStringExtra("latitude");
            FirstLat = Double.parseDouble(latitude);
            FirstLon = Double.parseDouble(longitude);

        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        registerSDK();
        //setContentView(R.layout.activity_baidumap);
        TextView tit_text = (TextView)findViewById(R.id.tit_text);
        tit_text.setText(R.string.update_lat_lon);
        text_name = (TextView)findViewById(R.id.text_name);
        text_address = (TextView)findViewById(R.id.text_address);
        //text_name.setText(name_title);
        LinearLayout back = (LinearLayout)findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button btn_sure = (Button)findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapReviseActivity.this, CameraDetailActivity.class);
                intent.putExtra("longitude", String.valueOf(lastLon));
                intent.putExtra("latitude", String.valueOf(lastLat));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Button resetmap = (Button)findViewById(R.id.btn_reset);
        resetmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerOptions ooB = new MarkerOptions().position(new LatLng(mCurrentLat,mCurrentLon)).icon(bdB)
                        .perspective(false).anchor(0.5f, 0.5f).zIndex(7);
                mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
                initMapStatus();
            }
        });
        bdB = BitmapDescriptorFactory
                .fromResource(R.drawable.map_local);
        text_lat = (TextView)findViewById(R.id.text_lat);
        text_lon = (TextView)findViewById(R.id.text_lon);

        mMapView = (MapView)findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
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

        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16);
        mBaiduMap.setMapStatus(msu);
        initMapStatus();
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
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(ptCenter));
            }
            @Override
            public void onMapStatusChange(MapStatus arg0) {
            }
        });
    }
    private void initMapStatus(){
        MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(new LatLng(FirstLat,FirstLon));
        mBaiduMap.animateMapStatus(u4);
        lastLat = FirstLat;
        lastLon = FirstLon;
        text_lat.setText(getString(FirstLat));
        text_lon.setText(getString(FirstLon));
    }
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }

            /*LocationClientOption option = new LocationClientOption();
            option.setIsNeedAddress(true);
            mLocClient.setLocOption(option);*/


            citystr = location.getCity();
            //Log.i("yunli","onReceiveLocationcitystr ========----------- " +citystr);
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            MarkerOptions ooB = new MarkerOptions().position(new LatLng(mCurrentLat,mCurrentLon)).icon(bdB)
                    .perspective(false).anchor(0.5f, 0.5f).zIndex(7);
            mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
        }
    }
    private String getLatString(double lat,double lon){

        BigDecimal b  =   new BigDecimal(lat);
        double Dlat = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        BigDecimal l  =   new BigDecimal(lon);
        double Dlon = l.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(Dlat));
        builder.append(", ");
        builder.append(String.valueOf(Dlon));
        return  builder.toString();
    }
    private String getString(double lat){

        BigDecimal b  =   new BigDecimal(lat);
        double Dlat = b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        return  String.valueOf(Dlat);
    }
    private void registerSDK(){
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(geoCodeResult.getLocation());
        mBaiduMap.animateMapStatus(status);
        lastLat = geoCodeResult.getLocation().latitude;
        lastLon = geoCodeResult.getLocation().longitude;
        text_name.setText(geoCodeResult.getAddress());
        text_lon.setText(getString(geoCodeResult.getLocation().longitude));
        text_lat.setText(getString(geoCodeResult.getLocation().latitude));
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapReviseActivity.this, "错误", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(result.getLocation());
        mBaiduMap.animateMapStatus(status);
        lastLat = result.getLocation().latitude;
        lastLon = result.getLocation().longitude;
        text_name.setText(result.getAddress());
        text_address.setText(result.getSematicDescription());
        //text_lat.setText(result.getBusinessCircle());
        //text_lat.setText(getLatString(result.getLocation().latitude,result.getLocation().longitude));
        text_lon.setText(getString(result.getLocation().longitude));
        text_lat.setText(getString(result.getLocation().latitude));
    }
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(MapReviseActivity.this,"key 验证出错! 错误码 :" + intent.getIntExtra
                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        +  " ; 请在 AndroidManifest.xml 文件中检查 key 设置",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
            }
        }
    }
    @Override
    protected void onDestroy() {
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        unregisterReceiver(mReceiver);
        bdB.recycle();

        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
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
        //String citystr = editCity.getText().toString();
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
            Toast.makeText(MapReviseActivity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MapReviseActivity.MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();


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
            Toast.makeText(MapReviseActivity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
     * @param result
     */
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapReviseActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(MapReviseActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
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
        sugAdapter = new ArrayAdapter<String>(MapReviseActivity.this, android.R.layout.simple_dropdown_item_1line, suggest);
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
