package com.ldnet.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
//import com.amap.api.location.LocationManagerProxy;
//import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.*;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.*;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.ldnet.activity.commen.Constant;
import com.ldnet.goldensteward.R;
import com.ldnet.map.MyDrivingMyRouteOverlay;
import com.ldnet.map.MyWalkMyRouteOverlay;
import com.ldnet.map.NewDrivingRouteOverlay;
import com.ldnet.utility.AMapUtils;


import java.util.ArrayList;

/**
 * Created by Alex on 2015/11/6.
 */
public class YellowPages_Map extends Activity implements
        View.OnClickListener, AMapNaviListener, RouteSearch.OnRouteSearchListener, AMapLocationListener {

    // 标题
    private TextView tv_page_title;
    private ImageButton mBtnBack;

    //高德定位

    // 地图和导航资源
    private MapView mMapView;
    private RadioGroup rdg_is_drive;
    private RadioButton radioButtonDrive,radioButtonWalk;
    private Button btn_start_navigate;
    private AMap mAMap;
    private AMapNavi mAMapNavi;

    //起始点、终点坐标
    private NaviLatLng mNaviStart;
    private NaviLatLng mNaviEnd;
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

    // 规划线路
    private RouteOverLay mRouteOverLay;

    // 是否驾车和是否计算成功的标志
    private boolean mIsDriveMode=true;
    private boolean mIsCalculateRouteSuccess = false;
    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mlocationClient = null;
    public LatLonPoint latLonPointEnd, latLonPointStart;
    public RouteSearch routeSearch;
    public RouteSearch.FromAndTo fromAndTo;
    public AMapLocation amapLocation;
    public Double latitude, longitude;


    public YellowPages_Map() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局文件
        setContentView(R.layout.activity_yellowpage_map);

        //标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.path_rogramming);


        //返回
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        mMapView = (MapView) findViewById(R.id.mv_yellowpages_map);
        mMapView.onCreate(savedInstanceState);
        //初始化地图
        initView();


        //商家地理位置坐标
        String Latitude = getIntent().getStringExtra("LATITUDE");
        String Longitude = getIntent().getStringExtra("LONGITUDE");

        latLonPointEnd = new LatLonPoint(Double.valueOf(Latitude), Double.valueOf(Longitude));
    }


    //初始化地图
    public void initView() {
        //初始化地图实例
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        //设置地图的中心点和大小
        mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latLonPointEnd.getLatitude(), latLonPointEnd.getLongitude())));
                mAMap.moveCamera(CameraUpdateFactory.zoomTo(14));
            }
        });

        //路线规划
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);

        //初始化定位
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(5000);
        mLocationOption.setOnceLocation(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();


        //导航页面按钮控件
        rdg_is_drive = (RadioGroup) findViewById(R.id.rdg_is_drive);
        rdg_is_drive.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rbtn_type_drive:
                        mIsDriveMode = true;
                        ((RadioButton) radioGroup.findViewById(R.id.rbtn_type_drive)).setChecked(true);
                        ((RadioButton) radioGroup.findViewById(R.id.rbtn_type_step)).setChecked(false);
                        calculateRoute();
                        break;
                    case R.id.rbtn_type_step:
                        mIsDriveMode = false;
                        ((RadioButton) radioGroup.findViewById(R.id.rbtn_type_drive)).setChecked(false);
                        ((RadioButton) radioGroup.findViewById(R.id.rbtn_type_step)).setChecked(true);
                        calculateRoute();
                        break;
                    default:
                        break;
                }
            }
        });
        btn_start_navigate = (Button) findViewById(R.id.btn_start_navigate);
        btn_start_navigate.setOnClickListener(this);
    }



    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //当前用户所在位置
        latLonPointStart = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        fromAndTo = new RouteSearch.FromAndTo(latLonPointStart, latLonPointEnd);
        //计算路径
        calculateRoute();
    }

    //点击按钮事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
