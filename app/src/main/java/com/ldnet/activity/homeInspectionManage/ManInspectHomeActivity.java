package com.ldnet.activity.homeInspectionManage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.tendcloud.tenddata.TCAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManInspectHomeActivity extends BaseActionBarActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.et_inspect_man)
    EditText etInspectMan;
    @BindView(R.id.btn_start_inspect)
    Button btnStartInspect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_home_man);
        ButterKnife.bind(this);

        tvPageTitle.setText(getString(R.string. inpspect_notice_title));
    }

    @OnClick({R.id.btn_back, R.id.btn_start_inspect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_start_inspect:
                Intent intent=new Intent(ManInspectHomeActivity.this,MainHomeInspectionActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "房屋验收-陪同验房人：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "房屋验收-陪同验房人：" + this.getClass().getSimpleName());
    }

}
