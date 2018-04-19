package com.ldnet.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldnet.activity.base.BaseFragment;
import com.ldnet.activity.find.FreaMarket_Create;
import com.ldnet.activity.find.FreaMarket_Details;
import com.ldnet.activity.find.Weekend_Create;
import com.ldnet.activity.find.Weekend_Details;
import com.ldnet.activity.home.HouseRentUpdate;
import com.ldnet.activity.home.HouseRent_Detail;
import com.ldnet.activity.informationpublish.InfoPublishDetailActivity;
import com.ldnet.entities.PublishEntity;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.FindService;
import com.ldnet.service.HouseRentService;
import com.ldnet.service.InfoBarService;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;
import com.third.SwipeListView2.SwipeListViewWrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.activity.me
 * @class describe
 * @time 2018/1/9 17:55
 * @change
 * @chang time
 * @class describe
 */

public class PublishFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_find_informations)
    TextView tvDataNull;
    @BindView(R.id.slv_me_publish)
    SwipeListViewWrap slvMePublish;
    @BindView(R.id.main_act_scrollview)
    PullToRefreshScrollView refreshScrollview;

    private HouseRentService houseRentService;
    private FindService findService;
    private InfoBarService infoBarService;
    private ListViewAdapter<PublishEntity> mAdapter;
    private List<PublishEntity> mDatas = new ArrayList<>();
    private int currentIndex = 0;
    private PublishEntity currentPublish;

    public static PublishFragment getInstance(Bundle bundle) {
        PublishFragment fragment = new PublishFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initAdapter();
        initEvent();
        loadData(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(getActivity(), "我的发布：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(getActivity(), "我的发布：" + this.getClass().getSimpleName());
    }


    private void initView() {
        currentIndex = getArguments().getInt("value");
        refreshScrollview.setMode(PullToRefreshBase.Mode.BOTH);
        refreshScrollview.setHeaderLayout(new HeaderLayout(getActivity()));
        refreshScrollview.setFooterLayout(new FooterLayout(getActivity()));
        slvMePublish.setFocusable(false);

        int deviceWidth=this.getResources().getDisplayMetrics().widthPixels;
        slvMePublish.setOffsetLeft(deviceWidth-Utility.dip2px(getActivity(),160));
    }

    //初始化适配器
    private void initAdapter() {
        mAdapter = new ListViewAdapter<PublishEntity>(getActivity(), R.layout.item_publish_content, mDatas) {
            @Override
            public void convert(ViewHolder holder, final PublishEntity publishEntity) {
                //0房屋租赁，1邻里通，2闲置物品，3周边游
                currentPublish = publishEntity;

                ImageView ivCover = holder.getView(R.id.iv_item_info_bar_cover);
                ImageView ivType = holder.getView(R.id.iv_item_info_type);
                holder.setText(R.id.tv_item_info_bar_title, publishEntity.Title);
                holder.setText(R.id.tv_item_info_bar_date, publishEntity.DateTime);
                ImageLoader.getInstance().displayImage(Services.getImageUrl(publishEntity.Cover), ivCover, Utility.imageOptions);

                Button btn_update = holder.getView(R.id.btn_item_publish_update);
                Button btn_delete = holder.getView(R.id.btn_item_publish_delete);

                if (publishEntity.Type == 1 && publishEntity.BarType == 0) {   //供应
                    ivType.setVisibility(View.VISIBLE);
                    ivType.setImageResource(R.drawable.ic_privide);
                } else if (publishEntity.Type == 1 && publishEntity.BarType == 1) { //需求
                    ivType.setVisibility(View.VISIBLE);
                    ivType.setImageResource(R.drawable.ic_need);
                } else {
                    ivType.setVisibility(View.GONE);
                }

                //邻里通隐藏修改功能
                if (publishEntity.Type == 1) {
                    btn_update.setText("关闭");
                } else {
                    btn_update.setText("编辑");
                }

                //删除
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (publishEntity.Type) {

                            case 0: //房屋租赁
                                showProgressDialog();
                                houseRentService.deleteHouseRent(publishEntity.Id, handlerDelete);
                                break;
                            case 1: //邻里通
                                showProgressDialog();
                                infoBarService.deleteInfoAction(publishEntity.Id, handlerDelete);
                                break;
                            case 2: //闲置物品
                                showProgressDialog();
                                findService.deleteFreaMarket(publishEntity.Id, handlerDelete);
                                break;
                            case 3:  //周边游
                                  findService.deleteWeekend(publishEntity.Id, handlerDelete);
                                break;
                        }
                        slvMePublish.closeOpenedItems();
                    }
                });

                //编辑
                btn_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (publishEntity.Type) {
                            case 0: //房屋租赁
                                Intent intent1 = new Intent(getActivity(), HouseRentUpdate.class);
                                intent1.putExtra("HouseRent_ID", publishEntity.Id);
                                startActivity(intent1);
                                break;
                            case 1: //邻里通
                                slvMePublish.closeOpenedItems();
                                break;
                            case 2:  //闲置物品
                                Intent intent2 = new Intent(getActivity(), FreaMarket_Create.class);
                                intent2.putExtra("FREA_MARKET_ID", publishEntity.Id);
                                intent2.putExtra("FROM_PUBLISH", "true");
                                startActivity(intent2);
                                break;
                            case 3: //周边游
                                Intent intent3 = new Intent(getActivity(), Weekend_Create.class);
                                intent3.putExtra("FREA_MARKET_ID", publishEntity.Id);
                                intent3.putExtra("FROM_PUBLISH", "true");
                                startActivity(intent3);
                                break;
                        }
                        slvMePublish.closeOpenedItems();
                    }
                });
            }
        };

        slvMePublish.setAdapter(mAdapter);
    }

    //初始化事件
    private void initEvent() {
        //刷新加载
        refreshScrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                loadData(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    findService.getMyPublishByType(currentIndex, mDatas.get(mDatas.size() - 1).Id, handler);
                } else {
                    refreshScrollview.onRefreshComplete();
                }
            }
        });


        slvMePublish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slvMePublish.closeOpenedItems();
            }
        });

        //item单击事件
        slvMePublish.setSwipeListViewListener(new com.third.SwipeListView2.BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                super.onClickFrontView(position);
                currentPublish = mDatas.get(position);
                HashMap<String, String> extras = new HashMap<String, String>();
                try {
                    switch (currentPublish.Type) {
                        case 0: //房屋租赁
                            Intent intent = new Intent(getActivity(), HouseRent_Detail.class);
                            intent.putExtra("HouseRent_ID", currentPublish.Id);
                            intent.putExtra("FROM_PUBLISH", "true");
                            extras.put("LEFT", "LEFT");
                            startActivity(intent);
                            break;
                        case 1: //邻里通
                            extras.clear();
                            extras.put("INFO_ID", currentPublish.Id);
                            extras.put("FROM_PUBLISH", "true");
                            extras.put("LEFT", "LEFT");
                            gotoActivity(InfoPublishDetailActivity.class.getName(), extras);
                            break;
                        case 2: //闲置物品
                            extras.clear();
                            extras.put("FREA_MARKET_ID", currentPublish.Id);
                            extras.put("FROM_PUBLISH", "true");
                            extras.put("LEFT", "LEFT");
                            gotoActivity(FreaMarket_Details.class.getName(), extras);
                            break;
                        case 3: //周边游
                            extras.clear();
                            extras.put("WEEKEND_ID", currentPublish.Id);
                            extras.put("FROM_PUBLISH", "true");
                            extras.put("LEFT", "LEFT");
                            gotoActivity(Weekend_Details.class.getName(), extras);
                            break;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //初始化服务
    private void initService() {
        houseRentService = new HouseRentService(getActivity());
        findService = new FindService(getActivity());
        infoBarService = new InfoBarService(getActivity());
    }

    //加载数据
    private void loadData(boolean show) {
        mDatas.clear();
        if (show) {
            showProgressDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    findService.getMyPublishByType(currentIndex, "", handler);}
            }, 100);
        } else {
            findService.getMyPublishByType(currentIndex, "", handler);
        }
    }

    //获取数据
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            refreshScrollview.onRefreshComplete();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    slvMePublish.setVisibility(View.VISIBLE);
                    tvDataNull.setVisibility(View.GONE);
                    mDatas.addAll((List<PublishEntity>) msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (mDatas != null && mDatas.size() > 0) {
                        showToast("没有更多数据");
                        tvDataNull.setVisibility(View.GONE);
                        slvMePublish.setVisibility(View.VISIBLE);
                    } else {
                        tvDataNull.setVisibility(View.VISIBLE);
                        slvMePublish.setVisibility(View.GONE);
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
    Handler handlerDelete = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast("删除成功");
                    loadData(false);
                    slvMePublish.closeOpenedItems();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast("删除失败");
                    break;
            }
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
