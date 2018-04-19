package com.ldnet.activity.me;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.commen.Services;
import com.ldnet.entities.AccountInfo;
import com.ldnet.entities.ConsumptionMessage;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2016/10/11.
 */
public class AccountRecord extends BaseActionBarActivity {

    private ImageButton mBack;
    private TextView mTitle;
    private TextView tv_account_record;
    private ListView lv_consumption_information;
    private ListViewAdapter mAdapter;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    //消费数据
    private List<ConsumptionMessage> mDatas;
    private AccountInfo accountInfo;
    private List<ConsumptionMessage> datas;
    private Services mServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_account_record);
        findView();
    }

    public void findView(){
        mServices = new Services();
        mBack = (ImageButton)findViewById(R.id.btn_back);
        mTitle = (TextView)findViewById(R.id.tv_page_title);
        tv_account_record = (TextView)findViewById(R.id.tv_account_record);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        lv_consumption_information = (ListView)findViewById(R.id.lv_consumption_information);
        lv_consumption_information.setFocusable(false);
        mDatas = new ArrayList<ConsumptionMessage>();
        mTitle.setText("消费记录");
        mBack.setOnClickListener(this);
        mDatas.clear();
        //账户余额操作记录
        GetRecordBalance("");
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "消费记录：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "消费记录：" + this.getClass().getSimpleName());
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                //账户余额操作记录
                GetRecordBalance("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    GetRecordBalance(mDatas.get(mDatas.size() - 1).ID);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    //  根据用户ID 获取账户余额的消费记录（账户余额操作记录）
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
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call,e,i);
                        mPullToRefreshScrollView.onRefreshComplete();
                        closeProgressDialog1();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.d("asdsdasd", "111111111" + s);
                        mPullToRefreshScrollView.onRefreshComplete();
                        closeProgressDialog1();
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
                                        mDatas.addAll(datas);
                                        mAdapter = new ListViewAdapter<ConsumptionMessage>(AccountRecord.this, R.layout.item_consumption, mDatas) {
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
                                        lv_consumption_information.setAdapter(mAdapter);
                                        mServices.setListViewHeightBasedOnChildren(lv_consumption_information);
                                    }  else {
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_back){
            finish();
        }
    }
}
