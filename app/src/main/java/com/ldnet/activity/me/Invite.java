package com.ldnet.activity.me;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.BottomDialog;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.tendcloud.tenddata.TCAgent;

public class Invite extends BaseActionBarActivity {

    private TextView tv_main_title, tv_share;
    private ImageButton btn_back;
    private Services services;
    private String mTitle;
    private WebView invite_browser;
    private ProgressDialog mProgressDialog;
    private ProgressBar mProgressBar;

    //初始化视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_invite);
        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_invite);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //分享
        tv_share = (TextView) findViewById(R.id.tv_share);
        invite_browser = (WebView) findViewById(R.id.invite_browser);
        //进度条
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_loading);
        invite_browser.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });
        invite_browser.loadUrl(new Services().getInvitation(true));
        initEvent();
        //初始化服务
        services = new Services();
        mTitle = getString(R.string.community_mobilization);
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        tv_share.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "邀请好友：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "邀请好友：" + this.getClass().getSimpleName());
    }


    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_share:
                BottomDialog dialog = new BottomDialog(this, new Services().getInvitation(false), mTitle);
                dialog.uploadImageUI(this);
                break;
            default:
                break;
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
