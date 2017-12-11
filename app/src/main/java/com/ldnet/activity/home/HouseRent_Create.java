package com.ldnet.activity.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.*;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.gson.Gson;
import com.ldnet.activity.adapter.*;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.interfaze.PictureChoseListener;
import com.ldnet.service.BaseService;
import com.ldnet.service.HouseRentService;
import com.ldnet.service.OkHttpService;
import com.ldnet.utility.*;
import com.nanchen.compresshelper.CompressHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import okhttp3.Call;
import okhttp3.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.ldnet.goldensteward.R.id.et_weekend_cost;

/**
 * Created by Murray on 2015/9/6.
 */
public class HouseRent_Create extends BaseActionBarActivity {

    //控件
    private LinearLayout ll_houserent_images;
    private ImageButton btn_back;
    private GridView addhouserent_add;
    private TextView tv_page_title;
    private EditText et_house_rent_detail_title, et_house_rent_detail_room, et_house_rent_detail_hall, et_house_rent_detail_toilet,
            et_house_rent_detail_area, et_house_rent_detail_floor, et_house_rent_detail_total_floor, et_house_rent_detail_room_number,
            et_house_rent_detail_tel, id_spinner_room_config_memo, et_house_rent_detail_rent;
    private CheckBox addhouserent_rdoleft;
    private Spinner id_spinner_orientation, id_spinner_fitment, id_spinner_roomtype, id_spinner_renttype, id_spinner_room_config;
    private Button btn_house_rent_create;

