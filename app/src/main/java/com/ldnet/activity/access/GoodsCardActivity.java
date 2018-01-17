package com.ldnet.activity.access;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autonavi.rtbt.IFrameForRTBT;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.BottomDialog;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;

import net.tsz.afinal.core.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ldnet.goldensteward.R.id.tv_access_goods_card_date;
import static com.ldnet.utility.Utility.getCacheBitmapFromView;

public class GoodsCardActivity extends BaseActionBarActivity {

    private TextView tvName, tvDate, tvTel, tvStatus, tvApproveDate;
    private String date, status, imageId, approveDate;
    private ImageView imageBar;
    private String imageUrl;
    private Services services;
    private LinearLayout llBack;
    private TextView title;
    private ImageButton back, btnShare;
    private Bitmap bitmapShare;
    private SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
    private SimpleDateFormat mformat2 = new SimpleDateFormat("yyyy-MM-DD HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_card);

        services = new Services();
        initView();

        Intent intent = getIntent();
        date = intent.getStringExtra("DATE");
        imageId = intent.getStringExtra("IMAGE_ID");
        status = intent.getStringExtra("STATUS");
        approveDate = intent.getStringExtra("APPROVE_DATE");
        //二维码图片
        if (!TextUtils.isEmpty(imageId)) {
            imageUrl = services.getImageUrl(imageId);
            new MyTask().execute(imageUrl);
        } else {
            showToast("二维码获取失败");
        }

        if (!TextUtils.isEmpty(UserInformation.getUserInfo().UserName)) {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(UserInformation.getUserInfo().getUserName());
        } else {
            tvName.setVisibility(View.GONE);
        }

        //出门时间
        if (!TextUtils.isEmpty(date)) {
            tvDate.setVisibility(View.VISIBLE);
            tvDate.setText("出门时间："+date);
        } else {
            tvDate.setVisibility(View.GONE);
        }

        //审核通过时间
        if (!TextUtils.isEmpty(approveDate)) {
            try {
                Date date2 = mformat.parse(approveDate);
                tvApproveDate.setText("审核时间：" + mformat2.format(date2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //审核状态
        if (!TextUtils.isEmpty(status) && status.equals("1")) {       //审核通过，未登记
            tvStatus.setText("审核状态：已通过");
            llBack.setBackgroundResource(R.drawable.goods_card_back1);
        } else if (!TextUtils.isEmpty(status) && status.equals("3")) {   //已登记
            tvStatus.setText("审核状态：已登记");
            llBack.setBackgroundResource(R.drawable.goods_card_back2);
        } else if (!TextUtils.isEmpty(status) && status.equals("4")) {  //过期
            tvStatus.setText("审核状态：已过期");
            llBack.setBackgroundResource(R.drawable.goods_card_back3);
        }
    }


    private void initView() {
        title = (TextView) findViewById(R.id.tv_page_title);
        title.setText("出门证");
        back = (ImageButton) findViewById(R.id.btn_back);

        tvDate = (TextView) findViewById(tv_access_goods_card_date);
        tvName = (TextView) findViewById(R.id.tv_access_goods_card_user_name);
        tvTel = (TextView) findViewById(R.id.tv_access_goods_card_tel);
        tvStatus = (TextView) findViewById(R.id.tv_access_goods_card_status);
        llBack = (LinearLayout) findViewById(R.id.ll_goods_access_back);
        imageBar = (ImageView) findViewById(R.id.iv_tv_access_goods_card_bar);
        tvApproveDate = (TextView) findViewById(R.id.tv_access_goods_card_approve_date);
        btnShare = (ImageButton) findViewById(R.id.btn_custom);
        btnShare.setVisibility(View.VISIBLE);
        btnShare.setImageResource(R.drawable.shares);
        back.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }


    public class MyTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL fileUrl = null;
            Bitmap bitmap = null;
            try {
                fileUrl = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection conn = (HttpURLConnection) fileUrl
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            closeProgressDialog();
            imageBar.setImageBitmap(bitmap);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(AccessControlMain.class.getName(),null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_custom:
                bitmapShare = getCacheBitmapFromView(llBack);
                if (bitmapShare != null) {
                    BottomDialog dialog = new BottomDialog(GoodsCardActivity.this, "出入证", bitmapShare);
                    dialog.uploadImageUI(GoodsCardActivity.this);
                }
                break;
        }
    }
}
