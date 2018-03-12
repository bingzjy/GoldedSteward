package com.ldnet.activity.find;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.FindService;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.CustomListView2;
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
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class FreaMarket extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private ImageButton btn_freamarket_create;
    private Services services;
    private CustomListView2 lv_find_fleamarket;
    private ListViewAdapter<com.ldnet.entities.FreaMarket> mAdapter;
    private List<com.ldnet.entities.FreaMarket> mDatas= new ArrayList<com.ldnet.entities.FreaMarket>();
    private TextView tv_find_fleamarket;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private FindService findService;
    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_fleamarket);
        //初始化服务
        services = new Services();
        findService=new FindService(this);
        initView();
        initEvent();
        initEvents();
        findService.getFreaMarketList("",handler);
        showProgressDialog1();
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "闲置物品-主页：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "闲置物品-主页：" + this.getClass().getSimpleName());
    }


    private void initView(){
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.frea_market_title);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //发布闲置物品按钮
        btn_freamarket_create = (ImageButton) findViewById(R.id.btn_custom);
        btn_freamarket_create.setImageResource(R.drawable.plus);
        btn_freamarket_create.setVisibility(View.VISIBLE);
        tv_find_fleamarket = (TextView)findViewById(R.id.tv_find_fleamarket);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        lv_find_fleamarket = (CustomListView2) findViewById(R.id.lv_find_fleamarket);
        lv_find_fleamarket.setFocusable(false);

        mAdapter = new ListViewAdapter<com.ldnet.entities.FreaMarket>(FreaMarket.this, R.layout.item_freamarket, mDatas) {
            @Override
            public void convert(ViewHolder holder, com.ldnet.entities.FreaMarket freaMarket) {
                //设置图片
                if (!TextUtils.isEmpty(freaMarket.Cover)) {
                    ImageLoader.getInstance().displayImage(services.getImageUrl(freaMarket.Cover),
                            (ImageView) holder.getView(R.id.iv_frea_market_image), imageOptions);
                }else {
                    ImageView imageView=(ImageView)holder.getView(R.id.iv_frea_market_image);
                    imageView.setImageResource(R.mipmap.default_info);
                }
                //标题、价格、时间、地址
                holder.setText(R.id.tv_frea_market_title, freaMarket.Title)
                        .setText(R.id.tv_frea_market_price, "￥" + freaMarket.Price)
                        .setText(R.id.tv_frea_market_time, Services.subStr(freaMarket.getDateTime()))
                        .setText(R.id.tv_frea_market_address, freaMarket.Address);
            }
        };
        lv_find_fleamarket.setAdapter(mAdapter);
    }


    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_freamarket_create.setOnClickListener(this);
        lv_find_fleamarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i <= mDatas.size()) {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("FREA_MARKET_ID", mDatas.get(i).Id);
                    try {
                        gotoActivityAndFinish(FreaMarket_Details.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回主页
                try {
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_custom://跳转到发布
                try {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("LEFT","LEFT");
                    gotoActivity(FreaMarket_Create.class.getName(), extras);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                gotoActivityAndFinish(MainActivity.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                findService.getFreaMarketList("",handler);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    findService.getFreaMarketList(mDatas.get(mDatas.size() - 1).Id,handler);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog1();
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    List<com.ldnet.entities.FreaMarket> data=(List<com.ldnet.entities.FreaMarket>) msg.obj;
                    mDatas.addAll(data);
                    mAdapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (mDatas != null && mDatas.size() > 0) {
                        showToast("沒有更多数据");
                    } else {
                        tv_find_fleamarket.setVisibility(View.VISIBLE);
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
