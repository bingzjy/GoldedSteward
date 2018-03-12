package com.ldnet.activity.me;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.ConsumptionMessage;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.tendcloud.tenddata.TCAgent;
import com.third.listviewshangxia.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zxs on 2016/2/17.
 * 消费列表
 */
public class Consumption extends BaseActionBarActivity{
    // 标题
    private RelativeLayout mTitlebar;
    private ImageButton mBack;
    private TextView mTitle;
    private TextView tv_account_record;
    //服务
    private Services mServices;
    private Handler mHandler;
    //消费的列表
    private ListView xLvConsumption;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    //消费数据
    private List<ConsumptionMessage> mDatas;
    private ListViewAdapter mAdapter;
    private List<ConsumptionMessage> datas;
    private TextView tvCustom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption);
        mServices = new Services();
        // 标题
        mTitlebar = (RelativeLayout) findViewById(R.id.rl_second_titlebar);
        tvCustom=(TextView)findViewById(R.id.tv_custom);
        tvCustom.setVisibility(View.VISIBLE);
        mBack = (ImageButton) findViewById(R.id.btn_back);
        mTitle = (TextView) findViewById(R.id.tv_page_title);
        tv_account_record = (TextView) findViewById(R.id.tv_account_record);
        mTitle.setText("交易明细");
        xLvConsumption = (ListView) findViewById(R.id.lv_consumption_information);
        xLvConsumption.setFocusable(false);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        mDatas = new ArrayList<ConsumptionMessage>();
        mHandler = new Handler();
        mServices = new Services();
        mDatas.clear();
        //  账户操作记录
        GetRecordList("");
        initEvent();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "交易明细：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "交易明细：" + this.getClass().getSimpleName());
    }

    public void initEvent() {
        mBack.setOnClickListener(this);
        xLvConsumption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到交易详情的具体细节
                if (position <= mDatas.size()) {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("RECORD_ID", mDatas.get(position).ID);
                    extras.put("OperTypes", mDatas.get(position).OperTypes);
                    try {
                        gotoActivity(ConsumptionDetails.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://
                try {
                    gotoActivityAndFinish(Recharge.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                gotoActivityAndFinish(Recharge.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                //  账户操作记录
                GetRecordList("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    GetRecordList(mDatas.get(mDatas.size() - 1).ID);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    //   根据用户ID 获取账户消费记录（账户操作记录）
    // GET BAccount/APP_GetRecordList_ByResidentID?ResidentID={ResidentID}&LastID={LastID}&PageCnt={PageCnt}
    public void GetRecordList(String lastID) {
        // 请求的URL
        String url = Services.mHost + "BAccount/APP_GetRecordList_ByResidentID?ResidentID=%s&LastID=%s&PageCnt=%s";
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
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        mPullToRefreshScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        mPullToRefreshScrollView.onRefreshComplete();
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<ConsumptionMessage>>() {
                                    }.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas != null) {
                                        mDatas.addAll(datas);
                                        mAdapter = new ListViewAdapter<ConsumptionMessage>(Consumption.this, R.layout.item_consumption, mDatas) {
                                            @Override
                                            public void convert(ViewHolder holder, ConsumptionMessage consumptionMessage) {
                                                //列表显示的内容//
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
                                        mServices.setListViewHeightBasedOnChildren(xLvConsumption);
                                    } else {
                                        if (mDatas != null && mDatas.size() > 0) {
                                            showToast("没有更多数据");
                                        } else {
                                            tv_account_record.setVisibility(View.VISIBLE);
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
}
