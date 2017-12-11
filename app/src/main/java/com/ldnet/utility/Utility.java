package com.ldnet.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ldnet.activity.access.VisitorCardActivity;
import com.ldnet.entities.CommunityServicesModel;
import com.ldnet.entities.Type;
import com.ldnet.goldensteward.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Utility {

    private static String aa = Services.timeFormat();
    private static String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";

    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^1[0-9]{10}$";


    // 获取屏幕的宽度
    public static int getScreenWidthforDIP(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return px2dip(context, (float) dm.widthPixels);
    }

    //获取屏幕的宽度，像素
    public static int getScreenWidthforPX(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    // 获取屏幕的高度
    public static int getScreenHeightforDIP(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return px2dip(context, (float) dm.heightPixels);
    }

    //获取屏幕的高度，像素
    public static int getScreenHeightforPX(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    // 转换dip到px
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    // 转换px到dip
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue //     * @param fontScale
     *                （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue //     * @param fontScale
     *                （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    // 设置ListView的高度为所有Item高的和
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int maxHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            if (listItem.getMeasuredHeight() > maxHeight) {
                maxHeight = listItem.getMeasuredHeight();
            }

        }
        totalHeight = maxHeight * listView.getCount();
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    // 设置Margin
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v
                    .getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    //把文字绘制成图像
    public static Bitmap getBitmapByText(String text, float dipW, float dipH, String frontColor, String backColor) {
        int width = dip2px(GSApplication.getInstance(), dipW);
        int height = dip2px(GSApplication.getInstance(), dipH);
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(Color.parseColor(backColor));
        Paint paint = new Paint();
        Typeface typeface = Typeface.DEFAULT;
        paint.setTypeface(typeface);
        paint.setColor(Color.parseColor(frontColor));
        paint.setTextSize(width / 7 * 3);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        //计算绘制文字时的起始点坐标
        float tx = (width - getFontlength(paint, text)) / 2;
        float ty = (height - getFontHeight(paint)) / 2 + getFontLeading(paint);
        canvas.drawText(text, tx, ty, paint);
        return bmp;
    }

    /**
     * @return 返回指定笔和指定字符串的长度
     */
    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    /**
     * @return 返回指定笔的文字高度
     */
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * @return 返回指定笔离文字顶部的基准距离
     */
    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 90, baos);
        return baos.toByteArray();
    }

    //生成16位主键
    public static String generateGUID() {
        return UUID.randomUUID().toString().replace("-","").trim();
    }

    public static String getDate(String date) {
        String dateStr = "";
        if (date.contains("T")) {
            int index = date.indexOf("T");
            dateStr = date.substring(0, index);
        } else {
            return date;
        }
        return dateStr;
    }


    public static boolean isPhone(String phone) {
        return Pattern.matches(REGEX_MOBILE, phone);
    }


    public static boolean isNull(String content) {
        if (content == null || (content != null && content.trim().equals(""))) {
            return true;
        }
        return false;
    }


    public static boolean editIsNull(EditText editText) {
        if (editText.getText() == null) {
            return true;
        } else {
            String content = editText.getText().toString().trim();
            return TextUtils.isEmpty(content);
        }
    }


    //获取当前截图
    public static Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }



    //为弹出层设置遮罩层
    public static void backgroundAlpaha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    //List<String> --->str,str1,str2
    public static String ListToString(List<String> list){
        String result="";
        if (list!=null&&list.size()>0){
            for(String str:list){
                if (TextUtils.isEmpty(result)){
                    result=str;
                }else{
                    result+=","+str;
                }
            }
        }else{
            return "";
        }
        return result;
    }

    public static String ListToString2(List<Type> list) {
        String result="";
        if (list!=null&&list.size()>0){
            for (Type type : list) {
                if (TextUtils.isEmpty(result)){
                    result = String.valueOf(type.Type);
                }else{
                    result += "," + String.valueOf(type.Type);
                }
            }
        }else{
            return "";
        }
        return result;
    }


    public static List<String> StringToList(String image){
        List<String> list=new ArrayList<>();
        if (!TextUtils.isEmpty(image)){
            String[] arr=image.split(",");
            if (arr.length>1){
                for (String str:arr){
                    if (!TextUtils.isEmpty(str)) {
                        list.add(str);
                    }
                }
            }else{
                list.add(image);
            }
        }else{
            return null;
        }
        return list;
    }

    //设置标题滑动
    public static void setTabsValue(PagerSlidingTabStrip tab,Context mContext) {
        //  tab.setShouldExpand(true);
        tab.setBackgroundResource(R.color.white);
        // 设置Tab的分割线的颜色
        // 设置分割线的上下的间距,传入的是dp
        tab.setDividerPaddingTopBottom(12);
        // 设置Tab底部线的高度,传入的是dp
        tab.setUnderlineHeight(1);
        //设置Tab底部线的颜色
        tab.setUnderlineColor(mContext.getResources().getColor(R.color.green));
        // 设置Tab 指示器Indicator的高度,传入的是dp
        tab.setIndicatorHeight(4);
        // 设置Tab Indicator的颜色
        tab.setIndicatorColor(mContext.getResources().getColor(R.color.green));

        // 设置Tab标题文字的大小,传入的是dp
        tab.setTextSize(14);
        // 设置选中Tab文字的颜色
        tab.setSelectedTextColor(mContext.getResources().getColor(R.color.green));
        //设置正常Tab文字的颜色
        tab.setTextColor(mContext.getResources().getColor(R.color.black_text));

        //是否支持动画渐变(颜色渐变和文字大小渐变)
        tab.setFadeEnabled(true);
        // 设置最大缩放,是正常状态的0.3倍
        tab.setZoomMax(0.1F);
        //设置Tab文字的左右间距,传入的是dp
        tab.setTabPaddingLeftRight(24);
    }

    //倒序添加所有优惠
    public static List<CommunityServicesModel> reverseModelList(List<CommunityServicesModel> list){
        List<CommunityServicesModel> list1=new ArrayList<>();
        CommunityServicesModel model=new CommunityServicesModel();
        model.setName("所有优惠");
        model.setId("200");
        list1.add(model);

        if (list!=null&&list.size()>0){
            for(CommunityServicesModel model1:list){
                list1.add(model1);
            }
        }
        return list1;
    }


    //加载图片显示的配置项
    public static DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
            .showImageOnFail(R.drawable.default_info)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .resetViewBeforeLoading(true)
            .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
            .build();

}
