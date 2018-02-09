package com.ldnet.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.ldnet.activity.adapter.CustomAlertDialog;
import com.ldnet.activity.base.BaseFragment;
import com.ldnet.activity.bindmanage.MyRelationActivity;
import com.ldnet.activity.mall.Shopping_Carts;
import com.ldnet.activity.me.*;
import com.ldnet.activity.me.Address;
import com.ldnet.activity.me.Community;
import com.ldnet.activity.me.Coupon;
import com.ldnet.activity.me.Information;
import com.ldnet.activity.me.Message;
import com.ldnet.activity.access.AccessControlMain;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.interfaze.PictureChoseListener;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.HomeService;
import com.ldnet.utility.*;
import com.nanchen.compresshelper.CompressHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ***************************************************
 * 主框架 - 我
 * **************************************************
 */
public class FragmentMe extends BaseFragment implements OnClickListener {

    // 标题
    private TextView tv_main_title;
    //我的头像
    private CircleImageView ibtn_me_thumbnail;
    //我的信息
    private LinearLayout ll_me_information;
    //我的积分
    private LinearLayout ll_me_integral;
    //我的钱包
    private LinearLayout ll_me_wallet;

    //我的购物车
    private LinearLayout ll_me_shopping_cart;
    //我的订单
    private LinearLayout ll_me_orders;
    //我的小区
    private LinearLayout ll_me_community;
    //我的家属
    private LinearLayout ll_me_community_relation;
    //门禁快捷
    private LinearLayout ll_me_create_shortcut;
    //邀请好友
    private LinearLayout ll_me_invite;
    //我的消息
    private LinearLayout ll_me_message;
    //我的发布
    private LinearLayout ll_me_publish;
    // 意见反馈
    private LinearLayout ll_me_feedback;
    //关于
    private LinearLayout ll_me_about;
    // 退出登录
    private LinearLayout ll_me_logout;
    //帐号名称
    private TextView tv_me_phone;
    //我的地址（收货地址）
    private LinearLayout ll_me_address;
    private LinearLayout ll_me_entry_exit;
    //优惠劵
    private LinearLayout ll_me_coupon;
    private LinearLayout ll_me_check;
    private ImageView iv_msg_center, iv_feedback, iv_msg_order;
    private Services services;
    /* 请求码*/
    private static final int CROPIMAGE_REQUEST_CODE = 1002;
    private static final int mCropImageWidth = 192;
    private Uri mImageUri;
    private PopupWindow popWindow1;
    private LayoutInflater layoutInflater;

    private String aa = Services.timeFormat();
    private String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";

    private HomeService homeService;
    private AcountService acountService;
    private Handler handlerDeleteRed=new Handler(){};

