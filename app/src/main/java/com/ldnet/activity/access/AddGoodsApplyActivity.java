package com.ldnet.activity.access;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.CommunityRoomInfo;
import com.ldnet.entities.MyProperties;
//import com.ldnet.goldensteward.R;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AccessControlService;
import com.ldnet.service.BaseService;
import com.ldnet.service.CommunityService;
import com.ldnet.utility.AddPopWindow;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.tendcloud.tenddata.TCAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.ldnet.activity.access.AddVisitorInviteActivity.getNewCommunity;
import static com.ldnet.goldensteward.R.id.dismiss;
import static com.ldnet.goldensteward.R.id.ll_fee;
import static com.ldnet.goldensteward.R.id.ll_fee1;
import static com.ldnet.goldensteward.R.id.ll_not_pay;
import static com.ldnet.goldensteward.R.id.ll_pop;
import static com.ldnet.goldensteward.R.id.ll_year;
import static com.ldnet.goldensteward.R.id.ll_year1;
import static com.ldnet.goldensteward.R.id.tv_fee;
import static com.ldnet.goldensteward.R.id.tv_year;
import static com.ldnet.utility.Utility.backgroundAlpaha;

public class AddGoodsApplyActivity extends BaseActionBarActivity {

    private EditText etGoods, etOther;
    private TextView spReason, spCommunity;
    private TextView tvOtherReasonTitle, tvDate, tvHeadTitle;
    private ImageButton back;
    private Button btnSubmit;
    private ListViewAdapter<String> reasonAdapter;
    private ListViewAdapter<CommunityRoomInfo> communityAdapter;
    private List<CommunityRoomInfo> communityList = new ArrayList<>();
    private CommunityRoomInfo selectRoom;
    private String paramsReason;
    private String paramsCommunity;
    private String paramsDate;
    private String paramsGoods;
    private String paramsId;
    private String paramsCommId;
    private String paramsRoomId;
    private String paramsRoomName;
    private boolean other;
    private CommunityService communityService;
    private AccessControlService accessControlService;
    private SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods_apply);

        communityService = new CommunityService(this);
        accessControlService = new AccessControlService(this);

        initView();

        communityService.getMyCommunity(getCommunityHandler);
    }

    private void initView() {
        getActionBar().hide();

        etGoods = (EditText) findViewById(R.id.et_add_goods_list);
        etOther = (EditText) findViewById(R.id.et_add_goods_other_reason);
        spReason = (TextView) findViewById(R.id.spinner_add_good_reason);
        spCommunity = (TextView) findViewById(R.id.spinner_add_good_community);
        tvOtherReasonTitle = (TextView) findViewById(R.id.tv_add_goods_access_other_title);
        tvDate = (TextView) findViewById(R.id.tv_add_goods_date);
        back = (ImageButton) findViewById(R.id.btn_back);
        btnSubmit = (Button) findViewById(R.id.btn_add_goods_submit);
        tvHeadTitle = (TextView) findViewById(R.id.tv_page_title);
        tvHeadTitle.setText("物品出入");


        //设置默认值
        tvDate.setText(mformat.format(new Date()));
        paramsCommId = UserInformation.getUserInfo().CommunityId;
        paramsRoomId = UserInformation.getUserInfo().getHouseId();
        paramsRoomName = UserInformation.getUserInfo().getHouseName();
        tvOtherReasonTitle.setVisibility(View.GONE);
        etOther.setVisibility(View.GONE);

        spReason.setText(Arrays.asList(getResources().getStringArray(R.array.goods_access_reason)).get(0));
        spCommunity.setText(UserInformation.getUserInfo().getHouseName());



        back.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        spCommunity.setOnClickListener(this);
        spReason.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_add_goods_submit:
                submit();
                break;
            case R.id.tv_add_goods_date:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(AddGoodsApplyActivity.this, listener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(new Date().getTime() - 1000);
                dialog.show();
                break;
            case R.id.spinner_add_good_community:
                if (communityList!=null&&communityList.size()>0){
                    showCommunityPop();
                }else{
                    showToast("请绑定小区和房间");
                }
                break;
            case R.id.spinner_add_good_reason:
                showReasonPop();
                break;
        }
    }

    private void submit() {
        if (!Utility.editIsNull(etGoods)) {
            paramsGoods = etGoods.getText().toString().trim();
            if (other && Utility.editIsNull(etOther)) {
                showToast("请输入其他原因");
            } else if (!other || (other && !Utility.editIsNull(etOther))) {

                if (other && !Utility.editIsNull(etOther)) {
                    paramsReason = etOther.getText().toString().trim();
                }
                if (!other){
                    paramsReason=spReason.getText().toString();
                }
                paramsId = Utility.generateGUID();
                accessControlService.addGoodsAccess(paramsId, paramsGoods, paramsReason,
                        tvDate.getText().toString() + " 00:00:00", paramsCommId, paramsRoomId, paramsRoomName, addRecordHandler);
            }
        } else {
            showToast("请输入物品列表");
        }
    }

    Handler getCommunityHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    List<MyProperties> list = (List<MyProperties>) msg.obj;
                    communityList = getNewCommunity(list);
                    //进出小区选择
                    communityAdapter = new ListViewAdapter<CommunityRoomInfo>(AddGoodsApplyActivity.this, R.layout.item_drop_down, communityList) {
                        @Override
                        public void convert(ViewHolder holder, CommunityRoomInfo communityRoomInfo) {
                            holder.setText(R.id.tv_community_room, communityRoomInfo.getCommunityName() + " " + communityRoomInfo.getRoomName());
                        }
                    };
                    communityAdapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:

                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };



    Handler addRecordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    Toast.makeText(AddGoodsApplyActivity.this, "添加成功,等待审核", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };


    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date;
            month += 1;
            if (month < 10) {
                date = year + "-0" + month + "-" + dayOfMonth;
            } else {
                date = year + "-" + month + "-" + dayOfMonth;
            }
            tvDate.setText(date);
            paramsReason = date;
        }
    };

    private void showCommunityPop(){
        LayoutInflater layoutInflater = LayoutInflater.from(AddGoodsApplyActivity.this);
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
        communityAdapter = new ListViewAdapter<CommunityRoomInfo>(AddGoodsApplyActivity.this, R.layout.item_drop_down, communityList) {
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
                        paramsCommId = selectRoom.getCommunityID();
                        paramsRoomId = selectRoom.getRoomID();
                        paramsRoomName = selectRoom.getRoomName();

                        spCommunity.setText(paramsRoomName);
                    }

                mPopWindow.dismiss();
            }
        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha(AddGoodsApplyActivity.this, 1f);
            }
        });
        backgroundAlpaha(AddGoodsApplyActivity.this, 0.5f);
    }

    private void showReasonPop(){
        final List<String> data=Arrays.asList(getResources().getStringArray(R.array.goods_access_reason));

        LayoutInflater layoutInflater = LayoutInflater.from(AddGoodsApplyActivity.this);
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
        reasonAdapter = new ListViewAdapter<String>(AddGoodsApplyActivity.this, R.layout.item_drop_down,
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
                spReason.setText(paramsReason);
                if (paramsReason.equals("其他")) {
                    other = true;
                    tvOtherReasonTitle.setVisibility(View.VISIBLE);
                    etOther.setVisibility(View.VISIBLE);
                } else {
                    other = false;
                    tvOtherReasonTitle.setVisibility(View.GONE);
                    etOther.setVisibility(View.GONE);
                }
              mPopWindow.dismiss();
            }
        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha(AddGoodsApplyActivity.this, 1f);
            }
        });
        backgroundAlpaha(AddGoodsApplyActivity.this, 0.5f);
    }


    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "添加物品出入申请:" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "添加物品出入申请:" + this.getClass().getSimpleName());
    }
}
