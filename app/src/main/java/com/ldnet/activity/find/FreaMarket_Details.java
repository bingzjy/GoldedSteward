package com.ldnet.activity.find;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.me.PublishActivity;
import com.ldnet.entities.FreaMarketDetails;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.FindService;
import com.ldnet.utility.BottomDialog;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.view.ImageCycleView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;

public class FreaMarket_Details extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private ImageCycleView vp_frea_market_images;
    private TextView tv_frea_market_title,tvCustom;
    private TextView tv_frea_market_contract_name;
    private TextView tv_frea_market_datetime;
    private TextView tv_frea_market_org_price;
    private TextView tv_frea_market_price;
    private TextView tv_frea_market_address;
    private TextView tv_frea_market_content;
    private Button btn_frea_market_call;

    private Services services;
    private String mContractPhone;
    private String mFreamarketId;
    private Boolean mFromPublish = false;
    private FreaMarketDetails details;
    private ArrayList<String> mImageUrl = new ArrayList<String>();
    private FindService findService;
    //初始化视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_fleamarket_details);
        //二手商品ID
        mFreamarketId = getIntent().getStringExtra("FREA_MARKET_ID");
        String formPublish = getIntent().getStringExtra("FROM_PUBLISH");
        if (!TextUtils.isEmpty(formPublish)) {
            mFromPublish = Boolean.valueOf(formPublish);
        }
        initView();
        initEvent();
        //初始化服务
        findService=new FindService(this);
        services = new Services();
        showProgressDialog();
        findService.getFreaMarketDetails(mFreamarketId,handlerGetDetail);
    }

    private void initView() {
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.frea_market_title);
        tvCustom=(TextView)findViewById(R.id.tv_custom);
        tvCustom.setVisibility(View.VISIBLE);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //初始化控件
        vp_frea_market_images = (ImageCycleView) findViewById(R.id.vp_frea_market_images);
        // 改线ViewPager的高度
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) vp_frea_market_images.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        linearParams.height = dm.widthPixels / 3 * 2;
        vp_frea_market_images.setLayoutParams(linearParams);
        //标题
        tv_frea_market_title = (TextView) findViewById(R.id.tv_frea_market_title);
        //联系人姓名
        tv_frea_market_contract_name = (TextView) findViewById(R.id.tv_frea_market_contract_name);
        //发布时间
        tv_frea_market_datetime = (TextView) findViewById(R.id.tv_frea_market_datetime);
        //新品价格
        tv_frea_market_org_price = (TextView) findViewById(R.id.tv_frea_market_org_price);
        //二手价格
        tv_frea_market_price = (TextView) findViewById(R.id.tv_frea_market_price);
        //发布地点
        tv_frea_market_address = (TextView) findViewById(R.id.tv_frea_market_address);
        //二手介绍
        tv_frea_market_content = (TextView) findViewById(R.id.tv_frea_market_content);
        //致电物主
        btn_frea_market_call = (Button) findViewById(R.id.btn_frea_market_call);
        if (mFromPublish) {
            tvCustom.setText("编辑");
            btn_frea_market_call.setText("编辑信息");
        }else{
            tvCustom.setText("分享");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "闲置物品-详情：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "闲置物品-详情：" + this.getClass().getSimpleName());
    }


    private com.ldnet.view.ImageCycleView.ImageCycleViewListener mAdCycleViewListener =
            new com.ldnet.view.ImageCycleView.ImageCycleViewListener() {

        @Override
        public void onImageClick(int position, View imageView) {

        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView, imageOptions);// 此处本人使用了ImageLoader对图片进行加装！
        }
    };

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_frea_market_call.setOnClickListener(this);
        tvCustom.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    if (!mFromPublish) {
                        gotoActivityAndFinish(FreaMarket.class.getName(), null);
                    } else {
                        finish();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_frea_market_call:
                if (btn_frea_market_call.getText().toString().contains("删除")) {         //删除
                    if (details != null) {
                        findService.deleteFreaMarket(details.Id,handlerDelete);
                    }
                } else if(btn_frea_market_call.getText().toString().contains("致电")) {   //致电
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mContractPhone));
                    startActivity(intent);
                }
                break;
            case R.id.tv_custom:
                if (mFromPublish) {  //编辑
                    Intent intent = new Intent(this, FreaMarket_Create.class);
                    intent.putExtra("FREA_MARKET_ID", mFreamarketId);
                    intent.putExtra("FROM_FREAMARKET_DETAILS", "true");
                    if (mFromPublish){
                        intent.putExtra("FROM_PUBLISH","true");
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                }else{  //分享
                    if (!TextUtils.isEmpty(details.Url)){
                        BottomDialog dialog=new BottomDialog(FreaMarket_Details.this,details.Url,details.Title);
                        dialog.uploadImageUI(FreaMarket_Details.this);
                    }else{
                        showToast("暂时不能分享");
                    }
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
                if (!mFromPublish) {
                    gotoActivityAndFinish(FreaMarket.class.getName(), null);
                } else {
                    finish();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    Handler handlerGetDetail=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    details=(FreaMarketDetails)msg.obj;
                    if (details.ResidentId.equals(UserInformation.getUserInfo().UserId)||mFromPublish){  //本人的发布信息，编辑
                        btn_frea_market_call.setText("删除信息");
                    }else{                                                   //不是本人 ，致电
                        btn_frea_market_call.setText("致电物主");
                    }

                    tv_frea_market_title.setText(details.Title);
                    tv_frea_market_contract_name.setText(details.ContractName);
                    mContractPhone = details.ContractTel;

                    tv_frea_market_datetime.setText(Services.subStr(details.Updated));
                    if (!TextUtils.isEmpty(details.OrgPrice)) {
                        tv_frea_market_org_price.setText("￥" + details.OrgPrice);
                    }
                    tv_frea_market_price.setText("￥" + details.Price);
                    tv_frea_market_address.setText(details.Address);
                    tv_frea_market_content.setText(details.Memo);
                    if (details.Img != null&&details.Img.size()>0) {
                        for (String imageId : details.Img) {
                            if (!TextUtils.isEmpty(imageId)) {
                                mImageUrl.add(Services.getImageUrl(imageId));
                            }
                        }
                        vp_frea_market_images.setImageResources(mImageUrl, mAdCycleViewListener);
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerDelete=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("删除成功");
                    if (mFromPublish){
                        //返回我的发布
                        Intent intent=new Intent(FreaMarket_Details.this, PublishActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else{
                        //返回我的发布
                        Intent intent=new Intent(FreaMarket_Details.this, FreaMarket.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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
