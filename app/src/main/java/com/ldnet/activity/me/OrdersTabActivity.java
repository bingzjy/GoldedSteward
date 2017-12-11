package com.ldnet.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.MyFrPagerAdapter;
import com.ldnet.utility.PagerSlidingTabStrip;

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
        setTabsValue();



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
                finish();
                break;
        }
    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线的颜色
        tabs.setDividerColor(getResources().getColor(R.color.green_light));
        // 设置分割线的上下的间距,传入的是dp
        tabs.setDividerPaddingTopBottom(12);
        // 设置Tab底部线的高度,传入的是dp
        tabs.setUnderlineHeight(1);
        //设置Tab底部线的颜色
        tabs.setUnderlineColor(getResources().getColor(R.color.green));

        // 设置Tab 指示器Indicator的高度,传入的是dp
        tabs.setIndicatorHeight(4);
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(getResources().getColor(R.color.green_light1));

        // 设置Tab标题文字的大小,传入的是dp
        tabs.setTextSize(16);
        // 设置选中Tab文字的颜色
        tabs.setSelectedTextColor(getResources().getColor(R.color.green));
        //设置正常Tab文字的颜色
        tabs.setTextColor(getResources().getColor(R.color.black));

        //  设置点击Tab时的背景色
//        tabs.setTabBackground(R.drawable.back_button_back);

        //是否支持动画渐变(颜色渐变和文字大小渐变)
        tabs.setFadeEnabled(true);
        // 设置最大缩放,是正常状态的0.3倍
        tabs.setZoomMax(0.1F);
        //设置Tab文字的左右间距,传入的是dp
        tabs.setTabPaddingLeftRight(24);
    }
}
