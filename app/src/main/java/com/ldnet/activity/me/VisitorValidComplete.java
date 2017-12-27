package com.ldnet.activity.me;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.amap.api.maps.model.Text;
import com.google.gson.Gson;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.view.SlideDateTimeListener;
import com.ldnet.view.SlideDateTimePicker;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by lee on 2017/4/25.
 *
 * 亲属、租户关系选择
 */
public class VisitorValidComplete extends BaseActionBarFragmentActivity implements View.OnClickListener {

    // 标题
    private TextView mTvPageTitle, tv_cname;
    // 完成
    private Button bt_valid_complete_visitor;
    // 返回
    private ImageButton mBtnBack;
    private EditText et_valid_start_date;
    private EditText et_weekend_start_time;
    private EditText et_valid_end_date;
    private EditText et_weekend_end_time;
    private RadioGroup radioGroup;
    private RadioButton qinshuRadioButton;
    private RadioButton zuhuRadioButton;
    private LinearLayout ll_date;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Calendar calendar = Calendar.getInstance();
    private String romm_id = "";
    private String phone = "";
    private String id = "";
    private String flag = "";
    private String class_from = "";
    private String COMMUNITY_ID, mCOMMUNITY_NAME = "";
    private String applyType = "";
    private int residentType;

    private Date endDateClick, startDateClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valid_complete);
        initView();
        intiEvent();
    }

    public void initView() {
        // 标题
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        tv_cname = (TextView) findViewById(R.id.tv_cname);
        mTvPageTitle.setText("认证");
        // 返回
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        et_valid_start_date = (EditText) findViewById(R.id.et_valid_start_date);
        et_valid_end_date = (EditText) findViewById(R.id.et_valid_end_date);
        bt_valid_complete_visitor = (Button) findViewById(R.id.bt_valid_complete_visitor);
        //获取实例
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupID);
        qinshuRadioButton = (RadioButton) findViewById(R.id.qinshuID);
        zuhuRadioButton = (RadioButton) findViewById(R.id.zuhuID);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        //默认设置(关系默认家属)
        qinshuRadioButton.setChecked(true);
        residentType = 1;
        ll_date.setVisibility(View.GONE);
        //设置监听
        radioGroup.setOnCheckedChangeListener(new RadioGroupListener());
        phone = getIntent().getStringExtra("Value");
        id = getIntent().getStringExtra("Id");
        flag = getIntent().getStringExtra("Flag");
        romm_id = getIntent().getStringExtra("ROOM_ID");
        class_from = getIntent().getStringExtra("CLASS_FROM");
        COMMUNITY_ID = getIntent().getStringExtra("COMMUNITY_ID");
        mCOMMUNITY_NAME = getIntent().getStringExtra("COMMUNITY_NAME");
        applyType = getIntent().getStringExtra("APPLY");
        tv_cname.setText(mCOMMUNITY_NAME);
    }

    public void intiEvent() {
        mBtnBack.setOnClickListener(this);
        et_valid_start_date.setOnClickListener(this);
        et_valid_end_date.setOnClickListener(this);
        bt_valid_complete_visitor.setOnClickListener(this);
    }


    //API/EntranceGuard/EGBind 修改用户与房屋绑定关系中的门禁信息状态
    public void postEGBind(String sDate, String eDate) {
        Log.d("asdsdasd", "--" + romm_id + "," + id + "," + et_valid_start_date.getText().toString().trim() + "-" + et_valid_end_date.getText().toString().trim());
        String url = Services.mHost + "API/EntranceGuard/EGBind";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("residentId", UserInformation.getUserInfo().getUserId());
        extras.put("roomId", romm_id);
        extras.put("ownerid", id);
        extras.put("leaseDateS", sDate);
        extras.put("leaseDateE", eDate);
        extras.put("residentType", residentType + "");
        Log.d("asdsdasd", "--" + Services.json(extras));
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        Log.d("asdsdasd", "--" + Services.json(extras));
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("residentId", UserInformation.getUserInfo().getUserId())
                .addParams("roomId", romm_id)
                .addParams("ownerid", id)
                .addParams("leaseDateS", et_valid_start_date.getText().toString().trim())
                .addParams("leaseDateE", et_valid_end_date.getText().toString().trim())
                .addParams("residentType", residentType + "")
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
                        Log.e("asdsdasd", "修改绑定状态结果" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            if (s.contains(getString(R.string.refuse))) {
                                showToast(json.getString("Message"));
                            } else {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    showToast("修改绑定状态成功");
                                    HashMap<String, String> extras1 = new HashMap<String, String>();
                                    extras1.put("LEFT", "LEFT");
                                    try {
                                        gotoActivityAndFinish(Community.class.getName(), extras1);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    showToast("修改绑定状态失败");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //绑定房子
    public void BindingHouse(final String communityId, final String roomId) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        final String url = Services.mHost + "API/Resident/ResidentBindRoom";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("CommunityId", communityId);
        extras.put("RoomId", roomId);
        extras.put("ResidentId", UserInformation.getUserInfo().getUserId());

        Log.e("asdsdasd", "CommunityId--" + communityId + "---RoomId---" + roomId + "---ResidentId---" + UserInformation.getUserInfo().getUserId());
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
                .addParams("CommunityId", communityId)
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .addParams("RoomId", roomId)
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
                        Log.e("asdsdasd", "111111111" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            if (s.contains(getString(R.string.refuse))) {
                                showToast(json.getString("Message"));
                            } else {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (json.getBoolean("Status")) {
                                    if (jsonObject.getBoolean("Valid")) {
                                        //绑定成功后，调用EGBind
                                        Log.e("asdsdasd", "绑定成功后，调用EGBind" + s);
                                        showToast(jsonObject.getString("Message"));
                                        HashMap<String, String> extras = new HashMap<String, String>();
                                        extras.put("LEFT", "LEFT");
                                        SetCurrentInforamtion(communityId, roomId);
                                        IntegralTip(url);


                                        Log.e("asdsdasd", "绑定成功后，调用EGBind----时间" + residentType + et_valid_start_date.getText() + "--" + et_valid_end_date.getText() + "-----");

                                        if (residentType == 2) {
                                            if (et_valid_start_date.getText().toString().trim() != null && et_valid_end_date.getText().toString().trim() != null) {
                                                postEGBind(et_valid_start_date.getText().toString().trim(), et_valid_end_date.getText().toString().trim());
                                            } else {
                                                showToast("请选择时间");
                                            }
                                        } else if (residentType == 1) {
                                            postEGBind("", "");
                                        }

                                    } else {
                                        showToast(jsonObject.getString("Message") == null ? "绑定失败" : jsonObject.getString("Message"));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //查看积分
    public void IntegralTip(String pUrl) {

        String url1 = "";
        try {
            pUrl = new URL(pUrl).getPath();
            // 请求的URL
            String url = Services.mHost + "API/Prints/Add/%s?route=%s";
            url = String.format(url, UserInformation.getUserInfo().UserId, URLEncoder.encode(pUrl, "UTF-8"));
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
                            Log.e("asdsdasd", "积分" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (json.getBoolean("Status")) {
                                    if (jsonObject.getBoolean("Valid")) {
                                        showToast("+" + jsonObject.getString("Obj") + "积分");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //切换小区\房子
    public void SetCurrentInforamtion(String communityId, String roomId) {
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
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    User user = gson.fromJson(jsonObject.getString("Obj"), User.class);
                                    UserInformation.setUserInfo(user);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
//            HashMap<String, String> extras1 = new HashMap<String, String>();
//            extras1.put("Value", phone);
//            extras1.put("Id", id);
//            extras1.put("Flag", flag);
//            extras1.put("ROOM_ID", romm_id);
//            try {
//                gotoActivityAndFinish(VisitorPsd.class.getName(), extras1);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
            if (class_from != null && !class_from.equals("")) {
                HashMap<String, String> extras1 = new HashMap<String, String>();
                extras1.put("Value", phone);
                extras1.put("Id", id);
                extras1.put("Flag", flag);
                extras1.put("ROOM_ID", romm_id);
                extras1.put("CLASS_FROM", VisitorValidComplete.class.getName());
                extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                extras1.put("COMMUNITY_NAME", mCOMMUNITY_NAME == null ? "" : mCOMMUNITY_NAME);
                extras1.put("IsFromRegister", "false");
                extras1.put("APPLY", applyType == null ? "" : applyType);
                try {
                    gotoActivityAndFinish(VisitorValid.class.getName(), extras1);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                HashMap<String, String> extras1 = new HashMap<String, String>();
                extras1.put("Value", phone);
                extras1.put("Id", id);
                extras1.put("Flag", flag);
                extras1.put("ROOM_ID", romm_id);
                extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                extras1.put("COMMUNITY_NAME", mCOMMUNITY_NAME == null ? "" : mCOMMUNITY_NAME);
                extras1.put("CLASS_FROM", VisitorValidComplete.class.getName());
                try {
                    gotoActivityAndFinish(VisitorValid.class.getName(), extras1);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (view.getId() == R.id.et_valid_start_date) {
            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                    .setListener(listener)
                    .setInitialDate(new Date())
                    .setIs24HourTime(true)
                    .setMinDate(new Date())
                    .build()
                    .show();
        } else if (view.getId() == R.id.et_weekend_start_time) {
            new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hour, int minute) {
                    //更新EditText控件时间 小于10加0
                    et_weekend_start_time.setText(new StringBuilder().append(hour < 10 ? "0" + hour : hour).append(":").append(minute < 10 ? "0" + minute : minute).append(":00"));
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        } else if (view.getId() == R.id.et_valid_end_date) {
            new SlideDateTimePicker.Builder(getSupportFragmentManager())
                    .setListener(listener1)
                    .setInitialDate(new Date())
                    .setIs24HourTime(true)
                    .setMinDate(new Date())
                    .build()
                    .show();
        } else if (view.getId() == R.id.et_weekend_end_time) {
            new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hour, int minute) {
                    //更新EditText控件时间 小于10加0
                    et_weekend_end_time.setText(new StringBuilder().append(hour < 10 ? "0" + hour : hour).append(":").append(minute < 10 ? "0" + minute : minute).append(":00"));
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        } else if (view.getId() == R.id.bt_valid_complete_visitor) {
            //关系认证确定后，直接调用postEGBind（）；
            Log.e("asdsdasd", "applyType" + applyType);
            if (class_from != null && applyType.equals("PASS")) {
                if (residentType == 2) {
                    Log.e("asdsdasd", "VisitorValidCompleted" + "租户申请密码" + et_valid_start_date.getText().toString() + "-" + et_valid_end_date.getText().toString());
                    if (startDateClick == null || endDateClick == null || TextUtils.isEmpty(et_valid_start_date.getText().toString()) || TextUtils.isEmpty(et_valid_end_date.getText().toString())) {
                        showToast("请选择时间");
                    } else if (startDateClick != null && endDateClick != null && startDateClick.getTime() > endDateClick.getTime()) {
                        showToast("开始时间不能大于结束时间");
                    } else {
                        postEGBind(et_valid_start_date.getText().toString().trim(), et_valid_end_date.getText().toString().trim());
                    }
//                    if (et_valid_start_date.getText().toString().trim() != null && et_valid_end_date.getText().toString().trim() != null) {
//                        postEGBind(et_valid_start_date.getText().toString().trim(), et_valid_end_date.getText().toString().trim());
//                    } else {
//                        showToast("请选择时间");
//                    }
                } else if (residentType == 1) {
                    Log.e("asdsdasd", "VisitorValidCompleted" + "家属申请密码");
                    postEGBind("", "");
                }
            } else if (applyType == null || applyType.equals("")) {
                Log.e("asdsdasd", "VisitorValidCompleted" + "绑定房子");
                if (TextUtils.isEmpty(COMMUNITY_ID)) {
                    showToast("小区信息为空");
                } else if (TextUtils.isEmpty(romm_id)) {
                    showToast("房间信息为空");
                } else if (residentType == 2) {
                    if (startDateClick == null || endDateClick == null || TextUtils.isEmpty(et_valid_start_date.getText().toString()) || TextUtils.isEmpty(et_valid_end_date.getText().toString())) {
                        showToast("请选择时间");
                    } else if (startDateClick != null && endDateClick != null && startDateClick.getTime() > endDateClick.getTime()) {
                        showToast("开始时间不能大于结束时间");
                    } else {
                        BindingHouse(COMMUNITY_ID, romm_id);
                    }
                } else if (residentType == 1) {
                    BindingHouse(COMMUNITY_ID, romm_id);
                }
            }


//            if (residentType == 2) {
//                if (et_valid_start_date.getText().toString().trim() != null && et_valid_end_date.getText().toString().trim() != null) {
//                    postEGBind(et_valid_start_date.getText().toString().trim(), et_valid_end_date.getText().toString().trim());
//                } else {
//                    showToast("请选择时间");
//                }
//            } else if (residentType == 1) {
//                postEGBind("", "");
//            }
        }
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            startDateClick = date;
            et_valid_start_date.setText(mFormatter.format(date));
        }
    };

    private SlideDateTimeListener listener1 = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            endDateClick = date;
            Log.e("asdsdasd", "时间选择" + mFormatter.format(date));
            et_valid_end_date.setText(mFormatter.format(date));
        }
    };

    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == qinshuRadioButton.getId()) {
                residentType = 1;
                Log.d("asdsdasd", residentType + "");
                ll_date.setVisibility(View.GONE);
            } else if (checkedId == zuhuRadioButton.getId()) {
                residentType = 2;
                Log.d("asdsdasd", residentType + "");
                ll_date.setVisibility(View.VISIBLE);
            }
        }
    }

    // 监听返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (class_from != null && !class_from.equals("")) {
                HashMap<String, String> extras1 = new HashMap<String, String>();
                extras1.put("Value", phone);
                extras1.put("Id", id);
                extras1.put("Flag", flag);
                extras1.put("ROOM_ID", romm_id);
                extras1.put("CLASS_FROM", VisitorValidComplete.class.getName());
                extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                extras1.put("COMMUNITY_NAME", mCOMMUNITY_NAME == null ? "" : mCOMMUNITY_NAME);
                extras1.put("IsFromRegister", "false");
                extras1.put("APPLY", applyType == null ? "" : applyType);
                try {
                    gotoActivityAndFinish(VisitorValid.class.getName(), extras1);
                } catch (ClassNotFoundException e) {
                    Log.e("asd", "back2222222222222-------------------22222");
                    e.printStackTrace();
                }
            } else {
                Log.e("asd", "back333333333333");
                HashMap<String, String> extras1 = new HashMap<String, String>();
                extras1.put("Value", phone);
                extras1.put("Id", id);
                extras1.put("Flag", flag);
                extras1.put("ROOM_ID", romm_id);
                extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                extras1.put("COMMUNITY_NAME", mCOMMUNITY_NAME == null ? "" : mCOMMUNITY_NAME);
                extras1.put("CLASS_FROM", VisitorValidComplete.class.getName());
                try {
                    gotoActivityAndFinish(VisitorValid.class.getName(), extras1);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        endDateClick = null;
        startDateClick = null;
        et_valid_start_date.setText("");
        et_valid_end_date.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        endDateClick = null;
        startDateClick = null;
    }
}
