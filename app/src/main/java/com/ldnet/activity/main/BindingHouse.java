package com.ldnet.activity.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.commen.Services;
import com.ldnet.activity.me.Community;
import com.ldnet.activity.me.VisitorValidComplete;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.BindingService;
import com.ldnet.utility.*;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ldnet.activity.commen.Services.CLASS_FROM;
import static com.ldnet.activity.commen.Services.COMMUNITY_ID;
import static com.ldnet.activity.commen.Services.COMMUNITY_NAME;
import static com.ldnet.activity.commen.Services.ROOM_ID;
import static com.ldnet.activity.commen.Services.ROOM_NAME;

/**
 * ***************************************************
 * 绑定房产，绑定用户的房屋信息
 * **************************************************
 * 逻辑：判断条件：是否已经绑定过（从当前用户的房产信息检索当前即将绑定的房屋）、是否是业主（从业主电话列表检索当前用户电话）
 * 非业主：关系选择、手机验证、通过后才执行绑定 (非业主是先绑定房子、修改门禁关系、设置当前房屋、设置积分)
 * 业主：执行绑定（绑定、设置当前房屋、设置积分）
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
    private String mHouseId,mHouseName;
    // 标题
    private TextView tv_page_title;
    // 返回
    private ImageButton btn_back;
    // 确定
    private Button btn_binding_house;
    private String mCommunity_name;
    private List<Building> buildings;
    private List<EntranceGuard> entranceGuards;
    private BindingService service;
    private AcountService acountService;
    private  boolean isOwner;

    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_house);

        service = new BindingService(this);
        acountService=new AcountService(this);
        //得到传递的小区ID
        IsFromRegister = Boolean.valueOf(getIntent().getStringExtra("IsFromRegister"));
        mCommunityId = getIntent().getStringExtra(COMMUNITY_ID);
        mCommunity_name = getIntent().getStringExtra(COMMUNITY_NAME);

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

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "绑定房子：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "绑定房子：" + this.getClass().getSimpleName());
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
                    service.RemoveHouse(mCommunityId, "0", UserInformation.getUserInfo().UserId,new Handler());
                }
                showProgressDialog();
                service.MyProperties(handlerMyProperties);
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
                        mHouseName=mRoom_datas.get(0).Name;
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
                        //遍历所有房产，判断是否已经绑定该小区
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
                        isOwner = false;
                        entranceGuards = (List<EntranceGuard>) msg.obj;
                        for (int j = 0; j < entranceGuards.size(); j++) {
                            //如果当前用户属于业主（不含家属和租户）列表中的，那么直接绑定
                            if (UserInformation.getUserInfo().getUserPhone().equals(entranceGuards.get(j).getValue())) {
                                isOwner = true;
                                break;
                            }
                        }

                        //业主直接绑定，无需设置门禁关系
                        if (isOwner) {
                            service.BindingHouse(mCommunityId, mHouseId, handlerBindingHouse);
                        } else {
                            closeProgressDialog();
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put(ROOM_ID, mHouseId);
                            extras.put(ROOM_NAME, mHouseName == null ? "" : mHouseName);
                            extras.put(CLASS_FROM, BindingHouse.class.getName());
                            extras.put(COMMUNITY_ID, mCommunityId);
                            extras.put(COMMUNITY_NAME, mCommunity_name == null ? "" : mCommunity_name);
                            try {
                                //非业主的话先验证（申请访客密码是也需要验证）
                                gotoActivityAndFinish(VisitorValidComplete.class.getName(), extras);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    closeProgressDialog();
                    Utility.showCallPop(BindingHouse.this,true);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }

        }
    };


    //业主绑定房子
    Handler handlerBindingHouse = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (msg.obj != null) {
                        showToast(msg.obj.toString());
                        if (!isOwner) {
                            service.postEGBind("0", UserInformation.getUserInfo().UserId, "", "", mHouseId, handlerEGBind);
                        }
                        service.SetCurrentInforamtion(mCommunityId, mHouseId, new Handler());
                        acountService.setIntegralTip(new Handler(), Services.mHost + "API/Resident/ResidentBindRoom");

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
                mHouseName=building.Name;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

