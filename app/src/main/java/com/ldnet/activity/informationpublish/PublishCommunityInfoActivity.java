package com.ldnet.activity.informationpublish;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.InfoBarType;
import com.ldnet.goldensteward.R;
import com.ldnet.interfaze.PictureChoseListener;
import com.ldnet.service.BaseService;
import com.ldnet.service.InfoBarService;
import com.ldnet.utility.GSApplication;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Services;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.nanchen.compresshelper.CompressHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.ldnet.activity.informationpublish.CommunityInfoBarMainActivity.currentBigType;

public class PublishCommunityInfoActivity extends BaseActionBarActivity {

    private TextView tvTitle,tvCustom,tvType,tvImageCount,tvContentLength;
    private ImageView btnBack;
    private LinearLayout llChoseType,llImageList;
    private EditText etTitle,etContent,etPhone;
    private ImageButton addImage;
    private PopupWindow popWindow,popTypeWindow;
    private String currentUploadImageId;
    private String currentLittleTypeId;
    private List<String> imagePathList=new ArrayList<>();
    private LayoutInflater inflater;
    private List<InfoBarType> allTypeList=new ArrayList<>();
    private Services services;
    private String tag=PublishCommunityInfoActivity.class.getSimpleName();
    private InfoBarService infoBarService;
    private int position;
    private RadioGroup radioGroup;
    private RadioButton radioButtonPrivide,radioButtonNeed;
    private int defaultType;
    private String need;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info_bar);
        services=new Services();
        infoBarService=new InfoBarService(this);
        inflater=LayoutInflater.from(PublishCommunityInfoActivity.this);

        allTypeList=(List<InfoBarType>) getIntent().getSerializableExtra("TYPE");
        position=getIntent().getIntExtra("ITEM",0);
        need=getIntent().getStringExtra("NEED");
        allTypeList.remove(0);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "邻里通发布：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "邻里通发布：" + this.getClass().getSimpleName());
    }


    private void initView(){
        tvType=(TextView)findViewById(R.id.tv_add_info_bar_type);
        tvCustom=(TextView)findViewById(R.id.tv_custom);
        tvTitle=(TextView)findViewById(R.id.tv_page_title);
        tvContentLength=(TextView)findViewById(R.id.tv_content_length);
        btnBack=(ImageView)findViewById(R.id.btn_back);
        etContent=(EditText)findViewById(R.id.et_add_info_bar_content);
        etPhone=(EditText)findViewById(R.id.et_add_info_bar_telephone);
        etTitle=(EditText)findViewById(R.id.et_add_info_bar_title);
        llChoseType=(LinearLayout)findViewById(R.id.ll_add_info_bar_chose_type);
        llImageList=(LinearLayout)findViewById(R.id.ll_add_info_bar_picture_list);
        addImage=(ImageButton) findViewById(R.id.btn_picture_add);
        tvImageCount=(TextView)findViewById(R.id.tv_add_image_count);
        radioGroup=(RadioGroup)findViewById(R.id.radio_group_publish_info_bar);
        radioButtonNeed=(RadioButton)findViewById(R.id.radio_button_need) ;
        radioButtonPrivide =(RadioButton)findViewById(R.id.radio_button_privide);
        tvCustom.setVisibility(View.VISIBLE);
        tvCustom.setText("发布");
        tvTitle.setText("发布");
        tvTitle.setTextColor(Color.BLACK);

        btnBack.setOnClickListener(this);
        tvCustom.setOnClickListener(this);
        llChoseType.setOnClickListener(this);
        addImage.setOnClickListener(this);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //需求是1，供应是0
                if (checkedId==R.id.radio_button_need){
                    defaultType=1;
                    radioButtonNeed.setTextColor(Color.WHITE);
                    radioButtonPrivide.setTextColor(Color.parseColor("#9b9b9b"));

                }else{
                    defaultType=0;
                    radioButtonPrivide.setTextColor(Color.WHITE);
                    radioButtonNeed.setTextColor(Color.parseColor("#9b9b9b"));

                }

            }
        });


        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvContentLength.setText(s.length()+"/200");
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_back:
                Intent intent=new Intent(PublishCommunityInfoActivity.this,CommunityInfoBarMainActivity.class);
                intent.putExtra("ITEM",position);
                intent.putExtra("NEED",need);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_custom:
                if (isNotNull()) {
                    showProgressDialog();
                    infoBarService.addInfoAction(String.valueOf(defaultType),
                            etTitle.getText().toString(),
                            etContent.getText().toString(),
                            currentLittleTypeId,
                            etPhone.getText().toString(),
                            imagePathList.get(0),
                            Utility.ListToString(imagePathList), handlerSubmitInfo);
                }
                break;
            case R.id.ll_add_info_bar_chose_type:
                if (allTypeList.size()>0){
                    showTypePopWindow(allTypeList);
                }else {
                    showToast("暂无类别可选");
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
        }
    }


    private boolean isNotNull() {

        if (Utility.editIsNull(etTitle)) {
            showToast("请输入标题");
            return false;
        }
        if (Utility.editIsNull(etContent)) {
            showToast("请输入内容");
            return false;
        }
        if (imagePathList == null || imagePathList.size() == 0) {
            showToast("请添加图片");
            return false;
        }
        if (TextUtils.isEmpty(currentLittleTypeId)) {
            showToast("请选择类别");
            return false;
        }
        if (Utility.editIsNull(etPhone)) {
            showToast("请输入电话");
            return false;
        }
        return true;
    }


    Handler handlerSubmitInfo =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("发布成功");
                    finish();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };
    //弹出选择类别
    public void showTypePopWindow(List<InfoBarType> list){
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        if (popTypeWindow == null) {
            View view2 = LayoutInflater.from(PublishCommunityInfoActivity.this).inflate(R.layout.pop_info_bar_chose_type, null);
            popTypeWindow = new PopupWindow(view2, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            initTypePop(view2,list);
        }
        popTypeWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popTypeWindow.setFocusable(true);
        popTypeWindow.setOutsideTouchable(true);
        popTypeWindow.setBackgroundDrawable(new BitmapDrawable());
        popTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Utility.backgroundAlpaha(PublishCommunityInfoActivity.this,0.5f);
        popTypeWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

        popTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popTypeWindow=null;
                Utility.backgroundAlpaha(PublishCommunityInfoActivity.this,1f);
            }
        });
    }

    //初始化类别选择View
    private void initTypePop(View view,final List<InfoBarType> list){
        TextView tvComplete=(TextView)view.findViewById(R.id.tv_add_info_bar_chose_complete);
        ListView listView=(ListView)view.findViewById(R.id.listview_add_info_bar_type);
        ListViewAdapter<InfoBarType> adapter=new ListViewAdapter<InfoBarType>(PublishCommunityInfoActivity.this,R.layout.item_drop_down,list) {
            @Override
            public void convert(ViewHolder holder, InfoBarType infoBarType) {
                TextView textView=holder.getView(R.id.tv_community_room);
                textView.setBackgroundColor(Color.parseColor("#F8F8F8"));
                textView.setTextColor(Color.parseColor("#4A4A4A"));
                textView.setText(infoBarType.name);
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentLittleTypeId =String.valueOf(list.get(position).value);
                tvType.setText(list.get(position).name);
                popTypeWindow.dismiss();
            }
        });

        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popTypeWindow.dismiss();
            }
        });
    }

    //压缩、保存所选图片
    private void showImage(final String path) {
        File file = new File(path);
        Bitmap bitmap = CompressHelper.getDefault(PublishCommunityInfoActivity.this).compressToBitmap(file);
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
                String fileId = services.Upload(PublishCommunityInfoActivity.this, path).FileName;
                imagePathList.add(fileId);
                Log.e("file", "已将上传："+fileId);
            }
        }.start();


        //显示图片
        creationImg(file.getAbsolutePath());
    }

    //视图上创建图片
    public void creationImg(final String imagePath) {
        ImageView iv = new ImageView(PublishCommunityInfoActivity.this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //添加到父布局
        llImageList.addView(iv, llImageList.getChildCount() - 1);
        //设置要添加的ImageView的尺寸、坐标
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
        linearParams.setMargins(linearParams.leftMargin, linearParams.topMargin, Utility.dip2px(PublishCommunityInfoActivity.this,
                getResources().getDimension(R.dimen.dimen_2dp)), linearParams.bottomMargin);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) addImage.getLayoutParams();
        linearParams.width = params.width;
        linearParams.height = params.height;
        iv.setLayoutParams(linearParams);

        //显示头像
        if (TextUtils.isEmpty(imagePath)) {
            ImageLoader.getInstance().displayImage(Services.getImageUrl(currentUploadImageId), iv, Utility.imageOptions);
        } else {
            ImageLoader.getInstance().displayImage("file://" + imagePath, iv, Utility.imageOptions);
        }
        //最多上传5张照片
        if (imagePathList.size() == 4) {
            addImage.setVisibility(View.GONE);
            tvImageCount.setText("5/5");
        }else{
            tvImageCount.setText(imagePathList.size()+1+"/5");
        }


        //添加图片长按事件，用于删除
        for (int i = 0; i < llImageList.getChildCount(); i++) {
            ImageView itemView = (ImageView) llImageList.getChildAt(i);
            if (itemView != addImage) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int index = llImageList.indexOfChild(v);
                        imagePathList.remove(index);
                        llImageList.removeViewAt(index);
                        tvImageCount.setText(imagePathList.size()+"/5");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent(PublishCommunityInfoActivity.this,CommunityInfoBarMainActivity.class);
            intent.putExtra("ITEM",position);
            intent.putExtra("NEED",need);
            startActivity(intent);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
