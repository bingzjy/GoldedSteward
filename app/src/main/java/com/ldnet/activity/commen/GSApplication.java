package com.ldnet.activity.commen;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.widget.GFImageView;
import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.ldnet.activity.main.Splash;
import com.ldnet.activity.base.ImageLoaderWithCookie;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.sharepreferencedata.TokenInformation;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//全局变量
public class GSApplication extends Application{
    private Services services;
    private static GSApplication instance;
    private Context context = this;
    public static GSApplication getInstance() {
        return instance;
    }

    static String aa = Services.timeFormat();
    String phone = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //设置Token
        Services.TOKEN = TokenInformation.getTokenInfo();
        //语音导航
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getString(R.string.app_id));

        //极光推送
        JPushInterface.setDebugMode(true);//设置开启日志，发布关闭
        JPushInterface.init(this);//初始化jpush
        Set<String> tagSet = new LinkedHashSet<String>();
        String pushId = UserInformation.getPushId();
        if (!TextUtils.isEmpty(pushId)) {
            phone = UserInformation.getUserInfo().UserPhone;
            tagSet.add(UserInformation.getUserInfo().CommunityId);
            tagSet.add(UserInformation.getUserInfo().HouseId);
            JPushInterface.setAliasAndTags(this, pushId, tagSet, null);
        }

        //图片加载配置
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .imageDownloader(new ImageLoaderWithCookie(getApplicationContext()))
                .build();
        ImageLoader.getInstance().init(config);

        //网络请求配置
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return false;
                    }
                }).build();
        OkHttpUtils.initClient(okHttpClient);
        services = new Services();

        // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
        Thread.setDefaultUncaughtExceptionHandler(restartHandler);

        //设置主题
        ThemeConfig theme = new ThemeConfig.Builder()
                .build();
        //GalleryFinal配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(false)
                .setEnableCrop(false)
                .setEnableRotate(false)
                .setCropSquare(false)
                .setEnablePreview(true)
                .build();

        //GalleryFinal配置imageloader
        CoreConfig coreConfig = new CoreConfig.Builder(context, new GSImageLoader(), theme)
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);

        //设置talkingdata
        TCAgent.LOG_ON = true;
        TCAgent.init(this);
        TCAgent.setReportUncaughtExceptions(true);//开启自动捕获异常
    }

    //重启应用
    public void restartApp() {
        Intent intent = new Intent(instance, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        instance.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("aaa","异常："+ex.toString());
            String exInfo = "";
            if (ex.getStackTrace() != null && ex.getStackTrace().length > 0) { //附加栈信息
                exInfo = "thread:" + thread
                        + "  |name:" + thread.getName()
                        + "  |id:" + thread.getId()
                        + "  |location:" + ex.getStackTrace()[0]
                        + "  |exception:" + ex;
                exInfo = exInfo.replace("]", "");
                exInfo = exInfo.replace("[", "");
            } else {                                                            //未知栈信息
                exInfo = "thread:" + thread
                        + "  |name:" + thread.getName()
                        + "  |id:" + thread.getId()
                        + "  |location:Unknow"
                        + "  |exception:" + ex;
                exInfo = exInfo.replace("]", "");
                exInfo = exInfo.replace("[", "");
            }
            Log.e("aaa", "提交异常信息：" + exInfo);
            services.PostError(exInfo);
            //记录异常信息
            TCAgent.onError(context, ex);
           // restartApp();//发生崩溃异常时,重启应用
        }
    };


    //继承GalleryFinal的ImageLoader
    class GSImageLoader implements cn.finalteam.galleryfinal.ImageLoader {

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(false).build();

        @Override
        public void displayImage(Activity activity, String path, GFImageView imageView, Drawable defaultDrawable, int width, int height) {
            ImageLoader.getInstance().displayImage("file://" + path, imageView, options);
        }

        @Override
        public void clearMemoryCache() {
        }
    }
}
