package com.ldnet.activity.accessmanage;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.CommunityRoomInfo;
import com.ldnet.entities.MyProperties;
import com.ldnet.entities.Rooms;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AccessControlService;
import com.ldnet.service.BaseService;
import com.ldnet.service.CommunityService;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.tendcloud.tenddata.TCAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.ldnet.utility.Utility.backgroundAlpaha;

public class AddVisitorInviteActivity extends BaseActionBarActivity implements RadioGroup.OnCheckedChangeListener {

    private EditText editTextName, editTextTel, editTextCarNo, editTextOtherReason;
    private RadioGroup radioGroupIsDriving;
    private RadioButton rbIsDriving;
    private TextView spinnerReaason, spinnerCommunity;
    private Button btnSubmit;
    private ImageButton imageButtonBack;
    private TextView textViewTitle, tvCarTitle, tvOtherTitle, tvDate;
    private ListViewAdapter<String> reasonAdapter;
    private ListViewAdapter<CommunityRoomInfo> communityAdapter;
    private List<CommunityRoomInfo> communityList = new ArrayList<>();

    private String paramsReason;
    private String paramsIsDriving;
    private String paramsID;
    private CommunityRoomInfo selectRoom;
    private String paramsName;
    private String paramsTel;
    private String paramsCarNo;
    private String paramsDate;
    private String paramsCommunityId, paramsRoomId, paramsRoomName;

    private boolean other;

    private AccessControlService service;
    private CommunityService communityService;

