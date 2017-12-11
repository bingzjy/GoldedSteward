package com.ldnet.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.ldnet.activity.ChangePassword;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Information extends BaseActionBarActivity {

    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private EditText et_me_information_name;
    private Button btn_me_information_comfirm;
    private Button mBtnMeChangePassword;

    //初始化视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_information);
        AppUtils.setupUI(findViewById(R.id.ll_information), this);
        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_information);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //修改密码
        mBtnMeChangePassword = (Button) findViewById(R.id.btn_change_password);
        //姓名编辑
        et_me_information_name = (EditText) findViewById(R.id.et_me_information_name);
        //设置用户旧的姓名
        String userName;
        User user = UserInformation.getUserInfo();
        if (!TextUtils.isEmpty(user.getUserName())) {
            userName = user.getUserName();
        } else {
            userName = user.getUserPhone();
        }
        et_me_information_name.setText(userName);
        et_me_information_name.setSelection(userName.length());
        et_me_information_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString().trim())) {
                    btn_me_information_comfirm.setEnabled(true);
                } else {
                    btn_me_information_comfirm.setEnabled(false);
                }
            }
        });
        //姓名提交
        btn_me_information_comfirm = (Button) findViewById(R.id.btn_me_information_comfirm);
        initEvent();
        //初始化服务
        services = new Services();
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_me_information_comfirm.setOnClickListener(this);
        mBtnMeChangePassword.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_me_information_comfirm:
                ChangeInformation(et_me_information_name.getText().toString().trim(), "");
                break;
            case R.id.btn_change_password://修改密码
                Intent intent_information = new Intent(this, ChangePassword.class);
                startActivity(intent_information);
                overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                break;
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void ChangeInformation(String name, String image) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        final String url = Services.mHost + "API/Resident/UpdateResident";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("nickName", name);
        extras.put("img", image);
        extras.put("Id", UserInformation.getUserInfo
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
                .addParams("Id", UserInformation.getUserInfo().getUserId())
                .addParams("nickName", name)
                .addParams("img", image)
                .build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call,e,i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
//                                    try {
                                        //重新设置本地用户信息中的用户姓名
                                        User user = UserInformation.getUserInfo();
                                        user.setUserName(et_me_information_name.getText().toString().trim());
                                        UserInformation.setUserInfo(user);
                                        IntegralTip(url);
                                        finish();
                                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);

                                } else {
                                    showToast(jsonObject.getString("Message"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void IntegralTip(String pUrl) {

        String url1 = "";
        try {
            pUrl = new URL(pUrl).getPath();
            url1 = Services.mHost + "API/Prints/Add/" + UserInformation.getUserInfo().UserId + "?route=" + pUrl;
            String aa = Services.timeFormat();
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String aa2 = url1;
            String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
            OkHttpUtils.get().url(url1)
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32(md5))
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                    .execute(new DataCallBack(this) {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            super.onError(call,e,i);
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            Log.d("asdsdasd", "111111111" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (json.getBoolean("Status")) {
                                    if (jsonObject.getBoolean("Valid")) {
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
