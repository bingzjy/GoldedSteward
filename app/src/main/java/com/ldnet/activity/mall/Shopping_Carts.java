package com.ldnet.activity.mall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.SD;
import com.ldnet.entities.ShoppingCart;
import com.ldnet.entities.Stock;
import com.ldnet.entities.SubOrders;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.OrderService;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.MyListView;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

import static com.ldnet.goldensteward.R.id.btn_goods_minus;
import static com.ldnet.goldensteward.R.id.btn_goods_plus;
import static com.ldnet.goldensteward.R.id.et_goods_numbers;

/**
 * Created by Alex on 2015/9/28.
 */
public class Shopping_Carts extends BaseActionBarActivity {

    // 标题
    private TextView tv_page_title;
    // 返回
    private ImageButton btn_back;
    //服务
    private Services services;
    private Handler mHandler;
    private Integer mPageIndex = 1;

    //购物车数据
    private List<ShoppingCart> mDatas;
    private ListViewAdapter<ShoppingCart> mAdapter;
    private MyListView lv_shopping_carts;

    private LinearLayout ll_goods_balance;
    //选择的商品数量
    private TextView tv_goods_numbers;
    //选择的商品总价
    private TextView tv_goods_prices;
    //去结算
    private Button btn_goods_balance;
    private TextView mShoppingCartEmpty;
    //上次下拉刷新的时间
    private String dataString;
    private List<SubOrders> orderses;
    private List<ShoppingCart> datas;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private OrderService orderService;
    private ShoppingCart currentShoppingCart;
    private SD currentSD;
    private EditText currentEditTextNumber;
    private Button currentBtnPush,currentBtnMine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置布局
        setContentView(R.layout.activity_mall_shopping_carts);

        orderService=new OrderService(this);

        // 标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.fragment_me_shopping_cart);

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //购物车为空
        mShoppingCartEmpty = (TextView) findViewById(R.id.shopping_cart_empty);

        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        //购物车，列表
        lv_shopping_carts = (MyListView) findViewById(R.id.lv_shopping_carts);
        lv_shopping_carts.setFocusable(false);
        mDatas = new ArrayList<ShoppingCart>();

        ll_goods_balance = (LinearLayout) findViewById(R.id.ll_goods_balance);
        //选择的商品数量
        tv_goods_numbers = (TextView) findViewById(R.id.tv_goods_numbers);
        //选择的商品总价
        tv_goods_prices = (TextView) findViewById(R.id.tv_goods_prices);
        //去结算
        btn_goods_balance = (Button) findViewById(R.id.btn_goods_balance);


        mPageIndex = 1;

        //获取购物车列表
        orderService.getShoppingCar(mPageIndex,handlerGetShoppingList);

        mPageIndex++;

