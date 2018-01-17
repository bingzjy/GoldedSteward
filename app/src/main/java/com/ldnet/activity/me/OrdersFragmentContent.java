package com.ldnet.activity.me;

import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.autonavi.rtbt.IFrameForRTBT;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseFragment;
import com.ldnet.activity.mall.Goods_Details;
import com.ldnet.activity.mall.Order_Details;
import com.ldnet.activity.mall.Pay;
import com.ldnet.entities.Goods;
import com.ldnet.entities.OD;
import com.ldnet.entities.OrderPay;
import com.ldnet.entities.Orders;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.OrderService;
import com.ldnet.utility.*;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.math.BigDecimal;
import java.util.*;

import static com.ldnet.utility.Utility.imageOptions;

/**
 *
 */
public class OrdersFragmentContent extends BaseFragment implements View.OnClickListener {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;

    //订单状态
    private RadioGroup rdg_orders_tabs;
    private LinearLayout ll_goods_balance;
    private TextView tv_goods_prices;
    private Button btn_goods_balance;

    //订单列表
    private List<Orders> mDatas;
    private ListViewAdapter<Orders> mAdapter;
    private CustomListView2 lv_mall_orders;
    private Integer mCurrentTypeId = 1;
    private Integer mCurrentPageIndex = 1;

    //订单子状态ID 1:待付款，3:已发货，4:已签收，5:待发货，6:已关闭 ，7：已取消 有取消原因CM
    private static final Integer ORDERS_STATUS_NOTPAY = 1;
    private static final Integer ORDERS_STATUS_SENDED = 3;
    private static final Integer ORDERS_STATUS_SUCCESS = 4;
    private static final Integer ORDERS_STATUS_NOTSENDED = 5;
    private static final Integer ORDERS_STATUS_CLOSE = 6;
    private static final Integer ORDERS_STATUS_CANCEL = 7;

    // 支付信息，包含订单号和金额
    private OrderPay mPayInformation;
    // 商品标题
    private String mSubject;
    // 商品描述
    private String mDescription;
    private TextView mOrderEmpty;
    private Handler mHandler;
    private String mFromClassName;
    private List<Orders> datas;
    private PullToRefreshScrollView mPullToRefreshScrollView;

    private OrderService orderService;

    public static Fragment getInstance(Bundle bundle) {
        OrdersFragmentContent fragment = new OrdersFragmentContent();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmnet_order, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(View view) {
        services = new Services();
        orderService=new OrderService(getActivity());
        mHandler = new Handler();
        //订单为空
        mOrderEmpty = (TextView) view.findViewById(R.id.order_empty);
        String title = getArguments().getString("title");
        if (title != null) {
            if (title.equals("待付款")) {
                mOrderEmpty.setText("您没有未支付的订单！");
                mCurrentTypeId = ORDERS_STATUS_NOTPAY;
            } else if (title.equals("待发货")) {
                mOrderEmpty.setText("您没有未发货的订单！");
                mCurrentTypeId = ORDERS_STATUS_NOTSENDED;
            } else if (title.equals("待收货")) {
                mOrderEmpty.setText("您没有未收货的订单！");
                mCurrentTypeId = ORDERS_STATUS_SENDED;
            } else if (title.equals("已完成")) {
                mOrderEmpty.setText("您没有已完成的订单！");
                mCurrentTypeId = ORDERS_STATUS_SUCCESS;
            } else if (title.equals("已取消")) {
                mOrderEmpty.setText("您没有已取消的订单！");
                mCurrentTypeId = ORDERS_STATUS_CANCEL;
            }
        }
        ll_goods_balance = (LinearLayout) view.findViewById(R.id.ll_goods_balance);
        tv_goods_prices = (TextView) view.findViewById(R.id.tv_goods_prices);
        btn_goods_balance = (Button) view.findViewById(R.id.btn_goods_balance);

        //订单列表

        mPullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(getActivity()));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(getActivity()));
        mDatas = new ArrayList<Orders>();
        lv_mall_orders = (CustomListView2) view.findViewById(R.id.lv_mall_orders);
        lv_mall_orders.setFocusable(false);
        mCurrentPageIndex = 1;//重置为第一页

