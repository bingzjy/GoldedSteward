package com.ldnet.activity.mall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.yellowpage.YellowPages_Map;
import com.ldnet.entities.Goods;
import com.ldnet.entities.RetailerGoodsType;
import com.ldnet.entities.RetailerInfo;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.view.customview.MyGridView;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by zxs on 2016/5/17.
 */
public class ShopStore extends BaseActionBarActivity {
    // 标题
    private TextView tv_page_title;
    // 返回
    private ImageButton btn_back;
    private Services mServices;
    private RetailerInfo mRetailerInfo;
    //店铺lodo
    private ImageView mCircleImageView;
    //店铺名字，地址
    private TextView mTvShopStoreName, mTvShopStoreAddress, mTvShopStoreIntroduction, mTvShopStoreAddressPCA;
    private TextView mTvShopPostage, mTvShopEFromPostag;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    //商家电话
    private Button mShopsTell;
    private String retailerID;
    private List<RetailerGoodsType> mRetailerType;
    private ListView mLvShopStore;
    private ListViewAdapter mAdapter;
    private ListViewAdapter gvAdapter;
    private String mCID;
    private String mUrl;
    private Handler mHandler;
    //地址定位
    private LinearLayout mLlAddressLocation;
    private List<Goods> datas;
    List<Goods> goods = new ArrayList<Goods>();
    private MyGridView gridViewgoods;
    private List<Goods> coll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_store);
        retailerID = getIntent().getStringExtra("RETAILERID");
        mCID = getIntent().getStringExtra("CID");
        mUrl = getIntent().getStringExtra("URL");
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //--------------TITLE-------------
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText("金牌店铺");
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        //  店铺信息
        mCircleImageView = (ImageView) findViewById(R.id.ibtn_shop_store_logo);
        //设置默认logo
        mCircleImageView.setImageResource(R.drawable.me_thumbnail_n);
        mTvShopStoreAddress = (TextView) findViewById(R.id.shop_store_address);
        mTvShopStoreName = (TextView) findViewById(R.id.shop_store_name);
        mTvShopStoreIntroduction = (TextView) findViewById(R.id.shop_store_introduction);
        mTvShopStoreAddressPCA = (TextView) findViewById(R.id.shop_store_address_p_c_a);
        mShopsTell = (Button) findViewById(R.id.shop_store_tel);
        mTvShopPostage = (TextView) findViewById(R.id.shop_store_postage);
        mTvShopEFromPostag = (TextView) findViewById(R.id.shop_store_exemption_from_postage);
        mLlAddressLocation = (LinearLayout) findViewById(R.id.ll_address_location);
        mServices = new Services();
        mHandler = new Handler();
        //店铺分类列表
        mLvShopStore = (ListView) findViewById(R.id.lv_shop_store_sort);
        mLvShopStore.setFocusable(false);
        mRetailerType = new ArrayList<RetailerGoodsType>();
        mRetailerType = mServices.getGoodsTypes(retailerID);
        getRetailerInfo(retailerID);
        mAdapter = new ListViewAdapter<RetailerGoodsType>(this, R.layout.item_shop_store_sort, mRetailerType) {
            @Override
            public void convert(ViewHolder holder, final RetailerGoodsType goodsType) {
                final Button goodsSort = holder.getView(R.id.tv_goods_sort);
                LinearLayout llGoodsSort = holder.getView(R.id.ll_goods_sort);
                //分类下无商品不显示分类
                if (goodsType.IsYGoods.equals("false")) {
                    llGoodsSort.setVisibility(View.GONE);
                }
                goodsSort.setText(goodsType.Title);
                goodsSort.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            HashMap<String, String> extras = new HashMap<String, String>();
                            extras.put("SORT_ID", goodsType.Id);
                            extras.put("RETAILERID", retailerID);
                            extras.put("PAGE_TITLE", goodsType.Title);
                            extras.put("CID", mCID);
                            gotoActivity(StoreGoods.class.getName(), extras);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // 分类下商品-------------------------
                MyGridView gridViewgoods = holder.getView(R.id.gv_goods);
                gridViewgoods.setSelector(R.color.white);

                final List<Goods> goods = new ArrayList<Goods>();
                ListViewAdapter gvAdapter = new ListViewAdapter<Goods>(ShopStore.this, R.layout.item_home_goods, goods) {
                    @Override
                    public void convert(ViewHolder holder, Goods goods1) {
                        // 商品图片
                        // 改线ViewPager的高度
                        ImageView thumbnail = holder.getView(R.id.iv_goods_image);
                        //设置商品图片的高度
                        LinearLayout.LayoutParams linearParams_good = (LinearLayout.LayoutParams) thumbnail.getLayoutParams();
                        int height = (Utility.getScreenHeightforDIP(ShopStore.this) - Utility.dip2px(ShopStore.this, 8.0f)) / 2;
                        int width = (Utility.getScreenHeightforDIP(ShopStore.this) - Utility.dip2px(ShopStore.this, 8.0f)) / 2;
                        linearParams_good.height = height;
                        linearParams_good.width = width;
                        thumbnail.setLayoutParams(linearParams_good);
                        linearParams_good.gravity = Gravity.CENTER;
                        if (!TextUtils.isEmpty(goods1.getThumbnail())) {
                            ImageLoader.getInstance().displayImage(mServices.getImageUrl(goods1.getThumbnail()), thumbnail, imageOptions);
                        } else {
                            thumbnail.setImageResource(R.drawable.default_goods);
                        }
                        // 商品标题
                        ((TextView) holder.getView(R.id.tv_goods_name)).setText(goods1.T.trim());

                        // 商品价格
                        TextView tv_goods_price = holder.getView(R.id.tv_goods_price);
                        if (goods1.Type.equals(2)) {
                            //tv_goods_price.setVisibility(View.GONE);
                            tv_goods_price.setText("报名" + " " + goods1.GP);
                            tv_goods_price.setTextColor(getResources().getColor(R.color.gray));
                        } else {
                            tv_goods_price.setVisibility(View.VISIBLE);
                            tv_goods_price.setText("￥" + goods1.GP);
                        }
                    }
                };
                gridViewgoods.setAdapter(gvAdapter);
                // 分类下商品-------------------------
                gridViewgoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ShopStore.this, Goods_Details.class);
                        intent.putExtra("GOODS", goods.get(position));
                        intent.putExtra("PAGE_TITLE", goods.get(position).T);
                        intent.putExtra("FROM_CLASS_NAME", ShopStore.class.getName());
                        intent.putExtra("URL", goods.get(position).URL);
                        intent.putExtra("CID", goods.get(position).GID);
                        startActivity(intent);
                    }
                });

                // 加载店铺首页商品
