package com.ldnet.activity.homeInspectionManage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldnet.activity.adapter.MainPagerAdapter;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.goldensteward.R;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainHomeInspectionActivity extends BaseActionBarFragmentActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.tv_custom)
    TextView tvCustom;
    @BindView(R.id.tv_handleing)
    TextView tvHandleing;
    @BindView(R.id.tv_tag1)
    TextView tvTag1;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.tv_tag2)
    TextView tvTag2;
    @BindView(R.id.iv_share)
    ImageView ivAddException;
    @BindView(R.id.viewpager_inspection)
    ViewPager viewPager;

    private MainPagerAdapter adapter;
    private InspectExceptionCompletedFragment completedFragment;
    private InspectExceptionHandleingFragment handleingFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentManager manager;
    private final String COMPLETED_KEY = "completed";
    private final String HANDLEING_KEY = "handleing";
    private String[] titles = {"进行中", "已完成"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_inspection_notice);
        ButterKnife.bind(this);

        manager = getSupportFragmentManager();
        initView();
        initFrgament(savedInstanceState);
        initEvent();
    }

    private void initView() {
        tvPageTitle.setText(getString(R.string.inpspect_notice_title));
        tvCustom.setText(getString(R.string.notice));
        tvCustom.setVisibility(View.VISIBLE);
        ivAddException.setVisibility(View.VISIBLE);
        ivAddException.setImageResource(R.drawable.add_green_icon);
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "房屋验收-主页：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "房屋验收-主页：" + this.getClass().getSimpleName());
    }


    private void initFrgament(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            completedFragment = (InspectExceptionCompletedFragment) manager.getFragment(savedInstanceState, COMPLETED_KEY);
            handleingFragment = (InspectExceptionHandleingFragment) manager.getFragment(savedInstanceState, HANDLEING_KEY);
        } else {
            completedFragment = InspectExceptionCompletedFragment.newInstance();
            handleingFragment = InspectExceptionHandleingFragment.newInstance();
        }
        fragmentList.clear();
        fragmentList.add(completedFragment);
        fragmentList.add(handleingFragment);
    }

    private void initEvent() {

        adapter = new MainPagerAdapter(getSupportFragmentManager(), this, titles, fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tvComplete.setTextColor(Color.parseColor("#4A4A4A"));
                    tvHandleing.setTextColor(Color.parseColor("#1FB79F"));
                    tvTag1.setBackgroundResource(R.color.green);
                    tvTag2.setBackgroundResource(R.color.white);
                } else {
                    tvComplete.setTextColor(Color.parseColor("#1FB79F"));
                    tvHandleing.setTextColor(Color.parseColor("#4A4A4A"));
                    tvTag1.setBackgroundResource(R.color.white);
                    tvTag2.setBackgroundResource(R.color.green);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @OnClick({R.id.btn_back, R.id.tv_custom, R.id.iv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_custom:
                Intent intent = new Intent(MainHomeInspectionActivity.this, NoticeHomeInspectionActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_share:
                Intent intent2 = new Intent(MainHomeInspectionActivity.this, CreateInspectExceptionActivity.class);
                startActivity(intent2);
                break;

        }
    }

}
