package com.ldnet.activity.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.amap.api.navi.*;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.*;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.AMapUtils;
import com.ldnet.utility.TTSController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2015/11/6.
 */
public class YellowPages_Navi extends BaseActionBarActivity implements View.OnClickListener ,AMapNaviViewListener,AMapNaviListener{

    // 标题
    private TextView tv_page_title;

    //导航View
    private AMapNaviView mAMapNaviView;
    //是否为模拟导航
   private boolean mIsEmulatorNavi;
    private ImageButton mBtnBack;


//    protected NaviLatLng mEndLatlng = new NaviLatLng(22.652, 113.966);
//    //算路起点坐标
//    protected NaviLatLng mStartLatlng = new NaviLatLng(22.540332, 113.939961);
    protected NaviLatLng mEndLatlng ;
    //算路起点坐标
    protected NaviLatLng mStartLatlng;
    //存储算路起点的列表
    protected final List<NaviLatLng> sList = new ArrayList<>();
    //存储算路终点的列表
    protected final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();
    protected final List<NaviLatLng> mWayPointList = new ArrayList<>();

    private AMapNavi mAMapNavi;

    private com.ldnet.map.TTSController mTTSController;
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //布局
        setContentView(R.layout.activity_yellowpage_navi);

        //标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.navigation);

        //初始化视图
        initView(savedInstanceState);
    }

    //初始化视图
    public void initView(Bundle savedInstanceState) {
        //布局
        setContentView(R.layout.activity_yellowpage_navi);
        //返回
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            Double slat=bundle.getDouble("SLAT");
            Double slon=bundle.getDouble("SLON");
            Double elat=bundle.getDouble("ELAT");
            Double elon=bundle.getDouble("ELON");
            mEndLatlng=new NaviLatLng(elat,elon);
            mStartLatlng=new NaviLatLng(slat,slon);
            type=bundle.getString("TYPE","WALK");
            mIsEmulatorNavi = bundle.getBoolean(AMapUtils.ISEMULATOR, false);
        }
      //  processBundle(bundle);
        init(savedInstanceState);
    }


//    private void processBundle(Bundle bundle) {
//        if (bundle != null) {
//            mIsEmulatorNavi = bundle.getBoolean(AMapUtils.ISEMULATOR, false);
//        }
//    }

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    private void init(Bundle savedInstanceState) {
//获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听
        mAMapNavi.addAMapNaviListener(this);

        mAMapNaviView = (AMapNaviView) findViewById(R.id.amnv_yellowpages_navi);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        mTTSController= com.ldnet.map.TTSController.getInstance(this);
        mTTSController.init();
        mAMapNavi.addAMapNaviListener(mTTSController);
        mAMapNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);


        AMapNaviViewOptions options = new AMapNaviViewOptions();
        options.setTilt(0);
        mAMapNaviView.setViewOptions(options);

        //设置模拟导航的行车速度
        if (mIsEmulatorNavi) {
            // 设置模拟速度
            mAMapNavi.setEmulatorNaviSpeed(55);
            // 开启模拟导航
            mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode);
            //
            AMapNavi.getInstance(this).setAMapNaviListener(TTSController.getInstance(this));
        } else {
            // 开启实时导航
            AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
            //
            AMapNavi.getInstance(this).setAMapNaviListener(TTSController.getInstance(this));

        }
//        mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.amnv_yellowpages_navi);
//        mAmapAMapNaviView.onCreate(savedInstanceState);
//        mAmapAMapNaviView.setAMapNaviViewListener(this);
//        TTSController.getInstance(this).startSpeaking();
//        if (mIsEmulatorNavi) {
//            // 设置模拟速度
//            AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
//            // 开启模拟导航
//            AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);
//            //
//            AMapNavi.getInstance(this).setAMapNaviListener(TTSController.getInstance(this));
//        } else {
//            // 开启实时导航
//            AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
//            //
//            AMapNavi.getInstance(this).setAMapNaviListener(TTSController.getInstance(this));
//        }


    }

    //点击处理事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                Intent intent = new Intent(YellowPages_Navi.this, YellowPages_Map.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
                break;
        }
    }
    @Override
    public void onInitNaviSuccess() {
/**
 * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
 *
 * @congestion 躲避拥堵
 * @avoidhightspeed 不走高速
 * @cost 避免收费
 * @hightspeed 高速优先
 * @multipleroute 多路径
 *
 *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
 *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
 */
        if (type.equals("DRIVE")){
            int strategy = 0;
            try {
                //再次强调，最后一个参数为true时代表多路径，否则代表单路径
                strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mWayPointList.add(mEndLatlng);
            // 驾车算路
            mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
        }else if(type.equals("WALK")){
            mAMapNavi.calculateWalkRoute(mStartLatlng,mEndLatlng);
        }
    }



    @Override
    public void onCalculateRouteSuccess() {
        if(type.equals("WALK")){
            Log.e("map","导航--步行onCalculateRouteSuccess");
            mAMapNavi.startNavi(NaviType.GPS);
        }else{
            Log.e("map","导航---驾车onCalculateRouteSuccess");
            mAMapNavi.startNavi(NaviType.EMULATOR);
        }
    }

    //----------------生命周期------------------------
// ------------------------------生命周期方法---------------------------

    @Override
    public void onNaviCancel() {
        Intent intent = new Intent(YellowPages_Navi.this, YellowPages_Map.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    public void onInitNaviFailure() {

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAMapNaviView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAMapNaviView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
        AMapNavi.getInstance(this).stopNavi();
        mTTSController.stopSpeaking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        mTTSController.destroy();

    }

    @Override
    public void onNaviSetting() {

    }

}
