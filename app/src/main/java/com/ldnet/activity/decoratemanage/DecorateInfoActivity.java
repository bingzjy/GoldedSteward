package com.ldnet.activity.decoratemanage;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DecorateInfoActivity extends BaseActionBarActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.tv_room_name)
    TextView tvRoomName;
    @BindView(R.id.tv_decorate_licence)
    TextView tvDecorateLicence;
    @BindView(R.id.tv_decorate_unit)
    TextView tvDecorateUnit;
    @BindView(R.id.tv_decorate_man)
    TextView tvDecorateMan;
    @BindView(R.id.tv_decorate_tel)
    TextView tvDecorateTel;
    @BindView(R.id.tv_decorate_submit_time)
    TextView tvDecorateSubmitTime;
    @BindView(R.id.tv_decorate_start_time)
    TextView tvDecorateStartTime;
    @BindView(R.id.tv_decorate_end_time)
    TextView tvDecorateEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decorate_info);
        ButterKnife.bind(this);
    }

    private void initView() {
        tvPageTitle.setText("装修信息");
        tvRoomName.setText("");
        tvDecorateLicence.setText("");
        tvDecorateUnit.setText("");
        tvDecorateMan.setText("");
        tvDecorateTel.setText("");
        tvDecorateStartTime.setText("");
        tvDecorateEndTime.setText("");
        tvDecorateSubmitTime.setText("");
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }
}
