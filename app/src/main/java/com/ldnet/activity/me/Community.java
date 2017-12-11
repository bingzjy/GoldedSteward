package com.ldnet.activity.me;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.BindingCommunity;
import com.ldnet.activity.BindingHouse;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.adapter.MyDialog;
import com.ldnet.activity.adapter.MyDialog2;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.home.*;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

public class Community extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private ImageButton btn_binding_community;
    private Services services;
    private List<MyProperties> myProperties = new ArrayList<MyProperties>();
    private ListView lv_me_properties;
    private ListViewAdapter mAdapter;
    private String mFromFlag;
    private String mCommunityId;
    private Boolean IsFromRegister;
    private String room_Id, resident_Id, community_Id = "";
    private String OpenEntranceState = new String("");
    private HashMap<String, String> currentExtras = new HashMap<String, String>();
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //初始化页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_community);

        //获取跳转是带的Flag
        mFromFlag = getIntent().getStringExtra("NOT_FROM_ME");
        if (TextUtils.isEmpty(mFromFlag)) {
            mFromFlag = "";
        }
        //初始化标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(getString(R.string.fragment_me_community));
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //绑定小区按钮
        btn_binding_community = (ImageButton) findViewById(R.id.btn_custom);
        btn_binding_community.setImageResource(R.drawable.plus);
        btn_binding_community.setVisibility(View.VISIBLE);

        //服务得到数据
        services = new Services();
        MyProperties();

        //我的物业列表
        lv_me_properties = (ListView) findViewById(R.id.lv_me_properties);

        initEvent();
    }

    //得到房屋信息的绑定
    private ListViewAdapter<Rooms> getHouseAdapter(List<Rooms> roomses) {

        ListViewAdapter<Rooms> mHouseAdapter = new ListViewAdapter<Rooms>(this, R.layout.item_me_properties_house, roomses) {
            @Override
            public void convert(ViewHolder holder, final Rooms rooms) {
                //设置小区名称和地址
                holder.setText(R.id.tv_house_name, rooms.Abbreviation);
                TextView tv_house_name = holder.getView(R.id.tv_house_name);
                ImageView iv_me_house_icon = holder.getView(R.id.iv_me_house_icon);
                //绑定按钮事件
                //解除房屋绑定
                Button btn_house_delete = holder.getView(R.id.btn_house_delete);
                btn_house_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (rooms != null) {
                            MyProperties mp = getProperty(rooms.RoomId);
                            if (mp != null) {
                                RemoveHouse(mp.CommunityId, rooms.RoomId, rooms, mDatas);
                            }
                        }
                    }
                });
                //设置为默认房屋
                Button btn_house_default = holder.getView(R.id.btn_house_default);
                btn_house_default.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyProperties mp = getProperty(rooms.RoomId);
                        if (rooms.RoomId != null && UserInformation.getUserInfo().UserId != null) {
                            getApprove(rooms.getRoomId(), UserInformation.getUserInfo().UserId, mp.CommunityId, 1, mp);
                        }
                        //   SetCurrentInforamtion(mp.CommunityId, rooms.RoomId, mp.getName());
                    }
                });
                //访客密码
                Button btn_community_psd = holder.getView(R.id.btn_community_psd);
                btn_community_psd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyProperties mp = getProperty(rooms.RoomId);
                        checkOpenEntrance(rooms.getRoomId(), mp.CommunityId, mp);
                        //         getApprove(rooms.getRoomId(), UserInformation.getUserInfo().UserId,mp.CommunityId,2,mp);
                    }
                });
                //设置当前项为默认项
                if (rooms.IsDefalut()) {
                    tv_house_name.setTextColor(getResources().getColor(R.color.green));
                    iv_me_house_icon.setImageResource(R.drawable.list_house_green);
                    btn_house_delete.setEnabled(false);
                    btn_house_default.setEnabled(false);
                } else {
                    tv_house_name.setTextColor(getResources().getColor(R.color.gray_deep));
                    iv_me_house_icon.setImageResource(R.drawable.list_house_gray);
                    btn_house_delete.setEnabled(true);
                    btn_house_default.setEnabled(true);
                }
            }
        };
        return mHouseAdapter;
    }

    //访客密码申请
    public void getApprove(final String roomId, final String residentId, final String cid, final int type, final MyProperties mp) {
        //判断房屋与APP用户绑定关系是否通过验证[业主]
        String url = Services.mHost + "API/EntranceGuard/Approve?roomId=" + roomId + "&residentId=" + residentId;
        Log.d("asdsdasd", url + "++");
        url = String.format(url);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
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
                        super.onResponse(s, i);
                        closeProgressDialog();

                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            Log.e("asdsdasd", "当前审核结果" + json);
                            room_Id = roomId;
                            resident_Id = residentId;
                            community_Id = cid;
                            if (json.getBoolean("Status")) {
                                //已审核
                                if (jsonObject.getBoolean("Valid")) {
                                    if (jsonObject.getString("Obj") != null && !jsonObject.getString("Obj").equals("[]")) {
                                        HashMap<String, String> extras = new HashMap<String, String>();
                                        extras.put("cid", cid);
                                        extras.put("ROOM_ID", room_Id);
                                        try {
                                            //切换房屋
                                            if (type == 1) {
                                                Log.e("asdsdasd", "审核结果---切换房屋" + json);
                                                Log.e("asdsdasd", "切换房屋小区" + mp.CommunityId + "名称" + mp.getName());
                                                SetCurrentInforamtion(mp.CommunityId, roomId, mp.getName());
                                            } else if (type == 2) {    //访客密码
                                                Log.e("asdsdasd", "审核结果---访客密码" + json);
                                                gotoActivityAndFinish(VisitorKeyChain.class.getName(), extras);
                                            }
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    //未审核
                                    MyDialog2 dialog2 = new MyDialog2(Community.this, "PASS");
                                    dialog2.show();
                                    dialog2.setDialogCallback(dialogcallback);
                                    Log.e("asdsdasd", "未审核");
                                }
                            } else {
                                showToast("审核未通过");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    MyDialog2.Dialogcallback dialogcallback = new MyDialog2.Dialogcallback() {
        @Override
        public void dialogdo(String type) {
            HashMap<String, String> extras = new HashMap<String, String>();
            extras.put("APPLY", type);
            extras.put("ROOM_ID", room_Id);
            extras.put("phone", UserInformation.getUserInfo().getPropertyPhone());
            extras.put("CLASS_FROM", VisitorPsd.class.getName());
            extras.put("COMMUNITY_ID", community_Id);
            try {
                gotoActivityAndFinish(VisitorPsd.class.getName(), extras);
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
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    //获取我的小区和房产
    public void MyProperties() {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetResidentBindInfo/%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId());
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);

                        Log.e("asdsdasd", "我的小区----Community----params" + UserInformation.getUserInfo().getUserId());
                        Log.e("asdsdasd", "我的小区----Community" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<MyProperties>>() {
                                    }.getType();
                                    myProperties.clear();
                                    myProperties = gson.fromJson(jsonObject.getString("Obj"), type);

                                    mAdapter = new ListViewAdapter<MyProperties>(Community.this, R.layout.item_me_properties_community, myProperties) {
                                        @Override
                                        public void convert(final ViewHolder holder, final MyProperties properties) {
                                            //设置小区名称和地址
                                            holder.setText(R.id.tv_community_name, properties.Name)
                                                    .setText(R.id.tv_community_address, properties.Address);
                                            TextView tv_community_name = holder.getView(R.id.tv_community_name);
                                            ImageView iv_me_community_icon = holder.getView(R.id.iv_me_community_icon);
                                            //绑定按钮事件
                                            //解除小区绑定
                                            Button btn_community_delete = holder.getView(R.id.btn_community_delete);
                                            btn_community_delete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    RemoveCommunity(properties.CommunityId, properties, mDatas);
                                                }
                                            });
                                            //绑定房屋
                                            Button btn_community_binding = holder.getView(R.id.btn_community_binding);
                                            btn_community_binding.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    HashMap<String, String> extras = new HashMap<String, String>();
                                                    extras.put("IsFromRegister", "false");
                                                    extras.put("COMMUNITY_ID", properties.CommunityId);
                                                    extras.put("COMMUNITY_NAME", properties.getName());
                                                    try {
                                                        gotoActivityAndFinish(BindingHouse.class.getName(), extras);
                                                    } catch (ClassNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });

                                            //设置小区为默认
                                            final Button btn_community_default = holder.getView(R.id.btn_community_default);
                                            if (properties.Rooms != null && properties.Rooms.size() > 0) {
                                                btn_community_default.setVisibility(View.GONE);
                                                //设置房间ListView
                                                CustomListView lv_house_information = holder.getView(R.id.lv_house_information);
                                                lv_house_information.setAdapter(getHouseAdapter(properties.Rooms));
                                                // Utility.setListViewHeightBasedOnChildren(lv_house_information);
//                                                btn_community_default.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View view) {
//                                                        btn_community_default.setEnabled(false);
//                                                        Log.e("asdsdasd","设置默认小区id"+properties.CommunityId+"名称："+properties.getName());
//                                                        SetCurrentInforamtion(properties.CommunityId, "", properties.getName());
//                                                    }
//                                                });

                                            } else {
                                                btn_community_default.setVisibility(View.VISIBLE);
                                                btn_community_default.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        btn_community_default.setEnabled(false);
                                                        Log.e("asdsdasd", "设置默认小区id" + properties.CommunityId + "名称：" + properties.getName());
                                                        SetCurrentInforamtion(properties.CommunityId, "", properties.getName());
                                                    }
                                                });
                                            }

                                            //设置当前项为默认项
                                            if (properties.IsDefalut()) {
                                                tv_community_name.setTextColor(getResources().getColor(R.color.green));
                                                iv_me_community_icon.setImageResource(R.drawable.list_community_green);
                                                btn_community_delete.setEnabled(false);
                                                btn_community_default.setEnabled(false);
                                            } else {
                                                tv_community_name.setTextColor(getResources().getColor(R.color.gray_deep));
                                                iv_me_community_icon.setImageResource(R.drawable.list_community_gray);
                                                btn_community_delete.setEnabled(true);
                                                btn_community_default.setEnabled(true);
                                            }
                                        }
                                    };
                                    // mAdapter.notifyDataSetChanged();
                                    lv_me_properties.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //解除社区绑定
    public void RemoveCommunity(final String communityId, final MyProperties properties, final List<MyProperties> mDatas) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Resident/RemoveBindCommunity";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("RoomId", "");
        extras.put("CommunityId", communityId);
        extras.put("ResidentId", UserInformation.getUserInfo
                ().getUserId());
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .addParams("RoomId", "")
                .addParams("CommunityId", communityId)
                .build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    for (int k = 0; k < mDatas.size(); k++) {
                                        if (mDatas.get(k) != null) {
                                            if (mDatas.get(k).getCommunityId().equals(properties.CommunityId)) {
                                                mDatas.remove(k);
                                            }
                                        }
                                    }
                                    mAdapter.setDatas(mDatas);
                                    mAdapter.notifyDataSetChanged();
                                    MyProperties();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //切换小区\房子
    public void SetCurrentInforamtion(final String communityId, final String roomId, final String mp) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Account/SetResidentLogonInfo";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("CommunityId", communityId);
        extras.put("HouseId", roomId);
        extras.put("ResidentId", UserInformation.getUserInfo
                ().getUserId());
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .addParams("HouseId", roomId)
                .addParams("CommunityId", communityId)
                .build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e("asdsdasd", "SetCurrentInforamtion" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    User user = gson.fromJson(jsonObject.getString("Obj"), User.class);
                                    UserInformation.setUserInfo(user);
                                    showToast("当前小区为" + mp);
                                    mAdapter.notifyDataSetChanged();
                                }
                            } else {
                                showToast("切换小区失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //解除房子绑定
    public void RemoveHouse(final String communityId, final String roomId, final Rooms rooms, final List<Rooms> mDatas) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Resident/RemoveBindRoom";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("RoomId", roomId);
        extras.put("CommunityId", communityId);
        extras.put("ResidentId", UserInformation.getUserInfo
                ().getUserId());
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .addParams("RoomId", roomId)
                .addParams("CommunityId", communityId)
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
                                    mDatas.remove(rooms);
                                    mAdapter.notifyDataSetChanged();
                                    //  mAdapter.setDatas(mDatas);
                                    MyProperties();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    private void checkOpenEntrance(final String room_Id, final String community_Id, final MyProperties mp) {
        //true 表示未开通门禁；false表示开通门禁
        OpenEntranceState = "";
        Log.e("asdsdasd", "是否开通门禁checkOpenEntrance");
        String url = Services.mHost + "API/EntranceGuard/Unused?communityid=" + community_Id;
        Log.d("asdsdasd", url + "++");
        url = String.format(url);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
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
                        super.onResponse(s, i);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            Log.e("asdsdasd", "小区" + UserInformation.getUserInfo().getCommunityId() + "是否开通门禁" + json);
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    //  未开通门禁
                                    // OpenEntranceState= getString(R.string.nouse_entrance);
                                    openEntrance(community_Id);
                                } else {
                                    getApprove(room_Id, UserInformation.getUserInfo().UserId, mp.CommunityId, 2, mp);
                                    //  OpenEntranceState= getString(R.string.use_entrance);
                                }
                            } else {
                                showToast("审核未通过");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void openEntrance(final String community_Id) {
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
                getPropertyTelphone(community_Id);
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


    //获取物业联系电话
    private void getPropertyTelphone(String community_Id) {
        String url = Services.mHost + "Api/Property/GetCommonTel/%s";
        url = String.format(url, community_Id);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
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
                        super.onResponse(s, i);
                        closeProgressDialog();
                        Log.e("asdsdasd", "物业电话" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<PPhones>>() {
                                    }.getType();
                                    List<PPhones> mDatas = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    List<PPhones> newDatas = new ArrayList<PPhones>();
                                    if (mDatas != null && mDatas.size() > 0) {
                                        for (int t = 0; t < mDatas.size(); t++) {
                                            if (mDatas.get(t).getTitle().equals("物业管理处电话")) {
                                                newDatas.add(mDatas.get(t));
                                            }
                                        }
                                        Log.e("asdsdasd", "物业电话1111" + s);
                                        if (newDatas.size() == 0) {
                                            showCallPop(mDatas);
                                        } else {
                                            showCallPop(newDatas);
                                        }
                                    } else {
                                        showToast(R.string.Property_does_not_provide_phone_call);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void showCallPop(List<PPhones> phonesList) {
        ListViewAdapter<PPhones> mAdapter;
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupView = layoutInflater.inflate(R.layout.pop_property_telphone, null);
        final PopupWindow mPopWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(popupView);
        View rootview = layoutInflater.inflate(R.layout.main, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        mPopWindow.setAnimationStyle(R.anim.slide_in_from_bottom);


        TextView title = (TextView) popupView.findViewById(R.id.poptitle);
        title.setVisibility(View.GONE);
        //  title.setText(getResources().getText(R.string.no_entrance));
        ListView listTelPhone = (ListView) popupView.findViewById(R.id.list_propert_telphone);
        mAdapter = new ListViewAdapter<PPhones>(this, R.layout.item_telephone, phonesList) {
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
        popupView.findViewById(R.id.cancel_call).getBackground().setAlpha(200);
        popupView.findViewById(R.id.cancel_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopWindow.setAnimationStyle(R.anim.slide_out_to_bottom);
                mPopWindow.dismiss();
                backgroundAlpaha(Community.this, 1.0f);
            }
        });
        backgroundAlpaha(Community.this, 0.5f);
    }

    public void backgroundAlpaha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }


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
