package com.ldnet.activity.communityshop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.view.dialog.CustomAlertDialog;
import com.ldnet.activity.base.BaseFragment;
import com.ldnet.activity.yellowpage.YellowPages_Map;
import com.ldnet.goldensteward.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MerchantFragment extends BaseFragment {

    @BindView(R.id.tv_address_name)
    TextView tvAddressName;
    @BindView(R.id.ll_merchant_address)
    LinearLayout llMerchantAddress;
    @BindView(R.id.tv_telephone)
    TextView tvTelephone;
    @BindView(R.id.ll_merchant_tel)
    LinearLayout llMerchantTel;
    @BindView(R.id.ll_merchant_business_qualification)
    LinearLayout llMerchantBusinessQualification;
    @BindView(R.id.tv_distribution_address)
    TextView tvDistributionAddress;
    @BindView(R.id.ll_merchant_distribution_address)
    LinearLayout llMerchantDistributionAddress;
    @BindView(R.id.tv_distribution_time)
    TextView tvDistributionTime;
    @BindView(R.id.tv_notification_content)
    TextView tvNotificationContent;
    Unbinder unbinder;

    private CustomAlertDialog dialog;
    private boolean permissGranted;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final String TAG = "CommunityShopMerchantFragment";

    public MerchantFragment() {

    }

    public static MerchantFragment newInstant() {
        return new MerchantFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateVIew()");
        View view = inflater.inflate(R.layout.fragment_community_shop_merchant, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.ll_merchant_address, R.id.ll_merchant_tel, R.id.ll_merchant_business_qualification})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_merchant_address: {
                if (!TextUtils.isEmpty("") && !TextUtils.isEmpty("")) {

                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("LATITUDE", "");
                    extras.put("LONGITUDE", "");
                    extras.put("LEFT", "LEFT");

                    if (requestPermission()) {
                        try {
                            gotoActivity(YellowPages_Map.class.getName(), extras);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "请手动开启定位权限", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showToast(R.string.position_on);
                }
            }
            break;
            case R.id.ll_merchant_tel:
                dialog = new CustomAlertDialog(getActivity(), true, getResources().getString(R.string.dialog_shortcut_title), getString(R.string.dialog_confirm));
                dialog.show();
                dialog.setDialogCallback(dialogcallback);
                break;
            case R.id.ll_merchant_business_qualification:
                Intent intent = new Intent(getActivity(), BussinessQualificationActivity.class);
                startActivity(intent);
                break;
        }
    }


    //弹出对话框
    CustomAlertDialog.Dialogcallback dialogcallback = new CustomAlertDialog.Dialogcallback() {
        @Override
        public void dialogdo() {
            //拨打电话
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"));
            startActivity(intent);
            dialog.dismiss();
        }

        @Override
        public void dialogDismiss() {

        }
    };


    //动态申请权限
    private boolean requestPermission() {
        permissGranted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getActivity(), permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(getActivity(), permissions, 321);
            } else {
                permissGranted = true;
            }
        } else {
            permissGranted = true;
        }
        return permissGranted;
    }


    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    boolean noRemaind = shouldShowRequestPermissionRationale(permissions[1]);
                    if (!noRemaind) {
                        Toast.makeText(getActivity(), "请手动开启定位权限", Toast.LENGTH_LONG).show();
                    }
                } else {
                    permissGranted = true;
                }
            }
        }
    }

}