        //获取订单数据
        orderService.getOrders(mCurrentTypeId, mCurrentPageIndex,handlerGetOrders);
        mCurrentPageIndex++;//加载下一页

        mAdapter = new ListViewAdapter<Orders>(getActivity(), R.layout.item_orders, mDatas) {
            @Override
            public void convert(ViewHolder holder, final Orders orders) {
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
                // 增加投诉
                final Button complain = holder.getView(R.id.btn_orders_complain);
                complain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), Complain.class);
                        intent.putExtra("ORDER_ID", orders.OID);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    }
                });
                Button delCancel = holder.getView(R.id.btn_orders_delete_cancel);
                Button details = holder.getView(R.id.btn_orders_details);
                if (mCurrentTypeId.equals(ORDERS_STATUS_NOTPAY)) {
                    delCancel.setText("取消订单");
                    delCancel.setVisibility(View.VISIBLE);
                    complain.setVisibility(View.GONE);
                } else if (mCurrentTypeId.equals(ORDERS_STATUS_SENDED)) {
                    delCancel.setText("确认收货");
                    delCancel.setVisibility(View.VISIBLE);
                } else if (mCurrentTypeId.equals(ORDERS_STATUS_SUCCESS)) {
                    delCancel.setText("删除订单");
                    delCancel.setVisibility(View.VISIBLE);
                } else {
                    delCancel.setVisibility(View.GONE);
                }

                //取消 、确认收货 、删除订单
                delCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mCurrentTypeId.equals(ORDERS_STATUS_NOTPAY)) {
                          orderService.orderCancel(orders.OID,handlerOrderCancel);
                        } else if (mCurrentTypeId.equals(ORDERS_STATUS_SENDED)) {
                            orderService.receiveComfirm(orders.OID,handlerReceiveComfirm);
                        } else if (mCurrentTypeId.equals(ORDERS_STATUS_SUCCESS)) {
                            showProgressDialog1();
                            orderService.orderDelete(orders.OID,handlerDeleteOrder);
                        } else {
                            return;
                        }
                    }
                });

                //订单详细页
                details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Order_Details.class);
                        intent.putExtra("ORDER_ID", orders.OID);
                        if (orders.OT==0){
                            intent.putExtra("GOODS_ID", orders.OD.get(0).getGID());
                        }else if (orders.OT==1){
                            intent.putExtra("GOODS_ID", "");
                        }
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    }
                });

                MyListView listView = holder.getView(R.id.lv_orders_goods);

                if (orders.OT == 1) {  //服务类商品
                    listView.setAdapter(new ListViewAdapter<Orders.ODSBean>(this.mContext, R.layout.item_orders_item, orders.ODS) {
                        @Override
                        public void convert(ViewHolder holder, Orders.ODSBean od) {
                            //商品图片
                            ImageView view = holder.getView(R.id.iv_goods_image);
                            if (!TextUtils.isEmpty(od.SI)) {
                                ImageLoader.getInstance().displayImage(services.getImageUrl(od.SI), view, Utility.imageOptions);
                            } else {
                                view.setImageResource(R.drawable.default_goods);
                            }
                            //商品信息
                            holder.setText(R.id.tv_goods_title, od.SN)
                                    .setText(R.id.tv_title1,"服务时长：")
                                    .setText(R.id.tv_goods_stock, od.T+"小时")
                                    .setText(R.id.tv_title2,"单价：")
                                    .setText(R.id.tv_goods_price, "￥" + od.P+"/时")
                                    .setText(R.id.tv_title3,"服务时间：")
                                    .setText(R.id.tv_goods_numbers, od.SM);
                        }
                    });
                } else {  //普通商品
                    listView.setAdapter(new ListViewAdapter<OD>(this.mContext, R.layout.item_orders_item, orders.OD) {
                        @Override
                        public void convert(ViewHolder holder, OD od) {
                            //商品图片
                            ImageView view = holder.getView(R.id.iv_goods_image);
                            if (!TextUtils.isEmpty(od.GI)) {
                                ImageLoader.getInstance().displayImage(services.getImageUrl(od.GI), view, Utility.imageOptions);
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
                }

            }
        };
        lv_mall_orders.setAdapter(mAdapter);


        lv_mall_orders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showToast("单击");
                Orders order=mDatas.get(position);
                Intent intent = new Intent(getActivity(), Order_Details.class);
                intent.putExtra("ORDER_ID", order.OID);
                if (order.OT==0){
                    intent.putExtra("GOODS_ID", order.OD.get(0).getGID());
                }else if (order.OT==1){
                    intent.putExtra("GOODS_ID", "");
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        Utility.setListViewHeightBasedOnChildren(lv_mall_orders);
        btn_goods_balance.setOnClickListener(this);
        initEvents();
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                mCurrentPageIndex = 1;//重置为第一页
                orderService.getOrders(mCurrentTypeId, mCurrentPageIndex,handlerGetOrders);
                mCurrentPageIndex++;//加载下一页
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    orderService.getOrders(mCurrentTypeId, mCurrentPageIndex,handlerGetOrders);
                    mCurrentPageIndex++;//加载下一页
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goods_balance:
                String orderIds = "";
                for (Orders orders : mDatas) {
                    if (orders.IsChecked) {
                        if (!TextUtils.isEmpty(orderIds)) {
                            orderIds += "," + orders.OID;
                        } else {
                            orderIds += orders.OID;
                        }
                    }
                }
                //根据订单id获取订单具体信息
                orderService.getOrderPayInformation(orderIds,handlerGetOrderPayInfo);
                break;
        }
    }

    //更新订单状态
    private void updateBlanceStatus() {
        mAdapter.notifyDataSetChanged();
        if (mCurrentTypeId.equals(ORDERS_STATUS_NOTPAY)) {
            BigDecimal totalPrices = new BigDecimal("0.00");
            for (Orders orders : mDatas) {
                if (orders.IsChecked) {
                    totalPrices = totalPrices.add(new BigDecimal(orders.AM));
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


    //获取订单列表数据
    Handler handlerGetOrders=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    lv_mall_orders.setVisibility(View.VISIBLE);
                    mOrderEmpty.setVisibility(View.GONE);
                    datas = (List<Orders>) msg.obj;
                    mDatas.addAll(datas);
                    updateBlanceStatus();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (mDatas != null && mDatas.size() > 0) {
                        showToast("沒有更多数据");
                        lv_mall_orders.setVisibility(View.VISIBLE);
                        mOrderEmpty.setVisibility(View.GONE);
                    } else {
                        lv_mall_orders.setVisibility(View.GONE);
                        mOrderEmpty.setVisibility(View.VISIBLE);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //获取订单支付信息
    Handler handlerGetOrderPayInfo=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mPayInformation=(OrderPay)msg.obj;
                    mSubject = getString(R.string.common_me_company);
                    mDescription = "购买商品总价：" + mPayInformation.Amount + "元";
                    Intent intent = new Intent(getActivity(), Pay.class);
                    intent.putExtra("ORDER_PAY", mPayInformation);
                    intent.putExtra("SUBJECT", mSubject);
                    intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                    intent.putExtra("DESCRIPTION", mDescription);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(R.string.mall_pay_submit_failure);
                    break;
            }
        }
    };

    //删除订单
    Handler handlerDeleteOrder=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog1();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mDatas.clear();
                    mCurrentPageIndex = 1;//重置为第一页
                    orderService.getOrders(mCurrentTypeId, mCurrentPageIndex,handlerGetOrders);
                    mCurrentPageIndex++;//加载下一页
                    showToast("删除成功");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast("删除失败");
                    break;
            }
        }
    };


    //确认收货
    Handler handlerReceiveComfirm=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mDatas.clear();
                    mCurrentPageIndex = 1;//重置为第一页
                    orderService.getOrders(mCurrentTypeId, mCurrentPageIndex,handlerGetOrders);
                    mCurrentPageIndex++;//加载下一页
                    showToast("订单已完成");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast("收货失败，请重试");
                    break;
            }
        }
    };


    //取消订单
    Handler handlerOrderCancel=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mDatas.clear();
                    mCurrentPageIndex = 1;//重置为第一页
                    orderService.getOrders(mCurrentTypeId, mCurrentPageIndex,handlerGetOrders);
                    mCurrentPageIndex++;//加载下一页
                    showToast("取消成功");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast("取消失败");
                    break;
            }
        }
    };

}
