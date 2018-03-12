package com.ldnet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.utility.*;
import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TDAccount;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * ***************************************************
 * 注册
 * **************************************************
 */
public class Register extends BaseActionBarActivity {

    // 标题
    private TextView tv_page_title;
    // 返回
    private ImageButton btn_back;
    // 获取验证码按钮
    private Button btn_register_validCode;
    // 验证手机号码按钮（下一步）
    private Button btn_register_valid_phone;
    // 注册按钮
    private Button btn_register_register;
    // 用户电话
    private EditText et_register_phone;
    // 验证码
    private EditText et_register_valid;
    // 用户密码
    private EditText et_register_password;
    // 推荐人电话
    private EditText et_register_referrer;
    // 验证电话号码表单
    private LinearLayout ll_register_valid_phone;
    // 注册-密码+邀请人表单
    private LinearLayout ll_register_password_referrer;
    // 服务
    private Services services;
    // 定时器计数器
    private Timer timer;// = new Timer();
    private MyTimerTask task;
    private int mTimerCount;
    private CheckBox mCkbAgreement;
    private TextView mTvAgreement;
    private Boolean mIsChecked;
    private final int IS_OWNER = 1;
    private final int IS_NOT_OWNER = 2;

    private AcountService service;
    // 初始化事件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        service = new AcountService(this);
        // 监听注册按钮事件
        btn_back.setOnClickListener(this);
        btn_register_validCode.setOnClickListener(this);
        btn_register_valid_phone.setOnClickListener(this);
        btn_register_register.setOnClickListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "注册页：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "注册页：" + this.getClass().getSimpleName());
    }



    // 初始化控件
    public void initView() {
        // 设置布局
        setContentView(R.layout.activity_register);
        AppUtils.setupUI(findViewById(R.id.ll_register), this);
        // 初始化服务
        services = new Services();

        // 切换界面
        ll_register_valid_phone = (LinearLayout) findViewById(R.id.ll_register_valid_phone);
        ll_register_password_referrer = (LinearLayout) findViewById(R.id.ll_register_password_referrer);

        // 标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.activity_register_title);

        // 注册按钮
        btn_register_register = (Button) findViewById(R.id.btn_register_register);

        // 返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        // 获取验证码按钮
        btn_register_validCode = (Button) findViewById(R.id.btn_register_validCode);

        // 验证手机号码按钮（下一步）
        btn_register_valid_phone = (Button) findViewById(R.id.btn_register_valid_phone);

        mTvAgreement = (TextView) findViewById(R.id.tv_agreement);
        mTvAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Browser.class);
                intent.putExtra("PAGE_URL", "http://www.goldwg.com:88/mobile/yonghuxieyi");
                intent.putExtra("FROM_CLASS_NAME", Register.class.getName());
                startActivity(intent);
            }
        });
        // 验证码
        et_register_valid = (EditText) findViewById(R.id.et_register_valid);
        // 用户密码
        et_register_password = (EditText) findViewById(R.id.et_register_password);
        et_register_referrer = (EditText) findViewById(R.id.et_register_referrer);

        // 监听用户电话输入
        et_register_phone = (EditText) findViewById(R.id.et_register_phone);
        //服务协议相关
        mCkbAgreement = (CheckBox) findViewById(R.id.ckb_agreement);
        mCkbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !TextUtils.isEmpty(et_register_valid.getText().toString()) && !TextUtils.isEmpty(et_register_phone.getText().toString())) {
                    btn_register_valid_phone.setEnabled(true);
                } else {
                    btn_register_valid_phone.setEnabled(false);
                }
            }
        });
    }


    public boolean isNUll() {
        if (TextUtils.isEmpty(et_register_phone.getText().toString().trim())) {
            showToast(getString(R.string.phone_is_null));
            return false;
        }
        if (TextUtils.isEmpty(et_register_valid.getText().toString().trim())) {
            showToast(getString(R.string.valid_is_null));
            return false;
        }
        return true;
    }

    public boolean isNUll1() {
        if (TextUtils.isEmpty(et_register_password.getText().toString().trim())) {
            showToast(getString(R.string.password_is_null));
            return false;
        }
        return true;
    }

    // 点击事件处理
    @Override
    public void onClick(View v) {
        super.onClick(v);
        try {
            switch (v.getId()) {
                // 获取验证码
                case R.id.btn_register_validCode:
                    final String phone = et_register_phone.getText().toString().trim();
                    if (Services.isPhone(phone)) {
                        btn_register_validCode.setEnabled(false);
                        // 开启定时器
                        if (timer != null) {
                            if (task != null) {
                                task.cancel();
                            }
                        }
                        timer = new Timer();
                        mTimerCount = 0;
                        task = new MyTimerTask();
                        timer.schedule(task, 1000, 1000);

                        new Thread() {
                            public void run() {
                                // 调用接口发送短信
                                service.getToken(phone,handlerToken);
                            }
                        }.start();
                    } else if (!Services.isPhone(phone) && !phone.equals("")) {
                        showToast(getString(R.string.phone_error));
                    } else if (TextUtils.isEmpty(phone)) {
                        showToast(getString(R.string.phone_is_null));
                    }
                    break;
                // 注册按钮
                case R.id.btn_register_register:
                    if (isNUll1()) {
                        showProgressDialog();
                        service.Register(
                                et_register_phone.getText().toString().trim(),
                                et_register_valid.getText().toString().trim(),
                                et_register_password.getText().toString()
                                        .trim(), et_register_referrer.getText()
                                        .toString().trim(),handlerRegister);
                    }
                    break;
                // 返回按钮
                case R.id.btn_back:
                    super.gotoActivityAndFinish(Login.class.getName(), null);
                    break;
                // 下一步按钮
                case R.id.btn_register_valid_phone:
                    if (isNUll() && Services.isPhone(et_register_phone.getText()
                            .toString().trim())) {
                        if (mCkbAgreement.isChecked()) {

                            showProgressDialog();
                            service.VaildCode(et_register_phone.getText()
                                    .toString().trim(), et_register_valid.getText()
                                    .toString().trim(), "0", handlerValidCode);

                        } else {
                            showToast(getString(R.string.check_accepted));
                        }
                    } else if (!TextUtils.isEmpty(et_register_phone.getText()
                            .toString().trim()) && !Services.isPhone(et_register_phone.getText()
                            .toString().trim())) {
                        showToast(getString(R.string.phone_error));
                    }
                    break;
                default:
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    Handler handlerToken = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (!TextUtils.isEmpty(msg.obj.toString()) && msg.obj.toString().equals(et_register_phone.getText().toString().trim())) {
                        service.GetCode(msg.obj.toString(), "0", handler);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };


    //验证验证码
    Handler handlerValidCode = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    closeProgressDialog();
                    ll_register_valid_phone.setVisibility(View.GONE);
                    ll_register_password_referrer.setVisibility(View.VISIBLE);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public  Handler handlerRegister=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    service.getData(et_register_phone.getText().toString().trim(),
                            et_register_password.getText().toString().trim(), 0,handlerGetData);


                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerGetData=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    int type=Integer.parseInt(msg.obj.toString());
                    if (type == 0) {
                        service.checkIsOwner(handlerCheckIsOwner);
                    } else if (type == IS_OWNER) {
                        service.IntegralTip(IS_OWNER,handlerIntegralTip);
                    }

                    //记录注册行为
                    TCAgent.onRegister(UserInformation.getUserInfo().getUserId(), TDAccount.AccountType.REGISTERED, UserInformation.getUserInfo().getUserName());
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerCheckIsOwner =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (msg.obj!= null) {
                        showToast(msg.obj.toString());
                        service.getData(UserInformation.getUserInfo().getUserPhone(), UserInformation.getUserInfo().getUserPassword(),
                                IS_OWNER,handlerGetData);
                    } else{
                        showToast(getString(R.string.network_error));
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    service.IntegralTip(IS_NOT_OWNER,handlerIntegralTip);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };



    Handler handlerIntegralTip =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    int type=Integer.parseInt(msg.obj.toString());
                    if (type ==IS_OWNER) {
                        try {
                            gotoActivityAndFinish(MainActivity.class.getName(), null);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (type == IS_NOT_OWNER) {
                        try {
                            gotoActivityAndFinish(BindingCommunity.class.getName(), null);
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                gotoActivityAndFinish(Login.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    /* 获取验证码按钮上的倒计时 */
    Handler handler = new TimerHandler(this);

    static class TimerHandler extends Handler {
        WeakReference<Register> mRegister;
        public TimerHandler(Register register) {
            mRegister = new WeakReference<Register>(register);
        }

        public void handleMessage(Message msg) {
            Register register = mRegister.get();

            if (msg.what == 100) {// 验证码发送成功
                com.ldnet.utility.Toast.makeText(
                        register.getApplicationContext(),
                        register.getResources().getString(
                                R.string.get_valid_desc), 1000)
                        .show();
            } else if (msg.what == 101) {// 手机号码已存在
                register.btn_register_validCode.setEnabled(true);
                register.task.cancel();
                com.ldnet.utility.Toast.makeText(register.getApplicationContext(),
                        msg.obj.toString(), 1000).show();
            } else if (msg.what == 102) { // 注册成功,直接登录

            } else if (msg.what == 103) {// 注册失败
                com.ldnet.utility.Toast.makeText(
                        register.getApplicationContext(),
                        register.getResources().getString(
                                R.string.activity_register_error),
                        1000).show();
            } else if (msg.what == 60) {
                // 取消定时器
                if (register.timer != null) {
                    register.timer.cancel();
                }
                // 修改按钮状态
                register.btn_register_validCode.setText(register.getResources()
                        .getString(R.string.get_valid_code));
                if (Services.isPhone(register.et_register_phone.getText()
                        .toString().trim())) {
                    register.btn_register_validCode.setEnabled(true);
                }
            } else {
                register.btn_register_validCode.setText(String
                        .valueOf(60 - register.mTimerCount));
            }
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Message message = new Message();
            message.what = mTimerCount;
            handler.sendMessage(message);
            ++mTimerCount;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerToken.removeCallbacksAndMessages(null);
        handlerValidCode.removeCallbacksAndMessages(null);
        handlerRegister.removeCallbacksAndMessages(null);
        handlerGetData.removeCallbacksAndMessages(null);
        handlerCheckIsOwner.removeCallbacksAndMessages(null);
        handlerIntegralTip.removeCallbacksAndMessages(null);
    }





}
