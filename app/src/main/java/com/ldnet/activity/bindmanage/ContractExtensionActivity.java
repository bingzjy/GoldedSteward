package com.ldnet.activity.bindmanage;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.HouseRelationService;
import com.ldnet.utility.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContractExtensionActivity extends BaseActionBarActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.et_add_resident_date_start)
    TextView etAddResidentDateStart;
    @BindView(R.id.et_add_resident_date_end)
    TextView etAddResidentDateEnd;
    @BindView(R.id.btn_add_relation_submit)
    Button btnAddRelationSubmit;

    private String paramsDates, paramsDatee, paramsResiID, paramsRoomId;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date minSDate, minEdate;
    private HouseRelationService houseRelationService;
    private int residentType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_extension);
        ButterKnife.bind(this);
        getActionBar().hide();

        tvPageTitle.setText(getString(R.string.add_relation_contract_again));

        houseRelationService=new HouseRelationService(this);
        paramsResiID = getIntent().getStringExtra("RESIDENT_ID");
        paramsRoomId = getIntent().getStringExtra("ROOM_ID");
        paramsDatee = getIntent().getStringExtra("EDATE");
        paramsDates = getIntent().getStringExtra("SDATE");
        residentType = getIntent().getIntExtra("STATE", 0);
        etAddResidentDateStart.setText(paramsDatee);

        if (residentType == 0 || residentType == 1) {   //未失效
            etAddResidentDateStart.setText(paramsDates);
            etAddResidentDateStart.setEnabled(false);
            etAddResidentDateEnd.setHint("请选择结束时间");

            try {
                minEdate = mFormat.parse(paramsDatee);
                minSDate = mFormat.parse(paramsDates);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (residentType == 2) {  //已失效

            etAddResidentDateStart.setEnabled(true);
            etAddResidentDateStart.setHint("请选择开始时间");
            etAddResidentDateEnd.setHint("请选择结束时间");
            try {
                minEdate = mFormat.parse(mFormat.format(new Date()));
                minSDate = mFormat.parse(mFormat.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }


    @OnClick({R.id.btn_back, R.id.et_add_resident_date_start, R.id.et_add_resident_date_end, R.id.btn_add_relation_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.et_add_resident_date_start: //续约的开始时间大于原结束时间
                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dialog1 = new DatePickerDialog(ContractExtensionActivity.this, listener1,
                        calendar1.get(Calendar.YEAR),
                        calendar1.get(Calendar.MONTH),
                        calendar1.get(Calendar.DAY_OF_MONTH));
                dialog1.getDatePicker().setMinDate(minSDate.getTime()-1000);
                dialog1.show();
                break;
            case R.id.et_add_resident_date_end:  //续约的结束时间大于原结束时间
                Calendar calendar2 = Calendar.getInstance();
                DatePickerDialog dialog2 = new DatePickerDialog(ContractExtensionActivity.this, listener2,
                        calendar2.get(Calendar.YEAR),
                        calendar2.get(Calendar.MONTH),
                        calendar2.get(Calendar.DAY_OF_MONTH));
                dialog2.getDatePicker().setMinDate(minEdate.getTime());
                dialog2.show();
                break;
            case R.id.btn_add_relation_submit:

                if (TextUtils.isEmpty(etAddResidentDateEnd.getText())||TextUtils.isEmpty(etAddResidentDateStart.getText())){
                    showToast("请选择时间");
                }else{
                    houseRelationService.SetRenewRoomResident(paramsResiID,paramsRoomId,paramsDates,paramsDatee,handler);
                    showProgressDialog();
                }
                break;
        }
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


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    finish();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

}
