package com.ldnet.activity.mall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.me.OrdersTabActivity;
import com.ldnet.entities.AccountInfo;
import com.ldnet.entities.BalancePaid;
import com.ldnet.entities.OrderPay;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UISwitchButton;
import com.ldnet.utility.UserInformation;
import com.tendcloud.tenddata.TCAgent;
import com.third.Alipay.PayKeys;
import com.third.Alipay.PayResult;
import com.third.Alipay.SignUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by Alex on 2015/9/28.
 */
public class Pay extends BaseActionBarActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    // 标题
    private TextView tv_page_title, mTvPaySurplus;
    // 返回
    private ImageButton btn_back;
    private LinearLayout ll_goods_balance;
    // 支付宝支付结果标识
    private static final int SDK_PAY_FLAG = 1;
    // 支付宝账号检查标识
    private static final int SDK_CHECK_FLAG = 2;
    // 服务
    private Services services;
    // 支付相关信息配置
    private PayKeys keys;
    // 支付信息，包含订单号和金额
    private OrderPay mPayInformation;
    // 商品标题
    private String mSubject;
    // 商品描述
    private String mDescription;
    private CheckBox mChkPayTypeChecked;
    private CheckBox mUnionPayTypeChecked;
    private UISwitchButton mBalancePayTypeChecked;
    private Button mBtnPay;
    private String tn;
    private String serverMode = "00";
    private AccountInfo accountInfo;
    private TextView mBalance;
    //来自哪里-商品详细页或者购物车
    private String mFromClassName;
    private TextView tv_goods_prices;
    //订单总金额
    private Float mPaySurplusMoneys, mOnlinePay;
    BalancePaid balancePaid;
    private Float mBalanceMoney;
    private Float totalPrices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置布局
        setContentView(R.layout.activity_mall_order_pay);
        // 标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText("订单支付");
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
//        btn_back.setVisibility(View.GONE);
        //初始化服务
        services = new Services();
        //初始化支付相关信息
        keys = new PayKeys();
        //获取订单相关的信息
        mPayInformation = (OrderPay) getIntent().getSerializableExtra("ORDER_PAY");
        mSubject = getIntent().getStringExtra("SUBJECT");
        totalPrices = getIntent().getFloatExtra("totalPrices", 0);
        mDescription = getIntent().getStringExtra("DESCRIPTION");
        mFromClassName = getIntent().getStringExtra("FROM_CLASS_NAME");
        mChkPayTypeChecked = (CheckBox) findViewById(R.id.chk_pay_type_checked);
        mUnionPayTypeChecked = (CheckBox) findViewById(R.id.chk_union_pay_type_checked);
        mBalancePayTypeChecked = (UISwitchButton) findViewById(R.id.chk_balance_pay_type_checked);
        mBalancePayTypeChecked.setChecked(false);
        ll_goods_balance = (LinearLayout) findViewById(R.id.ll_goods_balance);
        mBalance = (TextView) findViewById(R.id.text_use_balance);
        getUserId();
        mTvPaySurplus = (TextView) findViewById(R.id.text_pay_surplus);
        //订单总金额
        tv_goods_prices = (TextView) findViewById(R.id.tv_goods_prices);
        tv_goods_prices.setText(mDescription);
        initEvent();
    }


    public void initEvent() {
        mChkPayTypeChecked.setOnCheckedChangeListener(this);
        mUnionPayTypeChecked.setOnCheckedChangeListener(this);
        mBalancePayTypeChecked.setOnCheckedChangeListener(this);
        btn_back.setOnClickListener(this);
        ll_goods_balance.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "订单支付：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "订单支付：" + this.getClass().getSimpleName());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mBalancePayTypeChecked.isChecked()) {
            mChkPayTypeChecked.setVisibility(View.GONE);
            mUnionPayTypeChecked.setVisibility(View.GONE);
            mUnionPayTypeChecked.setChecked(false);
            mChkPayTypeChecked.setChecked(false);
            if (mPayInformation.Amount <= mBalanceMoney) {
                //  商品总价小于等于余额，支付按钮不显示钱并且不能选择其他支付方式
                mTvPaySurplus.setText("");
                //   * TODO
                mChkPayTypeChecked.setChecked(false);
                mUnionPayTypeChecked.setChecked(false);
                // 余额支付的钱等于商品总价
                mPaySurplusMoneys = mPayInformation.Amount;
                mTvPaySurplus.setText(("确认支付  ￥" + mPaySurplusMoneys));
            } else {
                //  商品总价大于余额，显示剩余支付的钱
                BigDecimal a1 = new BigDecimal(Float.toString(mPayInformation.Amount));
                BigDecimal a2 = new BigDecimal(Float.toString(mBalanceMoney));
                mOnlinePay = a1.subtract(a2).floatValue();
                mTvPaySurplus.setText(("确认支付  ￥" + mOnlinePay));
                //余额支付的钱等于余额
                mPaySurplusMoneys = mBalanceMoney;
            }
        } else {
            mChkPayTypeChecked.setVisibility(View.VISIBLE);
            mUnionPayTypeChecked.setVisibility(View.VISIBLE);
            if (!mChkPayTypeChecked.isChecked() && !mUnionPayTypeChecked.isChecked()) {
                mTvPaySurplus.setText("确认支付");
            } else {
                mTvPaySurplus.setText("确认支付  ￥" + mPayInformation.Amount);
            }
        }

        switch (buttonView.getId()) {
            case R.id.chk_pay_type_checked:
                if (mChkPayTypeChecked.isChecked()) {
                    mChkPayTypeChecked.setChecked(true);
                    mUnionPayTypeChecked.setChecked(false);
                }
                break;
            case R.id.chk_union_pay_type_checked:
                if (mUnionPayTypeChecked.isChecked()) {
                    mUnionPayTypeChecked.setChecked(true);
                    mChkPayTypeChecked.setChecked(false);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.ll_goods_balance://支付不同方式

                //余额支付
                Float amt = 0.0f;
                if (mBalancePayTypeChecked.isChecked()) {
                    if (!mChkPayTypeChecked.isChecked() && !mUnionPayTypeChecked.isChecked()
                            && mPayInformation.Amount > mBalanceMoney) {
                        showToast("余额不足，请选择其他支付方式");
                    } else {
                        //计算余额支付不足的情况
                        if (mPayInformation.Amount > mBalanceMoney) {
                            amt = mOnlinePay;
                        }
                        //支付类型判断
                        String payType = "0";
                        if (mChkPayTypeChecked.isChecked()) {
                            payType = "2";
                        }
                        if (mUnionPayTypeChecked.isChecked()) {
                            payType = "3";
                        }
                        // 只用余额支付需确定
                        if (mBalancePayTypeChecked.isChecked() && !mChkPayTypeChecked.isChecked() && !mUnionPayTypeChecked.isChecked() && mPayInformation.Amount <= mBalanceMoney) {
                            SurePayDialog();
                        } else {
                            //余额混合支付
                            balancePaid(mPayInformation.OrderPayNumber, String.valueOf(mPaySurplusMoneys), payType, "0");
                        }
                    }
                } else {
                    amt = mPayInformation.Amount;
                }
                //银联支付
                if (mUnionPayTypeChecked.isChecked()) {
                    unionPay(mPayInformation.OrderPayNumber, String.valueOf(amt));
                }
                //支付宝支付
                if (mChkPayTypeChecked.isChecked()) {
                    onPayClick(amt);
                }
                break;
        }
    }


    //余额支付显示对话框
    private void SurePayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("余额支付")
                .setCancelable(false)
                .setPositiveButton("确定", new SurePayDialogClass())
                .setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    class SurePayDialogClass implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:// 确认支付
                    balancePaid(mPayInformation.OrderPayNumber, String.valueOf(mPaySurplusMoneys), "0", "1");
                    break;
            }
        }
    }

    // 调用银联
    public void doStartUnionPayPlugin(Activity activity, String tn, String serverMode) {
        //UPPayAssistEx.startPay(activity, null, null, tn, serverMode);
    }

    //支付按钮点击事件
    private void onPayClick(float amount) {
        //检查支付配置信息
        if (TextUtils.isEmpty(keys.PARTNER) || TextUtils.isEmpty(keys.RSA_PRIVATE) || TextUtils.isEmpty(keys.SELLER)) {
            new AlertDialog.Builder(this)
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
        String orderInfo = getOrderInfo(amount);
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
                PayTask alipay = new PayTask(Pay.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //订单信息
    private String getOrderInfo(float amount) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + keys.PARTNER + "\"";
        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + keys.SELLER + "\"";
        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + mPayInformation.OrderPayNumber + "\"";
        // 商品名称
        orderInfo += "&subject=" + "\"" + mSubject + "\"";
        // 商品详情
        orderInfo += "&body=" + "\"" + mDescription + "\"";
        Log.d("resultStatus",amount+"--");
        // 商品金额
        orderInfo += "&total_fee=" + "\"" + amount + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + services.getPayCallBackByTaobao() + "\"";
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
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showToast("支付成功");
                        try {
                            HashMap<String, String> extras = new HashMap<String,String>();
                            extras.put("LEFT","LEFT");
                            gotoActivityAndFinish(OrdersTabActivity.class.getName(), null);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(Pay.this, R.string.mall_pay_succeed, Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            showToast("支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            showToast(R.string.mall_pay_failure);
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
            msg = "支付成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
//         builder.setCustomTitle();
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (str.equalsIgnoreCase("success")) {
                    try {
                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put("LEFT", "LEFT");
                        gotoActivityAndFinish(OrdersTabActivity.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                }

            }
        });
        builder.create().show();
    }

    //获取商家用户账户信息
    public void getUserId() {
        String url = Services.mHost + "BAccount/APP_GetAccountInfo?RID=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId);
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
                                    Gson gson = new Gson();
                                    accountInfo = gson.fromJson(jsonObject.getString("Obj"), AccountInfo.class);
                                    if (accountInfo == null) {
                                        mBalance.setText("共0元");
                                        mBalanceMoney = 0.0f;
                                    } else {
                                        if (accountInfo.Status.equals(true)) {
                                            // 用户余额
                                            mBalanceMoney = accountInfo.Balance;
                                            mBalance.setText("共" + mBalanceMoney + "元 " + accountInfo.GradeTitle);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //    使用余额支付 api/BOrder?orderID={orderID}&RID={RID}&BalancePayMoney={BalancePayMoney}&otherPayType={otherPayType}
    public void balancePaid(String orderID, String BalancePayMoney, String otherPayType, final String type) {
        // 请求的URL
        String url = Services.mHost + "BOrder/Set_BalancePay?orderID=%s&RID=%s&BalancePayMoney=%s&otherPayType=%s";
        User user = UserInformation.getUserInfo();
        url = String.format(url, orderID, user.getUserId(), BalancePayMoney, otherPayType);
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
                                    Gson gson = new Gson();
                                    balancePaid = gson.fromJson(jsonObject.getString("Obj"), BalancePaid.class);
                                    if ("1".equals(type)) {
                                        //余额满足支付订单金额
                                        if (mPayInformation.Amount.equals(mPaySurplusMoneys)) {
                                            if (balancePaid.IsBalancePayOK.equals(false)) {
                                                showToast("支付失败");
                                            } else {
                                                showToast("支付成功");
                                                try {
                                                    HashMap<String, String> extras = new HashMap<String, String>();
                                                    extras.put("LEFT", "LEFT");
                                                    gotoActivityAndFinish(OrdersTabActivity.class.getName(), extras);
                                                } catch (ClassNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    //银联支付请求tn
    public void unionPay(String orderID, String moneys) {
        // 请求的URL
        String url = Services.mHost + "BOrder/Get_UnionTnCode?orderID=%s&moneys=%s";
        url = String.format(url, orderID, moneys);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        Log.d("dsssssssssssssss",md5);
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd123", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (json.getBoolean("Status")) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    if (jsonObject.getString("Obj") != null) {
                                        tn = jsonObject.getString("Obj");
                                        doStartUnionPayPlugin(Pay.this, tn, serverMode);
                                    }
                                }
                            } else {
                                showToast("调取银联页面失败，请重试！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
