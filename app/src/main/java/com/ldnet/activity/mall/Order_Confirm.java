package com.ldnet.activity.mall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.me.AddressEdit;
import com.ldnet.activity.me.ChooseCoupon;
import com.ldnet.entities.AddressSimple;
import com.ldnet.entities.Goods;
import com.ldnet.entities.OrderPay;
import com.ldnet.entities.RS;
import com.ldnet.entities.SubOrders;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.OrderService;
import com.ldnet.utility.DialogAddress;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Services;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.third.Alipay.PayKeys;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alex on 2015/9/28.
 */
public class Order_Confirm extends BaseActionBarActivity {

    // 标题
    private TextView tv_page_title;
    // 返回
    private ImageButton btn_back;
    // 服务
    private Services services;
    //是否来自商品详细页面
    private Boolean mIsFromGoodsDetails;
    private String mIsFromChooseCoupon;
    //来自哪里-商品详细页或者购物车
    private String mFromClassName;
    //浏览的Web页面Url
    private String mUrl;
    //页面标题
    private String mTitle;
    //商品ID
    private Goods mGoods;
    //收货地址
    private List<AddressSimple> mAddress;
    private AddressSimple mCurrentAddress;
    private List<SubOrders> mSubOrders;
    //
    private LinearLayout ll_order_address_select;
    private TextView tv_address_name;
    private TextView tv_address_zipcode;
    private TextView tv_address_title;
    private ListView lv_order_details;
    private CheckBox chk_pay_type_checked;
    private CheckBox mUnionPayTypeChecked;
    private TextView tv_goods_numbers;
    private TextView tv_goods_prices;
    private Button btn_goods_balance;
    //订单总金额
    private BigDecimal totalPrices = new BigDecimal("0.00");
    private BigDecimal totalYhjjm = new BigDecimal("0.00");

    //
    private static final Integer PAY_TYPE_OFFLINE = 1;
    private static final Integer PAY_TYPE_ONLINE = 2;

    //
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
    private String mCID;
    private String serverMode = "01";
    private String tn;
    //    实际支付的钱
    protected Float mOnlinePay;
    private OrderPay orderPay;
    private OrderService orderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置布局
        setContentView(R.layout.activity_mall_order_confirm);
        //初始化支付相关信息
        keys = new PayKeys();
        orderService=new OrderService(this);

        //接收传递的参数
        mIsFromGoodsDetails = Boolean.valueOf(getIntent().getStringExtra("IS_FROM_GOODSDETAILS"));
        mFromClassName = getIntent().getStringExtra("FROM_CLASS_NAME");
        if (mIsFromGoodsDetails) {
            mGoods = (Goods) getIntent().getSerializableExtra("GOODS");
            mTitle = getIntent().getStringExtra("PAGE_TITLE");
            mUrl = getIntent().getStringExtra("URL");
            mCID = getIntent().getStringExtra("CID");
        }

        // 页面标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.mall_order_sure);
        // 返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        ll_order_address_select = (LinearLayout) findViewById(R.id.iv_order_address_select);
        tv_address_name = (TextView) findViewById(R.id.tv_address_name);
        tv_address_zipcode = (TextView) findViewById(R.id.tv_address_zipcode);
        tv_address_title = (TextView) findViewById(R.id.tv_address_title);
        lv_order_details = (ListView) findViewById(R.id.lv_order_details);

        // 服务初始化
        services = new Services();
        //初始化订单商品详细
        mSubOrders = (List<SubOrders>) getIntent().getSerializableExtra("SUB_ORDERS");

        lv_order_details.setAdapter(new ListViewAdapter<SubOrders>(this, R.layout.item_order, mSubOrders) {
            @Override
            public void convert(final ViewHolder holder, final SubOrders orders) {
                BigDecimal a1 = new BigDecimal(Float.toString(orders.TotalPrices()));
                BigDecimal a2 = new BigDecimal(Float.toString(orders.YHJJM));
                mOnlinePay = a1.subtract(a2).floatValue();
                holder.setText(R.id.tv_goods_business, orders.BN) //商家名称
                        .setText(R.id.tv_items_prices, "￥" + mOnlinePay);//小计

                //运费描述
                TextView tv_goods_freight_desc = holder.getView(R.id.tv_goods_freight_desc);//运费描述
                if (orders.ISP) {
                    holder.setText(R.id.tv_goods_freight, "￥" + orders.PE);//运费
                    if (orders.ISPH) {
                        tv_goods_freight_desc.setVisibility(View.VISIBLE);
                        holder.setText(R.id.tv_goods_freight_desc, "购满" + orders.MPE + "元，享免运费服务");
                    } else {
                        tv_goods_freight_desc.setVisibility(View.GONE);
                    }
                } else {
                    holder.setText(R.id.tv_goods_freight, "￥" + orders.PE);//运费
                    tv_goods_freight_desc.setVisibility(View.GONE);
                }

                //给商家的留言
                EditText et_order_message = holder.getView(R.id.et_order_message);
                et_order_message.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        orders.Message = editable.toString().trim();
                    }
                });

