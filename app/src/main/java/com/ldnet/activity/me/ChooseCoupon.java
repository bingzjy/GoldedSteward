package com.ldnet.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.mall.Order_Confirm;
import com.ldnet.entities.Coupon;
import com.ldnet.entities.Goods;
import com.ldnet.entities.RS;
import com.ldnet.entities.SubOrders;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.tendcloud.tenddata.TCAgent;
import com.third.listviewshangxia.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zxs on 2016/4/23.
 */
public class ChooseCoupon extends BaseActionBarActivity implements XListView.IXListViewListener, CompoundButton.OnCheckedChangeListener {
    private TextView tv_main_title;
    private TextView tv_sure_coupon;
    private ImageButton btn_back;
    private Services services;
    private ListView mLvCoupon;
    private ListViewAdapter mAdapter;
    private List<Coupon> mDatas;
    private Handler mHandler;
    protected SubOrders mOrderInfo;
    private List<SubOrders> mSubOrders;
    private Integer mPosition;
    private CheckBox mUnCouponCkb;
    private Boolean mIsFromGoodsDetails;
    private String mFromClassName;
    private LinearLayout mLlUncoupon;
    private String mUrl;
    //页面标题
    private String mTitle;
    //商品ID
    private Goods mGoods;
    private String mCID;
    private List<Coupon> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_coupon);

        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText("选择优惠劵");
        tv_sure_coupon = (TextView) findViewById(R.id.btn_custom);
        tv_sure_coupon.setVisibility(View.VISIBLE);
        tv_sure_coupon.setText("完成");
        //回退按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        mLvCoupon = (ListView) findViewById(R.id.xlv_choose_coupon);
        mSubOrders = (List<SubOrders>) getIntent().getSerializableExtra("SUB_ORDERS");
        mIsFromGoodsDetails = Boolean.valueOf(getIntent().getStringExtra("IS_FROM_GOODSDETAILS"));
        mFromClassName = getIntent().getStringExtra("FROM_CLASS_NAME");
        mPosition = getIntent().getIntExtra("ORDERS_POSITION", 0);
        mGoods = (Goods) getIntent().getSerializableExtra("GOODS");
        mTitle = getIntent().getStringExtra("PAGE_TITLE");
        mUrl = getIntent().getStringExtra("URL");
        mCID = getIntent().getStringExtra("CID");
        mOrderInfo = mSubOrders.get(mPosition);
        //不使用优惠劵
        mLlUncoupon = (LinearLayout) findViewById(R.id.ll_un_coupon);
        mUnCouponCkb = (CheckBox) findViewById(R.id.chk_un_coupon_checked);

        //初始化服务
        services = new Services();
        mHandler = new Handler();
        mDatas = new ArrayList<Coupon>();
        mAdapter = new ListViewAdapter<Coupon>(this, R.layout.item_choose_coupon, mDatas) {
            @Override
            public void convert(final ViewHolder holder, final Coupon coupon) {
                if (coupon.MainTypeID == 1) {
                    coupon.MainTypeName = "平台劵";
                } else {
                    coupon.MainTypeName = "商家劵";
                }
                holder.setText(R.id.tv_coupon_choose, "满" + coupon.FullMoney + "元可用" + "(" + coupon.MainTypeName + ")");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                String beginDateString = dateFormat.format(coupon.BeginTime);
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy.MM.dd");
                String endDateString = dateFormat1.format(coupon.EndTime);
                //显示开始时间和结束时间
                holder.setText(R.id.tv_coupon_data, beginDateString + "-" + endDateString);
                holder.setText(R.id.tv_coupon_money, String.valueOf("-" + coupon.ReduceMoney));

                //优惠券选择
                CheckBox mCouponCkb = holder.getView(R.id.chk_coupon_checked);
                mCouponCkb.setEnabled(coupon.IsAvailable);
                if (coupon.IsChecked != null && coupon.IsChecked) {
                    mCouponCkb.setChecked(true);
                } else {
                    mCouponCkb.setChecked(false);

                }
                mCouponCkb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUnCouponCkb.setChecked(false);
                        if (coupon.IsAvailable && (coupon.IsChecked == null || !coupon.IsChecked)) {
                            coupon.IsChecked = true;
                            for (Coupon c : mDatas) {
                                if (!c.ID.equals(coupon.ID)) {
                                    c.IsChecked = false;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        //---用户手贱，不选择优惠券
                        else if (coupon.IsAvailable && coupon.IsChecked) {
                            coupon.IsChecked = false;
                            mUnCouponCkb.setChecked(true);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        };
        mLvCoupon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Coupon coupon = mDatas.get(position);
                if (coupon.IsAvailable && (coupon.IsChecked == null || !coupon.IsChecked)) {
                    coupon.IsChecked = true;
                    for (Coupon c : mDatas) {
                        if (!c.ID.equals(coupon.ID)) {
                            c.IsChecked = false;
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    mUnCouponCkb.setChecked(false);
                }
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 0);
        mLvCoupon.setAdapter(mAdapter);

        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this,"选择优惠券："+this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this,"选择优惠券："+this.getClass().getSimpleName());
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
        tv_sure_coupon.setOnClickListener(this);
        mUnCouponCkb.setOnCheckedChangeListener(this);
        mLlUncoupon.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            for (Coupon c : mDatas) {
                c.IsChecked = false;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回
//                finish();
                Intent intent1 = new Intent(this, Order_Confirm.class);
                intent1.putExtra("SUB_ORDERS", (Serializable) mSubOrders);
                intent1.putExtra("IS_FROM_GOODSDETAILS", "false");
                intent1.putExtra("FROM_CLASS_NAME", mFromClassName);
                intent1.putExtra("GOODS", mGoods);
                intent1.putExtra("PAGE_TITLE", mTitle);
                intent1.putExtra("URL", mUrl);
                intent1.putExtra("CID", mCID);
                startActivity(intent1);
                finish();
                break;
            case R.id.btn_custom://完成选择优惠劵
                if (mUnCouponCkb.isChecked()) {
                    mSubOrders.get(mPosition).ISYHJ = false;
                    mSubOrders.get(mPosition).YHJID = "";
                    mSubOrders.get(mPosition).YHJJM = 0.0f;
                } else {
                    Coupon coupon = getSelected();
                    if (coupon != null) {
                        mSubOrders.get(mPosition).ISYHJ = true;
                        mSubOrders.get(mPosition).YHJID = coupon.ID;
                        mSubOrders.get(mPosition).YHJJM = coupon.ReduceMoney;
                    } else {
                        mSubOrders.get(mPosition).ISYHJ = false;
                        mSubOrders.get(mPosition).YHJID = "";
                        mSubOrders.get(mPosition).YHJJM = 0.0f;
                    }
                }
                Intent intent = new Intent(this, Order_Confirm.class);
                intent.putExtra("SUB_ORDERS", (Serializable) mSubOrders);
                intent.putExtra("IS_FROM_GOODSDETAILS", "false");
                intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                intent.putExtra("GOODS", mGoods);
                intent.putExtra("PAGE_TITLE", mTitle);
                intent.putExtra("URL", mUrl);
                intent.putExtra("CID", mCID);
                startActivity(intent);
                finish();
                break;
            case R.id.ll_un_coupon:
                mUnCouponCkb.setChecked(true);
                break;
        }
    }

    //获取用户选择的优惠劵
    private Coupon getSelected() {
        for (Coupon c : mDatas) {
            if (c.getIsChecked()) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 2000);
    }

    protected void loadData() {
        //显示上次刷新时间
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//        String dataString = dateFormat.format(new Date(System.currentTimeMillis()));
        getMyVolumeByRetailerID(mOrderInfo);

        //  停止刷新，显示刷新时间
//        mXLvCoupon.stopRefresh();
//        mXLvCoupon.stopLoadMore();
//        mXLvCoupon.setRefreshTime(dataString);
    }

    //  根据商品信息 获取可用的优惠卷
    public void getMyVolumeByRetailerID(SubOrders orders) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        try {
            // 请求的URL
            String url = Services.mHost + "Volume_Resident/App_GetMyVolume_ByRetailerID";
            JSONObject subOrderInfos = new JSONObject();
            //构造订单详细信息
            subOrderInfos.put("BID", orders.getBID());
            subOrderInfos.put("UID", UserInformation.getUserInfo().getUserId());
            subOrderInfos.put("MS", orders.getMessage());
            //订单中商品的详细信息
            JSONArray goodsArray = new JSONArray();
            for (RS s : orders.getRS()) {
                JSONObject goodsInfos = new JSONObject();
                goodsInfos.put("GID", s.getGID());
                goodsInfos.put("GGID", s.getGGID());
                goodsInfos.put("N", s.getGC());
                goodsInfos.put("SID", s.getSID());
                goodsInfos.put("GIM", s.getGI());
                goodsArray.put(goodsInfos);
            }
            //添加商品详细信息到订单中
            subOrderInfos.put("SD", goodsArray);
            HashMap<String, String> extras = new HashMap<>();
            extras.put("str", subOrderInfos.toString());
            Services.json(extras);
            String md5 = UserInformation.getUserInfo().getUserPhone() +
                    aa + aa1 + Services.json(extras) + Services.TOKEN;
            OkHttpUtils.post().url(url)
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32
                            (md5))
                    .addParams("str", subOrderInfos.toString())
                    .build()
                    .execute(new DataCallBack(this) {
                        @Override
                        public void onError(Call call, Exception e, int i) {

                        }

                        @Override
                        public void onResponse(String str, int i) {
                            Log.d("asdsdasd", "111111111" + str);
                            try {
                                JSONObject json = new JSONObject(str);
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (json.getBoolean("Status")) {
                                    if (jsonObject.getBoolean("Valid")) {
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<Coupon>>(){}.getType();
                                        datas = gson.fromJson(jsonObject.getString("Obj"),type);
                                        mDatas.clear();
                                        if (datas != null) {
                                            // 不能选择和别的订单相同的优惠券
                                            for (SubOrders s : mSubOrders) {
                                                if (!mOrderInfo.getBID().equals(s.getBID())) {
                                                    for (Coupon c : datas) {
                                                        if (s.getISYHJ() && c.getID().equals(s.getYHJID())) {
                                                            c.IsAvailable = false;
                                                        }
                                                    }
                                                }
                                            }
                                            // 默认当前订单使用的优惠券
                                            for (Coupon c : datas) {
                                                if (mOrderInfo.getISYHJ() && c.getID().equals(mOrderInfo.YHJID)) {
                                                    c.IsChecked = true;
                                                } else {
                                                    c.IsChecked = false;
                                                }
                                            }
                                            //  如果不使用优惠劵
                                            if (!mOrderInfo.getISYHJ()) {
                                                mUnCouponCkb.setChecked(true);
                                            }
                                            mDatas.addAll(datas);
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
