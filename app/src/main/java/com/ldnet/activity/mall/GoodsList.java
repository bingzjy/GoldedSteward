package com.ldnet.activity.mall;

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Goods;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.GoodsService;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxs on 2015/12/12.
 */
public class GoodsList extends BaseActionBarActivity {
    // 标题
    private TextView tv_page_title,tv_goods_list;
    // 返回
    private ImageButton btn_back;
    //自定义按钮--分享商品
    private Button btn_custom;
    //
    private String mCID;
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
    private List<Goods> datas;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private GoodsService goodsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        services = new Services();
        goodsService=new GoodsService(this);
        mHandler = new Handler();
        //得到参数
        mCID = getIntent().getStringExtra("CID");
        mTitle = getIntent().getStringExtra("PAGE_TITLE");
        setContentView(R.layout.activity_goodslist);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_goods_list = (TextView) findViewById(R.id.tv_goods_list);
        tv_page_title.setText(mTitle);
        goodList = new ArrayList<Goods>();
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        lvGoodList = (ListView) findViewById(R.id.goods_list);
        lvGoodList.setFocusable(false);

        goodList.clear();
        goodsService.getGoodsListByColumnId(mCID,"",handler);

        lvGoodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GoodsList.this, Goods_Details.class);
                intent.putExtra("GOODS", goodList.get(position));
                intent.putExtra("URL", goodList.get(position).URL);
                intent.putExtra("CID", mCID);
                intent.putExtra("PAGE_TITLE", mTitle);
                intent.putExtra("FROM_CLASS_NAME", GoodsList.class.getName());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });
        initEvent();
        initEvents();
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                goodList.clear();
                goodsService.getGoodsListByColumnId(mCID,"",handler);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (goodList != null && goodList.size() > 0) {
                    goodsService.getGoodsListByColumnId(mCID,goodList.get(goodList.size() - 1).GID,handler);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }


    public void initEvent() {
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_back:
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    //获取到商品列表
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    datas=(List<Goods>) msg.obj;
                    goodList.addAll(datas);
                    mAdapter = new ListViewAdapter<Goods>(GoodsList.this, R.layout.item_goodgrid, goodList) {
                        @Override
                        public void convert(ViewHolder holder, Goods goods) {
                            ImageView imageView = holder.getView(R.id.iv_goods_image1);
                            if (!TextUtils.isEmpty(goods.getThumbnail())) {
                                ImageLoader.getInstance().displayImage(services.getImageUrl(goods.getThumbnail()), imageView,imageOptions);
                            } else {
                                imageView.setImageResource(R.drawable.default_goods);
                            }
                            holder.setText(R.id.tv_goods_name1, goods.T.trim()).setText(R.id.tv_goods_price1, "￥" + goods.GP);
                        }
                    };
                    lvGoodList.setAdapter(mAdapter);
                    Services.setListViewHeightBasedOnChildren(lvGoodList);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (goodList != null && goodList.size() > 0) {
                        showToast("没有更多数据");
                    } else {
                        tv_goods_list.setVisibility(View.VISIBLE);
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