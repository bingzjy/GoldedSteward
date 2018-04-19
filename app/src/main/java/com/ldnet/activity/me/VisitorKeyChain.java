package com.ldnet.activity.me;

import android.app.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.dh.bluelock.pub.BlueLockPub;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.commen.Services;
import com.ldnet.entities.KeyChain;
import com.ldnet.goldensteward.R;
//import com.tencent.mm.sdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.view.dialog.BottomDialog;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.ldnet.activity.commen.Services.COMMUNITY_ID;
import static com.ldnet.activity.commen.Services.ROOM_ID;

/**
 * Created by lee on 2017/4/26.
 *
 * 获取到访客密码
 */
public class VisitorKeyChain extends BaseActionBarActivity {

    // 标题
    private TextView mTvPageTitle, tv_keychain_date, tv_keychain_psd,tvSharePass;
    // 完成
    private Button bt_key_chain_confirm;
    // 返回
    private ImageButton mBtnBack;
    private EditText et_valid_start_date;
    private EditText et_weekend_start_time;
    private EditText et_valid_end_date;
    private EditText et_weekend_end_time;
    private List<KeyChain> keyChains;
    private Services mServices;
    private String Type = "";//门禁类型，0-公共门，1-单元门
    private KeyChain keyChain;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mFormatter1 = new SimpleDateFormat("HH:mm");
    private String date = "";
    private final int TIME_DIALOG1 = 1;
    private final int TIME_DIALOG2 = 2;
    private int hour, minute;
    private String sTime = "", eTime = "";
    private String cid = "";
    private String room_id = "";
    private IWXAPI api;
    private String visitorPass="";
    private Services services;
    Calendar ca;
    private String shareVistorPassUrl=mServices.mHost1+"mobile/egvisitor?cid=";

