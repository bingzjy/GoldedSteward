package com.ldnet.activity.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.ldnet.activity.adapter.*;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.me.Community;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.interfaze.PictureChoseListener;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.PropertyServeService;
import com.ldnet.utility.*;
import com.nanchen.compresshelper.CompressHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Property_Repair_Create extends BaseActionBarActivity {
    private TextView tv_property_tel, tv_property_name;
    private ImageButton btn_back;
    private Services services;
    private TextView tv_repair_houseinfo;
    private ImageButton ibtn_repair_change_house,addImage;
    private EditText et_repair_content;
    private Button btn_repair_call_property;
    private Button btn_repair_confirm;
    private LinearLayout ll_repair_picture_list;
    private RadioButton rbtn_repair_personal;
    private RadioButton rbtn_repair_public;
    public static String mImageIds;
    private static int bxType = 0;
    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
    SharedPreferences sp;
    private static String content = "";
    private String aaa = "";
    private List<String> imagePathList=new ArrayList<>();
    private PropertyServeService repairService;
    private String url = Services.mHost + "WFRepairs/APP_YZ_CreateRepairs";
    private AcountService acountService;
    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_property_repair_create);
        AppUtils.setupUI(findViewById(R.id.ll_repair_create), this);
        repairService=new PropertyServeService(this);
        acountService=new AcountService(this);
        services = new Services();
        initView();
        initEvent();
    }


    public void initView(){
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        addImage=(ImageButton)findViewById(R.id.btn_picture_add);
        //初始化页面控件
        User user = UserInformation.getUserInfo();
        tv_repair_houseinfo = (TextView) findViewById(R.id.tv_repair_houseinfo);
        tv_repair_houseinfo.setText(user.CommuntiyName + "(" + user.HouseName + ")");
        et_repair_content = (EditText) findViewById(R.id.et_repair_content);
        ibtn_repair_change_house = (ImageButton) findViewById(R.id.ibtn_repair_change_house);
        btn_repair_call_property = (Button) findViewById(R.id.btn_repair_call_property);
        btn_repair_confirm = (Button) findViewById(R.id.btn_repair_confirm);
        ll_repair_picture_list = (LinearLayout) findViewById(R.id.ll_repair_picture_list);
        rbtn_repair_personal = (RadioButton) findViewById(R.id.rdb_tenement_title3);
        rbtn_repair_public = (RadioButton) findViewById(R.id.rdb_tenement_title1);
        rbtn_repair_public.setText("公共报修");
        rbtn_repair_personal.setText("个人报修");
        tv_property_tel = (TextView) findViewById(R.id.tv_property_tel);
        tv_property_name = (TextView) findViewById(R.id.tv_property_name);
        tv_property_tel.setText(UserInformation.getUserInfo().getUserPhone());
        tv_property_name.setText(UserInformation.getUserInfo().getUserName());
    }

    //初始化事件
    public void initEvent() {
        addImage.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        ibtn_repair_change_house.setOnClickListener(this);
        btn_repair_call_property.setOnClickListener(this);
        btn_repair_confirm.setOnClickListener(this);
        rbtn_repair_personal.setOnClickListener(this);
        rbtn_repair_public.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                MyDialog dialog = new MyDialog(this);
                dialog.show();
                dialog.setDialogCallback(dialogcallback);
                break;
            case R.id.ibtn_repair_change_house:
                try {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("NOT_FROM_ME", "102");
                    extras.put("LEFT", "LEFT");
                    gotoActivity(Community.class.getName(), extras);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_repair_call_property:
                //给物业打电话
                String phone = UserInformation.getUserInfo().PropertyPhone;
                if (!TextUtils.isEmpty(phone)) {
                    phone = "tel:" + phone;
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phone));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                } else {
                    showToast(getString(R.string.property_phone_none));
                }
                break;
            case R.id.btn_repair_confirm:
                //提交报修
                Integer type = 1;
                if (rbtn_repair_public.isChecked()) {
                    type = 0;
                }
                if (TextUtils.isEmpty(mImageIds)) {
                    mImageIds = "";
                }
                if (!TextUtils.isEmpty(et_repair_content.getText().toString().trim())) {
                    repairService.createRepair(Utility.ListToString(imagePathList),et_repair_content.getText().toString(),type,handlerAdd);
                } else {
                    showToast("报修内容不能为空");
                }
                break;
            case R.id.rdb_tenement_title3:  //个人报修
                bxType = 1;
                break;
            case R.id.rdb_tenement_title1:   //公共报修
                bxType = 0;
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

    MyDialog.Dialogcallback dialogcallback = new MyDialog.Dialogcallback() {
        @Override
        public void dialogdo() {
            try {
                gotoActivityAndFinish(Property_Repair.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void dialogDismiss() {
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MyDialog dialog = new MyDialog(this);
            dialog.show();
            dialog.setDialogCallback(dialogcallback);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //压缩、保存、上传所选图片
    private void showImage(final String path) {
        File file = new File(path);
        Bitmap bitmap = CompressHelper.getDefault(Property_Repair_Create.this).compressToBitmap(file);
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
                String fileId =new Services().Upload(Property_Repair_Create.this, path).FileName;
                imagePathList.add(fileId);
            }
        }.start();

        //显示图片
        creationImg(file.getAbsolutePath());
    }

    //视图上创建图片
    public void creationImg(final String imagePath) {
        ImageView iv = new ImageView(Property_Repair_Create.this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //添加到父布局
        ll_repair_picture_list.addView(iv, ll_repair_picture_list.getChildCount() - 1);
        //设置要添加的ImageView的尺寸、坐标
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
        linearParams.setMargins(linearParams.leftMargin, linearParams.topMargin,
                Utility.dip2px(Property_Repair_Create.this,
                getResources().getDimension(R.dimen.dimen_2dp)), linearParams.bottomMargin);
        linearParams.width = Utility.dip2px(this, 64f);
        linearParams.height = Utility.dip2px(this, 64f);
        iv.setLayoutParams(linearParams);

        //显示头像
        ImageLoader.getInstance().displayImage("file://" + imagePath, iv, Utility.imageOptions);
        //最多上传5张照片
        if (ll_repair_picture_list.getChildCount() == 6) {
            addImage.setVisibility(View.GONE);
        }else{
            addImage.setVisibility(View.VISIBLE);
        }

        //添加图片长按事件，用于删除
        for (int i = 0; i < ll_repair_picture_list.getChildCount(); i++) {
            ImageView itemView = (ImageView) ll_repair_picture_list.getChildAt(i);
            if (itemView != addImage) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int index = ll_repair_picture_list.indexOfChild(v);
                        imagePathList.remove(index);
                        ll_repair_picture_list.removeViewAt(index);
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

    Handler handlerAdd=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    try {
                        acountService.setIntegralTip(new Handler(),url);
                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put("LEFT", "LEFT");
                        gotoActivityAndFinish(Property_Repair.class.getName(), extras);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

}
