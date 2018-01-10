package com.ldnet.activity.find;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.ldnet.activity.adapter.ImageBucketChooseActivity;
import com.ldnet.activity.adapter.ImageChooseActivity;
import com.ldnet.activity.adapter.ImageItem;
import com.ldnet.activity.adapter.ImagePublishAdapter;
import com.ldnet.activity.adapter.ImageZoomActivity;
import com.ldnet.activity.adapter.MyDialog;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.home.Property_Repair;
import com.ldnet.activity.home.Property_Repair_Create;
import com.ldnet.activity.me.Publish;
import com.ldnet.activity.me.PublishActivity;
import com.ldnet.entities.FreaMarketDetails;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.interfaze.PictureChoseListener;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.FindService;
import com.ldnet.utility.CashierInputFilter;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.CustomConstants;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.GSApplication;
import com.ldnet.utility.IntentConstants;
import com.ldnet.utility.SDCardFileCache;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.Utility;
import com.nanchen.compresshelper.CompressHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

import static android.R.string.no;
import static android.icu.text.UnicodeSet.CASE;
import static com.ldnet.goldensteward.R.id.et_weekend_cost;
import static com.ldnet.goldensteward.R.id.ll_repair_picture_list;
import static com.ldnet.utility.Services.IntegralTip;

