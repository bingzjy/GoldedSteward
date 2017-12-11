package com.ldnet.activity.mall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ldnet.activity.Browser;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Goods;
import com.ldnet.entities.Goods1;
import com.ldnet.entities.OD;
import com.ldnet.entities.OrderPay;
import com.ldnet.entities.Orders;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.OrderService;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.third.Alipay.PayKeys;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import okhttp3.Call;

import static com.unionpay.mobile.android.global.a.I;

/**
 * Created by Alex on 2015/9/28.
 */
public class Order_Details extends BaseActionBarActivity {

    // 标题
    private TextView tv_page_title;
    // 返回
    private ImageButton btn_back;

    private TextView tv_orders_numbers;
    private TextView tv_orders_created;
    private TextView tv_orders_status;
    //    订单取消原因
    private TextView tv_order_cancel_reason;
    private TextView tv_address_title;
    private TextView tv_address_zipcode;
    private TextView tv_address_name;
    private ListView lv_order_details;
    private LinearLayout ll_goods_balance;
    private TextView tv_orders_prices;
    private Button btn_orders_balance;
    private TextView tv_business_name;
    private TextView tv_business_phone;
    private ImageButton ibtn_call_business;

    // 服务
    private Services services;
    // 获取订单详细
    private com.ldnet.entities.Orders mOrders;


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
    private static final Integer ORDERS_STATUS_CANCEL = 7;
    private LinearLayout mLlOrderCancelReason;
    private Button mOrderQuery;
    private Goods goods;
    private OrderService orderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_order_details);

        // 标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.mall_order_details);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_orders_numbers = (TextView) findViewById(R.id.tv_orders_numbers);
        tv_orders_created = (TextView) findViewById(R.id.tv_orders_created);
        tv_orders_status = (TextView) findViewById(R.id.tv_orders_status);
        mLlOrderCancelReason = (LinearLayout) findViewById(R.id.ll_order_cancel);
        tv_order_cancel_reason = (TextView) findViewById(R.id.tv_orders_cancel);
        tv_address_title = (TextView) findViewById(R.id.tv_address_title);
        tv_address_zipcode = (TextView) findViewById(R.id.tv_address_zipcode);
        tv_address_name = (TextView) findViewById(R.id.tv_address_name);
        lv_order_details = (ListView) findViewById(R.id.lv_order_details);
        ll_goods_balance = (LinearLayout) findViewById(R.id.ll_goods_balance);
        tv_orders_prices = (TextView) findViewById(R.id.tv_orders_prices);
        btn_orders_balance = (Button) findViewById(R.id.btn_orders_balance);
        tv_business_name = (TextView) findViewById(R.id.tv_business_name);
        tv_business_phone = (TextView) findViewById(R.id.tv_business_phone);
        ibtn_call_business = (ImageButton) findViewById(R.id.ibtn_call_business);
        //订单查询
        mOrderQuery = (Button) findViewById(R.id.bt_orders_query);
        initData();
        initEvent();
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_orders_balance.setOnClickListener(this);
        ibtn_call_business.setOnClickListener(this);
        mOrderQuery.setOnClickListener(this);
    }

    //初始化数据
    public void initData() {
        //加载数据
        keys = new PayKeys();
        String orderId = getIntent().getStringExtra("ORDER_ID");
        String goodsId = getIntent().getStringExtra("GOODS_ID");
        services = new Services();
        orderService=new OrderService(this);
        orderService.getOrderDetails(orderId,handlerOrderDetail);
        if (!TextUtils.isEmpty(goodsId)){
            orderService.getGoodsInfo(goodsId,handlerGoodsDetail);
        }
        Utility.setListViewHeightBasedOnChildren(lv_order_details);
    }


    // 点击事件处理
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_orders_balance://去支付
                orderService.getOrderPayInformation(mOrders.OID,handlerGetOrderPayInfo);
                break;
            case R.id.ibtn_call_business://拨打商家电话
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mOrders.BM));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                break;
            case R.id.bt_orders_query://订单查询
                if (mOrders != null) {
                    if (mOrders.ECode != null) {
                        String eCode = mOrders.ECode; //快递编码
                        String eNumber = mOrders.ENumber;//快递单号
                        Intent intentQuery = new Intent(this, Browser.class);
                        String url = "http://m.kuaidi100.com/index_all.html?type=" + eCode + "&postid=" + eNumber;
                        intentQuery.putExtra("PAGE_URL", url);
                        intentQuery.putExtra("PAGE_TITLE", "快递查询");
                        intentQuery.putExtra("FROM_CLASS_NAME", Order_Details.class.getName());
                        startActivity(intentQuery);
                        overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                    } else {
                        showToast("暂无快递信息！");
                    }
                }
                break;
            default:
                break;
        }
    }


    //获取订单详情
  Handler handlerOrderDetail=new Handler(){
      @Override
      public void handleMessage(Message msg) {
          super.handleMessage(msg);
          switch (msg.what){
              case BaseService.DATA_SUCCESS:
                  mOrders = (Orders) msg.obj;
                  //订单信息
                  tv_orders_numbers.setText(mOrders.ONB);
                  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  tv_orders_created.setText(Services.subStr(mOrders.PD));
                  tv_orders_status.setText(mOrders.OVN);
                  if (mOrders.OVID == ORDERS_STATUS_CANCEL) {
                      mLlOrderCancelReason.setVisibility(View.VISIBLE);
                      tv_order_cancel_reason.setText(mOrders.CM);
                  }

                  //收货地址
                  if (TextUtils.isEmpty(mOrders.AAD)) {
                      tv_address_title.setText(mOrders.AR + mOrders.ACT + mOrders.AA);
                  } else {
                      tv_address_title.setText(mOrders.AAD);
                  }
                  tv_address_zipcode.setVisibility(View.GONE);
                  tv_address_name.setText(mOrders.AN + " " + mOrders.AMP);

                  //商家信息
                  tv_business_name.setText(mOrders.BN);
                  tv_business_phone.setText(mOrders.BM);

                  //是否需要支付
                  if (mOrders.OVID == 1) {
                      ll_goods_balance.setVisibility(View.VISIBLE);
                      //支付Banner
                      tv_orders_prices.setText("￥" + mOrders.AM);
                  } else {
                      ll_goods_balance.setVisibility(View.GONE);
                  }

                  if (mOrders.OT==0){
                      //订单详情
                      lv_order_details.setAdapter(new ListViewAdapter<OD>(Order_Details.this, R.layout.item_orders_item, mOrders.OD) {
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
                  }else{
                      lv_order_details.setAdapter(new ListViewAdapter<Orders.ODSBean>(Order_Details.this, R.layout.item_orders_item, mOrders.ODS) {
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
                  }

                  break;
              case BaseService.DATA_SUCCESS_OTHER:

                  break;
              case BaseService.DATA_FAILURE:
              case BaseService.DATA_REQUEST_ERROR:
                  showToast(msg.obj.toString());
                  break;
          }
      }
  };


    //获取商品详情
    Handler handlerGoodsDetail=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    Goods1 goods1=(Goods1)msg.obj;
                    goods = new Goods();
                    goods.setGID(goods1.getGID());
                    goods.setGP(goods1.getGP());
                    goods.setGSID(goods1.getGSID());
                    goods.setGSN(goods1.getGSN());
                    goods.setIMG(goods1.getIMG());
                    goods.setRID(goods1.getRID());
                    goods.setRP(goods1.getRP());
                    goods.setSN(goods1.getSN());
                    goods.setST(goods1.getST());
                    goods.setT(goods1.getT());
                    goods.setType(1);
                    goods.setURL("http://b.goldwg.com//Goods/Preview?ID=" + goods1.getGID());
                    lv_order_details.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(Order_Details.this, Goods_Details.class);
                            intent.putExtra("GOODS", goods);
                            intent.putExtra("PAGE_TITLE", "");
                            intent.putExtra("FROM_CLASS_NAME", Order_Details.class.getName());
                            intent.putExtra("URL", goods.getURL());
                            intent.putExtra("CID", goods.getGID());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                        }
                    });
                    break;
                case BaseService.DATA_SUCCESS_OTHER:

                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //获取订单支付信息
    Handler handlerGetOrderPayInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    mPayInformation = (OrderPay) msg.obj;
                    mSubject = getString(R.string.common_me_company);
                    mDescription = "购买商品总价：" + mOrders.AM + "元";
                    Intent intent = new Intent(Order_Details.this, Pay.class);
                    intent.putExtra("ORDER_PAY", mPayInformation);
                    intent.putExtra("SUBJECT", mSubject);
                    intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                    intent.putExtra("DESCRIPTION", mDescription);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(R.string.mall_pay_submit_failure);
                    break;
            }
        }
    };
}
