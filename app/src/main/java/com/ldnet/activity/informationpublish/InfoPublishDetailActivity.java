package com.ldnet.activity.informationpublish;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.find.FreaMarket_Create;
import com.ldnet.activity.me.PublishActivity;
import com.ldnet.entities.InfoBarDetail;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.InfoBarService;
import com.ldnet.utility.BottomDialog;
import com.ldnet.utility.Services;
import com.ldnet.utility.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;

import static android.R.attr.data;
import static android.R.attr.rowHeight;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.ldnet.goldensteward.R.id.slv_me_publish;
import static com.ldnet.goldensteward.R.id.vp_frea_market_images;

public class InfoPublishDetailActivity extends BaseActionBarActivity {

    private Button btnCall;
    private LinearLayout llContent;
    private TextView tvTitle,tvContent;
    private InfoBarService service;
    private InfoBarDetail detailData;
    private ImageView ivShare;
    private ImageView btnBack;
    private String shareUrl;
    private boolean mFromPublish;
    private String detailId;
    private int position;
    private String need;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_bar_detail);

        //获取参数
        Intent intent=getIntent();
        detailId=intent.getStringExtra("INFO_ID");
        shareUrl=intent.getStringExtra("SHARE_URL");

        String fromPublish=intent.getStringExtra("FROM_PUBLISH");
        if (!TextUtils.isEmpty(fromPublish)){
            mFromPublish=Boolean.parseBoolean(fromPublish);
        }
        //初始化服务
        service=new InfoBarService(this);
        //初始化布局
        initView();
        //获取详情
        if (!TextUtils.isEmpty(detailId)){
            service.getInfoDetail(detailId,handler);
        }
    }

    private void initView(){
        String item=getIntent().getStringExtra("ITEM");
        need = getIntent().getStringExtra("NEED");
        if (TextUtils.isEmpty(item)){
            position=0;
        }else{
            position=Integer.parseInt(item);
        }
        tvContent=(TextView)findViewById(R.id.tv_info_bar_detail_content);
        tvTitle=(TextView)findViewById(R.id.tv_info_bar_detail_title);
        btnCall=(Button)findViewById(R.id.btn_info_bar_detail_call);
        btnBack=(ImageView)findViewById(R.id.btn_back);
        ivShare=(ImageView)findViewById(R.id.iv_share);
        llContent=(LinearLayout)findViewById(R.id.ll_detail_content);
        btnCall.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);

        if (mFromPublish){
            ivShare.setVisibility(View.GONE);
            btnCall.setText("删除信息");
        }else{
            btnCall.setText("联系他");
            ivShare.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_info_bar_detail_call:
                if (mFromPublish){
                    service.deleteInfoAction(detailId,handlerDelete);
                }else{
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + detailData.TEL));
                    startActivity(intent);
                }
                break;
            case R.id.iv_share:
                if (!TextUtils.isEmpty(shareUrl)){
                    BottomDialog dialog=new BottomDialog(InfoPublishDetailActivity.this,shareUrl,"邻里通");
                    dialog.uploadImageUI(InfoPublishDetailActivity.this);
                }else{
                    showToast("暂时不能分享");
                }
                break;
            case R.id.btn_back:
                if (mFromPublish){
                    finish();
                }else{
                    Intent intent=new Intent(InfoPublishDetailActivity.this,CommunityInfoBarMainActivity.class);
                    intent.putExtra("ITEM",position);
                    intent.putExtra("NEED",need==null?"":need);
                    startActivity(intent);
                }
                break;
        }
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    detailData=(InfoBarDetail) msg.obj;
                    initData(detailData);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast("详情获取失败");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    private void initData(InfoBarDetail data){
        tvTitle.setText(data.Title);
        tvContent.setText(data.Content);

        if (!TextUtils.isEmpty(data.Images)){
            String[] images=data.Images.split(",");
            for (String imageId:images){
                String imageUrl= Services.getImageUrl(imageId);
                ImageView imageView=new ImageView(InfoPublishDetailActivity.this);

                int screenWidth=Utility.getScreenWidthforPX(InfoPublishDetailActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                int marginLeft=Utility.dip2px(InfoPublishDetailActivity.this, 1.0f);
                int marginTop=Utility.dip2px(InfoPublishDetailActivity.this, 20.0f);
                layoutParams.setMargins(marginLeft,marginLeft,marginLeft,marginTop);
                imageView.setLayoutParams(layoutParams);
                ImageLoader.getInstance().displayImage(imageUrl,imageView,imageOptions);
                llContent.addView(imageView);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setMinimumWidth(screenWidth);
                imageView.setMaxWidth(screenWidth);
                imageView.setMaxHeight(screenWidth*5);
                imageView.setAdjustViewBounds(true);
            }
        }
    }


    //删除邻里通
    Handler handlerDelete=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("删除成功");
                    Intent intent1 = new Intent(InfoPublishDetailActivity.this, PublishActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast("删除失败");
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mFromPublish){
                finish();
            }else{
                Intent intent=new Intent(InfoPublishDetailActivity.this,CommunityInfoBarMainActivity.class);
                intent.putExtra("ITEM",position);
                intent.putExtra("NEED", need==null?"":need);
                startActivity(intent);
                finish();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}

