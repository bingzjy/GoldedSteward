package com.ldnet.activity.communityshop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldnet.activity.base.BaseFragment;
import com.ldnet.goldensteward.R;


public class ShopGoodsFragment extends BaseFragment {

    private static final String TAG = "CommunityShopGoodsFragment";

    public ShopGoodsFragment() {

    }


    public static ShopGoodsFragment newInstance() {
        return new ShopGoodsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateVIew()");
        View view = inflater.inflate(R.layout.fragment_community_shop_goods, container, false);
        return view;

    }

}
