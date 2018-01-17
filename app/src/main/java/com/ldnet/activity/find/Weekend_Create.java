package com.ldnet.activity.find;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import com.ldnet.activity.adapter.ImageItem;
import com.ldnet.activity.adapter.MyDialog;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.activity.me.PublishActivity;
import com.ldnet.entities.WeekendDetails;
import com.ldnet.goldensteward.R;
import com.ldnet.interfaze.PictureChoseListener;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.FindService;
import com.ldnet.utility.CashierInputFilter;
import com.ldnet.utility.Services;
import com.ldnet.utility.Utility;
import com.ldnet.view.SlideDateTimeListener;
import com.ldnet.view.SlideDateTimePicker;
import com.nanchen.compresshelper.CompressHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Weekend_Create extends BaseActionBarFragmentActivity implements View.OnClickListener {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private EditText et_weekend_title;
    private EditText et_weekend_address;
    private EditText et_weekend_cost;
    private EditText et_weekend_start_date;
    private EditText et_weekend_start_time;
    private EditText et_weekend_end_date;
    private EditText et_weekend_end_time;
    private EditText et_weekend_content;
    private LinearLayout ll_weekend_picture_list;
    private ImageButton addImage;
    private Button btn_weekend_confirm;
    private Calendar calendar = Calendar.getInstance();
    public static String mImageIds;
    private static String mFreamarketId;
    private static Boolean mFromPublish = false;
    private WeekendDetails details;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private static String title = "", address = "", cost = "", date1 = "", date2 = "", content = "";
    private boolean flag = false;
    private FindService findService;
    private List<String> imagePathList=new ArrayList<>();
    private String urlCreate=Services.mHost + "API/Resident/WeekendAdd";
    private String urlUpdate = Services.mHost + "API/Resident/WeekendUpdate";
    private AcountService acountService;
    private Date dateStart,dateEnd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_weekend_create);
        AppUtils.setupUI(findViewById(R.id.ll_weekend_create), this);

        //初始化视图
        initView();
        //初始化事件
        initEvent();
        initService();
        //获取回传的值
        String formPublish = getIntent().getStringExtra("FROM_PUBLISH");
        if (!TextUtils.isEmpty(formPublish)) {
            mFromPublish = Boolean.valueOf(formPublish);
            mFreamarketId = getIntent().getStringExtra("FREA_MARKET_ID");
            findService.getWeekendDetail(mFreamarketId,handlerDetail);
            showProgressDialog();
        }
    }

    private void initService() {
        //初始化服务
        findService = new FindService(this);
        acountService = new AcountService(this);
        services = new Services();
    }

    private void initView(){
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.weekend_publish);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //初始化表单控件
        et_weekend_title = (EditText) findViewById(R.id.et_weekend_title);
        et_weekend_address = (EditText) findViewById(R.id.et_weekend_address);
        et_weekend_cost = (EditText) findViewById(R.id.et_weekend_cost);
        et_weekend_start_date = (EditText) findViewById(R.id.et_weekend_start_date);
        et_weekend_end_date = (EditText) findViewById(R.id.et_weekend_end_date);
        et_weekend_content = (EditText) findViewById(R.id.et_weekend_content);

        ll_weekend_picture_list = (LinearLayout) findViewById(R.id.ll_weekend_picture_list);
        btn_weekend_confirm = (Button) findViewById(R.id.btn_weekend_confirm);
        addImage=(ImageButton)findViewById(R.id.btn_picture_add);

        //设置金额输入
        InputFilter[] filters={new CashierInputFilter()};
        et_weekend_cost.setFilters(filters);
    }

    public boolean isNull() {
        if (TextUtils.isEmpty(et_weekend_title.getText().toString().trim())) {
            showToast("标题不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_weekend_address.getText().toString().trim())) {
            showToast("活动地点不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_weekend_start_date.getText().toString().trim())) {
            showToast("请选择开始时间");
            return false;
        }

        if (TextUtils.isEmpty(et_weekend_end_date.getText().toString().trim())) {
            showToast("请选择结束时间");
            return false;
        }

        try {
            dateStart = mFormatter.parse(et_weekend_start_date.getText().toString());
            dateEnd = mFormatter.parse(et_weekend_end_date.getText().toString());
            if (dateEnd.getTime() <= dateStart.getTime()) {
                showToast("结束时间应大于开始时间");
                return false;
            }
            String currentDate=mFormatter.format(new Date());
            if (dateEnd.getTime()<mFormatter.parse(currentDate).getTime()){
                showToast("结束时间应大于当前时间");
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(et_weekend_content.getText().toString().trim())) {
            showToast("介绍不能为空");
            return false;
        }

        if (imagePathList.size()==0){
            showToast("请选择照片");
            return false;
        }
        return true;
    }
    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_weekend_confirm.setOnClickListener(this);
        et_weekend_start_date.setOnClickListener(this);
        et_weekend_end_date.setOnClickListener(this);
        addImage.setOnClickListener(this);
    }
    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://返回我的发布或周末去哪列表
                crenteCencalDialog();
                break;
            case R.id.btn_weekend_confirm:
                String title = et_weekend_title.getText().toString().trim();
                String address = et_weekend_address.getText().toString().trim();
                String cost = et_weekend_cost.getText().toString().trim();
                String sDate = et_weekend_start_date.getText().toString().trim();
                String eDate = et_weekend_end_date.getText().toString().trim();
                String content = et_weekend_content.getText().toString().trim();
                //  修改信息
                if (mFromPublish) {
                    if (isNull()) {
                        findService.weekendUpdate(mFreamarketId, title, sDate, eDate, address, cost,
                               Utility.ListToString(imagePathList), content,handlerUpdate);
                    }
                    //发布信息
                } else {
                    if (isNull()) {
                        findService.weekendCreate(title, sDate, eDate, address, cost,
                                Utility.ListToString(imagePathList), content,handlerCreate);
                    }
                }
                break;
            case R.id.et_weekend_start_date:
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .setIs24HourTime(true)
                        .setMinDate(new Date())
                        .build()
                        .show();
                break;
            case R.id.et_weekend_start_time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        et_weekend_start_time.setText(new StringBuilder().append(hour < 10 ? "0" + hour : hour).append(":").append(minute < 10 ? "0" + minute : minute).append(":00"));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                break;
            case R.id.et_weekend_end_date:
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener1)
                        .setInitialDate(new Date())
                        .setIs24HourTime(true)
                        .setMinDate(new Date())
                        .build()
                        .show();
                break;
            case R.id.et_weekend_end_time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        et_weekend_end_time.setText(new StringBuilder().append(hour < 10 ? "0" + hour : hour).append(":").append(minute < 10 ? "0" + minute : minute).append(":00"));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
                break;
            case R.id.btn_picture_add:
                showAddPicture(new PictureChoseListener() {
                    @Override
                    public void choseSuccess(String imagePath) {
                        showImage(imagePath);
                    }

                    @Override
                    public void choseFail() {

                    }
                });
                break;
            default:
                break;
        }
    }
    //关闭发布闲置物品显示对话框
    private void crenteCencalDialog() {
        MyDialog dialog = new MyDialog(this);
        dialog.show();
        dialog.setDialogCallback(dialogcallback);
    }

    MyDialog.Dialogcallback dialogcallback = new MyDialog.Dialogcallback() {
        @Override
        public void dialogdo() {
            if (mFromPublish) {
                Intent intent1 = new Intent(Weekend_Create.this, PublishActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
            } else {
                Intent intent1 = new Intent(Weekend_Create.this, Weekend.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
            }
        }

        @Override
        public void dialogDismiss() {
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            crenteCencalDialog();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            et_weekend_start_date.setText(mFormatter.format(date));
        }
    };

    private SlideDateTimeListener listener1 = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            et_weekend_end_date.setText(mFormatter.format(date));
        }
    };

    //压缩、保存、上传所选图片
    private void showImage(final String path) {
        File file = new File(path);
        Bitmap bitmap = CompressHelper.getDefault(Weekend_Create.this).compressToBitmap(file);
        FileOutputStream fileOutStream = null;
        try {
            fileOutStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80,
                    fileOutStream);
            fileOutStream.flush();
            fileOutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("file", "真实文件大小" + new File(path).length() / 1024 + "");

        //上传图片
        new Thread() {
            @Override
            public void run() {
                super.run();
                String fileId =new Services().Upload(Weekend_Create.this, path).FileName;
                imagePathList.add(fileId);
            }
        }.start();

        //显示图片
        creationImg(file.getAbsolutePath(),true);
    }

    //视图上创建图片
    public void creationImg(final String imagePath,final boolean notID) {
        ImageView iv = new ImageView(Weekend_Create.this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //添加到父布局
        ll_weekend_picture_list.addView(iv, ll_weekend_picture_list.getChildCount() - 1);
        //设置要添加的ImageView的尺寸、坐标
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
        linearParams.setMargins(linearParams.leftMargin, linearParams.topMargin,
                Utility.dip2px(Weekend_Create.this,
                        getResources().getDimension(R.dimen.dimen_2dp)), linearParams.bottomMargin);
        linearParams.width = Utility.dip2px(this, 64f);
        linearParams.height = Utility.dip2px(this, 64f);
        iv.setLayoutParams(linearParams);

        //显示头像
        if (notID){ //是图片路径
            ImageLoader.getInstance().displayImage("file://" + imagePath, iv, Utility.imageOptions);
        }else{    //是图片ID
            ImageLoader.getInstance().displayImage(Services.getImageUrl(imagePath), iv, Utility.imageOptions);
        }

        //最多上传5张照片
        if (ll_weekend_picture_list.getChildCount() == 6) {
            addImage.setVisibility(View.GONE);
        }else{
            addImage.setVisibility(View.VISIBLE);
        }

        //添加图片长按事件，用于删除
        for (int i = 0; i < ll_weekend_picture_list.getChildCount(); i++) {
            ImageView itemView = (ImageView) ll_weekend_picture_list.getChildAt(i);
            if (itemView != addImage) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int index = ll_weekend_picture_list.indexOfChild(v);
                        imagePathList.remove(index);
                        ll_weekend_picture_list.removeViewAt(index);
                        showToast("已删除");
                        if (imagePathList.size() < 5) {
                            //返回值为0，visible；返回值为4，invisible；返回值为8，gone。
                            if (addImage.getVisibility() != View.VISIBLE) {
                                addImage.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    }
                });
            }
        }

    }

    Handler handlerDetail=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    details = (WeekendDetails) msg.obj;
                    btn_weekend_confirm.setText("确认");
                    tv_main_title.setText("修改活动信息");
                    et_weekend_title.setText(details.Title);
                    et_weekend_address.setText(details.ActiveAddress);
                    et_weekend_content.setText(details.Memo);
                    et_weekend_cost.setText(String.valueOf(details.Cost));
                    et_weekend_start_date.setText(Services.subStr(details.StartDatetime));
                    et_weekend_end_date.setText(Services.subStr(details.EndDatetime));

                    if (details.Img.size()> 0) {
                        for (String imageId : details.Img) {
                            if (!TextUtils.isEmpty(imageId)){
                                imagePathList.add(imageId);
                                creationImg(imageId, false);
                            }
                        }
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    Handler handlerCreate=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    try {
                        gotoActivityAndFinish(Weekend.class.getName(), null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    acountService.setIntegralTip(new Handler(),urlCreate);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //修改
    Handler handlerUpdate=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    Intent intent=new Intent(Weekend_Create.this, PublishActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    acountService.setIntegralTip(new Handler(),urlUpdate);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


}
