package com.ldnet.activity.mall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.SD;
import com.ldnet.entities.ShoppingCart;
import com.ldnet.entities.SubOrders;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.OrderService;
import com.ldnet.utility.CenterImage;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.MyListView;
import com.ldnet.utility.Services;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static com.ldnet.goldensteward.R.id.et_goods_numbers;

/**
 * Created by Alex on 2015/9/28.
 */
public class Shopping_Carts extends BaseActionBarActivity {    // 标题
    private TextView tv_page_title, tv_edit;
    // 返回
    private ImageView btn_back;
    //服务
    private Services services;
    private Integer mPageIndex = 1;
    //购物车数据
    private List<ShoppingCart> mDatas = new ArrayList<ShoppingCart>();
    private ListViewAdapter<ShoppingCart> mAdapter;
    private MyListView lv_shopping_carts;
    private LinearLayout ll_goods_balance;
    //选择的商品总价
    private TextView tv_goods_prices;
    //去结算
    private Button btn_goods_balance;
    private TextView mShoppingCartEmpty;
    //上次下拉刷新的时间
    private List<SubOrders> orderses;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    //全选
    private CheckBox checkBox;
    private OrderService orderService;
    private ShoppingCart currentShoppingCart;
    private SD currentSD;
    private EditText currentEditTextNumber;
    private Button currentBtnPush, currentBtnMine;
    private boolean edtitType;//初始状态为结算
    private List<String> checkIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置布局
        setContentView(R.layout.activity_mall_shopping_carts);
        //初始化服务
        services = new Services();
        orderService = new OrderService(this);

        //获取购物车列表
        mPageIndex = 1;
        orderService.getShoppingCar(mPageIndex, handlerGetShoppingList);
        showProgressDialog();
        mPageIndex++;

