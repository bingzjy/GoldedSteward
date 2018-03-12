package com.ldnet.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.MyFrPagerAdapter;
import com.ldnet.utility.PagerSlidingTabStrip;
import com.ldnet.utility.Utility;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;

public class OrdersTabActivity extends BaseActionBarFragmentActivity implements View.OnClickListener{
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private TextView tv_main_title;
    private ImageButton btn_back;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tab);
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_orders);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(OrdersTabActivity.this);

        final ArrayList<String> titles = new ArrayList<String>();
        titles.add("待付款");
        titles.add("待发货");
        titles.add("待收货");
        titles.add("已完成");
        titles.add("已取消");
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        for (String s : titles) {
            Bundle bundle = new Bundle();
            bundle.putString("title", s);
            fragments.add(OrdersFragmentContent.getInstance(bundle));
        }
        pager.setAdapter(new MyFrPagerAdapter(getSupportFragmentManager(), titles, fragments));
        tabs.setViewPager(pager);
        //默认第一组选中
        pager.setCurrentItem(0);
        Utility.setTabsValue(tabs,OrdersTabActivity.this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "订单-主页：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "订单-主页：" + this.getClass().getSimpleName());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