//                if (goodsType.IsYGoods.equals("true")) {
                List<Goods> datas;
                if (goods.size() > 0) {
                    datas = mServices.getGoodsList(goods.get(goods.size() - 1).GID, 2, retailerID, goodsType.Id);
                } else {
                    datas = mServices.getGoodsList("", 2, retailerID, goodsType.Id);
                }
                if (datas != null) {
                    goods.addAll(datas);
                }
            }
        };
        mLvShopStore.setAdapter(mAdapter);
        setListViewHeight(mLvShopStore);
        initEvent();
        initEvents();
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mRetailerType = mServices.getGoodsTypes(retailerID);
                getRetailerInfo(retailerID);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "进入店铺：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "进入店铺：" + this.getClass().getSimpleName());
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
        mShopsTell.setOnClickListener(this);
        mLlAddressLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.shop_store_tel://联系电话
                if (!TextUtils.isEmpty(mRetailerInfo.P)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mRetailerInfo.P));
                    this.startActivity(intent);
                } else {
                    showToast("商家未提供联系方式！");
                }

                break;
            case R.id.ll_address_location://定位地址
                if (!TextUtils.isEmpty(mRetailerInfo.LX) && !TextUtils.isEmpty(mRetailerInfo.LY)) {
                    try {
                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put("LATITUDE", mRetailerInfo.LX);
                        extras.put("LONGITUDE", mRetailerInfo.LY);
                        gotoActivity(YellowPages_Map.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    showToast(R.string.position_on);
                }
                break;
            default:
                break;
        }

    }

    /**
     * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     *
     * @param listView
     */
    public void setListViewHeight(ListView listView) {

        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //GET BGoods/App_GetGoodsTypes?RetailerID={RetailerID}
    // 根据商家ID获取商品分类
    public void getGoodsTypes(final String retailerID) {
        // 请求的URL
        String url = Services.mHost + "BGoods/App_GetGoodsTypes?RetailerID=%s";
        url = String.format(url, retailerID);
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
                                 super.onResponse(s, i);
                                 Log.d("asdsdasd", "----" + s);
                                 try {
                                     JSONObject json = new JSONObject(s);
                                     JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                     if (json.getBoolean("Status")) {
                                         if (jsonObject.getBoolean("Valid")) {
                                             Gson gson = new Gson();
                                             Type type = new TypeToken<List<RetailerGoodsType>>() {
                                             }.getType();
                                             mRetailerType = gson.fromJson(jsonObject.getString("Obj"), type);
                                         }
                                     }
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                             }
                         }

                );
    }

    // 根据商家分类ID 获取商品列表