        initView();
        initEvent();
    }

    private void initView() {
        // 标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.fragment_me_shopping_cart);
        tv_edit = (TextView) findViewById(R.id.tv_custom);
        tv_edit.setVisibility(View.VISIBLE);
        tv_edit.setText("编辑");
        btn_back = (ImageView) findViewById(R.id.btn_back);
        //购物车为空
        mShoppingCartEmpty = (TextView) findViewById(R.id.shopping_cart_empty);

        checkBox = (CheckBox) findViewById(R.id.cb_all_check);

        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        //购物车，列表
        lv_shopping_carts = (MyListView) findViewById(R.id.lv_shopping_carts);
        lv_shopping_carts.setFocusable(false);

        ll_goods_balance = (LinearLayout) findViewById(R.id.ll_goods_balance);
        //选择的商品总价
        tv_goods_prices = (TextView) findViewById(R.id.tv_goods_prices);
        //去结算
        btn_goods_balance = (Button) findViewById(R.id.btn_goods_balance);

        mAdapter = new ListViewAdapter<ShoppingCart>(Shopping_Carts.this, R.layout.item_shopping_carts, mDatas) {
            @Override
            public void convert(ViewHolder holder, final ShoppingCart shoppingCart) {
                //是否选择
                final CheckBox chk_goods_checked = holder.getView(R.id.chk_goods_checked);
                chk_goods_checked.setChecked(shoppingCart.IsChecked);
                chk_goods_checked.setOnClickListener(new View.OnClickListener() { //商家选中，其子商品全选中
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
                lv_shopping_carts_goods.setAdapter(new ListViewAdapter<SD>(this.mContext, R.layout.item_shopping_carts_item2, sds) {
                    @Override
                    public void convert(ViewHolder holder, final SD sd) {

                        LinearLayout llBack = (LinearLayout) holder.getView(R.id.ll_item_goods);

                        //商品图片
                        CenterImage image = holder.getView(R.id.iv_goods_image);
                        if (!TextUtils.isEmpty(sd.GI)) {
                            ImageLoader.getInstance().displayImage(services.getImageUrl(sd.GI), image, imageOptions);
                        } else {
                            image.setImageResource(R.drawable.default_goods);
                        }


                        TextView tvGoodsTitle = holder.getView(R.id.tv_goods_title);

                        if (edtitType) {  //删除编辑状态
                            if (sd.isOutStore()) {    //判断是否无货
                                tvGoodsTitle.setTextColor(Color.parseColor("#9B9B9B"));
                                llBack.setBackgroundResource(R.color.gray_text_light7);
                                image.setCenterImgShow(1);
                            } else if (sd.isSoldOut()) {   //判断是否已下架
                                tvGoodsTitle.setTextColor(Color.parseColor("#9B9B9B"));
                                llBack.setBackgroundResource(R.color.gray_text_light7);
                                image.setCenterImgShow(2);
                            } else {
                                tvGoodsTitle.setTextColor(Color.parseColor("#4A4A4A"));
                                llBack.setBackgroundResource(R.color.white);
                                image.setCenterImgShow(0);
                            }
                        } else {    //结算状态
                            if (sd.isOutStore()) {    //判断是否无货
                                tvGoodsTitle.setTextColor(Color.parseColor("#9B9B9B"));
                                llBack.setBackgroundResource(R.color.gray_text_light7);
                                image.setCenterImgShow(1);
                                sd.setIsChecked(false);
                            } else if (sd.isSoldOut()) {   //判断是否已下架
                                tvGoodsTitle.setTextColor(Color.parseColor("#9B9B9B"));
                                llBack.setBackgroundResource(R.color.gray_text_light7);
                                image.setCenterImgShow(2);
                                sd.setIsChecked(false);
                            } else {
                                tvGoodsTitle.setTextColor(Color.parseColor("#4A4A4A"));
                                llBack.setBackgroundResource(R.color.white);
                                image.setCenterImgShow(0);
                                sd.setIsChecked(sd.IsChecked);
                            }
                        }


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
    }


    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_goods_balance.setOnClickListener(this);
        tv_edit.setOnClickListener(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setAllCheck(true);
                } else {
                    setAllCheck(false);
                }
            }
        });

        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                mPageIndex = 1;
                orderService.getShoppingCar(mPageIndex, handlerGetShoppingList);
                mPageIndex++;
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    orderService.getShoppingCar(mPageIndex, handlerGetShoppingList);
                    mPageIndex++;
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    // 点击事件处理
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_back:
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                    break;
                case R.id.btn_goods_balance:

                    if (!edtitType) {  //结算

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

                        if (mDatas == null || mDatas.size() == 0) {
                            showToast(getString(R.string.check_valid_goods));
                        } else {
                            //预提交订单
                            orderService.shoppingOrderSubmit(mDatas, handlerSubmit);
                        }

                    } else {     //删除

                        if (getCheckSDId() != null && getCheckSDId().size() > 0) {
                            showProgressDialog();
                            orderService.deleteShopping(Utility.ListToString(getCheckSDId()), handlerDelete);
                        }

                    }
                    break;
                case R.id.tv_custom:
                    //清空选中，重新选择
                    setAllCheck(false);
                    checkBox.setChecked(false);
                    if (!edtitType) { //编辑
                        btn_goods_balance.setText("删除");
                        btn_goods_balance.setTextColor(Color.WHITE);
                        btn_goods_balance.setBackgroundColor(Color.parseColor("#FF0C2A"));

                        tv_goods_prices.setVisibility(View.GONE);
                        tv_edit.setText("完成");
                        edtitType = true;
                    } else {
                        btn_goods_balance.setText("结算");
                        btn_goods_balance.setTextColor(Color.WHITE);
                        btn_goods_balance.setBackgroundColor(Color.parseColor("#25B59E"));

                        tv_goods_prices.setVisibility(View.VISIBLE);
                        tv_edit.setText("编辑");
                        edtitType = false;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //清空选中状态
    private void setAllCheck(boolean setCheckState) {
        if (mDatas != null && mDatas.size() > 0) {
            for (ShoppingCart shoppingCart : mDatas) {
                shoppingCart.setIsChecked(setCheckState);
                if (shoppingCart.getSD() != null && shoppingCart.getSD().size() > 0) {
                    List<SD> list = shoppingCart.getSD();
                    for (SD sd : list) {
                        sd.setIsChecked(setCheckState);
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    //批量删除
    private List<String> getCheckSDId() {
        checkIdList.clear();
        if (mDatas != null && mDatas.size() > 0) {
            for (ShoppingCart shoppingCart : mDatas) {
                if (shoppingCart.getSD() != null && shoppingCart.getSD().size() > 0) {
                    List<SD> list = shoppingCart.getSD();
                    for (SD sd : list) {
                        if (sd.IsChecked) {
                            checkIdList.add(sd.ID);
                        }
                    }
                }
            }
        } else {
            return null;
        }
        return checkIdList;
    }


    //设置商品数量
    private void setGoodsNumbers(SD sd, Boolean isMinus, Button btn_goods_minus, Button btn_goods_plus, EditText et_goods_numbers) {

        currentEditTextNumber = et_goods_numbers;
        currentBtnMine = btn_goods_minus;
        currentBtnPush = btn_goods_plus;
        currentSD = sd;
        //加减操作
        if (isMinus) {
            sd.N--;
            orderService.updateShopping(2, currentSD.ID, 1, handlerUpdateShopping);
        } else {
            sd.N++;
            orderService.updateShopping(1, currentSD.ID, 1, handlerUpdateShopping);
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
        //选择的商品总价
        tv_goods_prices.setText("总计：¥ " + String.valueOf(totalSum.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue()));
        //隐藏或显示结算按钮条
        if (totalNumber.intValue() == 0) {
            ll_goods_balance.setVisibility(View.GONE);
        } else {
            ll_goods_balance.setVisibility(View.VISIBLE);
        }
    }


    Handler handlerSubmit = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    orderses = (List<SubOrders>) msg.obj;
                    Intent intent = new Intent(Shopping_Carts.this, Order_Confirm.class);
                    intent.putExtra("SUB_ORDERS", (Serializable) orderses);
                    intent.putExtra("FROM_CLASS_NAME", this.getClass().getName());
                    intent.putExtra("IS_FROM_GOODSDETAILS", "false");
                    startActivity(intent);
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

    //获取购物车商品列表
    Handler handlerGetShoppingList = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    mDatas.addAll((List<ShoppingCart>) msg.obj);
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

    //更新购物车商品数据
    Handler handlerUpdateShopping = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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

    //删除购物车某商品
    Handler handlerDelete = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast(R.string.mall_deleted_succeed);
                    //       currentShoppingCart.SD.remove(currentSD);
                    mDatas.clear();
                    mPageIndex = 1;
                    orderService.getShoppingCar(mPageIndex, handlerGetShoppingList);
                    mPageIndex++;
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(R.string.mall_deleted_failure);
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                gotoActivityAndFinish(MainActivity.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
