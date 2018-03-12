package com.ldnet.activity.me;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Coupon;
import com.ldnet.entities.HouseRent;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.tendcloud.tenddata.TCAgent;
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
 * Created by zxs on 2016/4/15.
 * 获取可领取的优惠劵
 */
public class GainCoupon extends BaseActionBarActivity implements XListView.IXListViewListener {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private XListView mXLvCoupon;
    private ListViewAdapter mAdapter;
    private List<Coupon> mDatas;
    private Handler mHandler;

    //我的优惠劵
    private Button mMeCoupon;
    private List<Coupon> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaincoupon);
        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText("领取优惠劵");
        //回退按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        // 我的优惠劵按钮
        mXLvCoupon = (XListView) findViewById(R.id.lv_gain_coupon);
        //初始化服务
        services = new Services();
        mHandler = new Handler();
        mDatas = new ArrayList<Coupon>();
        mAdapter = new ListViewAdapter<Coupon>(this, R.layout.item_gain_coupon, mDatas) {
            @Override
            public void convert(ViewHolder holder, final Coupon coupon) {
                holder.setText(R.id.tv_coupon_reduceMoney, String.valueOf(coupon.ReduceMoney));
                holder.setText(R.id.tv_coupon_typeName, coupon.TypeName);
                holder.setText(R.id.tv_coupon_fullMoney, "满" + coupon.FullMoney + "元减免");
                holder.setText(R.id.tv_coupon_number, "剩余" + coupon.Number + "张");
                holder.setText(R.id.tv_coupon_title, coupon.Title);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                String beginDateString = dateFormat.format(coupon.BeginTime);
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy.MM.dd");
                String endDateString = dateFormat1.format(coupon.EndTime);
                //优惠劵开始时间和结束时间
                holder.setText(R.id.tv_coupon_beginTime_and_endTime, beginDateString + "-" + endDateString);
                //点击获取优惠劵
                Button mBtGetCoupon = holder.getView(R.id.bt_get_coupon);
                if (coupon.IsReceive.equals(true)) {
                    mBtGetCoupon.setText("已领取");
                    mBtGetCoupon.setEnabled(false);
                } else {
                    mBtGetCoupon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getReceiveVolume(coupon.ID, v);
                        }
                    });
                }

            }
        };

        mXLvCoupon.setXListViewListener(this);
        mXLvCoupon.setAdapter(mAdapter);
        //第一次加载数据
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(true);
            }
        }, 1);
        //绑定子条目事件
        mXLvCoupon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        initEvent();
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "领取优惠券：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "领取优惠券：" + this.getClass().getSimpleName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回主页
                try {
                    gotoActivityAndFinish(com.ldnet.activity.me.Coupon.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
//            case R.id.btn_custom://我的优惠劵
//                try {
//                    gotoActivityAndFinish(com.ldnet.activity.me.Coupon.class.getName(), null);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                break;
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
            getVolumeList(mDatas.get(mDatas.size() - 1).ID);
        } else {
            mDatas.clear();
            getVolumeList("");
        }

        //停止刷新 显示时间
        mXLvCoupon.stopLoadMore();
        mXLvCoupon.stopRefresh();
        mXLvCoupon.setRefreshTime(dataString);
    }

    //GET Volume_Resident/App_GetReceiveVolume?VID={VID}&ResidentID={ResidentID}
    //领取优惠卷   优惠卷ID(vid)
    public void getReceiveVolume(String vid, final View v) {
        // 请求的URL
        String url = Services.mHost + "Volume_Resident/App_GetReceiveVolume?VID=%s&ResidentID=%s";
        url = String.format(url, vid, UserInformation.getUserInfo().UserId);
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
                                    String couponObj = jsonObject.getString("obj");
                                    if (couponObj.equals(String.valueOf(true))) {
                                        Button m = (Button) v;
                                        m.setText("已领取");
                                        m.setEnabled(false);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // GET Volume_Resident/App_GetVolumeList?ResidentID={ResidentID}&LastID={LastID}&PageCnt={PageCnt}
    // 获取可领取的优惠卷
    public void getVolumeList(String LastID) {
        // 请求的URL
        String url = Services.mHost + "Volume_Resident/App_GetVolumeList?ResidentID=%s&LastID=%s&PageCnt=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, LastID, Services.PAGE_SIZE);
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
                                    Type type = new TypeToken<List<Coupon>>() {
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
}
