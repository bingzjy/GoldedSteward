package com.ldnet.activity.informationpublish;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.adapter.InfoBarPagerAdapter;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.entities.InfoBarType;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.InfoBarService;
import com.ldnet.utility.PagerSlidingTabStrip;
import com.ldnet.utility.Utility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


//小区信息栏
public class CommunityInfoBarMainActivity extends BaseActionBarFragmentActivity implements View.OnClickListener {

    private ImageView imageViewBack,imageViewEdit,imageViewSearch;
    private RadioGroup radioGroupCheck;
    private RadioButton btnNeed,btnPrivide;
    public static ViewPager viewPager;
    private PagerSlidingTabStrip tabStrip;
    private InfoBarService service;
    private List<InfoBarType> allTypeList=new ArrayList<>();
    private Integer currentIndex = 0;
    public static String currentBigType="0";
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_bar_main);
        initView();
        initEvent();
        service=new InfoBarService(this);
        bundle = savedInstanceState;
    }

    private void initView(){
        imageViewBack=(ImageView)findViewById(R.id.imageView_info_back);
        imageViewEdit=(ImageView)findViewById(R.id.imageview_edit);
        imageViewSearch=(ImageView)findViewById(R.id.imageview_search);
        viewPager=(ViewPager)findViewById(R.id.viewpager_info_bar);
        radioGroupCheck=(RadioGroup)findViewById(R.id.radio_group_info_bar);
        tabStrip=(PagerSlidingTabStrip)findViewById(R.id.pager_sliding_tab_strip_infobar);
        btnPrivide=(RadioButton)findViewById(R.id.radio_button_provide1);
        btnNeed=(RadioButton)findViewById(R.id.radio_button_need1);

        Utility.setTabsValue(tabStrip,CommunityInfoBarMainActivity.this);
    }


    private void initEvent(){
        imageViewSearch.setOnClickListener(this);
        imageViewEdit.setOnClickListener(this);
        imageViewBack.setOnClickListener(this);

        //需求是1，供应是0
        radioGroupCheck.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radio_button_need1) {
                    currentBigType ="1";
                }else{
                    currentBigType ="0";
                }
                //发送广播
                Intent intent=new Intent();
                intent.setAction("com.ldnet.my.test.broadcast");
                sendBroadcast(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("test",viewPager.getCurrentItem()+"");
        showProgressDialog();
        service.getInfoBarType(handler);

        Intent intent=getIntent();
        if (intent!=null){
            currentIndex=intent.getIntExtra("ITEM",0);
            String need=intent.getStringExtra("NEED");
            if (!TextUtils.isEmpty(need)&&need.equals("0")){
                btnPrivide.setChecked(true);
                btnNeed.setChecked(false);
            }else if(!TextUtils.isEmpty(need)&&need.equals("1")) {
                btnNeed.setChecked(true);
                btnPrivide.setChecked(false);
            }
        }

        Log.e("test","默认："+currentIndex);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView_info_back:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
                finish();
                break;
            case R.id.imageview_edit:
                Intent intent1=new Intent(CommunityInfoBarMainActivity.this,PublishCommunityInfoActivity.class);
                if (allTypeList.size()>0){
                    intent1.putExtra("TYPE",(Serializable) allTypeList);
                    intent1.putExtra("ITEM", viewPager.getCurrentItem());
                    intent1.putExtra("NEED", currentBigType);
                    startActivity(intent1);
                    overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
                }
                break;
            case R.id.imageview_search:
                    Intent intent2=new Intent(CommunityInfoBarMainActivity.this,SearchInfoByKeyWordsActivity.class);
                    intent2.putExtra("ITEM", viewPager.getCurrentItem());
                    intent2.putExtra("NEED", currentBigType);
                    startActivity(intent2);
                    overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
                break;
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    allTypeList.clear();
                    InfoBarType allType=new InfoBarType();
                    allType.name="全部";
                    allType.value=-1;
                    allTypeList.add(allType);
                    allTypeList.addAll((List<InfoBarType>)msg.obj);
                    viewPager.setAdapter(new InfoBarPagerAdapter(getSupportFragmentManager(),allTypeList));
                    tabStrip.setViewPager(viewPager);
                    viewPager.setCurrentItem(currentIndex);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
            finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
