package com.ldnet.activity.me;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.HouseRent;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.third.listviewshangxia.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zxs on 2016/4/5.
 * 我的优惠劵
 */
public class Coupon extends BaseActionBarActivity implements XListView.IXListViewListener {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private XListView mXLvCoupon;
    private ListViewAdapter mAdapter;
    private List<com.ldnet.entities.Coupon> mDatas;
    private Handler mHandler;
    private Button mBtGoCoupon;

    private List<com.ldnet.entities.Coupon> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_coupon);
        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_coupons);
        //回退按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        mXLvCoupon = (XListView) findViewById(R.id.lv_me_coupon);
        mBtGoCoupon = (Button) findViewById(R.id.bt_go_gain_coupon);
        //初始化服务
        services = new Services();
        mHandler = new Handler();
        mDatas = new ArrayList<com.ldnet.entities.Coupon>();
        mAdapter = new ListViewAdapter<com.ldnet.entities.Coupon>(this, R.layout.item_me_coupon, mDatas) {
            @Override
            public void convert(ViewHolder holder, com.ldnet.entities.Coupon coupon) {
                JZADScoreTextView jzadScoreTextView = holder.getView(R.id.jzad_score_textText);
                jzadScoreTextView.setDegrees(45);
                holder.setText(R.id.tv_me_title, coupon.Title);
                holder.setText(R.id.tv_me_typeName, coupon.TypeName);
                holder.setText(R.id.tv_me_reduceMoney, String.valueOf(coupon.ReduceMoney));
                holder.setText(R.id.tv_me_fullMoney, "满" + coupon.FullMoney + "元可用");
                holder.setText(R.id.tv_me_mainTypeName, coupon.MainTypeName);

                String[] str1 = coupon.BeginTime.split("T");
                String[] str2 = coupon.EndTime.split("T");
                //显示开始时间和结束时间
                holder.setText(R.id.tv_me_beginTime_and_endTime, str1[0] + "-" + str2[0]);
            }
        };
        loadData(true);
        mXLvCoupon.setXListViewListener(this);
        mXLvCoupon.setAdapter(mAdapter);
        initEvent();
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
        mBtGoCoupon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回主页
                finish();
                break;
            case R.id.bt_go_gain_coupon://去领劵
                try {
                    gotoActivityAndFinish(GainCoupon.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
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
                loadData(false);
            }
        }, 2000);
    }

    protected void loadData(Boolean isFirst) {

        //显示上次刷新时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String dataString = dateFormat.format(new Date(System.currentTimeMillis()));
        if (!isFirst) {
            myVolume(mDatas.get(mDatas.size() - 1).VolumeID);
        } else {
            mDatas.clear();
            myVolume("");
        }

        //  停止刷新，显示刷新时间
        mXLvCoupon.stopRefresh();
        mXLvCoupon.stopLoadMore();
        mXLvCoupon.setRefreshTime(dataString);
    }

    //GET Volume_Resident/App_MyVolume?ResidentID={ResidentID}&LastID={LastID}&PageCnt={PageCnt}
    //我的优惠卷列表
    public void myVolume(String lastID) {
        // 请求的URL
        String url = Services.mHost + "Volume_Resident/App_MyVolume?ResidentID=%s&LastID=%s&PageCnt=%s";
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
                                    Type type = new TypeToken<List<com.ldnet.entities.Coupon>>() {
                                    }.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas != null) {
                                        //判断是否出现“查看更多”
                                        if (datas.size() < services.getPageSize()) {
                                            mXLvCoupon.setPullLoadEnable(false);
                                        } else {
                                            mXLvCoupon.setPullLoadEnable(true);
                                        }
                                        mDatas.addAll(datas);
                                    } else {
                                        mXLvCoupon.setPullLoadEnable(false);
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
