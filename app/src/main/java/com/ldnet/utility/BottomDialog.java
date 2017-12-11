package com.ldnet.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import android.widget.PopupWindow.OnDismissListener;

import com.ldnet.goldensteward.R;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.tencent.mm.opensdk.modelmsg.*;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.platformtools.Util;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import okhttp3.Call;

/**
 *
 */
public class BottomDialog {
    private Context mContext;
    private String mUrl;
    private String mTitle;
    private String mImageUrl;
    private String mDescription;
    private IWXAPI api;
    private Bitmap bitmap;
    private final Integer THUMB_SIZE = 80;

    private static int IS_FINISH = 1;

    private static final String APP_ID = "wxa4207e39a8e5cf0f";

    private BottomDialog() {
    }

    public BottomDialog(Context mContext, String mTitle, Bitmap bitmap) {
        this.mContext = mContext;
        this.mTitle = mTitle;
        this.bitmap = bitmap;
    }

    public BottomDialog(Context context, String url, String title) {
        mContext = context;
        mUrl = url;
        mTitle = title;
    }

    public BottomDialog(Context context, String url, String title, String imageUrl, String discription) {
        mContext = context;
        mUrl = url;
        mTitle = title;
        mImageUrl = imageUrl;
        mDescription = discription;
    }

    private static final BottomDialog instance = new BottomDialog();
    private PopupWindow pw_uploadImage;
    private LinearLayout ll_bottom;

    public static BottomDialog getInstance() {
        return instance;
    }

    /**
     * 展示ui
     * @param activity
     */
    public void uploadImageUI(final Activity activity) {
        LinearLayout contentView = genericPayPopupWindowLayout(activity);
        pw_uploadImage = new PopupWindow(contentView, -1, -1);
        pw_uploadImage.setBackgroundDrawable(new ColorDrawable());
        pw_uploadImage.showAtLocation(activity.getWindow().getDecorView(),
                Gravity.LEFT, 0, 0);
        openAnimation();
    }

    private LinearLayout genericPayPopupWindowLayout(Activity context) {
        LinearLayout ll_base = new LinearLayout(context);
        ll_base.setOrientation(LinearLayout.VERTICAL);
        ll_base.setBackgroundColor(0x88000000);

        LinearLayout ll_top = new LinearLayout(context);
        ll_top.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeUploadImageUI();
            }
        });
        LinearLayout.LayoutParams ll_top_lp = new LinearLayout.LayoutParams(-1,
                0);
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        ll_top_lp.topMargin = frame.top;
        ll_top_lp.weight = 1.0f;
        ll_top.addView(new TextView(context));
        ll_top.setLayoutParams(ll_top_lp);
        ll_base.addView(ll_top);

        ll_bottom = new LinearLayout(context);
        ll_bottom.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams ll_bottom_lp = new LinearLayout.LayoutParams(
                -1, -2);
        ll_bottom_lp.bottomMargin = dp2px(context, 10);
        ll_bottom_lp.leftMargin = dp2px(context, 10);
        ll_bottom_lp.rightMargin = dp2px(context, 10);
        ll_bottom.setLayoutParams(ll_bottom_lp);
        ll_base.addView(ll_bottom);
        generateBottomLayout(context, ll_bottom);
        return ll_base;
    }

    // 开启动画
    public void openAnimation() {
        if (ll_bottom == null)
            return;
        ll_bottom.clearAnimation();
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0f);
        animation.setRepeatCount(0);
        animation.setDuration(250);
        ll_bottom.setAnimation(animation);
        animation.startNow();
    }


    // 选择方式
    private void generateBottomLayout(final Activity context,
                                      LinearLayout ll_bottom) {
        TextView tv_choosePhoto;

        if (bitmap == null) {
            TextView tv_taskPhoto = getItem(context, "分享到微信朋友圈", 0, 13, 0, 13,
                    0xFF25B59E,
                    getItemPressedStateListDrawable(context, 0, 0, 7, 7, 0, 0), 17);
            ll_bottom.addView(tv_taskPhoto);
            ll_bottom.addView(getLine(context, 0, 0, 0, 0, 0));
            tv_taskPhoto.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    regTiWx();
                    if (mUrl == null || mUrl.equals("") && bitmap == null) {
                        shareTextToApp(1);
                    } else if (bitmap != null) {
                        toShareApp(SendMessageToWX.Req.WXSceneTimeline);
                    } else {
                        toShareApp(SendMessageToWX.Req.WXSceneTimeline);
                    }
                }
            });

            tv_choosePhoto = getItem(context, "分享到微信好友", 0, 13, 0, 13,
                    0xFF25B59E,
                    getItemPressedStateListDrawable(context, 0, 0, 0, 0, 7, 7), 17);
        } else {
            tv_choosePhoto = getItem(context, "分享到微信好友", 0, 13, 0, 13,
                0xFF25B59E,
                    getItemPressedStateListDrawable(context, 0, 0, 7, 7, 7, 7), 17);
        }

        ll_bottom.addView(tv_choosePhoto);
        tv_choosePhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                regTiWx();
                if ((mUrl == null || mUrl.equals("")) && bitmap == null) {
                    shareTextToApp(0);
                } else if (bitmap != null) {
                    toShareApp(SendMessageToWX.Req.WXSceneSession);
                } else {
                    toShareApp(SendMessageToWX.Req.WXSceneSession);
                }
            }
        });
        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, dp2px(context,
                10)));
        ll_bottom.addView(view);
        TextView tv_cancel = getItem(context, "取消", 0, 13, 0, 13, 0xFF25B59E,
                getItemPressedStateListDrawable(context, 0, 0, 7, 7, 7, 7), 17);
        tv_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                closeUploadImageUI();
            }
        });
        ll_bottom.addView(tv_cancel);

    }


    private TextView getItem(Context context, String text, int left, int top,
                             int right, int bottom, int color, Drawable background, int textSize) {
        TextView tv = new TextView(context);
        tv.setClickable(true);
        tv.setPadding(dp2px(context, left), dp2px(context, top),
                dp2px(context, right), dp2px(context, bottom));
        tv.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        tv.setBackgroundDrawable(background);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        tv.setSingleLine();
        tv.setEllipsize(TruncateAt.END);

        return tv;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public GradientDrawable genericGradientDrawableRectangle(Context context,
                                                             int color, int radius, int strokeWidht, int strokeColor, int lR,
                                                             int tR, int rR, int bR) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadii(new float[]{dp2px(context, lR),
                dp2px(context, lR), dp2px(context, tR), dp2px(context, tR),
                dp2px(context, rR), dp2px(context, rR), dp2px(context, bR),
                dp2px(context, bR)});
        gradientDrawable.setStroke(strokeWidht, strokeColor);
        return gradientDrawable;
    }

    public View getLine(Context context, int color, int left, int top,
                        int right, int bottom) {
        View line = new View(context);
        LinearLayout.LayoutParams line_lp = new LinearLayout.LayoutParams(-1,
                dp2px(context, 0.8f));
        line_lp.leftMargin = dp2px(context, left);
        line_lp.topMargin = dp2px(context, top);
        line_lp.rightMargin = dp2px(context, right);
        line_lp.bottomMargin = dp2px(context, bottom);
        line.setBackgroundColor(color == 0 ? 0xffd9d9d9 : color);
        line.setLayoutParams(line_lp);
        return line;
    }

    /**
     * 条目按下效果, 淡灰色
     *
     * @return
     */
    public StateListDrawable getItemPressedStateListDrawable(Context context,
                                                             int pressed, int normal, int lR, int tR, int rR, int bR) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(
                new int[]{-android.R.attr.state_pressed},
                genericGradientDrawableRectangle(context,
                        normal == 0 ? 0xf9ffffff : normal, 0, 0, 0, lR, tR, rR,
                        bR));
        drawable.addState(
                new int[]{android.R.attr.state_pressed},
                genericGradientDrawableRectangle(context,
                        pressed == 0 ? 0xf9d9d9d9 : pressed, 0, 0, 0, lR, tR,
                        rR, bR));
        return drawable;
    }

    /**
     * 关闭UI
     */
    public void closeUploadImageUI() {
        if (ll_bottom == null)
            return;
        ll_bottom.clearAnimation();
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        animation.setRepeatCount(0);
        animation.setDuration(150);
        ll_bottom.setAnimation(animation);
        animation.startNow();
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (pw_uploadImage != null && pw_uploadImage.isShowing()) {
                    pw_uploadImage.dismiss();
                }
            }
        });
    }

    private void closeUploadImageUIDoHandler(final Activity context,
                                             final int type) {
        if (ll_bottom == null)
            return;
        ll_bottom.clearAnimation();
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        animation.setRepeatCount(0);
        animation.setDuration(150);
        ll_bottom.setAnimation(animation);
        animation.startNow();
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (pw_uploadImage != null && pw_uploadImage.isShowing()) {
                    pw_uploadImage.dismiss();
                }
            }
        });
        pw_uploadImage.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