public class FreaMarket_Create extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private EditText et_frea_market_title;
    private LinearLayout ll_frea_market_picture_list;
    private ImageButton addImage;
    private EditText et_frea_market_content;
    private EditText et_frea_market_new_price;
    private EditText et_frea_market_think_price;
    private Button btn_frea_market_confirm;

    public static String mImageIds;
    private static String mFreamarketId;
    private static Boolean mFromPublish = false;
    private static Boolean mFromFreaMarketDetails = false;
    FreaMarketDetails details;
    private String oldImg = "";
    public static List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private static String title = "", price = "", price1 = "", content = "";
    private boolean flag = false;
    private String FreaMarket = "";
    private List<String> imagePathList=new ArrayList<>();
    private FindService findService;
    private AcountService acountService;
    private  String url = Services.mHost + "API/Resident/UnUsedGoodsAdd";
    private String urlUpdate = Services.mHost + "API/Resident/UnUsedGoodsUpdate";
    private final int MAX_VALUE=1000000;
    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_fleamarket_create);
        AppUtils.setupUI(findViewById(R.id.ll_freamark_creat), this);
        if (ImageChooseActivity.instance != null) {
            ImageChooseActivity.instance.finish();
        }

        initView();
        initEvent();
        initService();

        String formPublish = getIntent().getStringExtra("FROM_PUBLISH");
        String fromFreaMarketDetails = getIntent().getStringExtra("FROM_FREAMARKET_DETAILS");

        if (!TextUtils.isEmpty(formPublish) || !TextUtils.isEmpty(fromFreaMarketDetails)) {  //来自我的发布，查看详情，编辑
            mFromPublish = Boolean.valueOf(formPublish);
            mFromFreaMarketDetails = Boolean.valueOf(fromFreaMarketDetails);
        }

        //来自我的发布或者详细编辑，显示修改信息
        if (mFromPublish || mFromFreaMarketDetails) {
            //获取详情
            mFreamarketId = getIntent().getStringExtra("FREA_MARKET_ID");   //闲置物品ID
            showProgressDialog();
            findService.getFreaMarketDetails(mFreamarketId,handlerGetDetail);
        }
    }


    private void initService(){
        services = new Services();
        findService=new FindService(this);
        acountService=new AcountService(this);
    }

    private void initView(){
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.frea_market_publish);
        addImage=(ImageButton) findViewById(R.id.btn_picture_add);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        et_frea_market_title = (EditText) findViewById(R.id.et_frea_market_title);
        ll_frea_market_picture_list = (LinearLayout) findViewById(R.id.ll_frea_market_picture_list);
        et_frea_market_content = (EditText) findViewById(R.id.et_frea_market_content);
        et_frea_market_new_price = (EditText) findViewById(R.id.et_frea_market_new_price);
        et_frea_market_think_price = (EditText) findViewById(R.id.et_frea_market_think_price);
        btn_frea_market_confirm = (Button) findViewById(R.id.btn_frea_market_confirm);

        //设置金额输入
        InputFilter[] filters={new CashierInputFilter(MAX_VALUE)};
        et_frea_market_new_price.setFilters(filters);
        et_frea_market_think_price.setFilters(filters);
    }

    public boolean isNull() {
        if (TextUtils.isEmpty(et_frea_market_title.getText().toString().trim())) {
            showToast("标题不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_frea_market_new_price.getText().toString().trim())) {
            showToast("新品价格不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_frea_market_think_price.getText().toString().trim())) {
            showToast("一口价不能为空");
            return false;
        }

        if (imagePathList.size()==0){
            showToast("请选择照片");
            return false;
        }

        if (TextUtils.isEmpty(et_frea_market_content.getText().toString().trim())) {
            showToast("介绍不能为空");
            return false;
        }
        return true;
    }

    //初始化事件
    public void initEvent() {
        addImage.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_frea_market_confirm.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back://判断是否取消
                crenteCencalDialog();
                break;
            case R.id.btn_frea_market_confirm://发布/修改
                String title = et_frea_market_title.getText().toString().trim();
                String content = et_frea_market_content.getText().toString().trim();
                String newPrice = et_frea_market_new_price.getText().toString().trim();
                String thinkPrice = et_frea_market_think_price.getText().toString().trim();
                if (isNull()) {
                    showProgressDialog();
                    if (mFromPublish || mFromFreaMarketDetails) {  //修改
                        findService.updateUnUsedGoods(mFreamarketId, title, content, Utility.ListToString(imagePathList), newPrice, thinkPrice, handlerUpdate);
                    } else {   //发布
                        findService.FreaMarketCreate(title, content, Utility.ListToString(imagePathList), newPrice, thinkPrice, handlerAdd);
                    }
                }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            crenteCencalDialog();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
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
                Intent intent1 = new Intent(FreaMarket_Create.this, PublishActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
            } else {
                Intent intent1 = new Intent(FreaMarket_Create.this, FreaMarket.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
            }
        }

        @Override
        public void dialogDismiss() {
        }
    };

    //压缩、保存、上传所选图片
    private void showImage(final String path) {
        File file = new File(path);
        Bitmap bitmap = CompressHelper.getDefault(FreaMarket_Create.this).compressToBitmap(file);
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
                String fileId =new Services().Upload(FreaMarket_Create.this, path).FileName;
                imagePathList.add(fileId);
            }
        }.start();

        //显示图片
        creationImg(file.getAbsolutePath(),true);
    }

    //视图上创建图片
    public void creationImg(final String imagePath,final boolean notID) {
        ImageView iv = new ImageView(FreaMarket_Create.this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //添加到父布局
        ll_frea_market_picture_list.addView(iv, ll_frea_market_picture_list.getChildCount() - 1);
        //设置要添加的ImageView的尺寸、坐标
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
        linearParams.setMargins(linearParams.leftMargin, linearParams.topMargin,
                Utility.dip2px(FreaMarket_Create.this,
                        getResources().getDimension(R.dimen.dimen_2dp)), linearParams.bottomMargin);
        linearParams.width = Utility.dip2px(this, 64f);
        linearParams.height = Utility.dip2px(this, 64f);
        iv.setLayoutParams(linearParams);

        //显示头像
        if (notID){  //imagePath是图片路径
            ImageLoader.getInstance().displayImage("file://" + imagePath, iv, Utility.imageOptions);
        }else{      //imagePath是图片ID
            ImageLoader.getInstance().displayImage(Services.getImageUrl(imagePath), iv, Utility.imageOptions);
        }
        //最多上传5张照片
        if (ll_frea_market_picture_list.getChildCount() == 6) {
            addImage.setVisibility(View.GONE);
        }else{
            addImage.setVisibility(View.VISIBLE);
        }

        //添加图片长按事件，用于删除
        for (int i = 0; i < ll_frea_market_picture_list.getChildCount(); i++) {
            ImageView itemView = (ImageView) ll_frea_market_picture_list.getChildAt(i);
            if (itemView != addImage) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int index = ll_frea_market_picture_list.indexOfChild(v);
                        imagePathList.remove(index);
                        ll_frea_market_picture_list.removeViewAt(index);
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
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("新增成功");
                    try {
                        gotoActivityAndFinish(FreaMarket.class.getName(), null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    acountService.setIntegralTip(new Handler(),url);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerUpdate=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("修改成功");
                    try {
                        gotoActivityAndFinish(PublishActivity.class.getName(), null);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    acountService.setIntegralTip(new Handler(),urlUpdate);
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
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    details=(FreaMarketDetails) msg.obj;
                    et_frea_market_title.setText(details.Title);
                    et_frea_market_new_price.setText(details.OrgPrice);
                    et_frea_market_think_price.setText("" + details.Price);
                    et_frea_market_content.setText(details.Memo);
                    btn_frea_market_confirm.setText("修改发布");
                    tv_main_title.setText("修改闲置物品信息");

                    if (details.Img.size() > 0) {
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





}
