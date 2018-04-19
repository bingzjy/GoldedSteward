package com.ldnet.activity.me;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
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
import com.ldnet.entities.IntegralSum;
import com.ldnet.entities.Integrals;
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

public class Integral extends BaseActionBarActivity {

    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private TextView tv_me_integral_sum;
    private ListView lv_me_integral;
    private List<Integrals> mIntegrals;
    private ListViewAdapter mAdapter;
    private Handler mHandler;
    private IntegralSum sum;
    private List<Integrals> datas;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    //初始化视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_integral);
        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_integral);

        btn_back = (ImageButton) findViewById(R.id.btn_back);

        //总积分
        tv_me_integral_sum = (TextView) findViewById(R.id.tv_me_integral_sum);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        //积分历史
        lv_me_integral = (ListView) findViewById(R.id.lv_me_integral);
        lv_me_integral.setFocusable(false);
        mIntegrals = new ArrayList<Integrals>();
        //初始化服务
        services = new Services();
        mHandler = new Handler();

        //总积分
        IntegralSum();
        //加载第一屏积分
        mIntegrals.clear();
        Integral("");
        initEvent();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "我的积分：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "我的积分：" + this.getClass().getSimpleName());
    }


    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mIntegrals.clear();
                Integral("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mIntegrals != null && mIntegrals.size() > 0) {
                    Integral(mIntegrals.get(mIntegrals.size() - 1).Id);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    //获取总积分
    public void IntegralSum() {
        // 请求的URL
        String url = Services.mHost + "API/Prints/GetSum/%s";
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
                                    sum = gson.fromJson(jsonObject.getString("Obj"), IntegralSum.class);
                                    tv_me_integral_sum.setText(String.valueOf(sum.Sum));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取积分列表
    public void Integral(String lastId) {
        // 请求的URL
        String url = Services.mHost + "API/Prints/GetList/%s?lastId=%s&pageSize=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, lastId, Services.PAGE_SIZE);
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
                        showProgressDialog1();
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call,e,i);
                        closeProgressDialog1();
                        mPullToRefreshScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        closeProgressDialog1();
                        mPullToRefreshScrollView.onRefreshComplete();
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Integrals>>() {
                                    }.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas != null) {
                                        mIntegrals.addAll(datas);
                                        mAdapter = new ListViewAdapter<Integrals>(Integral.this, R.layout.item_integrals, mIntegrals) {
                                            @Override
                                            public void convert(ViewHolder holder, Integrals integrals) {
                                                //支出积分颜色改变
                                                if (integrals.Score < 0) {
                                                    TextView tv_integral_score = holder.getView(R.id.tv_integral_score);
                                                    tv_integral_score.setTextColor(getResources().getColor(R.color.green));
                                                }

                                                holder.setText(R.id.tv_integral_score, String.valueOf(integrals.Score))
                                                        .setText(R.id.tv_integral_date, Services.subStr(integrals.Created))
                                                        .setText(R.id.tv_integral_title, integrals.Title)
                                                        .setText(R.id.tv_integral_memo, integrals.Memo);
                                            }
                                        };
                                        lv_me_integral.setAdapter(mAdapter);
                                        Utility.setListViewHeightBasedOnChildren(lv_me_integral);
                                    }else {
                                        if (mIntegrals != null && mIntegrals.size() > 0) {
                                            showToast("沒有更多数据");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
