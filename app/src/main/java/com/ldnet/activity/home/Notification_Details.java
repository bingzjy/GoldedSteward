package com.ldnet.activity.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.commen.Services;
import com.ldnet.entities.Notifications;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.NotificationService;
import com.ldnet.utility.*;
import com.ldnet.utility.filedata.SDCardFileCache;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.view.dialog.BottomDialog;
import com.tendcloud.tenddata.TCAgent;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Notification_Details extends BaseActionBarActivity {
    private TextView tv_main_title, tv_share;
    private ImageButton btn_back;
    private WebView webView;
    private Services services;
    private String mNotificationId;
    private TextView tv_notification_title;
    private TextView tv_notification_property;
    private TextView tv_notification_date;
    private TextView tv_notification_content;
    private SDCardFileCache mFileCaches;
    private String checkedId;
    private Notifications n;

    //浏览的Web页面Url
    private String mTitle;
    private String mUrl;
    private String mShareUrl;
    private String mTitleOrgin;
    private String mDescriptionOrgin;
    private String mFromClassName;
    private String detailType;    //1通知公告 2调研
    private String detailDate;
    private ProgressBar mProgressBar;
    private NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_notification_details);
        notificationService=new NotificationService(this);

        initView();
        setWebView();  //设置webview
        //得到参数
        detailType = getIntent().getStringExtra("NOTIFICATION_TYPE");
        if (!TextUtils.isEmpty(detailType) && detailType.equals("1")) {  //通知公告
            mTitle = getIntent().getStringExtra("PAGE_TITLE");
            mUrl = getIntent().getStringExtra("PAGE_URL");
            mShareUrl = getIntent().getStringExtra("PAGE_URL_ORGIN");
            mTitleOrgin = getIntent().getStringExtra("PAGE_TITLE_ORGIN");
            mDescriptionOrgin = getIntent().getStringExtra("PAGE_DESCRIPTION_ORGIN");
            mFromClassName = getIntent().getStringExtra("FROM_CLASS_NAME");
            mNotificationId = getIntent().getStringExtra("NOTIFICATION_ID");

            //获取详情
            showProgressDialog();
            notificationService.getNotificationDetail(mNotificationId,handlerDetail);
        } else if (!TextUtils.isEmpty(detailType) && detailType.equals("2")) {  //调研
            mTitle = getIntent().getStringExtra("PAGE_TITLE");
            mUrl = getIntent().getStringExtra("PAGE_DISPLAY_URL");
            mShareUrl = getIntent().getStringExtra("PAGE_SHARE_URL");
            mTitleOrgin = getIntent().getStringExtra("PAGE_TITLE_ORGIN");
            mFromClassName = getIntent().getStringExtra("FROM_CLASS_NAME");
            detailDate = getIntent().getStringExtra("PAGE_DATE");
            mNotificationId = getIntent().getStringExtra("NOTIFICATION_ID");
            tv_notification_date.setText(detailDate);
            webView.loadUrl(mUrl);
        }

        //初始化服务,文件缓存等
        mFileCaches = new SDCardFileCache(this);
        initEvent();

        //标记已读
        ReadInfoIDs read = ReadInfoIDs.getInstance();
        read.setRead(mNotificationId, read.TYPE_NOTIFICATION);
    }

    public void initView(){
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_share = (TextView) findViewById(R.id.tv_share);
        tv_share.setVisibility(View.VISIBLE);
        tv_notification_title = (TextView) findViewById(R.id.tv_notification_title);
        tv_notification_property = (TextView) findViewById(R.id.tv_notification_property);
        tv_notification_property.setText(UserInformation.getUserInfo().PropertyName);
        tv_notification_date = (TextView) findViewById(R.id.tv_notification_date);
        tv_notification_content = (TextView) findViewById(R.id.tv_notification_content);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_loading);
        webView = (WebView) findViewById(R.id.webView);
    }

    public void setWebView(){
        //Settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setLoadsImagesAutomatically(true);
        //ChromeClient
        WebChromeClient chromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            // 网页进度条的加载
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
        };
        webView.setWebChromeClient(chromeClient);

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
        webView.setWebViewClient(client);
    }


    Handler handlerDetail=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    Notifications notifications=(Notifications)msg.obj;
                    webView.loadUrl(notifications.Url);
                    //标题
                    tv_notification_title.setText(notifications.Title);
                    tv_main_title.setText(notifications.Title);
                    tv_notification_date.setText(Services.subStr(notifications.DateTime));
                    //正文
                    tv_notification_content.setText(Html.fromHtml(notifications.Content, imageGetter, null));
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //ImageGetter?
    final Html.ImageGetter imageGetter = new Html.ImageGetter() {
        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            Bitmap image;
            Integer parentcontentWidth = Utility.getScreenWidthforPX(getApplication()) - Utility.dip2px(getApplicationContext(), 16.0f);
            //判断SD卡里面是否存在图片文件
            image = mFileCaches.getFileFromFileCache(source);
            if (image != null) {
                image = mFileCaches.getFileFromFileCache(source);
                drawable = new BitmapDrawable(getResources(), image);
                Integer height = (int) (((float) parentcontentWidth / (float) drawable.getIntrinsicWidth()) * drawable.getIntrinsicHeight());
                drawable.setBounds(0, 0, parentcontentWidth, height);
                return drawable;
            } else {
                try {
                    image = new HttpImageAsyncTask().execute(source).get();
                    drawable = new BitmapDrawable(getResources(), image);
                    Integer height = (int) (((float) parentcontentWidth / (float) drawable.getIntrinsicWidth()) * drawable.getIntrinsicHeight());
                    drawable.setBounds(0, 0, parentcontentWidth, height);
                    return drawable;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return drawable;
        }

    };

    //获取文件的名字
    private String getFileName(String url) {
        String[] array = url.split("/");
        return array[array.length - 1];
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        tv_share.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_share:
                Log.d("Services Status", "mShareUrl:" + mShareUrl + "mTitleOrgin:" + mTitleOrgin);
                BottomDialog dialog = new BottomDialog(this, mShareUrl, mTitleOrgin);
                dialog.uploadImageUI(this);
                break;
            default:
                break;
        }
    }

    class HttpImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap image;
            InputStream is;
            try {
                //得到图片的URL地址
                String url = services.getImageUrl(getFileName(params[0]));
                HttpURLConnection connection = (HttpURLConnection) new URL(url)
                        .openConnection();
                is = new BufferedInputStream(connection.getInputStream());
                image = BitmapFactory.decodeStream(is);
                //写入缓存
                mFileCaches.putImageToFileCache(params[0], image);
                // 返回图片
                return image;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "通知详情：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "通知详情：" + this.getClass().getSimpleName());
    }
}
