package com.ldnet.activity.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.ldnet.activity.find.FreaMarket_Create;
import com.ldnet.activity.find.Weekend_Create;
import com.ldnet.activity.home.HouseRentUpdate;
import com.ldnet.activity.home.HouseRent_Create;
import com.ldnet.activity.home.Property_Complain_Create;
import com.ldnet.activity.home.Property_Repair_Create;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 图片选择
 */
public class ImageChooseActivity extends Activity {
    private List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private String mBucketName;
    private int availableSize;
    private GridView mGridView;
    private TextView mBucketNameTv;
    private TextView cancelTv;
    private ImageGridAdapter mAdapter;
    private Button mFinishBtn;
    private HashMap<String, ImageItem> selectedImgs = new HashMap<String, ImageItem>();
    private String classname = "";
    public static ImageChooseActivity instance;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_image_choose);
        instance = this;
        classname = getIntent().getStringExtra("classname");
        mDataList = (List<ImageItem>) getIntent().getSerializableExtra(
                IntentConstants.EXTRA_IMAGE_LIST);
        if (mDataList == null) mDataList = new ArrayList<ImageItem>();
        mBucketName = getIntent().getStringExtra(
                IntentConstants.EXTRA_BUCKET_NAME);

        if (TextUtils.isEmpty(mBucketName)) {
            mBucketName = "请选择";
        }
        availableSize = getIntent().getIntExtra(
                IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                CustomConstants.MAX_IMAGE_SIZE);

        initView();
        initListener();

    }

    private void initView() {
        mBucketNameTv = (TextView) findViewById(R.id.title);
        mBucketNameTv.setText(mBucketName);

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImageGridAdapter(ImageChooseActivity.this, mDataList);
        mGridView.setAdapter(mAdapter);
        mFinishBtn = (Button) findViewById(R.id.finish_btn);
        cancelTv = (TextView) findViewById(R.id.action);


        mFinishBtn.setText("完成" + "(" + selectedImgs.size() + "/"
                + availableSize + ")");
        mAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        mFinishBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (selectedImgs != null && selectedImgs.size() > 0) {
//                    com.ldnet.utility.Toast.makeText(ImageChooseActivity.this,"图片上传中，请稍等...",1000).show();
                    if (classname.equals("FreaMarket_Create")) {
                        Intent intent = new Intent(ImageChooseActivity.this,
                                FreaMarket_Create.class);
                        intent.putExtra(
                                IntentConstants.EXTRA_IMAGE_LIST,
                                (Serializable) new ArrayList<ImageItem>(selectedImgs
                                        .values()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
//                        finish();
                    } else if (classname.equals("Weekend_Create")) {
                        Intent intent = new Intent(ImageChooseActivity.this,
                                Weekend_Create.class);
                        intent.putExtra(
                                IntentConstants.EXTRA_IMAGE_LIST,
                                (Serializable) new ArrayList<ImageItem>(selectedImgs
                                        .values()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
//                        finish();
                    } else if (classname.equals("HouseRent_Create")) {
                        Intent intent = new Intent(ImageChooseActivity.this,
                                HouseRent_Create.class);
                        intent.putExtra(
                                IntentConstants.EXTRA_IMAGE_LIST,
                                (Serializable) new ArrayList<ImageItem>(selectedImgs
                                        .values()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
//                        finish();
                    } else if (classname.equals("HouseRentUpdate")) {
                        Intent intent = new Intent(ImageChooseActivity.this,
                                HouseRentUpdate.class);
                        intent.putExtra(
                                IntentConstants.EXTRA_IMAGE_LIST,
                                (Serializable) new ArrayList<ImageItem>(selectedImgs
                                        .values()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
//                        finish();
                    } else if (classname.equals("Property_Complain_Create")) {
                        Intent intent = new Intent(ImageChooseActivity.this,
                                Property_Complain_Create.class);
                        intent.putExtra(
                                IntentConstants.EXTRA_IMAGE_LIST,
                                (Serializable) new ArrayList<ImageItem>(selectedImgs
                                        .values()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
//                        finish();
                    } else if (classname.equals("Property_Repair_Create")) {
                        Intent intent = new Intent(ImageChooseActivity.this,
                                Property_Repair_Create.class);
                        intent.putExtra(
                                IntentConstants.EXTRA_IMAGE_LIST,
                                (Serializable) new ArrayList<ImageItem>(selectedImgs
                                        .values()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
//                        finish();
                    }
                }else{
                    com.ldnet.utility.Toast.makeText(ImageChooseActivity.this,"您未选择图片，请选择图片",1000).show();
                }

            }

        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ImageItem item = mDataList.get(position);
                if (item.isSelected) {
                    item.isSelected = false;
                    selectedImgs.remove(item.imageId);
                } else {
                    if (selectedImgs.size() >= availableSize) {
                        Toast.makeText(ImageChooseActivity.this,
                                "最多选择" + availableSize + "张图片",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    item.isSelected = true;
                    selectedImgs.put(item.imageId, item);
                }

                mFinishBtn.setText("完成" + "(" + selectedImgs.size() + "/"
                        + availableSize + ")");
                mAdapter.notifyDataSetChanged();
            }

        });

        cancelTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (classname.equals("FreaMarket_Create")) {
                    Intent intent = new Intent(ImageChooseActivity.this,
                            FreaMarket_Create.class);
                    intent.putExtra("flag","true");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    finish();
                } else if (classname.equals("Weekend_Create")) {
                    Intent intent = new Intent(ImageChooseActivity.this,
                            Weekend_Create.class);
                    intent.putExtra("flag","true");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    finish();
                } else if (classname.equals("HouseRent_Create")) {
                    Intent intent = new Intent(ImageChooseActivity.this,
                            HouseRent_Create.class);
                    intent.putExtra("flag","true");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    finish();
                } else if (classname.equals("Property_Repair_Create")) {
                    Intent intent = new Intent(ImageChooseActivity.this,
                            Property_Repair_Create.class);
                    intent.putExtra("flag","true");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    finish();
                } else if (classname.equals("HouseRentUpdate")) {
                    Intent intent = new Intent(ImageChooseActivity.this,
                            HouseRentUpdate.class);
                    intent.putExtra("flag","true");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    finish();
                }else if (classname.equals("Property_Complain_Create")) {
                    Intent intent = new Intent(ImageChooseActivity.this,
                            Property_Complain_Create.class);
                    intent.putExtra("flag","true");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    finish();
                }

            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (classname.equals("FreaMarket_Create")) {
                Intent intent = new Intent(ImageChooseActivity.this,
                        FreaMarket_Create.class);
                intent.putExtra("flag","true");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
            } else if (classname.equals("Weekend_Create")) {
                Intent intent = new Intent(ImageChooseActivity.this,
                        Weekend_Create.class);
                intent.putExtra("flag","true");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
            } else if (classname.equals("HouseRent_Create")) {
                Intent intent = new Intent(ImageChooseActivity.this,
                        HouseRent_Create.class);
                intent.putExtra("flag","true");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
            } else if (classname.equals("Property_Repair_Create")) {
                Intent intent = new Intent(ImageChooseActivity.this,
                        Property_Repair_Create.class);
                intent.putExtra("flag","true");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
            } else if (classname.equals("HouseRentUpdate")) {
                Intent intent = new Intent(ImageChooseActivity.this,
                        HouseRentUpdate.class);
                intent.putExtra("flag","true");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
            }else if (classname.equals("Property_Complain_Create")) {
                Intent intent = new Intent(ImageChooseActivity.this,
                        Property_Complain_Create.class);
                intent.putExtra("flag","true");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}