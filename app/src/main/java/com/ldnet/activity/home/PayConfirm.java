package com.ldnet.activity.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinaums.pppay.unify.UnifyPayListener;
import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.UnifyPayRequest;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.PropertyFeeService;
import com.tendcloud.tenddata.TCAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lee on 2017/7/26.
 */
public class PayConfirm extends BaseActionBarActivity implements UnifyPayListener {

    private TextView tvHouseInfo, tvFee, tvPayDate;
    private Button payConfirm;
    private PropertyFeeService service;
    private String orderId;
    private static final String TAG = "PayConfirm";

    private ImageView headerBack;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm);
        service = new PropertyFeeService(this);

        initView();
        initData();
    }


    void initView() {
        tvHouseInfo = (TextView) findViewById(R.id.tv_fee_house);
        tvFee = (TextView) findViewById(R.id.tv_fee_title);
        tvPayDate = (TextView) findViewById(R.id.tv_pay_date);
        payConfirm = (Button) findViewById(R.id.btn_pay_complete);
        headerBack = (ImageView) findViewById(R.id.btn_back);
        headerBack.setOnClickListener(this);
        payConfirm.setOnClickListener(this);
    }


    void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            tvFee.setText("物业费  " + intent.getStringExtra("fee").toString());
            tvHouseInfo.setText(intent.getStringExtra("house").toString());
            tvPayDate.setText(format.format(Calendar.getInstance().getTime()));
            String channel = intent.getStringExtra("PayType");
            orderId = intent.getStringExtra("OrderId");
            String payInfo = intent.getStringExtra("PayInfo");

            try {
                JSONObject jsonObject = new JSONObject(payInfo);
                String payUrl = jsonObject.getString("appPayRequest");
                toPay(payUrl, channel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_pay_complete:   //查看订单状态，是否支付成功
                showProgressDialog();
                service.getNewUnionPayResult(orderId, handlerCallBack);
                break;
        }
    }


    Handler handlerCallBack = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast("支付成功");
                    finish();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj == null ? getString(R.string.network_error) : msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @OnClick(R.id.btn_back)
    public void onViewClicked() {
    }


    @Override
    public void onResult(String s, String s1) {
        Log.e(TAG, "支付结果：" + s + "   " + s1);
    }



    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "物业交费-确认支付完成" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "物业交费-确认支付完成" + this.getClass().getSimpleName());
    }


    //调取微信或者支付宝的支付界面
    private void toPay(String payInfo, String channel) {
        UnifyPayPlugin payPlugin;
        UnifyPayRequest payRequest;
        payPlugin = UnifyPayPlugin.getInstance(this);
        payRequest = new UnifyPayRequest();
        payPlugin.setListener(PayConfirm.this);
        if (!TextUtils.isEmpty(channel) && channel.equals("1")) {
            payRequest.payChannel = UnifyPayRequest.CHANNEL_ALIPAY;
        } else if (!TextUtils.isEmpty(channel) && channel.equals("0")) {
            payRequest.payChannel = UnifyPayRequest.CHANNEL_WEIXIN;
        }

        payRequest.payData = payInfo;
        payPlugin.sendPayRequest(payRequest);

        payPlugin.setListener(new UnifyPayListener() {
            @Override
            public void onResult(String s, String s1) {
                Log.e(TAG, "支付回调：" + s + "s1:" + s1);
            }
        });
    }



}
