package com.ldnet.activity.mall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Goods;
import com.ldnet.entities.GoodsForGroupPurchase;
import com.ldnet.entities.SubOrders;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.GoodsService;
import com.ldnet.service.OrderService;
import com.ldnet.utility.BottomDialog;
import com.ldnet.utility.DialogGoods;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.tendcloud.tenddata.TCAgent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Alex on 2015/9/28.
 */
public class Goods_Details extends BaseActionBarActivity {

    // 标题
    private TextView tv_page_title;
    // 返回
    private ImageButton btn_back;
    //自定义按钮--分享商品
    private Button btn_custom;
    //浏览器
    private WebView wv_browser;
    //浏览的Web页面Url
    private String mUrl;
    //从哪个Activity跳转过来的
    private String mFromClassName;
    //页面标题
    private String mTitle;
    //商品ID
    private Goods mGoods;
    private GoodsForGroupPurchase groupPurchaseGoods;
    private String mCID;
    private LinearLayout ll_buttons_goods;
    private Button btn_goods_shopping_cart_add;
    private Button btn_goods_buy;
    private Services services;
    private Button mBtnShopStore;
    private List<SubOrders> orderses;
    private ProgressBar mProgressBar;
    private String urlParam;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private String goodsUrl,goodsId;
    private String loadUrl;
    private ImageButton back;
    private GoodsService goodsService;
    private OrderService orderService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置布局
        setContentView(R.layout.activity_mall_goods_details);

        goodsService=new GoodsService(this);
        orderService=new OrderService(this);

        //得到参数
        mFromClassName = getIntent().getStringExtra("FROM_CLASS_NAME");
        mCID = getIntent().getStringExtra("CID");
        mGoods = (Goods) getIntent().getSerializableExtra("GOODS");
        mTitle = getIntent().getStringExtra("PAGE_TITLE");
        mUrl = getIntent().getStringExtra("URL");
        goodsUrl = getIntent().getStringExtra("GOODS_URL");
        goodsId=getIntent().getStringExtra("GOODS_ID");
        User user = UserInformation.getUserInfo();

        if (TextUtils.isEmpty(goodsUrl)) {   //非团购
            if (mGoods.Type.equals(2)) {
                urlParam = "&UID=" + user.UserId + "&UName=" + user.UserName + "&UImgID=" + user.UserThumbnail;    //活动
            } else {
                urlParam = "&IsApp=true";                                                                    //
            }
            loadUrl = mUrl + urlParam;
        } else {         //团购
            urlParam = "&IsApp=true";
            goodsService.getGoodsInfoByGoodsId(goodsId,getGoodsInfoHandler);
            loadUrl = goodsUrl+urlParam;
        }

