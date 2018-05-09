package com.example.birdguan.cownet;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.birdguan.cownet.utils.OkHttpManager;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import okhttp3.Request;

public class MapActivity extends AppCompatActivity {
    public float latitude;
    public float longtitude;
    private MapView mapView;
    private BaiduMap baiduMap;


    private BDLocation mCurrentBDLocation = null;
    private boolean isFirstLocate = true;
    private boolean isAllPermissionsRequested = false;

    public LocationClient mLocationClient = null;
    public MyLocationListener mLocationListener = new MyLocationListener();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isInerFlag = false;
        Intent intent = getIntent();
        isInerFlag = intent.getBooleanExtra("isInerFlag", false);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        final EditText terminal_numberView = (EditText) findViewById(R.id.terminal_number);
//        String terminal_number = terminal_numberView.getText().toString();
        String url_map = "http://106.15.53.134:60002/DogsManageSystem/appSensor/appSensorInfo";
        Map<String, String> paras = new HashMap< >();
        paras.put("terminal_number", "9405");
        OkHttpManager.postAsync(url_map, paras, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
            }

            @Override
            public void requestSuccess(String result) {
                Log.d("debug", result);
                try {
                    JSONTokener jsonTokener = new JSONTokener(result);
                    JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                    if (jsonObject.get("message").equals("01")) {
                        String position = jsonObject.get("coordinate").toString();
                        String[] position_ = position.split(",");
                        //特别注意：
                        //服务器返回的数据格式是（经度，维度）
                        latitude = Float.parseFloat(position_[1]);
                        longtitude = Float.parseFloat(position_[0]);
                        Log.d("debug","latitude: "+latitude+"longtitude: "+longtitude);



                    } else {
                        Toast.makeText(MapActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException ex) {
                    Toast.makeText(MapActivity.this, "解析失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_TITLE | actionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mLocationClient = new LocationClient(getApplicationContext());

        mapView = (MapView) findViewById(R.id.map_view);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mapView.showScaleControl(true);

        ActionBar bst= getActionBar();
        //“我的位置”按钮
        Button btnPos = findViewById(R.id.btnPos);
        btnPos.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if (mCurrentBDLocation != null) {
                                              LatLng latLng = new LatLng(mCurrentBDLocation.getLatitude(), mCurrentBDLocation.getLongitude());
                                              MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(latLng, 17f);
                                              baiduMap.animateMapStatus(update);
                                          }
                                      }
                                  }
        );


        Button button_locationQuery = findViewById(R.id.button_locationQuery);
        EditText editText_locationQuery = findViewById(R.id.editText_locationQuery);
        //主菜单进入的位置查询时，显示查询框和查询按钮
        if (!isInerFlag) {
            //查询按钮
            button_locationQuery.setVisibility(View.VISIBLE);
            button_locationQuery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            //终端号输入框
            editText_locationQuery.setVisibility(View.VISIBLE);
            editText_locationQuery.setHint("请输入终端号查询");

        }else{
            //查询一只牛的位置，输入框和查询按钮不可见
            button_locationQuery.setVisibility(View.GONE);
            editText_locationQuery.setVisibility(View.GONE);
        }

        RequestPermissions();


    }

    @TargetApi(23)
    private void RequestPermissions() {
        Log.d("debug", "RequestPermissions");
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            isAllPermissionsRequested = true;
            initLocation();
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setOpenGps(true);
        option.setScanSpan(5000);
        option.SetIgnoreCacheException(false);

        mLocationClient.setLocOption(option);
        //注册监听函数
        mLocationClient.registerLocationListener(mLocationListener);

        baiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
    }

    private void navigateTo(BDLocation location) {
        MyLocationData data = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        baiduMap.setMyLocationData(data);

        if (isFirstLocate) {
            // 第一个参数是纬度值，第二个参数是经度度值
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //百度地图缩放范围，限定在3-19之间，值越大地图显示的信息越精细
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(latLng, 17f);
            baiduMap.animateMapStatus(update);

            //防止多次调用animateMapStatus()方法，将地图移动到我们当前位置只需在程序第一次定位的时候调用一次。
            isFirstLocate = false;
        }

        String text = location.getAddrStr() + "," + location.getLocationDescribe();
        Log.d("debug", text);
//        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        Log.d("debug", "onStart");
        super.onStart();

        if (isAllPermissionsRequested) {
            baiduMap.setMyLocationEnabled(true);
            if (!mLocationClient.isStarted())
                mLocationClient.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        baiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //onRequestPermissionsResult()方法中，对权限申请结果进行逻辑判断。这里使用一个循环对每个权限进行判断，
        // 如果有任意一个权限被拒绝了，那么就会直接调用finish()方法关闭程序，只有当所有的权限被用户同意了，才会
        // initLocation()方法开始地理位置定位。
        Log.d("debug", "onRequestPermissionsResult");
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能正常使用！", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }

                    isAllPermissionsRequested = true;
                    initLocation();
                } else {
                    Toast.makeText(this, "发生未知错误！", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        public void onReceiveLocation(BDLocation location) {
            Log.d("debug", "LocType：" + location.getLocType() + ",纬度：" + location.getLatitude() + ",经度：" + location.getLongitude());

            mCurrentBDLocation = location;

            navigateTo(location);

            baiduMap.clear();

            //定义Maker坐标点
            //LatLng point1 = new LatLng(location.getLatitude() - 0.004, location.getLongitude() + 0.003);
            //getPosition();
            Log.d("debug","current postion of the dog: latitude" + latitude + ",longtitude" + longtitude);
            LatLng point1 = new LatLng(latitude, longtitude);
            //LatLng point1 = new LatLng(0, 0);
            // 构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
            // 构建MarkerOption，用于在地图上添加Marker
            Marker marker = null;
            OverlayOptions option1 = new MarkerOptions().position(point1).icon(bitmap);
            // 在地图上添加Marker，并显示
            marker = (Marker)baiduMap.addOverlay(option1);
            marker.setToTop();

//            LatLng pointText = new LatLng(point.latitude - 0.0001, point.longitude);
//            //构建文字Option对象，用于在地图上添加文字
//            OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(24).fontColor(0xFFFF00FF).text("小花花").rotate(30).position(point);
//            //在地图上添加该文字对象并显示
//            baiduMap.addOverlay(textOption);

            //创建InfoWindow展示的view
            TextView text = new TextView(getApplicationContext());
            text.setTextSize(14);
            text.setTextColor(0xfff07000);
            text.getPaint().setFakeBoldText(true);
            text.setText("菲菲");
            //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
            baiduMap.showInfoWindow(new InfoWindow(text, point1, 50));
        }
    }

}
