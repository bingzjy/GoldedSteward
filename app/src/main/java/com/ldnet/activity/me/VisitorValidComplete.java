package com.ldnet.activity.me;

import android.Manifest;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;
import com.ldnet.activity.BindingHouse;
import com.ldnet.activity.FragmentHome;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.view.SlideDateTimeListener;
import com.ldnet.view.SlideDateTimePicker;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.alipay.sdk.authjs.a.c;
import static com.ldnet.utility.Services.CLASS_FROM;
import static com.ldnet.utility.Services.COMMUNITY_ID;
import static com.ldnet.utility.Services.COMMUNITY_NAME;
import static com.ldnet.utility.Services.OWNER_FLAG;
import static com.ldnet.utility.Services.OWNER_ID;
import static com.ldnet.utility.Services.OWNER_TEL;
import static com.ldnet.utility.Services.RESIDENT_DATE_END;
import static com.ldnet.utility.Services.RESIDENT_DATE_START;
import static com.ldnet.utility.Services.RESIDENT_TYPE;
import static com.ldnet.utility.Services.ROOM_ID;
import static com.ldnet.utility.Services.ROOM_NAME;
import static com.ldnet.utility.Services.TO_APPLY;
import static com.unionpay.mobile.android.global.a.F;
import static com.unionpay.mobile.android.global.a.H;

/**
 * Created by lee on 2017/4/25.
 *
 * 亲属、租户关系选择
 */
public class VisitorValidComplete extends BaseActionBarFragmentActivity implements View.OnClickListener {

