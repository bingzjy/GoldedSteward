package com.ldnet.activity.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.PropertyFeeService;
import com.tendcloud.tenddata.TCAgent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lee on 2017/7/26.
 */
public class PayConfirm extends BaseActionBarActivity {

    private TextView tvHouseInfo, tvFee, tvPayDate;
    private Button payConfirm;
    private PropertyFeeService service;
    private String orderId;
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
            String from = intent.getStringExtra("from");
            if (from != null && from.equals(Property_Fee.class.getName())) {
                //跳转支付宝
                String url = intent.getStringExtra("url");
                if (!TextUtils.isEmpty(url)) {
                    HashMap extra = new HashMap();
                    extra.put("url", url);
                    extra.put("from", PayConfirm.class.getName());
                    extra.put("house", intent.getStringExtra("house"));
                    extra.put("fee", intent.getStringExtra("fee"));

                    Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent2);
                }
                orderId = intent.getStringExtra("order");
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
                service.aliPayCallBack(orderId, handlerCallBack);
                break;
        }
    }

    Handler handlerCallBack = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "物业交费-确认支付完成" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "物业交费-确认支付完成" + this.getClass().getSimpleName());
    }

}
