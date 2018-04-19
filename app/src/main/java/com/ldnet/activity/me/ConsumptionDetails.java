package com.ldnet.activity.me;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.ConsumptionMessage;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by zxs on 2016/2/17.
 * 消费细节
 */
public class ConsumptionDetails extends BaseActionBarActivity {
    // 标题
    private RelativeLayout mTitlebar;
    private ImageButton mBack;
    private TextView mTitle,textView;
    // 显示收支细节
    protected TextView mTvOperMoneys, mTvOrderNumber, mTvOperTypesTitle, mTvOperSourceTitle, mTvPayTypeTitle, mTvOperDay;
    protected Services mServices;
    private Boolean mIsOperBalance = true;
    private ConsumptionMessage recordInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption_details);
        String recordId = getIntent().getStringExtra("RECORD_ID");
        String operTypes = getIntent().getStringExtra("OperTypes");
//        if (Valid.isNotNullOrEmpty(operTypes)) {
//            operTypes = "2";
//        } else {
//            operTypes = "1";
//        }
        mServices = new Services();
        //根据记录ID获取详细信息
        GetRecordInfo(recordId, operTypes);
        // 标题
        mTitlebar = (RelativeLayout) findViewById(R.id.rl_second_titlebar);
//        mTitlebar.setBackgroundColor(getResources().getColor(R.color.white));
        mBack = (ImageButton) findViewById(R.id.btn_back);
        mTitle = (TextView) findViewById(R.id.tv_page_title);
        mTitle.setText("收支详情");
        mTvOperMoneys = (TextView) findViewById(R.id.tv_oper_moneys);

        //订单号
        mTvOrderNumber = (TextView) findViewById(R.id.tv_order_number);

        //支入支出
        mTvOperTypesTitle = (TextView) findViewById(R.id.tv_oper_types_title);

        //订单类型
        mTvOperSourceTitle = (TextView) findViewById(R.id.tv_oper_source_title);

        //支付方式
        mTvPayTypeTitle = (TextView) findViewById(R.id.tv_pay_type_title);
        //收支细节
        textView = (TextView) findViewById(R.id.tv_pay_details);

        initEvent();
    }

    public void initEvent() {
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "收支详情：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "收支详情：" + this.getClass().getSimpleName());
    }

    // 根据记录ID获取详细信息  记录ID  类型 1：账户操作详细 2：余额操作详细
    // GET BAccount/APP_Get_RecordInfo?RecordID={RecordID}&Types={Types}
    public void GetRecordInfo(String recordID, String types) {
        // 请求的URL
        String url = Services.mHost + "BAccount/APP_Get_RecordInfo?RecordID=%s&Types=%s";
        url = String.format(url, recordID, types);
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
                                    recordInfo = gson.fromJson(jsonObject.getString("Obj"),ConsumptionMessage.class);
                                    mTvOperMoneys.setText(recordInfo.OperMoneys + "");
                                    mTvOrderNumber.setText(recordInfo.OrderNumber);
                                    mTvOperTypesTitle.setText(recordInfo.OperTypesTitle);
                                    mTvOperSourceTitle.setText(recordInfo.OperSourceTitle);
                                    //使用余额
                                    if (recordInfo.IsOperBalance.equals(mIsOperBalance)) {
                                        //只是用余额
                                        if (recordInfo.BalanceMoneys.equals(recordInfo.OperMoneys)) {
                                            mTvPayTypeTitle.setText(recordInfo.PayTypeTitle);
                                            textView.setText("余额：" + recordInfo.BalanceMoneys);
                                        } else {
                                            //使用余额和其他
                                            mTvPayTypeTitle.setText("余额 + " + recordInfo.PayTypeTitle);
                                            BigDecimal a1 = new BigDecimal(Float.toString(recordInfo.OperMoneys));
                                            BigDecimal a2 = new BigDecimal(Float.toString(recordInfo.BalanceMoneys));
                                            Float mOnlinePay = a1.subtract(a2).floatValue();
                                            textView.setText("余额：" + recordInfo.BalanceMoneys + " " + recordInfo.PayTypeTitle + "：" + mOnlinePay);
                                        }
                                    } else {
                                        //使用其他
                                        mTvPayTypeTitle.setText(recordInfo.PayTypeTitle);
                                        textView.setText(recordInfo.PayTypeTitle + "：" + recordInfo.OperMoneys);
                                    }
                                    //操作时间
                                    mTvOperDay = (TextView) findViewById(R.id.tv_oper_day);
//                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                    String dataString = dateFormat.format(recordInfo.OperDay);
                                    mTvOperDay.setText(Services.subStr(recordInfo.OperDay));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
