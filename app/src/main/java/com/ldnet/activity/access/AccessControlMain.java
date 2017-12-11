package com.ldnet.activity.access;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.activity.MainActivity;
import com.ldnet.activity.adapter.MainPagerAdapter;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AccessControlService;
import com.ldnet.service.BaseService;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AccessControlMain extends FragmentActivity implements View.OnClickListener {
    private ViewPager passViewPager;
    private TextView title, titleVisitor, titleGoods, tvVisitor, tvGoods;
    private ImageButton back;
    private GoodsRecordFragment goodsRecordFragment;
    private VisitorRecordFragment visitorRecordFragment;
    private final String GOODS_FRAGMENT = "goods_fragment";
    private final String Visitor_FRAGMENT = "visitor_fragment";
    private FragmentManager manager = getSupportFragmentManager();
    private List<Fragment> fragmentList = new ArrayList<>();
    private MainPagerAdapter adapter;
    private AccessControlService service;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //两次Back退出
    private long exitTime = 0;
    private Services services;
    private static final String TAG = "AccessControlMain";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_exit_main);

        service = new AccessControlService(AccessControlMain.this);
        services = new Services();

        initFragment(savedInstanceState);
        initView();
        initEvent();
    }

    private void initView() {
        passViewPager = (ViewPager) findViewById(R.id.viewpager_entry_exit);
        title = (TextView) findViewById(R.id.tv_page_title);
        back = (ImageButton) findViewById(R.id.btn_back);
        titleVisitor = (TextView) findViewById(R.id.tv_title_visitor);
        titleGoods = (TextView) findViewById(R.id.tv_title_goods);
        tvVisitor = (TextView) findViewById(R.id.tab_bar_visitor);
        tvGoods = (TextView) findViewById(R.id.tab_bar_goods);
        title.setText("出入管理");

        adapter = new MainPagerAdapter(getSupportFragmentManager(),
                AccessControlMain.this,
                new String[]{"访客记录", "物品出入"},
                fragmentList);
        passViewPager.setAdapter(adapter);
    }


    private void initEvent() {

        back.setOnClickListener(this);
        titleGoods.setOnClickListener(this);
        titleVisitor.setOnClickListener(this);
        tvVisitor.setOnClickListener(this);
        tvGoods.setOnClickListener(this);

        passViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    titleVisitor.setTextColor(Color.parseColor("#1FB79F"));
                    titleGoods.setTextColor(Color.parseColor("#4A4A4A"));
                    tvVisitor.setBackgroundResource(R.color.green);
                    tvGoods.setBackgroundResource(R.color.bg_gray);
                } else {
                    titleVisitor.setTextColor(Color.parseColor("#4A4A4A"));
                    titleGoods.setTextColor(Color.parseColor("#1FB79F"));
                    tvVisitor.setBackgroundResource(R.color.bg_gray);
                    tvGoods.setBackgroundResource(R.color.green);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void initFragment(Bundle bundle) {
        FragmentManager manager = getSupportFragmentManager();
        if (bundle != null) {
            goodsRecordFragment = (GoodsRecordFragment) manager.getFragment(bundle, GOODS_FRAGMENT);
            visitorRecordFragment = (VisitorRecordFragment) manager.getFragment(bundle, Visitor_FRAGMENT);
        } else {
            visitorRecordFragment = VisitorRecordFragment.newInstance();
            goodsRecordFragment = GoodsRecordFragment.newInstance();
        }

        fragmentList.clear();
        fragmentList.add(visitorRecordFragment);
        fragmentList.add(goodsRecordFragment);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                Intent intent1 = new Intent(AccessControlMain.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
            case R.id.btn_add_invite_visitor:
                Intent intent = new Intent(this, AddVisitorInviteActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_title_visitor:
                passViewPager.setCurrentItem(0);
                break;
            case R.id.tv_title_goods:
                passViewPager.setCurrentItem(1);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent1 = new Intent(AccessControlMain.this, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent1);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FragmentManager manager = getSupportFragmentManager();
        manager.putFragment(savedInstanceState, GOODS_FRAGMENT, goodsRecordFragment);
        manager.putFragment(savedInstanceState, Visitor_FRAGMENT, visitorRecordFragment);
    }

}