    //标题，室，厅，卫，面积，几层，总层数，房号，联系电话，带家具备注，租赁价格
    private static String title, room, hall, toilet, area, floor, totalfloor, roomNumber, tel, configMemo, rentPrice, isElevator;
    public static String mImageIds;
    //服务
    private Services service;
    private static HouseProperties mHouseProperties;
    private ArrayAdapter<KValues> adapterOrientation, adapterFitmentType, adapterRoomType, adapterRoomDeploy, adapterRentType;
    private boolean Borientation = false, Bfitment = false, Broomtype = false, Brenttype = false, Broom_config = false;

    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private SharedPreferences sp;
    private List<String> imagePathList=new ArrayList<>();
    private HouseRentService houseRentService;
    private ImageButton addImage;
    private Services services;
    private LayoutInflater layoutInflater;

    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_rent_layout);
        AppUtils.setupUI(findViewById(R.id.ll_house_rent), this);
        sp = getSharedPreferences(CustomConstants.APPLICATION_NAME, MODE_PRIVATE);

        if (ImageChooseActivity.instance != null) {
            ImageChooseActivity.instance.finish();
        }
        //服务初始化
        service = new Services();
        houseRentService=new HouseRentService(this);
        initView();
        initEvent();
        //获取房屋信息中的配置信息
        houseRentService.getHouseInfo(handlerGetInfo);
    }

    private void initView(){
        //页面标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(R.string.fragment_home_yellow_lease);
        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_house_rent_create = (Button) findViewById(R.id.btn_house_rent_create);
        //房屋照片
        ll_houserent_images = (LinearLayout) findViewById(R.id.ll_houserent_images);
        addImage=(ImageButton)findViewById(R.id.btn_picture_add);

        //是否带电梯
        addhouserent_rdoleft = (CheckBox) findViewById(R.id.addhouserent_rdoleft);
        //初始化EditText
        et_house_rent_detail_title = (EditText) findViewById(R.id.et_house_rent_detail_title);
        et_house_rent_detail_room = (EditText) findViewById(R.id.et_house_rent_detail_room);
        et_house_rent_detail_hall = (EditText) findViewById(R.id.et_house_rent_detail_hall);
        et_house_rent_detail_toilet = (EditText) findViewById(R.id.et_house_rent_detail_toilet);
        et_house_rent_detail_area = (EditText) findViewById(R.id.et_house_rent_detail_area);
        et_house_rent_detail_floor = (EditText) findViewById(R.id.et_house_rent_detail_floor);
        et_house_rent_detail_total_floor = (EditText) findViewById(R.id.et_house_rent_detail_total_floor);
        et_house_rent_detail_room_number = (EditText) findViewById(R.id.et_house_rent_detail_room_number);
        et_house_rent_detail_tel = (EditText) findViewById(R.id.et_house_rent_detail_tel);
        id_spinner_room_config_memo = (EditText) findViewById(R.id.id_spinner_room_config_memo);
        et_house_rent_detail_rent = (EditText) findViewById(R.id.et_house_rent_detail_rent);

        //设置金额输入
        InputFilter[] filters={new CashierInputFilter(1000000)};
        et_house_rent_detail_rent.setFilters(filters);

        //下拉spinner
        id_spinner_orientation = (Spinner) findViewById(R.id.id_spinner_orientation);
        id_spinner_fitment = (Spinner) findViewById(R.id.id_spinner_fitment);
        id_spinner_roomtype = (Spinner) findViewById(R.id.id_spinner_roomtype);
        id_spinner_room_config = (Spinner) findViewById(R.id.id_spinner_room_config);
        id_spinner_renttype = (Spinner) findViewById(R.id.id_spinner_renttype);

    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_house_rent_create.setOnClickListener(this);
        addImage.setOnClickListener(this);
    }

    //初始化适配器
    public void initData() {
        try {
            //朝向
            adapterOrientation = new ArrayAdapter<KValues>(this, R.layout.dropdown_check_item, mHouseProperties.getOrientation());
            adapterOrientation.setDropDownViewResource(R.layout.dropdown_item);
            id_spinner_orientation.setAdapter(adapterOrientation);
            id_spinner_orientation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Borientation = true;
                    try {
                        service.orientation = mHouseProperties.getOrientation().get(i).Key;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            //
            adapterFitmentType = new ArrayAdapter<KValues>(this, R.layout.dropdown_check_item, mHouseProperties.getFitmentType());
            //设置下拉列表的风格
            adapterFitmentType.setDropDownViewResource(R.layout.dropdown_item);
            id_spinner_fitment.setAdapter(adapterFitmentType);
            id_spinner_fitment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Bfitment = true;
                    try {
                        service.fitment = mHouseProperties.getFitmentType().get(i).Key;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            //
            adapterRoomType = new ArrayAdapter<KValues>(this, R.layout.dropdown_check_item, mHouseProperties.getRoomType());
            //设置下拉列表的风格
            adapterRoomType.setDropDownViewResource(R.layout.dropdown_item);
            id_spinner_roomtype.setAdapter(adapterRoomType);
            id_spinner_roomtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Broomtype = true;
                    try {
                        service.roomType = mHouseProperties.getRoomType().get(i).Key;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            //
            adapterRoomDeploy = new ArrayAdapter<KValues>(this, R.layout.dropdown_check_item, mHouseProperties.getRoomDeploy());
            adapterRoomDeploy.setDropDownViewResource(R.layout.dropdown_item);
            id_spinner_room_config.setAdapter(adapterRoomDeploy);
            id_spinner_room_config.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Broom_config = true;
                    try {
                        service.roomConfig = mHouseProperties.getRoomDeploy().get(i).Key;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            //
            adapterRentType = new ArrayAdapter<KValues>(this, R.layout.dropdown_check_item, mHouseProperties.getRentType());
            adapterRentType.setDropDownViewResource(R.layout.dropdown_item);
            id_spinner_renttype.setAdapter(adapterRentType);
            id_spinner_renttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Brenttype = true;
                    try {
                        service.rentType = mHouseProperties.getRentType().get(i).Key;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //数据校验
    public boolean isNull() {
        if (imagePathList.size()==0) {
            showToast("请选择照片");
            return false;
        }

        if (TextUtils.isEmpty(et_house_rent_detail_title.getText().toString().trim())) {
            showToast("标题不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_house_rent_detail_room.getText().toString().trim())) {
            showToast("房室不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_house_rent_detail_hall.getText().toString().trim())) {
            showToast("厅室不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_house_rent_detail_toilet.getText().toString().trim())) {
            showToast("卫室不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_house_rent_detail_area.getText().toString().trim())) {
            showToast("面积不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_house_rent_detail_floor.getText().toString().trim())) {
            showToast("楼层不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_house_rent_detail_total_floor.getText().toString().trim())) {
            showToast("总楼层不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_house_rent_detail_tel.getText().toString().trim())) {
            showToast("联系方式不能为空");
            return false;
        }
        if (service.orientation.equals("-1")) {
            showToast("请选择房屋朝向");
            return false;
        }
        if (service.fitment.equals("-1")) {
            showToast("请选择装修情况");
            return false;
        }
        if (service.roomType.equals("-1")) {
            showToast("请选择住宅类型");
            return false;
        }
        if (service.rentType.equals("-1")) {
            showToast("请选择租金类型");
            return false;
        }
        if (service.roomConfig.equals("-1")) {
            showToast("请选择房屋配置");
            return false;
        }
        if (TextUtils.isEmpty(et_house_rent_detail_rent.getText().toString().trim())) {
            showToast("租金不能为空");
            return false;
        }
        return true;
    }

    //获取用户输入数据
    private void getUserInput() {
        //标题，室，厅，卫，面积，几层，总层数，房号，联系电话，带家具备注，租赁价格
        title = et_house_rent_detail_title.getText().toString();
        room = et_house_rent_detail_room.getText().toString();
        hall = et_house_rent_detail_hall.getText().toString();
        toilet = et_house_rent_detail_toilet.getText().toString();
        area = et_house_rent_detail_area.getText().toString();
        floor = et_house_rent_detail_floor.getText().toString();
        totalfloor = et_house_rent_detail_total_floor.getText().toString();
        tel = et_house_rent_detail_tel.getText().toString();
        rentPrice = et_house_rent_detail_rent.getText().toString();
        isElevator = String.valueOf(addhouserent_rdoleft.isChecked());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_house_rent_create://发布
                //是否带电梯
                isElevator = String.valueOf(addhouserent_rdoleft.isChecked());
                User user = UserInformation.getUserInfo();
                if (isNull()) {
                    getUserInput();
                    houseRentService.addHouseRent("", user.CommunityId, title, title,
                            room, hall, toilet, area,
                            floor, totalfloor, service.orientation, service.fitment,
                            service.roomType, service.roomConfig, rentPrice, service.rentType,
                            Utility.ListToString(imagePathList), tel, "1", isElevator, user.UserId,handler);
                }
                break;
            case R.id.btn_back:
                crenteCencalDialog();
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
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            crenteCencalDialog();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //退出对话框
    private void crenteCencalDialog() {
        MyDialog dialog = new MyDialog(this);
        dialog.show();
        dialog.setDialogCallback(dialogcallback);
    }

    MyDialog.Dialogcallback dialogcallback = new MyDialog.Dialogcallback() {
        @Override
        public void dialogdo() {
            try {
                gotoActivityAndFinish(HouseRent_List.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void dialogDismiss() {
        }
    };

    //压缩、保存、上传所选图片
    private void showImage(final String path) {
        File file = new File(path);
        Bitmap bitmap = CompressHelper.getDefault(HouseRent_Create.this).compressToBitmap(file);
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

        //上传图片
        new Thread() {
            @Override
            public void run() {
                super.run();
                String fileId =service.Upload(HouseRent_Create.this, path).FileName;
                imagePathList.add(fileId);
            }
        }.start();

        //显示图片
        creationImg(file.getAbsolutePath());

    }

    //视图上创建图片
    public void creationImg(final String imagePath) {
        ImageView iv = new ImageView(HouseRent_Create.this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //添加到父布局
        ll_houserent_images.addView(iv, ll_houserent_images.getChildCount() - 1);
        //设置要添加的ImageView的尺寸、坐标
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
        linearParams.setMargins(linearParams.leftMargin, linearParams.topMargin, Utility.dip2px(HouseRent_Create.this,
                getResources().getDimension(R.dimen.dimen_2dp)), linearParams.bottomMargin);
        linearParams.width = Utility.dip2px(this, 64f);
        linearParams.height = Utility.dip2px(this, 64f);
        iv.setLayoutParams(linearParams);

        //显示头像
        ImageLoader.getInstance().displayImage("file://" + imagePath, iv, Utility.imageOptions);
        //最多上传5张照片
        if (ll_houserent_images.getChildCount() == 6) {
            addImage.setVisibility(View.GONE);
        }else{
            addImage.setVisibility(View.VISIBLE);
        }

        //添加图片长按事件，用于删除
        for (int i = 0; i < ll_houserent_images.getChildCount(); i++) {
            ImageView itemView = (ImageView) ll_houserent_images.getChildAt(i);
            if (itemView != addImage) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int index = ll_houserent_images.indexOfChild(v);
                        imagePathList.remove(index);
                        ll_houserent_images.removeViewAt(index);
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

   //获取适配数据
    private void getAdapterData(JSONObject jsonObject){
        mHouseProperties = new HouseProperties();
        try {
            mHouseProperties.setOrientation(jsonObject.getString("Orientation"));
            mHouseProperties.setFitmentType(jsonObject.getString("FitmentType"));
            mHouseProperties.setRentType(jsonObject.getString("RentType"));
            mHouseProperties.setRoomDeploy(jsonObject.getString("RoomDeploy"));
            mHouseProperties.setRoomType(jsonObject.getString("RoomType"));
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    Handler handlerGetInfo=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    JSONObject jsonObject=(JSONObject) msg.obj;
                    getAdapterData(jsonObject);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    try {
                        if (!TextUtils.isEmpty(UserInformation.getUserInfo().getPropertyId())) {
                            showToast(R.string.tenement_review_has_been_submitted);
                        }
                        gotoActivityAndFinish(HouseRent_List.class.getName(), null);
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
    protected void onDestroy() {
        Log.e("aa","test_onDestroy");
        super.onDestroy();
    }
}
