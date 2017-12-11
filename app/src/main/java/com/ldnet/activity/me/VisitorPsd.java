package com.ldnet.activity.me;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.BindingHouse;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.EntranceGuard;
import com.ldnet.entities.PPhones;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lee on 2017/4/24.
 * 获取业主密码
 */
public class VisitorPsd extends BaseActionBarActivity {

    // 标题
    private TextView mTvPageTitle,mTvVistiorChange;
    // 下一步
    private Button bt_next_visitor;
    // 返回
    private ImageButton mBtnBack,imgTel;
    private TextView tvNoOwner;
    private LinearLayout ll_noOwner,ll_haveOwner;
    private EditText et_visitor_phone;
    private Services mServices;
    private String romm_id = "";
    private String phoneWY = "";
    private String id,phone = "";
    private String flag = "";
    private List<EntranceGuard> entranceGuards;
    private String class_from = "";
    private String COMMUNITY_ID,mCOMMUNITY_NAME = "";
    private String applyType="";
    private boolean noOwner=false;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private ListViewAdapter<PPhones> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = new Services();
        // 设置布局
        setContentView(R.layout.activity_visitor_psd);
        AppUtils.setupUI(findViewById(R.id.ll_visitor_psd), this);

        initView();
        initEvent();
    }

    public void initView() {
        // 标题
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        mTvPageTitle.setText("验证业主");
        // 返回
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mTvVistiorChange=(TextView)findViewById(R.id.tv_vistior_change);
        et_visitor_phone = (EditText) findViewById(R.id.et_visitor_phone);
        bt_next_visitor = (Button) findViewById(R.id.bt_next_visitor);
        ll_noOwner=(LinearLayout)findViewById(R.id.ll_no_owner);
        ll_haveOwner=(LinearLayout)findViewById(R.id.ll_have_owner);
        imgTel=(ImageButton)findViewById(R.id.img_tel);
        tvNoOwner=(TextView)findViewById(R.id.tv_no_owner);

        romm_id = getIntent().getStringExtra("ROOM_ID");
        phoneWY = getIntent().getStringExtra("phone"); //物业电话
        class_from = getIntent().getStringExtra("CLASS_FROM");
        COMMUNITY_ID = getIntent().getStringExtra("COMMUNITY_ID");
        mCOMMUNITY_NAME=getIntent().getStringExtra("COMMUNITY_NAME");
        applyType=getIntent().getStringExtra("APPLY");
        ll_noOwner.setVisibility(View.VISIBLE);
        ll_haveOwner.setVisibility(View.VISIBLE);

        showProgressDialog();
        getEntranceGuard(romm_id);
    }

    // 初始化事件
    public void initEvent() {
        // 点击事件监听
        mBtnBack.setOnClickListener(this);
        bt_next_visitor.setOnClickListener(this);
      //  ll_noOwner.setOnClickListener(this);
        tvNoOwner.setOnClickListener(this);
        imgTel.setOnClickListener(this);
    }


    //获取房屋的业主列表
    public void getEntranceGuard(String roomId) {
        String url = Services.mHost + "API/EntranceGuard/RoomOwners?roomId=" + roomId;
        url = String.format(url);
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
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        closeProgressDialog();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        closeProgressDialog();
                        Log.e("asdsdasd", "getEntranceGuard=" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            Log.e("asdsdasd", "getEntranceGuard=" + jsonObject.getString("Obj"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    if (jsonObject.getString("Obj") != null && !jsonObject.getString("Obj").equals("[]")) {
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<EntranceGuard>>() {
                                        }.getType();
                                        entranceGuards = gson.fromJson(jsonObject.getString("Obj"), type);
                                        et_visitor_phone.setEnabled(true);
                                        bt_next_visitor.setVisibility(View.VISIBLE);

                                       // ll_noOwner.setVisibility(View.GONE);
                                        ll_haveOwner.setVisibility(View.VISIBLE);
                                    } else {
                                        ll_noOwner.setVisibility(View.VISIBLE);
                                        ll_haveOwner.setVisibility(View.GONE);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 点击事件处理
    @Override
    public void onClick(View v) {

        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_back:
                if(class_from!=null&&!class_from.equals("")&&!("").equals(applyType)){
                    HashMap<String, String> extras = new HashMap<String, String>();
                    try {
                        gotoActivityAndFinish(Community.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }else if (class_from != null && !class_from.equals("")&&("").equals(applyType)) {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("COMMUNITY_ID", COMMUNITY_ID);
                    extras.put("IsFromRegister", "false");
                    try {
                        gotoActivityAndFinish(BindingHouse.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if(class_from != null && !class_from.equals("")&&noOwner==true){
                    finish();
                }else {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    try {
                        gotoActivityAndFinish(Community.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.bt_next_visitor:
                if (entranceGuards != null&&entranceGuards.size() > 0) {
                    Log.e("asdsdasd", "1000");
                    if (!et_visitor_phone.getText().toString().trim().equals("")) {
                        Log.e("asdsdasd", "1111" +et_visitor_phone.getText().toString().trim() );
                        for (int i = 0; i < entranceGuards.size(); i++) {
                            String str = entranceGuards.get(i).getValue().substring(entranceGuards.get(i).getValue().length() - 4, entranceGuards.get(i).getValue().length());
                            if (str.equals(et_visitor_phone.getText().toString().trim())) {
                                phone = entranceGuards.get(i).getValue();
                                id = entranceGuards.get(i).getId();
                                flag = entranceGuards.get(i).getFlag();
                                break;
                            }
                        }
                        Log.e("asdsdasd", "2222" +phone+"--"+id+"---"+flag );
                        if (phone != null && !phone.equals("")) {
                            HashMap<String, String> extras1 = new HashMap<String, String>();
                            extras1.put("Id", id);
                            extras1.put("Value", phone);
                            extras1.put("Flag", flag);
                            extras1.put("ROOM_ID", romm_id);
                            extras1.put("APPLY",applyType==null?"":applyType);
                            if (class_from != null && !class_from.equals("")) {
                                extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                                extras1.put("CLASS_FROM", "BindingHouse");
                                extras1.put("COMMUNITY_NAME",mCOMMUNITY_NAME==null?"":mCOMMUNITY_NAME);
                            }
                            try {
                                Log.e("asdsdasd", "4444" +phone+"--"+id+"---"+flag +"COMMUNITY_NAME"+mCOMMUNITY_NAME);

                                gotoActivity(VisitorValid.class.getName(), extras1);
                                //gotoActivityAndFinish(VisitorValid.class.getName(), extras1);
                                Log.e("asdsdasd", "5555" +phone+"--"+id+"---"+flag );
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("yanzheng","et_visitor_phone:"+et_visitor_phone.getText().toString()+"----"+romm_id);
                            showToast("您的输入有误");
                        }
                    } else {
                        showToast("您的输入不可为空");
                    }
                } else {
                    getPropertyTelphone();
                }
                break;
            case R.id.img_tel:
                getPropertyTelphone();
                break;
            case R.id.tv_no_owner:
                getPropertyTelphone();
                break;
            default:
                break;
        }
    }

    // 监听返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(class_from!=null&&!class_from.equals("")&&!("").equals(applyType)){
                HashMap<String, String> extras = new HashMap<String, String>();
                try {
                    gotoActivityAndFinish(Community.class.getName(), extras);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }else if (class_from != null && !class_from.equals("")&&("").equals(applyType)) {
                HashMap<String, String> extras = new HashMap<String, String>();
                extras.put("COMMUNITY_ID", COMMUNITY_ID);
                extras.put("IsFromRegister", "false");
                try {
                    gotoActivityAndFinish(BindingHouse.class.getName(), extras);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if(class_from != null && !class_from.equals("")&&noOwner==true){
                finish();
            }else {
                HashMap<String, String> extras = new HashMap<String, String>();
                try {
                    gotoActivityAndFinish(Community.class.getName(), extras);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }






    //获取物业联系电话
    private void getPropertyTelphone(){
        String url = Services.mHost + "Api/Property/GetCommonTel/%s";
        url = String.format(url, COMMUNITY_ID==null?UserInformation.getUserInfo().getCommunityId():COMMUNITY_ID);
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
                                    List<PPhones> mDatas = gson.fromJson(jsonObject.getString("Obj"),
                                            listType);
                                    List<PPhones> newDatas=new ArrayList<PPhones>();
                                    if (mDatas != null && mDatas.size() > 0) {
                                        for(int t=0;t<mDatas.size();t++){
                                            if(mDatas.get(t).getTitle().equals("物业管理处电话")){
                                               newDatas.add(mDatas.get(t));
                                            }
                                        }
                                        if(newDatas.size()==0){
                                            showCallPop(mDatas);
                                        }else{
                                            showCallPop(newDatas);
                                        }
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



    private void showCallPop(List<PPhones> phonesList){

        LayoutInflater layoutInflater = LayoutInflater.from(VisitorPsd.this);
        View popupView = layoutInflater.inflate(R.layout.pop_property_telphone, null);
        final PopupWindow mPopWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(popupView);
        View rootview =layoutInflater.inflate(R.layout.main, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        mPopWindow.setAnimationStyle(R.anim.slide_in_from_bottom);

        TextView title=(TextView)popupView.findViewById(R.id.poptitle);
        title.setText(getResources().getText(R.string.noPrpperty));
        ListView listTelPhone=(ListView)popupView.findViewById(R.id.list_propert_telphone);
        mAdapter = new ListViewAdapter<PPhones>(VisitorPsd.this, R.layout.item_telephone, phonesList) {
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
        listTelPhone.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        popupView.findViewById(R.id.cancel_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopWindow.setAnimationStyle(R.anim.slide_out_to_bottom);
                mPopWindow.dismiss();
                backgroundAlpaha(VisitorPsd.this,1.0f);
            }
        });
        backgroundAlpaha(VisitorPsd.this,0.5f);
    }


    public void backgroundAlpaha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

}