//                Intent intent_yellowpage = new Intent(YellowPages_Map.this, YellowPageTabActivity.class);
//                intent_yellowpage.putExtra("YELLOW_PAGE_SORT_ID", "8f1f1e4092784199bbec0229e1cca9b0");
//                intent_yellowpage.putExtra("YELLOW_PAGE_SORT_NAME", getResources().getString(R.string.fragment_home_yellow_pages));
//                intent_yellowpage.putExtra("flag", "yellow");
//                startActivity(intent_yellowpage);
//                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                finish();
                break;
            case R.id.btn_start_navigate:
                if (mIsCalculateRouteSuccess) {
                    Intent gpsIntent = new Intent(YellowPages_Map.this,
                            YellowPages_Navi.class);
                    Bundle bundle = new Bundle();
                    if(mIsDriveMode){
                        bundle.putString("TYPE","DRIVE");
                    }else{
                        bundle.putString("TYPE","WALK");
                    }
                    bundle.putDouble("SLAT",latLonPointStart.getLatitude());
                    bundle.putDouble("SLON",latLonPointStart.getLongitude());
                    bundle.putDouble("ELAT",latLonPointEnd.getLatitude());
                    bundle.putDouble("ELON",latLonPointEnd.getLongitude());
                    bundle.putBoolean(AMapUtils.ISEMULATOR, false);
                    bundle.putInt(AMapUtils.ACTIVITYINDEX, AMapUtils.SIMPLEROUTENAVI);
                    gpsIntent.putExtras(bundle);
                    gpsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(gpsIntent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                } else {
                    com.ldnet.utility.Toast.makeText(this, R.string.first_path_rogramming_second_navigation, 1000).show();
                }

                break;
            default:
                break;
        }
    }


    //计算路径
    private void calculateRoute() {
        if (mIsDriveMode && fromAndTo != null) {
            RouteSearch.DriveRouteQuery query=new RouteSearch.DriveRouteQuery(fromAndTo,RouteSearch.DRIVING_SINGLE_DEFAULT,null,null,"");
            routeSearch.calculateDriveRouteAsyn(query);
            Log.e("map", "驾车路线计算");
        } else if (mIsDriveMode == false && fromAndTo != null) {
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            routeSearch.calculateWalkRouteAsyn(query);
            Log.e("map", "步行路线计算");
        }
    }


    //------------------生命周期重写函数---------------------------

    @Override
    public void onResume() {
        super.onResume();
        mlocationClient.startLocation();
        mMapView.onResume();

    }


    //驾车路线规划结果
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        if (i == 1000) {
            mIsCalculateRouteSuccess=true;
            mAMap.clear();// 清理地图上的所有覆盖物
            DriveRouteResult result;
            if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                if (driveRouteResult.getPaths().size() > 0) {
                    result = driveRouteResult;
                    final DrivePath drivePath = result.getPaths()
                            .get(0);
                    NewDrivingRouteOverlay myDrivingMyRouteOverlay=new NewDrivingRouteOverlay(
                            this,mAMap,drivePath,result.getStartPos(),result.getTargetPos(),null
                    );
                    Log.e("map", "驾车路线-----结果22222"+drivePath);
                    myDrivingMyRouteOverlay.removeFromMap();
                    myDrivingMyRouteOverlay.setNodeIconVisibility(false);
                    myDrivingMyRouteOverlay.setIsColorfulline(true);
                    myDrivingMyRouteOverlay.addToMap();
                    myDrivingMyRouteOverlay.zoomToSpan();
                    Log.e("map", "驾车路线-----结果333333333" + result.getPaths());
                }
            } else {
                Toast.makeText(YellowPages_Map.this, "获取路线结果为空", Toast.LENGTH_LONG).show();
            }
        } else {
            mIsCalculateRouteSuccess=false;
            Toast.makeText(YellowPages_Map.this, "获取路线结果失败,请手动开启位置权限", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        mAMap.clear();// 清理地图上的所有覆盖物
        WalkRouteResult result;
        if (i == 1000) {
            mIsCalculateRouteSuccess=true;
            if (walkRouteResult != null && walkRouteResult.getPaths() != null) {
                if (walkRouteResult.getPaths().size() > 0) {
                    result = walkRouteResult;
                    final WalkPath walkPath = result.getPaths()
                            .get(0);
                    MyWalkMyRouteOverlay walkRouteOverlay = new MyWalkMyRouteOverlay(
                            this, mAMap, walkPath,
                            result.getStartPos(),
                            result.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.setNodeIconVisibility(false);
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                } else {
                    Toast.makeText(YellowPages_Map.this, "获取路线结果为空", Toast.LENGTH_LONG).show();
                }
            } else {
                mIsCalculateRouteSuccess=false;
                Toast.makeText(YellowPages_Map.this, "获取路线结果失败,请手动开启位置权限", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mlocationClient.stopLocation();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mlocationClient=null;
        mLocationOption=null;
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteSuccess() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }


    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


}