//                doHandlerPhoto(context, type);
            }
        });
    }

    private void regTiWx() {
        api = WXAPIFactory.createWXAPI(mContext, APP_ID, true);
        // 将应用的appid注册到微信
        api.registerApp(APP_ID);
    }


    private void toShareApp(int flag) {
        try {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = mUrl;
            WXMediaMessage msg;

            msg = new WXMediaMessage(webpage);
            msg.title = mTitle;
            msg.description = Services.isNotNullOrEmpty(mDescription) ? mDescription : mTitle;
            ImageSize imageSize = new ImageSize(THUMB_SIZE, THUMB_SIZE);
            if (Services.isNotNullOrEmpty(mImageUrl)) {
                getData(flag, msg);
            } else if (bitmap != null) {
                WXImageObject imgObj = new WXImageObject(bitmap);
                WXMediaMessage imgMsg = new WXMediaMessage();
                imgMsg.mediaObject = imgObj;

                Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                bitmap.recycle();
                imgMsg.thumbData = Util.bmpToByteArray(thumbBmp, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("img");
                req.message = imgMsg;
                req.scene = flag;

                api.sendReq(req);
                closeUploadImageUI();
            } else {
                Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.share_app);
                Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, THUMB_SIZE, THUMB_SIZE, true);
                thumb.recycle();
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = flag;

                api.sendReq(req);
                closeUploadImageUI();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void shareTextToApp(int flag) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = mTitle;
        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = mTitle;
        // 发送文本类型的消息时，title字段不起作用
        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        if (flag == 0) {
            req.scene = SendMessageToWX.Req.WXSceneSession;   //好友会话
        } else if (flag == 1) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;   //朋友圈
        }
        // 调用api接口发送数据到微信
        api.sendReq(req);
        closeUploadImageUI();
    }


    public void getData(final int flag, final WXMediaMessage msg) {
        String url = mImageUrl;
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get()
                .url(url)
                .tag(this)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int i) {
                        Log.e("asd", "微信分享---getData()11111111" + bitmap.toString());
                        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);

                        bitmap.recycle();
                        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                        Log.e("asd", "微信分享网络图片大小" + (msg.thumbData.length / 1024));
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("webpage");
                        req.message = msg;
                        req.scene = flag;
                        api.sendReq(req);
                        Log.e("asd", "微信分享---getData()222222222");
                    }
                });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
