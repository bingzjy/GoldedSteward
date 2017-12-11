package com.ldnet.activity.qindian;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.amap.api.maps.model.Text;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.QinDianService;
import com.ldnet.utility.UserInformation;

/**
 * Created by lee on 2017/9/4.
 */
public class QinDianMain extends BaseActionBarActivity {
    private TextView tvTitle;
    private ImageButton btnBack;
    private Button btnChargeMoney, btnChargeBattery, btnMyAccount;
    private QinDianService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qindian_main);
        service = new QinDianService(this);

        showProgressDialog1();

        initView();
        //注册
        register();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_page_title);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnChargeMoney = (Button) findViewById(R.id.btn_money_charge);
        btnChargeBattery = (Button) findViewById(R.id.btn_battery_charge);
        btnMyAccount = (Button) findViewById(R.id.btn_account);
        tvTitle.setText(getResources().getString(R.string.qindian_main_title));
        btnBack.setOnClickListener(this);
        btnMyAccount.setOnClickListener(this);
        btnChargeBattery.setOnClickListener(this);
        btnChargeMoney.setOnClickListener(this);
    }


    private void register() {
        if (!TextUtils.isEmpty(UserInformation.getUserInfo().CZAID)) {
            if (TextUtils.isEmpty(UserInformation.getUserInfo().CZAUserId)) {
                //当前用户没有注册充智安用户
                service.registerCZAUser(registerHandler);
            }else{
                service.loginCZAUser(loginHandler);
                closeProgressDialog1();
                btnMyAccount.setEnabled(true);
                btnChargeMoney.setEnabled(true);
                btnChargeBattery.setEnabled(true);
            }
        } else {
            //当前小区没有开通充智安
            showToast("当前小区暂时未开通充电服务");
            closeProgressDialog1();
            btnMyAccount.setEnabled(false);
            btnChargeMoney.setEnabled(false);
            btnChargeBattery.setEnabled(false);
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.btn_account:
                    gotoActivity(QinDianAccount.class.getName(), null);
                    break;
                case R.id.btn_money_charge:
                    gotoActivity(ChargeMoneyActivity.class.getName(), null);
                    break;
                case R.id.btn_battery_charge:
                    gotoActivity(ChargeBatteryActivity.class.getName(), null);
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    Handler registerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (msg.obj != null) {
                        //保存用户充智安CZAUserId信息
                        User user = UserInformation.getUserInfo();
                        user.CZAUserId = msg.obj.toString();
                        UserInformation.setUserInfo(user);

                        //登录
                        service.loginCZAUser(loginHandler);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    closeProgressDialog1();
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog1();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    User user=UserInformation.getUserInfo();
                    user.setCZAToken(msg.obj.toString());
                    UserInformation.setUserInfo(user);
                    showToast("登录成功");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                showToast(msg.obj.toString());
                        break;
            }
        }
    };
}
