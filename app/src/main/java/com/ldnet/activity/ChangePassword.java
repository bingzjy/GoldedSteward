package com.ldnet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.me.Information;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * ***************************************************
 * 修改密码
 * **************************************************
 */
public class ChangePassword extends BaseActionBarActivity {

    // 标题
    private TextView mTvPageTitle;
    // 确定
    private Button mBtnChangePassword;
    // 密码
    private EditText mEtOldPassword;
    private EditText mEtChangePassword;
    private EditText mEtChangePasswordValid;
    // 返回
    private ImageButton mBtnBack;

    private Services mServices;
    private AcountService acountService;
    // 初始化控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = new Services();
        acountService=new AcountService(this);
        // 设置布局
        setContentView(R.layout.activity_change_password);
        AppUtils.setupUI(findViewById(R.id.ll_change_psd), this);
        // 标题
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        mTvPageTitle.setText(R.string.activity_change_password_title);

        // 确定
        mBtnChangePassword = (Button) findViewById(R.id.btn_change_password);

        //输入旧密码
        mEtOldPassword = (EditText) findViewById(R.id.et_old_password);
        // 输入新密码
        mEtChangePassword = (EditText) findViewById(R.id.et_change_password);
        // 确认新密码
        mEtChangePasswordValid = (EditText) findViewById(R.id.et_change_password_valid);
        // 返回
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        initEvent();
    }

    public boolean isNUll(){
        if (TextUtils.isEmpty(mEtOldPassword.getText().toString().trim())) {
            showToast("旧密码不能为空");
            return false;
        }
        if (TextUtils.isEmpty(mEtChangePassword.getText().toString().trim())) {
            showToast("新密码不能为空");
            return false;
        }
        if (TextUtils.isEmpty(mEtChangePasswordValid.getText().toString().trim())) {
            showToast("确认密码不能为空");
            return false;
        }
        if(!mEtChangePasswordValid.getText().toString().equals(mEtChangePassword.getText().toString())){
            showToast("两次密码不一致");
            return false;
        }
        return true;
    }

    // 初始化事件
    public void initEvent() {
        // 点击事件监听
        mBtnChangePassword.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
    }

    // 登录按钮的状态
    private void loginButtonStatus() {
        String oldPassword = mEtOldPassword.getText().toString().trim();
        String newPassword = mEtChangePassword.getText().toString().trim();
        String vaildPassword = mEtChangePasswordValid.getText().toString().trim();

        if (!TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(vaildPassword)) {
            mBtnChangePassword.setEnabled(true);
        } else {
            mBtnChangePassword.setEnabled(false);
        }
    }

    // 点击事件处理
    @Override
    public void onClick(View v) {

        super.onClick(v);
        // 异常捕获并输出相关信息
//        try {
        switch (v.getId()) {
            case R.id.btn_change_password://修改成功跳转
                if (!mEtOldPassword.getText().toString().equals(UserInformation.getUserInfo().UserPassword)) {
                    showToast("原密码不正确");
                } else {
                    if(isNUll()){
                        UpdatePassword(mEtOldPassword.getText().toString().trim(), mEtChangePassword.getText().toString().trim());
                    }
                }

                break;
            case R.id.btn_back://结束修改密码
                try {
                    gotoActivityAndFinish(Information.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    // 修改密码
    public void UpdatePassword(String oldPassword, String newPassword) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URLd
        final String url = Services.mHost + "API/Account/UpdatePassword";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("OldPassword", oldPassword);
        extras.put("NewPassword", newPassword);
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
                .addParams("OldPassword", oldPassword)
                .addParams("NewPassword", newPassword)
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
                                    User user = UserInformation.getUserInfo();
                                    user.setUserPassword(mEtChangePassword.getText().toString().trim());
                                    UserInformation.setUserInfo(user);
                                    showToast("密码修改成功，请重新登录！");

                                    acountService.setIntegralTip(new Handler(),url);
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
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

}
