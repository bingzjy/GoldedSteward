package com.ldnet.utility;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ldnet.activity.commen.Services;
import com.ldnet.entities.UpdateInformation;
import com.ldnet.goldensteward.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Alex on 2015/10/21.
 */
public class UpdateManager {
    // 下载中
    private static final int DOWNLOAD = 1;
    // 下载结束
    private static final int DOWNLOAD_FINISH = 2;
    //下载默认保存的文件名称
    private static final String APP_FILE_NAME = "ldnet_goldsteward.apk";
    // 服务器中APP的版本信息
    private UpdateInformation mUpdateInformation;
    // 下载保存路径
    private String mSavePath;
    // 记录进度条数量
    private int progress;
    // 是否取消更新
    private boolean cancelUpdate = false;
    private Context mContext;
    // 更新进度条
    private ProgressBar mProgress;
    private TextView tvProgressValue;
    private TextView tvProgressOperate;
    private AlertDialog mDownloadDialog;
    private AlertDialog mDownloadDialog1;
    // 服务获取最新的APP版本信息
    private Services services;
    private DownCompletedReceiver mReceiver;
    private DownloadManager manager;
    private downloadApkThread thread;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    tvProgressValue.setText(progress + "%");
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

    public UpdateManager(Context context) {
        this.mContext = context;
        services = new Services();
    }

    /**
     * 检测软件更新
     */
    public Boolean checkUpdate() {
        if (isUpdate()) {
            // 显示提示对话框
            showNoticeDialog2();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查软件是否有更新版本
     *
     * @return
     */
    private boolean isUpdate() {
        // 获取当前软件版本
        int versionCode = getVersionCode(mContext);
        // 获取服务器中的当前APP信息
        mUpdateInformation = services.getVersion();
        // 判断服务器中的APP版本编码，作为升级的依据
        if (mUpdateInformation != null) {
            services.visionCode = mUpdateInformation.getVersionCode();
            services.visionName = mUpdateInformation.getVersionName();
            if (versionCode < Integer.valueOf(mUpdateInformation.VersionCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo("com.ldnet.goldensteward", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog2() {
        mDownloadDialog1 = new AlertDialog.Builder(mContext).create();
        Window window = mDownloadDialog1.getWindow();
        mDownloadDialog1.setCanceledOnTouchOutside(true);
        mDownloadDialog1.show();

        window.setContentView(R.layout.dialog_update_notification_layout);

        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.8);
        window.setAttributes(lp);

        TextView tvTitle = (TextView) mDownloadDialog1.findViewById(R.id.tv_update_title);
        TextView tvContent = (TextView) mDownloadDialog1.findViewById(R.id.tv_update_message);
        TextView tvUpdate = (TextView) mDownloadDialog1.findViewById(R.id.tv_to_update);

        tvTitle.setText(R.string.soft_update_title + mUpdateInformation.VersionName);
        tvContent.setText(mUpdateInformation.getMemo());
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateProgress();
                mDownloadDialog1.dismiss();
            }
        });
    }


    //显示下载
    private void showUpdateProgress() {
        mDownloadDialog = new AlertDialog.Builder(mContext).create();
        Window window = mDownloadDialog.getWindow();
        mDownloadDialog.setCanceledOnTouchOutside(true);
        mDownloadDialog.show();
        //设置自定义布局，必须要在show之后
        window.setContentView(R.layout.dialog_update_layout);

        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.8
        window.setAttributes(lp);

        mProgress = (ProgressBar) mDownloadDialog.findViewById(R.id.update_progress);
        tvProgressValue = (TextView) mDownloadDialog.findViewById(R.id.tv_progress_value);
        TextView tvProgressOperate = (TextView) mDownloadDialog.findViewById(R.id.tv_operation);

        tvProgressOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDownManager();//后台下载
                mDownloadDialog.dismiss();
            }
        });
        // 下载文件
        downloadApk();
    }


    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        thread = new downloadApkThread();
        thread.start();
    }


    //下载文件线程
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // initDownManager();

                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(services.getAPPDownloadUrl(mUpdateInformation.Id));
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


    //后台下载
    private void initDownManager() {
        manager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        mReceiver = new DownCompletedReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(services.getAPPDownloadUrl(mUpdateInformation.Id)));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 下载时，通知栏显示途中
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载后文件存放的位置
        down.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, APP_FILE_NAME);
        // 将下载请求放入队列
        manager.enqueue(down);
        //注册下载广播
        mContext.registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    public class DownCompletedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //自动安装apk
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Uri uriForDownloadedFile = manager.getUriForDownloadedFile(downId);
                    Log.e("aaa", "uri=" + uriForDownloadedFile);
                    installApk();
                }
//                //停止服务并关闭广播
//                UpdateAppService.this.stopSelf();
            }
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
        mContext.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
