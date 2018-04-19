package com.ldnet.activity.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.ldnet.utility.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.commen.Services;
import com.ldnet.activity.me.Community;
import com.ldnet.entities.ImageItem;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.interfaze.PictureChoseListener;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.PropertyServeService;
import com.ldnet.utility.*;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.view.dialog.MyDialog;
import com.nanchen.compresshelper.CompressHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Property_Complain_Create extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private TextView tv_repair_houseinfo;
    private TextView tv_property_tel, tv_property_name;
    private ImageButton ibtn_complain_change_house,addImage;
    private EditText et_complain_content;
    private Button btn_complain_call_property;
    private Button btn_complain_confirm;
    private LinearLayout ll_complain_picture_list;
    public static String mImageIds;
    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private static String content = "";
    private String aaa = "";
    private PropertyServeService propertyService;
    private List<String> imagePathList=new ArrayList<>();
    private String url = Services.mHost + "WFComplaint/APP_YZ_CreateComplaint";
    private AcountService acountService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_property_complain_create);
        AppUtils.setupUI(findViewById(R.id.ll_complain_create), this);

        propertyService=new PropertyServeService(this);
        acountService=new AcountService(this);
        aaa = getIntent().getStringExtra("flag");
        initView();
        initEvent();
    }

    private void initView(){
        User user = UserInformation.getUserInfo();
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText("添加投诉");
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        addImage=(ImageButton)findViewById(R.id.btn_picture_add);
        tv_repair_houseinfo = (TextView) findViewById(R.id.tv_complain_houseinfo);
        tv_repair_houseinfo.setText(user.CommuntiyName + "(" + user.HouseName + ")");
        et_complain_content = (EditText) findViewById(R.id.et_complain_content);
        ibtn_complain_change_house = (ImageButton) findViewById(R.id.ibtn_complain_change_house);
        btn_complain_call_property = (Button) findViewById(R.id.btn_complain_call_property);
        btn_complain_confirm = (Button) findViewById(R.id.btn_complain_confirm);
        ll_complain_picture_list = (LinearLayout) findViewById(R.id.ll_complain_picture_list);
        tv_property_tel = (TextView) findViewById(R.id.tv_property_tel);
        tv_property_name = (TextView) findViewById(R.id.tv_property_name);
        tv_property_tel.setText(UserInformation.getUserInfo().getUserPhone());
        tv_property_name.setText(UserInformation.getUserInfo().getUserName());
    }

    //初始化事件
    public void initEvent() {
        addImage.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        ibtn_complain_change_house.setOnClickListener(this);
        btn_complain_call_property.setOnClickListener(this);
        btn_complain_confirm.setOnClickListener(this);
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
            case R.id.ibtn_complain_change_house:
                try {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("NOT_FROM_ME", "104");
                    extras.put("LEFT", "LEFT");
                    gotoActivity(Community.class.getName(), extras);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_complain_call_property:
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

            case R.id.btn_complain_confirm:
                //提交投诉
                if (TextUtils.isEmpty(mImageIds)) {
                    mImageIds = "";
                }
                if (!TextUtils.isEmpty(et_complain_content.getText().toString().trim())) {
                    showProgressDialog();
                    String content=et_complain_content.getText().toString().trim();
                    propertyService.createComplain(content,Utility.ListToString(imagePathList),handlerAdd);
                } else {
                    showToast("投诉内容不能为空");
                }
                break;
            case R.id.btn_picture_add:
                //添加图片
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
                gotoActivityAndFinish(Property_Complain.class.getName(), null);
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
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            MyDialog dialog = new MyDialog(this);
            dialog.show();
            dialog.setDialogCallback(dialogcallback);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    //压缩、保存、上传所选图片
    private void showImage(final String path) {
        File file = new File(path);
        Bitmap bitmap = CompressHelper.getDefault(Property_Complain_Create.this).compressToBitmap(file);
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
                String fileId =new Services().Upload(Property_Complain_Create.this, path).FileName;
                imagePathList.add(fileId);
            }
        }.start();

        //显示图片
        creationImg(file.getAbsolutePath());
    }

    //视图上创建图片
    public void creationImg(final String imagePath) {
        ImageView iv = new ImageView(Property_Complain_Create.this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //添加到父布局
        ll_complain_picture_list.addView(iv, ll_complain_picture_list.getChildCount() - 1);
        //设置要添加的ImageView的尺寸、坐标
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
        linearParams.setMargins(linearParams.leftMargin, linearParams.topMargin,
                Utility.dip2px(Property_Complain_Create.this,
                        getResources().getDimension(R.dimen.dimen_2dp)), linearParams.bottomMargin);
        linearParams.width = Utility.dip2px(this, 64f);
        linearParams.height = Utility.dip2px(this, 64f);
        iv.setLayoutParams(linearParams);

        //显示头像
        ImageLoader.getInstance().displayImage("file://" + imagePath, iv, Utility.imageOptions);
        //最多上传5张照片
        if (ll_complain_picture_list.getChildCount() == 6) {
            addImage.setVisibility(View.GONE);
        }else{
            addImage.setVisibility(View.VISIBLE);
        }

        //添加图片长按事件，用于删除
        for (int i = 0; i < ll_complain_picture_list.getChildCount(); i++) {
            ImageView itemView = (ImageView) ll_complain_picture_list.getChildAt(i);
            if (itemView != addImage) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int index = ll_complain_picture_list.indexOfChild(v);
                        imagePathList.remove(index);
                        ll_complain_picture_list.removeViewAt(index);
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
                        gotoActivityAndFinish(Property_Complain.class.getName(), extras);
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

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "物业服务-新增投诉" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "物业服务-新增投诉" + this.getClass().getSimpleName());
    }
}
