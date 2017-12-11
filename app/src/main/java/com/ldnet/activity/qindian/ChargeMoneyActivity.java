package com.ldnet.activity.qindian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.alipay.sdk.app.PayTask;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.ALiPayInfo;
import com.ldnet.entities.ChargAmount;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.QinDianService;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Services;
import com.ldnet.utility.ViewHolder;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.*;
import com.third.Alipay.PayResult;
import com.third.Alipay.SignUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.tencent.mm.algorithm.MD5.getMessageDigest;

/**
 * Created by zjy on 2017/9/5.
 */
public class ChargeMoneyActivity extends BaseActionBarActivity implements IWXAPIEventHandler, CompoundButton.OnCheckedChangeListener {
    private TextView title, tvAccountBalance;
    private ImageButton back;
    private Button btnChargeMoney;
    private GridView gridView;
    private LinearLayout llWeiXin, llAlipay;
    private CheckBox checkBoxWeiXin, checkBoxAlipay;
    private QinDianService service;
    private List<ChargAmount> chargAmountList = new ArrayList<>();
    private ListViewAdapter<ChargAmount> adapter;
    private String chargeAmountId, chargeAmountValue;
    private final String ALI_PAY = "1";
    private final String WEIXIN_PAY = "2";

    // 支付宝支付结果标识
    private static final int SDK_PAY_FLAG = 1;
    // 支付宝账号检查标识
    private static final int SDK_CHECK_FLAG = 2;
    private String payType = "1";
    private String orderID, nonce_str, prepayId, sign;
    private String TAG = ChargeMoneyActivity.class.getSimpleName();