    public DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.me_thumbnail_n)     //url爲空會显示该图片，自己放在drawable里面的
            .showImageOnFail(R.drawable.me_thumbnail_n)                //加载图片出现问题，会显示该图片
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .resetViewBeforeLoading(true)
            .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
            .build();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局
        View view = inflater.inflate(R.layout.fragment_main_me, container, false);
        //菜单
        setHasOptionsMenu(true);
        // 初始化视图
        initService();
        initView(view);

        return view;
    }


    //初始化服务
    private void initService() {
        services = new Services();
        homeService = new HomeService(getActivity());
        acountService = new AcountService(getActivity());
    }

    // 初始化视图
    private void initView(View view) {
        // 标题
        tv_main_title = (TextView) view.findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.module_title_me);

        //设置用户名
        tv_me_phone = (TextView) view.findViewById(R.id.tv_me_phone);
        //我的头像
        ibtn_me_thumbnail = (CircleImageView) view.findViewById(R.id.ibtn_me_thumbnail);
        //我的信息
        ll_me_information = (LinearLayout) view.findViewById(R.id.ll_me_Information);
        //我的积分
        ll_me_integral = (LinearLayout) view.findViewById(R.id.ll_me_integral);
        //我的钱包
        ll_me_wallet = (LinearLayout) view.findViewById(R.id.ll_me_wallet);

        //我的购物车
        ll_me_shopping_cart = (LinearLayout) view.findViewById(R.id.ll_me_shopping_cart);
        //优惠劵
        ll_me_coupon = (LinearLayout) view.findViewById(R.id.ll_me_coupon);
        //我的订单
        ll_me_orders = (LinearLayout) view.findViewById(R.id.ll_me_orders);
        //我的小区
        ll_me_community = (LinearLayout) view.findViewById(R.id.ll_me_community);
        //我的家属
        ll_me_community_relation=(LinearLayout)view.findViewById(R.id.ll_me_community_relation);
        if (UserInformation.getUserInfo().HasRoom == 1) {
            ll_me_community_relation.setVisibility(View.VISIBLE);
        } else {
            ll_me_community_relation.setVisibility(View.GONE);
        }

        ll_me_create_shortcut=(LinearLayout)view.findViewById(R.id.ll_me_create_shortcut) ;
        //我的地址（收货地址）
        ll_me_address = (LinearLayout) view.findViewById(R.id.ll_me_address);
        //邀请好友
        ll_me_invite = (LinearLayout) view.findViewById(R.id.ll_me_invite);
        ll_me_invite.setVisibility(View.GONE);
        //我的消息
        ll_me_message = (LinearLayout) view.findViewById(R.id.ll_me_message);
        //我的发布
        ll_me_publish = (LinearLayout) view.findViewById(R.id.ll_me_publish);
        //关于
        ll_me_about = (LinearLayout) view.findViewById(R.id.ll_me_about);
        //意见反馈
        ll_me_feedback = (LinearLayout) view.findViewById(R.id.ll_me_feedback);
        ll_me_check = (LinearLayout) view.findViewById(R.id.ll_me_check);
        //出入管理
        ll_me_entry_exit=(LinearLayout)view.findViewById(R.id.ll_me_entry_exit_manage);
        // 退出登录
        ll_me_logout = (LinearLayout) view.findViewById(R.id.ll_me_logout);
        iv_msg_center = (ImageView) view.findViewById(R.id.iv_msg_center);
        iv_feedback = (ImageView) view.findViewById(R.id.iv_feedback);
        iv_msg_order = (ImageView) view.findViewById(R.id.iv_msg_order);
        initEvents();
        ScreenUtils.initScreen(getActivity());
        layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 初始化事件
    private void initEvents() {
        //我的头像
        ibtn_me_thumbnail.setOnClickListener(this);
        //我的信息
        ll_me_information.setOnClickListener(this);
        //我的积分
        ll_me_integral.setOnClickListener(this);
        //我的钱包
        ll_me_wallet.setOnClickListener(this);
        //我的购物车
        ll_me_shopping_cart.setOnClickListener(this);
        // 优惠劵
        ll_me_coupon.setOnClickListener(this);
        //我的订单
        ll_me_orders.setOnClickListener(this);
        //我的小区
        ll_me_community.setOnClickListener(this);
        //我的家属
        ll_me_community_relation.setOnClickListener(this);
        //开启快捷
        ll_me_create_shortcut.setOnClickListener(this);
        //我的地址（收货地址）
        ll_me_address.setOnClickListener(this);
        //邀请好友
        ll_me_invite.setOnClickListener(this);
        //我的消息
        ll_me_message.setOnClickListener(this);
        //我的发布
        ll_me_publish.setOnClickListener(this);
        //关于
        ll_me_about.setOnClickListener(this);
        //意见反馈
        ll_me_feedback.setOnClickListener(this);
        // 退出登录
        ll_me_logout.setOnClickListener(this);
        ll_me_check.setOnClickListener(this);
        ll_me_entry_exit.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        homeService.getAppRedPoint(handlerGetRedPointPush);
        //设置用户姓名
        String userName;
        User user = UserInformation.getUserInfo();
        if (!TextUtils.isEmpty(user.UserName)) {
            userName = user.UserName;
        } else {
            userName = user.UserPhone;
        }
        tv_me_phone.setText(userName);
        //设置用户头像
        if (!TextUtils.isEmpty(user.UserThumbnail)) {
            ImageLoader.getInstance().displayImage(Services.getImageUrl(user.UserThumbnail),
                    ibtn_me_thumbnail, imageOptions);
        }
    }

    //隐藏小红点
    private void hintRed(int type) {
        //消除小红点
        Msg msg = PushMessage.getPushInfo();
        msg.REPAIRS = false;
        PushMessage.setPushInformation(msg);
        homeService.deleteRedPoint(type, handlerDeleteRed);
    }

    //显示小红点
    private void showRed(){
        //意见反馈、物业消息 是否显示消息中心小红点
        if (PushMessage.getPushInfo().isFEEDBACK()||PushMessage.getPushInfo().PROPERTY_MSG) {
            iv_msg_center.setVisibility(View.VISIBLE);
        } else {
            iv_msg_center.setVisibility(View.GONE);
        }


        //订单推送是否显示小红点
        if (PushMessage.getPushInfo().isORDER()) {
            iv_msg_order.setVisibility(View.VISIBLE);
        } else {
            iv_msg_order.setVisibility(View.GONE);
        }


        //设置底部我的模块是否显示小红点
        if (PushMessage.getPushInfo().isFEEDBACK() || PushMessage.getPushInfo().isORDER()||PushMessage.getPushInfo().PROPERTY_MSG) {
            getActivity().findViewById(R.id.iv_dc1).setVisibility(View.VISIBLE);
        } else {
            getActivity().findViewById(R.id.iv_dc1).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_me_thumbnail://我的头像
                showAddPicture(new PictureChoseListener() {
                    @Override
                    public void choseSuccess(String imagePath) {

                        Log.e("aaa","头像路径："+imagePath);

                        ExifInterface exifInterface = null;
                        try {
                            exifInterface = new ExifInterface(imagePath);
                            String datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);// 拍摄时间
                            String deviceName = exifInterface.getAttribute(ExifInterface.TAG_MAKE);// 设备品牌
                            String deviceModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL); // 设备型号
                            String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                            String lngValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                            String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                            String lngRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

                            if (latValue != null && latRef != null && lngValue != null && lngRef != null) {
                                try {
                                    float lat= convertRationalLatLonToFloat(latValue, latRef);
                                    float lng=convertRationalLatLonToFloat(lngValue, lngRef);

                                    Log.e("aaa","经纬度：lat:"+lat+"   lng:"+lng);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        mImageUri = Uri.fromFile(new File(imagePath));
                        onCropImage(mImageUri);
                    }
                    @Override
                    public void choseFail() {

                    }
                });
                break;
            case R.id.ll_me_Information: // 我的信息
                Intent intent_information = new Intent(getActivity(), Information.class);
                startActivity(intent_information);
                break;
            case R.id.ll_me_integral://我的积分
                Intent intent_integral = new Intent(getActivity(), Integral.class);
                startActivity(intent_integral);
                break;
            case R.id.ll_me_wallet://我的钱包
                Intent intent_wallet = new Intent(getActivity(), Recharge.class);
                startActivity(intent_wallet);
                break;
            case R.id.ll_me_shopping_cart://我的购物车
                try {
                    gotoActivity(Shopping_Carts.class.getName(),null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_me_coupon://我的优惠劵
                Intent intent_coupon = new Intent(getActivity(), Coupon.class);
                startActivity(intent_coupon);
                break;
            case R.id.ll_me_orders://我的订单
               //消除小红点
                hintRed(7);
                Intent intent_orders = new Intent(getActivity(), OrdersTabActivity.class);
                startActivity(intent_orders);
                break;
            case R.id.ll_me_community: //我的小区
                Intent intent_community = new Intent(getActivity(), Community.class);
                startActivity(intent_community);
                break;
            case R.id.ll_me_community_relation: //我的家属
                Intent intent_relation=new Intent(getActivity(), MyRelationActivity.class);
                startActivity(intent_relation);
                break;
            case R.id.ll_me_address://我的地址、收货地址
                Intent intent_address = new Intent(getActivity(), Address.class);
                startActivity(intent_address);
                break;
            case R.id.ll_me_invite://邀请好友
                Intent intent_invite = new Intent(getActivity(), Invite.class);
                startActivity(intent_invite);
                break;
            case R.id.ll_me_message: //我的消息
                Msg msg = MsgInformation.getMsg();
                if (MsgInformation.getMsg().isFEEDBACK() && !MsgInformation.getMsg().isORDER()) {
                    getActivity().findViewById(R.id.iv_dc1).setVisibility(View.VISIBLE);
                    iv_msg_center.setVisibility(View.GONE);
                    iv_msg_order.setVisibility(View.GONE);
                    msg.setMESSAGE(false);
                    msg.setORDER(false);
                } else if (!MsgInformation.getMsg().isFEEDBACK() && MsgInformation.getMsg().isORDER()) {
                    getActivity().findViewById(R.id.iv_dc1).setVisibility(View.VISIBLE);
                    iv_feedback.setVisibility(View.GONE);
                    iv_msg_center.setVisibility(View.GONE);
                    msg.setMESSAGE(false);
                    msg.setFEEDBACK(false);
                } else if (!MsgInformation.getMsg().isFEEDBACK() && !MsgInformation.getMsg().isORDER()) {
                    getActivity().findViewById(R.id.iv_dc1).setVisibility(View.GONE);
                    iv_msg_order.setVisibility(View.GONE);
                    iv_msg_center.setVisibility(View.GONE);
                    iv_feedback.setVisibility(View.GONE);
                    msg.setMESSAGE(false);
                    msg.setFEEDBACK(false);
                    msg.setORDER(false);
                }
                MsgInformation.setMsgInfo(msg);
                Intent intent_message = new Intent(getActivity(), Message.class);
                startActivity(intent_message);
                break;
            case R.id.ll_me_publish://我的发布
                Intent intent_publish = new Intent(getActivity(), PublishActivity.class);
                startActivity(intent_publish);
                break;
            case R.id.ll_me_about: //关于
                Intent intent_about = new Intent(getActivity(), About.class);
                startActivity(intent_about);
                break;
            case R.id.ll_me_feedback:// 意见反馈
                //消除小红点
                hintRed(6);
                Intent intent_feedback = new Intent(getActivity(), Feedback.class);
                startActivity(intent_feedback);
                break;
            case R.id.ll_me_logout: // 退出登录
                quitPopupWindow(ll_me_logout);
                break;
            case R.id.ll_me_check: // 版本检测
                Intent intent_check = new Intent(getActivity(), Check.class);
                startActivity(intent_check);
                break;
            case R.id.ll_me_entry_exit_manage: //出入管理
                Intent intent_pass = new Intent(getActivity(), AccessControlMain.class);
                startActivity(intent_pass);
                break;
            case R.id.ll_me_create_shortcut: //创建门禁快捷方式
                createShortcut();

                CustomAlertDialog dialog=new CustomAlertDialog(getActivity(),false,getResources().getString(R.string.dialog_shortcut_title),getString(R.string.dialog_confirm));
                dialog.show();
                dialog.setDialogCallback(dialogcallback);
                break;
            default:
                break;
        }
    }

    //退出登录弹框
    public void quitPopupWindow(View parent) {
        if (popWindow1 == null) {
            View view = layoutInflater.inflate(R.layout.quit_pop, null);
            popWindow1 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            quitPop(view);
        }
        popWindow1.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow1.setFocusable(true);
        popWindow1.setOutsideTouchable(true);
        popWindow1.setBackgroundDrawable(new BitmapDrawable());
        popWindow1.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow1.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    //退出登录弹框View
    public void quitPop(View view) {
        TextView quit = (TextView) view.findViewById(R.id.quit);//tuichu
        LinearLayout cancel1 = (LinearLayout) view.findViewById(R.id.cancel);//取消

        quit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow1.dismiss();
                // 定义 intent
                Intent intent = new Intent(getActivity(), Login.class);
                intent.putExtra("password", UserInformation.getUserInfo().getUserPassword());
                intent.putExtra("phone", UserInformation.getUserInfo().getUserPhone());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                getActivity().finish();
                // 清除保存的Cookie
                CookieInformation.clearCookieInfo();
                // 清除保存的用户信息
                UserInformation.clearUserInfo();
                TokenInformation.clearTokenInfo();
            }
        });
        cancel1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow1.dismiss();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getActivity().RESULT_OK == resultCode) {
            switch (requestCode) {
                case CROPIMAGE_REQUEST_CODE:
                    try {
                        Bitmap image = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mImageUri));
                        if (image != null) {
                            // 获取本地文件
                            File file = new File(mImageUri.getPath());

                            //压缩图片
                            Bitmap bitmap = CompressHelper.getDefault(getActivity()).compressToBitmap(file);
                            FileOutputStream fileOutStream = null;
                            try {
                                fileOutStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80,
                                        fileOutStream);
                                fileOutStream.flush();
                                fileOutStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            showToast("上传图片中...");
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    String fileId = services.Upload(getActivity(), mImageUri.getPath()).FileName;
                                    //修改服务器中用户的头像
                                    User user = UserInformation.getUserInfo();
                                    user.UserThumbnail = fileId;
                                    UserInformation.setUserInfo(user);
                                    showProgressDialog();
                                    acountService.changeInformation(user.UserName, user.UserThumbnail,handlerChangeUserInfo);
                                }
                            }.start();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //头像裁剪方法
    private void onCropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", mCropImageWidth);// 输出图片大小
        intent.putExtra("outputY", mCropImageWidth);
        intent.putExtra("return-initData", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        getActivity().startActivityForResult(intent, CROPIMAGE_REQUEST_CODE);
    }


    Handler handlerGetRedPointPush=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    List<Type> data=(List<Type>)msg.obj;
                    Msg push=PushMessage.setMsg(data);
                    PushMessage.setPushInformation(push);
                    showRed();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    List<Type> data2=new ArrayList<Type>();
                    Msg push2=PushMessage.setMsg(data2);
                    PushMessage.setPushInformation(push2);
                    showRed();
                    break;
            }
        }
    };

  //修改用户头像
 Handler handlerChangeUserInfo=new Handler(){
     @Override
     public void handleMessage(android.os.Message msg) {
         super.handleMessage(msg);
         closeProgressDialog();
         switch (msg.what){
             case BaseService.DATA_SUCCESS:
                 //显示头像
                 ImageLoader.getInstance().displayImage(Services.getImageUrl(UserInformation.getUserInfo().UserThumbnail),
                         ibtn_me_thumbnail, imageOptions);
                 break;
             case BaseService.DATA_FAILURE:
             case BaseService.DATA_REQUEST_ERROR:
                 showToast(msg.obj.toString());
                 break;
         }
     }
 };


    //创建门禁快捷方式
    private void createShortcut(){
        //创建一个添加快捷方式的Intent
        Intent addSC = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //快捷键的标题
        String title = getString(R.string.shortcut_title);
        //快捷键的图标
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getActivity(), R.drawable.home_entrance_guard);
        //创建单击快捷键启动本程序的Intent
        Intent launcherIntent = new Intent(getActivity(), EntranceGuardSplash.class);
        //设置快捷键的标题
        addSC.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        //设置快捷键的图标
        addSC.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        //设置单击此快捷键启动的程序
        addSC.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
        addSC.putExtra("duplicate", false);
        //向系统发送添加快捷键的广播
        getActivity().sendBroadcast(addSC);
    }

    //创建快捷方式弹出对话框
    CustomAlertDialog.Dialogcallback dialogcallback = new CustomAlertDialog.Dialogcallback() {
        @Override
        public void dialogdo() {
           //
        }

        @Override
        public void dialogDismiss() {

        }
    };


    private static float convertRationalLatLonToFloat(
            String rationalString, String ref) {

        String[] parts = rationalString.split(",");

        String[] pair;
        pair = parts[0].split("/");
        double degrees = Double.parseDouble(pair[0].trim())
                / Double.parseDouble(pair[1].trim());

        pair = parts[1].split("/");
        double minutes = Double.parseDouble(pair[0].trim())
                / Double.parseDouble(pair[1].trim());

        pair = parts[2].split("/");
        double seconds = Double.parseDouble(pair[0].trim())
                / Double.parseDouble(pair[1].trim());

        double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
        if ((ref.equals("S") || ref.equals("W"))) {
            return (float) -result;
        }
        return (float) result;
    }

}
