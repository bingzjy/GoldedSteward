package com.ldnet.activity.me;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldnet.activity.adapter.PublishPagerAdapter;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.entities.InfoBarType;
import com.ldnet.goldensteward.R;
import com.ldnet.view.customview.NoScrollViewPager;
import com.ldnet.view.customview.PagerSlidingTabStrip;
import com.ldnet.utility.Utility;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.activity.me
 * @class describe
 * @time 2018/1/9 17:58
 * @change
 * @chang time
 * @class describe
 */

public class PublishActivity extends BaseActionBarFragmentActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.pager_sliding_tab_strip_publish)
    PagerSlidingTabStrip pstPublish;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.viewpager_my_publish)
    NoScrollViewPager vpPublish;

    private List<InfoBarType> allType = new ArrayList<>();
    private int currentIndex = 0;
    private static final String TAG = "PublishActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_publish2);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "我的发布-主页：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "我的发布-主页：" + this.getClass().getSimpleName());
    }


    private void initView() {
        tvPageTitle.setText("我的发布");

        //0房屋租赁，1邻里通，2闲置物品，3周边游
        allType.add(new InfoBarType(0, "房屋租赁"));
        allType.add(new InfoBarType(1, "邻里通"));
        allType.add(new InfoBarType(2, "闲置物品"));
        allType.add(new InfoBarType(3, "周边游"));

        Utility.setTabsValue(pstPublish, this);
        pstPublish.setUnderlineColor(getResources().getColor(R.color.gray_back));

        vpPublish.setAdapter(new PublishPagerAdapter(getSupportFragmentManager(), allType));
        pstPublish.setViewPager(vpPublish);
        vpPublish.setCurrentItem(currentIndex);
    }


    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }


}
