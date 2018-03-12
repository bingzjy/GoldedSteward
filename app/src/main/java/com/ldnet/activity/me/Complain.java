package com.ldnet.activity.me;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zxs on 2016/5/12.
 */
public class Complain extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Button btn_me_feedback;
    private EditText et_me_feedback;
    private Services services;
    private String mOrderId;
    private String obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        AppUtils.setupUI(findViewById(R.id.ll_complain),this);

        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText("投诉");

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_me_feedback = (Button) findViewById(R.id.btn_me_feedback);
        et_me_feedback = (EditText) findViewById(R.id.et_me_feedback);
        mOrderId = getIntent().getStringExtra("ORDER_ID");
        et_me_feedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Services.isWithinScopeOfLength(editable.toString(), 1, 500)) {
                    btn_me_feedback.setEnabled(true);
                } else {
                    btn_me_feedback.setEnabled(false);
                }
            }
        });

        //初始化服务
        services = new Services();
        initEvent();
    }

    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_me_feedback.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "投诉：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "投诉：" + this.getClass().getSimpleName());
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回订单
                finish();
                break;
            case R.id.btn_me_feedback://提交商家投诉
                CreateComplaint(mOrderId, et_me_feedback.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    // POST BComplaint/APP_CreateComplaint
    // 提交订单投诉
    public void CreateComplaint(String orderID, String reason) {
        try {
            // 请求的URL
            String url = Services.mHost + "BComplaint/APP_CreateComplaint";
            JSONObject subOrderInfos = new JSONObject();
            subOrderInfos.put("ORDERID", orderID);
            subOrderInfos.put("RID", UserInformation.getUserInfo().getUserId());
            subOrderInfos.put("REASON", reason);
            subOrderInfos.put("LXR", UserInformation.getUserInfo().getUserName());
            subOrderInfos.put("LXDH", UserInformation.getUserInfo().getUserPhone());
            String aa = Services.timeFormat();
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + subOrderInfos.toString() + Services.TOKEN;
            OkHttpUtils.post().url(url)
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32(md5))
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addParams("str", subOrderInfos.toString())
                    .build()
                    .execute(new DataCallBack(this) {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            super.onError(call,e,i);
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            Log.d("asdsdasd", "111111111" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (json.getBoolean("Status")) {
                                    if (jsonObject.getBoolean("Valid")) {
                                        obj = jsonObject.getString("Obj");
                                        if (obj.equals("true")) {
                                            showToast("投诉成功");
                                        } else {
                                            showToast("投诉失败");
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
