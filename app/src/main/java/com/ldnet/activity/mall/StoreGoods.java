package com.ldnet.activity.mall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Goods;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.FooterLayout;
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
import java.util.List;

import okhttp3.Call;

/**
 * Created by zxs on 2016/5/17.
 * 店铺中商品分类列表
 */
public class StoreGoods extends BaseActionBarActivity {
    // 标题
    private TextView tv_page_title,tv_goods;
    // 返回
    private ImageButton btn_back;
    //自定义按钮--分享商品
    private Button btn_custom;
    //分类id
    private String mSortId;
    //商家id
    private String mRetailerId;
    //页面标题
    private String mTitle;
    private Services services;
    private ListView mGoodListGv;
    private ListView lvGoodList;
    private ListViewAdapter mAdapter;
    private Handler mHandler;
    private List<Goods> goodList;
    //上次下拉刷新的时间
    private String dataString;
    private String mCID;
    private String mUrl;
    private List<Goods> datas;
    private PullToRefreshScrollView mPullToRefreshScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_goods);
        services = new Services();
        mHandler = new Handler();
        //得到参数
        mSortId = getIntent().getStringExtra("SORT_ID");
        mTitle = getIntent().getStringExtra("PAGE_TITLE");
        mRetailerId = getIntent().getStringExtra("RETAILERID");
        mCID = getIntent().getStringExtra("CID");

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_goods = (TextView) findViewById(R.id.tv_goods);
        tv_page_title.setText(mTitle);
        goodList = new ArrayList<Goods>();
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        lvGoodList = (ListView) findViewById(R.id.xlv_shop_store_goods);
        lvGoodList.setFocusable(false);
        goodList.clear();
        getGoodsList("", 10, mRetailerId, mSortId);
        lvGoodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StoreGoods.this, Goods_Details.class);
                intent.putExtra("GOODS", goodList.get(position));
                intent.putExtra("URL", goodList.get(position).URL);
                intent.putExtra("CID", mCID);
                intent.putExtra("PAGE_TITLE", mTitle);
                intent.putExtra("FROM_CLASS_NAME", StoreGoods.class.getName());
                startActivity(intent);
            }
        });
        initEvent();
        initEvents();
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "店铺商品：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "店铺商品：" + this.getClass().getSimpleName());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                goodList.clear();
                getGoodsList("", 10, mRetailerId, mSortId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (goodList != null && goodList.size() > 0) {
                    getGoodsList(goodList.get(goodList.size() - 1).GID, 10, mRetailerId, mSortId);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    // 根据商家分类ID 获取商品列表
    public void getGoodsList(String lastId, Integer pageSize, String retailerId, String typeId) {
        // 请求的URL
        String url = Services.mHost + "BGoods/App_GetGoodsList_ByTypeID?LastID=%s&PageCnt=%s&RetailerID=%s&TypeID=%s";
        url = String.format(url, lastId, pageSize, retailerId, typeId);
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
                        super.onResponse(s, i);
                        mPullToRefreshScrollView.onRefreshComplete();
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Goods>>() {
                                    }.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas != null && datas.size() > 0) {
                                        goodList.addAll(datas);
                                        mAdapter = new ListViewAdapter<Goods>(StoreGoods.this, R.layout.item_goodgrid, goodList) {
                                            @Override
                                            public void convert(ViewHolder holder, Goods goods) {
                                                ImageView imageView = holder.getView(R.id.iv_goods_image1);
                                                if (!TextUtils.isEmpty(goods.getThumbnail())) {
                                                    ImageLoader.getInstance().displayImage(services.getImageUrl(goods.getThumbnail()), imageView, imageOptions);
                                                } else {
                                                    imageView.setImageResource(R.drawable.default_goods);
                                                }
                                                holder.setText(R.id.tv_goods_name1, goods.T.trim()).setText(R.id.tv_goods_price1, "￥" + goods.GP);
                                            }
                                        };
                                        lvGoodList.setAdapter(mAdapter);
                                        Services.setListViewHeightBasedOnChildren(lvGoodList);
                                    } else {
                                        if (goodList != null && goodList.size() > 0) {
                                            showToast("沒有更多数据");
                                        } else {
                                            tv_goods.setVisibility(View.VISIBLE);
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
