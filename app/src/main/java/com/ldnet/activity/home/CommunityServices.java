package com.ldnet.activity.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseFragment;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.CommunityService;
import com.ldnet.utility.*;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by zxs on 2016/3/1.
 * 社区服务
 */
public class CommunityServices extends BaseFragment implements View.OnClickListener {
    private TextView tv_community_services;
    private ImageButton btn_back;
    private Services services;
    private Handler mHandler;
    private CustomListView2 mLvCommunityServices;
    private ListViewAdapter<com.ldnet.entities.CommunityServices> mAdapter;
    private List<com.ldnet.entities.CommunityServices> mDatas=new ArrayList<com.ldnet.entities.CommunityServices>();;
    private String mSortId;
    private String mSortKeywords;
    private String mSortTypes;
    private String communityId;
    private String mCityCode = "西安";

    private PullToRefreshScrollView mPullToRefreshScrollView;
    private CommunityService communityService;

    private DisplayImageOptions imageOptions;
    private String aa = Services.timeFormat();
    private String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";


    public static Fragment getInstance(Bundle bundle) {
        CommunityServices fragment = new CommunityServices();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.default_info)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_community_services, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findView(view);
        initEvents();
    }

    public void findView(View view) {
        mSortId = getArguments().getString("Id");
        mSortKeywords = getArguments().getString("Name");
        communityId = UserInformation.getUserInfo().CommunityId;
        mCityCode = UserInformation.getUserInfo().CommuntiyCityId;
        tv_community_services = (TextView) view.findViewById(R.id.tv_community_services);
        mPullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(getActivity()));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(getActivity()));
        mLvCommunityServices = (CustomListView2) view.findViewById(R.id.lv_community_services);
        mLvCommunityServices.setFocusable(false);
        mLvCommunityServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i <= mDatas.size()) {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("COMMUNITY_SERVICES_ID", mDatas.get(i).Id);
                    extras.put("LEFT", "LEFT");
                    try {
                        gotoActivity(CommunityServicesDetails.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mAdapter = new ListViewAdapter<com.ldnet.entities.CommunityServices>(getActivity(), R.layout.item_community_services, mDatas) {
            @Override
            public void convert(ViewHolder holder, final com.ldnet.entities.CommunityServices communityServices) {

                if (TextUtils.isEmpty(communityServices.ActivityImages)) {   //没有商家活动展示
                    holder.getView(R.id.ll_activity_detail_content).setVisibility(View.GONE);
                    holder.getView(R.id.ll_no_activity_detail_content).setVisibility(View.VISIBLE);

                    //标题、地址
                    holder.setText(R.id.tv_training_title, communityServices.Title)
                            .setText(R.id.tv_training_address, communityServices.Address);
                    //点击电话，拨打电话给商家
                    holder.getView(R.id.tel_training).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + communityServices.Phone));
                            mContext.startActivity(intent);
                        }
                    });
                } else {   //有商家活动展示
                    holder.getView(R.id.ll_activity_detail_content).setVisibility(View.VISIBLE);
                    holder.getView(R.id.ll_no_activity_detail_content).setVisibility(View.GONE);

                    ImageView imageView = holder.getView(R.id.iv_detail_activity_image);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();

                    int screenrHeight = Utility.getScreenHeightforDIP(getActivity());
                    layoutParams.height = Utility.dip2px(getActivity(), Float.parseFloat(String.valueOf(screenrHeight / 3)));

                    if (communityServices.ActivityImages.contains(",")) {
                        String[] images = communityServices.ActivityImages.split(",");
                        ImageLoader.getInstance().displayImage(Services.getImageUrl(images[0]), imageView, imageOptions);
                    } else {
                        ImageLoader.getInstance().displayImage(Services.getImageUrl(communityServices.ActivityImages), imageView, imageOptions);
                    }
                    holder.setText(R.id.tv_detail_activity_title, communityServices.ActivityTitle.toString());
                }
            }
        };
        mLvCommunityServices.setAdapter(mAdapter);

        //初始化服务
        services = new Services();
        communityService = new CommunityService(getActivity());

        //获取数据
        loadData(true);
    }

    //加载数据
    private void loadData(boolean refresh) {
        if (refresh) {
            mDatas.clear();
            if (mSortId.equals("200")) { //获取优惠商家数据
                communityService.getYellowPageActivity("", handler);
            } else {
                communityService.getCommunityService(mSortId, "", handler);
            }
        } else {
            if (mSortId.equals("200")) { //获取优惠接口
                communityService.getYellowPageActivity(mDatas.get(mDatas.size() - 1).Id, handler);
            } else {
                communityService.getCommunityService(mSortId, mDatas.get(mDatas.size() - 1).Id, handler);
            }
        }
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
            default:
                break;
        }
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                loadData(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    loadData(false);
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
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mDatas.addAll((List<com.ldnet.entities.CommunityServices>)msg.obj);
                    mAdapter.notifyDataSetChanged();
                    mLvCommunityServices.setVisibility(View.VISIBLE);
                    tv_community_services.setVisibility(View.GONE);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (mDatas != null && mDatas.size() > 0) {
                        showToast("沒有更多数据");
                        mLvCommunityServices.setVisibility(View.VISIBLE);
                        tv_community_services.setVisibility(View.GONE);
                    } else {
                        mLvCommunityServices.setVisibility(View.GONE);
                        tv_community_services.setVisibility(View.VISIBLE);
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
