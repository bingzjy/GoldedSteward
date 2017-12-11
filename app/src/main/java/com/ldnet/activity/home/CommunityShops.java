package com.ldnet.activity.home;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.mall.Shopping_Carts;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zxs on 2016/1/19.
 */
public class CommunityShops extends BaseActionBarActivity {


    // 标题
    private TextView mTvPageTitle, mTvShare;
    // 返回
    private ImageButton mBtnBack;
    private Services mService;
    private TextView mShopNotification;
    // 小店商品id
    private CommunityShopId mShopId;

    //  商品分类
    private ListView mCommunityShopLv;
    private List<CommunityGoodsType> mGoodType;
    private ListViewAdapter<CommunityGoodsType> mLvAdapter;
    private String mCurrentTypeId;

    // 具体商品
    private GridView mCommunityShopGv;
    private List<Goods> mGoods;
    private ListViewAdapter<Goods> mGvAdapter;
    private List<Goods> goodslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_shops);
        mService = new Services();
        //接受来自FragmentHome 小店的对象
        mShopId = (CommunityShopId) getIntent().getSerializableExtra("communityShopId");

        //--------------BUTTON BACK-------------
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        //--------------TITLE-------------
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        mTvPageTitle.setText("社区小店");
        //--------------SHARE-------------
        mTvShare = (TextView) findViewById(R.id.tv_share);
        mTvShare.setText("购物车");
        mTvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityShops.this, Shopping_Carts.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        //通知配送时间
        mShopNotification = (TextView) findViewById(R.id.community_shop_text);
        mShopNotification.setText(mShopId.Remarks);

        mShopNotification.setOnClickListener(new View.OnClickListener() {
            Boolean flag = true;

            @Override
            public void onClick(View v) {
                if (flag) {
                    flag = false;
                    mShopNotification.setEllipsize(null); // 展开
                    mShopNotification.setSingleLine(false);
                } else {
                    flag = true;
                    mShopNotification.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                    mShopNotification.setSingleLine(true);
                }

            }
        });
        //商品分类
        mCommunityShopLv = (ListView) findViewById(R.id.community_shop_name_lv);
        mGoodType = new ArrayList<CommunityGoodsType>();
        getGoodsType(mShopId.RID);
        initEvent();
    }

    public void initEvent() {
        mBtnBack.setOnClickListener(this);
    }

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
        }
    }

    //更新商品信息
    private void updateGoods(Boolean isFirst) {
        if (isFirst) {
            mGoods.clear();
            GetGoodsList("", mShopId.RID, mCurrentTypeId);
        } else {
            GetGoodsList(mGoods.get(mGoods.size() - 1).GID, mShopId.RID, mCurrentTypeId);
        }
        showProgressDialog1();
    }

    //弹出对话框
    private void showDialog(Goods goods) {
        DialogGoods dialog;
        String title = "加入购物车";
        dialog = new DialogGoods(this, goods, new ShoppingCart(), title);
        dialog.show();
    }

    class ShoppingCart implements DialogGoods.OnGoodsDialogListener {
        @Override
        public void Confirm(String businessId, String goodsId, String stockId, Integer number) {
            if (mGoods.get(mGoods.size() - 1).ST == 0) {
                showToast(getResources().getString(R.string.mall_goods_not));
            } else {
                ShoppingCartsAdd(businessId, goodsId, stockId, number);
            }

        }
    }

    // 获取周边小店商品类型
    public void getGoodsType(String RID) {
        String url = Services.mHost + "GoodsShop/GetGoodsTypeList?RID=%s";
        url = String.format(url, RID);
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
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.d("asdsdasd", CookieInformation.getUserInfo().getCookieinfo() + "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<CommunityGoodsType>>() {
                                    }.getType();
                                    mGoodType = gson.fromJson(jsonObject.getString("Obj"), type);
                                    mLvAdapter = new ListViewAdapter<CommunityGoodsType>(CommunityShops.this, R.layout.item_community_shop_lv, mGoodType) {
                                        @Override
                                        public void convert(ViewHolder holder, CommunityGoodsType communityGoodsType) {
                                            holder.setText(R.id.text_community_shop_sort, communityGoodsType.Title);
                                        }
                                    };
                                    mCommunityShopLv.setAdapter(mLvAdapter);

                                    //默认选中第一条
                                    mCommunityShopLv.setSelection(0);

                                    mCommunityShopLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            mCurrentTypeId = mGoodType.get(position).Id;
                                            //加载该分类下的商品列表
                                            mCommunityShopLv.setClickable(false);
                                            updateGoods(true);
                                        }
                                    });

                                    //商品列表
                                    mCommunityShopGv = (GridView) findViewById(R.id.community_shop_details_gv);
                                    mGoods = new ArrayList<Goods>();
                                    mGvAdapter = new ListViewAdapter<Goods>(CommunityShops.this, R.layout.item_community_shop_details, mGoods) {
                                        @Override
                                        public void convert(ViewHolder holder, Goods goods) {
                                            LinearLayout linearLayoutshop = holder.getView(R.id.ll_shopping);
                                            //--绑定商品信息
                                            ImageView imageView = holder.getView(R.id.tv_community_goods_image);
                                            if (!TextUtils.isEmpty(goods.getThumbnail())) {
                                                ImageLoader.getInstance().displayImage(mService.getImageUrl(goods.getThumbnail()), imageView, imageOptions);
                                            } else {
                                                imageView.setImageResource(R.drawable.default_goods);
                                            }
                                            holder.setText(R.id.tv_community_goods_name, goods.T);
                                            holder.setText(R.id.tv_community_goods_price, "￥" + goods.GP);
                                        }
                                    };
                                    mCommunityShopGv.setAdapter(mGvAdapter);
                                    // 去掉gridview背景色
                                    mCommunityShopGv.setSelector(R.color.white);
                                    if (mShopId.Status.equals(false)) {
                                        mShopNotification.setText("暂停营业");
                                        mCommunityShopGv.setBackgroundColor(getResources().getColor(R.color.gray));
                                    }
                                    mCommunityShopGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            //  --弹出对话框加入购物车
                                            if (mShopId.Status.equals(true)) {
                                                if (mGoods != null) {
                                                    if (mGoods.get(position).ST > 0) {
                                                        showDialog(mGoods.get(position));
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    //拉去第一个分类的商品数据
                                    mCurrentTypeId = mGoodType.get(0).Id;
                                    //更新商品
                                    updateGoods(true);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //    周边小店商品
    public void GetGoodsList(String LastID, String RID, String TypeID) {
        String url = Services.mHost + "GoodsShop/GetGoodsList?LastID=%s&PageCnt=%s&RID=%s&TypeID=%s";
        url = String.format(url, LastID, 10000, RID, TypeID);
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
                        closeProgressDialog1();
                        mCommunityShopLv.setClickable(true);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.d("asdsdasd1-", "111111111" + s);
                        closeProgressDialog1();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Goods>>() {
                                    }.getType();
                                    goodslist = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (goodslist != null) {
                                        mGoods.addAll(goodslist);
                                    }
                                    mGvAdapter.notifyDataSetChanged();
                                    mCommunityShopLv.setClickable(true);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //添加到购物车
    public void ShoppingCartsAdd(String bid, String gid, String ggid, Integer n) {
        try {
            //JSON对象
            JSONObject object = new JSONObject();
            object.put("BID", bid);
            object.put("RID", UserInformation.getUserInfo().UserId);
            object.put("GID", gid);
            object.put("GGID", ggid);
            object.put("N", n);
            object.put("GI", "");

            // 请求的URL
            String url = Services.mHost + "BShoppingCart/APP_InsertShopping";
            HashMap<String, String> extras = new HashMap<>();
            extras.put("str", object.toString());
            Services.json(extras);
            String aa = Services.timeFormat();
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String aa2 = url;
            String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + Services.json(extras) + Services.TOKEN;
            OkHttpUtils.post().url(url)
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32(md5))
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addParams("str", object.toString())
                    .build()
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
                                        showToast(getResources().getString(R.string.mall_goods_shooping_cart_success));
                                    } else {
                                        showToast(getResources().getString(R.string.mall_goods_shooping_cart_error));
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
