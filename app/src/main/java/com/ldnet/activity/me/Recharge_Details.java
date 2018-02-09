package com.ldnet.activity.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.RechargeMessage;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.third.Alipay.PayKeys;
import com.third.Alipay.PayResult;
import com.third.Alipay.SignUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by zxs on 2016/2/16.
 */
public class Recharge_Details extends BaseActionBarActivity implements CompoundButton.OnCheckedChangeListener, TextWatcher {
    // 标题
    private ImageButton mBack;
    private TextView mTitle;
    //充值金额
    private EditText mEtPayMoney;
    private String mRechargeAmount;
    //充值方式
    private CheckBox mUnionCheckBox, mAlipayCheckBox;
    //确认充值
    private Button mSureRecharge;
    //银联tn，环境（00正式，01测试）
    private String tn;
    private String serverMode = "00";
    private Services mServices;
    // 支付宝支付结果标识
    private static final int SDK_PAY_FLAG = 1;
    // 支付宝账号检查标识
    private static final int SDK_CHECK_FLAG = 2;

    // 支付相关信息配置
    PayKeys keys;
    private RechargeMessage mRechargeMessage;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_details);
        AppUtils.setupUI(findViewById(R.id.ll_recharge_details),this);
        mServices = new Services();
        // 标题
        mBack = (ImageButton) findViewById(R.id.btn_back);
        mTitle = (TextView) findViewById(R.id.tv_page_title);
        mTitle.setText("充值");
        //充值金额
        mEtPayMoney = (EditText) findViewById(R.id.et_recharge_money);
        //充值方式
        mAlipayCheckBox = (CheckBox) findViewById(R.id.chk_pay_type_checked);
        mUnionCheckBox = (CheckBox) findViewById(R.id.chk_union_pay_type_checked);
        //确认充值
        mSureRecharge = (Button) findViewById(R.id.btn_recharge);
        initEvent();

    }

    public void initEvent() {
        mBack.setOnClickListener(this);
        mEtPayMoney.addTextChangedListener(this);
        mAlipayCheckBox.setOnCheckedChangeListener(this);
        mUnionCheckBox.setOnCheckedChangeListener(this);
        mSureRecharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回
                try {
                    gotoActivityAndFinish(Recharge.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_recharge://充值
                if (!mUnionCheckBox.isChecked() && !mAlipayCheckBox.isChecked()) {
                    Toast.makeText(this, "请选择充值方式", Toast.LENGTH_SHORT).show();
                } else {
                    mDialog = ProgressDialog.show(this, "", "正在充值...", true);

                    //充值类型：1：支付宝 2：银联 3：充值卡 4：微信
                    String payType = "0";
                    //充值卡卡号
                    String cardNmber = "";
                    if (mAlipayCheckBox.isChecked()) {
                        payType = "1";
                        cardNmber = "";
                    }
                    if (mUnionCheckBox.isChecked()) {
                        payType = "2";
                        cardNmber = "";
                    }
                    //获得充值信息
                    mRechargeAmount = String.valueOf(mEtPayMoney.getText().toString().trim());
                    CreateRecharge(mRechargeAmount, payType, cardNmber);
                    break;
                }
        }
    }

    public static int temp = -1;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            // 这段代码来实现单选功能
            if (temp != -1) {
                CheckBox tempButton = (CheckBox) Recharge_Details.this.findViewById(temp);
                if (tempButton != null) {
                    tempButton.setChecked(false);
                }
            }
            //得到当前的position
            temp = buttonView.getId();
        } else {
            temp = -1;
        }
    }

    // 创建充值信息
    // GET BAccount/APP_CreateRecharge?ResidentID={ResidentID}&PaymentMoney={PaymentMoney}&RechargeType={RechargeType}&CardNumber={CardNumber}
    public void CreateRecharge(String paymentMoney, String rechargeType, String cardNumber) {
        // 请求的URL
        String url = Services.mHost + "BAccount/APP_CreateRecharge?ResidentID=%s&PaymentMoney=%s&RechargeType=%s&CardNumber=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, paymentMoney, rechargeType, cardNumber);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
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
                                    Gson gson = new Gson();
                                    mRechargeMessage = gson.fromJson(jsonObject.getString("Obj"),RechargeMessage.class);
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDialog.cancel();
                                        }
                                    }, 1000);
                                    if (mAlipayCheckBox.isChecked()) {
                                        onPayClick();
                                    }
                                    if (mUnionCheckBox.isChecked()) {
                                        doStartUnionPayPlugin(Recharge_Details.this, mRechargeMessage.TN, serverMode);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 调用银联
    public void doStartUnionPayPlugin(Activity activity, String tn, String serverMode) {
        //UPPayAssistEx.startPay(activity, null, null, tn, serverMode);
    }

    //编辑框改变的监听事件
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // checkStatus();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        reChargeButtonStatus();
    }

    // 充值按钮的状态
    private void reChargeButtonStatus() {
        if (!TextUtils.isEmpty(mEtPayMoney.getText().toString().trim())) {
            mSureRecharge.setEnabled(true);
        } else {
            mSureRecharge.setEnabled(false);
        }
    }

    //支付按钮点击事件
    private void onPayClick() {
        //检查支付配置信息
        if (TextUtils.isEmpty(keys.PARTNER) || TextUtils.isEmpty(keys.RSA_PRIVATE) || TextUtils.isEmpty(keys.SELLER)) {
            new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle("警告")
                    .setMessage("需要配置[PARTNER | RSA_PRIVATE | SELLER]")
                    .setPositiveButton(R.string.sure_information,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    finish();
                                }
                            }).show();
            return;
        }

        // 订单
        String orderInfo = getOrderInfo();
        // 对订单做RSA 签名
        String sign = SignUtils.sign(orderInfo, keys.RSA_PRIVATE);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&sign_type=\"RSA\"";

        //异步调用支付接口
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(Recharge_Details.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);
                //TODO
                android.os.Message msg = new android.os.Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    // mSubject = getString(R.string.common_me_company);
//    mDescription = "购买商品总价：" + mOrders.AM + "元";
    //订单信息
    private String getOrderInfo() {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + keys.PARTNER + "\"";
        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + keys.SELLER + "\"";
        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + mRechargeMessage.PayOrder + "\"";
        // 商品名称
        orderInfo += "&subject=" + "\"" + getString(R.string.common_me_company) + "\"";
        // 商品详情
        orderInfo += "&body=" + "\"" + "购买商品总价：" + mRechargeMessage.Moneys + "元" + "\"";
        // 商品金额
        orderInfo += "&total_fee=" + "\"" + mRechargeMessage.Moneys + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + Services.getPayCallBack() + "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";
        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";
        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";
        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    //异步支付完成后接收支付结果
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showToast("充值成功");
                        try {
                            gotoActivityAndFinish(Recharge.class.getName(), null);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(Pay.this, R.string.mall_pay_succeed, Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            showToast("充值结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            showToast("充值取消");
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    showToast("检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
        }


    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        final String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "充值成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "充值失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了充值";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("充值结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    if (str.equalsIgnoreCase("success")) {
                        gotoActivityAndFinish(Recharge.class.getName(), null);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.create().show();
    }
}