    // 标题
    private TextView mTvPageTitle, tv_cname, tv_rname;
    // 完成
    private Button bt_valid_complete_visitor;
    // 返回
    private ImageButton mBtnBack;
    private EditText et_valid_start_date;
    private EditText et_weekend_start_time;
    private EditText et_valid_end_date;
    private EditText et_weekend_end_time;
    private RadioGroup radioGroup;
    private RadioButton qinshuRadioButton;
    private RadioButton zuhuRadioButton;
    private LinearLayout ll_date;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Calendar calendar = Calendar.getInstance();
    private String room_id, room_name = "";
    private String owner_phone = "";
    private String id = "";
    private String flag = "";
    private String class_from = "";
    private String community_id, community_name = "";
    private String applyType = "";
    private int residentType;
    private Date endDateClick, startDateClick;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valid_complete);
        initView();
        intiEvent();
    }

    public void initView() {
        // 标题
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        tv_cname = (TextView) findViewById(R.id.tv_cname);
        tv_rname = (TextView) findViewById(R.id.tv_rname);
        mTvPageTitle.setText("认证");
        // 返回
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        et_valid_start_date = (EditText) findViewById(R.id.et_valid_start_date);
        et_valid_end_date = (EditText) findViewById(R.id.et_valid_end_date);
        bt_valid_complete_visitor = (Button) findViewById(R.id.bt_valid_complete_visitor);
        //获取实例
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupID);
        qinshuRadioButton = (RadioButton) findViewById(R.id.qinshuID);
        zuhuRadioButton = (RadioButton) findViewById(R.id.zuhuID);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        //默认设置(关系默认家属)
        qinshuRadioButton.setChecked(true);
        residentType = 1;
        ll_date.setVisibility(View.GONE);
        //设置监听
        radioGroup.setOnCheckedChangeListener(new RadioGroupListener());
        //获取参数
        owner_phone = getIntent().getStringExtra(OWNER_TEL);
        id = getIntent().getStringExtra(OWNER_ID);
        flag = getIntent().getStringExtra(OWNER_FLAG);

        room_id = getIntent().getStringExtra(ROOM_ID);
        room_name = getIntent().getStringExtra(ROOM_NAME);
        class_from = getIntent().getStringExtra(CLASS_FROM);
        community_id = getIntent().getStringExtra(COMMUNITY_ID);
        community_name = getIntent().getStringExtra(COMMUNITY_NAME);
        applyType = getIntent().getStringExtra(TO_APPLY);
        //设置小区名
        tv_cname.setText(community_name);
        tv_rname.setText(room_name);
    }

    public void intiEvent() {
        mBtnBack.setOnClickListener(this);
        et_valid_start_date.setOnClickListener(this);
        et_valid_end_date.setOnClickListener(this);
        bt_valid_complete_visitor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                backEvent();
                break;
            case R.id.et_valid_start_date:
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .setIs24HourTime(true)
                        .setMinDate(new Date())
                        .build()
                        .show();
                break;
            case R.id.et_weekend_start_time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        //更新EditText控件时间 小于10加0
                        et_weekend_start_time.setText(new StringBuilder().append(hour < 10 ? "0" + hour : hour)
                                .append(":").append(minute < 10 ? "0" + minute : minute).append(":00"));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                break;
            case R.id.et_valid_end_date:
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener1)
                        .setInitialDate(new Date())
                        .setIs24HourTime(true)
                        .setMinDate(new Date())
                        .build()
                        .show();
                break;
            case R.id.et_weekend_end_time:
                //更新EditText控件时间 小于10加0
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {

                        et_weekend_end_time.setText(new StringBuilder().append(hour < 10 ? "0" + hour : hour)
                                .append(":").append(minute < 10 ? "0" + minute : minute).append(":00"));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                break;
            case R.id.bt_valid_complete_visitor:
                //如果是租户身份，则必选日期
                if (residentType == 2 && notNull() || residentType == 1) {

                    HashMap<String, String> extra = new HashMap<>();
                    extra.put(ROOM_ID, room_id);
                    extra.put(ROOM_NAME, room_name);
                    extra.put(COMMUNITY_ID, community_id);
                    extra.put(COMMUNITY_NAME, community_name);
                    extra.put(RESIDENT_TYPE, String.valueOf(residentType));
                    extra.put(RESIDENT_DATE_START, residentType == 1 ? "" : et_valid_start_date.getText().toString().trim());
                    extra.put(RESIDENT_DATE_END, residentType == 1 ? "" : et_valid_end_date.getText().toString().trim());
                    extra.put(CLASS_FROM, class_from);
                    extra.put(TO_APPLY, applyType == null ? "" : applyType);
                    try {
                        gotoActivity(VisitorPsd.class.getName(), extra);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            startDateClick = date;
            et_valid_start_date.setText(mFormatter.format(date));
        }
    };

    private SlideDateTimeListener listener1 = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            endDateClick = date;
            et_valid_end_date.setText(mFormatter.format(date));
        }
    };

    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == qinshuRadioButton.getId()) {
                residentType = 1;
                Log.d("asdsdasd", residentType + "");
                ll_date.setVisibility(View.GONE);
            } else if (checkedId == zuhuRadioButton.getId()) {
                residentType = 2;
                Log.d("asdsdasd", residentType + "");
                ll_date.setVisibility(View.VISIBLE);
            }
        }
    }

    // 监听返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            backEvent();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        endDateClick = null;
        startDateClick = null;
        et_valid_start_date.setText("");
        et_valid_end_date.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        endDateClick = null;
        startDateClick = null;
    }

    //判断用户输入
    private boolean notNull() {
        if (startDateClick == null || endDateClick == null ||
                TextUtils.isEmpty(et_valid_start_date.getText().toString()) ||
                TextUtils.isEmpty(et_valid_end_date.getText().toString())) {

            showToast(getString(R.string.select_bind_date));
            return false;

        } else if (startDateClick != null && endDateClick != null && startDateClick.getTime() > endDateClick.getTime()) {
            showToast(getString(R.string.start_date_invalid));
            return false;
        }
        return true;
    }


    //返回事件
    private void backEvent() {
        try {
            if (!TextUtils.isEmpty(class_from) && class_from.equals(MainActivity.class.getName())) {  //返回首頁
                gotoActivityAndFinish(MainActivity.class.getName(), null);
            } else if (!TextUtils.isEmpty(class_from) && class_from.equals(BindingHouse.class.getName())) {   //返回綁定房屋
                HashMap<String, String> extra = new HashMap<>();
                extra.put(COMMUNITY_ID, community_id);
                extra.put(COMMUNITY_NAME, community_name);
                gotoActivityAndFinish(BindingHouse.class.getName(), extra);
            } else {                                                                                      //返回我的小区
                gotoActivityAndFinish(Community.class.getName(), null);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
