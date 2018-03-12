package com.ldnet.activity.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.HouseRent;
import com.ldnet.entities.MyProperties;
import com.ldnet.entities.PPhones;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class Property_Telephone extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private ListView lv_property_telephone;
    private List<PPhones> mDatas;
    private ListViewAdapter<PPhones> mAdapter;

    //初始化视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_property_telephone);
        //初始化控件

        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.property_services_telephone);

        //初始化服务
        lv_property_telephone = (ListView) findViewById(R.id.lv_property_telephone);
        PTelephones();
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        initEvent();
    }

    //获取物业电话列表
    public void PTelephones() {
        // 请求的URL
        String url = Services.mHost + "Api/Property/GetCommonTel/%s";
        url = String.format(url, UserInformation.getUserInfo().getCommunityId());
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
                        super.onError(call,e,i);
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        closeProgressDialog();
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<PPhones>>() {
                                    }.getType();
                                    mDatas = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    if (mDatas != null && mDatas.size() > 0) {
                                        mAdapter = new ListViewAdapter<PPhones>(Property_Telephone.this, R.layout.item_telephone, mDatas) {
                                            @Override
                                            public void convert(ViewHolder holder, final PPhones phones) {
                                                holder.setText(R.id.tv_title, phones.Title).setText(R.id.tv_telephone, phones.Tel);
                                                ImageButton telephone = holder.getView(R.id.ibtn_telephone);
                                                telephone.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phones.Tel));
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
                                                    }
                                                });
                                            }
                                        };
                                        lv_property_telephone.setAdapter(mAdapter);
                                    } else {
                                        showToast(R.string.Property_does_not_provide_phone_call);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_back:
                    gotoActivityAndFinish(Property_Services.class.getName(), null);
                    break;
                default:
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                gotoActivityAndFinish(Property_Services.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "物业服务-物业电话" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "物业服务-物业电话" + this.getClass().getSimpleName());
    }
}
