package com.ldnet.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Cookies;
import com.ldnet.entities.HouseRent;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.utility.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TDAccount;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static com.ldnet.goldensteward.R.id.et_login_password;
import static com.ldnet.goldensteward.R.id.et_login_phone;


/**
 * ***************************************************
 * 开始广告页
 * **************************************************
 */
public class Splash extends BaseActionBarActivity {

    private ImageView iv;
    private String url = Services.mHost + "API/Account/Logon";
    private AcountService acountService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉信息栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        acountService = new AcountService(this);
        // 设置内容
        final View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);
        init(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "闪屏页："+this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "闪屏页："+this.getClass().getSimpleName());
    }



    public void init(final View view) {
        iv = (ImageView) findViewById(R.id.iv);
        /**渐变展示启动屏**/
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if (isNetworkAvailable()) {
                            // 如果Cookie存在，直接跳转，无需访问服务器进行登录
                            if (!TextUtils.isEmpty(CookieInformation.getUserInfo().getCookieinfo())) {
                                User user = UserInformation.getUserInfo();
                                showProgressDialog();
                                acountService.getToken(user.UserPhone, handlerToken);
                            } else {
                                Message msg = new Message();
                                msg.what = 1003;
                                handler.sendMessage(msg);
                            }
                        } else {
                            Message msg = new Message();
                            msg.what = 1001;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    Handler handlerToken = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    acountService.Login(UserInformation.getUserInfo().UserPhone,
                            UserInformation.getUserInfo().UserPassword, handlerLogin);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    Handler handlerLogin = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:

                    //记录用户登录行为
                    TCAgent.onLogin(UserInformation.getUserInfo().getUserId(), TDAccount.AccountType.REGISTERED, UserInformation.getUserInfo().getUserName());

                    //设置别名
                    JPushInterface.setDebugMode(true);//设置开启日志，发布关闭
                    JPushInterface.init(Splash.this);//初始化jpush
                    Set<String> tagSet = new LinkedHashSet<String>();
                    tagSet.add(UserInformation.getUserInfo().CommunityId);
                    tagSet.add(UserInformation.getUserInfo().HouseId);
                    JPushInterface.setAliasAndTags(Splash.this, UserInformation.getPushId(), tagSet, null);

                    //判断是否绑定小区，未绑定直接跳转绑定小区
                    if (TextUtils.isEmpty(UserInformation.getUserInfo().getCommunityId())) {
                        closeProgressDialog();
                        try {
                            gotoActivityAndFinish(BindingCommunity.class.getName(), null);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        acountService.setIntegralTip(handlerIntegralTip, url);
                    }

                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog();
                    //用户密码修改登陆失败，重新登陆
                    // 定义 intent
                    Intent intent = new Intent(Splash.this, Login.class);
                    intent.putExtra("password", UserInformation.getUserInfo().getUserPassword());
                    intent.putExtra("phone", UserInformation.getUserInfo().getUserPhone());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    finish();
                    // 清除保存的Cookie
                    CookieInformation.clearCookieInfo();
                    // 清除保存的用户信息
                    UserInformation.clearUserInfo();
                    TokenInformation.clearTokenInfo();
                    break;
            }
        }
    };

    Handler handlerIntegralTip = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    try {
                        if (!TextUtils.isEmpty(UserInformation.getUserInfo().CommunityId)) {
                            gotoActivityAndFinish(MainActivity.class.getName(), null);
                        } else {
                            gotoActivityAndFinish(BindingCommunity.class.getName(), null);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    //Handler处理消息
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1001:
                    showToast(getString(R.string.network_none_tip));
                    break;
                case 1002:
                    //记录用户登录行为
                    TCAgent.onLogin(UserInformation.getUserInfo().getUserId(), TDAccount.AccountType.REGISTERED, UserInformation.getUserInfo().getUserName());
                    try {
                        gotoActivityAndFinish(MainActivity.class.getName(), null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1003:
                    try {
                        gotoActivityAndFinish(Login.class.getName(), null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //判断当前是否有可用的网路链接
    private Boolean isNetworkAvailable() {
        Context context = getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }
        return false;
    }
}
