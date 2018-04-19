package com.ldnet.activity.communityshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.ldnet.activity.adapter.MainPagerAdapter;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.listener.AppBarStateChangeListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunityShopMainActivity extends AppCompatActivity {

    @BindView(R.id.ll_show_search)
    ConstraintLayout llShowSearch;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.iv_community_shop_merchant_image)
    ImageView ivCommunityShopMerchantImage;
    @BindView(R.id.tv_community_shop_merchant_name)
    TextView tvCommunityShopMerchantName;
    @BindView(R.id.tv_community_shop_notification)
    TextView tvCommunityShopNotification;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.gridview_coupon_list)
    GridView gridviewCouponList;
    @BindView(R.id.tv_distribute_community_name)
    TextView tvDistributeCommunityName;
    @BindView(R.id.tv_money_off)
    TextView tvMoneyOff;
    @BindView(R.id.iv_community_shop_detail)
    ImageView ivCommunityShopDetail;
    @BindView(R.id.ll_shop_distribution_info)
    LinearLayout llShopDistributionInfo;
    @BindView(R.id.ll_shop_money_off_info)
    LinearLayout llShopMoneyOffInfo;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.searchview_community_shop)
    SearchView searchviewCommunityShop;
    @BindView(R.id.tabLayout_shop)
    TabLayout tabLayoutShop;
    @BindView(R.id.viewpager_community_shop_main)
    ViewPager viewPager;
    @BindView(R.id.activity_main)
    CoordinatorLayout activityMain;
    @BindView(R.id.iv_searchbar_back)
    ImageView ivSearchbarBack;
    @BindView(R.id.iv_searchbar_share)
    ImageView ivSearchbarShare;

    private MerchantFragment merchantFragment;
    private ShopGoodsFragment goodsFragment;
    private static final String MERCHANT_NAME = "merchant";
    private static final String GOODS_NAME = "goods";
    private List<Fragment> fragmentList = new ArrayList<>();
    private MainPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_shop_main);
        ButterKnife.bind(this);

        initFragment(savedInstanceState);

        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {   //折叠
                    llShopDistributionInfo.setVisibility(View.GONE);
                    llShopMoneyOffInfo.setVisibility(View.GONE);
                    gridviewCouponList.setVisibility(View.GONE);
                    llShowSearch.setVisibility(View.VISIBLE);
                } else if (state == State.EXPANDED) {   //展开
                    llShopDistributionInfo.setVisibility(View.VISIBLE);
                    llShopMoneyOffInfo.setVisibility(View.VISIBLE);
                    gridviewCouponList.setVisibility(View.VISIBLE);
                    llShowSearch.setVisibility(View.GONE);
                } else {
                    llShopDistributionInfo.setVisibility(View.VISIBLE);
                    llShopMoneyOffInfo.setVisibility(View.VISIBLE);
                    gridviewCouponList.setVisibility(View.VISIBLE);
                    llShowSearch.setVisibility(View.GONE);
                }
            }
        });

        adapter = new MainPagerAdapter(getSupportFragmentManager(), CommunityShopMainActivity.this,
                new String[]{"商品", "商家"},
                fragmentList);
        viewPager.setAdapter(adapter);
        tabLayoutShop.setupWithViewPager(viewPager);
    }


    private void initFragment(Bundle bundle) {
        FragmentManager manager = getSupportFragmentManager();
        if (bundle != null) {
            goodsFragment = (ShopGoodsFragment) manager.getFragment(bundle, GOODS_NAME);
            merchantFragment = (MerchantFragment) manager.getFragment(bundle, MERCHANT_NAME);
        } else {
            goodsFragment = ShopGoodsFragment.newInstance();
            merchantFragment = MerchantFragment.newInstant();
        }
        fragmentList.clear();
        fragmentList.add(goodsFragment);
        fragmentList.add(merchantFragment);
    }


    @OnClick({R.id.iv_back, R.id.iv_share, R.id.iv_search, R.id.ll_shop_distribution_info, R.id.ll_shop_money_off_info,
            R.id.iv_searchbar_back, R.id.iv_searchbar_share, R.id.searchview_community_shop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.iv_searchbar_back:
                finish();
                break;
            case R.id.iv_share:    //分享
            case R.id.iv_searchbar_share: //搜索栏分享


                break;
            case R.id.iv_search:  //跳转到搜索
            case R.id.searchview_community_shop:
                startActivity(new Intent(this, SearchShopActivity.class));
                break;
            case R.id.ll_shop_distribution_info: //商家详情信息
            case R.id.ll_shop_money_off_info:
                startActivity(new Intent(this, ShopInfoActivity.class));
                break;
        }
    }

}
