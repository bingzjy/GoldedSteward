package com.ldnet.activity.me;

import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import android.widget.Toast;

import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.find.FreaMarket_Create;
import com.ldnet.activity.find.FreaMarket_Details;
import com.ldnet.activity.find.Weekend_Create;
import com.ldnet.activity.find.Weekend_Details;
import com.ldnet.activity.home.HouseRentUpdate;
import com.ldnet.activity.home.HouseRent_Detail;
import com.ldnet.activity.informationpublish.InfoPublishDetailActivity;
import com.ldnet.entities.HouseRent;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.FindService;
import com.ldnet.service.HouseRentService;
import com.ldnet.service.InfoBarService;
import com.ldnet.utility.*;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.third.SwipeListView.BaseSwipeListViewListener;
import com.third.SwipeListView.SwipeListView;

import java.util.*;

import static com.unionpay.mobile.android.global.a.w;

public class Publish extends BaseActionBarActivity {

    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private SwipeListView slv_me_publish;
    private List<com.ldnet.entities.Publish> mDatas = new ArrayList<com.ldnet.entities.Publish>();
    private ListViewAdapter<com.ldnet.entities.Publish> mAdapter;
    private Handler mHandler;
    private List<HouseRent> houseRents = new ArrayList<HouseRent>();
    private HouseRent house_rent;
    private TextView tv_publish;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private HouseRentService houseRentService;
    private FindService findService;
    private InfoBarService infoBarService;
    private com.ldnet.entities.Publish currentPublish;
    private final String INFOBAR="邻里通";
    private final String HOUSE="房屋租赁";
    private final String FREAMARK ="闲置物品";
    private final String WEEKEND ="周末去哪";
    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_publish);
        initView();
        initEvent();
        initService();
    }

    //初始化服务
    private void initService() {
        houseRentService = new HouseRentService(this);
        findService = new FindService(this);
        infoBarService = new InfoBarService(this);
        services = new Services();
    }

    private void initView(){
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_publish);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_publish = (TextView)findViewById(R.id.tv_publish);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        slv_me_publish = (SwipeListView) findViewById(R.id.slv_me_publish);
        slv_me_publish.setFocusable(false);
        slv_me_publish.setOffsetLeft(this.getResources().getDisplayMetrics().widthPixels * 3 / 5);

        mAdapter = new ListViewAdapter<com.ldnet.entities.Publish>(Publish.this, R.layout.item_publish, mDatas) {
            @Override
            public void convert(ViewHolder holder, final com.ldnet.entities.Publish publish) {
                holder.setText(R.id.tv_publish_title, publish.Title)
                        .setText(R.id.tv_publish_type, publish.Type)
                        .setText(R.id.tv_publish_time, Services.subStr(publish.DateTime));

                //设置默认
                Button btn_update = holder.getView(R.id.btn_update);
                Button btn_delete = holder.getView(R.id.btn_delete);

                //房屋租赁，提前获取对应的详细
                if (publish.Type.equals(HOUSE)){
                    houseRentService.getHouseRentListById(publish.Id,handlerHouseResent);
                }
                //邻里通隐藏修改功能
                if (publish.Type.equals(INFOBAR)){
                    btn_update.setText("关闭");
                }else{
                    btn_update.setText("修改");
                }

                //更新
                btn_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //闲置物品   周末去哪   房屋租赁
                        if (publish.Type.equals(FREAMARK)) {
                            Intent intent = new Intent(Publish.this, FreaMarket_Create.class);
                            intent.putExtra("FREA_MARKET_ID", publish.Id);
                            intent.putExtra("FROM_PUBLISH", "true");
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

                        } else if (publish.Type.equals(WEEKEND)) {
                            Intent intent = new Intent(Publish.this, Weekend_Create.class);
                            intent.putExtra("FREA_MARKET_ID", publish.Id);
                            intent.putExtra("FROM_PUBLISH", "true");
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                        }else if(publish.Type.equals(INFOBAR)){
                            slv_me_publish.closeOpenedItems();
                        } else {
                            int position = mDatas.indexOf(publish);
                            if (houseRents.get(position)!=null){
                                Intent intent = new Intent(Publish.this, HouseRentUpdate.class);
                                intent.putExtra("HouseRent_ID",publish.Id);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                            }
                        }
                        slv_me_publish.closeOpenedItems();
                    }
                });
                //删除
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentPublish=publish;

                        if (publish.Type.equals(FREAMARK)) {   //闲置

                            findService.deleteFreaMarket(publish.Id, handlerDelete);

                        } else if (publish.Type.equals(WEEKEND)) {   //周末去哪

                            findService.deleteWeekend(publish.Id, handlerDelete);

                        } else if (publish.Type.equals(INFOBAR)) {    //邻里通

                            infoBarService.deleteInfoAction(publish.Id,handlerDelete);

                        } else {    //房屋租赁

                            houseRentService.deleteHouseRent(publish.Id,handlerDelete);
                        }
                        slv_me_publish.closeOpenedItems();
                    }
                });

            }
        };
        slv_me_publish.setAdapter(mAdapter);
        Utility.setListViewHeightBasedOnChildren(slv_me_publish);
    }

    @Override
    protected void onResume() {
        super.onResume();
        houseRents=new ArrayList<>();
        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        houseRents.clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    public void initEvent() {
        btn_back.setOnClickListener(this);
        //点击item关闭滑动
        slv_me_publish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slv_me_publish.closeOpenedItems();
            }
        });
        slv_me_publish.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                super.onClickFrontView(position);
                try {
                    if (position <= mDatas.size()) {
                        com.ldnet.entities.Publish publish = mDatas.get(position);
                        HashMap<String, String> extras = new HashMap<String, String>();
                        if (publish.Type.equals(FREAMARK)) {
                            extras.clear();
                            extras.put("FREA_MARKET_ID", publish.Id);
                            extras.put("FROM_PUBLISH", "true");
                            extras.put("LEFT", "LEFT");
                            gotoActivity(FreaMarket_Details.class.getName(), extras);
                        } else if (publish.Type.equals(WEEKEND)) {
                            extras.clear();
                            extras.put("WEEKEND_ID", publish.Id);
                            extras.put("FROM_PUBLISH", "true");
                            extras.put("LEFT", "LEFT");
                            gotoActivity(Weekend_Details.class.getName(), extras);
                        }else if (publish.Type.equals(INFOBAR)){
                            extras.clear();
                            extras.put("INFO_ID", publish.Id);
                            extras.put("FROM_PUBLISH", "true");
                            extras.put("LEFT", "LEFT");
                            gotoActivity(InfoPublishDetailActivity.class.getName(), extras);
                        }else {
                            if (houseRents.get(position) != null) {
                                Intent intent = new Intent(Publish.this, HouseRent_Detail.class);
                                intent.putExtra("HouseRent_ID", publish.Id);
                                intent.putExtra("FROM_PUBLISH", "true");
                                extras.put("LEFT", "LEFT");
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                            }else{
                                showToast("请稍后再试");
                            }
                        }
                        slv_me_publish.closeOpenedItems();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        });
        //下拉刷新
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                findService.getMyPublish("",handlerGetPublish);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    findService.getMyPublish(mDatas.get(mDatas.size() - 1).Id, handlerGetPublish);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    //加载数据
    private void loadData() {
        showProgressDialog();
        mDatas.clear();
        findService.getMyPublish("",handlerGetPublish);
    }

    //获取所有的发布内容列表
    Handler handlerGetPublish=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    List<com.ldnet.entities.Publish> data=(List<com.ldnet.entities.Publish>) msg.obj;
                    mDatas.addAll(data);
                    mAdapter.notifyDataSetChanged();
                    Utility.setListViewHeightBasedOnChildren(slv_me_publish);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (mDatas != null && mDatas.size() > 0) {
                        showToast("沒有更多数据");
                    } else {
                        tv_publish.setVisibility(View.VISIBLE);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //删除房屋租赁、邻里通、周边游、闲置物品
    Handler handlerDelete=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("删除成功");
                    mDatas.remove(currentPublish);
                    mAdapter.notifyDataSetChanged();
                    slv_me_publish.closeOpenedItems();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast("删除失败");
                    break;
            }
        }
    };

    //获取房屋租赁
    Handler handlerHouseResent=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    house_rent =(HouseRent) msg.obj;
                    if (house_rent!=null){
                        houseRents.add(house_rent);
                    }
                    break;
            }
        }
    };
}
