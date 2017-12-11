package com.ldnet.activity.me;

import android.os.*;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.ldnet.activity.BindingHouse;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.Register;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.home.HouseRent_List;
import com.ldnet.entities.MyProperties;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lee on 2017/4/24.
 * 访客认证
 */
public class VisitorValid extends BaseActionBarActivity {

    // 标题
    private TextView mTvPageTitle, tv_vistior_send;
    // 完成
    private Button bt_complete_visitor, bt_visitor_valid;
    // 返回
    private ImageButton mBtnBack;
    private EditText et_visitor_phone;
    private Services mServices;
    private String romm_id = "";
    private String phone = "";
    private String id = "";
    private String flag = "";
    private String class_from = "";
    private String COMMUNITY_ID, mCOMMUNITY_NAME = "";
    private int time = 60;
    private Timer timer;
    private String applyType = "";
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = new Services();
        // 设置布局
        setContentView(R.layout.activity_visitor_valid);
        AppUtils.setupUI(findViewById(R.id.ll_visitor_valid), this);
        initView();
        initEvent();
    }

    public void initView() {
        mServices = new Services();
        // 标题
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        mTvPageTitle.setText("验证");
        // 返回
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        tv_vistior_send = (TextView) findViewById(R.id.tv_vistior_send);
        bt_complete_visitor = (Button) findViewById(R.id.bt_complete_visitor);
        bt_visitor_valid = (Button) findViewById(R.id.bt_visitor_valid);
        et_visitor_phone = (EditText) findViewById(R.id.et_visitor_phone);


        phone = getIntent().getStringExtra("Value");
        id = getIntent().getStringExtra("Id");
        flag = getIntent().getStringExtra("Flag");
        romm_id = getIntent().getStringExtra("ROOM_ID");
        class_from = getIntent().getStringExtra("CLASS_FROM");
        COMMUNITY_ID = getIntent().getStringExtra("COMMUNITY_ID");

        Log.e("asd", "----------VistorValid" + COMMUNITY_ID);

        mCOMMUNITY_NAME = getIntent().getStringExtra("COMMUNITY_NAME");
        applyType = getIntent().getStringExtra("APPLY");
        tv_vistior_send.setText("短信已发送至:" + phone);
        RunTimer();
        getValid();
    }


    //获取短信验证码
    public void getValid() {
        String url = Services.mHost + "API/EntranceGuard/SendCode";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Id", romm_id);
        extras.put("Value", phone);
        extras.put("Flag", flag);
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        Log.e("asdsdasd", "短信验证--" + Services.json(extras));
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("Id", romm_id)
                .addParams("Value", phone)
                .addParams("Flag", flag)
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
                        Log.e("asdsdasd", "发送短信结果" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (jsonObject.getBoolean("Valid")) {
                                showToast("发送验证码成功");
                            } else {
                                tv_vistior_send.setText("短信发送失败");
                                if (jsonObject.getString("Message").contains(getString(R.string.repeat))) {
                                    showToast("发送验证码失败,十分钟之内" + jsonObject.getString("Message"));
                                } else {
                                    showToast("发送验证码失败," + jsonObject.getString("Message"));
                                }
                                //  VisitorValid.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                gotoActivityAndFinish(MainActivity.class.getName(), null);
                            } catch (ClassNotFoundException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }


    //验证短信验证码 API/EntranceGuard/ValidCode
    public void postValid() {
        Log.d("asdsdasd", " .-------3........" + id);
        String url = Services.mHost + "API/EntranceGuard/ValidCode";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Id", et_visitor_phone.getText().toString().trim());
        extras.put("Value", phone);
        extras.put("Flag", flag);
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
                .addParams("Id", et_visitor_phone.getText().toString().trim())
                .addParams("Value", phone)
                .addParams("Flag", flag)
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

                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    HashMap<String, String> extras1 = new HashMap<String, String>();
                                    extras1.put("Value", phone);
                                    extras1.put("ROOM_ID", romm_id);
                                    extras1.put("Flag", flag);
                                    extras1.put("Id", id);

                                    if (class_from != null && !class_from.equals("")) {

                                        extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                                        extras1.put("CLASS_FROM", "BindingHouse");
                                        extras1.put("APPLY", applyType == null ? "" : applyType);
                                        extras1.put("COMMUNITY_NAME", mCOMMUNITY_NAME == null ? "" : mCOMMUNITY_NAME);

                                        Log.e("asd", "gotoActivityAndFinish(VisitorValidComplete" + COMMUNITY_ID + "----" + mCOMMUNITY_NAME);
                                    }
                                    try {
                                        Log.e("asdsdasd", "验证通过----认证关系" + s);
                                        gotoActivityAndFinish(VisitorValidComplete.class.getName(), extras1);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    if (TextUtils.isEmpty(jsonObject.getString("Message"))) {
                                        showToast("验证码输入有误");
                                    } else {
                                        showToast(jsonObject.getString("Message"));
                                    }
                                }
                            } else {
                                showToast("验证码输入有误");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void initEvent() {
        mBtnBack.setOnClickListener(this);
        bt_complete_visitor.setOnClickListener(this);
        bt_visitor_valid.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
//                HashMap<String, String> extras1 = new HashMap<String, String>();
//                extras1.put("phone", phone);
//                extras1.put("ROOM_ID", romm_id);
//                try {
//                    gotoActivityAndFinish(VisitorPsd.class.getName(), extras1);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
                if (class_from != null && !class_from.equals("")) {
                    HashMap<String, String> extras1 = new HashMap<String, String>();
                    //extras1.put("phone", phone);
                    extras1.put("ROOM_ID", romm_id);
                    extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                    extras1.put("CLASS_FROM", class_from == null ? "" : class_from);
                    extras1.put("IsFromRegister", "false");
                    try {
                        gotoActivityAndFinish(VisitorPsd.class.getName(), extras1);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    HashMap<String, String> extras1 = new HashMap<String, String>();
                    //  extras1.put("phone", phone);
                    extras1.put("ROOM_ID", romm_id);
                    try {
                        gotoActivityAndFinish(VisitorPsd.class.getName(), extras1);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.bt_complete_visitor:
                if (!TextUtils.isEmpty(et_visitor_phone.getText().toString().trim())) {
                    postValid();
                } else {
                    showToast(getString(R.string.valid_is_null));
                }
                break;
            case R.id.bt_visitor_valid:
                time = 60;
                RunTimer();
                break;
            default:
                break;
        }
    }

    public void RunTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                time--;
                Message msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);

            }
        };
        timer.schedule(task, 100, 1000);
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (time > 0) {
                        bt_visitor_valid.setEnabled(false);
                        bt_visitor_valid.setText("获取验证码" + "(" + time + ")");
                        bt_visitor_valid.setTextSize(14);
                    } else {
                        timer.cancel();
                        bt_visitor_valid.setText("重新获取");
                        bt_visitor_valid.setEnabled(true);
                        bt_visitor_valid.setTextSize(14);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };


    // 监听返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            //本页getIntent
//            phone = getIntent().getStringExtra("Value");
//            id = getIntent().getStringExtra("Id");
//            flag = getIntent().getStringExtra("Flag");
//            romm_id = getIntent().getStringExtra("ROOM_ID");
//            class_from = getIntent().getStringExtra("CLASS_FROM");
//            COMMUNITY_ID = getIntent().getStringExtra("COMMUNITY_ID");
//
//

//            VisitorPsd需要getIntent
//            romm_id = getIntent().getStringExtra("ROOM_ID");
//            phoneWY = getIntent().getStringExtra("phone"); //物业电话
//            class_from = getIntent().getStringExtra("CLASS_FROM");
//            COMMUNITY_ID = getIntent().getStringExtra("COMMUNITY_ID");
//            mCOMMUNITY_NAME=getIntent().getStringExtra("COMMUNITY_NAME");
//            applyType=getIntent().getStringExtra("APPLY");


            if (class_from != null && !class_from.equals("")) {
                HashMap<String, String> extras1 = new HashMap<String, String>();
                //   extras1.put("phone", phone);
                extras1.put("ROOM_ID", romm_id);
                extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                extras1.put("CLASS_FROM", class_from == null ? "" : class_from);
                extras1.put("IsFromRegister", "false");
                try {
                    gotoActivityAndFinish(VisitorPsd.class.getName(), extras1);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                HashMap<String, String> extras1 = new HashMap<String, String>();
                //   extras1.put("phone", phone);
                extras1.put("ROOM_ID", romm_id);
                try {
                    gotoActivityAndFinish(VisitorPsd.class.getName(), extras1);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }
}
