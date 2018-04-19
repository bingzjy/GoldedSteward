package com.ldnet.activity.accessmanage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.view.dialog.BottomDialog;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.tendcloud.tenddata.TCAgent;

import net.tsz.afinal.core.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import static com.ldnet.utility.Utility.getCacheBitmapFromView;

public class VisitorCardActivity extends BaseActionBarActivity {
    private String imageURL;
    private String fromClassName;
    private Services services;
    private ImageView imageBar;
    private TextView title, tvVisitorName, tvVisitorTel, tvVisitorDate, tvUserName, tvUserTel, tvUserRoom;
    private ImageButton btnBack, btnShare;
    private Bitmap bitmapShare;
    private String name, date, phone, status, imageId,room;
    private LinearLayout llBack,llVisitorBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_card);

        initView();
        services = new Services();

        Intent intent = getIntent();
        fromClassName = intent.getStringExtra("FROM_CLASS");
        name = intent.getStringExtra("NAME");
        date = intent.getStringExtra("DATE");
        phone = intent.getStringExtra("TEL");
        status = intent.getStringExtra("STATUS");
        imageId = intent.getStringExtra("IMAGE_ID");
        room=intent.getStringExtra("ROOM");

        tvVisitorName.setText(name);
        tvVisitorTel.setText("手机号码：" + phone);
        tvVisitorDate.setText("到访日期:" + date);
        tvUserName.setText("邀请人：" + UserInformation.getUserInfo().getUserName());
        tvUserTel.setText("手机号码：" + UserInformation.getUserInfo().getUserPhone());
        tvUserRoom.setText("受访地址：" + room);

        if (status.equals("0")) {
            llVisitorBack.setBackgroundResource(R.drawable.visitor_card_back1);
        } else if (status.equals("1")||status.equals("2")) {
            llVisitorBack.setBackgroundResource(R.drawable.visitor_card_back2);
        } else if (status.equals("3")) {
            llVisitorBack.setBackgroundResource(R.drawable.visitor_card_back3);
        }

        imageURL = services.getImageUrl(imageId);
        new MyTask().execute(imageURL);
    }

    private void initView() {
        llVisitorBack=(LinearLayout)findViewById(R.id.ll_back_visitor_card);
        llBack = (LinearLayout) findViewById(R.id.ll_back2);
        imageBar = (ImageView) findViewById(R.id.iv_access_card_bar);
        btnShare = (ImageButton) findViewById(R.id.btn_custom);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        title = (TextView) findViewById(R.id.tv_page_title);
        tvVisitorName = (TextView) findViewById(R.id.tv_visitor_name);
        tvVisitorDate = (TextView) findViewById(R.id.tv_access_card_visite_date);
        tvVisitorTel = (TextView) findViewById(R.id.tv_access_card_visitor_tel);
        tvUserName = (TextView) findViewById(R.id.tv_access_card_invitor_name);
        tvUserTel = (TextView) findViewById(R.id.tv_access_card_invitor_tel);
        tvUserRoom = (TextView) findViewById(R.id.tv_access_card_invitor_address);
        title.setText("访客邀请");
        btnShare.setVisibility(View.VISIBLE);
        btnShare.setImageResource(R.drawable.shares);
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(AccessControlMain.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_custom:
                bitmapShare = getCacheBitmapFromView(llBack);
                if (bitmapShare != null) {
                    BottomDialog dialog = new BottomDialog(VisitorCardActivity.this, "出入证", bitmapShare);
                    dialog.uploadImageUI(VisitorCardActivity.this);
                }
                break;
        }
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
            bitmapShare = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (fromClassName.equals(AddVisitorInviteActivity.class.getName())) {
                try {
                    gotoActivityAndFinish(AccessControlMain.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "访客证:" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "访客证:" + this.getClass().getSimpleName());
    }
}
