package com.ldnet.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.HouseProperties;
import com.ldnet.entities.HouseRent;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.HouseRentService;
import com.ldnet.utility.*;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;
import com.third.listviewshangxia.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Murray on 2015/9/10.
 */
public class HouseRent_List extends BaseActionBarActivity {
    private ListView houserent_list;
    private ImageButton btn_back, btn_create;
    private TextView tv_page_title;
    private TextView tv_rent_list;
    private ListViewAdapter mListViewAdapter;
    private List<HouseRent> mList;
    private Context context;
    private Services services;
    private Intent intent;
    private HouseProperties mHouseProperties;
    private List<HouseRent> datas;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private HouseRentService houseRentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.houserent_list);

        //页面标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.fragment_home_yellow_lease);
        //发布房屋租赁
        btn_create = (ImageButton) findViewById(R.id.btn_custom);
        btn_create.setImageResource(R.drawable.plus);
        btn_create.setVisibility(View.VISIBLE);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_rent_list = (TextView) findViewById(R.id.tv_rent_list);

        //服务，获取数据
        services = new Services();
        houseRentService=new HouseRentService(this);
        //第一次加载数据
        showProgressDialog();
        houseRentService.getHouseInfo(handlerGetInfo);
     //   getHouseRentInfo();

        mList = new ArrayList<HouseRent>();
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        houserent_list = (ListView) findViewById(R.id.houserent_listview);
        houserent_list.setFocusable(false);
        houserent_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i <= mList.size()) {
                    HouseRent house = mList.get(i);
                    intent = new Intent(HouseRent_List.this, HouseRent_Detail.class);
                    intent.putExtra("HouseRent", house);
                    intent.putExtra("HouseRent_ID",house.getId());
                    startActivity(intent);
                    finish();
                }
            }
        });
        initEvent();
        initEvents();
    }

    public void initEvent() {
        btn_create.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mList.clear();
                houseRentService.getHouseRentList("",handlerDataList);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mList != null && mList.size() > 0) {
                    houseRentService.getHouseRentList(mList.get(mList.size() - 1).Id,handlerDataList);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                break;
            case R.id.btn_custom:
                intent = new Intent(this, HouseRent_Create.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                finish();
                break;
        }
    }

    private void getAdapterData(JSONObject jsonObject){
        mHouseProperties = new HouseProperties();
        try {
            mHouseProperties.setOrientation(jsonObject.getString("Orientation"));
            mHouseProperties.setFitmentType(jsonObject.getString("FitmentType"));
            mHouseProperties.setRentType(jsonObject.getString("RentType"));
            mHouseProperties.setRoomDeploy(jsonObject.getString("RoomDeploy"));
            mHouseProperties.setRoomType(jsonObject.getString("RoomType"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mListViewAdapter = new ListViewAdapter<HouseRent>(HouseRent_List.this, R.layout.item_houserent_list, mList) {
            @Override
            public void convert(ViewHolder holder, HouseRent h) {
                holder.setText(R.id.houserent_item_title, h.Title);
                try {
                    holder.setText(R.id.houserent_item_housetype, mHouseProperties.getRoomType().get(Integer.valueOf(h.getRoomType()) + 1).Value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.setText(R.id.houserent_item_acreage, h.Acreage + "平米");
                holder.setText(R.id.houserent_item_address, h.Address);
                holder.setText(R.id.houserent_item_price, "￥" + h.Price + "元");
                holder.setText(R.id.houserent_item_status, h.Status);
                ImageView image = holder.getView(R.id.houserent_item_img);
                if (!TextUtils.isEmpty(h.Images)) {
                    ImageLoader.getInstance().displayImage(services.getImageUrl(h.getThumbnail()), image,imageOptions);
                } else {
                    image.setImageResource(R.drawable.default_info);
                }
            }
        };
        houserent_list.setAdapter(mListViewAdapter);
        mList.clear();
        houseRentService.getHouseRentList("",handlerDataList);
    }


    Handler handlerGetInfo=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    JSONObject jsonObject=(JSONObject) msg.obj;
                    getAdapterData(jsonObject);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerDataList=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPullToRefreshScrollView.onRefreshComplete();
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    datas=(List<HouseRent>)msg.obj;
                    mList.addAll(datas);
                    mListViewAdapter.notifyDataSetChanged();
                    Services.setListViewHeightBasedOnChildren(houserent_list);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (mList != null && mList.size() > 0) {
                        showToast("沒有更多数据");
                    } else {
                        tv_rent_list.setVisibility(View.VISIBLE);
                    }
                    mListViewAdapter.notifyDataSetChanged();
                    Services.setListViewHeightBasedOnChildren(houserent_list);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "房屋租赁列表：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "房屋租赁列表：" + this.getClass().getSimpleName());
    }
}
