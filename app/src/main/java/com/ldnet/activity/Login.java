package com.ldnet.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.google.gson.Gson;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.PushService;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ***************************************************
 * 登录页面
 * **************************************************
 */
public class Login extends BaseActionBarActivity {

    private EditText et_login_phone;
    private EditText et_login_password;
    private Button btn_login_login;
    private String url = Services.mHost + "API/Account/Logon";
    private String password = "";
    private String phone = "";
    private AcountService acountService;
    // 初始化事件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppUtils.setupUI(findViewById(R.id.rl_login), this);
        // 去掉信息栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        acountService=new AcountService(Login.this);

        initView();
    }


    // 初始化控件
    public void initView() {
        phone = getIntent().getStringExtra("phone");
        password = getIntent().getStringExtra("password");
        // 登录按钮
        btn_login_login = (Button) findViewById(R.id.btn_login_login);
        // 用户名 - 用户电话
        et_login_phone = (EditText) findViewById(R.id.et_login_phone);
        // 用户密码
        et_login_password = (EditText) findViewById(R.id.et_login_password);
        if (password != null) {
            et_login_password.setText(password);
            et_login_phone.setText(phone);
            et_login_phone.setSelection(phone.length());
        }

        // 控件事件绑定
        findViewById(R.id.ll_button_forgot).setOnClickListener(this);
        findViewById(R.id.ll_button_register).setOnClickListener(this);
        findViewById(R.id.btn_login_login).setOnClickListener(this);
    }

    public boolean isNUll() {
        if (TextUtils.isEmpty(et_login_phone.getText().toString().trim())) {
            showToast("手机号码不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_login_password.getText().toString().trim())) {
            showToast("密码不能为空");
            return false;
        }
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode==KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        try {
            switch (v.getId()) {
                // 忘记密码
                case R.id.ll_button_forgot:
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("LEFT", "LEFT");
                    gotoActivityAndFinish(Forgot.class.getName(), extras);
                    break;
                // 注册
                case R.id.ll_button_register:
                    HashMap<String, String> extras1 = new HashMap<String, String>();
                    extras1.put("LEFT", "LEFT");
                    gotoActivityAndFinish(Register.class.getName(), extras1);
                    break;
                // 登录
                case R.id.btn_login_login:
                    if (isNUll()) {
                        showProgressDialog();
                       acountService.getToken(et_login_phone.getText().toString().trim(),handlerToken);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Handler handlerToken=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    String phone = et_login_phone.getText().toString().trim();
                    String password = et_login_password.getText().toString().trim();
                    acountService.Login(phone, password, handlerLogin);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerLogin=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    //请求手机唯一码。绑定关系，用于获取推送
                     requestPermission();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerGetPush=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    //设置推送的别名
                    String pushId=msg.obj.toString();
                    UserInformation.setPushId(pushId);

                    JPushInterface.setDebugMode(true);//设置开启日志，发布关闭
                    JPushInterface.init(Login.this);//初始化jpush
                    Set<String> tagSet = new LinkedHashSet<String>();
                    tagSet.add(UserInformation.getUserInfo().CommunityId);
                    tagSet.add(UserInformation.getUserInfo().HouseId);
                    JPushInterface.setAliasAndTags(Login.this, pushId, tagSet, null);

                    //积分设置
                    acountService.setIntegralTip(handlerIntegralTip,url);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    //积分设置
                    acountService.setIntegralTip(handlerIntegralTip,url);
                    break;
            }
        }
    };


    Handler handlerIntegralTip=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast(msg.obj.toString());
                    try {
                        if (!TextUtils.isEmpty(UserInformation.getUserInfo().CommunityId)) {
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put("LEFT", "LEFT");
                            gotoActivityAndFinish(MainActivity.class.getName(), extras);
                        } else {
                            gotoActivityAndFinish(BindingCommunity.class.getName(), null);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    try {
                        if (!TextUtils.isEmpty(UserInformation.getUserInfo().CommunityId)) {
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put("LEFT", "LEFT");
                            gotoActivityAndFinish(MainActivity.class.getName(), extras);
                        } else {
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put("LEFT", "LEFT");
                            gotoActivityAndFinish(BindingCommunity.class.getName(), extras);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };



    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 110);
            } else {
                //权限已获取，做自己的处理
                //请求推送码
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String DEVICE_ID = tm.getDeviceId();
                acountService.setLoginUserPush(DEVICE_ID, handlerGetPush);
            }
        } else {
            //权限已获取，做自己的处理
            //请求推送码
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();
            acountService.setLoginUserPush(DEVICE_ID, handlerGetPush);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限已获取，做自己的处理
                    //请求推送码
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String DEVICE_ID = tm.getDeviceId();

                    acountService.setLoginUserPush(DEVICE_ID, handlerGetPush);
                } else {
                    Toast.makeText(Login.this, "请手动开启读取设备信息权限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
