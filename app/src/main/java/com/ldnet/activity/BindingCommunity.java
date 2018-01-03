package com.ldnet.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
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
import com.ldnet.entities.HouseRent;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
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
    // 初始化事件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initLocation();
        acountService=new AcountService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void initLocation(){
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
                // 当前保存的用户数据
                User user = UserInformation.getUserInfo();
                bindCommunity(community.ID);

            }
        });
    }

    //获取省
    public void Provinces() {
        // 请求的URL
        String url = Services.mHost + "API/Common/GetProvince";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e("asdsdasd", "获取省" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Areas>>() {
                                    }.getType();
                                    mProvinces = gson.fromJson(jsonObject.getString("Obj"), listType);

                                  //  mllAddressFeedback.setVisibility(View.VISIBLE);
                                    initProvinces();
                                    mAdapter_Provinces.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //绑定小区
    public void bindCommunity(final String communityId) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("CommunityId", communityId);
        extras.put("ResidentId", UserInformation.getUserInfo().getUserId());
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + Services.json(extras) + Services.TOKEN;
        // 请求的URL
        final String url = Services.mHost + "API/Resident/SetBingCommunity";
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("CommunityId", communityId)
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .build()
                .execute(new DataCallBack(this) {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        showProgressDialog();
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e("asdsdasd", "绑定小区" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    try {
                                        if (!mFromCommunity) {
                                            //重新登录获取登录信息
                                            getData(UserInformation.getUserInfo().getUserPhone(), UserInformation.getUserInfo().getUserPassword());
                                        } else {
                                            HashMap<String, String> extras = new HashMap<String, String>();
                                            extras.put("LEFT", "LEFT");
                                            gotoActivityAndFinish(com.ldnet.activity.me.Community.class.getName(), extras);
                                        }
                                        acountService.setIntegralTip(new Handler(),url);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    //绑定失败
                                    HashMap<String, String> extras = null;
                                    try {
                                        extras = new HashMap<String, String>();
                                        extras.put("LEFT", "LEFT");
                                        gotoActivityAndFinish(com.ldnet.activity.me.Community.class.getName(), extras);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                  showToast(jsonObject.getString("Message"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getData(final String phone,final  String psd) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        final String url = Services.mHost + "API/Account/Logon";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("UserName", phone);
        extras.put("Password", psd);
        extras.put("PlatForm", "Android");
        Services.json(extras);
        String md5 = phone + aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("UserName", phone)
                .addParams("Password", psd)
                .addParams("PlatForm", "Android")
                .build().execute(new DataCallBack(this) {

            @Override
            public String parseNetworkResponse(Response response, int id) throws IOException {
                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    List<String> cookies = headers.values("Set-Cookie");
                    Log.d("aaaaaaaaa", "onResponse: " + cookies.get(0));
                    CookieInformation.setCookieInfo("cookies", cookies.get(0));
                }
                return super.parseNetworkResponse(response, id);
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String s, int i) {
                Log.d("asdsdasd", "--" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                    if (json.getBoolean("Status")) {
                        if (jsonObject.getBoolean("Valid")) {
                            //判断是否存在物业
                            Gson gson = new Gson();
                            User user = gson.fromJson(jsonObject.getString("Obj"), User.class);
                            UserInformation.setUserInfo(user);
                            User newUser = UserInformation.getUserInfo();
                            if (!TextUtils.isEmpty(newUser.PropertyId)) {
                                HashMap<String, String> extras = new HashMap<String, String>();
                                extras.put("IsFromRegister", "true");
                                extras.put("COMMUNITY_ID", newUser.CommunityId);
                                extras.put("LEFT", "LEFT");
                                gotoActivityAndFinish(BindingHouse.class.getName(), extras);
                            } else {
                                HashMap<String, String> extras = new HashMap<String, String>();
                                extras.put("LEFT", "LEFT");
                                gotoActivityAndFinish(MainActivity.class.getName(), extras);
                            }
                            acountService.setIntegralTip(new Handler(),url);
                        } else {
                            showToast(jsonObject.getString("Message"));
                        }
                    } else {
                        showToast(jsonObject.getString("Message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取小区数据
    public void searchCommunities(final String name,final  String lat,final  String lng,final  boolean searchType, final boolean type) {
        // 请求的URL
        String url = Services.mHost + "API/Property/GetAllCommunityUid";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Name", name);
        extras.put("Lat", lat);
        extras.put("Lng", lng);
        extras.put("SearchType", String.valueOf(searchType));
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("Name", name)
                .addParams("Lat", lat)
                .addParams("Lng", lng)
                .addParams("SearchType", String.valueOf(searchType))
                .build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e("amapLocation", "获取小区信息参数" + lat + "---" + lng);
                        Log.e("amapLocation", "获取小区信息" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    showToast(jsonObject.getString("Message"));
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Community>>() {
                                    }.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    if (type) {//关键字搜索
                                        Log.e("amapLocation", "关键字搜索" + s);
                                        if (datas != null && datas.size() != 0) {
                                            communities.clear();
                                            communities.addAll(datas);
                                            lv_binding_community.setVisibility(View.VISIBLE);
                                            progressbar_loading.setVisibility(View.GONE);
                                            tv_search_null_word.setVisibility(View.GONE);
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            Log.e("amapLocation", "关键字搜索空");
                                            tv_search_null_word.setVisibility(View.VISIBLE);
                                            lv_binding_community.setVisibility(View.GONE);
                                            progressbar_loading.setVisibility(View.GONE);
                                        }
                                    } else {   //定位自动搜索
                                        Log.e("amapLocation", "定位自动搜索" + s);
                                        if (datas != null) {
                                            communities.clear();
                                            communities.addAll(datas);
                                            progressbar_loading.setVisibility(View.GONE);
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            showToast(R.string.manually_entered_village);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取市根据省、直辖市的ID
    public void Cities(final Integer provinceId) {
        // 请求的URL
        String url = Services.mHost + "API/Common/GetCity/%s";
        url = String.format(url, provinceId);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    showToast(jsonObject.getString("Message"));
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Areas>>() {
                                    }.getType();
                                    Areas = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    if (Areas != null) {
                                        mCities.clear();
                                        mCities.addAll(Areas);
                                        mAdapter_Cities.notifyDataSetChanged();
                                        if (mCities.size() > 0 && mCities != null) {
                                            //改变区
                                            Areas areas1 = mCities.get(0);
                                            if (areas1 != null) {
                                                Areas(areas1.Id);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取区域根据城市ID
    public void Areas(final Integer cityId) {
        // 请求的URL
        String url = Services.mHost + "API/Common/GetArea/%s";
        url = String.format(url, cityId);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.d("asdsdasd", "2222222222" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    showToast(jsonObject.getString("Message"));
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Areas>>() {
                                    }.getType();
                                    Areas1 = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    if (Areas1 != null) {
                                        mAreas.clear();
                                        mAreas.addAll(Areas1);
                                        mAdapter_Areas.notifyDataSetChanged();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //意见反馈
    public void Feedback(final String content) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        try {
            // 请求的URL
            String url = Services.mHost + "API/File/AppFeedback";
            //获取当前应用的版本号
            GSApplication application = GSApplication.getInstance();
            String appVersion = null;
            appVersion = application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionName;
            HashMap<String, String> extras = new HashMap<>();
            extras.put("Content", content);
            extras.put("AppVersion", appVersion);
            extras.put("AppSystem", "android");
            extras.put("AppSystemVersion", Build.MODEL + " - Android " + Build.VERSION.RELEASE);
            extras.put("AppType", "业主App");
            extras.put("UserId", UserInformation.getUserInfo().getUserId());
            extras.put("UserName", UserInformation.getUserInfo().getUserName() + "[" + UserInformation.getUserInfo().getUserPhone() + "]");
            Services.json(extras);
            String md5 = UserInformation.getUserInfo().getUserPhone() +
                    aa + aa1 + Services.json(extras) + Services.TOKEN;
            OkHttpUtils.post().url(url)
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32(md5))
                    .addParams("Content", content)
                    .addParams("AppVersion", appVersion)
                    .addParams("AppSystem", "android")
                    .addParams("AppSystemVersion", Build.MODEL + " - Android " + Build.VERSION.RELEASE)
                    .addParams("AppType", "业主App")
                    .addParams("UserId", UserInformation.getUserInfo().getUserId())
                    .addParams("UserName", UserInformation.getUserInfo().getUserName() + "[" + UserInformation.getUserInfo().getUserPhone() + "]")
                    .build()
                    .execute(new DataCallBack(this) {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            super.onError(call, e, i);
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            Log.d("asdsdasd", "111111111" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (json.getBoolean("Status")) {
                                    if (jsonObject.getBoolean("Valid")) {
                                        showToast(getResources().getString(R.string.activity_me_feedback_success));
                                        try {
                                            HashMap<String, String> extras = new HashMap<String, String>();
                                            extras.put("LEFT", "LEFT");
                                            gotoActivityAndFinish(com.ldnet.activity.me.Community.class.getName(), null);
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
        String keywords = et_binding_community_search.getText().toString()
                .trim();

        poiSearchByLocation = true;

        if (longitude != null && latitude != null) {
            searchCommunities(keywords, String.valueOf(latitude), String.valueOf(longitude), true, true);
        } else {
            searchCommunities(keywords, "0.0", "0.0", true, true);
        }
    }

    //        ----------------------------提交地址反馈-----------------
    //初始化省、直辖市信息
    private void initProvinces() {

        //将可选内容与ArrayAdapter连接起来
        mAdapter_Provinces = new ArrayAdapter(this, R.layout.dropdown_check_item, mProvinces);
        //设置下拉列表的风格
        mAdapter_Provinces.setDropDownViewResource(R.layout.dropdown_item);
        //将adapter 添加到spinner中
        sr_address_provinces.setAdapter(mAdapter_Provinces);
        //设置选择事件
        sr_address_provinces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //改变城市
                Areas areas = mProvinces.get(i);
                if (areas.Id != null) {
                    Cities(areas.Id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //初始化城市信息
    private void initCities() {
        mCities = new ArrayList<Areas>();
        //将可选内容与ArrayAdapter连接起来
        mAdapter_Cities = new ArrayAdapter(this, R.layout.dropdown_check_item, mCities);
        //设置下拉列表的风格
        mAdapter_Cities.setDropDownViewResource(R.layout.dropdown_item);
        //将adapter 添加到spinner中
        sr_address_cities.setAdapter(mAdapter_Cities);
        //设置选择事件
        sr_address_cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //改变区
                Areas areas = mCities.get(i);
                if (areas != null) {
                    Areas(areas.Id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //初始化区域信息
    private void initAreas() {
        mAreas = new ArrayList<Areas>();
        //将可选内容与ArrayAdapter连接起来
        mAdapter_Areas = new ArrayAdapter(this, R.layout.dropdown_check_item, mAreas);
        //设置下拉列表的风格
        mAdapter_Areas.setDropDownViewResource(R.layout.dropdown_item);
        //将adapter 添加到spinner中
        sr_address_areas.setAdapter(mAdapter_Areas);
    }
    //        ----------------------------提交地址反馈-----------------

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
                Feedback(pId + "-" + cId + "-" + aId + "-" + community);

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


    @Override
    protected void onStop() {
        super.onStop();

    }


    public void stopLocation(){
        mlocationClient.stopLocation();
        mlocationClient=null;
        mLocationOption=null;
    }



    Handler handlerLocal=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==111){
                AMapLocation aMapLocation=(AMapLocation) msg.obj;
                searchCommunities("1", String.valueOf(aMapLocation.getLatitude()),
                        String.valueOf(aMapLocation.getLongitude()), false, false);
                latitude = aMapLocation.getLatitude();
                longitude = aMapLocation.getLongitude();
            }
        }
    };
}
