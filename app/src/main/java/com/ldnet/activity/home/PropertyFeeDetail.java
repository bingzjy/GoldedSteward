package com.ldnet.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Fees;
import com.ldnet.entities.User;
import com.ldnet.entities.lstAPPFees;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.ViewHolder;

/**
 * Created by lee on 2016/7/12.
 */
public class PropertyFeeDetail extends BaseActionBarActivity implements View.OnClickListener {

    private TextView tv_main_title, tv_fee_houseinfo, tv_fee_year_month, tv_fee_sum,tv_fee_submit;
    private ImageButton btn_back;
    private ListView fee_listview;
    private ListViewAdapter adapter;

    private User user;
    private Fees fees;
    private boolean flag;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_fee_detail);

        findView();
    }

    public void findView() {

        fees = (Fees) getIntent().getSerializableExtra("lstAPPFees");
        flag = getIntent().getBooleanExtra("flag",false);
//        position = getIntent().getIntExtra("position",0);

        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(getString(R.string.fee_detail));
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        tv_fee_houseinfo = (TextView) findViewById(R.id.tv_fee_houseinfo);
        tv_fee_year_month = (TextView) findViewById(R.id.tv_fee_year_month);
        tv_fee_submit = (TextView) findViewById(R.id.tv_fee_submit);
        tv_fee_submit.setOnClickListener(this);
        tv_fee_sum = (TextView) findViewById(R.id.tv_fee_sum);
        user = UserInformation.getUserInfo();
        tv_fee_houseinfo.setText(user.CommuntiyName + "(" + user.HouseName + ")");
        tv_fee_year_month.setText(fees.getFeeDate() + "月的费用");
        tv_fee_sum.setText("共" + fees.getSum() + "元");

        fee_listview = (ListView) findViewById(R.id.fee_listview);
        adapter = new ListViewAdapter<lstAPPFees>(this, R.layout.property_fee_detail_item, fees.getLstAPPFees()) {
            @Override
            public void convert(ViewHolder holder, lstAPPFees lstAPPFees) {
                TextView tv_fee_type = (TextView) holder.getView(R.id.tv_fee_type);
                TextView tv_fee_num = (TextView) holder.getView(R.id.tv_fee_num);

                tv_fee_type.setText(lstAPPFees.getItemTitle());
                tv_fee_num.setText(lstAPPFees.getPayable()+"元");
            }
        };
        fee_listview.setAdapter(adapter);
        if(flag){
            tv_fee_submit.setVisibility(View.GONE);
        }else{
            tv_fee_submit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(Property_Fee.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_fee_submit:
                Intent intent = new Intent();
                intent.setClass(PropertyFeeDetail.this, PropertyFeeConfirm.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            default:
                break;
        }
    }
}
