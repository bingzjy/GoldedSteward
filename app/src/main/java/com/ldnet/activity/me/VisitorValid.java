package com.ldnet.activity.me;

import android.os.*;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ldnet.activity.FragmentHome;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.BindingService;
import com.ldnet.utility.Services;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.ldnet.utility.Services.CLASS_FROM;
import static com.ldnet.utility.Services.COMMUNITY_ID;
import static com.ldnet.utility.Services.COMMUNITY_NAME;
import static com.ldnet.utility.Services.OWNER_FLAG;
import static com.ldnet.utility.Services.OWNER_ID;
import static com.ldnet.utility.Services.OWNER_TEL;
import static com.ldnet.utility.Services.RESIDENT_DATE_END;
import static com.ldnet.utility.Services.RESIDENT_DATE_START;
import static com.ldnet.utility.Services.RESIDENT_TYPE;
import static com.ldnet.utility.Services.ROOM_ID;
import static com.ldnet.utility.Services.TO_APPLY;

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
    private String resident_type, resident_sdate, resident_edate;
    private String room_owner_phone = "";
    private String room_owner_id = "";
    private String flag = "";
    private String class_from = "";
    private String community_id, community_name = "";
    private int time = 60;
    private Timer timer;
    private String applyType = "";
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String TAG = "VisitorValid";
    private BindingService bindService;
    private AcountService acountService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = new Services();
        bindService = new BindingService(this);
        acountService = new AcountService(this);
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
        room_owner_phone = getIntent().getStringExtra(OWNER_TEL);   //房屋业主电话
        room_owner_id = getIntent().getStringExtra(OWNER_ID); //房屋业主ID
        flag = getIntent().getStringExtra(OWNER_FLAG);
        room_id = getIntent().getStringExtra(ROOM_ID);
        class_from = getIntent().getStringExtra(CLASS_FROM);
        community_id = getIntent().getStringExtra(COMMUNITY_ID);
        community_name = getIntent().getStringExtra(COMMUNITY_NAME);
        applyType = getIntent().getStringExtra(TO_APPLY);

        resident_type = getIntent().getStringExtra(RESIDENT_TYPE);
        resident_sdate = getIntent().getStringExtra(RESIDENT_DATE_START);
        resident_edate = getIntent().getStringExtra(RESIDENT_DATE_END);
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
                finish();
                break;
            case R.id.bt_complete_visitor:
                if (!TextUtils.isEmpty(et_visitor_phone.getText().toString().trim())) {
                    showProgressDialog();
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

    private boolean notNull() {
        if (TextUtils.isEmpty(room_id)) {
            showToast("房间信息无效");
            return false;
        }

        if (TextUtils.isEmpty(community_id)) {
            showToast("小区信息无效");
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

    //计时器
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
                        bt_visitor_valid.setText(getString(R.string.get_valid_codes) + "(" + time + ")");
                        bt_visitor_valid.setTextSize(14);
                    } else {
                        timer.cancel();
                        bt_visitor_valid.setText(getString(R.string.get_valid_code_again));
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
                        showToast(msg.obj.toString());
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
                    validSuccess();
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

            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (msg.obj != null) {
                        showToast(msg.obj.toString());
                        bindService.postEGBind(resident_type, room_owner_id, resident_sdate, resident_edate, room_id, handlerEGBind);
                        bindService.SetCurrentInforamtion(community_id, room_id, new Handler());
                        acountService.setIntegralTip(new Handler(), Services.mHost + "API/Resident/ResidentBindRoom");
                        try {
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put("LEFT", "LEFT");
                            gotoActivityAndFinish(Community.class.getName(), extras);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
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


    //修改用户与房屋绑定关系中的门禁信息状态
    Handler handlerEGBind = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast("修改绑定状态成功");

                    try {
                        gotoActivityAndFinish(Community.class.getName(), null);
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


    //验证通过  1，若是绑定，则需绑定、添加门禁关系、 2若是身份验证则需 添加门禁关系 返回我的主页或者我的小区
    private void validSuccess() {
        if (!TextUtils.isEmpty(applyType) && applyType.equals("PASS")) {

            bindService.postEGBind(resident_type, room_owner_id, resident_sdate, resident_edate, room_id, handlerEGBind);

        } else if (TextUtils.isEmpty(applyType) && !TextUtils.isEmpty(class_from) && class_from.equals("BindingHouse")) {

            bindService.BindingHouse(community_id, room_id, handlerBindingHouse);

        }
    }


    // 监听返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
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