        mAdapter = new ListViewAdapter<ShoppingCart>(Shopping_Carts.this, R.layout.item_shopping_carts, mDatas) {
            @Override
            public void convert(ViewHolder holder, final ShoppingCart shoppingCart) {
                //是否选择
                final CheckBox chk_goods_checked = holder.getView(R.id.chk_goods_checked);
                chk_goods_checked.setChecked(shoppingCart.IsChecked);
                chk_goods_checked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shoppingCart.IsChecked = ((CheckBox) view).isChecked();
                        for (SD s : shoppingCart.SD) {
                            s.IsChecked = shoppingCart.IsChecked;
                        }
                        updateListStatus();
                    }
                });
                //商家名称
                holder.setText(R.id.tv_goods_business, shoppingCart.BN);
                //是否满XX元，免运费
                LinearLayout ll_goods_is_freight = holder.getView(R.id.ll_goods_is_freight);
                if (shoppingCart.ISP && shoppingCart.ISPH) {
                    ll_goods_is_freight.setVisibility(View.VISIBLE);
                    holder.setText(R.id.tv_goods_freight, "购满" + shoppingCart.MPE + "元，享免运费服务");
                } else {
                    ll_goods_is_freight.setVisibility(View.GONE);
                }
                //商品子列表
                ListView lv_shopping_carts_goods = holder.getView(R.id.lv_shopping_carts_goods);
                List<SD> sds = shoppingCart.SD;
                lv_shopping_carts_goods.setAdapter(new ListViewAdapter<SD>(this.mContext, R.layout.item_shopping_carts_item, sds) {
                    @Override
                    public void convert(ViewHolder holder, final SD sd) {
                        //是否选择
                        CheckBox chk_goods_checked = holder.getView(R.id.chk_goods_checked);
                        chk_goods_checked.setChecked(sd.IsChecked);
                        chk_goods_checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                sd.IsChecked = b;
                                shoppingCart.IsChecked = true;
                                for (SD s : shoppingCart.SD) {
                                    if (!s.IsChecked) {
                                        shoppingCart.IsChecked = false;
                                    }
                                }
                                updateListStatus();
                            }
                        });
                        //商家名称
                        holder.setText(R.id.tv_goods_title, sd.GN)
                                .setText(R.id.tv_goods_stock, sd.GGN)
                                .setText(R.id.tv_goods_price, "￥" + sd.GGP)
                                .setText(et_goods_numbers, String.valueOf(sd.N));


                        //删除按钮
                        holder.getView(R.id.btn_goods_delete).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                currentSD=sd;
                                currentShoppingCart=shoppingCart;
                               orderService.deleteShopping(sd.ID,handlerDelete);
                             //   ShoppingCartsDelete(sd.ID, shoppingCart, sd);
                            }
                        });
                        //商品图片
                        ImageView image = holder.getView(R.id.iv_goods_image);
                        if (!TextUtils.isEmpty(sd.GI)) {
                            ImageLoader.getInstance().displayImage(services.getImageUrl(sd.GI), image,imageOptions);
                        } else {
                            image.setImageResource(R.drawable.default_goods);
                        }
                        //
                        final Button btn_goods_minus = holder.getView(R.id.btn_goods_minus);
                        if (sd.N == 1) {
                            btn_goods_minus.setEnabled(false);
                        } else {
                            btn_goods_minus.setEnabled(true);
                        }
                        final Button btn_goods_plus = holder.getView(R.id.btn_goods_plus);
                        final EditText et_goods_numbers = holder.getView(R.id.et_goods_numbers);
                        //减
                        btn_goods_minus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setGoodsNumbers(sd, true, btn_goods_minus, btn_goods_plus, et_goods_numbers);
                                updateListStatus();
                            }
                        });
                        //加
                        btn_goods_plus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setGoodsNumbers(sd, false, btn_goods_minus, btn_goods_plus, et_goods_numbers);
                                updateListStatus();
                            }
                        });
                    }
                });
                //小计
                holder.setText(R.id.tv_items_prices, "￥" + shoppingCart.TotalPrices());
                Utility.setListViewHeightBasedOnChildren(lv_shopping_carts_goods);
            }
        };
        lv_shopping_carts.setAdapter(mAdapter);
        Utility.setListViewHeightBasedOnChildren(lv_shopping_carts);

        initEvent();
        initEvents();
        //初始化服务
        services = new Services();
        mHandler = new Handler();
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_goods_balance.setOnClickListener(this);
    }

    // 点击事件处理
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_back:
                    finish();
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    break;
                case R.id.btn_goods_balance:
                    Iterator<ShoppingCart> iterSC = mDatas.iterator();
                    while (iterSC.hasNext()) {
                        ShoppingCart cart = iterSC.next();
                        Iterator<SD> iterSDs = cart.SD.iterator();
                        while (iterSDs.hasNext()) {
                            SD sd = iterSDs.next();
                            //删除用户未选择的商品项
                            if (!sd.IsChecked) {
                                iterSDs.remove();
                            }
                        }
                        //如果商品为空，这移除此购物车项
                        if (cart.SD.size() == 0) {
                            iterSC.remove();
                        }
                    }
                    //预提交订单
                    orderService.shoppingOrderSubmit(mDatas,handlerSubmit);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //设置商品数量
    private void setGoodsNumbers(SD sd, Boolean isMinus, Button btn_goods_minus, Button btn_goods_plus, EditText et_goods_numbers) {

        currentEditTextNumber=et_goods_numbers;
        currentBtnMine=btn_goods_minus;
        currentBtnPush=btn_goods_plus;
        currentSD=sd;
        //加减操作
        if (isMinus) {
            sd.N--;
            orderService.updateShopping(2,currentSD.ID,1,handlerUpdateShopping);
        } else {
            sd.N++;
            orderService.updateShopping(1,currentSD.ID,1,handlerUpdateShopping);
        }
    }

    //设置列表状态
    private void updateListStatus() {
        mAdapter.notifyDataSetChanged();
        Integer totalNumber = 0;
        BigDecimal totalSum = new BigDecimal("0.00");
        for (ShoppingCart sc : mDatas) {
            totalNumber += sc.TotalNumbers();
            totalSum = totalSum.add(new BigDecimal(sc.TotalPrices().toString()));
        }
        tv_goods_numbers.setText(String.valueOf(totalNumber));
        //选择的商品总价
        tv_goods_prices.setText("￥" + String.valueOf(totalSum.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue()));
        //隐藏或显示结算按钮条
        if (totalNumber.intValue() == 0) {
            ll_goods_balance.setVisibility(View.GONE);
        } else {
            ll_goods_balance.setVisibility(View.VISIBLE);
        }
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                mPageIndex = 1;
             //   ShoppingCarts(mPageIndex);
                orderService.getShoppingCar(mPageIndex,handlerGetShoppingList);
                mPageIndex++;
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                  //  ShoppingCarts(mPageIndex);
                    orderService.getShoppingCar(mPageIndex,handlerGetShoppingList);
                    mPageIndex++;
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    Handler handlerSubmit=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    orderses=(List<SubOrders>)msg.obj;
                    Intent intent = new Intent(Shopping_Carts.this, Order_Confirm.class);
                    intent.putExtra("SUB_ORDERS", (Serializable) orderses);
                    intent.putExtra("FROM_CLASS_NAME", this.getClass().getName());
                    intent.putExtra("IS_FROM_GOODSDETAILS", "false");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    finish();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast("提交失败，请稍后再试");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerStock=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    List<Stock> mStocks=(List<Stock>)msg.obj;

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



    Handler handlerGetShoppingList=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog1();
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mDatas.addAll((List<ShoppingCart>)msg.obj);
                    updateListStatus();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (mDatas != null && mDatas.size() > 0) {
                        showToast("沒有更多数据");
                    } else {
                        mShoppingCartEmpty.setVisibility(View.VISIBLE);
                    }
                    updateListStatus();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    Handler handlerUpdateShopping=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    currentEditTextNumber.setText(String.valueOf(currentSD.N));
                    if (currentSD.N == 1) {
                        currentBtnMine.setEnabled(false);
                    } else {
                          currentBtnPush.setEnabled(true);
                          currentBtnMine.setEnabled(true);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerDelete=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast(R.string.mall_deleted_succeed);
                    currentShoppingCart.SD.remove(currentSD);
                    mDatas.clear();
                    mPageIndex = 1;
                    orderService.getShoppingCar(mPageIndex,handlerGetShoppingList);
                    mPageIndex++;
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(R.string.mall_deleted_failure);
                    break;
            }
        }
    };

}
