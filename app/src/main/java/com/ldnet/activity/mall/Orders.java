package com.ldnet.activity.mall;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.OD;
import com.ldnet.entities.OrderPay;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;
import com.third.Alipay.PayKeys;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

public class Orders extends BaseActionBarActivity {

    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;

    //订单状态
    private RadioGroup rdg_orders_tabs;
    private LinearLayout ll_goods_balance;
    private TextView tv_goods_prices;
    private Button btn_goods_balance;

    //订单列表
    private List<com.ldnet.entities.Orders> mDatas;
    private ListViewAdapter<com.ldnet.entities.Orders> mAdapter;
    private ListView lv_mall_orders;
    private Integer mCurrentTypeId = 1;
    private Integer mCurrentPageIndex = 1;

    //订单子状态ID 1:待付款，3:已发货，4:已签收，5:待发货，6:已关闭 ，7：已取消 有取消原因CM
    private static final Integer ORDERS_STATUS_NOTPAY = 1;
    private static final Integer ORDERS_STATUS_SENDED = 3;
    private static final Integer ORDERS_STATUS_SUCCESS = 4;
    private static final Integer ORDERS_STATUS_NOTSENDED = 5;
    private static final Integer ORDERS_STATUS_CLOSE = 6;
    private static final Integer ORDERS_STATUS_CANCEL = 7;
    // 支付宝支付结果标识
    private static final int SDK_PAY_FLAG = 1;
    // 支付宝账号检查标识
    private static final int SDK_CHECK_FLAG = 2;
    // 支付相关信息配置
    private PayKeys keys;
    // 支付信息，包含订单号和金额
    private OrderPay mPayInformation;
    // 商品标题
    private String mSubject;
    // 商品描述
    private String mDescription;
    private String mFromClassName;
    private TextView mOrderEmpty;
    private List<com.ldnet.entities.Orders> datas;
    private PullToRefreshScrollView mPullToRefreshScrollView;

    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_orders);
        //初始化支付宝支付信息
        keys = new PayKeys();

        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_orders);

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        ll_goods_balance = (LinearLayout) findViewById(R.id.ll_goods_balance);
        tv_goods_prices = (TextView) findViewById(R.id.tv_goods_prices);
        btn_goods_balance = (Button) findViewById(R.id.btn_goods_balance);
        //订单为空
        mOrderEmpty = (TextView) findViewById(R.id.order_empty);
        //订单分类
        rdg_orders_tabs = (RadioGroup) findViewById(R.id.rdg_orders_tabs);
        for (int i = 0; i < rdg_orders_tabs.getChildCount(); i++) {
            rdg_orders_tabs.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCurrentTypeId = Integer.valueOf(view.getTag().toString());
                    mDatas.clear();
                    mCurrentPageIndex = 1;//重置为第一页
                    Orders(mCurrentTypeId, mCurrentPageIndex);
                    mCurrentPageIndex++;//加载下一页
                }
            });
        }

        //订单列表
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        mDatas = new ArrayList<com.ldnet.entities.Orders>();
        lv_mall_orders = (ListView) findViewById(R.id.lv_mall_orders);
        lv_mall_orders.setFocusable(false);
        //初始化服务
        services = new Services();
        rdg_orders_tabs.getChildAt(0).performClick();
        initEvent();
        initEvents();
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_goods_balance.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "我的订单列表：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "我的订单列表：" + this.getClass().getSimpleName());
    }


    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_goods_balance:
                String orderIds = "";
                for (com.ldnet.entities.Orders orders : mDatas) {
                    if (orders.IsChecked) {
                        if (!TextUtils.isEmpty(orderIds)) {
                            orderIds += "," + orders.OID;
                        } else {
                            orderIds += orders.OID;
                        }
                    }
                }
                OrderPayInformation(orderIds);
                break;
            default:
                break;
        }
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                mCurrentPageIndex = 1;//重置为第一页
                Orders(mCurrentTypeId, mCurrentPageIndex);
                mCurrentPageIndex++;//加载下一页
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    Orders(mCurrentTypeId, mCurrentPageIndex);
                    mCurrentPageIndex++;//加载下一页
                }else{
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    private void updateBlanceStatus() {
        mAdapter.notifyDataSetChanged();
        if (mCurrentTypeId.equals(ORDERS_STATUS_NOTPAY)) {
            BigDecimal totalPrices = new BigDecimal("0.00");
            for (com.ldnet.entities.Orders orders : mDatas) {
                if (orders.IsChecked) {
                    totalPrices = totalPrices.add(new BigDecimal(orders.AM.toString()));
                }
            }

            if (totalPrices.floatValue() == 0.00f) {
                ll_goods_balance.setVisibility(View.GONE);
                tv_goods_prices.setText("￥" + totalPrices.floatValue());
            } else {
                ll_goods_balance.setVisibility(View.VISIBLE);
                tv_goods_prices.setText("￥" + totalPrices.floatValue());
            }
        } else {
            ll_goods_balance.setVisibility(View.GONE);
        }
    }

    //取消订单
    //BOrder/APP_CancelOrder?OrderID={OrderID}
    public void OrderCancel(String orderID) {
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_CancelOrder?OrderID=%s";
        url = String.format(url, orderID);
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
                                    mDatas.clear();
                                    mCurrentPageIndex = 1;//重置为第一页
                                    Orders(mCurrentTypeId, mCurrentPageIndex);
                                    mCurrentPageIndex++;//加载下一页
                                    showToast("取消成功");
                                } else {
                                    showToast("取消失败");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //确认收货
    //BOrder/APP_ConfirmReceive?OrderID={OrderID}
    public void ReceiveComfirm(String orderID) {
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_ConfirmReceive?OrderID=%s";
        url = String.format(url, orderID);
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
                                    mDatas.clear();
                                    mCurrentPageIndex = 1;//重置为第一页
                                    Orders(mCurrentTypeId, mCurrentPageIndex);
                                    mCurrentPageIndex++;//加载下一页
                                    showToast("订单已完成");
                                } else {
                                    showToast("收货失败，请重试");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //删除订单
    //BOrder/APP_DeleteOrder?OrderID={OrderID}
    public void OrderDelete(String orderID) {
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_DeleteOrder?OrderID=%s";
        url = String.format(url, orderID);
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
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd", "111111111" + s);
                        closeProgressDialog1();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    mDatas.clear();
                                    mCurrentPageIndex = 1;//重置为第一页
                                    Orders(mCurrentTypeId, mCurrentPageIndex);
                                    mCurrentPageIndex++;//加载下一页
                                    showToast("删除成功");
                                } else {
                                    showToast("删除失败");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取订单的支付信息
    //
    public void OrderPayInformation(String orderIDs) {
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_GetGoPayInfo_Post";
        //请求参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("IDS", orderIDs);
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
                                    mPayInformation = gson.fromJson(jsonObject.getString("Obj"), OrderPay.class);
                                    mSubject = getString(R.string.common_me_company);
                                    mDescription = "购买商品总价：" + mPayInformation.Amount + "元";
                                    Intent intent = new Intent(Orders.this, Pay.class);
                                    intent.putExtra("ORDER_PAY", mPayInformation);
                                    intent.putExtra("SUBJECT", mSubject);
                                    intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                                    intent.putExtra("DESCRIPTION", mDescription);
                                    startActivity(intent);
                                } else {
                                    showToast(R.string.mall_pay_submit_failure);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取订单列表
    //BOrder/APP_GetOrderList?ResidentID={ResidentID}&ViceType={ViceType}&PageCnt={PageCnt}&PageIndex={PageIndex}
    public void Orders(Integer viceType, Integer pageIndex) {
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_GetOrderList?ResidentID=%s&ViceType=%s&PageCnt=%s&PageIndex=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId(), viceType, Services.PAGE_SIZE, pageIndex);
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
                                    Type type = new TypeToken<List<com.ldnet.entities.Orders>>(){}.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"),type);
                                    if (datas != null && datas.size() > 0) {
                                        mDatas.addAll(datas);
                                        mAdapter = new ListViewAdapter<com.ldnet.entities.Orders>(Orders.this, R.layout.item_orders, mDatas) {
                                            @Override
                                            public void convert(ViewHolder holder, final com.ldnet.entities.Orders orders) {
                                                holder.setText(R.id.tv_goods_business, orders.BN)
                                                        .setText(R.id.tv_items_prices, String.valueOf(orders.AM));

                                                CheckBox chkbox = holder.getView(R.id.chk_goods_checked);
                                                if (mCurrentTypeId.equals(ORDERS_STATUS_NOTPAY)) {
                                                    chkbox.setChecked(orders.IsChecked);
                                                    chkbox.setVisibility(View.VISIBLE);
                                                    chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                        @Override
                                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                            orders.IsChecked = b;
                                                            updateBlanceStatus();
                                                        }
                                                    });
                                                } else {
                                                    chkbox.setVisibility(View.GONE);
                                                }

                                                Button delCancel = holder.getView(R.id.btn_orders_delete_cancel);
                                                Button details = holder.getView(R.id.btn_orders_details);
                                                if (mCurrentTypeId.equals(ORDERS_STATUS_NOTPAY)) {
                                                    delCancel.setText("取消订单");
                                                    delCancel.setVisibility(View.VISIBLE);
                                                } else if (mCurrentTypeId.equals(ORDERS_STATUS_SENDED)) {
                                                    delCancel.setText("确认收货");
                                                    delCancel.setVisibility(View.VISIBLE);
                                                } else if (mCurrentTypeId.equals(ORDERS_STATUS_SUCCESS)) {
                                                    delCancel.setText("删除订单");
                                                    delCancel.setVisibility(View.VISIBLE);
                                                } else {
                                                    delCancel.setVisibility(View.GONE);
                                                }

                                                //取消 或 删除订单
                                                delCancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        if (mCurrentTypeId.equals(ORDERS_STATUS_NOTPAY)) {
                                                            OrderCancel(orders.OID);
                                                        } else if (mCurrentTypeId.equals(ORDERS_STATUS_SENDED)) {
                                                            ReceiveComfirm(orders.OID);
                                                        } else if (mCurrentTypeId.equals(ORDERS_STATUS_SUCCESS)) {
                                                            OrderDelete(orders.OID);
                                                        } else {
                                                            return;
                                                        }
                                                    }
                                                });

                                                //订单详细页
                                                details.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        HashMap<String, String> extras = new HashMap<String, String>();
                                                        extras.put("ORDER_ID", orders.OID);
                                                        try {
                                                            gotoActivity(Order_Details.class.getName(), extras);
                                                        } catch (ClassNotFoundException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                                ListView listView = holder.getView(R.id.lv_orders_goods);
                                                listView.setAdapter(new ListViewAdapter<OD>(this.mContext, R.layout.item_orders_item, orders.OD) {
                                                    @Override
                                                    public void convert(ViewHolder holder, OD od) {
                                                        //商品图片
                                                        ImageView view = holder.getView(R.id.iv_goods_image);
                                                        if (!TextUtils.isEmpty(od.GI)) {
                                                            ImageLoader.getInstance().displayImage(services.getImageUrl(od.GI), view, imageOptions);
                                                        } else {
                                                            view.setImageResource(R.drawable.default_goods);
                                                        }
                                                        //商品信息
                                                        holder.setText(R.id.tv_goods_title, od.GN)
                                                                .setText(R.id.tv_goods_stock, od.GTN)
                                                                .setText(R.id.tv_goods_price, "￥" + od.GP)
                                                                .setText(R.id.tv_goods_numbers, String.valueOf(od.N));
                                                    }
                                                });
                                                Utility.setListViewHeightBasedOnChildren(listView);
                                            }
                                        };
                                        lv_mall_orders.setAdapter(mAdapter);
                                        updateBlanceStatus();
                                        mOrderEmpty.setVisibility(View.GONE);
                                    } else {
                                        if (mDatas != null && mDatas.size() > 0) {
                                            showToast("沒有更多数据");
                                        } else {
                                            mOrderEmpty.setVisibility(View.VISIBLE);
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
