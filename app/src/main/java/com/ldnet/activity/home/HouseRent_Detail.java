package com.ldnet.activity.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.me.PublishActivity;
import com.ldnet.entities.HouseProperties;
import com.ldnet.entities.HouseRent;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.HouseRentService;
import com.ldnet.utility.*;
import com.ldnet.view.ImageCycleView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Murray on 2015/9/10.
 */
public class HouseRent_Detail extends BaseActionBarActivity {
    private TextView houserent_detail_price, houserent_detail_rentType,
            houserent_detail_room, houserent_detail_acreage,
            houserent_detail_fitmenttype, houserent_detail_roomtype,
            houserent_detail_floor, houserent_detail_orientation, btn_update,
            houserent_detail_title, houserent_detail_address, houserent_detail_tv_address,
            tv_page_title,houserent_detail_RoomDeploy;
    private Button houserent_detail_contracttel;
    private RelativeLayout houserent_detail_rl_address;
    private Services service;
    private ImageButton btn_back;
    private HouseProperties mHouseProperties=new HouseProperties();
    private ImageCycleView vp_house_rent_images;
    private List<View> mImages;
    private PagerAdapter mAdapter;
    private HouseRent houseRent;
    private ArrayList<String> mImageUrl = new ArrayList<String>();
    private Boolean mFromPublish = false;
    private String houseId;
    private HouseRentService houseService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.houserent_item_detail);

        //来自我的发布
        String formPublish = getIntent().getStringExtra("FROM_PUBLISH");
        if (!TextUtils.isEmpty(formPublish)) {
            mFromPublish = Boolean.valueOf(formPublish);
        }

        initView();
        initEvent();
        initService();

        //获取房屋信息中的配置信息
        houseService.getHouseInfo(handlerGetHouseInfo);

        //来自房屋租赁
        houseId=getIntent().getStringExtra("HouseRent_ID");
        if (!TextUtils.isEmpty(houseId)){
            showProgressDialog();
            houseService.getHouseRentListById(houseId, handlerHouseResent);
        }
    }

    private void initService(){
        service = new Services();
        houseService=new HouseRentService(this);
    }

    private void initData(){
        //标题
        houserent_detail_title.setText(houseRent.Title);
        //租金
        houserent_detail_price.setText("￥" + houseRent.Price + "元");
        //房屋结构
        houserent_detail_room.setText(houseRent.Room + "室" + houseRent.Hall + "厅" + houseRent.Toilet + "卫");
        //房屋面积
        houserent_detail_acreage.setText(houseRent.Acreage + "平米");
        //楼层情况
        houserent_detail_floor.setText(houseRent.Floor + "/" + houseRent.FloorCount);
        //地址
        houserent_detail_address.setText(houseRent.Address);

        try {
            //付钱方式
            int key5=Integer.parseInt(houseRent.RentType)+1;
            houserent_detail_rentType.setText(mHouseProperties.getRentType().get(key5).getValue());

            //装修
            int key4=Integer.parseInt(houseRent.FitmentType)+1;
            houserent_detail_fitmenttype.setText(mHouseProperties.getFitmentType().get(key4).getValue());
            //概况
            int key3=Integer.parseInt(houseRent.getRoomType())+1;
            houserent_detail_roomtype.setText(mHouseProperties.getRoomType().get(key3).getValue());
            //房屋配置
            int key2=Integer.parseInt(houseRent.getRoomDeploy())+1;
            houserent_detail_RoomDeploy.setText(mHouseProperties.getRoomDeploy().get(key2).getValue());
            //朝向
            int key = Integer.parseInt(houseRent.getOrientation())+1;
            houserent_detail_orientation.setText(mHouseProperties.getOrientation().get(key).getValue());



        } catch (Exception e) {
            e.printStackTrace();
        }


        if (mFromPublish) {
            houserent_detail_rl_address.setVisibility(View.GONE);
            houserent_detail_tv_address.setVisibility(View.GONE);
            houserent_detail_contracttel.setText("删除信息");
        }

        //图片加载
        if (!TextUtils.isEmpty(houseRent.Images)) {
            for (String imageid : houseRent.Images.split(",")) {
                if(!TextUtils.isEmpty(imageid)){
                    mImageUrl.add(Services.getImageUrl(imageid));
                }
            }
            vp_house_rent_images.setImageResources(mImageUrl, mAdCycleViewListener);
        }
    }
    private void initView(){
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.houserent_detail_title);
        houserent_detail_RoomDeploy = (TextView)findViewById(R.id.houserent_detail_RoomDeploy);
        houserent_detail_rl_address = (RelativeLayout)findViewById(R.id.houserent_detail_rl_address);
        houserent_detail_title = (TextView) findViewById(R.id.houserent_detail_title);
        houserent_detail_price = (TextView) findViewById(R.id.houserent_detail_price);
        houserent_detail_rentType = (TextView) findViewById(R.id.houserent_detail_rentType);
        houserent_detail_room = (TextView) findViewById(R.id.houserent_detail_room);
        houserent_detail_acreage = (TextView) findViewById(R.id.houserent_detail_acreage);
        houserent_detail_fitmenttype = (TextView) findViewById(R.id.houserent_detail_fitmenttype);
        houserent_detail_roomtype = (TextView) findViewById(R.id.houserent_detail_roomtype);
        houserent_detail_floor = (TextView) findViewById(R.id.houserent_detail_floor);
        houserent_detail_orientation = (TextView) findViewById(R.id.houserent_detail_orientation);
        houserent_detail_address = (TextView) findViewById(R.id.houserent_detail_address);
        houserent_detail_tv_address = (TextView) findViewById(R.id.houserent_detail_tv_address);
        houserent_detail_contracttel = (Button) findViewById(R.id.houserent_detail_contracttel);
        btn_update = (TextView) findViewById(R.id.btn_update);
        vp_house_rent_images = (ImageCycleView) findViewById(R.id.vp_house_rent_images);

        // 改线ViewPager的高度
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) vp_house_rent_images.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        linearParams.height = dm.widthPixels / 3 * 2;
        vp_house_rent_images.setLayoutParams(linearParams);

        if (mFromPublish){
            btn_update.setText("编辑");
            houserent_detail_contracttel.setText("删除信息");
            btn_update.setVisibility(View.VISIBLE);
        }else{
            houserent_detail_contracttel.setText("拨打电话");
            btn_update.setVisibility(View.GONE);
        }
    }
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        houserent_detail_contracttel.setOnClickListener(this);
    }

    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {

        @Override
        public void onImageClick(int position, View imageView) {

        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView, imageOptions);// 此处本人使用了ImageLoader对图片进行加装！
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (mFromPublish) {
                    finish();
                } else {
                    try {
                        gotoActivityAndFinish(HouseRent_List.class.getName(), null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.houserent_detail_contracttel://如果来自我的发布跳转到编辑
                if (mFromPublish) {
                    houseService.deleteHouseRent(houseId,handlerDelete);
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + houseRent.ContactTel));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                }
                break;
            case R.id.btn_update:
                Intent intent = new Intent(this, HouseRentUpdate.class);
                intent.putExtra("HouseRent", houseRent);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mFromPublish) {
                finish();
            } else {
                try {
                    gotoActivityAndFinish(HouseRent_List.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //获取房屋租赁
    Handler handlerHouseResent=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    houseRent= (HouseRent) msg.obj;
                    initData();
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
                    if (mFromPublish){
                        //返回我的发布
                        Intent intent=new Intent(HouseRent_Detail.this, PublishActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else{
                        //返回我的房屋租赁列表
                        Intent intent=new Intent(HouseRent_Detail.this, HouseRent_List.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast("删除失败");
                    break;
            }
        }
    };


    private void setAdapterData(JSONObject jsonObject1) {
        mHouseProperties = new HouseProperties();
        try {
            mHouseProperties.setOrientation(jsonObject1.getString("Orientation"));
            mHouseProperties.setFitmentType(jsonObject1.getString("FitmentType"));
            mHouseProperties.setRentType(jsonObject1.getString("RentType"));
            mHouseProperties.setRoomDeploy(jsonObject1.getString("RoomDeploy"));
            mHouseProperties.setRoomType(jsonObject1.getString("RoomType"));
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Handler handlerGetHouseInfo=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    JSONObject object = (JSONObject) msg.obj;
                    setAdapterData(object);
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
        TCAgent.onPageStart(this, "房屋租赁详情：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "房屋租赁详情：" + this.getClass().getSimpleName());
    }
}
