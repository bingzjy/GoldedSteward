package com.ldnet.activity.me;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ldnet.activity.Browser;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.Services;
import com.ldnet.view.Rotate3dAnimation;
import com.tendcloud.tenddata.TCAgent;

import java.util.HashMap;

public class About extends BaseActionBarActivity {

    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private TextView tv_about_version;
    private LinearLayout ll_about_business;
    private LinearLayout ll_about_covenanter;
    private LinearLayout mCallMe;
    //商家报名
    public final static String mUrlBusiness = "http://www.goldwg.com/form/retailer";
    //物业报名
    public final static String mUrlProperty = "http://www.goldwg.com/form/property";
    private TextView tv;
    private Handler handler = new Handler();

    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_about);
        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_about);

        tv_about_version = (TextView) findViewById(R.id.tv_about_version);
        try {
            tv_about_version.setText(getApplication().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ll_about_business = (LinearLayout) findViewById(R.id.ll_about_business);
        ll_about_covenanter = (LinearLayout) findViewById(R.id.ll_about_covenanter);
        mCallMe = (LinearLayout) findViewById(R.id.call_me);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv = (TextView) findViewById(R.id.tv);
        initEvent();
        //初始化服务
        services = new Services();
        handler.post(myRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "我-关于：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "我-关于：" + this.getClass().getSimpleName());
    }

    private Runnable myRunnable= new Runnable() {
        public void run() {

            if (true) {
                handler.postDelayed(this, 3000);
                applyRotation(0,90);
            }
        }
    };

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        ll_about_business.setOnClickListener(this);
        ll_about_covenanter.setOnClickListener(this);
        mCallMe.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        try {
            HashMap<String, String> extras = new HashMap<String, String>();
            switch (v.getId()) {
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.call_me:
                    String number = "02989322635";
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                    startActivity(intent);
                    break;
                case R.id.ll_about_business:
                    extras.clear();
                    extras.put("PAGE_TITLE", getString(R.string.common_me_business));
                    extras.put("FROM_CLASS_NAME", this.getClass().getName());
                    extras.put("PAGE_URL", mUrlBusiness);
                    extras.put("LEFT", "LEFT");
                    gotoActivityAndFinish(Browser.class.getName(), extras);
                    break;
                case R.id.ll_about_covenanter:
                    extras.clear();
                    extras.put("PAGE_TITLE", getString(R.string.common_me_property));
                    extras.put("FROM_CLASS_NAME", this.getClass().getName());
                    extras.put("PAGE_URL", mUrlProperty);
                    extras.put("LEFT", "LEFT");
                    gotoActivityAndFinish(Browser.class.getName(), extras);
                    break;
                default:
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void applyRotation(float start, float end) {
        // 计算中心点
        final float centerX = tv.getWidth() / 2.0f;
        final float centerY = tv.getHeight() / 2.0f;

        final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
                centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        // 设置监听
        rotation.setAnimationListener(new DisplayNextView());

        tv.startAnimation(rotation);
    }

    private final class DisplayNextView implements Animation.AnimationListener {

        public void onAnimationStart(Animation animation) {
        }

        // 动画结束
        public void onAnimationEnd(Animation animation) {
            tv.post(new SwapViews());
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private final class SwapViews implements Runnable {

        public void run() {
            final float centerX = tv.getWidth() / 2.0f;
            final float centerY = tv.getHeight() / 2.0f;
            Rotate3dAnimation rotation = null;

            tv.requestFocus();

            rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f,
                    false);
            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            // 开始动画
            tv.startAnimation(rotation);
            tv.setText("aaaaaaaa");
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