                //商品子列表
                ListView lv_shopping_carts_goods = holder.getView(R.id.lv_orders_goods);
                List<RS> rss = orders.RS;
                lv_shopping_carts_goods.setAdapter(new ListViewAdapter<RS>(this.mContext, R.layout.item_order_item, rss) {
                    @Override
                    public void convert(ViewHolder holder, final RS rs) {
                        //商家名称
                        holder.setText(R.id.tv_goods_title, rs.GN)
                                .setText(R.id.tv_goods_stock, rs.GGN)
                                .setText(R.id.tv_goods_price, "￥" + rs.GP)
                                .setText(R.id.tv_goods_numbers, String.valueOf(rs.GC));
                        //商品图片
                        ImageView image = holder.getView(R.id.iv_goods_image);
                        if (!TextUtils.isEmpty(rs.GI)) {
                            ImageLoader.getInstance().displayImage(services.getImageUrl(rs.GI), image, imageOptions);
                        } else {
                            image.setImageResource(R.drawable.default_goods);
                        }
                    }
                });
                Utility.setListViewHeightBasedOnChildren(lv_shopping_carts_goods);
                // -----------------------------------
                if (orders.ISYHJ.equals(false)) {
                    orders.YHJID = "";
                }
                // 是否使用优惠劵 点击选择优惠劵
                LinearLayout llCoupon = holder.getView(R.id.ll_me_coupon);
                llCoupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Order_Confirm.this, ChooseCoupon.class);
                        intent.putExtra("SUB_ORDERS", (Serializable) mSubOrders);
                        intent.putExtra("ORDERS_POSITION", holder.getPosition());
                        intent.putExtra("IS_FROM_GOODSDETAILS", "false");
                        intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                        intent.putExtra("GOODS", mGoods);
                        intent.putExtra("PAGE_TITLE", mTitle);
                        intent.putExtra("URL", mUrl);
                        intent.putExtra("CID", mCID);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                        Order_Confirm.this.finish();
                    }
                });

                holder.setText(R.id.tv_goods_coupon, "优惠劵减免" + orders.YHJJM + "元");
            }

        });
        Utility.setListViewHeightBasedOnChildren(lv_order_details);

        //订单总数量和总金额
        tv_goods_numbers = (TextView) findViewById(R.id.tv_goods_numbers);
        tv_goods_prices = (TextView) findViewById(R.id.tv_goods_prices);
        Integer totalNumbers = 0;
        for (SubOrders so : mSubOrders) {
            totalNumbers += so.TotalNumbers();
            totalPrices = totalPrices.add(new BigDecimal(so.TotalPrices().toString()));
            totalYhjjm = totalYhjjm.add(new BigDecimal(so.TotalYhjjm().toString()));
        }
        tv_goods_numbers.setText(String.valueOf(totalNumbers));
        mOnlinePay = totalPrices.subtract(totalYhjjm).floatValue();
        tv_goods_prices.setText("￥" + String.valueOf(mOnlinePay));
        //确认订单按钮
        btn_goods_balance = (Button) findViewById(R.id.btn_goods_balance);
        //初始化收货地址
        orderService.getPlaceOfReceipt( getAddressHandler);
        initEvent();
    }


    //绑定收货地址信息
    private void bindingAddress() {
        tv_address_title.setText(mCurrentAddress.AD);
        tv_address_zipcode.setText(mCurrentAddress.ZC);
        tv_address_name.setText(mCurrentAddress.NP);
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
        ll_order_address_select.setOnClickListener(this);
        btn_goods_balance.setOnClickListener(this);
    }

    // 点击事件处理
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_back:
                    if (mIsFromGoodsDetails) {
                        Intent intent = new Intent(Order_Confirm.this, Goods_Details.class);
                        intent.putExtra("GOODS", mGoods);
                        intent.putExtra("PAGE_TITLE", mTitle);
                        intent.putExtra("URL", mUrl);
                        intent.putExtra("CID", mCID);
                        intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
                        this.finish();
                    } else if (mFromClassName.equals(ShopStore.class.getName()) || mFromClassName.equals(StoreGoods.class.getName())
                            || mFromClassName.equals(GoodsList.class.getName())) {
                        finish();
                    } else {
                        //返回购物车或者商品详细页？mFromClassName
                        gotoActivityAndFinish(Shopping_Carts.class.getName(), null);
                    }
                    break;
                case R.id.iv_order_address_select://选择地址
                    if (mCurrentAddress == null) {
                        showToast("请设置默认地址");
                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put("FROM_ORDER_CONFIRM", "true");
                        extras.put("LEFT", "LEFT");
                        try {
                            gotoActivity(AddressEdit.class.getName(), extras);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        new DialogAddress(Order_Confirm.this, mCurrentAddress.ID, new AddressSelect()).show();
                    }

                    break;
                case R.id.btn_goods_balance://提交订单
                    if (mCurrentAddress == null) {
                        showToast(getResources().getString(R.string.mall_not_goods_address));
                    } else {
                        if (mPayInformation == null) {
                            //提交订单
                          //  OrderSubmitNew(mSubOrders, mCurrentAddress.ID);
                            orderService.orderSubmitConfirm(mSubOrders,mCurrentAddress.ID,orderSubmitHandler);
                        } else {
                            Intent intent = new Intent(this, Pay.class);
                            intent.putExtra("ORDER_PAY", mPayInformation);
                            intent.putExtra("SUBJECT", mSubject);
                            intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                            intent.putExtra("DESCRIPTION", mDescription);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                        }
                    }

                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mIsFromGoodsDetails) {
                Intent intent = new Intent(Order_Confirm.this, Goods_Details.class);
                intent.putExtra("GOODS", mGoods);
                intent.putExtra("PAGE_TITLE", mTitle);
                intent.putExtra("URL", mUrl);
                intent.putExtra("CID", mCID);
                intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
                this.finish();
            } else if (mFromClassName.equals(ShopStore.class.getName()) || mFromClassName.equals(StoreGoods.class.getName())
                    || mFromClassName.equals(GoodsList.class.getName())) {
                finish();
            } else {
                //返回购物车或者商品详细页？mFromClassName
                try {
                    gotoActivityAndFinish(Shopping_Carts.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }


    class AddressSelect implements DialogAddress.OnAddressDialogListener {
        @Override
        public void Confirm(String addressId) {
            for (AddressSimple as : mAddress) {
                if (as.ID.equals(addressId)) {
                    as.IsChecked = true;
                    mCurrentAddress = as;
                    bindingAddress();
                } else {
                    as.IsChecked = false;
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
//        Log.i("initData", initData.toString() + "");
        String str = data.getExtras().getString("pay_result");
//        Log.i("str", initData.getExtras() + "");
//        Bundle s = initData.getExtras();
        if (str.equalsIgnoreCase("success")) {
            // 支付成功后，extra中如果存在result_data，取出校验
            // result_data结构见c）result_data参数说明
//            if (initData.hasExtra("result_data")) {
//                String result = initData.getExtras().getString("result_data");
//                try {
//                    JSONObject resultJson = new JSONObject(result);
//                    String sign = resultJson.getString("sign");
//                    String dataOrg = resultJson.getString("initData");
//                    // 验签证书同后台验签证书
//                    // 此处的verify，商户需送去商户后台做验签
//                    boolean ret = RSAUtil.verify(dataOrg, sign, serverMode);
//                    if (ret) {
//                        // 验证通过后，显示支付结果
//                        msg = "支付成功！";
//                    } else {
//                        // 验证不通过后的处理
//                        // 建议通过商户后台查询支付结果
//                        msg = "支付失败！";
//                    }
//                } catch (JSONException e) {
//                }
//            } else {
            // 未收到签名信息
            // 建议通过商户后台查询支付结果
            msg = "支付成功！";

        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
//         builder.setCustomTitle();
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }



    Handler  getAddressHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mAddress=(List<AddressSimple>) msg.obj;
                    for (AddressSimple as : mAddress) {
                        if (as.ISD) {
                            mCurrentAddress = as;
                            bindingAddress();
                        }else{
                            mCurrentAddress = mAddress.get(0);
                            bindingAddress();
                        }
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast(R.string.mall_not_goods_address);
                    try {
                        gotoActivityAndFinish(AddressEdit.class.getName(), null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler orderSubmitHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mPayInformation=(OrderPay)msg.obj;
                    mSubject = getString(R.string.common_me_company);
                    mDescription = "购买商品总价：" + totalPrices.floatValue() + "元";
                    Intent intent = new Intent(Order_Confirm.this, Pay.class);

                    intent.putExtra("SUBJECT", mSubject);
                    intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                    intent.putExtra("totalPrices", totalPrices.floatValue());
                    intent.putExtra("DESCRIPTION", mDescription);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ORDER_PAY", mPayInformation);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast(R.string.you_submit);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };
    
}
