package com.ldnet.activity.me;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.commen.GSApplication;
import com.ldnet.activity.commen.Services;
import com.ldnet.entities.Areas;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zjy on 2017/5/23.
 */
public class SubmitSearchNullCommunity extends BaseActionBarActivity implements View.OnClickListener{
    private Spinner sr_address_provinces;
    private Spinner sr_address_cities;
    private Spinner sr_address_areas;
    private List<Areas> mProvinces;
    private List<Areas> mCities;
    private List<Areas> mAreas;
    private List<Areas> dataAreas;
    private ArrayAdapter<Areas> mAdapter_Provinces;
    private ArrayAdapter<Areas> mAdapter_Cities;
    private ArrayAdapter<Areas> mAdapter_Areas;
    private Button btn_address_confirm;
    private EditText mEtAddressDetails;
    private LinearLayout mllAddressFeedback;
    private LinearLayout mLlNotCommunity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_null_community);
        initView();
        initData();
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "提交地址搜索反馈：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "提交地址搜索反馈：" + this.getClass().getSimpleName());
    }


    private void initView(){
        sr_address_provinces=(Spinner)findViewById(R.id.sr_address_provinces);
        sr_address_cities=(Spinner)findViewById(R.id.sr_address_cities);
        sr_address_areas=(Spinner)findViewById(R.id.sr_address_areas);
        mEtAddressDetails=(EditText)findViewById(R.id.et_address_details);
        btn_address_confirm=(Button)findViewById(R.id.btn_address_confirm);
    }
    private void initData(){
        //获取省、直辖市
        mProvinces = new ArrayList<Areas>();
        Provinces();
        //市
        initCities();
        //区
        initAreas();
        //小区名
        mEtAddressDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(mEtAddressDetails.getText().toString().trim())) {
                    btn_address_confirm.setEnabled(true);
                } else {
                    btn_address_confirm.setEnabled(false);
                }
            }
        });
        btn_address_confirm = (Button) findViewById(R.id.btn_address_confirm);
        btn_address_confirm.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_address_confirm://提交地址
                //输入地址反馈
                String pId = ((Areas) sr_address_provinces.getSelectedItem()).Name;
                String cId = ((Areas) sr_address_cities.getSelectedItem()).Name;
                String aId = ((Areas) sr_address_areas.getSelectedItem()).Name;
                String community = mEtAddressDetails.getText().toString().trim();
                Feedback(pId + "-" + cId + "-" + aId + "-" + community);
                break;
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(Community.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
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


    //获取市根据省、直辖市的ID
    public void Cities(Integer provinceId) {
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
                                    dataAreas = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    if (dataAreas != null) {
                                        mCities.clear();
                                        mCities.addAll(dataAreas);
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
    public void Areas(Integer cityId) {
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
                        Log.e("asdsdasd", "获取区域信息" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    showToast(jsonObject.getString("Message"));
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Areas>>() {
                                    }.getType();
                                   List<Areas> Areas1 = gson.fromJson(jsonObject.getString("Obj"), listType);
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
    public void Feedback(String content) {
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
                                            gotoActivityAndFinish(Community.class.getName(), null);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                try {
                    gotoActivityAndFinish(Community.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}