package com.ldnet.activity.homeinspectionmanage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.interfaze.PictureChoseListener;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.Utility;
import com.nanchen.compresshelper.CompressHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddInspectExceptionActivity extends BaseActionBarActivity {

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.et_location)
    EditText etLocation;
    @BindView(R.id.et_check_item)
    EditText etCheckItem;
    @BindView(R.id.et_describe)
    EditText etDescribe;
    @BindView(R.id.btn_picture_add)
    ImageButton addImage;
    @BindView(R.id.ll_picture_list)
    LinearLayout llPictureList;
    @BindView(R.id.btn_create)
    Button btnCreate;

    private List<String> imagePathList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_inspect_exception);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        tvPageTitle.setText("添加异常");
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "房屋验收-创建异常：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "房屋验收-创建异常：" + this.getClass().getSimpleName());
    }


    @OnClick({R.id.btn_back, R.id.btn_picture_add, R.id.btn_create})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
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
            case R.id.btn_create:
                break;
        }
    }


    //压缩、保存、上传所选图片
    private void showImage(final String path) {
        File file = new File(path);
        Bitmap bitmap = CompressHelper.getDefault(AddInspectExceptionActivity.this).compressToBitmap(file);
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
                String fileId =new Services().Upload(AddInspectExceptionActivity.this, path).FileName;
                imagePathList.add(fileId);
            }
        }.start();

        //显示图片
        creationImg(file.getAbsolutePath());
    }

    //视图上创建图片
    public void creationImg(final String imagePath) {
        ImageView iv = new ImageView(AddInspectExceptionActivity.this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //添加到父布局
        llPictureList.addView(iv, llPictureList.getChildCount() - 1);
        //设置要添加的ImageView的尺寸、坐标
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv.getLayoutParams();
        linearParams.setMargins(linearParams.leftMargin, linearParams.topMargin,
                Utility.dip2px(AddInspectExceptionActivity.this,
                        getResources().getDimension(R.dimen.dimen_2dp)), linearParams.bottomMargin);
        linearParams.width = Utility.dip2px(this, 64f);
        linearParams.height = Utility.dip2px(this, 64f);
        iv.setLayoutParams(linearParams);

        //显示头像
        ImageLoader.getInstance().displayImage("file://" + imagePath, iv, Utility.imageOptions);
        //最多上传5张照片
        if (llPictureList.getChildCount() == 6) {
            addImage.setVisibility(View.GONE);
        }else{
            addImage.setVisibility(View.VISIBLE);
        }

        //添加图片长按事件，用于删除
        for (int i = 0; i < llPictureList.getChildCount(); i++) {
            ImageView itemView = (ImageView) llPictureList.getChildAt(i);
            if (itemView != addImage) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int index = llPictureList.indexOfChild(v);
                        imagePathList.remove(index);
                        llPictureList.removeViewAt(index);
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

}
