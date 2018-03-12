package com.ldnet.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.PoiItem;
//import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.me.SubmitSearchNullCommunity;
import com.ldnet.entities.Areas;
import com.ldnet.entities.Community;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.BindingService;
import com.ldnet.service.PropertyServeService;
import com.ldnet.utility.*;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * ***************************************************
 * 绑定社区，通过百度地图获取社区信息，然后绑定
 * **************************************************
 */
public class BindingCommunity extends BaseActionBarActivity implements
        AMapLocationListener, OnPoiSearchListener {

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    // 搜索按钮
    private Button btn_binding_community_search;
    // 搜索文本框
    private EditText et_binding_community_search;
    // 标题
    private TextView tv_page_title,tv_search_null_word;
    // 返回
    private ImageButton btn_back;
    // 小区信息列表
    private LinearLayout progressbar_loading;
    private ListView lv_binding_community;
    private ListViewAdapter<Community> mAdapter;
    // 搜索结果
    private List<Community> communities;
    // 高德地图
    private PoiSearch poiSearch;
    private String mCityCode;
    // 服务接口
    private Services services;

    private Boolean mFromCommunity = false;
    // 地址反馈
    private Spinner sr_address_provinces;
    private Spinner sr_address_cities;
    private Spinner sr_address_areas;
    private List<Areas> mProvinces;
    private List<Areas> mCities;
    private List<Areas> mAreas;
    private ArrayAdapter<Areas> mAdapter_Provinces;
    private ArrayAdapter<Areas> mAdapter_Cities;
    private ArrayAdapter<Areas> mAdapter_Areas;
    private Button btn_address_confirm;
    private EditText mEtAddressDetails;
    private LinearLayout mllAddressFeedback;
    private LinearLayout mLlNotCommunity;

    private List<Community> datas;
    private List<Areas> Areas;
    private List<Areas> Areas1;

    public AMapLocationClientOption mLocationOption = null;
    public AMapLocationClient mlocationClient = null;
    public Double latitude, longitude;
    public boolean poiSearchByLocation;
    private AcountService acountService;
    private PropertyServeService propertyService;
    private BindingService bindingService;
    private boolean keyWordsSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initLocation();
        acountService=new AcountService(this);
        propertyService = new PropertyServeService(this);
        bindingService = new BindingService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "绑定小区：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "绑定小区：" + this.getClass().getSimpleName());
    }

    //初始化定位
    public void initLocation() {
        mlocationClient = new AMapLocationClient(BindingCommunity.this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(BindingCommunity.this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(-1);
        mlocationClient.setLocationOption(mLocationOption);
        //设置定位参数
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mlocationClient.startLocation();
    }

    // 初始化视图
    public void initView() {
        // 初始化布局文件
        setContentView(R.layout.activity_binding_community);
        AppUtils.setupUI(findViewById(R.id.ll_binding_community), this);
        //接收参数
        String flag = getIntent().getStringExtra("FROM_COMMUNITY");
        if (!TextUtils.isEmpty(flag)) {
            mFromCommunity = Boolean.valueOf(flag);
        }

        //无搜索小区显示提示
      //  mLlNotCommunity = (LinearLayout) findViewById(R.id.ll_not_community);
        tv_search_null_word=(TextView)findViewById(R.id.tv_searchnull_word);
        tv_search_null_word.setOnClickListener(this);
        tv_search_null_word.setVisibility(View.GONE);

        progressbar_loading = (LinearLayout)findViewById(R.id.progressbar_loading);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        if (!mFromCommunity) {
            btn_back.setVisibility(View.GONE);
        }else{
            btn_back.setOnClickListener(this);
        }

        // 标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.activity_binding_community_title);

        // 搜索按钮btn_binding_community_search
        btn_binding_community_search = (Button) findViewById(R.id.btn_binding_community_search);
        btn_binding_community_search.setOnClickListener(this);
        et_binding_community_search = (EditText) findViewById(R.id.et_binding_community_search);
        // 小区信息列表
        lv_binding_community = (ListView) findViewById(R.id.lv_binding_community);
        communities = new ArrayList<Community>();
        // 绑定数据
        mAdapter = new ListViewAdapter<Community>(this,
                R.layout.item_community_search, communities) {
            @Override
            public void convert(ViewHolder holder, Community t) {
                ((TextView) holder.getView(R.id.tv_community_name))
                        .setText(t.Name);
                ((TextView) holder.getView(R.id.tv_community_address))
                        .setText(t.Address);
                ImageView img = holder.getView(R.id.img_property_certification);
                if (t.IsProperty) {
                    img.setVisibility(View.VISIBLE);
                } else {
                    img.setVisibility(View.GONE);
                }
            }
        };
        lv_binding_community.setAdapter(mAdapter);
        lv_binding_community.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 获取绑定的数据
                Community community = communities.get(position);
                showProgressDialog();
                bindingService.bindCommunity(community.ID, handlerBindCommunity);
            }
        });
    }


    // POI搜索
    private void poiSearch() {
        /* 隐藏软键盘 */
        InputMethodManager imm = (InputMethodManager) et_binding_community_search
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(
                    et_binding_community_search.getApplicationWindowToken(), 0);
        }

        // 清空数据出现加载
        progressbar_loading.setVisibility(View.VISIBLE);
        communities.clear();
        mAdapter.notifyDataSetChanged();

        // 整理关键字
        String keywords = et_binding_community_search.getText().toString().trim();

        poiSearchByLocation = true;
        keyWordsSearch = true;
        if (longitude != null && latitude != null) {
            bindingService.searchCommunities(keywords, String.valueOf(latitude), String.valueOf(longitude), true, handlerSearchCommunity);
        } else {
            bindingService.searchCommunities(keywords, "0.0", "0.0", true, handlerSearchCommunity);
        }
    }


    // 点击事件处理
    @Override
    public void onClick(View v) {
        super.onClick(v);

        // 分别处理按钮点击事件
        switch (v.getId()) {
            case R.id.btn_binding_community_search:
                poiSearch();
                break;
            case R.id.btn_back:
                if (mFromCommunity) {
                    try {
                        gotoActivityAndFinish(com.ldnet.activity.me.Community.class.getName(), null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_address_confirm://提交地址
                //输入地址反馈
                String pId = ((Areas) sr_address_provinces.getSelectedItem()).Name;
                String cId = ((Areas) sr_address_cities.getSelectedItem()).Name;
                String aId = ((Areas) sr_address_areas.getSelectedItem()).Name;
                String community = mEtAddressDetails.getText().toString().trim();
                propertyService.feedback(pId + "-" + cId + "-" + aId + "-" + community, handlerFeedback);
                break;
            case R.id.tv_searchnull_word:
                try {
                    gotoActivityAndFinish(SubmitSearchNullCommunity.class.getName(),null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }



    // 定位成功后的回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                Message msg=new Message();
                msg.what=111;
                msg.obj=amapLocation;
                handlerLocal.sendMessage(msg);
                stopLocation();

            } else {
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    /*
     * 高德地图，POI搜索事件监听
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 0) {
            if (result != null) {
                ArrayList<PoiItem> items = result.getPois();

                if (items != null) {
                    // 清除原来的数据
                    communities.clear();

                    for (PoiItem item : items) {
                        String title = item.getTitle();
                        if (title.contains("门）") || title.contains("门)")
                                || title.length() > 20) {
                            continue;
                        } else {
                            Community c = new Community();
                            c.Uid = item.getPoiId();
                            c.Name = item.getTitle();
                            if (!TextUtils.isEmpty(item.getSnippet())) {
                                c.Address = item.getSnippet();
                            } else {
                                c.Address = item.getProvinceName() + item.getCityName() + item.getAdName() + item.getTitle();
                            }
                            c.Distance = (double) item.getDistance();
                            c.Latitude = String.valueOf(item.getLatLonPoint()
                                    .getLatitude());
                            c.Longitude = String.valueOf(item.getLatLonPoint()
                                    .getLongitude());
                            c.Tel = item.getTel();
                            c.CityCode = item.getCityCode();
                            c.AreaId = item.getAdCode();

                            communities.add(c);
                        }
                    }
                    progressbar_loading.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    //重置用户信息
    Handler handlerGetData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    try {
                        if (TextUtils.isEmpty(UserInformation.getUserInfo().PropertyId)) {
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put("LEFT", "LEFT");
                            gotoActivityAndFinish(MainActivity.class.getName(), extras);
                        } else {
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put("IsFromRegister", "true");
                            extras.put("COMMUNITY_ID", UserInformation.getUserInfo().CommunityId);
                            extras.put("LEFT", "LEFT");
                            gotoActivityAndFinish(BindingHouse.class.getName(), extras);
                        }
                        acountService.setIntegralTip(new Handler(), Services.mHost + "API/Account/Logon");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //意见反馈
    Handler handlerFeedback = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast(getResources().getString(R.string.activity_me_feedback_success));
                    try {
                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put("LEFT", "LEFT");
                        gotoActivityAndFinish(com.ldnet.activity.me.Community.class.getName(), null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //定位回调处理
    Handler handlerLocal=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==111){
                AMapLocation aMapLocation=(AMapLocation) msg.obj;
                keyWordsSearch = false;
                bindingService.searchCommunities("1", String.valueOf(aMapLocation.getLatitude()),
                        String.valueOf(aMapLocation.getLongitude()), false, handlerSearchCommunity);
                latitude = aMapLocation.getLatitude();
                longitude = aMapLocation.getLongitude();
            }
        }
    };


    //绑定小区
    Handler handlerBindCommunity = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    try {
                        if (!mFromCommunity) {
                            //重新获取用户信息
                            acountService.getData(UserInformation.getUserInfo().getUserPhone(), UserInformation.getUserInfo().getUserPassword(), 0, handlerGetData);
                        } else {
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put("LEFT", "LEFT");
                            gotoActivityAndFinish(com.ldnet.activity.me.Community.class.getName(), extras);
                        }
                        acountService.setIntegralTip(new Handler(), Services.mHost + "API/Resident/SetBingCommunity");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:  //绑定失败
                    showToast(msg.obj.toString());
                    HashMap<String, String> extras = null;
                    try {
                        extras = new HashMap<String, String>();
                        extras.put("LEFT", "LEFT");
                        gotoActivityAndFinish(com.ldnet.activity.me.Community.class.getName(), extras);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };


    //搜索小区
    Handler handlerSearchCommunity = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    datas = (List<Community>) msg.obj;
                    if (keyWordsSearch) {   //通过关键词搜索
                        communities.clear();
                        communities.addAll(datas);
                        lv_binding_community.setVisibility(View.VISIBLE);
                        progressbar_loading.setVisibility(View.GONE);
                        tv_search_null_word.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    } else {            //地理位置自动搜索
                        communities.clear();
                        communities.addAll(datas);
                        progressbar_loading.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (keyWordsSearch) {
                        tv_search_null_word.setVisibility(View.VISIBLE);
                        lv_binding_community.setVisibility(View.GONE);
                        progressbar_loading.setVisibility(View.GONE);
                    } else {
                        showToast(R.string.manually_entered_village);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mFromCommunity) {
                try {
                    gotoActivityAndFinish(com.ldnet.activity.me.Community.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    public void stopLocation() {
        mlocationClient.stopLocation();
        mlocationClient = null;
        mLocationOption = null;
    }
}
