package com.ldnet.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.AccountInfo;
import com.ldnet.entities.ConsumptionMessage;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.third.listviewshangxia.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zxs on 2016/2/15.
 * 我的钱包
 */
public class Recharge extends BaseActionBarActivity implements XListView.IXListViewListener {
    // 标题
    private RelativeLayout mTitlebar;
    private ImageButton mBack;
    private TextView mTitle;
    private Button mConsumptionDetails;
    // 余额
    private TextView mBalance;
    private Button mBalanceDetails;
    // 充值
    private Button mRecharge;
    private Services mServices;

    private Handler mHandler;
    //消费的列表
    private XListView xLvConsumption;
    //消费数据
    private List<ConsumptionMessage> mDatas;
    private ListViewAdapter mAdapter;
    private RelativeLayout mRl;
    private AccountInfo accountInfo;
    private List<ConsumptionMessage> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_recharge);
        mServices = new Services();
        // 标题
        mTitlebar = (RelativeLayout) findViewById(R.id.rl_second_titlebar);
        mBack = (ImageButton) findViewById(R.id.btn_back);
        mTitle = (TextView) findViewById(R.id.tv_page_title);
        mTitle.setText("我的钱包");
        mConsumptionDetails = (Button) findViewById(R.id.btn_custom);
        mConsumptionDetails.setVisibility(View.VISIBLE);
        mConsumptionDetails.setText("交易明细");
        mConsumptionDetails.setTextColor(getResources().getColor(R.color.white));
        // 余额
        mBalance = (TextView) findViewById(R.id.tv_me_balance);
        getUserId();

        //查询余额详情
        mBalanceDetails = (Button) findViewById(R.id.btn_balance_details);
        //充值
        mRecharge = (Button) findViewById(R.id.btn_recharge);
        xLvConsumption = (XListView) findViewById(R.id.lv_consumption_information);
        mDatas = new ArrayList<ConsumptionMessage>();
        mAdapter = new ListViewAdapter<ConsumptionMessage>(this, R.layout.item_consumption, mDatas) {
            @Override
            public void convert(ViewHolder holder, ConsumptionMessage consumptionMessage) {
                //列表显示的内容
                holder.setText(R.id.tv_opersource_paytype, consumptionMessage.OperSourceTitle);//
                holder.setText(R.id.tv_oper_day, Services.subStr(consumptionMessage.OperDay));//
                holder.setText(R.id.tv_oper_moneys, "￥" + consumptionMessage.OperMoneys);
                TextView operMoneys = holder.getView(R.id.tv_oper_moneys);
                if (consumptionMessage.OperSourceTitle.equals("充值") || consumptionMessage.OperSourceTitle.equals("退款")) {
                    operMoneys.setText("+" + consumptionMessage.OperMoneys);
                    operMoneys.setTextColor(getResources().getColor(R.color.green));
                } else {
                    operMoneys.setText("-" + consumptionMessage.OperMoneys);
                    operMoneys.setTextColor(getResources().getColor(R.color.red));
                }
            }
        };
        xLvConsumption.setAdapter(mAdapter);
        xLvConsumption.setXListViewListener(this);
        mHandler = new Handler();
        initEvent();
    }

    public void initEvent() {
        mBack.setOnClickListener(this);
        mConsumptionDetails.setOnClickListener(this);
        mRecharge.setOnClickListener(this);
        mBalanceDetails.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回
                finish();
                break;
            case R.id.btn_custom://账户明细
                try {
                    gotoActivityAndFinish(Consumption.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_recharge://充值
                try {
                    gotoActivityAndFinish(Recharge_Details.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_balance_details://余额详情
                Intent intent = new Intent(this,AccountRecord.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(true);
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(true);
            }
        }, 2000);
    }

    private void loadData(Boolean isFirst) {

        //显示上次刷新时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String dataString = dateFormat.format(new Date(System.currentTimeMillis()));
        //第一次传空，否则传列表最后id
        if (!isFirst) {
//            //账户余额操作记录
            GetRecordBalance(mDatas.get(mDatas.size() - 1).ID);
        } else {
            mDatas.clear();
            //账户余额操作记录
            GetRecordBalance("");
        }
        xLvConsumption.stopLoadMore();
        xLvConsumption.stopRefresh();
        xLvConsumption.setRefreshTime(dataString);
    }

    //获取商家用户账户信息
    public void getUserId() {
        String url = Services.mHost + "BAccount/APP_GetAccountInfo?RID=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId());
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
                                    accountInfo = gson.fromJson(jsonObject.getString("Obj"), AccountInfo.class);
                                    if (accountInfo == null) {
                                        //            mBalance.setText("共0元");
                                    } else {
                                        if (accountInfo.Status.equals(true)) {
                                            // 用户余额
                                            mBalance.setText("￥" + accountInfo.Balance);
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

    //  根据用户ID 获取账户余额的消费记录（账户余额操作记录）
    // GET BAccount/APP_GetRecordBalance_List_ByResidentID?ResidentID={ResidentID}&LastID={LastID}&PageCnt={PageCnt}
    public void GetRecordBalance(String lastID) {
        // 请求的URL
        String url = Services.mHost + "BAccount/APP_GetRecordBalance_List_ByResidentID?ResidentID=%s&LastID=%s&PageCnt=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, lastID, Services.PAGE_SIZE);
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
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        showProgressDialog();
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call,e,i);
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.d("asdsdasd", "111111111" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<ConsumptionMessage>>() {
                                    }.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    //
                                    if (datas != null) {
                                        //判断是否出现“查看更多”
                                        if (datas.size() < mServices.getPageSize()) {
                                            xLvConsumption.setPullLoadEnable(false);
                                        } else {
                                            xLvConsumption.setPullLoadEnable(true);
                                        }
                                        mDatas.addAll(datas);

                                    } else {
                                        showToast("您还没有关于余额的操作记录");
                                    }
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
