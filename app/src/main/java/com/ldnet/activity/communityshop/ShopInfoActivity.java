package com.ldnet.activity.communityshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldnet.goldensteward.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopInfoActivity extends AppCompatActivity {

    @BindView(R.id.iv_shop_info_back)
    ImageView ivBack;
    @BindView(R.id.iv_shop_info_share)
    ImageView ivShare;
    @BindView(R.id.iv_shop_info_search)
    ImageView ivSearch;
    @BindView(R.id.iv_shopinfo_merchant_logo)
    ImageView ivLogo;
    @BindView(R.id.iv_shopinfo_merchant_name)
    TextView ivName;
    @BindView(R.id.iv_shopinfo_merchant_notification_title)
    TextView ivNotificationTitle;
    @BindView(R.id.collapsing_toolbar_shop_info)
    CollapsingToolbarLayout collapsingToolbarShopInfo;
    @BindView(R.id.app_bar_shop_info)
    AppBarLayout appBarShopInfo;
    @BindView(R.id.gridview_coupon_list)
    GridView gvCouponList;
    @BindView(R.id.tv_shopinfo_distribute_community_name)
    TextView tvCommunityName;
    @BindView(R.id.tv_shopinfo_money_off)
    TextView tvMoneyOff;
    @BindView(R.id.tv_shopinfo_distribution_detail)
    TextView tvDistributionDetail;
    @BindView(R.id.tv_shopinfo_distribution_time)
    TextView tvDistributionTime;
    @BindView(R.id.tv_shopinfo_notification_detial)
    TextView tvNotificationDetial;
    @BindView(R.id.iv_close)
    ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_shop_info_back, R.id.iv_shop_info_share, R.id.iv_shop_info_search, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_shop_info_back:
                finish();
                break;
            case R.id.iv_shop_info_share:
                break;
            case R.id.iv_shop_info_search:
                startActivity(new Intent(this, SearchShopActivity.class));
                finish();
                break;
            case R.id.iv_close:
                finish();
                break;
        }
    }
}
