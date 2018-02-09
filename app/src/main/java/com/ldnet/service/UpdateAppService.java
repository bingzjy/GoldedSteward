package com.ldnet.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ldnet.entities.UpdateInformation;
import com.ldnet.utility.Services;

import java.io.File;

import static com.ldnet.service.BaseService.mContext;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.service
 * @class describe
 * @time 2018/1/25 14:25
 * @change
 * @chang time
 * @class describe
 */

public class UpdateAppService extends Service {

    private DownloadManager manager;
    private DownCompletedReceiver mReceiver;
    //下载默认保存的文件名称
    private static final String APP_FILE_NAME = "ldnet_goldsteward.apk";
    // 服务器中APP的版本信息
    private UpdateInformation mUpdateInformation;
    // 下载保存路径
    private String mSavePath;
    private Services service;


    public UpdateAppService() {
    }

    /**
     * 初始化下载器
     **/
    private void initDownManager() {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mReceiver = new DownCompletedReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(service.getAPPDownloadUrl(mUpdateInformation.Id)));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 下载时，通知栏显示途中
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载后文件存放的位置
        down.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, APP_FILE_NAME);
        // 将下载请求放入队列
        manager.enqueue(down);
        //注册下载广播
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initDownManager();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("aaa","更新服务");
        service = new Services();
        mUpdateInformation = service.getVersion();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                    installApkNew(uriForDownloadedFile);
                }
                //停止服务并关闭广播
                UpdateAppService.this.stopSelf();
            }
        }
    }
    //安装apk
    protected void installApkNew(Uri uri) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
         android.os.Process.killProcess(android.os.Process.myPid());
        startActivity(intent);
    }



    //安装APP
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
