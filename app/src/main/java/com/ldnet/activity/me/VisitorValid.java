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

import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.BindingService;
import com.ldnet.utility.Services;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.unionpay.mobile.android.global.a.s;

/**
 * Created by lee on 2017/4/24.
 * 请求服务器发送短信验证码，用户手动输入验证码，身份认证
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
    private String room_id = "";
    private String room_owner_phone = "";
    private String room_owner_id = "";
    private String flag = "";
    private String class_from = "";
    private String COMMUNITY_ID, mCOMMUNITY_NAME = "";
    private int time = 60;
    private Timer timer;
    private String applyType = "";
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String TAG = "VisitorValid";
    private BindingService bindService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = new Services();
        bindService = new BindingService(this);
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

        //获取传递参数
        getExtra();
        tv_vistior_send.setText("短信已发送至:" + room_owner_phone);

        RunTimer();
        if (notNull()) {
            bindService.getValid(room_id, room_owner_phone, flag, handlerSendSms);
        }
    }

    //获取传递参数
    private void getExtra() {
        room_owner_phone = getIntent().getStringExtra("ROOM_OWNER_TEL");   //房屋业主电话
        room_owner_id = getIntent().getStringExtra("ROOM_OWNER_ID"); //房屋业主ID
        flag = getIntent().getStringExtra("ROOM_OWNER_FLAG");
        room_id = getIntent().getStringExtra("ROOM_ID");
        class_from = getIntent().getStringExtra("CLASS_FROM");
        COMMUNITY_ID = getIntent().getStringExtra("COMMUNITY_ID");
        mCOMMUNITY_NAME = getIntent().getStringExtra("COMMUNITY_NAME");
        applyType = getIntent().getStringExtra("APPLY");
    }

    private boolean notNull() {
        if (TextUtils.isEmpty(room_id)) {
            showToast("房间无效");
            return false;
        }
        if (TextUtils.isEmpty(room_owner_phone)) {
            showToast("手机号无效");
            return false;
        }

        if (TextUtils.isEmpty(flag)) {
            showToast("标志无效");
            return false;
        }
        return true;
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
                if (class_from != null && !class_from.equals("")) {
                    HashMap<String, String> extras1 = new HashMap<String, String>();
                    extras1.put("ROOM_ID", room_id);
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
                    extras1.put("ROOM_ID", room_id);
                    try {
                        gotoActivityAndFinish(VisitorPsd.class.getName(), extras1);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.bt_complete_visitor:
                if (!TextUtils.isEmpty(et_visitor_phone.getText().toString().trim())) {
                    bindService.postValid(et_visitor_phone.getText().toString().trim(), room_owner_phone, flag, handlerCheckVlaidCode);
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

    //发送短信验证码
    Handler handlerSendSms = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast(getString(R.string.vertification_code_send_ok));
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    tv_vistior_send.setText(getString(R.string.vertification_code_send_fail));
                    if (msg.obj.toString().contains(getString(R.string.repeat))) {
                        showToast(getString(R.string.vertification_code_resend_fail) + msg.obj.toString());
                    } else {
                        showToast(getString(R.string.vertification_code_send_fail) + msg.toString());
                    }
                    break;
            }
        }
    };

    //验证短信验证码
    Handler handlerCheckVlaidCode = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    HashMap<String, String> extras1 = new HashMap<String, String>();
                    extras1.put("Value", room_owner_phone);
                    extras1.put("ROOM_ID", room_id);
                    extras1.put("Flag", flag);
                    extras1.put("Id", room_owner_id);

                    if (class_from != null && !class_from.equals("")) {

                        extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                        extras1.put("CLASS_FROM", "BindingHouse");
                        extras1.put("APPLY", applyType == null ? "" : applyType);
                        extras1.put("COMMUNITY_NAME", mCOMMUNITY_NAME == null ? "" : mCOMMUNITY_NAME);

                        Log.e(TAG, "gotoActivityAndFinish(VisitorValidComplete" + COMMUNITY_ID + "----" + mCOMMUNITY_NAME);
                    }
                    try {
                        Log.e(TAG, "验证通过----认证关系" + s);
                        gotoActivityAndFinish(VisitorValidComplete.class.getName(), extras1);
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

    // 监听返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            if (class_from != null && !class_from.equals("")) {
                HashMap<String, String> extras1 = new HashMap<String, String>();
                //   extras1.put("room_owner_phone", room_owner_phone);
                extras1.put("ROOM_ID", room_id);
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
                //   extras1.put("room_owner_phone", room_owner_phone);
                extras1.put("ROOM_ID", room_id);
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