    private String tag = AddVisitorInviteActivity.class.getSimpleName();
    private SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visitor);

        service = new AccessControlService(AddVisitorInviteActivity.this);
        communityService = new CommunityService(AddVisitorInviteActivity.this);

        initView();

        communityService.getMyCommunity(getCommunityHandler);
    }

    //初始化布局
    private void initView() {
        getActionBar().hide();

        editTextCarNo = (EditText) findViewById(R.id.ed_add_invite_visitor_car_no);
        tvDate = (TextView) findViewById(R.id.ed_add_invite_visitor_date);
        editTextName = (EditText) findViewById(R.id.ed_add_invite_visitor_name);
        editTextTel = (EditText) findViewById(R.id.ed_add_invite_visitor_tel);
        editTextOtherReason = (EditText) findViewById(R.id.ed_add_invite_visitor_other);
        spinnerCommunity = (TextView) findViewById(R.id.spinner_add_invite_visitor_community);
        spinnerReaason = (TextView) findViewById(R.id.spinner_add_invite_visitor_reason);
        textViewTitle = (TextView) findViewById(R.id.tv_page_title);
        imageButtonBack = (ImageButton) findViewById(R.id.btn_back);
        btnSubmit = (Button) findViewById(R.id.ed_add_invite_visitor_submit);
        radioGroupIsDriving = (RadioGroup) findViewById(R.id.rg_is_driving);
        rbIsDriving = (RadioButton) findViewById(R.id.radio_button_is_driving);
        tvCarTitle=(TextView)findViewById(R.id.ed_add_invite_visitor_car_no_title);
        tvOtherTitle = (TextView) findViewById(R.id.ed_add_invite_visitor_other_title);
        textViewTitle.setText("添加访客");

        //设置默认值
        tvDate.setText(mformat.format(new Date()));
        paramsCommunityId = UserInformation.getUserInfo().getCommunityId();
        paramsRoomId = UserInformation.getUserInfo().getHouseId();
        paramsRoomName = UserInformation.getUserInfo().getHouseName();

        spinnerCommunity.setText(UserInformation.getUserInfo().getHouseName());
        spinnerReaason.setText(Arrays.asList(getResources().getStringArray(R.array.invite_visitor_reason)).get(0));


        radioGroupIsDriving.setOnCheckedChangeListener(this);
        imageButtonBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        spinnerCommunity.setOnClickListener(this);
        spinnerReaason.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.ed_add_invite_visitor_submit:
                submit();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.ed_add_invite_visitor_date:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(AddVisitorInviteActivity.this, listener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(new Date().getTime()-1000);
                dialog.show();
                break;
            case R.id.spinner_add_invite_visitor_reason:
                showReasonPop();
                break;
            case R.id.spinner_add_invite_visitor_community:
                if(communityList!=null&&communityList.size()>0){
                    showCommunityPop();
                }else{
                    showToast("请绑定小区和房间");
                }

                break;
        }
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int ar, int month, int dayOfMonth) {
            String date;
            month += 1;
            if (month < 10) {
                date = ar + "-0" + month + "-" + dayOfMonth;
            } else {
                date = ar + "-" + month + "-" + dayOfMonth;
            }
            tvDate.setText(date);
        }
    };


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_button_not_driving) {
            editTextCarNo.setVisibility(View.GONE);
            tvCarTitle.setVisibility(View.GONE);
            paramsIsDriving = "false";
        } else if (checkedId == R.id.radio_button_is_driving) {
            editTextCarNo.setVisibility(View.VISIBLE);
            tvCarTitle.setVisibility(View.VISIBLE);
            paramsIsDriving = "true";
        }
    }

    //提交数据
    private void submit() {
        if (!Utility.editIsNull(editTextName)) {       //姓名

            paramsName = editTextName.getText().toString().trim();

            if (!Utility.editIsNull(editTextTel)) {     // 电话

                if (Utility.isPhone(editTextTel.getText().toString().trim())) {   //验证手机号格式

                    paramsTel = editTextTel.getText().toString().trim();

                    if (rbIsDriving.isChecked() && Utility.editIsNull(editTextCarNo)) {    //车牌号

                        showToast("请输入汽车牌照");

                    } else if (!rbIsDriving.isChecked() || (rbIsDriving.isChecked() && !Utility.editIsNull(editTextCarNo))) {

                        //获取车牌号参数
                        if (rbIsDriving.isChecked() && !Utility.editIsNull(editTextCarNo)) {
                            paramsCarNo = editTextCarNo.getText().toString();
                        } else {
                            paramsCarNo = "";
                        }

                        if (other && Utility.editIsNull(editTextOtherReason)) {      //来访事由

                            showToast("请输入其他来访事由");

                        } else if (!other || (other == true && !Utility.editIsNull(editTextOtherReason))) {

                            if (!Utility.editIsNull(editTextOtherReason)) {
                                paramsReason = editTextOtherReason.getText().toString();
                            }
                            if (!other){
                                paramsReason=spinnerReaason.getText().toString();
                            }
                            //可提交
                            paramsID = Utility.generateGUID();
                            service.addVisitorAccess(paramsID, paramsName, paramsTel, tvDate.getText() + " 00:00:00",
                                    paramsReason, paramsIsDriving, paramsCarNo, paramsCommunityId, paramsRoomId, paramsRoomName, addRecordHandler);
                    }
                }
                } else {
                    showToast("手机号码输入有误");
                }
            } else {
                showToast("请输入电话");
            }
        } else {
            showToast("请输入姓名");
        }
    }


    Handler addRecordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    Toast.makeText(AddVisitorInviteActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    try {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("IMAGE_ID", paramsID);
                        params.put("FROM_CLASS", AddVisitorInviteActivity.class.getName());
                        params.put("DATE", tvDate.getText().toString());
                        params.put("TEL", paramsTel);
                        params.put("NAME", paramsName);
                        params.put("STATUS", "0");
                        params.put("ROOM",paramsRoomName);
                        gotoActivity(VisitorCardActivity.class.getName(), params);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case BaseService.DATA_REQUEST_ERROR:
                    case BaseService.DATA_FAILURE:
                        showToast(msg.obj.toString());
                        break;
            }
        }
    };


    Handler getCommunityHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    List<MyProperties> list = (List<MyProperties>) msg.obj;
                    communityList = getNewCommunity(list);

                    communityAdapter = new ListViewAdapter<CommunityRoomInfo>(AddVisitorInviteActivity.this, R.layout.item_drop_down, communityList) {
                        @Override
                        public void convert(ViewHolder holder, CommunityRoomInfo communityRoomInfo) {
                            holder.setText(R.id.tv_community_room, communityRoomInfo.getCommunityName() + " " + communityRoomInfo.getRoomName());
                        }
                    };
                    communityAdapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast("请绑定小区和房间");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //过滤出当前用户所有的房子信息
    public static List<CommunityRoomInfo> getNewCommunity(List<MyProperties> list) {
        List<CommunityRoomInfo> communityList = new ArrayList<>();

        for (MyProperties myProperties : list) {

            List<Rooms> roomsList = myProperties.getRooms();

            if (roomsList != null && roomsList.size() > 0) {

                for (Rooms rooms : roomsList) {
                    CommunityRoomInfo info = new CommunityRoomInfo(myProperties.getCommunityId(), myProperties.getName(), rooms.RoomId, rooms.getAbbreviation());
                    communityList.add(info);
                }
            }
        }
        return communityList;
    }

    //房间选择弹出框
    private void showCommunityPop(){
        LayoutInflater layoutInflater = LayoutInflater.from(AddVisitorInviteActivity.this);
        View popupView = layoutInflater.inflate(R.layout.pop_property_telphone, null);
        final PopupWindow mPopWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(popupView);
        View rootview = layoutInflater.inflate(R.layout.main, null);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
        mPopWindow.setAnimationStyle(R.anim.slide_in_from_bottom);
        TextView title = (TextView) popupView.findViewById(R.id.poptitle);
        title.setVisibility(View.GONE);

        LinearLayout llCancel=(LinearLayout) popupView.findViewById(R.id.cancel_call);
        llCancel.setVisibility(View.GONE);

        ListView listView = (ListView) popupView.findViewById(R.id.list_propert_telphone);
        //进出小区选择
        communityAdapter = new ListViewAdapter<CommunityRoomInfo>(AddVisitorInviteActivity.this, R.layout.item_drop_down, communityList) {
            @Override
            public void convert(ViewHolder holder, CommunityRoomInfo communityRoomInfo) {
                holder.setText(R.id.tv_community_room, communityRoomInfo.getCommunityName() + " " + communityRoomInfo.getRoomName());
            }
        };

        listView.setAdapter(communityAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CommunityRoomInfo selectRoom=communityList.get(position);
                if (selectRoom!=null){
                    paramsCommunityId = selectRoom.getCommunityID();
                    paramsRoomId = selectRoom.getRoomID();
                    paramsRoomName = selectRoom.getRoomName();

                    spinnerCommunity.setText(paramsRoomName);
                }

                mPopWindow.dismiss();
            }
        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha(AddVisitorInviteActivity.this, 1f);
            }
        });
        backgroundAlpaha(AddVisitorInviteActivity.this, 0.5f);
    }

    //原因弹出框
    private void showReasonPop(){
        final List<String> data=Arrays.asList(getResources().getStringArray(R.array.invite_visitor_reason));

        LayoutInflater layoutInflater = LayoutInflater.from(AddVisitorInviteActivity.this);
        View popupView = layoutInflater.inflate(R.layout.pop_property_telphone, null);
        final PopupWindow mPopWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(popupView);
        View rootview = layoutInflater.inflate(R.layout.main, null);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER, 10, 0);
        mPopWindow.setAnimationStyle(R.anim.slide_in_from_bottom);
        TextView title = (TextView) popupView.findViewById(R.id.poptitle);
        title.setVisibility(View.GONE);

        LinearLayout llCancel=(LinearLayout) popupView.findViewById(R.id.cancel_call);
        llCancel.setVisibility(View.GONE);

        ListView listView = (ListView) popupView.findViewById(R.id.list_propert_telphone);
        reasonAdapter = new ListViewAdapter<String>(AddVisitorInviteActivity.this, R.layout.item_drop_down,
                data) {
            @Override
            public void convert(ViewHolder holder, String s) {
                holder.setText(R.id.tv_community_room, s);

            }
        };

        listView.setAdapter(reasonAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                paramsReason = data.get(position);
                spinnerReaason.setText(paramsReason);
                if (paramsReason.equals("其他")) {
                    other = true;
                    tvOtherTitle.setVisibility(View.VISIBLE);
                    editTextOtherReason.setVisibility(View.VISIBLE);
                } else {
                    other = false;
                    tvOtherTitle.setVisibility(View.GONE);
                    editTextOtherReason.setVisibility(View.GONE);
                }
                mPopWindow.dismiss();
            }
        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha(AddVisitorInviteActivity.this, 1f);
            }
        });
        backgroundAlpaha(AddVisitorInviteActivity.this, 0.5f);
    }


    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "添加访客邀请:" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "添加访客邀请:" + this.getClass().getSimpleName());
    }
}