//    public void getGoodsList(String lastId, Integer pageSize, String retailerId, String typeId) {
//        Log.d("sadasda23123", lastId + "--" + pageSize + "--" + retailerId + "--" + typeId);
//        // 请求的URL
//        String url = Services.mHost + "BGoods/App_GetGoodsList_ByTypeID?LastID=%s&PageCnt=%s&RetailerID=%s&TypeID=%s";
//        url = String.format(url, lastId, pageSize, retailerId, typeId);
//        OkHttpUtils.get().url(url)
//                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
//                .execute(new DataCallBack(this) {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        super.onError(call, e, i);
//                    }
//
//                    @Override
//                    public void onResponse(String s, int i) {
//                        super.onResponse(s, i);
//                        Log.d("asdsdasd", "====" + s);
//                        try {
//                            JSONObject json = new JSONObject(s);
//                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
//                            if (json.getBoolean("Status")) {
//                                if (jsonObject.getBoolean("Valid")) {
//                                    Gson gson = new Gson();
//                                    Type type = new TypeToken<List<Goods>>() {
//                                    }.getType();
//                                    datas = gson.fromJson(jsonObject.getString("Obj"), type);
//                                    if (datas != null) {
//                                        goods.addAll(datas);
//                                        mAdapter = new ListViewAdapter<RetailerGoodsType>(ShopStore.this, R.layout.item_shop_store_sort, mRetailerType) {
//                                            @Override
//                                            public void convert(ViewHolder holder, final RetailerGoodsType goodsType) {
//                                                // 加载店铺首页商品
//
//                                                final Button goodsSort = holder.getView(R.id.tv_goods_sort);
//                                                LinearLayout llGoodsSort = holder.getView(R.id.ll_goods_sort);
//                                                //分类下无商品不显示分类
//                                                if (goodsType.IsYGoods.equals("false")) {
//                                                    llGoodsSort.setVisibility(View.GONE);
//                                                }
//                                                goodsSort.setText(goodsType.Title);
//                                                goodsSort.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        try {
//                                                            HashMap<String, String> extras = new HashMap<String, String>();
//                                                            extras.put("SORT_ID", goodsType.Id);
//                                                            extras.put("RETAILERID", retailerID);
//                                                            extras.put("PAGE_TITLE", goodsType.Title);
//                                                            extras.put("CID", mCID);
////                            extras.put("URL", mUrl);
//                                                            gotoActivity(StoreGoods.class.getName(), extras);
//                                                        } catch (ClassNotFoundException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                    }
//                                                });
//                                                // 分类下商品-------------------------
//                                                gridViewgoods = holder.getView(R.id.gv_goods);
//                                                gridViewgoods.setSelector(R.color.white);
//
//                                                final List<Goods> goods = new ArrayList<Goods>();
//                                                ListViewAdapter gvAdapter = new ListViewAdapter<Goods>(ShopStore.this, R.layout.item_home_goods, goods) {
//                                                    @Override
//                                                    public void convert(ViewHolder holder, Goods goods1) {
//                                                        // 商品图片
//                                                        // 改线ViewPager的高度
//                                                        ImageView thumbnail = holder.getView(R.id.iv_goods_image);
//                                                        //设置商品图片的高度
//                                                        LinearLayout.LayoutParams linearParams_good = (LinearLayout.LayoutParams) thumbnail.getLayoutParams();
//                                                        int height = (Utility.getScreenWidthforPX(ShopStore.this) - Utility.dip2px(ShopStore.this, 8.0f)) / 2;
//                                                        linearParams_good.height = height;
//                                                        thumbnail.setLayoutParams(linearParams_good);
//                                                        if (!TextUtils.isEmpty(goods1.getThumbnail())) {
//                                                            ImageLoader.getInstance().displayImage(mServices.getImageUrl(goods1.getThumbnail()), thumbnail, GSApplication.getInstance().imageOptions);
//                                                        } else {
//                                                            thumbnail.setImageResource(R.drawable.default_goods);
//                                                        }
//                                                        // 商品标题
//                                                        ((TextView) holder.getView(R.id.tv_goods_name)).setText(goods1.T.trim());
//
//                                                        // 商品价格
//                                                        TextView tv_goods_price = holder.getView(R.id.tv_goods_price);
//                                                        if (goods1.Type.equals(2)) {
//                                                            //tv_goods_price.setVisibility(View.GONE);
//                                                            tv_goods_price.setText("报名" + " " + goods1.GP);
//                                                            tv_goods_price.setTextColor(getResources().getColor(R.color.gray));
//                                                        } else {
//                                                            tv_goods_price.setVisibility(View.VISIBLE);
//                                                            tv_goods_price.setText("￥" + goods1.GP);
//                                                        }
//                                                    }
//                                                };
//                                                gridViewgoods.setAdapter(gvAdapter);
//                                                // 分类下商品-------------------------
//                                                gridViewgoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                                    @Override
//                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                                        Intent intent = new Intent(ShopStore.this, Goods_Details.class);
//                                                        intent.putExtra("GOODS", goods.get(position));
//                                                        intent.putExtra("PAGE_TITLE", goods.get(position).T);
//                                                        intent.putExtra("FROM_CLASS_NAME", ShopStore.class.getName());
//                                                        intent.putExtra("URL", goods.get(position).URL);
//                                                        intent.putExtra("CID", goods.get(position).GID);
//                                                        startActivity(intent);
//                                                    }
//                                                });
//                                                getGoodsList("", 2, goodsType.RetailerId, goodsType.Id);
//                                            }
//
//                                        };
//                                        setListViewHeight(mLvShopStore);
//                                        mLvShopStore.setAdapter(mAdapter);
//
//                                    }
//
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }

    //根据商家ID获取商家信息
    // GET BRetailer/App_GetRetailerInfo_ByID?RetailerID={RetailerID}
    public void getRetailerInfo(String retailerID) {
        // 请求的URL
        String url = Services.mHost + "BRetailer/App_GetRetailerInfo_ByID?RetailerID=%s";
        url = String.format(url, retailerID);
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
                        mPullToRefreshScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd", "-=-=-" + s);
                        mPullToRefreshScrollView.onRefreshComplete();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    mRetailerInfo = gson.fromJson(jsonObject.getString("Obj"), RetailerInfo.class);
                                    if (mRetailerInfo != null) {
                                        if (!TextUtils.isEmpty(mRetailerInfo.LS)) {
                                            ImageLoader.getInstance().displayImage(mServices.getImageUrl(mRetailerInfo.LS), mCircleImageView, imageOptions);
                                        }
                                        if (mRetailerInfo.ISCT.equals(true)) {
                                            mTvShopPostage.setText("运费：￥" + mRetailerInfo.TM);
                                        } else {
                                            mTvShopPostage.setText("运费：￥0");
                                            mTvShopEFromPostag.setVisibility(View.GONE);
                                        }
                                        if (mRetailerInfo.ISAD.equals(true)) {
                                            mTvShopEFromPostag.setText("满" + mRetailerInfo.FM + "元包邮");
                                        } else {
                                            mTvShopEFromPostag.setVisibility(View.GONE);
                                        }

                                        mTvShopStoreAddress.setText(mRetailerInfo.A);
                                        mTvShopStoreName.setText(mRetailerInfo.N);
                                        mTvShopStoreIntroduction.setText("    " + mRetailerInfo.ABS);
                                        if (mRetailerInfo.PN == null) {
                                            mRetailerInfo.PN = "";
                                        }
                                        if (mRetailerInfo.CN == null) {
                                            mRetailerInfo.CN = "";
                                        }
                                        if (mRetailerInfo.AN == null) {
                                            mRetailerInfo.AN = "";
                                        }
                                        mTvShopStoreAddressPCA.setText(mRetailerInfo.PN + mRetailerInfo.CN + mRetailerInfo.AN);
                                        //  mShopsTell.setText("电话： " + mRetailerInfo.P);
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
