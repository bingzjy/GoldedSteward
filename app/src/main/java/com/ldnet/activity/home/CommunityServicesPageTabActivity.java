package com.ldnet.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.entities.CommunityServicesModel;
import com.ldnet.entities.YellowPageSort;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.CommunityService;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by zxs on 2016/3/30.
 * 黄页
 */
public class CommunityServicesPageTabActivity extends BaseActionBarFragmentActivity implements View.OnClickListener {
    private String mYellowPageSortID;
    private String mYellowPageSortTitle;
    private List<CommunityServicesModel> mYellowPageSorts;
    private Services service;
    private TextView tv_main_title;
    private ImageView btn_back;
    private CommunityService communityService;

    // 黄页的标签
    private PagerSlidingTabStrip mYellowPageTabs;
    private ViewPager mYellowPagePager;
    private Integer mCurrentIndex = 0;
    private FragmentPagerAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局
        setContentView(R.layout.activity_yellowpage_tab);
        service = new Services();
        communityService=new CommunityService(this);

        initView();
        showProgressDialog();
        communityService.getYellowPageSortById(handler);
    }

    private void initView(){
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText("周边惠");
        btn_back = (ImageView) findViewById(R.id.btn_back);
        mYellowPagePager = (ViewPager) findViewById(R.id.pager);
        mYellowPageTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        Utility.setTabsValue(mYellowPageTabs,this);
        btn_back.setOnClickListener(this);

        adapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public CharSequence getPageTitle(int position) {
                return mYellowPageSorts.get(position).getName();
            }

            @Override
            public int getCount() {
                return mYellowPageSorts == null ? 0 : mYellowPageSorts.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                return super.instantiateItem(container, position);
            }

            @Override
            public Fragment getItem(int position) {
                Bundle b = new Bundle();
                b.putString("Id", mYellowPageSorts.get(position).getId());
                b.putString("Name", mYellowPageSorts.get(position).getName());
                return CommunityServices.getInstance(b);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
                finish();
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
                    //所有类型
                    mYellowPageSorts =(List<CommunityServicesModel>) msg.obj;
                    mYellowPageSorts=Utility.reverseModelList(mYellowPageSorts);//添加所有优惠
                    adapter.notifyDataSetChanged();
                    mYellowPagePager.setAdapter(adapter);
                    mYellowPageTabs.setViewPager(mYellowPagePager);
                    mYellowPagePager.setCurrentItem(mCurrentIndex);
                    btn_back.setOnClickListener(CommunityServicesPageTabActivity.this);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };



}
