package com.ldnet.activity.bindmanage;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.autonavi.rtbt.IFrameForRTBT;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.CommunityRoomInfo;
import com.ldnet.entities.OwnerRoom;
import com.ldnet.entities.OwnerRoomRelation;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.HouseRelationService;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.tendcloud.tenddata.TCAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ldnet.utility.Utility.backgroundAlpaha;
import static com.ldnet.utility.Utility.isNull;

public class AddRelationActivity extends BaseActionBarActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.et_add_relation_name)
    EditText etAddRelationName;
    @BindView(R.id.et_add_relation_tel)
    EditText etAddRelationTel;
    @BindView(R.id.radio_button_family)
    RadioButton radioButtonFamily;
    @BindView(R.id.radio_button_resident)
    RadioButton radioButtonResident;
    @BindView(R.id.rg_relation_type)
    RadioGroup rgRelationType;
    @BindView(R.id.ed_add_invite_visitor_car_no_title)
    TextView edAddInviteVisitorCarNoTitle;
    @BindView(R.id.tv_add_relation_comunity)
    TextView tvAddRelationComunity;
    @BindView(R.id.et_add_resident_date_start)
    TextView etAddResidentDateStart;
    @BindView(R.id.et_add_resident_date_end)
    TextView etAddResidentDateEnd;
    @BindView(R.id.btn_add_relation_submit)
    Button btnAddRelationSubmit;
    @BindView(R.id.ll_add_relation_check_date)
    LinearLayout llAddRelationCheckDate;

    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    private ListViewAdapter<OwnerRoom> communityAdapter;
    private String paramsRoomId, paramsRoomName, paramsResiType = "2", paramsName, paramsTel, paramsDates = "", paramsDatee ="";
    private List<OwnerRoom> roomList = new ArrayList<>();
    private HouseRelationService relationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relation);
        getActionBar().hide();
        ButterKnife.bind(this);
        relationService = new HouseRelationService(this);
        relationService.getOwnerRooms(handlerGetOwnerRoom);
        showProgressDialog();
        initView();
    }

    private void initView() {
        tvPageTitle.setText(getString(R.string.add_relation));

        rgRelationType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.radio_button_resident){   //租户
                    llAddRelationCheckDate.setVisibility(View.VISIBLE);
                    paramsResiType = "2";
                }else{                                        //亲属
                    llAddRelationCheckDate.setVisibility(View.GONE);
                    paramsResiType = "1";
                }
            }
        });

        paramsDates = mFormat.format(new Date());
        paramsDatee = mFormat.format(new Date());

    }


    @OnClick({R.id.btn_back, R.id.et_add_resident_date_start, R.id.et_add_resident_date_end, R.id.btn_add_relation_submit, R.id.tv_add_relation_comunity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.et_add_resident_date_start:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(AddRelationActivity.this, listener1,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(new Date().getTime() - 1000);
                dialog.show();
                break;
            case R.id.et_add_resident_date_end:
                Calendar calendar2 = Calendar.getInstance();
                DatePickerDialog dialog2 = new DatePickerDialog(AddRelationActivity.this, listener2,
                        calendar2.get(Calendar.YEAR),
                        calendar2.get(Calendar.MONTH),
                        calendar2.get(Calendar.DAY_OF_MONTH));
                dialog2.getDatePicker().setMinDate(new Date().getTime() - 1000);
                dialog2.show();
                break;
            case R.id.btn_add_relation_submit:
                if (isNotNull()) {
                    paramsName = etAddRelationName.getText().toString().trim();
                    paramsTel = etAddRelationTel.getText().toString().trim();
                    addRelation();
                }
                break;
            case R.id.tv_add_relation_comunity:
                showRoomPop();
                break;
        }
    }

    private boolean isNotNull() {
        if (Utility.editIsNull(etAddRelationName)) {
            showToast("请输入姓名");
            return false;
        }
        if (Utility.editIsNull(etAddRelationTel)) {
            showToast("请输入电话");
            return false;
        }

        if (!Utility.isPhone(etAddRelationTel.getText().toString().trim())) {
            showToast("请输入有效电话");
            return false;
        }

        if (paramsResiType.equals("2") && (TextUtils.isEmpty(etAddResidentDateStart.getText()) || TextUtils.isEmpty(etAddResidentDateEnd.getText()))) {
            showToast("请选择日期");
            return false;
        }

        return true;
    }

    //添加关系
    private void addRelation() {
        relationService.addMyRoomBindRelation(paramsName, paramsTel, paramsResiType, paramsRoomId, paramsDates, paramsDatee, handlerAdd);
    }


    Handler handlerAdd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast("添加成功");
                    finish();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //获取户主身份的所有房子
    Handler handlerGetOwnerRoom = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    roomList = (List<OwnerRoom>) msg.obj;
                    paramsRoomName=roomList.get(0).Abbreviation;
                    paramsRoomId=roomList.get(0).Id;
                    tvAddRelationComunity.setText(paramsRoomName);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast("暂无业主身份的房子，请绑定房间后再添加");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //显示房屋选择
    private void showRoomPop() {
        LayoutInflater layoutInflater = LayoutInflater.from(AddRelationActivity.this);
        View popupView = layoutInflater.inflate(R.layout.pop_property_telphone, null);
        final PopupWindow mPopWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(popupView);
        View rootview = layoutInflater.inflate(R.layout.main, null);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
        mPopWindow.setAnimationStyle(R.anim.slide_in_from_bottom);
        TextView title = (TextView) popupView.findViewById(R.id.poptitle);
        title.setVisibility(View.GONE);

        LinearLayout llCancel = (LinearLayout) popupView.findViewById(R.id.cancel_call);
        llCancel.setVisibility(View.GONE);

        ListView listView = (ListView) popupView.findViewById(R.id.list_propert_telphone);
        //进出小区选择
        communityAdapter = new ListViewAdapter<OwnerRoom>(AddRelationActivity.this, R.layout.item_drop_down, roomList) {
            @Override
            public void convert(ViewHolder holder, OwnerRoom roomRelation) {
                holder.setText(R.id.tv_community_room, roomRelation.Abbreviation);
            }
        };

        listView.setAdapter(communityAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                paramsRoomId = roomList.get(position).Id;
                paramsRoomName = roomList.get(position).Abbreviation;
                tvAddRelationComunity.setText(paramsRoomName);

                mPopWindow.dismiss();
            }
        });

        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha(AddRelationActivity.this, 1f);
            }
        });
        backgroundAlpaha(AddRelationActivity.this, 0.5f);
    }


    private DatePickerDialog.OnDateSetListener listener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int ar, int month, int dayOfMonth) {
            String date;
            month += 1;
            if (month < 10) {
                date = ar + "-0" + month + "-" + dayOfMonth;
            } else {
                date = ar + "-" + month + "-" + dayOfMonth;
            }
            paramsDates = date;
            etAddResidentDateStart.setText(paramsDates);
        }
    };


    private DatePickerDialog.OnDateSetListener listener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int ar, int month, int dayOfMonth) {
            String date;
            month += 1;
            if (month < 10) {
                date = ar + "-0" + month + "-" + dayOfMonth;
            } else {
                date = ar + "-" + month + "-" + dayOfMonth;
            }

            paramsDatee = date;
            etAddResidentDateEnd.setText(paramsDatee);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "门禁管理-添加绑定关系：" + this.getClass().getSimpleName());

    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "门禁管理-添加绑定关系：" + this.getClass().getSimpleName());
    }
}
