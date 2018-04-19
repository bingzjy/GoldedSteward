package com.ldnet.activity.homeinspectionmanage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ldnet.goldensteward.R;
import com.tendcloud.tenddata.TCAgent;

public class InspectExceptionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_exception_detail);
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "房屋验收-异常详情：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "房屋验收-异常详情：" + this.getClass().getSimpleName());
    }

}
