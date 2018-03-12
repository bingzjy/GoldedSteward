package com.ldnet.activity.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.*;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Item;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.CommunityService;
import com.ldnet.utility.*;
import com.ldnet.view.ImageCycleView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.ldnet.goldensteward.R.id.ll_activity_content;

/**
 * Created by zxs on 2016/3/1.
 * 社区服务详情
 */
public class CommunityServicesDetails extends BaseActionBarActivity {
    // 标题
    private TextView tv_main_title;
    private ImageButton  btn_custom;
    private ImageView btn_back;
    private Services services;
    private String mCommunityServicesId;
    private com.ldnet.entities.CommunityServicesDetails communityServicesDetails;
    //社区服务的图片
    private ImageCycleView mImgHousekeeping;
    private TextView mTvHousekeepingTitle, mTvHousekeepingAddress, mTvHousekeepingMemo,mTvHousekeepingActivityTitle;
    private List<Item> itemList= new ArrayList<Item>();
    private CustomListView2 mLvcommunityServices;
    private ListViewAdapter mAdapter;
    private ArrayList<String> mImageUrl = null;
    private ArrayList<String> mActivityImageUrl = new ArrayList<String>();
    private CommunityService communityService;
    private ImageView ivShare;
    private LinearLayout llActivityContent;
    private  LinearLayout.LayoutParams imageLayoutParams;
    private LinearLayout llOpenMap;
    private String shareUrl;
    private DisplayImageOptions imageOptions;
    private String aa = Services.timeFormat();
    private String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
    private Button btnCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_services_details);
        //初始化View、事件
        initView();
        initEvent();
        //初始化服务
        services = new Services();
        communityService=new CommunityService(this);
        //图片加载配置
        imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.default_info)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
                .build();

        //传递的参数
        mCommunityServicesId = getIntent().getStringExtra("COMMUNITY_SERVICES_ID");
        //加载数据
        showProgressDialog();
        communityService.getCommunityServiceDetail(mCommunityServicesId,handler);
    }

    private void initView() {
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        //返回按钮
        btn_back = (ImageView) findViewById(R.id.btn_back);
        //分享
        ivShare = (ImageView) findViewById(R.id.iv_share);
        ivShare.setVisibility(View.VISIBLE);
        //社区服务的图片
        mImgHousekeeping = (ImageCycleView) findViewById(R.id.img_housekeeping);
        mTvHousekeepingActivityTitle = (TextView) findViewById(R.id.tv_housekeeping_title_activity);
        // 改线ViewPager的高度
        imageLayoutParams = (LinearLayout.LayoutParams) mImgHousekeeping.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        imageLayoutParams.height = dm.widthPixels / 3 * 2;
        mImgHousekeeping.setLayoutParams(imageLayoutParams);
        mImageUrl = new ArrayList<String>();
        //社区服务的标题
        mTvHousekeepingTitle = (TextView) findViewById(R.id.tv_housekeeping_title);
        //社区服务的地址
        mTvHousekeepingAddress = (TextView) findViewById(R.id.tv_housekeeping_address);
        //社区服务的介绍
        mTvHousekeepingMemo = (TextView) findViewById(R.id.tv_housekeeping_memo);
        //服务项目
        mLvcommunityServices = (CustomListView2) findViewById(R.id.lv_housekeeping);
        mLvcommunityServices.setFocusable(false);
        //拨打商家电话
        btnCall = (Button) findViewById(R.id.btn_phone_housekeeping);
        //商家活动布局
        llActivityContent = (LinearLayout) findViewById(ll_activity_content);
        //地址
        llOpenMap = (LinearLayout) findViewById(R.id.ll_open_map);
    }

    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {

        @Override
        public void onImageClick(int position, View imageView) {

        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView, imageOptions);
        }
    };

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
       ivShare.setOnClickListener(this);
        llOpenMap.setOnClickListener(this);
        btnCall.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回社区服务列表
                finish();
                break;
            case R.id.iv_share://分享
                shareUrl=communityServicesDetails.ActivityUrl;
                if (!TextUtils.isEmpty(shareUrl)){
                    BottomDialog dialog=new BottomDialog(CommunityServicesDetails.this,
                            shareUrl,"商家服务","","商家服务");
                    dialog.uploadImageUI(CommunityServicesDetails.this);
                }else{
                    showToast("暂时不支持分享");
                }
                break;
            case R.id.ll_open_map: //打开地图 ，位置访问
                if (!TextUtils.isEmpty(communityServicesDetails.Latitude) && !TextUtils.isEmpty(communityServicesDetails.Longitude)) {
                    try {
                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put("LATITUDE", communityServicesDetails.Latitude);
                        extras.put("LONGITUDE",communityServicesDetails.Longitude);
                        extras.put("LEFT", "LEFT");
                        gotoActivity(YellowPages_Map.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    showToast(R.string.position_on);
                }
                break;
            case R.id.btn_phone_housekeeping:
                if (communityServicesDetails!=null&&!TextUtils.isEmpty(communityServicesDetails.Phone)){
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + communityServicesDetails.Phone));
                    startActivity(intent);
                }else{
                    showToast("商家暂时未提供电话");
                }
                break;
            default:
                break;
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    communityServicesDetails=(com.ldnet.entities.CommunityServicesDetails) msg.obj;
                    showData();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast("暂时无数据");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //数据展示
    private void showData(){
        //设置电话可否拨打
        if (TextUtils.isEmpty(communityServicesDetails.Phone)){
            btnCall.setVisibility(View.GONE);
        }else{
            btnCall.setVisibility(View.VISIBLE);
        }
        //设置标题
        tv_main_title.setText(communityServicesDetails.Title);
        //设置活动标题
        if (!TextUtils.isEmpty(communityServicesDetails.ActivityTitle)){
            mTvHousekeepingActivityTitle.setText(communityServicesDetails.ActivityTitle);
        }else{
            llActivityContent.setVisibility(View.GONE);
            mTvHousekeepingActivityTitle.setVisibility(View.GONE);
        }
        //设置活动图片展示
        String activityImages = communityServicesDetails.ActivityImages;
        mActivityImageUrl.clear();
        if (!TextUtils.isEmpty(activityImages)) {
            if (activityImages.contains(",")) {
                String[] images = activityImages.split(",");
                for (String imageId : images) {
                    ImageView imageView=new ImageView(CommunityServicesDetails.this);
                    imageView.setLayoutParams(imageLayoutParams);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ImageLoader.getInstance().displayImage(Services.getImageUrl(imageId),
                            imageView, imageOptions);

                    llActivityContent.addView(imageView);
                }
            } else {
                ImageView imageView=new ImageView(CommunityServicesDetails.this);
                imageView.setLayoutParams(imageLayoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.getInstance().displayImage(Services.getImageUrl(activityImages),
                        imageView,imageOptions);

                llActivityContent.addView(imageView);
            }
        }

        //设置商家介绍的图片展示
        if (!TextUtils.isEmpty(communityServicesDetails.Images)) {
            if(communityServicesDetails.Images.contains(",")){
                String[] str = communityServicesDetails.Images.split(",");
                for (int j = 0; j < str.length; j++) {
                    mImageUrl.add(Services.getImageUrl(str[j]));
                }
                mImgHousekeeping.setImageResources(mImageUrl, mAdCycleViewListener);
            }else{
                mImageUrl.add(Services.getImageUrl(communityServicesDetails.Images));
                mImgHousekeeping.setImageResources(mImageUrl, mAdCycleViewListener);
            }
        }else {
            mImgHousekeeping.setVisibility(View.GONE);
        }

        mTvHousekeepingTitle.setText(communityServicesDetails.Title);
        mTvHousekeepingAddress.setText(communityServicesDetails.Address);
        mTvHousekeepingMemo.setText(communityServicesDetails.Memo);
        itemList = communityServicesDetails.Item;
        mAdapter = new ListViewAdapter<Item>(CommunityServicesDetails.this, R.layout.item_housekeeping, itemList) {
            @Override
            public void convert(ViewHolder holder, Item item) {
                holder.setText(R.id.tv_item_housekeeping_name, item.Name).setText(R.id.tv_item_housekeeping_cost, "￥" + item.Cost);
            }
        };
        mLvcommunityServices.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "周边惠-详情：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "周边惠-详情：" + this.getClass().getSimpleName());
    }
}
