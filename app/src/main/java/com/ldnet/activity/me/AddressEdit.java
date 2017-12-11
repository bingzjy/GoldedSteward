package com.ldnet.activity.me;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.City;
import com.amap.api.maps.offlinemap.Province;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Areas;
import com.ldnet.entities.HouseRent;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AddressService;
import com.ldnet.service.BaseService;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.unionpay.mobile.android.global.a.s;
import static com.unionpay.mobile.android.pboctransaction.samsung.f.f;

public class AddressEdit extends BaseActionBarActivity {

    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private EditText et_address_contract;
    private EditText et_address_mobile;
    private EditText et_address_zipcode;
    private EditText et_address_details;
    private Button btn_address_confirm;
    private CheckBox chk_address_default;

    private com.ldnet.entities.Address mAddress;
    private Spinner sr_address_provinces;
    private Spinner sr_address_cities;
    private Spinner sr_address_areas;
    private List<Areas> mProvinces;
    private ArrayAdapter<Areas> mAdapter_Provinces;
    private List<Areas> mCities;
    private ArrayAdapter<Areas> mAdapter_Cities;
    private List<Areas> mAreas;
    private ArrayAdapter<Areas> mAdapter_Areas;
    private Boolean mFromOrderConfirm = false;

    private List<Areas> Areas;
    private List<Areas> Areas1;
    private List<Areas> Areas2;
    private  String addressId;
    private AddressService addressService;
    private String cityType="";
    private String areaType="";
    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_address_edit2);
        AppUtils.setupUI(findViewById(R.id.ll_address),this);

        addressService=new AddressService(this);

        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_address);

        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_address_confirm = (Button) findViewById(R.id.btn_address_confirm);
        //来自提交订单的标记
        String fromOrderConfirm = getIntent().getStringExtra("FROM_ORDER_CONFIRM");
        if (!TextUtils.isEmpty(fromOrderConfirm)) {
            mFromOrderConfirm = Boolean.valueOf(fromOrderConfirm);
        }
        et_address_contract = (EditText) findViewById(R.id.et_address_contract);
        et_address_mobile = (EditText) findViewById(R.id.et_address_mobile);
        et_address_zipcode = (EditText) findViewById(R.id.et_address_zipcode);
        //省
        sr_address_provinces = (Spinner) findViewById(R.id.sr_address_provinces);
        //市
        sr_address_cities = (Spinner) findViewById(R.id.sr_address_cities);
        //区
        sr_address_areas = (Spinner) findViewById(R.id.sr_address_areas);
        et_address_details = (EditText) findViewById(R.id.et_address_details);
        chk_address_default = (CheckBox) findViewById(R.id.chk_address_default);
        //初始化实体
        addressId = getIntent().getStringExtra("ADDRESS_ID");

        addressService.getProvinces(handlerProvinces);
        initEvent();
    }

    //初始化省、直辖市信息
    private void initProvinces() {
        mAdapter_Provinces = new ArrayAdapter(this, R.layout.dropdown_check_item, mProvinces);
        mAdapter_Provinces.setDropDownViewResource(R.layout.dropdown_item);
        sr_address_provinces.setAdapter(mAdapter_Provinces);
        sr_address_provinces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //改变城市
                Areas areas = mProvinces.get(i);
                if (areas.Id != null) {
                    cityType="1";
                    addressService.getCities(areas.Id,handlerCitys);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //初始化城市信息
    private void initCities() {
        mAdapter_Cities = new ArrayAdapter(this, R.layout.dropdown_check_item, mCities);
        mAdapter_Cities.setDropDownViewResource(R.layout.dropdown_item);
        sr_address_cities.setAdapter(mAdapter_Cities);
        sr_address_cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //改变区
                Areas areas = mCities.get(i);
                if (areas != null) {
                    areaType="2";
                    addressService.getAreas(areas.Id,handlerAreas);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //初始化区域信息
    private void initAreas() {
        mAdapter_Areas = new ArrayAdapter(this, R.layout.dropdown_check_item, mAreas);
        mAdapter_Areas.setDropDownViewResource(R.layout.dropdown_item);
        sr_address_areas.setAdapter(mAdapter_Areas);
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_address_confirm.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_back:
                    if (mFromOrderConfirm) {
                        finish();
                    } else {
                        gotoActivityAndFinish(Address.class.getName(), null);
                    }
                    break;
                case R.id.btn_address_confirm:
                    if (!isNull()){
                    String contract = et_address_contract.getText().toString().trim();
                    String mobile = et_address_mobile.getText().toString().trim();
                    String zipcode = et_address_zipcode.getText().toString().trim();
                    String details = et_address_details.getText().toString().trim();
                    Integer pId = ((Areas) sr_address_provinces.getSelectedItem()).Id;
                    Integer cId = ((Areas) sr_address_cities.getSelectedItem()).Id;
                    Integer aId = ((Areas) sr_address_areas.getSelectedItem()).Id;
                    Boolean isDefault = chk_address_default.isChecked();

                    if (mAddress != null) { //更新地址
                        addressService.updateAddress(mAddress.ID, contract, mobile, "", "", zipcode, isDefault, details, pId, cId, aId, handlerAddUpdateAddress);
                    } else {   //新增地址
                        addressService.addAddress(contract, mobile, "", "", zipcode, isDefault, details, pId, cId, aId, handlerAddUpdateAddress);
                    }
                    }
                    break;
                default:
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isNull(){
        if (Utility.editIsNull(et_address_contract)){
            showToast("请输入联系人");
            return true;
        }
        if (Utility.editIsNull(et_address_mobile)){
            showToast("请输入联系电话");
            return true;
        }
        if (Utility.editIsNull(et_address_details)){
            showToast("请输入详细地址");
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mFromOrderConfirm) {
                finish();
            } else {
                try {
                    gotoActivityAndFinish(Address.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    public void initAddressData() {
        et_address_contract.setText(mAddress.N);
        et_address_mobile.setText(mAddress.MP);
        et_address_zipcode.setText(mAddress.ZC);
        et_address_details.setText(mAddress.AD);
        chk_address_default.setChecked(mAddress.ISD);
        addressService.getCities(mAddress.PID,handlerCitys);
        cityType="0";
        areaType="0";
        addressService.getAreas(mAddress.CID,handlerAreas);

        //省，默认值
        for (Areas b : mProvinces) {
            if (b.Id.equals(mAddress.PID)) {
                sr_address_provinces.setSelection(mProvinces.indexOf(b));
                break;
            }
        }
    }


    Handler handlerAddUpdateAddress = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    try {
                        if (mFromOrderConfirm) {
                            finish();
                        } else {
                            gotoActivityAndFinish(Address.class.getName(), null);
                        }
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



    Handler handlerGetAddress = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    mAddress=(com.ldnet.entities.Address) msg.obj;
                    initAddressData();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


Handler handlerProvinces=new Handler(){
    @Override
    public void handleMessage(android.os.Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case BaseService.DATA_SUCCESS:
                mProvinces=(List<com.ldnet.entities.Areas>) msg.obj;
                initProvinces();
                if (!TextUtils.isEmpty(addressId)) {
                    addressService.getAddressById(addressId,handlerGetAddress);
                } else {
                    User user = UserInformation.getUserInfo();
                    if (!TextUtils.isEmpty(user.UserName)) {
                        et_address_contract.setText(user.UserName);
                    }
                    et_address_mobile.setText(user.UserPhone);
                }
                break;
            case BaseService.DATA_FAILURE:
            case BaseService.DATA_REQUEST_ERROR:
                showToast(msg.obj.toString());
                break;
        }
    }
};


    Handler handlerCitys=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mCities=(List<com.ldnet.entities.Areas>) msg.obj;
                    initCities();

                    //市，默认值
                    if(mAddress != null){
                        for (Areas b : mCities) {
                            if (b.Id.equals(mAddress.CID)) {
                                sr_address_cities.setSelection(mCities.indexOf(b));
                                break;
                            }
                        }
                    }

                    if ("1".equals(cityType)) {
                        if (Areas != null) {
                            mCities.clear();
                            mCities.addAll(Areas);
                            mAdapter_Cities.notifyDataSetChanged();
                        }

                        //改变区
                        Areas areas1 = mCities.get(0);
                        if (areas1 != null) {
                            areaType="1";
                            addressService.getAreas(areas1.Id,handlerAreas);
                        }
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerAreas=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mAreas=(List<com.ldnet.entities.Areas>) msg.obj;
                    initAreas();
                    if(mAddress != null){
                        //区，默认值
                        for (Areas b : mAreas) {
                            if (b.Id.equals(mAddress.AID)) {
                                sr_address_areas.setSelection(mAreas.indexOf(b));
                                break;
                            }
                        }
                    }
                    if ("1".equals(areaType)) {
                        if (Areas1 != null) {
                            mAreas.clear();
                            mAreas.addAll(Areas1);
                            mAdapter_Areas.notifyDataSetChanged();
                        }
                    } else if ("2".equals(areaType)) {
                        if (Areas2 != null) {
                            mAreas.clear();
                            mAreas.addAll(Areas2);
                            mAdapter_Areas.notifyDataSetChanged();
                        }
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


}
