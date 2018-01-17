package com.ldnet.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.v4.app.Fragment;
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
import com.ldnet.interfaze.PictureChoseListener;

import net.tsz.afinal.FinalBitmap;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

public class BaseFragment extends Fragment {

    protected LoadingDialog dialog;
    protected LoadingDialog1 dialog1;
    Context context;
    protected FinalBitmap finalBitmap;
    private PictureChoseListener imageLisener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        finalBitmap = FinalBitmap.create(getActivity()); //初始化
        finalBitmap.configBitmapLoadThreadSize(3);//定义线程数量
        finalBitmap.configDiskCachePath(Environment.getExternalStorageDirectory() + "/upload/upload.jpeg");//设置缓存目录；
        finalBitmap.configDiskCacheSize(1024 * 1024 * 10);//设置缓存大小
        finalBitmap.configLoadingImage(R.drawable.default_info);//设置加载图片
        finalBitmap.configLoadfailImage(R.drawable.default_info);

        return null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            com.ldnet.utility.Toast.makeText(getActivity(), str, 1000).show();
        } else {
            if (!TextUtils.isEmpty(defaultTip.trim())) {
                com.ldnet.utility.Toast.makeText(getActivity(), defaultTip, 1000).show();
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
            dialog = new LoadingDialog(getActivity());
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.setText(str);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void showProgressDialog1() {
        if (dialog1 == null) {
            dialog1 = new LoadingDialog1(getActivity());
            dialog1.setCanceledOnTouchOutside(false);
        }
        if (!dialog1.isShowing()) {
            dialog1.show();
        }
    }

    // 页面跳转方法
    public void gotoActivityAndFinish(String className,
                                      HashMap<String, String> extras) throws ClassNotFoundException {

        // 跳转到新的Activity
        gotoActivity(className, extras);

        // 结束当前Activity
        getActivity().finish();
    }

    // 页面跳转方法
    public void gotoActivity(String className, HashMap<String, String> extras)
            throws ClassNotFoundException {

        // 定义 intent
        Intent intent = new Intent(getActivity(), Class.forName(className));

        // 添加参数
        if (extras != null) {
            Iterator<Map.Entry<String, String>> iterator = extras.entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                @SuppressWarnings("rawtypes")
                Map.Entry entry = (Map.Entry) iterator.next();
                intent.putExtra(entry.getKey().toString(), entry.getValue()
                        .toString());
            }
        }

        // 跳转
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
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

    public void closeProgressDialog1() {
        if (dialog1 != null && dialog1.isShowing()) {
            dialog1.dismiss();
        }
    }

    public void openNetWork() {
        // 如果网络不可用，则弹出对话框，对网络进行设置
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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


    //添加图片
    public void showAddPicture(PictureChoseListener listener){
        this.imageLisener=listener;
        PopupWindow popWindow=null;
        View parent = LayoutInflater.from(getActivity()).inflate(R.layout.main, null);
        if (popWindow == null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_select_photo, null);
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

            mFileCacheDirectory = new File(Environment.getExternalStorageDirectory(), getActivity().getPackageName());
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
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

}
