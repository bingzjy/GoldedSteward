package com.ldnet.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.*;

import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.activity.commen.Services;
import com.ldnet.activity.me.Community;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.view.customview.BadgeView;
import com.tendcloud.tenddata.TCAgent;
import com.third.listviewshangxia.XListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Property_Repair extends BaseActionBarFragmentActivity implements View.OnClickListener {
    private TextView tv_main_title, tv_charge,tv_text;
    private ImageButton btn_back;
    private ImageButton btn_create_repair;
    private ViewPager mViewPager;
    private TextView tv_repair_houseinfo;
    private ImageButton ibtn_repair_change_house;
    private XListView lv_home_repairs;
    private PopupWindow popupWindow;
    private ListViewAdapter adapter;
    private ListView listView;
    /**
     * 顶部三个LinearLayout
     */
    private LinearLayout ll_zc;//早餐
    private LinearLayout ll_dc;//晚餐
    /**
     * 顶部的三个TextView
     */
    private TextView tv_zc;
    private TextView tv_wc;
    private TextView tv_dc;
    /**
     * 分别为每个TabIndicator创建一个BadgeView
     */
    private BadgeView zc;
    private BadgeView wc;
    private BadgeView dc;
    private int currentIndex;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    //初始化视图
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_property_repair);
        //初始化控件
        initView();
        initEvent();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // 重置所有TextView的字体颜色
                resetTextView();
                switch (position) {
                    case 0:
                        ll_zc.removeView(zc);
                        ll_zc.addView(zc);
                        tv_zc.setTextColor(getResources().getColor(R.color.white));
                        ll_zc.setBackgroundResource(R.drawable.sharp_rect_green);
                        break;
                    case 1:
                        tv_dc.setTextColor(getResources().getColor(R.color.white));
                        ll_dc.setBackgroundResource(R.drawable.sharp_rect_green);
                        break;
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                /**
                 * 利用position和currentIndex判断用户的操作是哪一页往哪一页滑动
                 * 然后改变根据positionOffset动态改变TabLine的leftMargin
                 */
                /**
                 * currentIndex:当前行 ;positionOffsetPixels: 位置偏移像素;
                 * positionOffset:位置偏移
                 */
                System.out.println("\n" + "currentIndex:" + currentIndex + "\n"
                        + "position:" + position + "\n" + "positionOffset:"
                        + positionOffset + "\n" + "positionOffsetPixels:"
                        + positionOffsetPixels + "\n");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 重置颜色
     */
    protected void resetTextView() {
        tv_zc.setTextColor(getResources().getColor(R.color.gray_deep));
        tv_dc.setTextColor(getResources().getColor(R.color.gray_deep));
        ll_zc.setBackgroundResource(R.drawable.sharp_rect_white);
        ll_dc.setBackgroundResource(R.drawable.sharp_rect_white);
    }

    /**
     * 初始化控件，初始化Fragment
     */
    private void initView() {
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.property_services_repair);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_charge = (TextView) findViewById(R.id.tv_charge);
        User user = UserInformation.getUserInfo();
        tv_repair_houseinfo = (TextView) findViewById(R.id.tv_repair_houseinfo);
        tv_repair_houseinfo.setText(user.CommuntiyName + "(" + user.HouseName + ")");
        ibtn_repair_change_house = (ImageButton) findViewById(R.id.ibtn_repair_change_house);
        //现在报修按钮
        btn_create_repair = (ImageButton) findViewById(R.id.btn_custom);
        btn_create_repair.setImageResource(R.drawable.plus);
        btn_create_repair.setVisibility(View.VISIBLE);
        mViewPager = (ViewPager) findViewById(R.id.id_vp);
        ll_zc = (LinearLayout) findViewById(R.id.ll_zc);
        ll_dc = (LinearLayout) findViewById(R.id.ll_dc);

        tv_zc = (TextView) findViewById(R.id.tv_zc);
        tv_dc = (TextView) findViewById(R.id.tv_dc);

        RepairIngFragment tab01 = new RepairIngFragment();
        RepairCompletedFragment tab03 = new RepairCompletedFragment();
        mFragments.add(tab01);
        mFragments.add(tab03);

        zc = new BadgeView(this);
        dc = new BadgeView(this);
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_create_repair.setOnClickListener(this);
        ibtn_repair_change_house.setOnClickListener(this);
        tv_charge.setOnClickListener(this);
        /**
         * 设置顶部三个标签页点击事件
         */
        ll_zc.setOnClickListener(this);
        ll_dc.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    Services.comment = "";
                    gotoActivityAndFinish(Property_Services.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_custom:
                try {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("LEFT", "LEFT");
                    gotoActivityAndFinish(Property_Repair_Create.class.getName(), extras);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ibtn_repair_change_house:
                try {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("NOT_FROM_ME", "101");
                    extras.put("LEFT", "LEFT");
                    gotoActivityAndFinish(Community.class.getName(), extras);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_zc:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.ll_dc:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.tv_charge:
                Intent intent = new Intent(this,Property_Repair_Fee.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                Services.comment = "";
                gotoActivityAndFinish(Property_Services.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "物业服务-报修主页" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "物业服务-报修主页" + this.getClass().getSimpleName());
    }
}
