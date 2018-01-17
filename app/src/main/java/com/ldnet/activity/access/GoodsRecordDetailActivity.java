package com.ldnet.activity.access;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;

public class GoodsRecordDetailActivity extends BaseActionBarActivity {

    private TextView tvGoods, tvReason, tvDate, tvCommunity, tvStatus, title;
    private String accessGoods, accessReason, accessDate, accessCommunity, fromClass, status;
    private ImageView back;
    private RelativeLayout rlTab;
    private LinearLayout llApprove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_record_detail);

        tvGoods = (TextView) findViewById(R.id.tv_detail_all_goods);
        tvCommunity = (TextView) findViewById(R.id.tv_detail_goods_community);
        tvDate = (TextView) findViewById(R.id.tv_detail_goods_date);
        tvReason = (TextView) findViewById(R.id.tv_detail_goods_reasons);
        tvStatus = (TextView) findViewById(R.id.tv_detail_goods_status);
        title = (TextView) findViewById(R.id.tv_page_title);
        llApprove = (LinearLayout) findViewById(R.id.ll_approve_tag);
        title.setText("详情");
        back = (ImageView) findViewById(R.id.btn_back);
        back.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            fromClass = intent.getStringExtra("FROM_CLASS");
            if (fromClass.equals(AccessControlMain.class.getName())) {
                accessReason = intent.getStringExtra("REASON");
                accessCommunity = intent.getStringExtra("C_NAME");
                accessDate = intent.getStringExtra("DATE");
                accessGoods = intent.getStringExtra("GOODS");
                status = intent.getStringExtra("STATUS");

                tvReason.setText(accessReason == null ? "事项：未填" : "事项：" + accessReason);
                tvDate.setText(accessDate == null ? "时间：未填" : "时间：" + accessDate);
                tvCommunity.setText(accessCommunity == null ? "进出小区：未填" : "进出小区：" + accessCommunity);
                tvGoods.setText(accessGoods == null ? "物品清单：未填" : "物品清单：" + accessGoods);
                tvStatus.setText(status);

                if (status != null && status.equals("审核未通过")) {
                    llApprove.setVisibility(View.GONE);
                } else {
                    llApprove.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_back) {
            finish();
        }
    }

}
