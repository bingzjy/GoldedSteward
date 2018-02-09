package com.ldnet.activity.find;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.entities.InformationType;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.FindService;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.MyPagerAdapter;
import com.ldnet.utility.PagerSlidingTabStrip;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.Utility;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

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
}

