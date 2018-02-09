package com.ldnet.activity.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PropertyFeePayTypeActivity extends BaseActionBarActivity{

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.btn_pay_complete)
    Button btnPayComplete;
    @BindView(R.id.chk_alipay_type_checked)
    CheckBox chkAliPay;
    @BindView(R.id.ll_pay_type_alipay)
    LinearLayout llPayTypeAlipay;
    @BindView(R.id.chk_wxpay_type_checked2)
    CheckBox chkWXPay;
    @BindView(R.id.ll_pay_type_wxpay)
    LinearLayout llPayTypeWxpay;

    private int payType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_fee_pay_type);
        ButterKnife.bind(this);

        tvPageTitle.setText("支付方式");
    }

    @OnClick({R.id.btn_back, R.id.btn_pay_complete, R.id.chk_alipay_type_checked, R.id.chk_wxpay_type_checked2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_pay_complete:
                if (payType == 0) { //支付宝
                    try {
                        Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                    }
                } else {  //微信
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                    intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
                    startActivity(intent);
                }
                break;
            case R.id.chk_alipay_type_checked:
                payType = 0;
                chkWXPay.setChecked(false);
                break;
            case R.id.chk_wxpay_type_checked2:
                payType = 1;
                chkAliPay.setChecked(false);
                break;
        }
    }
}
