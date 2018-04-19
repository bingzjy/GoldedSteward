package com.ldnet.activity.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.commen.Services;
import com.ldnet.activity.home.Property_Fee;
import com.ldnet.activity.mall.Order_Details;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.filedata.SDCardFileCache;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.view.HeaderLayout;
import com.ldnet.view.dialog.BottomDialog;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tendcloud.tenddata.TCAgent;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * ***************************************************
 * 网页浏览
 * **************************************************
 */
public class Browser extends BaseActionBarActivity {
    // 标题
    private TextView tv_page_title, tv_share;
    // 返回
    private ImageButton btn_back;
    private Services service;
    // 进度条
    private ProgressBar mProgressBar;
    //浏览器
    private WebView wv_browser;
    //浏览的Web页面Url
    private String mUrl;
    private String mUrlOrgin;
    private String mTitleOrgin;
    private String mDescriptionOrgin;
    //从哪个Activity跳转过来的
    private String mFromClassName;
    //页面标题
    private String mTitle;
    private String image;
    private IWXAPI api;
    private Bitmap bmp;
    private static final String APP_ID = "wxa4207e39a8e5cf0f";
    private SDCardFileCache mFileCaches;
    //关于进度条的定义
    protected static final int STOP = 0x10000;
    protected static final int NEXT = 0x10001;
    private int iCount = 0;
    private Handler mHandler;
    private ProgressBar progressBar;
    private PullToRefreshScrollView mPullToRefreshScrollView;

    // 初始化控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        service = new Services();
        mFileCaches = new SDCardFileCache(getApplication());
        //得到参数
        mTitle = getIntent().getStringExtra("PAGE_TITLE");
        mUrl = getIntent().getStringExtra("PAGE_URL");
        mUrlOrgin = getIntent().getStringExtra("PAGE_URL_ORGIN");
        mTitleOrgin = getIntent().getStringExtra("PAGE_TITLE_ORGIN");
        mDescriptionOrgin = getIntent().getStringExtra("PAGE_DESCRIPTION_ORGIN");
        mFromClassName = getIntent().getStringExtra("FROM_CLASS_NAME");
        //--------------BUTTON BACK-------------
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        if (TextUtils.isEmpty(mFromClassName)) {
            btn_back.setVisibility(View.GONE);
        }
        //--------------TITLE-------------
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);

        //--------------SHARE-------------
        tv_share = (TextView) findViewById(R.id.tv_share);
        //缴费，不分享
        if (Property_Fee.class.getName().equals(mFromClassName)) {
            tv_share.setVisibility(View.GONE);
        }
        //用户协议不分享
        if (Register.class.getName().equals(mFromClassName) || Order_Details.class.getName().equals(mFromClassName)) {
            tv_share.setVisibility(View.GONE);
        }
        //进度条
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_loading);
        //--------------WEBVIEW-------------
        wv_browser = (WebView) findViewById(R.id.wv_browser);
        // 允许加载JavaScript脚本语言。
        wv_browser.getSettings().setJavaScriptEnabled(true);
        // 设置是否支持手势缩放
        wv_browser.getSettings().setBuiltInZoomControls(true);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        initView();
        image = getIntent().getStringExtra("PAGE_IMAGE");
        initEvent();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "网页：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "网页：" + this.getClass().getSimpleName());
    }

    public void initView() {
        //Settings
        WebSettings webSettings = wv_browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setLoadsImagesAutomatically(true);
        tv_page_title.setText(mTitle);
        //ChromeClient
        WebChromeClient chromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                tv_page_title.setText(title);
                super.onReceivedTitle(view, title);
            }

            // 网页进度条的加载
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mPullToRefreshScrollView.onRefreshComplete();
                } else {
                    if (View.INVISIBLE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        };
        wv_browser.setWebChromeClient(chromeClient);
        //Client
        WebViewClient client = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }
        };
        wv_browser.setWebViewClient(client);
        if (!mUrl.contains("?") && !mUrl.contains("&")) {
            mUrl = mUrl + "?CID=" + UserInformation.getUserInfo().getCommunityId() + "&UID=" + UserInformation.getUserInfo().getUserId();
        }
        wv_browser.loadUrl(mUrl);
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                initView();
            }
        });
    }

    public void initEvent() {
        tv_share.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    // 点击事件处理
    @Override
    public void onClick(View v) {

        try {
            switch (v.getId()) {
                case R.id.btn_back:
                    if (Order_Details.class.getName().equals(mFromClassName)) {
                        finish();
                    } else {
                        super.gotoActivityAndFinish(mFromClassName, null);
                    }

                    break;
                case R.id.tv_share:
                    if (!TextUtils.isEmpty(image)) {
                        BottomDialog dialog = new BottomDialog(this, mUrlOrgin, mTitleOrgin, service.getImageUrl(image), mDescriptionOrgin);
                        dialog.uploadImageUI(this);
                    } else {
                        BottomDialog dialog = new BottomDialog(this, mUrlOrgin, mTitleOrgin);
                        dialog.uploadImageUI(this);
                    }

                    break;
                default:
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (Order_Details.class.getName().equals(mFromClassName)) {
                finish();
            } else {
                try {
                    super.gotoActivityAndFinish(mFromClassName, null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private Bitmap imageCreateByUrl(String url) {
        Bitmap image;
        InputStream is = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            image = BitmapFactory.decodeStream(is);

            // 返回图片
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
