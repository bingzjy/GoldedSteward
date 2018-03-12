package com.ldnet.activity.me;


import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.*;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import android.widget.Toast;

import com.ldnet.activity.BindingCommunity;
import com.ldnet.activity.BindingHouse;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.adapter.CustomAlertDialog;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.home.*;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.BindingService;
import com.ldnet.service.EntranceGuardService;
import com.ldnet.utility.*;
import com.tendcloud.tenddata.TCAgent;

import java.util.*;

import static com.ldnet.goldensteward.R.id.tv_house_name;
import static com.ldnet.map.ChString.type;
import static com.ldnet.utility.Services.CLASS_FROM;
import static com.ldnet.utility.Services.COMMUNITY_ID;
import static com.ldnet.utility.Services.COMMUNITY_NAME;
import static com.ldnet.utility.Services.ROOM_ID;
import static com.ldnet.utility.Services.ROOM_NAME;
import static com.ldnet.utility.Services.TO_APPLY;

public class Community extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private ImageButton btn_binding_community;
    private Services services;
    private List<MyProperties> myProperties = new ArrayList<MyProperties>();
    private ListView lv_me_properties;
    private ListViewAdapter mAdapter;
    ListViewAdapter<Rooms> mHouseAdapter;
    private String mFromFlag;
    private String mCommunityId;
    private Boolean IsFromRegister;
    private String room_Id, resident_Id, community_Id, room_name, community_name = "";
    private String OpenEntranceState = new String("");
    private HashMap<String, String> currentExtras = new HashMap<String, String>();
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    //    private MyProperties currentMyProperty;
    private BindingService bindingService;
    private AcountService acountService;
    private EntranceGuardService entranceGuardService;
    private int getApproveType = 0;
    private Rooms currentRoom;
    //初始化页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_community);
        //服务得到数据
        services = new Services();
        bindingService = new BindingService(this);
        acountService = new AcountService(this);
        entranceGuardService = new EntranceGuardService(this);

        //获取跳转是带的Flag
        mFromFlag = getIntent().getStringExtra("NOT_FROM_ME");
        if (TextUtils.isEmpty(mFromFlag)) {
            mFromFlag = "";
        }

        initView();
        initEvent();
        //获取我的房产数据
        showProgressDialog();
        bindingService.MyProperties(handlerMyProperties);
    }

    private void initView() {
        //初始化标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(getString(R.string.fragment_me_community));
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //绑定小区按钮
        btn_binding_community = (ImageButton) findViewById(R.id.btn_custom);
        btn_binding_community.setImageResource(R.drawable.plus);
        btn_binding_community.setVisibility(View.VISIBLE);
        //我的物业列表
        lv_me_properties = (ListView) findViewById(R.id.lv_me_properties);


        //小区适配器
        mAdapter = new ListViewAdapter<MyProperties>(Community.this, R.layout.item_me_properties_community, myProperties) {
            @Override
            public void convert(final ViewHolder holder, final MyProperties properties) {

                //设置小区名称和地址
                holder.setText(R.id.tv_community_name, properties.Name)
                        .setText(R.id.tv_community_address, properties.Address);
                TextView tv_community_name = holder.getView(R.id.tv_community_name);
                ImageView iv_me_community_icon = holder.getView(R.id.iv_me_community_icon);

                //解除小区绑定
                Button btn_community_delete = holder.getView(R.id.btn_community_delete);
                btn_community_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgressDialog();
                        bindingService.RemoveCommunity(properties.CommunityId, handlerRemove);
                    }
                });
                //绑定房屋
                Button btn_community_binding = holder.getView(R.id.btn_community_binding);
                btn_community_binding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put("IsFromRegister", "false");
                        extras.put(COMMUNITY_ID, properties.CommunityId);
                        extras.put(COMMUNITY_NAME, properties.getName());
                        try {
                            gotoActivityAndFinish(BindingHouse.class.getName(), extras);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

                //设置小区为默认
                final Button btn_community_default = holder.getView(R.id.btn_community_default);


                //设置房间ListView
                CustomListView lv_house_information = holder.getView(R.id.lv_house_information);
                lv_house_information.setAdapter(getHouseAdapter(properties.Rooms, properties,myProperties.size()));

                if (properties.Rooms != null && properties.Rooms.size() > 0) {
                    btn_community_default.setVisibility(View.GONE);

                } else {
                    btn_community_default.setVisibility(View.VISIBLE);
                    btn_community_default.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            btn_community_default.setEnabled(false);
                            bindingService.SetCurrentInforamtion(properties.CommunityId, "", handlerSetUserInfo);
                        }
                    });
                }




                //设置当前项为默认项
                if (properties.IsDefalut()) {
                    properties.Default = true;
                    tv_community_name.setTextColor(getResources().getColor(R.color.green));
                    iv_me_community_icon.setImageResource(R.drawable.list_community_green);
                    btn_community_delete.setEnabled(false);
                    btn_community_default.setEnabled(false);
                } else {
                    properties.Default = false;
                    tv_community_name.setTextColor(getResources().getColor(R.color.gray_deep));
                    iv_me_community_icon.setImageResource(R.drawable.list_community_gray);
                    btn_community_delete.setEnabled(true);
                    btn_community_default.setEnabled(true);
                }

            }
        };
        lv_me_properties.setAdapter(mAdapter);
    }


    //得到房屋信息的绑定
    private ListViewAdapter<Rooms> getHouseAdapter(final List<Rooms> roomses, final MyProperties properties, final int communityCount) {

        mHouseAdapter = new ListViewAdapter<Rooms>(this, R.layout.item_me_properties_house, roomses) {
            @Override
            public void convert(ViewHolder holder, final Rooms rooms) {
                currentRoom = rooms;
                //设置小区名称和地址
                holder.setText(tv_house_name, rooms.Abbreviation);
                TextView tv_house_name = holder.getView(R.id.tv_house_name);
                ImageView iv_me_house_icon = holder.getView(R.id.iv_me_house_icon);
                //绑定按钮事件
                //解除房屋绑定
                Button btn_house_delete = holder.getView(R.id.btn_house_delete);
                btn_house_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgressDialog();
                        bindingService.RemoveHouse(properties.CommunityId, rooms.RoomId, UserInformation.getUserInfo().UserId, handlerRemove);
                    }
                });
                //设置为默认房屋
                Button btn_house_default = holder.getView(R.id.btn_house_default);
                btn_house_default.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showProgressDialog();
                        getApproveType = 1;//1，设置默认房屋  2,获取访客密码
                        room_Id = rooms.getRoomId();
                        community_Id = properties.CommunityId;
                        room_name = rooms.Abbreviation;
                        community_name = properties.Name;
                        acountService.getApprove(room_Id, UserInformation.getUserInfo().UserId, handlerGetApprove);
                    }
                });
                //访客密码
                Button btn_community_psd = holder.getView(R.id.btn_community_psd);
                btn_community_psd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        room_Id = rooms.getRoomId();
                        community_Id = properties.CommunityId;
                        room_name = rooms.Abbreviation;
                        community_name = properties.Name;
                        entranceGuardService.checkOpenEntrance(community_Id, handlerCheckOpenEntrance);
                    }
                });

                //设置当前项为默认项
                if (rooms.IsDefalut()) {
                    rooms.Default = true;
                    tv_house_name.setTextColor(getResources().getColor(R.color.green));
                    iv_me_house_icon.setImageResource(R.drawable.list_house_green);
               //     btn_house_delete.setEnabled(false);
                    btn_house_default.setEnabled(false);
                } else {
                    rooms.Default = false;
                    tv_house_name.setTextColor(getResources().getColor(R.color.gray_deep));
                    iv_me_house_icon.setImageResource(R.drawable.list_house_gray);
              //      btn_house_delete.setEnabled(true);
                    btn_house_default.setEnabled(true);
                }

                //只有唯一小区、唯一房间
                if (communityCount==1&&roomses.size()==1){

                    btn_house_delete.setEnabled(true);

                }else if (rooms.IsDefalut()&&communityCount>1){  //多个小区,当前默认，不可接触

                    btn_house_delete.setEnabled(false);

                }else if (!rooms.IsDefalut()){   //不是当前默认，可接触

                    btn_house_delete.setEnabled(true);

                }

            }
        };
        return mHouseAdapter;
    }


    CustomAlertDialog.Dialogcallback dialogcallback = new CustomAlertDialog.Dialogcallback() {
        @Override
        public void dialogdo() {
            HashMap<String, String> extras = new HashMap<String, String>();
            extras.put(TO_APPLY, "PASS");
            extras.put(ROOM_ID, room_Id);
            extras.put(ROOM_NAME, room_name == null ? "" : room_name);
            extras.put(CLASS_FROM, Community.class.getName());
            extras.put(COMMUNITY_ID, community_Id);
            extras.put(COMMUNITY_NAME, community_name == null ? "" : community_name);
            try {
                gotoActivityAndFinish(VisitorValidComplete.class.getName(), extras);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void dialogDismiss() {

        }
    };


    //通过房屋ID获取小区信息
    private MyProperties getProperty(String roomId) {
        for (MyProperties fp : myProperties) {
            if (fp.Rooms != null) {
                for (Rooms rooms : fp.Rooms) {
                    if (rooms.RoomId.equals(roomId)) {
                        return fp;
                    }
                }
            } else {
                continue;
            }
        }
        return null;
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_binding_community.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "我的小区：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "我的小区：" + this.getClass().getSimpleName());
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    if (mFromFlag.equals("101")) {
                        gotoActivityAndFinish(Property_Repair.class.getName(), null);
                    } else if (mFromFlag.equals("102")) {
                        gotoActivityAndFinish(Property_Repair_Create.class.getName(), null);
                    } else if (mFromFlag.equals("103")) {
                        gotoActivityAndFinish(Property_Complain.class.getName(), null);
                    } else if (mFromFlag.equals("104")) {
                        gotoActivityAndFinish(Property_Complain_Create.class.getName(), null);
                    } else {
                        gotoActivityAndFinish(MainActivity.class.getName(), null);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_custom:
                try {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("FROM_COMMUNITY", "true");
                    extras.put("LEFT", "LEFT");

                    gotoActivity(BindingCommunity.class.getName(), extras);
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
            try {
                if (mFromFlag.equals("101")) {
                    gotoActivityAndFinish(Property_Repair.class.getName(), null);
                } else if (mFromFlag.equals("102")) {
                    gotoActivityAndFinish(Property_Repair_Create.class.getName(), null);
                } else if (mFromFlag.equals("103")) {
                    gotoActivityAndFinish(Property_Complain.class.getName(), null);
                } else if (mFromFlag.equals("104")) {
                    gotoActivityAndFinish(Property_Complain_Create.class.getName(), null);
                } else {
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void openEntrance() {
        TextView log_off_cancel;
        TextView log_off_confirm;
        TextView tv_dialog_title;
        final AlertDialog alertDialog = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.ly_off);
        alertDialog.findViewById(R.id.line).setVisibility(View.VISIBLE);
        tv_dialog_title = (TextView) alertDialog.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(getString(R.string.no_entrance));
        log_off_cancel = (TextView) alertDialog.findViewById(R.id.log_off_cancel);
        log_off_confirm = (TextView) alertDialog.findViewById(R.id.log_off_confirm);
        log_off_confirm.setText("确定");
        log_off_cancel.setText("取消");
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(lp);
        log_off_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showCallPop(Community.this, false);
                alertDialog.dismiss();
            }
        });
        log_off_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    //解除小区绑定关系
    Handler handlerRemove = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    bindingService.MyProperties(handlerMyProperties);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //设置默认房屋
    Handler handlerSetUserInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast("当前小区" + UserInformation.getUserInfo().CommuntiyName);
                    mAdapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //是否开通门禁返回
    Handler handlerCheckOpenEntrance = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:  //开通门禁 ,再次判断是否通过审核
                    getApproveType = 2;
                    acountService.getApprove(room_Id, UserInformation.getUserInfo().UserId, handlerGetApprove);
                    break;
                case BaseService.DATA_SUCCESS_OTHER: //未开通门禁
                    openEntrance();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerGetApprove = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS: //通过审核

                    if (getApproveType == 1) {   //设置当前房产

                        bindingService.SetCurrentInforamtion(community_Id, room_Id, handlerSetUserInfo);

                    } else if (getApproveType == 2) {//获取访客密码

                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put(COMMUNITY_ID, community_Id);
                        extras.put(ROOM_ID, room_Id);
                        try {
                            gotoActivityAndFinish(VisitorKeyChain.class.getName(), extras);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:  //未通过审核
                   CustomAlertDialog dialog2 = new CustomAlertDialog(Community.this,false,getString(R.string.dialog_title),getString(R.string.dialog_verify));
                    dialog2.show();
                    dialog2.setDialogCallback(dialogcallback);
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
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    myProperties.clear();
                    myProperties.addAll((List<MyProperties>) msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };



    //动态申请权限
    private void requestPermission(HashMap<String, String> extras) {
        currentExtras = extras;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(Community.this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(Community.this, permissions, 321);
            } else {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(Community.this, permissions[1])) {
                    ActivityCompat.requestPermissions(Community.this, permissions, 322);
                } else {
                    try {
                        gotoActivity(BindingCommunity.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321 || requestCode == 322) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    boolean noRemaind = shouldShowRequestPermissionRationale(permissions[1]);
                    if (!noRemaind) {
                        Toast.makeText(Community.this, "请手动开启定位权限", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        gotoActivity(BindingCommunity.class.getName(), currentExtras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
