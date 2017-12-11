package com.ldnet.activity.me;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.UpdateInformation;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.Services;
import com.ldnet.utility.UpdateManager;
import com.ldnet.utility.UserInformation;
import com.unionpay.tsmservice.data.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lee on 2016/11/28.
 */
public class Check extends BaseActionBarActivity {

    private TextView tv_main_title;
    private TextView tv_version;
    private TextView tv_version1;
    private TextView tv_version2;
    private ImageButton btn_back;
    // 下载中
    private static final int DOWNLOAD = 1;
    // 下载结束
    private static final int DOWNLOAD_FINISH = 2;
    //下载默认保存的文件名称
    private static final String APP_FILE_NAME = "ldnet_goldsteward.apk";
    // 下载保存路径
    private String mSavePath;
    // 记录进度条数量
    private int progress;
    // 是否取消更新
    private boolean cancelUpdate = false;
    // 更新进度条
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;

    //版本更新
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_check);
        findView();
    }

    public void findView() {
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version1 = (TextView) findViewById(R.id.tv_version1);
        tv_version2 = (TextView) findViewById(R.id.tv_version2);
        tv_main_title.setText(R.string.fragment_me_check);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        try {
            String vision = getApplication().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0).versionName;
            tv_version.setText("Android " + vision);
            int visionCode = getApplication().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0).versionCode;
            if (Integer.parseInt(Services.visionCode) > visionCode) {
                tv_version1.setText("您有新版本" + Services.visionName + ",");
                tv_version2.setVisibility(View.VISIBLE);
                tv_version2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelUpdate = false;
                        showDownloadDialog();
//                        Uri uri = Uri.parse("http://apifive.goldwg.com/API/File/GetMobileAppByAppName/jpgjyzAPP");
//                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(it);
                    }
                });
            } else {
                tv_version1.setText("您的软件是最新版本。");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            try {
                gotoActivityAndFinish(MainActivity.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                gotoActivityAndFinish(MainActivity.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setCancelable(false);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 设置取消状态
                cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // 现在文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     *
     * @author coolszy
     * @date 2012-4-26
     * @blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL("http://apifive.goldwg.com/API/File/GetMobileAppByAppName/jpgjyzAPP");
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, APP_FILE_NAME);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, APP_FILE_NAME);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        this.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
