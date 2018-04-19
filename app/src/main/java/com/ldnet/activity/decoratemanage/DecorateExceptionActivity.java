package com.ldnet.activity.decoratemanage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DecorateExceptionActivity extends BaseActionBarActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvTitle;
    @BindView(R.id.tv_custom)
    TextView tvCustom;
    @BindView(R.id.lv_exception)
    ListView lvException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decorate_exception);
        ButterKnife.bind(this);
        tvCustom.setText("装修信息");
        tvTitle.setText("巡查异常");
    }



    @OnClick({R.id.btn_back, R.id.tv_custom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_custom:
                try {
                    gotoActivity(DecorateInfoActivity.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


}
