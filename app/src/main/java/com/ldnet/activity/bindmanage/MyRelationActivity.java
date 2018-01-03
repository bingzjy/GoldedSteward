package com.ldnet.activity.bindmanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.third.SwipeListView.SwipeListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MyRelationActivity extends BaseActionBarActivity {
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.iv_share)
    ImageView ivAdd;
    @BindView(R.id.slv_realtion_content)
    SwipeListView slvRealtion;
    @BindView(R.id.tv_null_data_title)
    TextView tvNullDataTitle;

    private Unbinder unbinder;
    private static final String TAG = "MyRelationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_relation);
        unbinder = ButterKnife.bind(this);

        initView();
    }


    private void initView() {
        tvPageTitle.setText(getString(R.string.bind_relation_main_title));
        ivAdd.setImageResource(R.drawable.green_add_icon);
        ivAdd.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @OnClick({R.id.btn_back, R.id.iv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.iv_share:
                Intent intent = new Intent(MyRelationActivity.this, AddRelationActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