    Handler receFromServer =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== DataCallBack.DATA_SUCCESS){   //服务器请求成功，有返回data
                makePass();
            }else if (msg.what==DataCallBack.DATA_SUCCESS_OTHER||msg.what==DataCallBack.DATA_FAILURE){    //服务器请求成功，返回data为null
                showToast("密码获取失败");
                Log.e("asdsdasd", "userID" + UserInformation.getUserInfo().getUserId() + "houseid" + UserInformation.getUserInfo().getHouseId() + "cid" + UserInformation.getUserInfo().getCommunityId());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_chain);
        initView();
        initEvent();
        services = new Services();
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "访客密码：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "访客密码：" + this.getClass().getSimpleName());
    }


    public void initView() {
        mServices = new Services();
        cid = getIntent().getStringExtra(COMMUNITY_ID);
        room_id = getIntent().getStringExtra(ROOM_ID);
        // 标题
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        tv_keychain_date = (TextView) findViewById(R.id.tv_keychain_date);
        tv_keychain_psd = (TextView) findViewById(R.id.tv_keychain_psd);
        tvSharePass=(TextView)findViewById(R.id.tv_share_pass);
        mTvPageTitle.setText("访客密码");
        tvSharePass.setVisibility(View.GONE);
        // 返回
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        et_valid_start_date = (EditText) findViewById(R.id.et_valid_start_date);
        et_valid_end_date = (EditText) findViewById(R.id.et_valid_end_date);
        bt_key_chain_confirm = (Button) findViewById(R.id.bt_key_chain_confirm);
        date = mFormatter.format(new Date());
        tv_keychain_date.setText("请选择时间" + "(" + date + ")");
        ca = Calendar.getInstance();
        hour = ca.get(Calendar.HOUR_OF_DAY);
        minute = ca.get(Calendar.MINUTE);
        //  getKeyChain();

        ActionBar bar=getActionBar();
        bar.hide();
    }



    public void initEvent() {
        mBtnBack.setOnClickListener(this);
        et_valid_start_date.setOnClickListener(this);
        et_valid_end_date.setOnClickListener(this);
        bt_key_chain_confirm.setOnClickListener(this);
        tvSharePass.setOnClickListener(this);
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG1:
                return new TimePickerDialog(this, mdateListener1, hour, minute, true);
            case TIME_DIALOG2:
                return new TimePickerDialog(this, mdateListener2, hour, minute, true);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display1() {

        sTime =new StringBuilder().append(hour < 10 ? "0" + hour : hour).append(":").append(minute < 10 ? "0" + minute : minute)+"";
        et_valid_start_date.setText(sTime);

    }

    public void display2() {
        eTime=new StringBuilder().append(hour < 10 ? "0" + hour : hour).append(":").append(minute < 10 ? "0" + minute : minute)+"";
        et_valid_end_date.setText(eTime);
    }

    private TimePickerDialog.OnTimeSetListener mdateListener1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int h, int m) {
            if(ca.get(Calendar.HOUR_OF_DAY)>h){
                hour=ca.get(Calendar.HOUR_OF_DAY);
            }else if(ca.get(Calendar.HOUR_OF_DAY)==h&&ca.get(Calendar.MINUTE)>m){
                m=ca.get(Calendar.MINUTE);
            }else{
                hour = h;
                minute = m;
            }
            display1();
        }
    };

    //结束时间
    private TimePickerDialog.OnTimeSetListener mdateListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int h, int m) {
            if(ca.get(Calendar.HOUR_OF_DAY)>h){
                hour=ca.get(Calendar.HOUR_OF_DAY);
            }else if(ca.get(Calendar.HOUR_OF_DAY)==h&&ca.get(Calendar.MINUTE)>m){
                m=ca.get(Calendar.MINUTE)+1;
            }else if(m==ca.get(Calendar.MINUTE)&&h==ca.get(Calendar.HOUR_OF_DAY)){
                hour=h;
                m=ca.get(Calendar.MINUTE)+1;
            }else{
                hour = h;
                minute = m;
            }
            display2();
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            HashMap<String, String> extras = new HashMap<String, String>();
            try {
                gotoActivityAndFinish(Community.class.getName(), extras);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.bt_key_chain_confirm) {

            if (TextUtils.isEmpty(et_valid_end_date.getText())||TextUtils.isEmpty(et_valid_start_date.getText())){
                showToast(getString(R.string.select_time));
            }else{
                //生成密码
                getKeyChain();
            }

        } else if (view.getId() == R.id.et_valid_start_date) {

            showDialog(TIME_DIALOG1);
        } else if (view.getId() == R.id.et_valid_end_date) {
            showDialog(TIME_DIALOG2);
        }else if (view.getId()==R.id.tv_share_pass) {
            if( ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE
            )!= PackageManager.PERMISSION_GRANTED){
                Log.e("asdsdasd","权限未开通");
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_SETTINGS}, 1);
            }else {
                Log.e("asdsdasd","分享clicking");
                setTvSharePass();
            }
        }

    }


    //添加访客密码获取日志API/EntranceGuard/VisitorLog
    public void visitorLog() {
        String url = Services.mHost + "API/EntranceGuard/VisitorLog";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("communityId", cid);
        extras.put("residentId", UserInformation.getUserInfo().getUserId());
        extras.put("egId", room_id);
        extras.put("date", date);
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        Log.d("asdsdasd", "--" + Services.json(extras));
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("communityId", cid)
                .addParams("residentId", UserInformation.getUserInfo().getUserId())
                .addParams("egId", room_id)
                .addParams("date", date)
                .build()
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
                        Log.d("asdsdasd", "*-*-*-*****-**" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {

                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }



    //获取钥匙串
    public void getKeyChain() {
        String url = mServices.mHost + "API/EntranceGuard/KeyChain?residentId=%s&roomId=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId(),room_id);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
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
                        Log.e("asdsdasd", "访客密码生成keyChain" + s);
                        closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<KeyChain>>() {
                                    }.getType();
                                    if (jsonObject.getString("Obj") != null && !jsonObject.getString("Obj").equals("[]")) {

                                        if (keyChains!=null&&keyChains.size()>0){
                                            keyChains.clear();
                                        }
                                        keyChains = gson.fromJson(jsonObject.getString("Obj"), listType);
                                        Message message=new Message();
                                        message.what = DATA_SUCCESS;
                                        receFromServer.sendMessage(message);
                                    } else {
                                        Message message=new Message();
                                        message.what=DATA_SUCCESS_OTHER;
                                        receFromServer.sendMessage(message);
                                    }
                                } else {
                                    Message message=new Message();
                                    message.what = DATA_FAILURE;
                                    receFromServer.sendMessage(message);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 监听返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            HashMap<String, String> extras = new HashMap<String, String>();
            try {
                gotoActivityAndFinish(Community.class.getName(), extras);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1&&grantResults.length>0){
            setTvSharePass();
            Log.e("asdsdasd","用户权限开通了");
        }else{
            Log.e("asdsdasd","用户拒绝未开通");
        }
    }



    private void setTvSharePass(){
        shareVistorPassUrl=shareVistorPassUrl+UserInformation.getUserInfo().getCommunityId()+"&code="+visitorPass;
        if (!Services.netWorkConnected()){
            showToast("网络连接异常，请检查网络");
        }else{
            if (!visitorPass.equals("")){
                //设置微信分享
                Log.e("asd","访客密码url--------"+shareVistorPassUrl);
                BottomDialog dialog=new BottomDialog(this,shareVistorPassUrl,visitorPass);
                dialog.uploadImageUI(this);
            }else{
                showToast("访客密码暂无");
            }
        }

    }

    private void makePass(){
        if (et_valid_start_date.getText().toString().trim() != null && et_valid_end_date.getText().toString().trim() != null) {
            if (keyChains != null) {
                if (keyChains.size() > 0) {
                    for (int i = 0; i < keyChains.size(); i++) {

                        if (keyChains.get(i).getType().equals("0")) {
                            Type = keyChains.get(i).getType();
                            keyChain = keyChains.get(i);
                            break;
                        }
                    }
                    Date date1 = null;
                    Date date2 = null;
                    Date date3 = null;
                    try {
                        date1 = mFormatter.parse(date);
                        date2 = mFormatter1.parse(sTime);
                        date3 = mFormatter1.parse(eTime);

                        //密码有效时间，双向延长15min
                        Date date22=new Date(date2.getTime()-15*60*1000);
                        String strDate2 = mFormatter1.format(date22);

                        Date date33=new Date(date3.getTime()+15*60*1000);
                        String strDate3=mFormatter1.format(date33);


                        date2=mFormatter1.parse(strDate2);
                        date3=mFormatter1.parse(strDate3);

                        Log.e("asd",date2+"---"+date3);

                        String cmTimeSlotCode = BlueLockPub.bleLockInit(this)
                                .generateVisitCodeWithDate(keyChain.getId(), keyChain.getPassword()
                                        , Integer.parseInt(keyChain.getCommunityNo()), Integer.parseInt(keyChain.getBuildingNo())
                                        , date1, date2, date3);
                        Log.e("asdsdasd", cmTimeSlotCode + "...........");
                        if (cmTimeSlotCode != null && !cmTimeSlotCode.equals("")) {
                            tv_keychain_psd.setVisibility(View.VISIBLE);
                            visitorPass=cmTimeSlotCode;
                            tv_keychain_psd.setVisibility(View.VISIBLE);
                            tv_keychain_psd.setText("您的访客密码是:" + cmTimeSlotCode);
                            tvSharePass.setVisibility(View.VISIBLE);
                            visitorLog();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            showToast("请选择时间");
        }

    }





}
