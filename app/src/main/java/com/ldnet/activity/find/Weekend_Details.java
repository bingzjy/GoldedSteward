package com.ldnet.activity.find;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.me.PublishActivity;
import com.ldnet.entities.APPHomePage_Column;
import com.ldnet.entities.WeekendDetails;
import com.ldnet.entities.WeekendSignUp;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.FindService;
import com.ldnet.utility.BottomDialog;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.view.ImageCycleView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Weekend_Details extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private ImageCycleView vp_weekenimages;
    private TextView tv_weekend_title, tvCustom;
    private TextView tv_weekend_content;
    private TextView tv_weekend_cost;
    private TextView tv_weekend_signup_number;
    private TextView tv_weekend_start_datetime;
    private TextView tv_weekend_end_datetime;
    private TextView tv_weekend_address;
    private Button btn_weekend_signup_information;
    private Button btn_weekend_call;
    private Button btn_weekend_signup;

    private Services services;
    private String mContractPhone;
    private String mWeekendId;
    private Boolean mFromPublish = false;

    private WeekendDetails mDetails;
    private List<WeekendSignUp> infos;
    private ArrayList<String> mImageUrl = null;
    private AcountService acountService;
    private FindService findService;
    private String url = Services.mHost + "API/Resident/WeekendRecordAdd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_weekend_details);

        //二手商品ID
        mWeekendId = getIntent().getStringExtra("WEEKEND_ID");
        String formPublish = getIntent().getStringExtra("FROM_PUBLISH");
        if (!TextUtils.isEmpty(formPublish)) {
            mFromPublish = Boolean.valueOf(formPublish);
        }
        initView();
        initService();
        initEvent();
        findService.getWeekendDetail(mWeekendId, handlerGetDetail);
    }

    //初始化服务
    private void initService() {
        services = new Services();
        acountService = new AcountService(this);
        findService = new FindService(this);
    }

    private void initView() {
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_find_weekend);
        tvCustom = (TextView) findViewById(R.id.tv_custom);
        tvCustom.setVisibility(View.VISIBLE);
        if (mFromPublish) {
            tvCustom.setText("编辑");
        } else {
            tvCustom.setText("分享");
        }
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //初始化控件
        vp_weekenimages = (ImageCycleView) findViewById(R.id.vp_weekend_images);
        // 改线ViewPager的高度
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) vp_weekenimages.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        linearParams.height = dm.widthPixels / 16 * 9;
        vp_weekenimages.setLayoutParams(linearParams);
        mImageUrl = new ArrayList<String>();
        //标题
        tv_weekend_title = (TextView) findViewById(R.id.tv_weekend_title);
        //介绍
        tv_weekend_content = (TextView) findViewById(R.id.tv_weekend_content);
        //费用
        tv_weekend_cost = (TextView) findViewById(R.id.tv_weekend_cost);
        tv_weekend_signup_number = (TextView) findViewById(R.id.tv_weekend_signup_number);
        tv_weekend_start_datetime = (TextView) findViewById(R.id.tv_weekend_start_datetime);
        tv_weekend_end_datetime = (TextView) findViewById(R.id.tv_weekend_end_datetime);
        tv_weekend_address = (TextView) findViewById(R.id.tv_weekend_address);
        btn_weekend_call = (Button) findViewById(R.id.btn_weekend_call);
        btn_weekend_signup = (Button) findViewById(R.id.btn_weekend_signup);

        if (mFromPublish) {
            btn_weekend_call.setText("删除活动");
            btn_weekend_signup.setText("查看报名");
        }
    }


    private com.ldnet.view.ImageCycleView.ImageCycleViewListener mAdCycleViewListener =
            new com.ldnet.view.ImageCycleView.ImageCycleViewListener() {

                @Override
                public void onImageClick(int position, View imageView) {

                }

                @Override
                public void onImageDataClick(int position, View imageView, List<APPHomePage_Column> mData) {

                }

                @Override
                public void displayImage(String imageURL, ImageView imageView) {
                    ImageLoader.getInstance().displayImage(imageURL, imageView, imageOptions);// 此处本人使用了ImageLoader对图片进行加装！
                }
            };

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_weekend_call.setOnClickListener(this);
        btn_weekend_signup.setOnClickListener(this);
        tvCustom.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    if (!mFromPublish) {
                        gotoActivityAndFinish(Weekend.class.getName(), null);
                    } else {
                        finish();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_weekend_signup_information://查看报名
                findService.WeekendSignUpInformation(mWeekendId, "", handlerGetSignInfo);
                break;
            case R.id.btn_weekend_call://致电组织者或者删除
                if (btn_weekend_call.getText().toString().contains("删除")) {
                    findService.deleteWeekend(mWeekendId, handlerDelete);
                } else if (btn_weekend_call.getText().toString().contains("致电")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mContractPhone));
                    startActivity(intent);
                }
                break;
            case R.id.btn_weekend_signup://点击报名或者查看报名
                if (btn_weekend_signup.getText().toString().contains("查看")) {
                    findService.WeekendSignUpInformation(mWeekendId, "", handlerGetSignInfo);
                } else if (btn_weekend_signup.getText().toString().contains("报名")) {
                    if (!TextUtils.isEmpty(UserInformation.getUserInfo().getUserName())) {
                        weekendApplyDialog();
                    } else {
                        showToast(getResources().getString(R.string.weekend_signup_tip));
                    }
                }
                break;
            case R.id.tv_custom:
                if (mFromPublish) {  //编辑
                    Intent intent2 = new Intent(Weekend_Details.this, Weekend_Create.class);
                    intent2.putExtra("FREA_MARKET_ID", mWeekendId);
                    intent2.putExtra("FROM_PUBLISH", "true");
                    startActivity(intent2);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                } else {   //分享
                    BottomDialog dialog = new BottomDialog(Weekend_Details.this, mDetails.Url, mDetails.Title);
                    dialog.uploadImageUI(Weekend_Details.this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    //报名的对话框
    private void weekendApplyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.weekend_signup)
                .setCancelable(false)
                .setPositiveButton("确定", new weekendApplyDialogClass())
                .setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    class weekendApplyDialogClass implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:// 报名
                    findService.WeekendSignUp(mWeekendId, handlerSignUp);
                    break;
            }
        }
    }

    private void setDetailData() {
        //是否可以报名
        tv_weekend_end_datetime.setText(Services.subStr(mDetails.EndDatetime));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        //显示和隐藏按钮
        if (mDetails.ResidentId.equals(UserInformation.getUserInfo().UserId)) {  //是业主本人发布的
            btn_weekend_call.setText("删除活动");
            btn_weekend_signup.setText("查看报名");
        } else {                                                              //非业主本人，可报名
            btn_weekend_call.setText("致电组织者");
            btn_weekend_signup.setText("我要报名");
            Date endDate = null;
            try {
                endDate = dateFormat.parse(tv_weekend_end_datetime.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (mDetails.IsRecord) {
                btn_weekend_signup.setEnabled(false);
                btn_weekend_signup.setText("已报名");
            } else if (endDate.getTime() < new Date().getTime() && !mDetails.IsRecord) {
                btn_weekend_signup.setEnabled(false);
                btn_weekend_signup.setText("已截止");
            }
        }

        mContractPhone = mDetails.ContractTel;
        tv_weekend_title.setText(mDetails.Title);
        tv_weekend_content.setText(mDetails.Memo);
        tv_weekend_cost.setText("￥" + mDetails.Cost);
        tv_weekend_signup_number.setText(String.valueOf(mDetails.MemberCount));
        tv_weekend_start_datetime.setText(Services.subStr(mDetails.StartDatetime));
        tv_weekend_address.setText(mDetails.ActiveAddress);

        if (mDetails.MemberCount > 0
                && mDetails.ResidentId.equals(UserInformation.getUserInfo().UserId)) {
            btn_weekend_call.setEnabled(false);
            tvCustom.setVisibility(View.GONE);
        } else {
            tvCustom.setVisibility(View.VISIBLE);
            btn_weekend_call.setEnabled(true);
        }

        //图片加载
        if (mDetails.Img != null && mDetails.Img.size() > 0) {
            for (String imageID : mDetails.Img) {
                if (!TextUtils.isEmpty(imageID)) {
                    mImageUrl.add(Services.getImageUrl(imageID));
                }
            }
            vp_weekenimages.setImageResources(mImageUrl, mAdCycleViewListener);
        }
    }

    Handler handlerSignUp = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast(getResources().getString(R.string.weekend_signup_success));
                    btn_weekend_signup.setEnabled(false);
                    btn_weekend_signup.setText("已报名");
                    acountService.setIntegralTip(new Handler(), url);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    Handler handlerGetSignInfo = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    infos = (List<WeekendSignUp>) msg.obj;
                    final String items[] = new String[infos.size()];
                    for (int i = 0; i < infos.size(); i++) {
                        items[i] = infos.get(i).toString();
                    }
                    //dialog参数设置
                    AlertDialog.Builder builder = new AlertDialog.Builder(Weekend_Details.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);  //先得到构造器
                    builder.setTitle(R.string.apply_information); //设置标题
                    //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setPositiveButton(getResources().getString(R.string.sure_information), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    Handler handlerGetDetail = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    mDetails = (WeekendDetails) msg.obj;
                    setDetailData();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //删除周边游
    Handler handlerDelete = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    showToast("删除成功");
                    if (mFromPublish) {
                        //返回我的发布
                        Intent intent = new Intent(Weekend_Details.this, PublishActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        //返回我的发布
                        Intent intent = new Intent(Weekend_Details.this, Weekend.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast("删除失败");
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "周边游-详情：" + this.getClass().getSimpleName());

    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "周边游-详情：" + this.getClass().getSimpleName());
    }
}
