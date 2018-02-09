package com.ldnet.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.goldensteward.R;
import com.ldnet.interfaze.PermissionListener;
import com.ldnet.interfaze.PictureChoseListener;
import com.ldnet.utility.ActivityUtil;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import net.tsz.afinal.FinalBitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;



public class BaseActionBarFragmentActivity extends FragmentActivity {

    protected LoadingDialog dialog;
    Context context;
    protected FinalBitmap finalBitmap;
    private int flag;
    protected DisplayImageOptions imageOptions;
    String aa = Services.timeFormat();
    String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
    private PictureChoseListener imageLisener;
    private PermissionListener mListener;
    private static final int PERMISSION_REQUESTCODE = 100;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finalBitmap = FinalBitmap.create(this); //初始化
        finalBitmap.configBitmapLoadThreadSize(3);//定义线程数量
        finalBitmap.configDiskCachePath(Environment.getExternalStorageDirectory() + "/upload/upload.jpeg");//设置缓存目录；
        finalBitmap.configDiskCacheSize(1024 * 1024 * 10);//设置缓存大小
        finalBitmap.configLoadingImage(R.drawable.default_goods);//设置加载图片

        imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.default_info)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
                .build();

        ActivityUtil.addActivity(this);
    }

    /**
     * 判断checkBox弹出的图层是否显示
     *
     * @return
     */
    protected boolean isShownTopBar() {
        return false;
    }

    /**
     * 关闭checkBox弹出的图层
     *
     * @return
     */
    protected void closeTopBar() {
    }

    /**
     * 打开checkBox弹出的图层
     *
     * @return
     */
    protected void openTopBar() {
    }

    //---------------------------------------------------------------------------------------
    public void showToast(String str, String defaultTip) {
        if (!TextUtils.isEmpty(str.trim())) {
            com.ldnet.utility.Toast.makeText(this, str, 1000).show();
        } else {
            if (!TextUtils.isEmpty(defaultTip.trim())) {
                com.ldnet.utility.Toast.makeText(this, defaultTip, 1000).show();
            }
        }
    }

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    public void showToast(String str) {
        showToast(str, "");
    }

    public void showProgressDialog(String str) {
        if (dialog == null) {
            dialog = new LoadingDialog(this);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.setText(str);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 隐藏输入法面板
     */
    public static void hideKeyboard(Activity c, View v) {
        InputMethodManager imm = (InputMethodManager) c.getSystemService(c.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void showProgressDialog() {
        this.showProgressDialog("正在加载...");
    }

    public void closeProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // 页面跳转方法
    public void gotoActivityAndFinish(String className,
                                      HashMap<String, String> extras) throws ClassNotFoundException {

        // 跳转到新的Activity
        gotoActivity(className, extras);
        // 结束当前Activity
        this.finish();
    }

    // 页面跳转方法
    public void gotoActivity(String className, HashMap<String, String> extras)
            throws ClassNotFoundException {

        // 定义 intent
        Intent intent = new Intent(this, Class.forName(className));

        // 添加参数
        if (extras != null) {
            Iterator<Map.Entry<String, String>> iterator = extras.entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                @SuppressWarnings("rawtypes")
                Map.Entry entry = (Map.Entry) iterator.next();
                intent.putExtra(entry.getKey().toString(), entry.getValue()
                        .toString());
                if(entry.getKey().toString().equals("LEFT")){
                    flag = 1;
                }
            }
        }

        // 跳转
        startActivity(intent);
        if(flag == 1) {
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }else {
            overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        }
    }

    public void openNetWork() {
        // 如果网络不可用，则弹出对话框，对网络进行设置
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("没有可用的网络");
        builder.setMessage("是否对网络进行设置?");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = null;
                        try {
                            String sdkVersion = android.os.Build.VERSION.SDK;
                            if (Integer.valueOf(sdkVersion) > 10) {
                                intent = new Intent(
                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                            } else {
                                intent = new Intent();
                                ComponentName comp = new ComponentName(
                                        "com.android.settings",
                                        "com.android.settings.WirelessSettings");
                                intent.setComponent(comp);
                                intent.setAction("android.intent.action.VIEW");
                            }
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }
        );
        builder.show();
    }

    @Override
    protected void onDestroy() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
        super.onDestroy();
    }


    //添加图片
    public void showAddPicture(PictureChoseListener listener){
        this.imageLisener=listener;
        PopupWindow popWindow=null;
        View parent = LayoutInflater.from(BaseActionBarFragmentActivity.this).inflate(R.layout.main, null);
        if (popWindow == null) {
            View view = LayoutInflater.from(BaseActionBarFragmentActivity.this).inflate(R.layout.pop_select_photo, null);
            popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            initPop(view,popWindow);
        }
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    //初始化图片选择View
    public void initPop(View view,final PopupWindow popupWindow) {
        File mFileCacheDirectory;
        TextView photograph = (TextView) view.findViewById(R.id.photograph);//拍照
        TextView albums = (TextView) view.findViewById(R.id.albums);//相册
        LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);//取消
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            mFileCacheDirectory = new File(Environment.getExternalStorageDirectory(), BaseActionBarFragmentActivity.this.getPackageName());
            // 判断当前目录是否存在
            if (!mFileCacheDirectory.exists()) {
                mFileCacheDirectory.mkdir();
            }
            //配置
            final FunctionConfig functionConfig = new FunctionConfig.Builder()
                    .setEnableCamera(true)
                    .setEnableEdit(false)
                    .setEnableCrop(false)
                    .setEnableRotate(false)
                    .setCropSquare(false)
                    .setEnablePreview(true)
                    .build();
            photograph.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    popupWindow.dismiss();
                    GalleryFinal.openCamera(101, functionConfig, mOnHanlderResultCallback);
                }
            });
            albums.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    popupWindow.dismiss();
                    GalleryFinal.openGallerySingle(101, functionConfig, mOnHanlderResultCallback);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    popupWindow.dismiss();
                }
            });
        } else {
            showToast("没找到手机SD卡，图片无法上传！");
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                if (!TextUtils.isEmpty(resultList.get(0).getPhotoPath())) {
                    imageLisener.choseSuccess(resultList.get(0).getPhotoPath());
                }else{
                    imageLisener.choseFail();
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(BaseActionBarFragmentActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

//    public void requestRunPermisssion(String[] permissions, PermissionListener listener){
//        mListener = listener;
//        List<String> permissionLists = new ArrayList<>();
//        for(String permission : permissions){
//            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
//                permissionLists.add(permission);
//            }
//        }
//
//        if(!permissionLists.isEmpty()){
//            ActivityCompat.requestPermissions(this, permissionLists.toArray(new String[permissionLists.size()]), PERMISSION_REQUESTCODE);
//        }else{
//            //表示全都授权了
//            mListener.onGranted();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case PERMISSION_REQUESTCODE:
//                if(grantResults.length > 0){
//                    //存放没授权的权限
//                    List<String> deniedPermissions = new ArrayList<>();
//                    for(int i = 0; i < grantResults.length; i++){
//                        int grantResult = grantResults[i];
//                        String permission = permissions[i];
//                        if(grantResult != PackageManager.PERMISSION_GRANTED){
//                            deniedPermissions.add(permission);
//                        }
//                    }
//                    if(deniedPermissions.isEmpty()){
//                        //说明都授权了
//                        mListener.onGranted();
//                    }else{
//                        mListener.onDenied(deniedPermissions);
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//    }

}
