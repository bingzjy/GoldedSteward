package com.ldnet.activity.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.adapter.FeeListViewAdapter;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Fees;
import com.ldnet.entities.lstAPPFees;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lee on 2016/7/12.
 */
public class PropertyFeeConfirm extends BaseActionBarActivity implements View.OnClickListener {

    private RelativeLayout ll_top;
    private TextView tv_page_title;
    private ImageButton btn_back;
    private ExpandableListView exlv_property_fees;
    private TextView tv_fee_sum;
    private TextView tv_fee_pay;
    private FeeListViewAdapter adapter;
    private List<Fees> mDatas;
    private List<Fees> fees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_fee_confirm);
        findView();
    }

    public void findView() {
        ll_top = (RelativeLayout) findViewById(R.id.ll_top);
        tv_page_title = (TextView) ll_top.findViewById(R.id.tv_page_title);
        tv_page_title.setText("确定物业费订单");
        btn_back = (ImageButton) ll_top.findViewById(R.id.btn_back);
        tv_fee_pay = (TextView) findViewById(R.id.tv_fee_pay);
        tv_fee_sum = (TextView) findViewById(R.id.tv_fee_sum);
        tv_fee_sum.setText("共0元");
        exlv_property_fees = (ExpandableListView) findViewById(R.id.exlv_property_fees);
        btn_back.setOnClickListener(this);
        tv_fee_pay.setOnClickListener(this);
        mDatas = new ArrayList<Fees>();
        Fees(null, null, null, "0");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                break;
            case R.id.tv_fee_pay:
                //检查用户选择
                String ids = "";
                for (Fees f : mDatas) {
                    if (f.IsChecked != null && f.IsChecked) {
                        for (lstAPPFees laf : f.lstAPPFees) {
                            if (!laf.Status) {
                                if (!TextUtils.isEmpty(ids)) {
                                    ids += "," + laf.ID;
                                } else {
                                    ids = laf.ID;
                                }
                            }
                        }
                    }
                }
                //判断用户选择，并提交到缴费的页面
                if (!TextUtils.isEmpty(ids)) {
                    GetTnByFeesIds(ids, UserInformation.getUserInfo().UserId);
                } else {
                    showToast(getString(R.string.go_paid_none));
                }
                break;
            default:
                break;
        }
    }

    //获取物业费列表
    public void Fees(String year, String month, String status, final String type1) {
        if (TextUtils.isEmpty(year)) {
            year = "0";
        }
        if (TextUtils.isEmpty(month)) {
            month = "0";
        }
        if (TextUtils.isEmpty(status)) {
            status = "2";
        }
        // 请求的URL
        String url = Services.mHost + "API/Fee/GetFeeByRoomID/%s?year=%s&month=%s&status=%s";
        url = String.format(url, UserInformation.getUserInfo().HouseId, year, month, status);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                .execute(new DataCallBack(this) {

                             @Override
                             public void onBefore(Request request, int id) {
                                 super.onBefore(request, id);
                                 showProgressDialog();
                             }

                             @Override
                             public void onError(Call call, Exception e, int i) {
                                 super.onError(call, e, i);
                                 closeProgressDialog();
                             }

                             @Override
                             public void onResponse(String s, int i) {
                                 super.onResponse(s, i);
                                 Log.e("fee","initData:"+s);
                                 closeProgressDialog();
                                 try {
                                     JSONObject json = new JSONObject(s);
                                     JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                     if (json.getBoolean("Status")) {
                                         if (jsonObject.getBoolean("Valid")) {
                                             Gson gson = new Gson();
                                             Type type = new TypeToken<List<Fees>>() {
                                             }.getType();
                                             if (jsonObject.getString("Obj").equals("[]")) {
                                                 showToast("暂时没有数据");
                                                 return;
                                             }
                                             fees = gson.fromJson(jsonObject.getString("Obj"), type);
                                             mDatas.clear();
                                             for (int j = 0; j < fees.size(); j++) {
                                                 for (int z = 0; z < fees.get(j).lstAPPFees.size(); z++) {
                                                     if (!fees.get(j).lstAPPFees.get(z).getStatus()) {
                                                         mDatas.add(fees.get(j));
                                                     }
                                                 }
                                             }
//                                             adapter = new FeeListViewAdapter(PropertyFeeConfirm.this, removeDuplicate1(mDatas), tv_fee_sum);
                                             exlv_property_fees.setAdapter(adapter);
                                         }


                                     }
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                             }
                         }
                );
    }


    public static List<Fees> removeDuplicate1(List<Fees> list) {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    public void GetTnByFeesIds(String feesIds, String payerId) {
        // 请求的URL
        String url = Services.mHost + "API/Fee/GetTNByFeesIds_CountMerter/%s?payerId=%s";
        url = String.format(url, feesIds, payerId);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        showProgressDialog();
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd", "111111111" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            if (json.getBoolean("Status")) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("Obj"));
                                    String tn = jsonObject1.getString("TN");
                                    if (!TextUtils.isEmpty(tn)) {
                                        doStartUnionPayPlugin(PropertyFeeConfirm.this, tn, "00");
                                    }
                                }
                            } else {
                                showToast("调取银联页面失败，请重试");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 调用银联
    public void doStartUnionPayPlugin(Activity activity, String tn, String serverMode) {
       // UPPayAssistEx.startPay(activity, null, null, tn, serverMode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        final String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
//         builder.setCustomTitle();
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Fees(null, null, null, "1");
            }
        });
        builder.create().show();
    }
}
