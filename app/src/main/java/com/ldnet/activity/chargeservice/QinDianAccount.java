package com.ldnet.activity.chargeservice;


import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.activity.adapter.MainPagerAdapter;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.QinDianService;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lee on 2017/9/5.
 */
public class QinDianAccount extends FragmentActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private TextView tvAccountBalance, tvTitle, titleConsume, titleCharge;
    private ImageButton back;
    private MainPagerAdapter adapter;
    private ConsumeDetailFragment consumeDetailFragment;
    private ChargeMoneyDetailFragment chargeMoneyDetailFragment;
    private TextView tabBar1, tabBar2;
    private final String CONSUME_KEY = "consuem";
    private final String CHARGE_KEY = "charge";
    private LinearLayout llConsume;
    private RelativeLayout rlCharge;
    private QinDianService service;
    private List<Fragment> fragmentList=new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qindian_account);
        service = new QinDianService(this);
        initDate(savedInstanceState);
        initView();
        initEvent();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //获取账户余额
        service.getRemind(getRemindHandler);
        TCAgent.onPageStart(this, "充电服务-我的账户：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "充电服务-我的账户：" + this.getClass().getSimpleName());
    }

    void initDate(Bundle savedInstanceState) {
        FragmentManager manager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            consumeDetailFragment = (ConsumeDetailFragment) manager.getFragment(savedInstanceState, CONSUME_KEY);
            chargeMoneyDetailFragment = (ChargeMoneyDetailFragment) manager.getFragment(savedInstanceState, CHARGE_KEY);
        } else {
            consumeDetailFragment = ConsumeDetailFragment.newInstance();
            chargeMoneyDetailFragment = ChargeMoneyDetailFragment.newInstance();
        }
        fragmentList.clear();
        fragmentList.add(consumeDetailFragment);
        fragmentList.add(chargeMoneyDetailFragment);
    }


    private void initView() {
        ActionBar bar = getActionBar();
        bar.hide();

        rlCharge = (RelativeLayout) findViewById(R.id.rl_charge);
        llConsume = (LinearLayout) findViewById(R.id.ll_consume);
        viewPager = (ViewPager) findViewById(R.id.qindian_account_viewpager);
        tvAccountBalance = (TextView) findViewById(R.id.tv_account_balance);
        back = (ImageButton) findViewById(R.id.btn_back);
        titleCharge = (TextView) findViewById(R.id.title2);
        titleConsume = (TextView) findViewById(R.id.title1);

        tvTitle = (TextView) findViewById(R.id.tv_page_title);
        tvTitle.setText("我的账户");
        tabBar1 = (TextView) findViewById(R.id.tab_bar1);
        tabBar2 = (TextView) findViewById(R.id.tab_bar2);


        adapter=new MainPagerAdapter(getSupportFragmentManager(),
                QinDianAccount.this,
                new String[]{"消费记录","充值记录"},
                fragmentList);
        viewPager.setAdapter(adapter);
    }


    private void initEvent() {
        back.setOnClickListener(this);
        llConsume.setOnClickListener(this);
        rlCharge.setOnClickListener(this);
        titleCharge.setOnClickListener(this);
        titleConsume.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        titleConsume.setTextColor(Color.parseColor("#1FB79F"));
                        titleCharge.setTextColor(Color.parseColor("#4A4A4A"));
                        tabBar1.setBackgroundResource(R.color.green);
                        tabBar2.setBackgroundResource(R.color.bg_gray);
                        break;
                    case 1:
                        titleCharge.setTextColor(Color.parseColor("#1FB79F"));
                        titleConsume.setTextColor(Color.parseColor("#4A4A4A"));
                        tabBar1.setBackgroundResource(R.color.bg_gray);
                        tabBar2.setBackgroundResource(R.color.green);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.rl_charge:
                Intent intent2 = new Intent(QinDianAccount.this, ChargeMoneyActivity.class);
                startActivity(intent2);
                break;
            case R.id.title1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.title2:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    Handler getRemindHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    tvAccountBalance.setText(msg.obj.toString());
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    Toast.makeText(QinDianAccount.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager manager = getSupportFragmentManager();
        manager.putFragment(outState, CONSUME_KEY, consumeDetailFragment);
        manager.putFragment(outState, CHARGE_KEY, chargeMoneyDetailFragment);
    }
}