    //微信支付参数
    private final String APP_ID = "wxf61e999af9c60317";
    private final String PACKAGE_VALUE = "Sign=WXPay";
    private final String PARTNER_ID = "1384000202";
    private final String KEY = "ymKqEY5goYnLk9JGT0BdBELArJH95SRq";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qindian_charge_money);

        service = new QinDianService(this);
        showProgressDialog();
        service.getRemind(getRemindHandler);
        service.getAmountInfo(getAmountValueHandler);
        initView();
    }


    private void initView() {
        title = (TextView) findViewById(R.id.tv_page_title);
        back = (ImageButton) findViewById(R.id.btn_back);
        tvAccountBalance = (TextView) findViewById(R.id.tv_account_balance);
        btnChargeMoney = (Button) findViewById(R.id.button_charge);
        gridView = (GridView) findViewById(R.id.charge_gridView);
        llAlipay = (LinearLayout) findViewById(R.id.ll_alipay);
        llWeiXin = (LinearLayout) findViewById(R.id.ll_weixin);
        checkBoxAlipay = (CheckBox) findViewById(R.id.checkbox_alipay);
        checkBoxWeiXin = (CheckBox) findViewById(R.id.checkbox_weixin);
        checkBoxWeiXin.setChecked(true);

        title.setText("充值");
        btnChargeMoney.setOnClickListener(this);
        back.setOnClickListener(this);

        checkBoxAlipay.setOnCheckedChangeListener(this);
        checkBoxWeiXin.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.button_charge:
                chargeAmount();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkbox_alipay:
                if (checkBoxAlipay.isChecked()) {
                    checkBoxWeiXin.setChecked(false);
                    checkBoxAlipay.setChecked(true);
                }
                break;
            case R.id.checkbox_weixin:
                if (checkBoxWeiXin.isChecked()) {
                    checkBoxAlipay.setChecked(false);
                    checkBoxWeiXin.setChecked(true);
                }
                break;
        }
    }


    private void setAdapter() {
        adapter = new ListViewAdapter<ChargAmount>(this, R.layout.item_qindian_money_value, chargAmountList) {
            @Override
            public void convert(ViewHolder holder, ChargAmount chargAmounts) {
                holder.setText(R.id.tv_equipment_number, chargAmounts.getPayment());
                holder.getView(R.id.tv_equipment_msg2).setVisibility(View.GONE);
                holder.getView(R.id.tv_equipment_msg1).setVisibility(View.GONE);
                View view = holder.getView(R.id.ll_back);

                TextView textView = holder.getView(R.id.tv_equipment_number);
                if (chargAmounts.isChecked()) {
                    textView.setTextColor(Color.WHITE);
                    view.setBackgroundResource(R.drawable.back_button_fill_green);
                } else {
                    textView.setTextColor(Color.BLACK);
                    view.setBackgroundResource(R.drawable.back_border_line_green2);
                }
            }
        };
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateData(chargAmountList);//全部设为未选
                chargAmountList.get(position).setChecked(true);
                adapter.notifyDataSetChanged();

                chargeAmountId = chargAmountList.get(position).getId();
                chargeAmountValue = chargAmountList.get(position).getPayment();
            }
        });

    }

    //提交支付
    private void chargeAmount() {
        //充值类型 1：支付宝 2：微信 3：银联卡
        if (!TextUtils.isEmpty(chargeAmountId)) {
            if (checkBoxAlipay.isChecked() && !checkBoxWeiXin.isChecked()) {        //支付宝
                payType = ALI_PAY;
            } else if (!checkBoxAlipay.isChecked() && checkBoxWeiXin.isChecked()) {  //微信
                payType = WEIXIN_PAY;
            }
            service.chargeAmount(chargeAmountId, "1", chargAmountHandler);
        } else {
            showToast("请选择充值金额");
        }
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

    }

    //微信支付
    private void weixinPay(final String nonstr, final String prepayId, final String sign) {
        //注册APPID
        com.tencent.mm.opensdk.openapi.IWXAPI iwxapi = com.tencent.mm.opensdk.openapi.WXAPIFactory.createWXAPI(this, null);
        iwxapi.registerApp("wxf61e999af9c60317");
        //10位时间戳
        Long l = Long.parseLong(Services.timeFormat());
        String timeStamp = String.valueOf(l / 1000);
        //生成二次签名
        List<NameValuePair> params = getSecondSignParams(nonstr, prepayId, timeStamp);
        String secondSign = genSecondPackageSign(params);
        Log.e(TAG, "NONSTR:" + nonstr);
        Log.e(TAG, "PREPATID:" + prepayId);
        Log.e(TAG, "TIMESTAMP:" + timeStamp);
        Log.e(TAG,"SECOND_SIGN:"+secondSign);

        //封装支付参数
        PayReq request = new PayReq();
        request.packageValue =PACKAGE_VALUE;
        request.appId =APP_ID;
        request.partnerId =PARTNER_ID;
        request.timeStamp = timeStamp;
        request.nonceStr = nonstr;
        request.prepayId = prepayId;
        request.sign = secondSign;

        iwxapi.sendReq(request);
    }


    //微信获取参数列表
    private List<NameValuePair> getSecondSignParams(final String nonstr, final String prepayId, final String timeStamp) {
        //appId，partnerId，prepayId，nonceStr，timeStamp，package
        List<NameValuePair> packageParams = new LinkedList<>();
        packageParams.add(new BasicNameValuePair("appid", APP_ID));
        packageParams.add(new BasicNameValuePair("package", PACKAGE_VALUE));
        packageParams.add(new BasicNameValuePair("partnerid", PARTNER_ID));
        packageParams.add(new BasicNameValuePair("noncestr", nonstr));
        packageParams.add(new BasicNameValuePair("prepayid", prepayId));
        packageParams.add(new BasicNameValuePair("timestamp", timeStamp));
        return packageParams;
    }


    //微信第二次签名
    private String genSecondPackageSign(List<NameValuePair> params) {
        //拼接排序list
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(KEY);//上面已经获取
        String packageSign = getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return packageSign;
    }


    //支付宝按钮点击事件
    private void onPayClick(final float amount, final String orderID) {
        //检查支付配置信息
        if (TextUtils.isEmpty(ALiPayInfo.PARTNER) || TextUtils.isEmpty(ALiPayInfo.Private_Key) || TextUtils.isEmpty(ALiPayInfo.Seller_ID)) {
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
        String orderInfo = getOrderInfo(amount, orderID);
        // 对订单做RSA 签名
        String sign = SignUtils.sign(orderInfo, ALiPayInfo.Private_Key);
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
                PayTask alipay = new PayTask(ChargeMoneyActivity.this);
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
    private String getOrderInfo(float amount, String orderID) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + ALiPayInfo.PARTNER + "\"";
        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + ALiPayInfo.Seller_ID + "\"";
        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderID + "\"";
        // 商品名称
        orderInfo += "&subject=" + "\"" + "充智安" + "\"";
        // 商品详情
        orderInfo += "&body=" + "\"" + "亲点科技社区快捷充电" + "\"";
        Log.d("resultStatus", amount + "--");
        // 商品金额
        orderInfo += "&total_fee=" + "\"" + amount + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + ALiPayInfo.Notify_URL + "\"";
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
                    Log.e("QinDianService","支付结果"+resultStatus);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showToast("支付成功");
                        service.getRemind(getRemindHandler);
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

    //充值获取订单信息
    Handler chargAmountHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    org.json.JSONObject jsonObject = (org.json.JSONObject) msg.obj;
                    try {
                        orderID = jsonObject.getString("orderNo");
                        //微信支付
                        if (!TextUtils.isEmpty(payType) && payType.equals(WEIXIN_PAY)) {
                            nonce_str = jsonObject.getString("nonce_str");
                            prepayId = jsonObject.getString("prepayId");
                            sign = jsonObject.getString("sign");

                            if (!TextUtils.isEmpty(orderID) && !TextUtils.isEmpty(nonce_str) && !TextUtils.isEmpty(prepayId) && !TextUtils.isEmpty(sign)) {
                                //调取微信支付
                                weixinPay(nonce_str, prepayId, sign);
                            } else {
                                showToast("支付提交失败，请稍后再试");
                            }
                        } else {
                            //调取支付宝支付
                            if (!TextUtils.isEmpty(orderID)) {
                                onPayClick(Float.parseFloat(chargeAmountValue), orderID);
                            } else {
                                showToast("支付提交失败，请稍后再试");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //获取充值可选金额
    Handler getAmountValueHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    chargAmountList = (List<ChargAmount>) msg.obj;
                    setAdapter();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    Handler getRemindHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    tvAccountBalance.setText(msg.obj.toString()+"元");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    private void updateData(List<ChargAmount> list) {
        for (ChargAmount chargAmount : list) {
            if (chargAmount.isChecked()) {
                chargAmount.setChecked(false);
            }
        }
    }

}