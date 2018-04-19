package com.ldnet.activity.find;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ldnet.activity.main.MainActivity;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.entities.InformationType;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.FindService;
import com.ldnet.activity.adapter.MyPagerAdapter;
import com.ldnet.view.customview.PagerSlidingTabStrip;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.Utility;
import com.tendcloud.tenddata.TCAgent;

import java.util.List;

/**
 * Created by zxs on 2016/3/29.
 * 资讯
 */
public class InforTabActivity extends BaseActionBarFragmentActivity implements View.OnClickListener {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private List<InformationType> mTypeDatas;//类型集合
    private Handler mHandler;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private Integer mCurrentIndex = 0;
    private FindService findService;
    private AcountService acountService;
    private final String URL_INFO=Services.mHost+"API/Information/Sel_HomePageList";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_tab);
        //初始化服务
        services = new Services();
        findService=new FindService(this);
        acountService=new AcountService(this);
        acountService.setIntegralTip(new Handler(),URL_INFO);

        //初始化View,事件
        initView();
        initEvent();
        //获取所有分类
        findService.getInfomationTypes(handler);
    }


    private void initView(){
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        Utility.setTabsValue(tabs,InforTabActivity.this);
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_find_info);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
    }

    @Override
    public void onResume() {
        super.onResume();
        pager.setCurrentItem(pager.getCurrentItem());
        TCAgent.onPageStart(this, "生活资讯-主页：" + this.getClass().getSimpleName());

    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回主页
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
                finish();
                break;
        }
    }

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



    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mTypeDatas=(List<InformationType>) msg.obj;
                    pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mTypeDatas));
                    tabs.setViewPager(pager);
                    pager.setCurrentItem(mCurrentIndex);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast("暂时没有分类");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "生活资讯-主页：" + this.getClass().getSimpleName());
    }

}

