package com.ldnet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import android.widget.Toast;
import com.google.gson.Gson;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.HouseRent;
import com.ldnet.entities.StatusBoolean;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.utility.*;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * ***************************************************
 * 忘记密码（密码找回）
 * ***************************************************
 */
public class Forgot extends BaseActionBarActivity {

    // 标题
    private TextView tv_page_title;
    // 返回
    private ImageButton btn_back;

    // 布局
    private LinearLayout ll_forgot_valid_account;
    private LinearLayout ll_forgot_change_password;

    // 验证账号
    private EditText et_forgot_phone;
    private EditText et_forgot_valid;
    private Button btn_forgot_validCode;
    private Button btn_forgot_next;

    // 修改密码
    private EditText et_forgot_password;
    private EditText et_forgot_password_valid;
    private Button btn_forgot_confirm;

    // 服务
    private Services services;

    // 定时器计数器
    private Timer timer;// = new Timer();
    private int mTimerCount;
    private AcountService acountService;
    // 初始化事件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 点击事件监听
        initView();

        acountService=new AcountService(this);
        btn_forgot_confirm.setOnClickListener(this);
        btn_forgot_validCode.setOnClickListener(this);
        btn_forgot_next.setOnClickListener(this);
        btn_back.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "忘记密码：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "忘记密码：" + this.getClass().getSimpleName());
    }

    // 初始化控件
    public void initView() {

        // 设置布局
        setContentView(R.layout.activity_forgot);
        AppUtils.setupUI(findViewById(R.id.ll_forgot), this);
        // 初始化服务
        services = new Services();

        // 标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.activity_forgot_title);

        // 返回
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        // 布局
        ll_forgot_valid_account = (LinearLayout) findViewById(R.id.ll_forgot_valid_account);
        ll_forgot_change_password = (LinearLayout) findViewById(R.id.ll_forgot_change_password);

        // 确认账号
        et_forgot_phone = (EditText) findViewById(R.id.et_forgot_phone);
        et_forgot_valid = (EditText) findViewById(R.id.et_forgot_valid);
        btn_forgot_validCode = (Button) findViewById(R.id.btn_forgot_validCode);
        btn_forgot_next = (Button) findViewById(R.id.btn_forgot_next);

        // 修改密码
        et_forgot_password = (EditText) findViewById(R.id.et_forgot_password);
        et_forgot_password_valid = (EditText) findViewById(R.id.et_forgot_password_valid);
        btn_forgot_confirm = (Button) findViewById(R.id.btn_forgot_confirm);
    }

    // 点击事件处理
    @Override
    public void onClick(View v) {
        super.onClick(v);
        // 获取用户输入
        final String phone = et_forgot_phone.getText().toString().trim();
        String code = et_forgot_valid.getText().toString().trim();
        String password = et_forgot_password.getText().toString().trim();
        String validPassword = et_forgot_password_valid.getText().toString()
                .trim();
        // 异常捕获并输出相关信息
        try {
            switch (v.getId()) {
                // 获取验证
                case R.id.btn_forgot_validCode:
                    btn_forgot_validCode.setEnabled(false);
                    getToken(phone);
                    // 开启定时器
                    timer = new Timer();
                    mTimerCount = 0;
                    timer.schedule(task, 1000, 1000);


                    break;
                // 下一步按钮，验证验证码
                case R.id.btn_forgot_next:
                    if(isNUll()){
                        VaildCode(phone, code, "1");
                    }
                    break;
                // 修改密码确认
                case R.id.btn_forgot_confirm:
                    // 修改密码，如果成功则跳转
                    if(isNUll1()){
                        ChangePassword(phone, password);
                    }
                    break;
                // 返回按钮
                case R.id.btn_back:
                    super.gotoActivityAndFinish(Login.class.getName(), null);
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

    public boolean isNUll(){
        if (TextUtils.isEmpty(et_forgot_phone.getText().toString().trim())) {
            showToast("手机号码不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_forgot_valid.getText().toString().trim())) {
            showToast("验证码不能为空");
            return false;
        }
        return true;
    }

    public boolean isNUll1(){
        if (TextUtils.isEmpty(et_forgot_password.getText().toString().trim())) {
            showToast("密码不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_forgot_password_valid.getText().toString().trim())) {
            showToast("确认密码不能为空");
            return false;
        }
        if(!et_forgot_password_valid.getText().toString().equals(et_forgot_password.getText().toString())){
            showToast("两次密码不一致");
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                gotoActivityAndFinish(Login.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    // 验证手机号码是否存在
    public void VaildPhone(String tel) {
        // 请求的URL
        // Get请求,参数为电话号码
        String url = Services.mHost + "API/Account/PhoneExists/%s";
        url = String.format(url, tel);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = tel + aa + aa1 + aa2 + Services.TOKEN;
        Log.d("asdsdasd","11111111");
        OkHttpUtils.get().url(url)
                .addHeader("phone", tel)
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
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        Log.d("asdsdasd","22222");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (Services.isPhone(et_forgot_phone.getText().toString()) && jsonObject.getBoolean("Valid")) {
                                new Thread() {
                                    public void run() {
                                        // 调用接口发送短信
                                        GetCode(et_forgot_phone.getText().toString(), "1");

                                    }

                                    ;
                                }.start();

                            } else {
                                showToast(getResources().getString(
                                                R.string.activity_forgot_valid_desc));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 获取验证码
    public void GetCode(String tel, String type) {
        // 请求的URL
        // Get请求,第一个参数电话、第二个参数类型是指注册（0）还是忘记密码（1）
        String url = Services.mHost + "API/Account/RegisterSendSMS/%s/%s";
        url = String.format(url, tel, type);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = tel + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", tel)
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
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Message message = new Message();
                                    message.what = 100;
                                    handler.sendMessage(message);
                                } else {
                                    Message message = new Message();
                                    message.what = 101;
                                    message.obj = json.getString("Message");
                                    handler.sendMessage(message);
                                }
                            } else{
                                showToast(jsonObject.getString("Message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 验证验证码正确性
    public void VaildCode(String tel, String code, String type) {
        // 请求的URL
        // Get请求,第一个参数电话、第二个参数验证码、第三个参数类型是指注册（0）还是忘记密码（1）
        String url = Services.mHost + "API/Account/ValidSNSCode/%s/%s/%s";
        url = String.format(url, tel, code, type);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = tel + aa + aa1 + aa2 + Services.TOKEN;

        OkHttpUtils.get().url(url)
                .addHeader("phone", tel)
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
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
                        Log.d("asdsdasd", "111111111" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    ll_forgot_valid_account.setVisibility(View.GONE);
                                    ll_forgot_change_password.setVisibility(View.VISIBLE);
                                } else {
                                    showToast(getResources().getString(R.string.get_valid_error));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 修改密码
    public void ChangePassword(String tel, String password) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URLd
        String url = Services.mHost + "API/Account/ResetPassword";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Tel", tel);
        extras.put("Password", password);
        Services.json(extras);
        String md5 = tel +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", tel)
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("Tel", tel)
                .addParams("Password", password)
                .build()
                .execute(new DataCallBack(this) {

                    @Override
                    public void onBefore(Request request, int id) {
                        showProgressDialog();
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd1111", "111111111" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    // 修改密码成功后，直接登录
                                    showToast("密码修改成功");
                                    getData(et_forgot_phone.getText().toString().trim(), et_forgot_password.getText().toString().trim());
                                } else {
                                    showToast(getResources().getString(R.string.activity_change_password_error));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getToken(final String phone) {
        String url1 = Services.mHost + "GetToken?phone=%s&clientType=22";
        url1 = String.format(url1, phone);
        OkHttpUtils.get().url(url1)
                .addHeader("phone", phone)
                .addHeader("timestamp", Services.timeFormat())
                .addHeader("nonce", (int) ((Math.random() * 9 + 1) * 100000) + "")
                .build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd---====", "----" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    String token = jsonObject.getString("Obj");
                                    TokenInformation.setTokenInfo(token);
                                    Services.TOKEN = TokenInformation.getTokenInfo().toString();
                                    VaildPhone(phone);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getData(String phone, String psd) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        final String url = Services.mHost + "API/Account/Logon";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("UserName", phone);
        extras.put("Password", psd);
        extras.put("PlatForm", "Android");
        Services.json(extras);
        String md5 = phone +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", phone)
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("UserName", phone)
                .addParams("Password", psd)
                .addParams("PlatForm", "Android")
                .build().execute(new DataCallBack(this) {

            @Override
            public String parseNetworkResponse(Response response, int id) throws IOException {
                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    if (headers.values("Set-Cookie").size() > 0) {
                        List<String> cookies = headers.values("Set-Cookie");
                        CookieInformation.setCookieInfo("cookies", cookies.get(0));
                    }
                }
                return super.parseNetworkResponse(response, id);
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String s, int i) {
                Log.d("asdsdasd", "--" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                    if (json.getBoolean("Status")) {
                        if (jsonObject.getBoolean("Valid")) {
                            Gson gson = new Gson();
                            User user = gson.fromJson(jsonObject.getString("Obj"), User.class);
                            UserInformation.setUserInfo(user);
                            acountService.setIntegralTip(new Handler(),url);
                            if (!TextUtils.isEmpty(user.CommunityId)) {
                                HashMap<String, String> extras = new HashMap<String, String>();
                                extras.put("LEFT", "LEFT");
                                gotoActivityAndFinish(MainActivity.class.getName(), extras);
                            } else {
                                HashMap<String, String> extras = new HashMap<String, String>();
                                extras.put("LEFT", "LEFT");
                                gotoActivityAndFinish(BindingCommunity.class.getName(), extras);
                            }
                        } else {
                            showToast(jsonObject.getString("Message"));
                        }
                    } else {
                        showToast(jsonObject.getString("Message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /* 获取验证码按钮上的倒计时 */
    Handler handler = new TimerHandler(this);

    static class TimerHandler extends Handler {
        WeakReference<Forgot> mForgot;

        public TimerHandler(Forgot forgot) {
            mForgot = new WeakReference<Forgot>(forgot);
        }

        public void handleMessage(Message msg) {
            Forgot forgot = mForgot.get();
            if (msg.what == 100) {// 发送验证码成功
                com.ldnet.utility.Toast.makeText(
                        forgot.getApplicationContext(),
                        forgot.getResources()
                                .getString(R.string.get_valid_desc),
                        1000).show();
            } else if (msg.what == 101) {// 发送验证码失败
                com.ldnet.utility.Toast.makeText(forgot.getApplicationContext(),
                        msg.obj.toString(), 1000).show();
            } else if (msg.what == 60) {
                // 取消定时器
                if (forgot.timer != null) {
                    forgot.timer.cancel();
                }
                // 修改按钮状态
                forgot.btn_forgot_validCode.setText(forgot.getResources()
                        .getString(R.string.get_valid_code));
                if (Services.isPhone(forgot.et_forgot_phone.getText().toString()
                        .trim())) {
                    forgot.btn_forgot_validCode.setEnabled(true);
                }
            } else {
                forgot.btn_forgot_validCode.setText(String
                        .valueOf(60 - forgot.mTimerCount));
            }
        }

        ;
    }

    ;

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = mTimerCount;
            handler.sendMessage(message);
            ++mTimerCount;
        }
    };
}
