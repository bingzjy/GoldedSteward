package com.ldnet.activity.homeinspectionmanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.home.Property_Services;
import com.ldnet.goldensteward.R;
import com.tendcloud.tenddata.TCAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoticeHomeInspectionActivity extends BaseActionBarActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.tv_custom)
    TextView tvCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_inspection_exception);
        ButterKnife.bind(this);

        tvPageTitle.setText(getString(R.string.inpspect_notice_title));
        tvCustom.setText(getString(R.string.start_inspect_home));
        tvCustom.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "房屋验收-须知：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "房屋验收-须知：" + this.getClass().getSimpleName());
    }

    @OnClick({R.id.tv_custom,R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(Property_Services.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_custom:
                Intent intent=new Intent(NoticeHomeInspectionActivity.this,WriteInspectHomeManActivity.class);
                startActivity(intent);
                break;
        }
    }


}
