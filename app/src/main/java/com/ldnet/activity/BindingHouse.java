package com.ldnet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.base.LoadingDialog;
import com.ldnet.activity.commen.GetData;
import com.ldnet.activity.me.Community;
import com.ldnet.activity.me.VisitorPsd;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.BindingService;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import okhttp3.Call;
import okhttp3.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ldnet.goldensteward.R.id.aaa;
import static com.ldnet.utility.Services.IntegralTip;
import static com.ldnet.utility.Utility.backgroundAlpaha;

/**
 * ***************************************************
 * 绑定房产，绑定用户的房屋信息
 * **************************************************
 * 逻辑：判断条件：是否已经绑定过（从当前用户的房产信息检索当前即将绑定的房屋）、是否是业主（从业主电话列表检索当前用户电话）
 * 执行：绑定、设置门禁关系、设置当前房屋、设置积分
 *
 */
public class BindingHouse extends BaseActionBarActivity {

    private Spinner sr_binding_house_build;
    private List<Building> mBuild_datas;
    private ArrayAdapter<Building> adapter_build;
    private Spinner sr_binding_house_unit;
    private List<Building> mUnit_datas;
    private ArrayAdapter<Building> adapter_unit;
    private Spinner sr_binding_house_room;
    private List<Building> mRoom_datas;
    private ArrayAdapter<Building> adapter_room;
    private String mCommunityId;
    private Boolean IsFromRegister;
    private String mHouseId;
    // 标题
    private TextView tv_page_title;
    // 返回
    private ImageButton btn_back;
    // 确定
    private Button btn_binding_house;
    private String mCOMMUNITY_NAME;
    private List<Building> buildings;
    private List<EntranceGuard> entranceGuards;
    private BindingService service;
    private AcountService acountService;

    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_house);

        service = new BindingService(this);
        acountService=new AcountService(this);
        //得到传递的小区ID
        IsFromRegister = Boolean.valueOf(getIntent().getStringExtra("IsFromRegister"));
        mCommunityId = getIntent().getStringExtra("COMMUNITY_ID");
        mCOMMUNITY_NAME = getIntent().getStringExtra("COMMUNITY_NAME");

        initView();
        initEvent();
        //楼栋信息
        initBuildings();
        //单元信息
        initUnits();
        //房屋信息
        initHouses();
        //获取楼栋信息
        service.Buildings(mCommunityId, handlerBuilding);
        showProgressDialog();
    }

    private void initView(){
        //标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.fragment_me_community_btn_plus);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //确定
        btn_binding_house = (Button) findViewById(R.id.btn_binding_house);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    if (!IsFromRegister) {
                        gotoActivityAndFinish(Community.class.getName(), null);
                    } else {
                        gotoActivityAndFinish(MainActivity.class.getName(), null);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_binding_house:
                if (mHouseId != null && mHouseId.equals("0")) {
                    service.RemoveHouse(mCommunityId, "0", handlerRemoveHouse);
                }
                showProgressDialog();
                service.MyProperties(mCommunityId, mHouseId, handlerMyProperties);
                break;
            default:
                break;
        }
    }


    Handler handlerBuilding = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if ((List<Building>) msg.obj != null && ((List<Building>) msg.obj).size() > 0) {
                        buildings = (List<Building>) msg.obj;
                        mBuild_datas.addAll(buildings);
                        adapter_build.notifyDataSetChanged();
                    } else {
                        btn_binding_house.setEnabled(false);
                        showToast(R.string.perfect_property_information);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerUnit = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if ((List<Building>) msg.obj != null && ((List<Building>) msg.obj).size() > 0) {
                        mUnit_datas.clear();
                        mUnit_datas.addAll((List<Building>) msg.obj);
                        adapter_unit.notifyDataSetChanged();
                    }
                    buildings.clear();
                    if (mUnit_datas.get(0) != null && !TextUtils.isEmpty(mUnit_datas.get(0).Id)) {
                        service.Houses(mUnit_datas.get(0).getId(), handlerHouse);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerHouse = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if ((List<Building>) msg.obj != null && ((List<Building>) msg.obj).size() > 0) {
                        mRoom_datas.clear();
                        mRoom_datas.addAll((List<Building>) msg.obj);
                        adapter_room.notifyDataSetChanged();
                    }
                    buildings.clear();
                    if (mRoom_datas.get(0) != null && !TextUtils.isEmpty(mRoom_datas.get(0).Id)) {
                        mHouseId = mRoom_datas.get(0).Id;
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //获取我的小区和房产,判断用户是否绑定该房屋
    Handler handlerMyProperties = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (msg.obj != null && ((List<MyProperties>) msg.obj).size() > 0) {
                        boolean havebind = false;
                        List<MyProperties> myProperties = (List<MyProperties>) msg.obj;

                        for (MyProperties data : myProperties) {
                            if (data.getCommunityId().equals(mCommunityId)) {
                                if (data.getRooms() != null && data.getRooms().size() > 0) {
                                    for (Rooms room : data.getRooms()) {
                                        if (room.getRoomId().equals(mHouseId)) {
                                            havebind = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (!havebind) {
                            service.getEntranceGuard(mHouseId, handlerGetEntranceGuard);
                        } else {
                            closeProgressDialog();
                            showToast(getString(R.string.havebind));
                        }
                    } else {
                        closeProgressDialog();
                        showToast(R.string.network_error);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //获取房屋的业主列表
    Handler handlerGetEntranceGuard = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (msg.obj != null && ((List<EntranceGuard>) msg.obj).size() > 0) {
                        boolean isOwner = false;
                        entranceGuards = (List<EntranceGuard>) msg.obj;
                        for (int j = 0; j < entranceGuards.size(); j++) {
                            //如果当前用户属于业主（不含家属和租户）列表中的，那么直接绑定
                            if (UserInformation.getUserInfo().getUserPhone().equals(entranceGuards.get(j).getValue())) {
                                isOwner = true;
                                break;
                            }
                        }

                        if (isOwner) {
                            service.BindingHouse(mCommunityId, mHouseId, handlerBindingHouse);
                        } else {
                            closeProgressDialog();
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put("ROOM_ID", mHouseId);
                            extras.put("CLASS_FROM", "BindingHouse");
                            extras.put("COMMUNITY_ID", mCommunityId);
                            extras.put("COMMUNITY_NAME", mCOMMUNITY_NAME);
                            try {
                                //非业主的话先验证（申请访客密码是也需要验证）
                                gotoActivityAndFinish(VisitorPsd.class.getName(), extras);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    closeProgressDialog();
                    service.getPropertyTelphone(mCommunityId,handlerGetPropertyPhone);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }

        }
    };

    //绑定房子
    Handler handlerBindingHouse = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (msg.obj != null) {
                        showToast(msg.obj.toString());
                        service.postEGBind("","",mHouseId,handlerEGBind);
                        service.SetCurrentInforamtion(mCommunityId, mHouseId,new Handler());
                        acountService.setIntegralTip(new Handler(),Services.mHost + "API/Resident/ResidentBindRoom");
                        try {
                            if (!IsFromRegister) {
                                HashMap<String, String> extras = new HashMap<String, String>();
                                extras.put("LEFT", "LEFT");
                                gotoActivityAndFinish(Community.class.getName(), extras);
                            } else {
                                HashMap<String, String> extras = new HashMap<String, String>();
                                extras.put("LEFT", "LEFT");
                                gotoActivityAndFinish(MainActivity.class.getName(), extras);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
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

    //获取物业管理处电话
    Handler handlerGetPropertyPhone=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    List<PPhones> allDatas=(List<PPhones>)msg.obj;
                    List<PPhones> newDatas=new ArrayList<>();
                    //筛选出管理处电话
                    for (PPhones phone:allDatas){
                        if (phone.getTitle().equals("物业管理处电话")){
                            newDatas.add(phone);
                        }
                    }
                    //弹出提示
                    if (newDatas.size()==0){
                        showCallPop(allDatas);
                    }else{
                        showCallPop(newDatas);
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast(R.string.Property_does_not_provide_phone_call);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //解除房子绑定
    Handler handlerRemoveHouse = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("解除成功");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //修改用户与房屋绑定关系中的门禁信息状态
    Handler handlerEGBind=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("修改绑定状态成功");
                    HashMap<String, String> extras1 = new HashMap<String, String>();
                    try {
                        gotoActivityAndFinish(Community.class.getName(), extras1);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast("修改绑定状态失败");
                    break;
            }
        }
    };

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_binding_house.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                if (!IsFromRegister) {
                    gotoActivityAndFinish(Community.class.getName(), null);
                } else {
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    //初始化楼栋信息
    private void initBuildings() {
        sr_binding_house_build = (Spinner) findViewById(R.id.sr_binding_house_build);
        mBuild_datas = new ArrayList<Building>();
        //将可选内容与ArrayAdapter连接起来
        adapter_build = new ArrayAdapter(this, R.layout.dropdown_check_item, mBuild_datas);
        //设置下拉列表的风格
        adapter_build.setDropDownViewResource(R.layout.dropdown_item);
        //将adapter 添加到spinner中
        sr_binding_house_build.setAdapter(adapter_build);
        //设置选择事件
        sr_binding_house_build.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Building building = mBuild_datas.get(i);
                        if (!TextUtils.isEmpty(building.Id)) {
                            service.Units(building.Id, handlerUnit);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }

        );
    }

    //初始化单元信息
    private void initUnits() {
        sr_binding_house_unit = (Spinner) findViewById(R.id.sr_binding_house_unit);
        mUnit_datas = new ArrayList<Building>();
        //将可选内容与ArrayAdapter连接起来
        adapter_unit = new ArrayAdapter(this, R.layout.dropdown_check_item, mUnit_datas);
        //设置下拉列表的风格
        adapter_unit.setDropDownViewResource(R.layout.dropdown_item);
        //将adapter 添加到spinner中
        sr_binding_house_unit.setAdapter(adapter_unit);
        //设置选择事件
        sr_binding_house_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Building building = mUnit_datas.get(i);
                if (!TextUtils.isEmpty(building.Id)) {
                    service.Houses(building.Id, handlerHouse);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //初始化楼栋房间信息
    private void initHouses() {
        sr_binding_house_room = (Spinner) findViewById(R.id.sr_binding_house_room);

        mRoom_datas = new ArrayList<Building>();
        //将可选内容与ArrayAdapter连接起来
        adapter_room = new ArrayAdapter(this, R.layout.dropdown_check_item, mRoom_datas);
        //设置下拉列表的风格
        adapter_room.setDropDownViewResource(R.layout.dropdown_item);
        //将adapter 添加到spinner中
        sr_binding_house_room.setAdapter(adapter_room);
        //设置选择事件
        sr_binding_house_room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Building building = mRoom_datas.get(i);
                mHouseId = building.Id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showCallPop(List<PPhones> phonesList) {
        ListViewAdapter<PPhones> mAdapter;
        LayoutInflater layoutInflater = LayoutInflater.from(BindingHouse.this);
        View popupView = layoutInflater.inflate(R.layout.pop_property_telphone, null);
        final PopupWindow mPopWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(popupView);
        View rootview = layoutInflater.inflate(R.layout.main, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        mPopWindow.setAnimationStyle(R.anim.slide_in_from_bottom);

        TextView title = (TextView) popupView.findViewById(R.id.poptitle);
        title.setText(getResources().getText(R.string.noPrpperty));
        ListView listTelPhone = (ListView) popupView.findViewById(R.id.list_propert_telphone);
        mAdapter = new ListViewAdapter<PPhones>(BindingHouse.this, R.layout.item_telephone, phonesList) {
            @Override
            public void convert(ViewHolder holder, final PPhones phones) {
                holder.setText(R.id.tv_title, phones.Title).setText(R.id.tv_telephone, phones.Tel);
                ImageButton telephone = holder.getView(R.id.ibtn_telephone);
                telephone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phones.Tel));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    }
                });
            }
        };
        listTelPhone.setAdapter(mAdapter);
        popupView.findViewById(R.id.cancel_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopWindow.setAnimationStyle(R.anim.slide_out_to_bottom);
                mPopWindow.dismiss();
                backgroundAlpaha(BindingHouse.this, 1.0f);
            }
        });
        backgroundAlpaha(BindingHouse.this, 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerBuilding.removeCallbacksAndMessages(null);
        handlerBindingHouse.removeCallbacksAndMessages(null);
        handlerGetEntranceGuard.removeCallbacksAndMessages(null);
        handlerMyProperties.removeCallbacksAndMessages(null);
        handlerHouse.removeCallbacksAndMessages(null);
        handlerUnit.removeCallbacksAndMessages(null);

    }
}

