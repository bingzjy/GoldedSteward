package com.ldnet.activity.me;

import android.os.*;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import com.ldnet.utility.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.commen.Services;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.PropertyServeService;
import com.tendcloud.tenddata.TCAgent;

public class Feedback extends BaseActionBarActivity {

    private TextView tv_main_title;
    private ImageButton btn_back;
    private Button btn_me_feedback;
    private EditText et_me_feedback;
    private Services services;
    private PropertyServeService propertyService;
    //初始化视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_feedback);
        AppUtils.setupUI(findViewById(R.id.ll_feedback),this);
        //初始化服务
        services = new Services();
        propertyService=new PropertyServeService(this);
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_feedback);

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_me_feedback = (Button) findViewById(R.id.btn_me_feedback);
        et_me_feedback = (EditText) findViewById(R.id.et_me_feedback);
        initEvent();
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_me_feedback.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "意见反馈：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "意见反馈：" + this.getClass().getSimpleName());
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_me_feedback:
                if(!TextUtils.isEmpty(et_me_feedback.getText().toString().trim())){
                    propertyService.feedback(et_me_feedback.getText().toString().trim(),handler);
                }else{
                    showToast("意见或建议不能为空");
                }
                break;
            default:
                break;
        }
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast(getResources().getString(R.string.activity_me_feedback_success));
                    finish();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };



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
