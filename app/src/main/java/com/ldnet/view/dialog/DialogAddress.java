package com.ldnet.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.entities.AddressSimple;
import com.ldnet.entities.Stock;
import com.ldnet.goldensteward.R;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.ViewHolder;
import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Alex on 2015/9/28.
 */
public class DialogAddress extends Dialog {
    private List<Stock> mStocks;
    private String mAddressId;
    private Context mContext;
    private Services services;
    private OnAddressDialogListener goodsDialogListener;

    private ListViewAdapter mAdapter;
    private List<AddressSimple> mDatas;
    private ListView lv_address_select;
    private Button dialog_button_comfirm;

    //构造函数
    public DialogAddress(Context context, String addressId, OnAddressDialogListener customDialogListener) {
        super(context, R.style.dialog_fullscreen);
        mContext = context;
        //商品信息
        mAddressId = addressId;
        //服务--获取规格
        services = new Services();
        //确定按钮响应事件
        this.goodsDialogListener = customDialogListener;
    }

    //onCreate方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_address);

        //地址列表
        lv_address_select = (ListView) findViewById(R.id.lv_address_select);
        Addresses();
        //地址选择确定按钮
        dialog_button_comfirm = (Button) findViewById(R.id.dialog_button_comfirm);
        dialog_button_comfirm.setOnClickListener(clickListener);
    }

    //按钮事件监听
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goodsDialogListener.Confirm(mAddressId);
            DialogAddress.this.dismiss();
        }
    };

    //定义回调事件
    public interface OnAddressDialogListener {
        void Confirm(String addressId);
    }

    //取消Dialog
    private void closeDialog() {
        this.cancel();
    }

    //获取收货地址列表
    public void Addresses() {
        // 请求的URL
        String url = Services.mHost + "DeliveryAddress/APP_GetAddressSimpleList?ResidentID=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId());
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
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<AddressSimple>>() {
                                    }.getType();
                                    mDatas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    mAdapter = new ListViewAdapter<AddressSimple>(mContext, R.layout.item_select_address, mDatas) {
                                        @Override
                                        public void convert(ViewHolder holder, final AddressSimple addressSimple) {
                                            holder.setText(R.id.tv_me_address_title, addressSimple.AD)
                                                    .setText(R.id.tv_me_address_zipcode, addressSimple.ZC)
                                                    .setText(R.id.tv_me_address_name, addressSimple.NP);

                                            //选择按钮
                                            CheckBox checkBox = holder.getView(R.id.chk_address_checked);
                                            checkBox.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Boolean chkd = ((CheckBox) view).isChecked();
                                                    if (chkd) {
                                                        addressSimple.IsChecked = chkd;
                                                        mAddressId = addressSimple.ID;
                                                        for (AddressSimple as : mDatas) {
                                                            if (as.ID.equals(addressSimple.ID)) {
                                                                as.IsChecked = false;
                                                            }
                                                        }
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                        }
                                    };
                                    lv_address_select.setAdapter(mAdapter);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