        //按钮列
        ll_buttons_goods = (LinearLayout) findViewById(R.id.ll_buttons_goods);
        //是否显示购买、添加购物车
        ll_buttons_goods.setVisibility(View.VISIBLE);
        //分享按钮
        btn_custom = (Button) findViewById(R.id.btn_custom);
        btn_custom.setText(R.string.share);
        btn_custom.setVisibility(View.VISIBLE);
        //--------------BUTTON BACK-------------
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        if (!Services.isNotNullOrEmpty(mFromClassName)) {
            btn_back.setVisibility(View.GONE);
        }
        //--------------TITLE-------------
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.goods_details);
        //进度条
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_loading);
        //--------------WEBVIEW-------------
        wv_browser = (WebView) findViewById(R.id.wv_browser);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        initView();
        //进入店铺
        mBtnShopStore = (Button) findViewById(R.id.btn_shop_store);
        // 如果从店铺里进入商品详情不显示进入店铺
        if (mFromClassName.equals(StoreGoods.class.getName()) || mFromClassName.equals(ShopStore.class.getName())) {
            mBtnShopStore.setVisibility(View.GONE);
            btn_custom.setVisibility(View.GONE);
        }
        //加入购物车
        btn_goods_shopping_cart_add = (Button) findViewById(R.id.btn_goods_shopping_cart_add);
        //立即购买
        btn_goods_buy = (Button) findViewById(R.id.btn_goods_buy);
        services = new Services();
        initEvent();
        initEvents();
    }


    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                initView();
            }
        });
    }

    public void initView(){
        //Settings
        WebSettings webSettings = wv_browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setLoadsImagesAutomatically(true);

        //ChromeClient
        WebChromeClient chromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                tv_page_title.setText(title);
                super.onReceivedTitle(view, title);
            }

            // 网页进度条的加载
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mPullToRefreshScrollView.onRefreshComplete();
                } else {
                    if (View.INVISIBLE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        };
        wv_browser.setWebChromeClient(chromeClient);
        //Client
        WebViewClient client = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }
        };
        wv_browser.setWebViewClient(client);
        wv_browser.loadUrl(loadUrl);
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_goods_shopping_cart_add.setOnClickListener(this);
        btn_goods_buy.setOnClickListener(this);
        btn_custom.setOnClickListener(this);
        mBtnShopStore.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "商品详情：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "商品详情：" + this.getClass().getSimpleName());
    }



    // 点击事件处理
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_back://判断是否来自店铺的商品
                    if (mFromClassName.equals(StoreGoods.class.getName()) || mFromClassName.equals(ShopStore.class.getName())
                            || mFromClassName.equals(Order_Details.class.getName())||mFromClassName.equals(MainActivity.class.getName())) {
                        finish();
                    } else{
                        HashMap<String, String> extras = new HashMap<String, String>();
                        if (Services.isNotNullOrEmpty(mCID)) {
                            extras.put("PAGE_TITLE", mTitle);
                            extras.put("CID", mCID);
                        }
                        super.gotoActivityAndFinish(mFromClassName, extras);
                    }
                    break;
                case R.id.btn_shop_store://进入店铺
                    if (mGoods!=null){
                        HashMap<String, String> extrasShopStore = new HashMap<String, String>();
                        extrasShopStore.put("RETAILERID", mGoods.RID);
                        extrasShopStore.put("CID", mCID);
                        gotoActivity(ShopStore.class.getName(), extrasShopStore);
                    }else if (mGoods==null&&groupPurchaseGoods!=null){
                        HashMap<String, String> extrasShopStore = new HashMap<String, String>();
                        extrasShopStore.put("RETAILERID",groupPurchaseGoods.RID);
                        extrasShopStore.put("CID", mCID);
                        gotoActivity(ShopStore.class.getName(), extrasShopStore);
                    }
                    break;
                case R.id.btn_goods_shopping_cart_add://添加购物车
                    showDialog(false);
                    break;
                case R.id.btn_goods_buy://立即购买
                    showDialog(true);
                    break;
                case R.id.btn_custom:
                    if (mGoods!=null&&Services.isNotNullOrEmpty(mGoods.getThumbnail())&&!TextUtils.isEmpty(mUrl)) {
                        BottomDialog dialog = new BottomDialog(this, mUrl, mGoods.GSN, services.getImageUrl(mGoods.getThumbnail()), mGoods.T);
                        dialog.uploadImageUI(this);
                    }else if (TextUtils.isEmpty(mUrl)){
                        BottomDialog dialog = new BottomDialog(this, mGoods.URL, mGoods.GSN, services.getImageUrl(mGoods.getThumbnail()), mGoods.T);
                        dialog.uploadImageUI(this);
                    }else {
                        BottomDialog dialog = new BottomDialog(this, mUrl, mGoods.T);
                        dialog.uploadImageUI(this);
                    }
                    break;
                default:
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    String textView;

    //弹出对话框
    private void showDialog(Boolean isBuy) {
        DialogGoods dialog;
        if (isBuy) {
            textView = "确认";
            if (mGoods!=null){
                dialog = new DialogGoods(this, mGoods, new DirectBuy(), textView);
                dialog.show();
            }

        } else {
            textView = "加入购物车";
            if (mGoods!=null){
                dialog = new DialogGoods(this, mGoods, new ShoppingCart(), textView);
                dialog.show();
            }
        }

    }

    //添加购物车
    class ShoppingCart implements DialogGoods.OnGoodsDialogListener {
        @Override
        public void Confirm(String businessId, String goodsId, String stockId, Integer number) {
            if (mGoods.ST>=1){
                orderService.addPurchaseCar(businessId, goodsId, stockId, number,addPurchaseCarHandler);
            }else{
                showToast(getResources().getString(R.string.mall_goods_not));
            }

        }
    }

    //立即购买
    class DirectBuy implements DialogGoods.OnGoodsDialogListener {
        @Override
        public void Confirm(String businessId, String goodsId, String stockId, Integer number) {
           if (mGoods.ST>=1){
               orderService.orderPreSubmit(businessId, goodsId, stockId, number,orderHandler);
           }else{
               showToast(getResources().getString(R.string.mall_goods_not));
            }
        }
    }


    //提交订单
    Handler orderHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    orderses = (List<SubOrders>) msg.obj;
                    if (orderses != null) {
                        Intent intent = new Intent(Goods_Details.this, Order_Confirm.class);
                        intent.putExtra("SUB_ORDERS", (Serializable) orderses);
                        intent.putExtra("IS_FROM_GOODSDETAILS", "true");
                        intent.putExtra("GOODS", mGoods);
                        intent.putExtra("PAGE_TITLE", mTitle);
                        intent.putExtra("URL", mUrl==null?goodsUrl:mUrl);
                        intent.putExtra("CID", mCID);
                        intent.putExtra("FROM_CLASS_NAME", mFromClassName);
                        startActivity(intent);
                        Goods_Details.this.finish();
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast("获取订单信息失败");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                   showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //加入购物车
    Handler addPurchaseCarHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast(getResources().getString(R.string.mall_goods_shooping_cart_success));
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                   showToast(getResources().getString(R.string.mall_goods_shooping_cart_error));
                    break;
            }
        }
    };


    //获取商品信息
    Handler getGoodsInfoHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    Goods goods=(Goods) msg.obj;
                    goods.setURL(goodsUrl);
                    goods.setType(1);
                    if (mGoods==null){
                        mGoods=goods;
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                        showToast(msg.obj.toString());
                        break;
            }
        }
    };
}
