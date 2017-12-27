package com.ldnet.activity.me;

import android.os.*;
import android.os.Message;
import android.view.*;
import android.widget.*;
import com.ldnet.activity.BindingHouse;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.EntranceGuard;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.BindingService;
import com.ldnet.utility.*;
import java.util.*;

/**
 * Created by lee on 2017/4/24.
 * 输入业主电话后4位
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
    private String room_id = "";
    private String roomOwnerId, roomOwnerPhone = "";
    private String flag = "";
    private List<EntranceGuard> entranceGuards;
    private String class_from = "";
    private String COMMUNITY_ID,mCOMMUNITY_NAME = "";
    private String applyType="";
    private BindingService bindingService;
    private static final String TAG = "VisitorPsd";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingService = new BindingService(this);
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

        room_id = getIntent().getStringExtra("ROOM_ID");
        class_from = getIntent().getStringExtra("CLASS_FROM");
        COMMUNITY_ID = getIntent().getStringExtra("COMMUNITY_ID");
        mCOMMUNITY_NAME=getIntent().getStringExtra("COMMUNITY_NAME");
        applyType=getIntent().getStringExtra("APPLY");
        ll_noOwner.setVisibility(View.VISIBLE);
        ll_haveOwner.setVisibility(View.VISIBLE);

        showProgressDialog();
        bindingService.getEntranceGuard(room_id, handlerEntrance);

    }

    // 初始化事件
    public void initEvent() {
        // 点击事件监听
        mBtnBack.setOnClickListener(this);
        bt_next_visitor.setOnClickListener(this);
        tvNoOwner.setOnClickListener(this);
        imgTel.setOnClickListener(this);
    }

    // 点击事件处理
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    HashMap<String, String> extras = new HashMap<String, String>();

                    if (class_from != null && !class_from.equals("") && !("").equals(applyType)) {   //用于验证身份
                        gotoActivityAndFinish(Community.class.getName(), extras);

                    } else if (class_from != null && !class_from.equals("") && ("").equals(applyType)) { //用于绑定房屋
                        extras.put("COMMUNITY_ID", COMMUNITY_ID);
                        extras.put("IsFromRegister", "false");
                        gotoActivityAndFinish(BindingHouse.class.getName(), extras);

                    } else {
                        gotoActivityAndFinish(Community.class.getName(), extras);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_next_visitor:
                if (checkInputPhoneValid()) {
                    HashMap<String, String> extras1 = new HashMap<String, String>();
                    extras1.put("ROOM_OWNER_ID", roomOwnerId);
                    extras1.put("ROOM_OWNER_TEL", roomOwnerPhone);
                    extras1.put("ROOM_OWNER_FLAG", flag);
                    extras1.put("ROOM_ID", room_id);
                    extras1.put("APPLY", applyType == null ? "" : applyType);
                    if (class_from != null && !class_from.equals("")) {
                        extras1.put("COMMUNITY_ID", COMMUNITY_ID);
                        extras1.put("CLASS_FROM", "BindingHouse");
                        extras1.put("COMMUNITY_NAME", mCOMMUNITY_NAME == null ? "" : mCOMMUNITY_NAME);
                    }
                    try {
                        gotoActivity(VisitorValid.class.getName(), extras1);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.img_tel:
            case R.id.tv_no_owner:
                Utility.showCallPop(VisitorPsd.this, false);
                break;
            default:
                break;
        }
    }

    //判断输入电话是否有效
    private boolean checkInputPhoneValid() {
        boolean validTel = false;
        //业主电话列表空
        if (entranceGuards == null || entranceGuards.size() == 0) {
            Utility.showCallPop(VisitorPsd.this, true);
            return false;
        }
        //用户输入空
        if (Utility.editIsNull(et_visitor_phone)) {
            showToast(getString(R.string.input_phone_null));
            return false;
        }

        //遍历业主电话列表，检索用户输入的电话是否存在
        for (EntranceGuard entranceGuard : entranceGuards) {
            String tel = entranceGuard.getValue().substring(entranceGuard.getValue().length() - 4, entranceGuard.getValue().length());
            if (tel.equals(et_visitor_phone.getText().toString().trim())) {
                roomOwnerPhone = entranceGuard.getValue();
                roomOwnerId = entranceGuard.getId();
                flag = entranceGuard.getFlag();
                validTel = true;
                break;
            }
        }
        //检索用户输入电话失败
        if (!validTel) {
            showToast(getString(R.string.input_error));
            return false;
        }
        return true;
    }

    // 监听返回按键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            try {
                HashMap<String, String> extras = new HashMap<String, String>();

                if (class_from != null && !class_from.equals("") && !("").equals(applyType)) {   //用于验证身份
                    gotoActivityAndFinish(Community.class.getName(), extras);

                } else if (class_from != null && !class_from.equals("") && ("").equals(applyType)) { //用于绑定房屋
                    extras.put("COMMUNITY_ID", COMMUNITY_ID);
                    extras.put("IsFromRegister", "false");
                    gotoActivityAndFinish(BindingHouse.class.getName(), extras);

                } else {
                    gotoActivityAndFinish(Community.class.getName(), extras);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return super.dispatchKeyEvent(event);
    }


    //获取业主信息列表
    Handler handlerEntrance = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    entranceGuards = (List<EntranceGuard>) msg.obj;
                    et_visitor_phone.setEnabled(true);
                    bt_next_visitor.setVisibility(View.VISIBLE);
                    ll_haveOwner.setVisibility(View.VISIBLE);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    ll_noOwner.setVisibility(View.VISIBLE);
                    ll_haveOwner.setVisibility(View.GONE);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

}
