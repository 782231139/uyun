package com.uyun.hummer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
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
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.uyun.hummer.R;
import com.uyun.hummer.base.activity.BaseFragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by Liyun on 2017/12/14.
 */

public class BaiduMapActivity extends BaseFragmentActivity implements OnGetGeoCoderResultListener {
    private MapView mMapView ;
    private BaiduMap mBaiduMap;
    private Marker mMarkerB;
    private TextView text_lat;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        try {
            Intent intent = getIntent();
            String mapJson = intent.getStringExtra("mapJson");
            JSONObject jsonObject = new JSONObject(mapJson);
            name_title = jsonObject.getString("name");
            String longitude = jsonObject.getString("longitude");
            String latitude = jsonObject.getString("latitude");
            FirstLat = Double.parseDouble(latitude);
            FirstLon = Double.parseDouble(longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        registerSDK();
        setContentView(R.layout.activity_baidumap);
        TextView tit_text = (TextView)findViewById(R.id.tit_text);
        tit_text.setText(R.string.update_lat_lon);
        TextView text_name = (TextView)findViewById(R.id.text_name);
        text_name.setText(name_title);
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
                HashMap<String,String> paramsMap=new HashMap<>();
                paramsMap.put("name",name_title);
                paramsMap.put("longitude",String.valueOf(lastLon));
                paramsMap.put("latitude",String.valueOf(lastLat));
                Gson gson=new Gson();
                String mapJson = gson.toJson(paramsMap);
                Intent intent = new Intent(BaiduMapActivity.this, OtherWebviewActivity.class);
                intent.putExtra("mapJson", mapJson);
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
        mLocClient.setLocOption(option);
        mLocClient.start();

        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
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
        text_lat.setText(getLatString(FirstLat,FirstLon));
    }
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }
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
        text_lat.setText(getLatString(geoCodeResult.getLocation().latitude,geoCodeResult.getLocation().longitude));
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(BaiduMapActivity.this, "错误", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(result.getLocation());
        mBaiduMap.animateMapStatus(status);
        lastLat = result.getLocation().latitude;
        lastLon = result.getLocation().longitude;
        text_lat.setText(getLatString(result.getLocation().latitude,result.getLocation().longitude));

    }
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
               Toast.makeText(BaiduMapActivity.this,"key 验证出错! 错误码 :" + intent.getIntExtra
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

}
